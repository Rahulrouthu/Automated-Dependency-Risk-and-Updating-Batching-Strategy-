package com.capstone.dependencyrisk.controller;

import com.capstone.dependencyrisk.dto.ReportDto;
import com.capstone.dependencyrisk.service.GitHubRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final GitHubRepositoryService gitHubRepositoryService;

    @Autowired
    public ReportController(GitHubRepositoryService gitHubRepositoryService) {
        this.gitHubRepositoryService = gitHubRepositoryService;
    }

    @GetMapping("/{repositoryId}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long repositoryId) {
        ReportDto report = gitHubRepositoryService.getReport(repositoryId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}
