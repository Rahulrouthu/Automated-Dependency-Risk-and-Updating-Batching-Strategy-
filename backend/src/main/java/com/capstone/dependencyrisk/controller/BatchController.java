package com.capstone.dependencyrisk.controller;

import com.capstone.dependencyrisk.dto.BatchPlanDto;
import com.capstone.dependencyrisk.service.BatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin(origins = "*")
public class BatchController {

    private final BatchingService batchingService;

    @Autowired
    public BatchController(BatchingService batchingService) {
        this.batchingService = batchingService;
    }

    @GetMapping("/repository/{repositoryId}")
    public ResponseEntity<BatchPlanDto> getBatchesByRepository(@PathVariable Long repositoryId) {
        BatchPlanDto plan = batchingService.getBatchPlan(repositoryId);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }
}
