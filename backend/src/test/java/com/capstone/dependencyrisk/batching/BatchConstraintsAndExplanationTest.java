package com.capstone.dependencyrisk.batching;

import com.capstone.dependencyrisk.dto.BatchDto;
import com.capstone.dependencyrisk.dto.DependencyDetailDto;
import com.capstone.dependencyrisk.dto.RiskAssessmentDto;
import com.capstone.dependencyrisk.dto.VulnerabilityDto;
import com.capstone.dependencyrisk.entity.BatchCategory;
import com.capstone.dependencyrisk.entity.EcosystemType;
import com.capstone.dependencyrisk.entity.RiskLevel;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import com.capstone.dependencyrisk.entity.VulnerabilitySeverity;
import com.capstone.dependencyrisk.risk.RiskWeightsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BatchConstraintsAndExplanationTest {

    private BatchOptimizationEngine engine;

    @BeforeEach
    public void setUp() {
        RiskWeightsConfig config = new RiskWeightsConfig();
        engine = new BatchOptimizationEngine(config);
    }

    @Test
    public void testUrgentSecurityQuarantine() {
        DependencyDetailDto dep = new DependencyDetailDto();
        dep.setId(1L);
        dep.setName("log4j-core");
        dep.setCoordinates("org.apache.logging.log4j:log4j-core");
        dep.setCurrentVersion("2.14.1");
        dep.setLatestVersion("2.17.1");
        dep.setVersionDiffType(VersionDiffType.MINOR);
        dep.setEcosystem(EcosystemType.MAVEN);
        dep.setSecurityPriority("CRITICAL");

        RiskAssessmentDto risk = new RiskAssessmentDto();
        risk.setRiskLevel(RiskLevel.CRITICAL);
        risk.setTotalScore(95.0);
        dep.setRiskAssessment(risk);

        VulnerabilityDto vuln = new VulnerabilityDto();
        vuln.setVulnId("GHSA-j2ge-4vdv-vq5u");
        vuln.setSeverity(VulnerabilitySeverity.CRITICAL);
        dep.setVulnerabilities(Collections.singletonList(vuln));

        List<BatchDto> batches = engine.generateOptimizedBatches(Collections.singletonList(dep));
        assertEquals(1, batches.size());
        BatchDto b = batches.get(0);
        assertEquals(BatchCategory.URGENT_SECURITY, b.getCategory());
        assertNotNull(b.getReasonForGrouping());
        assertTrue(b.getReasonForGrouping().contains("Quarantined"));
        assertFalse(b.getConstraints().isEmpty());
    }

    @Test
    public void testMajorVersionIsolatedInStandaloneBatch() {
        DependencyDetailDto dep = new DependencyDetailDto();
        dep.setId(2L);
        dep.setName("express");
        dep.setCoordinates("express");
        dep.setCurrentVersion("4.17.1");
        dep.setLatestVersion("5.0.0");
        dep.setVersionDiffType(VersionDiffType.MAJOR);
        dep.setEcosystem(EcosystemType.NPM);
        dep.setSecurityPriority("NONE");

        RiskAssessmentDto risk = new RiskAssessmentDto();
        risk.setRiskLevel(RiskLevel.HIGH);
        risk.setTotalScore(65.0);
        dep.setRiskAssessment(risk);

        List<BatchDto> batches = engine.generateOptimizedBatches(Collections.singletonList(dep));
        assertEquals(1, batches.size());
        BatchDto b = batches.get(0);
        assertEquals(BatchCategory.ISOLATED_MAJOR, b.getCategory());
        assertTrue(b.getReasonForGrouping().contains("Separated into a standalone batch"));
    }

    @Test
    public void testMaxBatchSizeConstraint() {
        List<DependencyDetailDto> deps = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            DependencyDetailDto d = new DependencyDetailDto();
            d.setId((long) (100 + i));
            d.setName("pkg-" + i);
            d.setCoordinates("pkg-" + i);
            d.setCurrentVersion("1.0." + i);
            d.setLatestVersion("1.0." + (i + 1));
            d.setVersionDiffType(VersionDiffType.PATCH);
            d.setEcosystem(EcosystemType.NPM);
            d.setDev(false);
            d.setSecurityPriority("NONE");

            RiskAssessmentDto risk = new RiskAssessmentDto();
            risk.setRiskLevel(RiskLevel.LOW);
            risk.setTotalScore(10.0);
            d.setRiskAssessment(risk);
            deps.add(d);
        }

        List<BatchDto> batches = engine.generateOptimizedBatches(deps);
        // 25 packages split into chunks of max 10: 10 + 10 + 5 = 3 batches
        assertEquals(3, batches.size());
        for (BatchDto b : batches) {
            assertTrue(b.getDependencies().size() <= BatchOptimizationEngine.MAX_BATCH_SIZE);
            assertEquals(BatchCategory.SAFE_PATCHES, b.getCategory());
        }
    }
}
