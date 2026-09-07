package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompatibilityService {

    public static class CompatibilityResult {
        public boolean hasConflict;
        public String conflictDescription;
        public List<String> incompatibleDependencies = new ArrayList<>();

        public CompatibilityResult(boolean hasConflict, String conflictDescription) {
            this.hasConflict = hasConflict;
            this.conflictDescription = conflictDescription;
        }
    }

    public CompatibilityResult evaluateCompatibility(DependencyEntity target, List<DependencyEntity> allDeps) {
        if (target == null) return new CompatibilityResult(false, null);

        // Check if multiple versions of the same dependency exist in project
        for (DependencyEntity other : allDeps) {
            if (other != target && other.getName().equalsIgnoreCase(target.getName())) {
                if (!other.getCurrentVersion().equalsIgnoreCase(target.getCurrentVersion())) {
                    return new CompatibilityResult(true, "Version conflict: Multiple distinct versions (" +
                            target.getCurrentVersion() + " vs " + other.getCurrentVersion() + ") requested across manifests.");
                }
            }
        }

        // Major framework breaking changes
        if (target.getVersionDiffType() == VersionDiffType.MAJOR) {
            String name = target.getName().toLowerCase();
            if (name.contains("spring-boot") || name.contains("spring-core")) {
                return new CompatibilityResult(false, "Major Spring Framework upgrade (Jakarta EE namespace shift, Java 17+ requirement).");
            }
            if (name.contains("react") || name.contains("angular") || name.contains("vue")) {
                return new CompatibilityResult(false, "Major UI Framework upgrade with potential breaking API lifecycle hooks.");
            }
        }

        return new CompatibilityResult(false, null);
    }
}
