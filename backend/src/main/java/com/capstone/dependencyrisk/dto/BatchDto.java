package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.BatchCategory;
import com.capstone.dependencyrisk.entity.RiskLevel;
import java.util.ArrayList;
import java.util.List;

public class BatchDto {
    private Long id;
    private int batchNumber;
    private String title;
    private BatchCategory category;
    private RiskLevel batchRiskLevel;
    private double batchRiskScore;
    private String strategyDescription;
    private String reasonForGrouping;
    private List<String> constraints = new ArrayList<>();
    private String algorithmName = "Risk-Aware Constraint-Based Greedy Dependency Batching Strategy";
    private String executionCommands;
    private String prTitle;
    private String prBody;
    private List<DependencyDetailDto> dependencies = new ArrayList<>();

    public BatchDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getBatchNumber() { return batchNumber; }
    public void setBatchNumber(int batchNumber) { this.batchNumber = batchNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BatchCategory getCategory() { return category; }
    public void setCategory(BatchCategory category) { this.category = category; }

    public RiskLevel getBatchRiskLevel() { return batchRiskLevel; }
    public void setBatchRiskLevel(RiskLevel batchRiskLevel) { this.batchRiskLevel = batchRiskLevel; }

    public double getBatchRiskScore() { return batchRiskScore; }
    public void setBatchRiskScore(double batchRiskScore) { this.batchRiskScore = batchRiskScore; }

    public String getStrategyDescription() { return strategyDescription; }
    public void setStrategyDescription(String strategyDescription) { this.strategyDescription = strategyDescription; }

    public String getReasonForGrouping() { return reasonForGrouping; }
    public void setReasonForGrouping(String reasonForGrouping) { this.reasonForGrouping = reasonForGrouping; }

    public List<String> getConstraints() { return constraints; }
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }

    public String getAlgorithmName() { return algorithmName; }
    public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }

    public String getExecutionCommands() { return executionCommands; }
    public void setExecutionCommands(String executionCommands) { this.executionCommands = executionCommands; }

    public String getPrTitle() { return prTitle; }
    public void setPrTitle(String prTitle) { this.prTitle = prTitle; }

    public String getPrBody() { return prBody; }
    public void setPrBody(String prBody) { this.prBody = prBody; }

    public List<DependencyDetailDto> getDependencies() { return dependencies; }
    public void setDependencies(List<DependencyDetailDto> dependencies) { this.dependencies = dependencies; }
}
