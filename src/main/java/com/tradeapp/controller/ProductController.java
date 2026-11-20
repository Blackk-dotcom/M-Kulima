package com.tradeapp.controller;

import com.tradeapp.model.Product;
import com.tradeapp.model.User;
import com.tradeapp.repository.ProductRepository;
import com.tradeapp.repository.UserRepository;
import com.tradeapp.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ProductController(ProductRepository productRepository, UserRepository userRepository,
                              FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) Long producerId,
                               @RequestParam(required = false) String category) {
        if (producerId != null) return productRepository.findByProducerId(producerId);
        if (category != null) return productRepository.findByCategory(category);
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<Product> create(@RequestBody Product product, Authentication auth) {
        User producer = (User) auth.getPrincipal();
        product.setProducer(producer);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setCategory(updated.getCategory());
            existing.setUnitPrice(updated.getUnitPrice());
            existing.setUnit(updated.getUnit());
            existing.setStockQty(updated.getStockQty());
            existing.setMinOrderQuantity(updated.getMinOrderQuantity());
            return ResponseEntity.ok(productRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Matches PRODUCT_IMAGE_API.md: stores the file, writes the resulting URL
    // onto the product, and returns the updated product JSON.
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('PRODUCER')")
    public ResponseEntity<Product> uploadImage(@PathVariable Long id,
                                                @RequestParam("image") MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        String relativeUrl = fileStorageService.store(id, image);
        product.setImageUrl(relativeUrl);
        return ResponseEntity.ok(productRepository.save(product));
    }
}
