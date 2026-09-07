package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.dto.RiskAssessmentDto;
import com.capstone.dependencyrisk.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RiskEngine {

    private final RiskWeightsConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RiskEngine(RiskWeightsConfig config) {
        this.config = config;
    }

    public RiskAssessmentEntity calculateRisk(DependencyEntity dependency, List<VulnerabilityEntity> vulns, boolean hasBuildConflict) {
        RiskWeightsConfig.Weights w = config.getWeights();
        List<String> reasons = new ArrayList<>();
        List<RiskAssessmentDto.FactorContributionDto> contributions = new ArrayList<>();

        // 1. Version Change Risk (0 to 25 pts based on weights)
        double versionScore = 0.0;
        VersionDiffType diff = dependency.getVersionDiffType();
        if (diff == VersionDiffType.MAJOR) {
            versionScore = w.getVersionChange();
            reasons.add("+" + (int)versionScore + " Major version jump (" + dependency.getCurrentVersion() + " -> " + dependency.getLatestVersion() + ") introduces breaking API changes");
        } else if (diff == VersionDiffType.MINOR) {
            versionScore = w.getVersionChange() * 0.45;
            reasons.add("+" + String.format("%.1f", versionScore) + " Minor feature release (" + dependency.getCurrentVersion() + " -> " + dependency.getLatestVersion() + ")");
        } else if (diff == VersionDiffType.PATCH) {
            versionScore = w.getVersionChange() * 0.15;
            reasons.add("+" + String.format("%.1f", versionScore) + " Patch bugfix update (" + dependency.getCurrentVersion() + " -> " + dependency.getLatestVersion() + ")");
        } else if (diff == VersionDiffType.UP_TO_DATE) {
            versionScore = 0.0;
            reasons.add("+0.0 Dependency is up to date");
        }

        // 2. Security Vulnerability Risk (0 to 35+ pts based on weights and CVSS)
        double securityScore = 0.0;
        String securityPriority = "NONE";
        boolean securityUnavailable = "SECURITY_DATA_UNAVAILABLE".equalsIgnoreCase(dependency.getSecurityDataStatus());

        if (securityUnavailable) {
            securityPriority = "UNAVAILABLE";
            reasons.add("Security vulnerability data could not be verified (OSV service unreachable)");
        } else if (vulns != null && !vulns.isEmpty()) {
            VulnerabilitySeverity maxSev = VulnerabilitySeverity.UNKNOWN;
            Double maxCvss = 0.0;
            for (VulnerabilityEntity v : vulns) {
                if (v.getSeverity().ordinal() < maxSev.ordinal() || maxSev == VulnerabilitySeverity.UNKNOWN) {
                    maxSev = v.getSeverity();
                }
                if (v.getCvssScore() != null && v.getCvssScore() > maxCvss) {
                    maxCvss = v.getCvssScore();
                }
            }

            if (maxSev == VulnerabilitySeverity.CRITICAL || maxCvss >= 9.0) {
                securityScore = Math.max(w.getSecurityVulnerability() * 1.8, 60.0);
                securityPriority = "CRITICAL";
                reasons.add("+" + String.format("%.1f", securityScore) + " Critical severity vulnerability detected (" + vulns.get(0).getVulnId() + ", CVSS " + (maxCvss > 0 ? maxCvss : 9.8) + ")");
            } else if (maxSev == VulnerabilitySeverity.HIGH || maxCvss >= 7.0) {
                securityScore = w.getSecurityVulnerability() * 1.0;
                securityPriority = "HIGH";
                reasons.add("+" + String.format("%.1f", securityScore) + " High severity vulnerability detected (" + vulns.get(0).getVulnId() + ")");
            } else if (maxSev == VulnerabilitySeverity.MODERATE || maxCvss >= 4.0) {
                securityScore = w.getSecurityVulnerability() * 0.60;
                securityPriority = "MEDIUM";
                reasons.add("+" + String.format("%.1f", securityScore) + " Moderate security advisory detected (" + vulns.get(0).getVulnId() + ")");
            } else if (maxSev == VulnerabilitySeverity.LOW) {
                securityScore = w.getSecurityVulnerability() * 0.30;
                securityPriority = "LOW";
                reasons.add("+" + String.format("%.1f", securityScore) + " Low security notice detected (" + vulns.get(0).getVulnId() + ")");
            }
        } else {
            reasons.add("+0.0 No known security vulnerabilities reported in active database");
        }
        dependency.setSecurityPriority(securityPriority);

        // 3. Compatibility & Deprecation Risk (0 to 20 pts)
        double compatScore = 0.0;
        if (dependency.isDeprecated()) {
            compatScore = w.getCompatibilityBreaking();
            reasons.add("+" + (int)compatScore + " Package is deprecated in official registry");
        } else if (diff == VersionDiffType.MAJOR) {
            compatScore = w.getCompatibilityBreaking() * 0.75;
            reasons.add("+" + String.format("%.1f", compatScore) + " Potential transitive peer-dependency incompatibility across major versions");
        }

        // 4. Dependency Topology & Blast Radius (0 to 12 pts)
        double impactScore = 0.0;
        if (dependency.isDirect()) {
            if (!dependency.isDev()) {
                impactScore = w.getDependencyImpact();
                reasons.add("+" + (int)impactScore + " Core runtime production dependency (high blast radius)");
            } else {
                impactScore = w.getDependencyImpact() * 0.35;
                reasons.add("+" + String.format("%.1f", impactScore) + " Development/test scope dependency");
            }
        } else {
            impactScore = w.getDependencyImpact() * 0.20;
            reasons.add("+" + String.format("%.1f", impactScore) + " Transitive dependency");
        }

        // 5. Build Integrity & Conflict (0 to 8 pts)
        double buildScore = 0.0;
        if (hasBuildConflict) {
            buildScore = w.getBuildIntegrity();
            reasons.add("+" + (int)buildScore + " Version conflict / lockfile mismatch detected");
        }

        // Total score normalized 0 to 100
        double rawTotal = versionScore + securityScore + compatScore + impactScore + buildScore;
        double totalScore = Math.min(100.0, Math.max(0.0, Math.round(rawTotal * 10.0) / 10.0));

        // Separate Pure Update Risk (0-100 normalized without security weight)
        double maxUpdateWeight = w.getVersionChange() + w.getCompatibilityBreaking() + w.getDependencyImpact() + w.getBuildIntegrity();
        double rawUpdateScore = (versionScore + compatScore + impactScore + buildScore);
        double updateRiskScore = maxUpdateWeight > 0 ?
                Math.min(100.0, Math.round((rawUpdateScore / maxUpdateWeight * 100.0) * 10.0) / 10.0) : rawUpdateScore;

        // Determine Risk Level dynamically from configured thresholds
        RiskWeightsConfig.Thresholds t = config.getThresholds();
        RiskLevel totalRiskLevel = classifyScoreWithThresholds(totalScore, t);
        RiskLevel updateRiskLevel = classifyScoreWithThresholds(updateRiskScore, t);

        // Calculate Factor Contributions
        if (totalScore > 0) {
            if (versionScore > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Version Change Risk", Math.round(versionScore * 10.0) / 10.0, Math.round((versionScore / totalScore * 100.0) * 10.0) / 10.0, "Risk from semantic version drift"));
            }
            if (securityScore > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Security Vulnerability Risk", Math.round(securityScore * 10.0) / 10.0, Math.round((securityScore / totalScore * 100.0) * 10.0) / 10.0, "Risk from known CVE advisories"));
            }
            if (compatScore > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Compatibility Risk", Math.round(compatScore * 10.0) / 10.0, Math.round((compatScore / totalScore * 100.0) * 10.0) / 10.0, "Risk from API deprecation or breaking changes"));
            }
            if (impactScore > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Dependency Impact", Math.round(impactScore * 10.0) / 10.0, Math.round((impactScore / totalScore * 100.0) * 10.0) / 10.0, "Blast radius based on runtime vs dev scope"));
            }
            if (buildScore > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Build Integrity", Math.round(buildScore * 10.0) / 10.0, Math.round((buildScore / totalScore * 100.0) * 10.0) / 10.0, "Conflict with lockfile or build tree"));
            }
        }

        // Generate tailored recommendation
        String recommendation = generateRecommendation(totalRiskLevel, updateRiskLevel, securityPriority, diff, vulns, dependency);

        RiskAssessmentEntity entity = new RiskAssessmentEntity();
        entity.setDependency(dependency);
        entity.setTotalScore(totalScore);
        entity.setRiskLevel(totalRiskLevel);
        entity.setVersionRisk(versionScore);
        entity.setSecurityRisk(securityScore);
        entity.setCompatibilityRisk(compatScore);
        entity.setDependencyImpactRisk(impactScore);
        entity.setBuildRisk(buildScore);
        entity.setRecommendationText(recommendation);

        try {
            entity.setExplanationJson(objectMapper.writeValueAsString(reasons));
        } catch (Exception e) {
            entity.setExplanationJson("[\"" + String.join("\", \"", reasons) + "\"]");
        }

        return entity;
    }

    public RiskLevel classifyScoreWithThresholds(double score, RiskWeightsConfig.Thresholds t) {
        if (score <= t.getLowMax()) {
            return RiskLevel.LOW;
        } else if (score <= t.getMediumMax()) {
            return RiskLevel.MEDIUM;
        } else if (score <= t.getHighMax()) {
            return RiskLevel.HIGH;
        } else {
            return RiskLevel.CRITICAL;
        }
    }

    public RiskWeightsConfig getConfig() {
        return config;
    }

    private String generateRecommendation(RiskLevel totalLevel, RiskLevel updateLevel, String securityPriority, VersionDiffType diff, List<VulnerabilityEntity> vulns, DependencyEntity dep) {
        if ("CRITICAL".equals(securityPriority) || "HIGH".equals(securityPriority)) {
            if (updateLevel == RiskLevel.HIGH || updateLevel == RiskLevel.CRITICAL) {
                return "Prioritize the security update immediately, but perform controlled compatibility and regression testing before deployment (High Update Risk).";
            }
            if (vulns != null && !vulns.isEmpty()) {
                String fixVer = vulns.get(0).getFixedVersion();
                if (fixVer != null && !fixVer.isEmpty()) {
                    return "URGENT: Remediate immediately. Upgrade " + dep.getName() + " to secure version " + fixVer + " in a dedicated security hotfix PR.";
                }
            }
            return "URGENT: Security vulnerability active. Update to latest stable version immediately.";
        }

        if (diff == VersionDiffType.MAJOR || updateLevel == RiskLevel.HIGH || updateLevel == RiskLevel.CRITICAL) {
            return "Major version change detected. Isolate into standalone pull request with dedicated regression test suite.";
        }
        if (updateLevel == RiskLevel.MEDIUM) {
            return "Test in staging environment. Verify integration tests pass before deploying to production.";
        }
        return "Safe to update. Backward-compatible patch or minor release safe for automated batch merging.";
    }
}
