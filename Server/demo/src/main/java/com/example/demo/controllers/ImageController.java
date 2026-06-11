package com.example.demo.controllers;

import com.example.demo.models.Image;
import com.example.demo.repositories.ImageRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ImageController {

    private ImageRepository imageRepository;
    private static final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file")MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String uniqueName = UUID.randomUUID().toString() + extension;

            Path filePath = Paths.get(UPLOAD_DIR, uniqueName);
            Files.write(filePath, file.getBytes());

            Image image = new Image(originalName, uniqueName);
            imageRepository.save(image);

            return ResponseEntity.ok(Map.of("message", "фото загружено", "id: ", image.getId(), "fileName: ", uniqueName));
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка загрузки " + e.getMessage()));
        }
    }

    @PostMapping("/images")
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageRepository.findAllByOrderByUploadedAtDesc();
        return ResponseEntity.ok(images);
    }

}
