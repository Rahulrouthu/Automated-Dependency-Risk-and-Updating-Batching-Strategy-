package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class RiskEngineTest {

    private RiskEngine riskEngine;

    @BeforeEach
    public void setUp() {
        RiskWeightsConfig config = new RiskWeightsConfig();
        riskEngine = new RiskEngine(config);
    }

    @Test
    public void testLowRiskPatch() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("lodash");
        dep.setCurrentVersion("4.17.20");
        dep.setLatestVersion("4.17.21");
        dep.setVersionDiffType(VersionDiffType.PATCH);
        dep.setDirect(true);
        dep.setDev(false);

        RiskAssessmentEntity assessment = riskEngine.calculateRisk(dep, Collections.emptyList(), false);
        assertNotNull(assessment);
        assertEquals(RiskLevel.LOW, assessment.getRiskLevel());
        assertTrue(assessment.getTotalScore() < 25.0);
    }

    @Test
    public void testCriticalRiskVulnerability() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("log4j-core");
        dep.setCurrentVersion("2.14.1");
        dep.setLatestVersion("2.17.1");
        dep.setVersionDiffType(VersionDiffType.MINOR);
        dep.setDirect(true);
        dep.setDev(false);

        VulnerabilityEntity vuln = new VulnerabilityEntity();
        vuln.setVulnId("GHSA-j2ge-4vdv-vq5u");
        vuln.setSeverity(VulnerabilitySeverity.CRITICAL);
        vuln.setCvssScore(10.0);

        RiskAssessmentEntity assessment = riskEngine.calculateRisk(dep, Collections.singletonList(vuln), false);
        assertNotNull(assessment);
        assertEquals(RiskLevel.CRITICAL, assessment.getRiskLevel());
        assertTrue(assessment.getTotalScore() >= 75.0);
    }
}
