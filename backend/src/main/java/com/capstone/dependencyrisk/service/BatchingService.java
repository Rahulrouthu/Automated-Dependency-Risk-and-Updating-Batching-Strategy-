package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.batching.BatchOptimizationEngine;
import com.capstone.dependencyrisk.dto.BatchDto;
import com.capstone.dependencyrisk.dto.BatchPlanDto;
import com.capstone.dependencyrisk.dto.DependencyDetailDto;
import com.capstone.dependencyrisk.entity.*;
import com.capstone.dependencyrisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BatchingService {

    private final BatchOptimizationEngine batchOptimizationEngine;
    private final DependencyBatchJpaRepository batchRepository;
    private final BatchItemJpaRepository batchItemRepository;
    private final RepositoryJpaRepository repositoryJpaRepository;

    @Autowired
    public BatchingService(BatchOptimizationEngine batchOptimizationEngine,
                           DependencyBatchJpaRepository batchRepository,
                           BatchItemJpaRepository batchItemRepository,
                           RepositoryJpaRepository repositoryJpaRepository) {
        this.batchOptimizationEngine = batchOptimizationEngine;
        this.batchRepository = batchRepository;
        this.batchItemRepository = batchItemRepository;
        this.repositoryJpaRepository = repositoryJpaRepository;
    }

    public List<BatchDto> generateBatchesFromDtos(List<DependencyDetailDto> dependencies) {
        return batchOptimizationEngine.generateOptimizedBatches(dependencies);
    }

    @Transactional
    public List<DependencyBatchEntity> persistBatches(RepositoryEntity repoEntity, List<BatchDto> batchDtos, List<DependencyEntity> savedDependencies) {
        // Cleanly delete previous batch items and batches
        if (repoEntity.getId() != null) {
            batchItemRepository.deleteByRepositoryId(repoEntity.getId());
            batchRepository.deleteByRepositoryId(repoEntity.getId());
        }

        Map<Long, DependencyEntity> depMapById = savedDependencies.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(DependencyEntity::getId, Function.identity(), (a, b) -> a));

        Map<String, DependencyEntity> depMapByCoord = savedDependencies.stream()
                .collect(Collectors.toMap(d -> d.getCoordinates().toLowerCase().trim(), Function.identity(), (a, b) -> a));

        List<DependencyBatchEntity> savedBatches = new ArrayList<>();

        for (BatchDto dto : batchDtos) {
            DependencyBatchEntity bEntity = new DependencyBatchEntity();
            bEntity.setRepository(repoEntity);
            bEntity.setBatchNumber(dto.getBatchNumber());
            bEntity.setTitle(dto.getTitle());
            bEntity.setCategory(dto.getCategory());
            bEntity.setBatchRiskLevel(dto.getBatchRiskLevel());
            bEntity.setBatchRiskScore(dto.getBatchRiskScore());
            bEntity.setStrategyDescription(dto.getStrategyDescription());
            bEntity.setReasonForGrouping(dto.getReasonForGrouping());
            bEntity.setExecutionCommands(dto.getExecutionCommands());
            bEntity.setPrTitle(dto.getPrTitle());
            bEntity.setPrBody(dto.getPrBody());

            DependencyBatchEntity savedBatch = batchRepository.save(bEntity);

            List<BatchItemEntity> items = new ArrayList<>();
            for (DependencyDetailDto dDto : dto.getDependencies()) {
                DependencyEntity dEntity = null;
                if (dDto.getId() != null) {
                    dEntity = depMapById.get(dDto.getId());
                }
                if (dEntity == null && dDto.getCoordinates() != null) {
                    dEntity = depMapByCoord.get(dDto.getCoordinates().toLowerCase().trim());
                }

                if (dEntity != null) {
                    items.add(new BatchItemEntity(savedBatch, dEntity, dDto.getRecommendedAction()));
                }
            }

            if (!items.isEmpty()) {
                batchItemRepository.saveAll(items);
            }
            savedBatch.setItems(items);
            savedBatches.add(savedBatch);
        }

        return savedBatches;
    }

    public BatchPlanDto getBatchPlan(Long repositoryId) {
        RepositoryEntity repo = repositoryJpaRepository.findById(repositoryId).orElse(null);
        if (repo == null) return null;

        List<DependencyBatchEntity> batchEntities = batchRepository.findByRepositoryIdOrderByBatchNumberAsc(repositoryId);
        BatchPlanDto plan = new BatchPlanDto();
        plan.setRepositoryId(repositoryId);
        plan.setRepositoryUrl(repo.getUrl());
        plan.setTotalBatches(batchEntities.size());

        int totalDeps = 0;
        List<BatchDto> dtos = new ArrayList<>();
        for (DependencyBatchEntity b : batchEntities) {
            BatchDto dto = new BatchDto();
            dto.setId(b.getId());
            dto.setBatchNumber(b.getBatchNumber());
            dto.setTitle(b.getTitle());
            dto.setCategory(b.getCategory());
            dto.setBatchRiskLevel(b.getBatchRiskLevel());
            dto.setBatchRiskScore(b.getBatchRiskScore());
            dto.setStrategyDescription(b.getStrategyDescription());
            dto.setReasonForGrouping(b.getReasonForGrouping());
            dto.setExecutionCommands(b.getExecutionCommands());
            dto.setPrTitle(b.getPrTitle());
            dto.setPrBody(b.getPrBody());
            totalDeps += b.getItems().size();
            dtos.add(dto);
        }

        plan.setTotalDependenciesInBatches(totalDeps);
        plan.setBatches(dtos);
        plan.setGlobalExecutionGuide("Execute batches sequentially starting from Urgent Security Hotfixes -> Safe Automated Patches -> Feature & Minor Updates -> Isolated Major Upgrades.");
        return plan;
    }
}
