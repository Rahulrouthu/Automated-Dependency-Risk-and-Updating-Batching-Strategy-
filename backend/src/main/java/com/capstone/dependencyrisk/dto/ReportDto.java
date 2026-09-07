package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.RiskLevel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDto {
    private Long repositoryId;
    private String repositoryName;
    private String repositoryUrl;
    private LocalDateTime generatedAt;
    private double healthGradeScore;
    private String healthGrade; // A, B, C, D, F
    private RiskLevel overallRisk;
    private int totalDependencies;
    private int outdatedDependencies;
    private int totalVulnerabilities;
    private int criticalVulnerabilities;
    private int highVulnerabilities;
    private int moderateVulnerabilities;
    private int lowVulnerabilities;
    private int totalBatchesRecommended;
    private String dataSource = "LIVE";
    private String analysisConfidence = "HIGH";
    private double analysisConfidenceScore = 100.0;
    private String riskConfigSnapshot;
    private List<String> executiveSummaryBullets = new ArrayList<>();
    private List<String> prioritizedRemediationRoadmap = new ArrayList<>();
    private List<BatchDto> proposedBatches = new ArrayList<>();
    private List<DependencyDetailDto> criticalItems = new ArrayList<>();

    public ReportDto() {}

    public Long getRepositoryId() { return repositoryId; }
    public void setRepositoryId(Long repositoryId) { this.repositoryId = repositoryId; }

    public String getRepositoryName() { return repositoryName; }
    public void setRepositoryName(String repositoryName) { this.repositoryName = repositoryName; }

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public double getHealthGradeScore() { return healthGradeScore; }
    public void setHealthGradeScore(double healthGradeScore) { this.healthGradeScore = healthGradeScore; }

    public String getHealthGrade() { return healthGrade; }
    public void setHealthGrade(String healthGrade) { this.healthGrade = healthGrade; }

    public RiskLevel getOverallRisk() { return overallRisk; }
    public void setOverallRisk(RiskLevel overallRisk) { this.overallRisk = overallRisk; }

    public int getTotalDependencies() { return totalDependencies; }
    public void setTotalDependencies(int totalDependencies) { this.totalDependencies = totalDependencies; }

    public int getOutdatedDependencies() { return outdatedDependencies; }
    public void setOutdatedDependencies(int outdatedDependencies) { this.outdatedDependencies = outdatedDependencies; }

    public int getTotalVulnerabilities() { return totalVulnerabilities; }
    public void setTotalVulnerabilities(int totalVulnerabilities) { this.totalVulnerabilities = totalVulnerabilities; }

    public int getCriticalVulnerabilities() { return criticalVulnerabilities; }
    public void setCriticalVulnerabilities(int criticalVulnerabilities) { this.criticalVulnerabilities = criticalVulnerabilities; }

    public int getHighVulnerabilities() { return highVulnerabilities; }
    public void setHighVulnerabilities(int highVulnerabilities) { this.highVulnerabilities = highVulnerabilities; }

    public int getModerateVulnerabilities() { return moderateVulnerabilities; }
    public void setModerateVulnerabilities(int moderateVulnerabilities) { this.moderateVulnerabilities = moderateVulnerabilities; }

    public int getLowVulnerabilities() { return lowVulnerabilities; }
    public void setLowVulnerabilities(int lowVulnerabilities) { this.lowVulnerabilities = lowVulnerabilities; }

    public int getTotalBatchesRecommended() { return totalBatchesRecommended; }
    public void setTotalBatchesRecommended(int totalBatchesRecommended) { this.totalBatchesRecommended = totalBatchesRecommended; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getAnalysisConfidence() { return analysisConfidence; }
    public void setAnalysisConfidence(String analysisConfidence) { this.analysisConfidence = analysisConfidence; }

    public double getAnalysisConfidenceScore() { return analysisConfidenceScore; }
    public void setAnalysisConfidenceScore(double analysisConfidenceScore) { this.analysisConfidenceScore = analysisConfidenceScore; }

    public String getRiskConfigSnapshot() { return riskConfigSnapshot; }
    public void setRiskConfigSnapshot(String riskConfigSnapshot) { this.riskConfigSnapshot = riskConfigSnapshot; }

    public List<String> getExecutiveSummaryBullets() { return executiveSummaryBullets; }
    public void setExecutiveSummaryBullets(List<String> executiveSummaryBullets) { this.executiveSummaryBullets = executiveSummaryBullets; }

    public List<String> getPrioritizedRemediationRoadmap() { return prioritizedRemediationRoadmap; }
    public void setPrioritizedRemediationRoadmap(List<String> prioritizedRemediationRoadmap) { this.prioritizedRemediationRoadmap = prioritizedRemediationRoadmap; }

    public List<BatchDto> getProposedBatches() { return proposedBatches; }
    public void setProposedBatches(List<BatchDto> proposedBatches) { this.proposedBatches = proposedBatches; }

    public List<DependencyDetailDto> getCriticalItems() { return criticalItems; }
    public void setCriticalItems(List<DependencyDetailDto> criticalItems) { this.criticalItems = criticalItems; }
}
