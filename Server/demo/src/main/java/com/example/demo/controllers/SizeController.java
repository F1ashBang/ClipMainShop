package com.example.demo.controllers;


import com.example.demo.models.Size;
import com.example.demo.repositories.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sizes")
public class SizeController {

    @Autowired
    private SizeRepository sizeRepository;

    @GetMapping
    public ResponseEntity<List<Size>> getAllSizes () {
        return ResponseEntity.ok(sizeRepository.findAllByOrderBySortOrderAsc());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Size>> getSizesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(sizeRepository.findByCategory(category));
    }

    @PostMapping
    public ResponseEntity<?> createSize(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String category = body.getOrDefault("category", "универсальный");
        int sortOrder = Integer.parseInt(body.getOrDefault("sortOrder", "0"));

        if (sizeRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "такой размер уже существует"));
        }

        Size size = new Size(sortOrder, category, name);
        sizeRepository.save(size);

        return ResponseEntity.ok(Map.of("message", "размер успешно добавлен"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSize(@PathVariable Long id) {
        var sizeOpt = sizeRepository.findById(id);
        if (sizeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Размер не найден"));
        }

        sizeRepository.delete(sizeOpt.get());
        return ResponseEntity.ok(Map.of("message", "Размер успешно удален"));
    }
}
