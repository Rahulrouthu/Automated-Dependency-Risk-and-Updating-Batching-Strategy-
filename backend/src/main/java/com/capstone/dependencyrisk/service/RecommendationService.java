package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.dto.DependencyDetailDto;
import com.capstone.dependencyrisk.entity.RiskLevel;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public String generateAction(DependencyDetailDto dep) {
        if (dep == null) return "No action required";

        if (dep.getRiskAssessment() != null && dep.getRiskAssessment().getRiskLevel() == RiskLevel.CRITICAL) {
            return "🚨 Immediate Security Hotfix";
        }
        if (dep.getVersionDiffType() == VersionDiffType.MAJOR) {
            return "⚠️ Staged Migration & Regression Testing";
        }
        if (dep.getRiskAssessment() != null && dep.getRiskAssessment().getRiskLevel() == RiskLevel.HIGH) {
            return "⚠️ Detailed Testing & Changelog Review";
        }
        if (dep.getVersionDiffType() == VersionDiffType.MINOR) {
            return "⚡ Batch Minor Upgrade";
        }
        if (dep.getVersionDiffType() == VersionDiffType.PATCH) {
            return "✓ Safe Auto-Patch";
        }
        return "✓ Up to Date";
    }
}
