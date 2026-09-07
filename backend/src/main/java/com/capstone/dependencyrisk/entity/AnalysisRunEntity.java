package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_runs")
public class AnalysisRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private RepositoryEntity repository;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Column(name = "total_dependencies")
    private int totalDependencies = 0;

    @Column(name = "outdated_count")
    private int outdatedCount = 0;

    @Column(name = "vulnerability_count")
    private int vulnerabilityCount = 0;

    @Column(name = "critical_count")
    private int criticalCount = 0;

    @Column(name = "high_count")
    private int highCount = 0;

    @Column(name = "batch_count")
    private int batchCount = 0;

    @Column(name = "overall_risk_score")
    private double overallRiskScore = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_risk_level")
    private RiskLevel overallRiskLevel = RiskLevel.LOW;

    @Column(name = "data_source")
    private String dataSource = "LIVE";

    @Column(name = "analysis_confidence")
    private String analysisConfidence = "HIGH";

    @Column(name = "analysis_confidence_score")
    private double analysisConfidenceScore = 100.0;

    @Column(name = "risk_config_snapshot_json", columnDefinition = "TEXT")
    private String riskConfigSnapshotJson;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public AnalysisRunEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepositoryEntity getRepository() { return repository; }
    public void setRepository(RepositoryEntity repository) { this.repository = repository; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public int getTotalDependencies() { return totalDependencies; }
    public void setTotalDependencies(int totalDependencies) { this.totalDependencies = totalDependencies; }

    public int getOutdatedCount() { return outdatedCount; }
    public void setOutdatedCount(int outdatedCount) { this.outdatedCount = outdatedCount; }

    public int getVulnerabilityCount() { return vulnerabilityCount; }
    public void setVulnerabilityCount(int vulnerabilityCount) { this.vulnerabilityCount = vulnerabilityCount; }

    public int getCriticalCount() { return criticalCount; }
    public void setCriticalCount(int criticalCount) { this.criticalCount = criticalCount; }

    public int getHighCount() { return highCount; }
    public void setHighCount(int highCount) { this.highCount = highCount; }

    public int getBatchCount() { return batchCount; }
    public void setBatchCount(int batchCount) { this.batchCount = batchCount; }

    public double getOverallRiskScore() { return overallRiskScore; }
    public void setOverallRiskScore(double overallRiskScore) { this.overallRiskScore = overallRiskScore; }

    public RiskLevel getOverallRiskLevel() { return overallRiskLevel; }
    public void setOverallRiskLevel(RiskLevel overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getAnalysisConfidence() { return analysisConfidence; }
    public void setAnalysisConfidence(String analysisConfidence) { this.analysisConfidence = analysisConfidence; }

    public double getAnalysisConfidenceScore() { return analysisConfidenceScore; }
    public void setAnalysisConfidenceScore(double analysisConfidenceScore) { this.analysisConfidenceScore = analysisConfidenceScore; }

    public String getRiskConfigSnapshotJson() { return riskConfigSnapshotJson; }
    public void setRiskConfigSnapshotJson(String riskConfigSnapshotJson) { this.riskConfigSnapshotJson = riskConfigSnapshotJson; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
