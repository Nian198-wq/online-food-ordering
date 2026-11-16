package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.CreateOrderItemDTO;
import com.niit.guanlixiyong.dto.CreateOrderRequest;
import com.niit.guanlixiyong.dto.OrderResponseDTO;
import com.niit.guanlixiyong.entity.Dishes;
import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.repository.DishesRepository;
import com.niit.guanlixiyong.repository.OrderRepository;
import com.niit.guanlixiyong.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderWorkflowIntegrationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DishesRepository dishesRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createsOrderAndDeductsStock() {
        Users user = createUser();
        Dishes dish = createDish(10);

        OrderResponseDTO order = orderService.createOrder(request(user, dish, 3, newId()));

        assertEquals("PENDING", order.getStatus());
        assertEquals(new BigDecimal("30.00"), order.getTotalAmount());
        assertEquals(7, dishesRepository.findById(dish.getId()).orElseThrow().getStock());
    }

    @Test
    void insufficientStockRollsBackWithoutCreatingOrder() {
        Users user = createUser();
        Dishes dish = createDish(2);
        String requestId = newId();

        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request(user, dish, 3, requestId))
        );

        assertEquals(true, error.getMessage().contains("库存不足"));
        assertEquals(2, dishesRepository.findById(dish.getId()).orElseThrow().getStock());
        assertEquals(false, orderRepository.findByClientRequestId(requestId).isPresent());
    }

    @Test
    void repeatedClientRequestIdIsIdempotent() {
        Users user = createUser();
        Dishes dish = createDish(5);
        String requestId = newId();

        OrderResponseDTO first = orderService.createOrder(request(user, dish, 2, requestId));
        OrderResponseDTO replay = orderService.createOrder(request(user, dish, 2, requestId));

        assertEquals(first.getId(), replay.getId());
        assertEquals(3, dishesRepository.findById(dish.getId()).orElseThrow().getStock());
        assertEquals(1, orderRepository.findByUser(user).size());
    }

    @Test
    void cancelRestoresStockAndRejectsInvalidTransition() {
        Users user = createUser();
        Dishes dish = createDish(8);
        OrderResponseDTO order = orderService.createOrder(request(user, dish, 3, newId()));

        assertThrows(RuntimeException.class, () -> orderService.startPreparing(order.getId()));
        orderService.confirmOrder(order.getId());
        OrderResponseDTO cancelled = orderService.cancelOrder(order.getId());

        assertEquals("CANCELLED", cancelled.getStatus());
        assertEquals(8, dishesRepository.findById(dish.getId()).orElseThrow().getStock());
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(order.getId()));
    }

    @Test
    void concurrentOrdersDoNotOversell() throws Exception {
        Users user = createUser();
        Dishes dish = createDish(5);
        int attempts = 12;
        ExecutorService executor = Executors.newFixedThreadPool(attempts);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < attempts; i++) {
                futures.add(executor.submit(() -> {
                    start.await(5, TimeUnit.SECONDS);
                    try {
                        orderService.createOrder(request(user, dish, 1, newId()));
                        return true;
                    } catch (RuntimeException expected) {
                        return false;
                    }
                }));
            }

            start.countDown();
            int successfulOrders = 0;
            for (Future<Boolean> future : futures) {
                if (future.get(15, TimeUnit.SECONDS)) {
                    successfulOrders++;
                }
            }

            assertEquals(5, successfulOrders);
            assertEquals(0, dishesRepository.findById(dish.getId()).orElseThrow().getStock());
            assertEquals(5, orderRepository.findByUser(user).size());
        } finally {
            executor.shutdownNow();
        }
    }

    private Users createUser() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        Users user = new Users();
        user.setUsername("customer_" + suffix);
        user.setPassword("test-password");
        user.setEmail(suffix + "@example.com");
        user.setPhone("139" + suffix.substring(0, 8));
        user.setRole("CUSTOMER");
        user.setAddress("测试地址");
        return usersRepository.saveAndFlush(user);
    }

    private Dishes createDish(int stock) {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        Dishes dish = new Dishes();
        dish.setName("测试菜品_" + suffix);
        dish.setDescription("事务测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setCategory("热菜");
        dish.setImageUrl("/images/test.jpg");
        dish.setAvailable(true);
        dish.setStock(stock);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        return dishesRepository.saveAndFlush(dish);
    }

    private CreateOrderRequest request(Users user, Dishes dish, int quantity, String requestId) {
        CreateOrderItemDTO item = new CreateOrderItemDTO();
        item.setDishesId(dish.getId());
        item.setQuantity(quantity);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(user.getId());
        request.setClientRequestId(requestId);
        request.setOrderType("DINE_IN");
        request.setDeliveryAddress("");
        request.setDeliveryPhone("13900000000");
        request.setItems(List.of(item));
        return request;
    }

    private String newId() {
        return UUID.randomUUID().toString();
    }
}
