package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisConfidenceEngine {

    public static class ConfidenceResult {
        private String level; // HIGH, MEDIUM, LOW
        private double score; // 0 - 100
        private Map<String, String> signals = new HashMap<>();

        public ConfidenceResult(String level, double score, Map<String, String> signals) {
            this.level = level;
            this.score = score;
            this.signals = signals;
        }

        public String getLevel() { return level; }
        public double getScore() { return score; }
        public Map<String, String> getSignals() { return signals; }
    }

    public ConfidenceResult calculateConfidence(
            boolean repoAccessSuccess,
            boolean manifestFound,
            boolean lockfileFound,
            boolean registryConnected,
            boolean securityConnected,
            List<DependencyEntity> dependencies) {

        double score = 0.0;
        Map<String, String> signals = new HashMap<>();

        // Signal 1: Repository Access (20 pts)
        if (repoAccessSuccess) {
            score += 20.0;
            signals.put("repositoryAccess", "CONNECTED");
        } else {
            signals.put("repositoryAccess", "FAILED");
        }

        // Signal 2: Manifest Parsing (25 pts)
        if (manifestFound && dependencies != null && !dependencies.isEmpty()) {
            score += 25.0;
            signals.put("manifestParsing", "SUCCESS");
        } else {
            signals.put("manifestParsing", "MISSING_OR_EMPTY");
        }

        // Signal 3: Lockfile Presence / Exact Pinning (20 pts)
        if (lockfileFound) {
            score += 20.0;
            signals.put("lockfileStatus", "RESOLVED_LOCKFILE");
        } else {
            // Check if ecosystem uses manifest-as-lockfile (Maven/Python requirements pin)
            boolean isManifestPinned = dependencies != null && dependencies.stream()
                    .anyMatch(d -> !"UNKNOWN".equalsIgnoreCase(d.getResolvedVersion()) && d.getResolvedVersion() != null);
            if (isManifestPinned) {
                score += 15.0;
                signals.put("lockfileStatus", "MANIFEST_PINNED");
            } else {
                score += 5.0;
                signals.put("lockfileStatus", "ESTIMATED_RANGES");
            }
        }

        // Signal 4: Package Registry Data (20 pts)
        if (registryConnected) {
            score += 20.0;
            signals.put("registryStatus", "CONNECTED");
        } else {
            signals.put("registryStatus", "UNAVAILABLE");
        }

        // Signal 5: Live OSV Security Advisory Data (15 pts)
        if (securityConnected) {
            score += 15.0;
            signals.put("securityDatabase", "CONNECTED");
        } else {
            signals.put("securityDatabase", "UNAVAILABLE");
        }

        String level;
        if (score >= 75.0) {
            level = "HIGH";
        } else if (score >= 45.0) {
            level = "MEDIUM";
        } else {
            level = "LOW";
        }

        return new ConfidenceResult(level, Math.round(score * 10.0) / 10.0, signals);
    }
}
