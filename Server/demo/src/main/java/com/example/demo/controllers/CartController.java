package com.example.demo.controllers;


import com.example.demo.models.CartItem;
import com.example.demo.repositories.CartItemRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cartItemRepository.findByUser(userOpt.get()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, String> body) {
        Long userId = Long.parseLong(body.get("userId"));
        Long productId = Long.parseLong(body.get("productId"));
        int quantity = Integer.parseInt(body.getOrDefault("quantity", "1"));

        var userOpt = userRepository.findById(userId);
        var productOpt = productRepository.findById(productId);

        if (userOpt.isEmpty() || productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        var existingItem = cartItemRepository.findByUserAndProductId(userOpt.get(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        }
        else {
            CartItem cartItem = new CartItem(userOpt.get(), productOpt.get(), quantity);
            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.ok(Map.of("message", "Товар успешно добавлен!"));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody Map<String, String> body) {
        Long userId = Long.parseLong(body.get("userId"));
        Long productId = Long.parseLong(body.get("productId"));
        int quantity = Integer.parseInt(body.getOrDefault("quantity", "1"));

        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        var itemOpt = cartItemRepository.findByUserAndProductId(userOpt.get(), productId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "товар не найден"));
        }

        if (quantity <= 0) {
            cartItemRepository.delete(itemOpt.get());
            return ResponseEntity.ok(Map.of("message", "Товар удалён из корзины"));
        }

        CartItem item = itemOpt.get();
        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return ResponseEntity.ok(Map.of("message", "Кол-во товаров обновленно"));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeProductFromCart(@RequestBody Map<String, String> body) {
        Long userId = Long.parseLong(body.get("userId"));
        Long productId = Long.parseLong(body.get("productId"));

        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
        }

        var itemOpt = cartItemRepository.findByUserAndProductId(userOpt.get(), productId);
        if (itemOpt.isPresent()) {
            cartItemRepository.delete(itemOpt.get());
        }
        return ResponseEntity.ok(Map.of("message", "товар успешно удален"));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        cartItemRepository.deleteByUser(userOpt.get());
        return ResponseEntity.ok(Map.of("message", "Корзина очищена"));
    }
}
