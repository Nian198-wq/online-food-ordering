package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.StatisticsDTO;
import com.niit.guanlixiyong.entity.Delivery;
import com.niit.guanlixiyong.entity.Order;
import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 配送服务类
 */
@Service
public class DeliveryService {

    @Resource
    private DeliveryRepository deliveryRepository;

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private UsersRepository usersRepository;

    /**
     * 获取所有配送记录
     */
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    /**
     * 按订单 ID 获取配送
     */
    public Delivery getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("配送记录不存在"));
    }

    /**
     * 按配送员获取配送
     */
    public List<Delivery> getByDeliveryUser(Long deliveryUserId) {
        Users deliveryUser = usersRepository.findById(deliveryUserId)
                .orElseThrow(() -> new RuntimeException("配送员不存在"));
        return deliveryRepository.findByDeliveryUser(deliveryUser);
    }

    /**
     * 按状态获取配送
     */
    public List<Delivery> getByStatus(String status) {
        return deliveryRepository.findByStatus(status);
    }

    /**
     * 分配配送员
     */
    public Delivery assignDeliveryUser(Long orderId, Long deliveryUserId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("配送记录不存在"));
        
        Users deliveryUser = usersRepository.findById(deliveryUserId)
                .orElseThrow(() -> new RuntimeException("配送员不存在"));
        
        delivery.setDeliveryUser(deliveryUser);
        delivery.setStatus("ASSIGNED");
        delivery.setAssignedTime(LocalDateTime.now());
        
        return deliveryRepository.save(delivery);
    }

    /**
     * 取餐
     */
    public Delivery pickupOrder(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("配送记录不存在"));
        
        delivery.setStatus("PICKED_UP");
        delivery.setPickupTime(LocalDateTime.now());
        
        return deliveryRepository.save(delivery);
    }

    /**
     * 配送中
     */
    public Delivery inTransit(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("配送记录不存在"));
        
        delivery.setStatus("IN_TRANSIT");
        
        return deliveryRepository.save(delivery);
    }

    /**
     * 已送达
     */
    public Delivery delivered(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("配送记录不存在"));
        
        delivery.setStatus("DELIVERED");
        delivery.setDeliveredTime(LocalDateTime.now());
        
        // 更新订单状态
        Order order = delivery.getOrder();
        if (!"COMPLETED".equals(order.getStatus())) {
            order.setStatus("COMPLETED");
            order.setCompleteTime(LocalDateTime.now());
            orderRepository.save(order);
        }
        
        return deliveryRepository.save(delivery);
    }

    /**
     * 获取可分配的配送员
     */
    public List<Users> getAvailableDeliveryUsers() {
        return usersRepository.findByRole("DELIVERY");
    }
}
