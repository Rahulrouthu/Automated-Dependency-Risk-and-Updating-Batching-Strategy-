package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.RiskLevel;
import java.time.LocalDateTime;
import java.util.List;

public class RepositorySummaryDto {
    private Long id;
    private String owner;
    private String name;
    private String fullName;
    private String url;
    private String defaultBranch;
    private List<String> detectedEcosystems;
    private int totalFiles;
    private int totalDependencies;
    private int outdatedCount;
    private int vulnerabilityCount;
    private int criticalCount;
    private int highCount;
    private int moderateCount;
    private int lowCount;
    private int recommendedBatches;
    private double overallRiskScore;
    private RiskLevel overallRiskLevel;
    private String dataSource = "LIVE";
    private String analysisConfidence = "HIGH";
    private double analysisConfidenceScore = 100.0;
    private String analysisStatus = "COMPLETED";
    private String githubStatus = "CONNECTED";
    private String registryStatus = "CONNECTED";
    private String securityStatus = "CONNECTED";
    private String riskConfigSnapshot;
    private LocalDateTime lastScannedAt;

    public RepositorySummaryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFullName() { return (owner != null && name != null) ? owner + "/" + name : name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }

    public List<String> getDetectedEcosystems() { return detectedEcosystems; }
    public void setDetectedEcosystems(List<String> detectedEcosystems) { this.detectedEcosystems = detectedEcosystems; }

    public int getTotalFiles() { return totalFiles; }
    public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }

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

    public int getModerateCount() { return moderateCount; }
    public void setModerateCount(int moderateCount) { this.moderateCount = moderateCount; }

    public int getLowCount() { return lowCount; }
    public void setLowCount(int lowCount) { this.lowCount = lowCount; }

    public int getRecommendedBatches() { return recommendedBatches; }
    public void setRecommendedBatches(int recommendedBatches) { this.recommendedBatches = recommendedBatches; }

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

    public String getAnalysisStatus() { return analysisStatus; }
    public void setAnalysisStatus(String analysisStatus) { this.analysisStatus = analysisStatus; }

    public String getGithubStatus() { return githubStatus; }
    public void setGithubStatus(String githubStatus) { this.githubStatus = githubStatus; }

    public String getRegistryStatus() { return registryStatus; }
    public void setRegistryStatus(String registryStatus) { this.registryStatus = registryStatus; }

    public String getSecurityStatus() { return securityStatus; }
    public void setSecurityStatus(String securityStatus) { this.securityStatus = securityStatus; }

    public String getRiskConfigSnapshot() { return riskConfigSnapshot; }
    public void setRiskConfigSnapshot(String riskConfigSnapshot) { this.riskConfigSnapshot = riskConfigSnapshot; }

    public LocalDateTime getLastScannedAt() { return lastScannedAt; }
    public void setLastScannedAt(LocalDateTime lastScannedAt) { this.lastScannedAt = lastScannedAt; }
}
