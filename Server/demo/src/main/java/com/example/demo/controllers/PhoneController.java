package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.Map;

@RestController
public class PhoneController {
    @Autowired
    private UserRepository userRepository;

    @Value("${admin.api.key}")
    private String adminApiKey;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");

        if (phone == null || !phone.matches("\\d{11}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Номер уже зарегистрирован!"));
        }

        User user = new User(phone);
        userRepository.save(user);


        System.out.println("===========================");
        System.out.println("Код для " + phone + ":" + user.getVerificationCode());
        System.out.println("===========================");


        return ResponseEntity.ok(Map.of("message", "Код отправлен", "userId", user.getId()));
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String phone) {
        boolean valid = phone != null && phone.matches("\\d{11}");
        boolean exists = userRepository.existsByPhoneNumber(phone);
        return ResponseEntity.ok(Map.of("valid", valid, "exists", exists));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");

        var userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
        }

        User user = userOpt.get();

        if (!user.getVerified()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Номер не подтверждён"));
        }

        String newCode = String.valueOf((int)(Math.random() * 9000) + 1000);
        user.setVerificationCode(newCode);
        userRepository.save(user);

        System.out.println("===========================");
        System.out.println("Код для входа: " + phone + ":" + newCode);
        System.out.println("===========================");

        return ResponseEntity.ok(Map.of("message", "код для входа отправлен", "userId", user.getId()));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String code = body.get("code");

        System.out.println("===================================");
        System.out.println("ЗАПРОС НА ПРОВЕРКУ КОДА:");
        System.out.println("phone: " + phone);
        System.out.println("code: " + code);
        System.out.println("===================================");

        var userOpt = userRepository.findByPhoneNumber(phone);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Номер телефона не найден"));
        }

        User user = userOpt.get();

        System.out.println("Код из БД: " + user.getVerificationCode());
        System.out.println("Совпадает? " + user.getVerificationCode().equals(code));
        System.out.println("===================================");

        if (!user.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Неверный код"));
        }


        user.setVerified(true);
        userRepository.save(user);

        System.out.println("НОМЕР ПОДТВЕРЖДЁН: " + phone);
        System.out.println("===================================");

        return ResponseEntity.ok(Map.of(
                "message", "Номер подтвержден!",
                "userId", user.getId()
        ));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {

        String phone = body.get("phone");
        String code = body.get("code");


        System.out.println("АДМИН ВХОД: phone=" + phone + ", code=" + code  );
        System.out.println("АДМИН ВХОД: adminKey=" + adminKey);

        if (adminKey == null || !adminKey.equals(adminApiKey)) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        var userOpt = userRepository.findByPhoneNumber(phone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        User user = userOpt.get();

        if (!user.getIsAdmin()) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        if (!user.getVerificationCode().equals(code)) {
            return ResponseEntity.badRequest().body(Map.of("error", "пользователь не найден"));
        }

        user.setVerificationCode(null);
        userRepository.save(user);

        System.out.println("===================================");
        System.out.println("АДМИН ВОШЁЛ: " + phone);
        System.out.println("===================================");

        return ResponseEntity.ok(Map.of("message", "вход выполнен", "userId", user.getId()));
    }

    @PutMapping("admin/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {

        if (adminKey == null || !adminKey.equals(adminApiKey)) {
            return ResponseEntity.status(403).body(Map.of("error", "доступ запрещен"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не найден"));
        }

        User user = userOpt.get();
        if (body.containsKey("isAdmin")) {
            user.setIsAdmin(Boolean.parseBoolean(body.get("isAdmin")));
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "обновлено"));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {

        if (adminKey == null || !adminKey.equals(adminApiKey)) {
            return ResponseEntity.status(403).body(Map.of("error", "доступ запрещен"));
        }

        return ResponseEntity.ok(userRepository.findAll());
    }
}
