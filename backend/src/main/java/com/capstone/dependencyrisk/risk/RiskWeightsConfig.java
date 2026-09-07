package com.capstone.dependencyrisk.risk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.risk")
public class RiskWeightsConfig {

    private Weights weights = new Weights();
    private Thresholds thresholds = new Thresholds();

    public static class Weights {
        private double versionChange = 25.0;
        private double securityVulnerability = 35.0;
        private double compatibilityBreaking = 20.0;
        private double dependencyImpact = 12.0;
        private double buildIntegrity = 8.0;

        public double getVersionChange() { return versionChange; }
        public void setVersionChange(double versionChange) { this.versionChange = versionChange; }

        public double getSecurityVulnerability() { return securityVulnerability; }
        public void setSecurityVulnerability(double securityVulnerability) { this.securityVulnerability = securityVulnerability; }

        public double getCompatibilityBreaking() { return compatibilityBreaking; }
        public void setCompatibilityBreaking(double compatibilityBreaking) { this.compatibilityBreaking = compatibilityBreaking; }

        public double getDependencyImpact() { return dependencyImpact; }
        public void setDependencyImpact(double dependencyImpact) { this.dependencyImpact = dependencyImpact; }

        public double getBuildIntegrity() { return buildIntegrity; }
        public void setBuildIntegrity(double buildIntegrity) { this.buildIntegrity = buildIntegrity; }
    }

    public static class Thresholds {
        private double lowMax = 24.0;
        private double mediumMax = 49.0;
        private double highMax = 74.0;
        private double criticalMin = 75.0;

        public double getLowMax() { return lowMax; }
        public void setLowMax(double lowMax) { this.lowMax = lowMax; }

        public double getMediumMax() { return mediumMax; }
        public void setMediumMax(double mediumMax) { this.mediumMax = mediumMax; }

        public double getHighMax() { return highMax; }
        public void setHighMax(double highMax) { this.highMax = highMax; }

        public double getCriticalMin() { return criticalMin; }
        public void setCriticalMin(double criticalMin) { this.criticalMin = criticalMin; }
    }

    public Weights getWeights() { return weights; }
    public void setWeights(Weights weights) { this.weights = weights; }

    public Thresholds getThresholds() { return thresholds; }
    public void setThresholds(Thresholds thresholds) { this.thresholds = thresholds; }
}
