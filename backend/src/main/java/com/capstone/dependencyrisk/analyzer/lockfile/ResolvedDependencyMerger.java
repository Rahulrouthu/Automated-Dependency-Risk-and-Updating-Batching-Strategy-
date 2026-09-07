package com.capstone.dependencyrisk.analyzer.lockfile;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResolvedDependencyMerger {

    private final List<LockfileParser> lockfileParsers;

    @Autowired
    public ResolvedDependencyMerger(List<LockfileParser> lockfileParsers) {
        this.lockfileParsers = lockfileParsers;
    }

    /**
     * Merges lockfile data into extracted declared dependencies.
     *
     * @param dependencies Extracted dependencies from manifest (e.g. package.json)
     * @param lockfileEntries Map of lockfile path -> lockfile raw content
     */
    public void mergeResolvedVersions(List<DependencyEntity> dependencies, Map<String, String> lockfileEntries) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }

        // Priority resolution: package-lock.json > npm-shrinkwrap.json > pnpm-lock.yaml > yarn.lock
        String activeLockfileType = null;
        Map<String, String> resolvedMap = null;

        for (String priorityFile : new String[]{"package-lock.json", "npm-shrinkwrap.json", "pnpm-lock.yaml", "yarn.lock"}) {
            for (Map.Entry<String, String> entry : lockfileEntries.entrySet()) {
                String path = entry.getKey();
                if (path.toLowerCase().endsWith(priorityFile)) {
                    for (LockfileParser parser : lockfileParsers) {
                        if (parser.canHandle(path)) {
                            Map<String, String> res = parser.parseResolvedVersions(entry.getValue());
                            if (!res.isEmpty()) {
                                resolvedMap = res;
                                activeLockfileType = parser.getLockfileType();
                                break;
                            }
                        }
                    }
                }
                if (resolvedMap != null) break;
            }
            if (resolvedMap != null) break;
        }

        for (DependencyEntity dep : dependencies) {
            if (dep.getEcosystem() == EcosystemType.NPM) {
                String key = dep.getCoordinates(); // e.g. "axios" or "@angular/core"
                String nameOnly = dep.getName();

                if (resolvedMap != null) {
                    String resolved = resolvedMap.get(key);
                    if (resolved == null) {
                        resolved = resolvedMap.get(nameOnly);
                    }

                    if (resolved != null && !resolved.trim().isEmpty()) {
                        dep.setResolvedVersion(resolved.trim());
                        // Use actual resolved version for vulnerability & drift queries
                        dep.setCurrentVersion(resolved.trim());
                        dep.setLockfileSource(activeLockfileType);
                    } else {
                        dep.setResolvedVersion("UNKNOWN");
                        dep.setLockfileSource("NOT_FOUND_IN_LOCKFILE");
                    }
                } else {
                    // No lockfile was provided in repository
                    dep.setResolvedVersion("UNKNOWN");
                    dep.setLockfileSource("NONE");
                }
            } else {
                // For Maven / Gradle / Python, the manifest declaration is the resolved pin
                if (dep.getResolvedVersion() == null || dep.getResolvedVersion().isEmpty()) {
                    dep.setResolvedVersion(dep.getCurrentVersion());
                    dep.setLockfileSource("MANIFEST");
                }
            }
        }
    }
}
