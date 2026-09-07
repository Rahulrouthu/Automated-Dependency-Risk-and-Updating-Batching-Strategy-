package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityPriorityVsUpdateRiskTest {

    private RiskEngine riskEngine;
    private RiskWeightsConfig config;

    @BeforeEach
    public void setUp() {
        config = new RiskWeightsConfig();
        riskEngine = new RiskEngine(config);
    }

    @Test
    public void testPatchSecurityUpdateHasLowUpdateRiskButCriticalSecurityPriority() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("axios");
        dep.setCurrentVersion("0.21.1");
        dep.setLatestVersion("0.21.2"); // Patch version update
        dep.setVersionDiffType(VersionDiffType.PATCH);
        dep.setDirect(true);
        dep.setDev(false);

        VulnerabilityEntity vuln = new VulnerabilityEntity();
        vuln.setVulnId("GHSA-cph5-m8f7-6c5x");
        vuln.setSeverity(VulnerabilitySeverity.CRITICAL);
        vuln.setCvssScore(9.8);
        vuln.setFixedVersion("0.21.2");

        RiskAssessmentEntity assessment = riskEngine.calculateRisk(dep, Collections.singletonList(vuln), false);
        assertNotNull(assessment);
        assertEquals("CRITICAL", dep.getSecurityPriority());
        assertEquals(RiskLevel.CRITICAL, assessment.getRiskLevel());
        assertNotNull(assessment.getRecommendationText());
        assertTrue(assessment.getRecommendationText().contains("URGENT: Remediate immediately"));
    }

    @Test
    public void testSecurityDataUnavailableRecordedCleanly() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("lodash");
        dep.setCurrentVersion("4.17.21");
        dep.setLatestVersion("4.17.21");
        dep.setVersionDiffType(VersionDiffType.UP_TO_DATE);
        dep.setSecurityDataStatus("SECURITY_DATA_UNAVAILABLE");

        RiskAssessmentEntity assessment = riskEngine.calculateRisk(dep, Collections.emptyList(), false);
        assertNotNull(assessment);
        assertEquals("UNAVAILABLE", dep.getSecurityPriority());
    }
}
