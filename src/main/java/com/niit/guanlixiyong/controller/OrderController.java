package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.dto.CreateOrderRequest;
import com.niit.guanlixiyong.dto.OrderResponseDTO;
import com.niit.guanlixiyong.dto.PaymentRequest;
import com.niit.guanlixiyong.entity.Payment;
import com.niit.guanlixiyong.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Validated @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * 按订单号获取订单
     */
    @GetMapping("/no/{orderNo}")
    public ResponseEntity<OrderResponseDTO> getByOrderNo(@PathVariable String orderNo) {
        return ResponseEntity.ok(orderService.getByOrderNo(orderNo));
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    /**
     * 获取所有订单
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * 按状态获取订单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    /**
     * 确认订单
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    /**
     * 开始制作
     */
    @PostMapping("/{id}/prepare")
    public ResponseEntity<OrderResponseDTO> startPreparing(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.startPreparing(id));
    }

    /**
     * 开始配送
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<OrderResponseDTO> startDelivery(
            @PathVariable Long id,
            @RequestParam(required = false) Long deliveryUserId) {
        return ResponseEntity.ok(orderService.startDelivery(id, deliveryUserId));
    }

    /**
     * 完成订单
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<OrderResponseDTO> completeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.completeOrder(id));
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    /**
     * 处理支付
     */
    @PostMapping("/payment")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(orderService.processPayment(request));
    }
}
