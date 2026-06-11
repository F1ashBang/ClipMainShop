package com.example.demo.controllers;

import com.example.demo.models.OrderModel;
import com.example.demo.models.OrderItem;
import com.example.demo.models.User;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.parseLong(body.get("userId").toString());

            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
            }

            OrderModel orderModel = new OrderModel();
            orderModel.setUser(user);
            orderModel.setStatus("new");
            orderModel.setFirstName(body.get("firstName") != null ? body.get("firstName").toString() : null);
            orderModel.setLastName(body.get("lastName") != null ? body.get("lastName").toString() : null);
            orderModel.setMiddleName(body.get("middleName") != null ? body.get("middleName").toString() : null);
            orderModel.setAddress(body.get("address") != null ? body.get("address").toString() : null);
            orderModel.setPhoneAtOrder(body.get("phone") != null ? body.get("phone").toString() : user.getPhoneNumber());
            orderModel.setCreatedAt(LocalDateTime.now());
            orderModel.setUpdatedAt(LocalDateTime.now());

            double totalPrice = 0;

            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

            for (Map<String, Object> itemData : items) {
                OrderItem item = new OrderItem();
                item.setProductId(itemData.get("productId") != null ? Long.parseLong(itemData.get("productId").toString()) : null);
                item.setProductTitle(itemData.get("productTitle") != null ? itemData.get("productTitle").toString() : null);
                item.setProductPrice(itemData.get("productPrice") != null ? itemData.get("productPrice").toString() : null);
                item.setSize(itemData.get("size") != null ? itemData.get("size").toString() : null);
                item.setQuantity(itemData.get("quantity") != null ? Integer.parseInt(itemData.get("quantity").toString()) : null);
                item.setImageUrl(itemData.get("imageUrl") != null ? itemData.get("imageUrl").toString() : null);

                orderModel.add(item);

                try {
                    double price = Double.parseDouble(item.getProductPrice());
                    totalPrice += price * item.getQuantity();
                }
                catch (NumberFormatException e) {}
            }

            orderModel.setTotalPrice(totalPrice);
            orderRepository.save(orderModel);

            if (body.get("firstName") != null) user.setFirstName(body.get("firstName").toString());
            if (body.get("lastName") != null) user.setLastName(body.get("lastName").toString());
            if (body.get("middleName") != null) user.setMiddleName(body.get("middleName").toString());
            if (body.get("address") != null) user.setAddress(body.get("address").toString());

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("Message", "Заказ создан",
                                            "orderId", orderModel.getId(),
                                            "totalPrice", orderModel.getTotalPrice()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка при создании заказа " + e.getMessage()));
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
        }

        List<OrderModel> orderModels = orderRepository.findByUserOrderByCreatedAtDesc(user);

        return ResponseEntity.ok(orderModels);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        OrderModel orderModel = orderRepository.findById(orderId).orElse(null);

        if (orderModel == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "заказ не найден"));
        }
        return ResponseEntity.ok(orderModel);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody Map<String, String> body,
                                               @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {

        OrderModel order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "не удалось найти заказ"));
        }

        String newStatus = body.get("status");
        if (newStatus != null) {
            order.setStatus(newStatus);
        }

        String reason = body.get("cancellationReason");

        System.out.println("===================================");
        System.out.println("ОБНОВЛЕНИЕ СТАТУСА ЗАКАЗА #" + orderId);
        System.out.println("status: " + newStatus);
        System.out.println("cancellationReason: " + reason);
        System.out.println("===================================");

        if (reason != null) {
            order.setCancellationReason(reason);
        }

        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of("message", "Статус обновлён", "status", order.getStatus()));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(@RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/user/{userId}/last")
    public ResponseEntity<?> getLastUserOrder(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Пользователь не найден"));
        }

        List<OrderModel> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        if (orders.isEmpty()) {
            return ResponseEntity.ok(Map.of("hashOrder", false));
        }

        OrderModel lastOrder = orders.get(0);

        return ResponseEntity.ok(Map.of(
                "hasOrder", true,
                "firstName", lastOrder.getFirstName() != null ? lastOrder.getFirstName() : "",
                "lastName", lastOrder.getLastName() != null ? lastOrder.getLastName() : "",
                "middleName", lastOrder.getMiddleName() != null ? lastOrder.getMiddleName() : "",
                "phone", lastOrder.getPhoneAtOrder() != null ? lastOrder.getPhoneAtOrder() : "",
                "address", lastOrder.getAddress() != null ? lastOrder.getAddress() : "")
        );
    }
}
