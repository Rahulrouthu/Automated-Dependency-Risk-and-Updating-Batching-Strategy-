package com.capstone.dependencyrisk.dto;

import java.util.ArrayList;
import java.util.List;

public class BatchPlanDto {
    private Long repositoryId;
    private String repositoryUrl;
    private int totalBatches;
    private int totalDependenciesInBatches;
    private List<BatchDto> batches = new ArrayList<>();
    private String globalExecutionGuide;

    public BatchPlanDto() {}

    public Long getRepositoryId() { return repositoryId; }
    public void setRepositoryId(Long repositoryId) { this.repositoryId = repositoryId; }

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public int getTotalBatches() { return totalBatches; }
    public void setTotalBatches(int totalBatches) { this.totalBatches = totalBatches; }

    public int getTotalDependenciesInBatches() { return totalDependenciesInBatches; }
    public void setTotalDependenciesInBatches(int totalDependenciesInBatches) { this.totalDependenciesInBatches = totalDependenciesInBatches; }

    public List<BatchDto> getBatches() { return batches; }
    public void setBatches(List<BatchDto> batches) { this.batches = batches; }

    public String getGlobalExecutionGuide() { return globalExecutionGuide; }
    public void setGlobalExecutionGuide(String globalExecutionGuide) { this.globalExecutionGuide = globalExecutionGuide; }
}
