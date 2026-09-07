package com.capstone.dependencyrisk.controller;

import com.capstone.dependencyrisk.dto.AnalysisResultDto;
import com.capstone.dependencyrisk.dto.AnalyzeRequestDto;
import com.capstone.dependencyrisk.dto.PresetRepoDto;
import com.capstone.dependencyrisk.dto.RepositorySummaryDto;
import com.capstone.dependencyrisk.service.GitHubRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@CrossOrigin(origins = "*")
public class RepositoryAnalysisController {

    private final GitHubRepositoryService gitHubRepositoryService;

    @Autowired
    public RepositoryAnalysisController(GitHubRepositoryService gitHubRepositoryService) {
        this.gitHubRepositoryService = gitHubRepositoryService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResultDto> analyzeRepository(@RequestBody AnalyzeRequestDto request) {
        if (request.getRepositoryUrl() == null || request.getRepositoryUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AnalysisResultDto result = gitHubRepositoryService.analyzeRepository(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RepositorySummaryDto>> getRecentRepositories() {
        return ResponseEntity.ok(gitHubRepositoryService.getRecentRepositories());
    }

    @GetMapping("/presets")
    public ResponseEntity<List<PresetRepoDto>> getPresets() {
        return ResponseEntity.ok(gitHubRepositoryService.getPresets());
    }
}
