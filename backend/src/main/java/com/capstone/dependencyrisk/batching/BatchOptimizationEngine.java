package com.capstone.dependencyrisk.batching;

import com.capstone.dependencyrisk.dto.BatchDto;
import com.capstone.dependencyrisk.dto.DependencyDetailDto;
import com.capstone.dependencyrisk.entity.BatchCategory;
import com.capstone.dependencyrisk.entity.EcosystemType;
import com.capstone.dependencyrisk.entity.RiskLevel;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import com.capstone.dependencyrisk.risk.RiskWeightsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Risk-Aware Constraint-Based Greedy Dependency Batching Strategy
 *
 * Implements a constraint-satisfaction greedy clustering algorithm that groups compatible
 * dependency updates while quarantining high-risk major changes and critical vulnerabilities.
 *
 * Constraints Enforced:
 * 1. Ecosystem Homogeneity: Dependencies in a batch must belong to the exact same ecosystem.
 * 2. Security Quarantine: Vulnerable packages with Critical/High advisories are isolated into atomic hotfixes.
 * 3. Major Version Isolation: Major version bumps with potential breaking changes are kept in isolated PRs.
 * 4. Maximum Batch Size: Non-breaking updates are clustered into chunks of at most MAX_BATCH_SIZE (10).
 * 5. Combined Risk Threshold: Clustered batches must not exceed the configured maximum risk threshold.
 * 6. Scope Separation: Development/test tooling updates are grouped separately from production runtime dependencies.
 */
@Component
public class BatchOptimizationEngine {

    public static final String ALGORITHM_NAME = "Risk-Aware Constraint-Based Greedy Dependency Batching Strategy";
    public static final int MAX_BATCH_SIZE = 10;

    private final RiskWeightsConfig riskConfig;

    @Autowired
    public BatchOptimizationEngine(RiskWeightsConfig riskConfig) {
        this.riskConfig = riskConfig;
    }

    public List<BatchDto> generateOptimizedBatches(List<DependencyDetailDto> dependencies) {
        List<BatchDto> batches = new ArrayList<>();
        if (dependencies == null || dependencies.isEmpty()) {
            return batches;
        }

        // Filter only outdated or vulnerable dependencies needing update
        List<DependencyDetailDto> candidates = dependencies.stream()
                .filter(d -> d.getVersionDiffType() != VersionDiffType.UP_TO_DATE && d.getLatestVersion() != null)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return batches;
        }

        int batchCounter = 1;
        RiskWeightsConfig.Thresholds t = riskConfig.getThresholds();

        // ---------------------------------------------------------------------
        // Constraint 1 & 2: Urgent Security Hotfixes (Quarantined 1 per vulnerable package)
        // ---------------------------------------------------------------------
        List<DependencyDetailDto> urgentSecurity = candidates.stream()
                .filter(d -> "CRITICAL".equalsIgnoreCase(d.getSecurityPriority()) ||
                             "HIGH".equalsIgnoreCase(d.getSecurityPriority()) ||
                             (d.getVulnerabilities() != null && !d.getVulnerabilities().isEmpty() &&
                              d.getVulnerabilities().stream().anyMatch(v -> v.getSeverity().name().contains("HIGH") || v.getSeverity().name().contains("CRIT"))))
                .collect(Collectors.toList());

        for (DependencyDetailDto secDep : urgentSecurity) {
            BatchDto batch = new BatchDto();
            batch.setBatchNumber(batchCounter++);
            batch.setTitle("🚨 Urgent Security Hotfix: Upgrade " + secDep.getName());
            batch.setCategory(BatchCategory.URGENT_SECURITY);
            batch.setBatchRiskLevel(secDep.getRiskAssessment() != null ? secDep.getRiskAssessment().getRiskLevel() : RiskLevel.CRITICAL);
            batch.setBatchRiskScore(secDep.getRiskAssessment() != null ? secDep.getRiskAssessment().getTotalScore() : 90.0);
            batch.setDependencies(Collections.singletonList(secDep));
            batch.setStrategyDescription("Isolate high-urgency security patch to resolve known CVE vulnerability immediately without coupling with other package changes.");
            batch.setReasonForGrouping("Quarantined into an immediate standalone hotfix batch to remediate critical CVE vulnerability (" +
                    (secDep.getVulnerabilities().isEmpty() ? "Advisory" : secDep.getVulnerabilities().get(0).getVulnId()) + ") without bundling unrelated changes.");
            batch.setConstraints(Arrays.asList("Security Quarantine Enforced", "Single Package Hotfix", "Zero Unrelated Changes"));
            batch.setAlgorithmName(ALGORITHM_NAME);
            batch.setExecutionCommands(generateCommand(Collections.singletonList(secDep), secDep.getEcosystem()));
            batch.setPrTitle("fix(security): update " + secDep.getName() + " to " + secDep.getLatestVersion());
            batch.setPrBody(generatePrBody(batch));
            batches.add(batch);
        }

        // Remaining dependencies for constraint-based greedy clustering
        Set<Long> processedIds = urgentSecurity.stream().map(DependencyDetailDto::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> processedCoords = urgentSecurity.stream().map(DependencyDetailDto::getCoordinates).collect(Collectors.toSet());

        List<DependencyDetailDto> remaining = candidates.stream()
                .filter(d -> (d.getId() == null || !processedIds.contains(d.getId())) && !processedCoords.contains(d.getCoordinates()))
                .collect(Collectors.toList());

        // ---------------------------------------------------------------------
        // Constraint 3: Group by Ecosystem
        // ---------------------------------------------------------------------
        Map<EcosystemType, List<DependencyDetailDto>> byEcosystem = remaining.stream()
                .collect(Collectors.groupingBy(DependencyDetailDto::getEcosystem));

        for (Map.Entry<EcosystemType, List<DependencyDetailDto>> entry : byEcosystem.entrySet()) {
            EcosystemType ecosystem = entry.getKey();
            List<DependencyDetailDto> ecoDeps = entry.getValue();

            // -----------------------------------------------------------------
            // Constraint 4: Isolate Major Version Upgrades (1 PR per major bump)
            // -----------------------------------------------------------------
            List<DependencyDetailDto> majorUpdates = ecoDeps.stream()
                    .filter(d -> d.getVersionDiffType() == VersionDiffType.MAJOR ||
                                 (d.getRiskAssessment() != null && d.getRiskAssessment().getRiskLevel() == RiskLevel.HIGH))
                    .collect(Collectors.toList());

            for (DependencyDetailDto highDep : majorUpdates) {
                BatchDto highBatch = new BatchDto();
                highBatch.setBatchNumber(batchCounter++);
                highBatch.setTitle("⚠️ Major Version Upgrade: " + highDep.getName() + " (" + highDep.getCurrentVersion() + " -> " + highDep.getLatestVersion() + ")");
                highBatch.setCategory(BatchCategory.ISOLATED_MAJOR);
                highBatch.setBatchRiskLevel(highDep.getRiskAssessment() != null ? highDep.getRiskAssessment().getRiskLevel() : RiskLevel.HIGH);
                highBatch.setBatchRiskScore(highDep.getRiskAssessment() != null ? highDep.getRiskAssessment().getTotalScore() : 60.0);
                highBatch.setDependencies(Collections.singletonList(highDep));
                highBatch.setStrategyDescription("Isolated high-risk major version update. Contains potential breaking changes and API deprecations. Requires dedicated migration audit and regression tests.");
                highBatch.setReasonForGrouping("Separated into a standalone batch because this dependency requires a major version update (" +
                        highDep.getCurrentVersion() + " -> " + highDep.getLatestVersion() + ") with high compatibility impact and potential breaking API changes.");
                highBatch.setConstraints(Arrays.asList("Major Version Isolation", "Dedicated Migration Scope", "Regression Audit Required"));
                highBatch.setAlgorithmName(ALGORITHM_NAME);
                highBatch.setExecutionCommands(generateCommand(Collections.singletonList(highDep), ecosystem));
                highBatch.setPrTitle("refactor(deps): upgrade " + highDep.getName() + " from " + highDep.getCurrentVersion() + " to " + highDep.getLatestVersion());
                highBatch.setPrBody(generatePrBody(highBatch));
                batches.add(highBatch);
            }

            Set<String> majorCoords = majorUpdates.stream().map(DependencyDetailDto::getCoordinates).collect(Collectors.toSet());
            List<DependencyDetailDto> nonMajorDeps = ecoDeps.stream()
                    .filter(d -> !majorCoords.contains(d.getCoordinates()))
                    .collect(Collectors.toList());

            // -----------------------------------------------------------------
            // Constraint 5 & 6: Production vs Dev Tooling separation for Low & Medium Risk
            // -----------------------------------------------------------------
            List<DependencyDetailDto> prodLowRisk = nonMajorDeps.stream()
                    .filter(d -> !d.isDev() && (d.getRiskAssessment() == null || d.getRiskAssessment().getTotalScore() <= t.getLowMax()))
                    .collect(Collectors.toList());

            List<DependencyDetailDto> devLowRisk = nonMajorDeps.stream()
                    .filter(d -> d.isDev() && (d.getRiskAssessment() == null || d.getRiskAssessment().getTotalScore() <= t.getLowMax()))
                    .collect(Collectors.toList());

            // Chunk prod safe patches into batches of at most MAX_BATCH_SIZE
            List<List<DependencyDetailDto>> prodChunks = chunkList(prodLowRisk, MAX_BATCH_SIZE);
            for (int i = 0; i < prodChunks.size(); i++) {
                List<DependencyDetailDto> chunk = prodChunks.get(i);
                BatchDto patchBatch = new BatchDto();
                patchBatch.setBatchNumber(batchCounter++);
                String partSuffix = prodChunks.size() > 1 ? " (Part " + (i + 1) + ")" : "";
                patchBatch.setTitle("📦 Safe Automated Updates (" + ecosystem.getDisplayName() + " - " + chunk.size() + " packages)" + partSuffix);
                patchBatch.setCategory(BatchCategory.SAFE_PATCHES);
                patchBatch.setBatchRiskLevel(RiskLevel.LOW);
                double avgScore = chunk.stream().mapToDouble(d -> d.getRiskAssessment() != null ? d.getRiskAssessment().getTotalScore() : 10.0).average().orElse(10.0);
                patchBatch.setBatchRiskScore(Math.round(avgScore * 10.0) / 10.0);
                patchBatch.setDependencies(chunk);
                patchBatch.setStrategyDescription("Atomic batch combining backward-compatible low-risk patch and bugfix updates. Safe for automated CI validation and fast-track merging.");
                patchBatch.setReasonForGrouping("Grouped because all " + chunk.size() + " dependencies have low update risk, no detected compatibility conflicts, and the combined batch risk (" +
                        patchBatch.getBatchRiskScore() + ") is below the configured threshold (" + t.getLowMax() + ").");
                patchBatch.setConstraints(Arrays.asList("Same Ecosystem: " + ecosystem.name(), "Max Batch Size <= " + MAX_BATCH_SIZE, "Combined Risk <= " + t.getLowMax(), "Non-Breaking Patch Updates"));
                patchBatch.setAlgorithmName(ALGORITHM_NAME);
                patchBatch.setExecutionCommands(generateCommand(chunk, ecosystem));
                patchBatch.setPrTitle("chore(deps): batch update " + chunk.size() + " safe dependencies (" + ecosystem.name().toLowerCase() + ")" + partSuffix);
                patchBatch.setPrBody(generatePrBody(patchBatch));
                batches.add(patchBatch);
            }

            // Chunk dev tooling safe patches
            List<List<DependencyDetailDto>> devChunks = chunkList(devLowRisk, MAX_BATCH_SIZE);
            for (int i = 0; i < devChunks.size(); i++) {
                List<DependencyDetailDto> chunk = devChunks.get(i);
                BatchDto devBatch = new BatchDto();
                devBatch.setBatchNumber(batchCounter++);
                String partSuffix = devChunks.size() > 1 ? " (Part " + (i + 1) + ")" : "";
                devBatch.setTitle("🛠️ Development & Tooling Updates (" + ecosystem.getDisplayName() + " - " + chunk.size() + " packages)" + partSuffix);
                devBatch.setCategory(BatchCategory.SAFE_PATCHES);
                devBatch.setBatchRiskLevel(RiskLevel.LOW);
                double avgScore = chunk.stream().mapToDouble(d -> d.getRiskAssessment() != null ? d.getRiskAssessment().getTotalScore() : 10.0).average().orElse(10.0);
                devBatch.setBatchRiskScore(Math.round(avgScore * 10.0) / 10.0);
                devBatch.setDependencies(chunk);
                devBatch.setStrategyDescription("Grouped development and test scope tooling dependencies separately from production runtime code.");
                devBatch.setReasonForGrouping("Grouped development and test scope tooling dependencies separately from production runtime code to minimize deployment blast radius.");
                devBatch.setConstraints(Arrays.asList("Dev Scope Tooling", "Same Ecosystem: " + ecosystem.name(), "Max Batch Size <= " + MAX_BATCH_SIZE, "Non-Breaking Updates"));
                devBatch.setAlgorithmName(ALGORITHM_NAME);
                devBatch.setExecutionCommands(generateCommand(chunk, ecosystem));
                devBatch.setPrTitle("chore(devDependencies): update " + chunk.size() + " dev dependencies (" + ecosystem.name().toLowerCase() + ")" + partSuffix);
                devBatch.setPrBody(generatePrBody(devBatch));
                batches.add(devBatch);
            }

            // -----------------------------------------------------------------
            // Staged Minor Feature Updates (Score between lowMax and highMax)
            // -----------------------------------------------------------------
            Set<String> processedNonMajor = new HashSet<>();
            prodLowRisk.forEach(d -> processedNonMajor.add(d.getCoordinates()));
            devLowRisk.forEach(d -> processedNonMajor.add(d.getCoordinates()));

            List<DependencyDetailDto> mediumRiskDeps = nonMajorDeps.stream()
                    .filter(d -> !processedNonMajor.contains(d.getCoordinates()))
                    .collect(Collectors.toList());

            List<List<DependencyDetailDto>> medChunks = chunkList(mediumRiskDeps, MAX_BATCH_SIZE);
            for (int i = 0; i < medChunks.size(); i++) {
                List<DependencyDetailDto> chunk = medChunks.get(i);
                BatchDto medBatch = new BatchDto();
                medBatch.setBatchNumber(batchCounter++);
                String partSuffix = medChunks.size() > 1 ? " (Part " + (i + 1) + ")" : "";
                medBatch.setTitle("⚡ Feature & Minor Updates (" + ecosystem.getDisplayName() + " - " + chunk.size() + " packages)" + partSuffix);
                medBatch.setCategory(BatchCategory.MINOR_UPDATES);
                medBatch.setBatchRiskLevel(RiskLevel.MEDIUM);
                double avgScore = chunk.stream().mapToDouble(d -> d.getRiskAssessment() != null ? d.getRiskAssessment().getTotalScore() : 35.0).average().orElse(35.0);
                medBatch.setBatchRiskScore(Math.round(avgScore * 10.0) / 10.0);
                medBatch.setDependencies(chunk);
                medBatch.setStrategyDescription("Grouped minor feature version bumps. Recommend executing integration test suites and staging environment validation prior to production deploy.");
                medBatch.setReasonForGrouping("Grouped minor feature bumps with medium update risk (" + medBatch.getBatchRiskScore() +
                        "). Non-breaking but requires staging integration verification.");
                medBatch.setConstraints(Arrays.asList("Same Ecosystem: " + ecosystem.name(), "Minor Updates Only", "Staging Validation Required"));
                medBatch.setAlgorithmName(ALGORITHM_NAME);
                medBatch.setExecutionCommands(generateCommand(chunk, ecosystem));
                medBatch.setPrTitle("feat(deps): upgrade " + chunk.size() + " minor dependencies (" + ecosystem.name().toLowerCase() + ")" + partSuffix);
                medBatch.setPrBody(generatePrBody(medBatch));
                batches.add(medBatch);
            }
        }

        return batches;
    }

    private <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        if (list == null || list.isEmpty()) return chunks;
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return chunks;
    }

    private String generateCommand(List<DependencyDetailDto> deps, EcosystemType eco) {
        if (deps == null || deps.isEmpty()) return "# No commands needed";

        StringBuilder sb = new StringBuilder();
        if (eco == EcosystemType.NPM) {
            sb.append("npm install ");
            for (DependencyDetailDto d : deps) {
                String pkg = (d.getGroupOrNamespace() != null && !d.getGroupOrNamespace().isEmpty()) ?
                        d.getGroupOrNamespace() + "/" + d.getName() : d.getName();
                sb.append(pkg).append("@").append(d.getLatestVersion()).append(" ");
            }
            sb.append("\nnpm test");
        } else if (eco == EcosystemType.PYTHON) {
            sb.append("pip install --upgrade ");
            for (DependencyDetailDto d : deps) {
                sb.append(d.getName()).append("==").append(d.getLatestVersion()).append(" ");
            }
            sb.append("\npytest");
        } else if (eco == EcosystemType.MAVEN) {
            sb.append("# Maven Update Commands\n");
            for (DependencyDetailDto d : deps) {
                String g = d.getGroupOrNamespace() != null ? d.getGroupOrNamespace() : "org.apache";
                sb.append("mvn versions:use-dep-version -Dincludes=").append(g).append(":").append(d.getName())
                  .append(" -DdepVersion=").append(d.getLatestVersion()).append(" -DgenerateBackupPoms=false\n");
            }
            sb.append("mvn clean test");
        } else if (eco == EcosystemType.GRADLE) {
            sb.append("# Gradle Update Steps:\n");
            for (DependencyDetailDto d : deps) {
                String g = d.getGroupOrNamespace() != null ? d.getGroupOrNamespace() : "";
                sb.append("# Update ").append(g).append(":").append(d.getName()).append(" to version ").append(d.getLatestVersion()).append("\n");
            }
            sb.append("./gradlew test");
        }

        return sb.toString();
    }

    private String generatePrBody(BatchDto batch) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Automated Dependency Update - ").append(batch.getTitle()).append("\n\n");
        sb.append("### Summary\n");
        sb.append("- **Batch Risk Score:** ").append(batch.getBatchRiskScore()).append("/100 (").append(batch.getBatchRiskLevel().getLabel()).append(")\n");
        sb.append("- **Category:** ").append(batch.getCategory().getTitle()).append("\n");
        sb.append("- **Algorithm:** ").append(batch.getAlgorithmName()).append("\n");
        if (batch.getReasonForGrouping() != null) {
            sb.append("- **Grouping Reason:** ").append(batch.getReasonForGrouping()).append("\n");
        }
        sb.append("- **Strategy:** ").append(batch.getStrategyDescription()).append("\n\n");

        sb.append("### Dependencies Included (").append(batch.getDependencies().size()).append(")\n\n");
        sb.append("| Package | Current | Resolved | Target | Type | Security | Update Risk | Action |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |\n");
        for (DependencyDetailDto d : batch.getDependencies()) {
            String resolved = d.getResolvedVersion() != null ? d.getResolvedVersion() : d.getCurrentVersion();
            String riskLabel = d.getRiskAssessment() != null ? d.getRiskAssessment().getRiskLevel().getLabel() + " (" + d.getRiskAssessment().getTotalScore() + ")" : "N/A";
            sb.append("| `").append(d.getCoordinates()).append("` | ")
              .append(d.getCurrentVersion()).append(" | ")
              .append(resolved).append(" | ")
              .append(d.getLatestVersion()).append(" | ")
              .append(d.getVersionDiffType() != null ? d.getVersionDiffType().name() : "UNKNOWN").append(" | ")
              .append(d.getSecurityPriority()).append(" | ")
              .append(riskLabel).append(" | ")
              .append(d.getRecommendedAction()).append(" |\n");
        }

        sb.append("\n### Verification Commands\n```bash\n");
        sb.append(batch.getExecutionCommands()).append("\n```\n");

        sb.append("\n### Rollback Plan\n");
        sb.append("If regression tests fail, revert this Pull Request branch or restore previous dependency lockfile.\n");

        return sb.toString();
    }
}
