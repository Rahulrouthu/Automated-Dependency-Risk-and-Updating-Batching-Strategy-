package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class RiskConfigThresholdsAndSnapshotTest {

    private RiskEngine riskEngine;
    private RiskWeightsConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        config = new RiskWeightsConfig();
        riskEngine = new RiskEngine(config);
    }

    @Test
    public void testDynamicThresholdClassification() {
        RiskWeightsConfig.Thresholds t = config.getThresholds();
        t.setLowMax(20.0);
        t.setMediumMax(40.0);
        t.setHighMax(70.0);
        t.setCriticalMin(71.0);

        assertEquals(RiskLevel.LOW, riskEngine.classifyScoreWithThresholds(15.0, t));
        assertEquals(RiskLevel.MEDIUM, riskEngine.classifyScoreWithThresholds(25.0, t));
        assertEquals(RiskLevel.HIGH, riskEngine.classifyScoreWithThresholds(55.0, t));
        assertEquals(RiskLevel.CRITICAL, riskEngine.classifyScoreWithThresholds(80.0, t));
    }

    @Test
    public void testRiskConfigurationSnapshotSerialization() throws Exception {
        config.getWeights().setVersionChange(30.0);
        config.getThresholds().setLowMax(20.0);

        String json = objectMapper.writeValueAsString(config);
        assertNotNull(json);
        assertTrue(json.contains("\"versionChange\":30.0") || json.contains("\"versionChange\" : 30.0") || json.contains("30.0"));
        assertTrue(json.contains("20.0"));

        RiskWeightsConfig deserialized = objectMapper.readValue(json, RiskWeightsConfig.class);
        assertEquals(30.0, deserialized.getWeights().getVersionChange());
        assertEquals(20.0, deserialized.getThresholds().getLowMax());
    }

    @Test
    public void testFactorContributionsCalculated() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("express");
        dep.setCurrentVersion("4.17.1");
        dep.setLatestVersion("5.0.0");
        dep.setVersionDiffType(VersionDiffType.MAJOR);
        dep.setDirect(true);
        dep.setDev(false);

        RiskAssessmentEntity assessment = riskEngine.calculateRisk(dep, Collections.emptyList(), false);
        assertNotNull(assessment);
        assertTrue(assessment.getTotalScore() > 0);
        assertTrue(assessment.getVersionRisk() > 0);
        assertTrue(assessment.getDependencyImpactRisk() > 0);
    }
}
