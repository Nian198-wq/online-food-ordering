package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.*;
import com.niit.guanlixiyong.entity.*;
import com.niit.guanlixiyong.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单服务类
 */
@Service
public class OrderService {

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "PENDING", Set.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", Set.of("PREPARING", "CANCELLED"),
            "PREPARING", Set.of("DELIVERING", "CANCELLED"),
            "DELIVERING", Set.of("COMPLETED"),
            "COMPLETED", Set.of(),
            "CANCELLED", Set.of()
    );

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private UsersRepository usersRepository;

    @Resource
    private DishesRepository dishesRepository;

    @Resource
    private PaymentRepository paymentRepository;

    @Resource
    private DeliveryRepository deliveryRepository;

    /**
     * 创建订单
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequest request) {
        String clientRequestId = normalizeRequestId(request.getClientRequestId());
        if (clientRequestId != null) {
            Order existing = orderRepository.findByClientRequestId(clientRequestId).orElse(null);
            if (existing != null) {
                if (!existing.getUser().getId().equals(request.getUserId())) {
                    throw new RuntimeException("客户端请求号已被其他用户使用");
                }
                return convertToResponseDTO(existing);
            }
        }

        // 验证用户
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证订单类型
        String orderType = request.getOrderType() != null ? request.getOrderType() : "DELIVERY";
        
        // 外卖订单必须填写地址
        if ("DELIVERY".equals(orderType)) {
            if (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().length() < 5) {
                throw new RuntimeException("外卖订单必须填写详细的配送地址（至少5个字符）");
            }
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderNo(generateOrderNo());
        order.setClientRequestId(clientRequestId);
        order.setOrderType(orderType);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryPhone(request.getDeliveryPhone());
        order.setFlavorPreference(request.getFlavorPreference());
        order.setStatus("PENDING");
        order.setRemarks(request.getRemarks());
        order.setCreateTime(LocalDateTime.now());

        // 创建订单项并计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderItemDTO itemDTO : request.getItems()) {
            if (itemDTO == null || itemDTO.getDishesId() == null || itemDTO.getQuantity() == null
                    || itemDTO.getQuantity() <= 0) {
                throw new RuntimeException("订单项格式不正确");
            }
        }

        for (CreateOrderItemDTO itemDTO : request.getItems()) {
            Dishes dishes = dishesRepository.findById(itemDTO.getDishesId())
                    .orElseThrow(() -> new RuntimeException("菜品不存在: " + itemDTO.getDishesId()));

            if (!dishes.getAvailable()) {
                throw new RuntimeException("菜品不可用：" + dishes.getName());
            }

            Integer stock = dishes.getStock() == null ? 0 : dishes.getStock();
            if (stock < itemDTO.getQuantity()) {
                throw new RuntimeException("库存不足：" + dishes.getName() + "，当前库存 " + stock);
            }

            int updated = dishesRepository.decreaseStock(dishes.getId(), itemDTO.getQuantity());
            if (updated == 0) {
                Dishes latest = dishesRepository.findById(dishes.getId())
                        .orElseThrow(() -> new RuntimeException("菜品已下架：" + dishes.getName()));
                int latestStock = latest.getStock() == null ? 0 : latest.getStock();
                throw new RuntimeException(
                        "库存不足：" + dishes.getName() + "，当前库存 " + latestStock);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDishes(dishes);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(dishes.getPrice());
            orderItem.setSubtotal(dishes.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItem.setSpecialRequest(itemDTO.getSpecialRequest());

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        return convertToResponseDTO(savedOrder);
    }

    /**
     * 获取订单详情
     */
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToResponseDTO(order);
    }

    /**
     * 按订单号获取订单
     */
    public OrderResponseDTO getByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToResponseDTO(order);
    }

    /**
     * 获取用户订单列表
     */
    public List<OrderResponseDTO> getUserOrders(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    /**
     * 确认订单
     */
    @Transactional
    public OrderResponseDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        transition(order, "CONFIRMED");
        order.setConfirmTime(LocalDateTime.now());
        
        return convertToResponseDTO(orderRepository.save(order));
    }

    /**
     * 开始制作
     */
    @Transactional
    public OrderResponseDTO startPreparing(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        transition(order, "PREPARING");
        order.setPrepareTime(LocalDateTime.now());
        
        return convertToResponseDTO(orderRepository.save(order));
    }

    /**
     * 开始配送
     */
    @Transactional
    public OrderResponseDTO startDelivery(Long orderId, Long deliveryUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        transition(order, "DELIVERING");
        order.setDeliverTime(LocalDateTime.now());
        
        if (deliveryUserId != null) {
            Users deliveryUser = usersRepository.findById(deliveryUserId)
                    .orElseThrow(() -> new RuntimeException("配送员不存在"));
            order.setDeliveryUser(deliveryUser);
        }
        
        // 创建配送记录
        createDeliveryRecord(order);
        
        return convertToResponseDTO(orderRepository.save(order));
    }

    /**
     * 完成订单
     */
    @Transactional
    public OrderResponseDTO completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        transition(order, "COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        
        // 更新配送状态
        deliveryRepository.findByOrderId(orderId).ifPresent(delivery -> {
            delivery.setStatus("DELIVERED");
            delivery.setDeliveredTime(LocalDateTime.now());
            deliveryRepository.save(delivery);
        });
        
        return convertToResponseDTO(orderRepository.save(order));
    }

    /**
     * 取消订单
     */
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        transition(order, "CANCELLED");
        for (OrderItem item : order.getOrderItems()) {
            dishesRepository.increaseStock(item.getDishes().getId(), item.getQuantity());
        }
        if (order.getPayment() != null && "SUCCESS".equals(order.getPayment().getStatus())) {
            order.getPayment().setStatus("REFUNDED");
        }

        return convertToResponseDTO(orderRepository.save(order));
    }

    /**
     * 获取所有订单
     */
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    /**
     * 按状态获取订单
     */
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    /**
     * 处理支付
     */
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if ("CANCELLED".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不能支付");
        }
        if (order.getPayment() != null) {
            return order.getPayment();
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentTime(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);
        orderRepository.save(order);

        return savedPayment;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int suffix = 1000 + random.nextInt(9000);
        return "ORD" + timestamp + suffix;
    }

    /**
     * 生成交易号
     */
    private String generateTransactionId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int suffix = 100000 + random.nextInt(900000);
        return "PAY" + timestamp + suffix;
    }

    private void transition(Order order, String targetStatus) {
        String currentStatus = order.getStatus();
        Set<String> allowedTargets = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedTargets.contains(targetStatus)) {
            throw new RuntimeException(
                    "订单状态不能从 " + currentStatus + " 变更为 " + targetStatus);
        }
        order.setStatus(targetStatus);
    }

    private String normalizeRequestId(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return null;
        }
        return requestId.trim();
    }

    /**
     * 创建配送记录
     */
    private void createDeliveryRecord(Order order) {
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryUser(order.getDeliveryUser());
        delivery.setPickupAddress("餐厅地址");
        delivery.setDeliveryAddress(order.getDeliveryAddress());
        delivery.setDeliveryPhone(order.getDeliveryPhone());
        delivery.setStatus("ASSIGNED");
        delivery.setAssignedTime(LocalDateTime.now());
        deliveryRepository.save(delivery);
    }

    /**
     * 转换为响应 DTO
     */
    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        
        if (order.getDeliveryUser() != null) {
            dto.setDeliveryUserId(order.getDeliveryUser().getId());
            dto.setDeliveryUsername(order.getDeliveryUser().getUsername());
        }
        
        // 转换订单项，包含菜品名称和价格
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setOrderId(item.getOrder().getId());
                    itemDTO.setDishesId(item.getDishes().getId());
                    itemDTO.setDishesName(item.getDishes().getName());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setSubtotal(item.getSubtotal());
                    itemDTO.setSpecialRequest(item.getSpecialRequest());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        // 转换支付信息
        if (order.getPayment() != null) {
            PaymentDTO paymentDTO = new PaymentDTO();
            BeanUtils.copyProperties(order.getPayment(), paymentDTO);
            dto.setPayment(paymentDTO);
        }
        
        return dto;
    }
}
