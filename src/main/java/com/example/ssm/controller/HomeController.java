package com.example.ssm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Small welcome endpoint for the application root.
 *
 * <p>This prevents /ssm-crud-demo/ from looking like a broken site and points
 * learners to the real REST API entry point.</p>
 */
@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message", "ssm-crud-demo is running",
                "booksApi", "/ssm-crud-demo/api/books"
        );
    }
}
