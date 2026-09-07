package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_id", nullable = false)
    private DependencyEntity dependency;

    @Column(name = "total_score", nullable = false)
    private double totalScore; // 0.0 - 100.0

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "version_risk")
    private double versionRisk;

    @Column(name = "security_risk")
    private double securityRisk;

    @Column(name = "compatibility_risk")
    private double compatibilityRisk;

    @Column(name = "dependency_impact_risk")
    private double dependencyImpactRisk;

    @Column(name = "build_risk")
    private double buildRisk;

    @Column(name = "explanation_json", columnDefinition = "TEXT")
    private String explanationJson;

    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;

    public RiskAssessmentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DependencyEntity getDependency() { return dependency; }
    public void setDependency(DependencyEntity dependency) { this.dependency = dependency; }

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

    public String getExplanationJson() { return explanationJson; }
    public void setExplanationJson(String explanationJson) { this.explanationJson = explanationJson; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }
}
