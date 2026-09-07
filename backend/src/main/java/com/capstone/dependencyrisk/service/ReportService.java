package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.dto.BatchDto;
import com.capstone.dependencyrisk.dto.DependencyDetailDto;
import com.capstone.dependencyrisk.dto.ReportDto;
import com.capstone.dependencyrisk.dto.RepositorySummaryDto;
import com.capstone.dependencyrisk.entity.RiskLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    public ReportDto generateExecutiveReport(RepositorySummaryDto summary, List<DependencyDetailDto> dependencies, List<BatchDto> batches) {
        ReportDto report = new ReportDto();
        report.setRepositoryId(summary.getId());
        report.setRepositoryName(summary.getName());
        report.setRepositoryUrl(summary.getUrl());
        report.setGeneratedAt(LocalDateTime.now());
        report.setTotalDependencies(summary.getTotalDependencies());
        report.setOutdatedDependencies(summary.getOutdatedCount());
        report.setTotalVulnerabilities(summary.getVulnerabilityCount());
        report.setCriticalVulnerabilities(summary.getCriticalCount());
        report.setHighVulnerabilities(summary.getHighCount());
        report.setModerateVulnerabilities(summary.getModerateCount());
        report.setLowVulnerabilities(summary.getLowCount());
        report.setTotalBatchesRecommended(batches.size());
        report.setOverallRisk(summary.getOverallRiskLevel());
        report.setProposedBatches(batches);
        report.setDataSource(summary.getDataSource() != null ? summary.getDataSource() : "LIVE");
        report.setAnalysisConfidence(summary.getAnalysisConfidence() != null ? summary.getAnalysisConfidence() : "HIGH");
        report.setAnalysisConfidenceScore(summary.getAnalysisConfidenceScore());
        report.setRiskConfigSnapshot(summary.getRiskConfigSnapshot());

        // Compute Health Grade (A: 90-100, B: 80-89, C: 70-79, D: 60-69, F: <60)
        double healthScore = 100.0 - (summary.getOverallRiskScore() * 0.7 + (summary.getCriticalCount() * 15.0) + (summary.getHighCount() * 8.0));
        healthScore = Math.max(10.0, Math.min(100.0, Math.round(healthScore * 10.0) / 10.0));
        report.setHealthGradeScore(healthScore);

        if (healthScore >= 90) report.setHealthGrade("A");
        else if (healthScore >= 80) report.setHealthGrade("B");
        else if (healthScore >= 70) report.setHealthGrade("C");
        else if (healthScore >= 60) report.setHealthGrade("D");
        else report.setHealthGrade("F");

        // Executive bullets
        List<String> bullets = new ArrayList<>();
        bullets.add("Scanned " + summary.getTotalDependencies() + " total dependencies across detected manifests.");
        bullets.add("Identified " + summary.getOutdatedCount() + " outdated dependencies (" + 
                String.format("%.1f", (summary.getOutdatedCount() * 100.0 / Math.max(1, summary.getTotalDependencies()))) + "% of total).");
        if (summary.getVulnerabilityCount() > 0) {
            bullets.add("Detected " + summary.getVulnerabilityCount() + " security vulnerabilities (" + summary.getCriticalCount() + " Critical, " + summary.getHighCount() + " High).");
        } else {
            bullets.add("No known CVE vulnerabilities detected across current dependency versions.");
        }
        bullets.add("Synthesized updates into " + batches.size() + " risk-optimized dependency update batches.");
        report.setExecutiveSummaryBullets(bullets);

        // Prioritized Roadmap
        List<String> roadmap = new ArrayList<>();
        int step = 1;
        if (summary.getCriticalCount() > 0 || summary.getHighCount() > 0) {
            roadmap.add(step++ + ". Remediate " + (summary.getCriticalCount() + summary.getHighCount()) + " Critical/High security hotfixes immediately using generated hotfix PRs.");
        }
        roadmap.add(step++ + ". Apply " + batches.stream().filter(b -> b.getBatchRiskLevel() == RiskLevel.LOW).count() + " low-risk automated patch batch(es) to fast-track bug fixes.");
        roadmap.add(step++ + ". Schedule testing for " + batches.stream().filter(b -> b.getBatchRiskLevel() == RiskLevel.MEDIUM).count() + " medium-risk minor feature batch(es) in staging.");
        roadmap.add(step++ + ". Plan dedicated migration sprints for " + batches.stream().filter(b -> b.getBatchRiskLevel() == RiskLevel.HIGH).count() + " high-risk major version upgrade(s).");
        report.setPrioritizedRemediationRoadmap(roadmap);

        // Critical items list
        List<DependencyDetailDto> criticalList = dependencies.stream()
                .filter(d -> (d.getRiskAssessment() != null && (d.getRiskAssessment().getRiskLevel() == RiskLevel.CRITICAL || d.getRiskAssessment().getRiskLevel() == RiskLevel.HIGH)) ||
                             "CRITICAL".equalsIgnoreCase(d.getSecurityPriority()) || "HIGH".equalsIgnoreCase(d.getSecurityPriority()))
                .collect(Collectors.toList());
        report.setCriticalItems(criticalList);

        return report;
    }
}
