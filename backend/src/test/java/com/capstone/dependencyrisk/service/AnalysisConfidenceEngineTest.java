package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisConfidenceEngineTest {

    private AnalysisConfidenceEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new AnalysisConfidenceEngine();
    }

    @Test
    public void testHighConfidenceWithAllSignals() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("express");
        dep.setResolvedVersion("4.18.2");

        AnalysisConfidenceEngine.ConfidenceResult res = engine.calculateConfidence(
                true, true, true, true, true, Collections.singletonList(dep)
        );

        assertNotNull(res);
        assertEquals("HIGH", res.getLevel());
        assertEquals(100.0, res.getScore());
        assertEquals("CONNECTED", res.getSignals().get("repositoryAccess"));
        assertEquals("SUCCESS", res.getSignals().get("manifestParsing"));
        assertEquals("RESOLVED_LOCKFILE", res.getSignals().get("lockfileStatus"));
        assertEquals("CONNECTED", res.getSignals().get("registryStatus"));
        assertEquals("CONNECTED", res.getSignals().get("securityDatabase"));
    }

    @Test
    public void testMediumConfidenceWhenLockfileMissing() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("express");
        dep.setResolvedVersion("UNKNOWN");

        AnalysisConfidenceEngine.ConfidenceResult res = engine.calculateConfidence(
                true, true, false, true, true, Collections.singletonList(dep)
        );

        assertNotNull(res);
        assertTrue(res.getScore() < 100.0);
        assertEquals("ESTIMATED_RANGES", res.getSignals().get("lockfileStatus"));
    }

    @Test
    public void testLowConfidenceWhenManifestMissing() {
        AnalysisConfidenceEngine.ConfidenceResult res = engine.calculateConfidence(
                true, false, false, false, false, Collections.emptyList()
        );

        assertNotNull(res);
        assertEquals("LOW", res.getLevel());
        assertTrue(res.getScore() < 45.0);
    }
}
