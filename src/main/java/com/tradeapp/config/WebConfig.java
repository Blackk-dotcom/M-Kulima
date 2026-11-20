package com.tradeapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

// Serves uploaded product images back out over HTTP from /uploads/** so the
// Android app can load them directly (e.g. via Glide) using the imageUrl
// returned by the product-image upload endpoint.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}
