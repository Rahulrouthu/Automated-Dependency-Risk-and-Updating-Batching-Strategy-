package com.capstone.dependencyrisk.controller;

import com.capstone.dependencyrisk.dto.PresetRepoDto;
import com.capstone.dependencyrisk.service.GitHubRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthCheckController {

    private final GitHubRepositoryService gitHubRepositoryService;

    @Autowired
    public HealthCheckController(GitHubRepositoryService gitHubRepositoryService) {
        this.gitHubRepositoryService = gitHubRepositoryService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Automated Dependency Update Risk Assessment and Batching Strategy Backend");
        health.put("version", "1.0.0");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/presets")
    public ResponseEntity<List<PresetRepoDto>> getPresets() {
        return ResponseEntity.ok(gitHubRepositoryService.getPresets());
    }
}
