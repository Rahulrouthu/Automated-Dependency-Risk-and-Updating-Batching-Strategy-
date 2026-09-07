package com.capstone.dependencyrisk.entity;

public enum RiskLevel {
    LOW("Low Risk", "#10b981", "Safe to update via standard automated batch."),
    MEDIUM("Medium Risk", "#3b82f6", "Test in staging environment prior to production deployment."),
    HIGH("High Risk", "#f59e0b", "Perform full regression testing and review official migration guides."),
    CRITICAL("Critical Risk", "#ef4444", "Prioritize immediately due to security vulnerability or severe breaking change.");

    private final String label;
    private final String colorCode;
    private final String defaultRecommendation;

    RiskLevel(String label, String colorCode, String defaultRecommendation) {
        this.label = label;
        this.colorCode = colorCode;
        this.defaultRecommendation = defaultRecommendation;
    }

    public String getLabel() {
        return label;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getDefaultRecommendation() {
        return defaultRecommendation;
    }
}
