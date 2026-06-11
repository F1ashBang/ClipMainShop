package com.example.demo.controllers;

import com.example.demo.models.Image;
import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    private static final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestParam("title") String title,
                                           @RequestParam("price") String price,
                                           @RequestParam("description") String description,
                                           @RequestParam(value = "sizes", required = false) String sizesJson,
                                           @RequestParam("files") List<MultipartFile> files) {
        try {
            File uploadDir = new File(UPLOAD_DIR);

            Product product = new Product(title, price, description);

            if (!uploadDir.exists()) { uploadDir.mkdirs(); }

            if (sizesJson != null && !sizesJson.isEmpty()) {
                String[] sizeNames = sizesJson.replace("[", "").replace("]", "")
                        .replace("\"", "").split(",");
                for (String name : sizeNames) {
                    name = name.trim();
                    if (!name.isEmpty()) {
                        var sizeOpt = sizeRepository.findByName(name);
                        if (sizeOpt.isPresent()) {
                            product.addSize(sizeOpt.get());
                        }
                    }
                }
            }

            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String uniqueName = UUID.randomUUID().toString() + extension;

                Files.write(Paths.get(UPLOAD_DIR, uniqueName), file.getBytes());

                Image image = new Image(originalName, uniqueName);
                product.addImage(image);
            }

            productRepository.save(product);

            return ResponseEntity.ok(Map.of("message", "Товар создан", "id: ", product.getId()));
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Не удалось создать запись", "ошибка: ", e.getMessage()));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAllByOrderByIdDesc());
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchProducts(@RequestParam String q) {
        List<Product> results = productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/products/{id:\\d+}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Path filePath = Paths.get("C:/uploads/", filename);
        Resource resource = new FileSystemResource(filePath);

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
