package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.entity.Delivery;
import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配送管理控制器
 */
@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Resource
    private DeliveryService deliveryService;

    /**
     * 获取所有配送记录
     */
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    /**
     * 按订单 ID 获取配送
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }

    /**
     * 按配送员获取配送
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Delivery>> getByDeliveryUser(@PathVariable Long userId) {
        return ResponseEntity.ok(deliveryService.getByDeliveryUser(userId));
    }

    /**
     * 按状态获取配送
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getByStatus(status));
    }

    /**
     * 分配配送员
     */
    @PostMapping("/{orderId}/assign/{deliveryUserId}")
    public ResponseEntity<Delivery> assignDeliveryUser(
            @PathVariable Long orderId,
            @PathVariable Long deliveryUserId) {
        return ResponseEntity.ok(deliveryService.assignDeliveryUser(orderId, deliveryUserId));
    }

    /**
     * 取餐
     */
    @PostMapping("/{orderId}/pickup")
    public ResponseEntity<Delivery> pickupOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.pickupOrder(orderId));
    }

    /**
     * 配送中
     */
    @PostMapping("/{orderId}/transit")
    public ResponseEntity<Delivery> inTransit(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.inTransit(orderId));
    }

    /**
     * 已送达
     */
    @PostMapping("/{orderId}/delivered")
    public ResponseEntity<Delivery> delivered(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.delivered(orderId));
    }

    /**
     * 获取可分配的配送员
     */
    @GetMapping("/users/available")
    public ResponseEntity<List<Users>> getAvailableDeliveryUsers() {
        return ResponseEntity.ok(deliveryService.getAvailableDeliveryUsers());
    }
}
