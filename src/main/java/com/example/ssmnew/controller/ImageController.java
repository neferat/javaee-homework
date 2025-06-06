package com.example.ssmnew.controller;

import com.example.ssmnew.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/api/images/{imageId}")
    public ResponseEntity<Map<String, String>> getImageUrl(@PathVariable String imageId) {
        String imageUrl = imageService.getImageUrl(imageId);
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }
} 