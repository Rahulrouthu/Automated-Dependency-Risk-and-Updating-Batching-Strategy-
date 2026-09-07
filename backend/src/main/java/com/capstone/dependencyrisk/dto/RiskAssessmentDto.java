package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.RiskLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RiskAssessmentDto {
    private double totalScore;
    private RiskLevel riskLevel;
    private double versionRisk;
    private double securityRisk;
    private double compatibilityRisk;
    private double dependencyImpactRisk;
    private double buildRisk;
    private double updateRiskScore;
    private RiskLevel updateRiskLevel;
    private String securityPriority;
    private List<String> reasons = new ArrayList<>();
    private List<FactorContributionDto> factorContributions = new ArrayList<>();
    private String recommendation;

    public static class FactorContributionDto {
        private String factorName;
        private double points;
        private double percentage;
        private String description;

        public FactorContributionDto() {}

        public FactorContributionDto(String factorName, double points, double percentage, String description) {
            this.factorName = factorName;
            this.points = points;
            this.percentage = percentage;
            this.description = description;
        }

        public String getFactorName() { return factorName; }
        public void setFactorName(String factorName) { this.factorName = factorName; }

        public double getPoints() { return points; }
        public void setPoints(double points) { this.points = points; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public RiskAssessmentDto() {}

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public double getVersionRisk() { return versionRisk; }
    public void setVersionRisk(double versionRisk) { this.versionRisk = versionRisk; }

    public double getSecurityRisk() { return securityRisk; }
    public void setSecurityRisk(double securityRisk) { this.securityRisk = securityRisk; }

    public double getCompatibilityRisk() { return compatibilityRisk; }
    public void setCompatibilityRisk(double compatibilityRisk) { this.compatibilityRisk = compatibilityRisk; }

    public double getDependencyImpactRisk() { return dependencyImpactRisk; }
    public void setDependencyImpactRisk(double dependencyImpactRisk) { this.dependencyImpactRisk = dependencyImpactRisk; }

    public double getBuildRisk() { return buildRisk; }
    public void setBuildRisk(double buildRisk) { this.buildRisk = buildRisk; }

    public double getUpdateRiskScore() { return updateRiskScore; }
    public void setUpdateRiskScore(double updateRiskScore) { this.updateRiskScore = updateRiskScore; }

    public RiskLevel getUpdateRiskLevel() { return updateRiskLevel; }
    public void setUpdateRiskLevel(RiskLevel updateRiskLevel) { this.updateRiskLevel = updateRiskLevel; }

    public String getSecurityPriority() { return securityPriority; }
    public void setSecurityPriority(String securityPriority) { this.securityPriority = securityPriority; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public List<FactorContributionDto> getFactorContributions() { return factorContributions; }
    public void setFactorContributions(List<FactorContributionDto> factorContributions) { this.factorContributions = factorContributions; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
