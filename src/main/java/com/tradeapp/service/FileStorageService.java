package com.tradeapp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// Saves uploaded product images to a local folder on disk. Swapping this for
// S3 or another object store later only means changing this one class - the
// controller and the rest of the app just deal in imageUrl strings.
@Service
public class FileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private Path root;

    @PostConstruct
    public void init() throws IOException {
        root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public String store(Long productId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded image is empty");
        }
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        String filename = "product-" + productId + "-" + UUID.randomUUID() + extension;

        try (InputStream in = file.getInputStream()) {
            Path target = root.resolve(filename);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store uploaded image", e);
        }

        // Matches the static resource mapping in WebConfig (/uploads/products/**).
        return "/uploads/products/" + filename;
    }
}
