package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.analyzer.DependencyParser;
import com.capstone.dependencyrisk.analyzer.ParserFactory;
import com.capstone.dependencyrisk.analyzer.lockfile.ResolvedDependencyMerger;
import com.capstone.dependencyrisk.dto.*;
import com.capstone.dependencyrisk.entity.*;
import com.capstone.dependencyrisk.exception.ManifestNotFoundException;
import com.capstone.dependencyrisk.github.GitHubApiClient;
import com.capstone.dependencyrisk.repository.*;
import com.capstone.dependencyrisk.risk.RiskEngine;
import com.capstone.dependencyrisk.risk.RiskWeightsConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RepositoryScannerService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryScannerService.class);

    private final GitHubApiClient gitHubClient;
    private final ParserFactory parserFactory;
    private final ResolvedDependencyMerger resolvedDependencyMerger;
    private final VersionAnalysisService versionAnalysisService;
    private final VulnerabilityService vulnerabilityService;
    private final CompatibilityService compatibilityService;
    private final RiskEngine riskEngine;
    private final ResourceLinkService resourceLinkService;
    private final RecommendationService recommendationService;
    private final BatchingService batchingService;
    private final AnalysisConfidenceEngine confidenceEngine;
    private final RepositoryJpaRepository repositoryJpaRepository;
    private final DependencyFileJpaRepository fileRepository;
    private final DependencyJpaRepository dependencyRepository;
    private final RiskAssessmentJpaRepository riskAssessmentRepository;
    private final VulnerabilityJpaRepository vulnerabilityJpaRepository;
    private final AnalysisRunJpaRepository analysisRunRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RepositoryScannerService(
            GitHubApiClient gitHubClient,
            ParserFactory parserFactory,
            ResolvedDependencyMerger resolvedDependencyMerger,
            VersionAnalysisService versionAnalysisService,
            VulnerabilityService vulnerabilityService,
            CompatibilityService compatibilityService,
            RiskEngine riskEngine,
            ResourceLinkService resourceLinkService,
            RecommendationService recommendationService,
            BatchingService batchingService,
            AnalysisConfidenceEngine confidenceEngine,
            RepositoryJpaRepository repositoryJpaRepository,
            DependencyFileJpaRepository fileRepository,
            DependencyJpaRepository dependencyRepository,
            RiskAssessmentJpaRepository riskAssessmentRepository,
            VulnerabilityJpaRepository vulnerabilityJpaRepository,
            AnalysisRunJpaRepository analysisRunRepository) {
        this.gitHubClient = gitHubClient;
        this.parserFactory = parserFactory;
        this.resolvedDependencyMerger = resolvedDependencyMerger;
        this.versionAnalysisService = versionAnalysisService;
        this.vulnerabilityService = vulnerabilityService;
        this.compatibilityService = compatibilityService;
        this.riskEngine = riskEngine;
        this.resourceLinkService = resourceLinkService;
        this.recommendationService = recommendationService;
        this.batchingService = batchingService;
        this.confidenceEngine = confidenceEngine;
        this.repositoryJpaRepository = repositoryJpaRepository;
        this.fileRepository = fileRepository;
        this.dependencyRepository = dependencyRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.vulnerabilityJpaRepository = vulnerabilityJpaRepository;
        this.analysisRunRepository = analysisRunRepository;
    }

    @Transactional
    public AnalysisResultDto scanRepository(AnalyzeRequestDto request) {
        long startTime = System.currentTimeMillis();
        GitHubApiClient.RepoDetails repoDetails = gitHubClient.parseGitHubUrl(request.getRepositoryUrl());
        if (request.getBranch() != null && !request.getBranch().trim().isEmpty()) {
            repoDetails.branch = request.getBranch().trim();
        }

        boolean isDemo = request.isDemoMode() ||
                repoDetails.normalizedUrl.contains("vulnerable-microservice-demo") ||
                repoDetails.normalizedUrl.contains("devops-capstone");

        String dataSource = isDemo ? "DEMO" : "LIVE";

        // Save or update RepositoryEntity
        RepositoryEntity repoEntity = repositoryJpaRepository.findByOwnerAndName(repoDetails.owner, repoDetails.name)
                .orElseGet(() -> new RepositoryEntity(repoDetails.owner, repoDetails.name, repoDetails.normalizedUrl, repoDetails.branch));
        repoEntity.setDefaultBranch(repoDetails.branch);
        repoEntity.setLastScannedAt(LocalDateTime.now());
        repoEntity = repositoryJpaRepository.save(repoEntity);

        // Analysis Run record
        AnalysisRunEntity runEntity = new AnalysisRunEntity();
        runEntity.setRepository(repoEntity);
        runEntity.setStatus(AnalysisStatus.VALIDATING_REPOSITORY);
        runEntity.setDataSource(dataSource);
        runEntity.setStartedAt(LocalDateTime.now());

        // Snapshot current risk configuration for reproducibility
        try {
            runEntity.setRiskConfigSnapshotJson(objectMapper.writeValueAsString(riskEngine.getConfig()));
        } catch (Exception e) {
            runEntity.setRiskConfigSnapshotJson("{}");
        }
        runEntity = analysisRunRepository.save(runEntity);

        // Validate repository exists unless in demo mode
        if (!isDemo) {
            try {
                gitHubClient.validateRepositoryExistence(repoDetails, request.getGithubToken());
            } catch (Exception e) {
                runEntity.setStatus(AnalysisStatus.REPOSITORY_NOT_FOUND);
                runEntity.setErrorMessage(e.getMessage());
                analysisRunRepository.save(runEntity);
                throw e;
            }
        }

        runEntity.setStatus(AnalysisStatus.SCANNING_MANIFESTS);
        analysisRunRepository.save(runEntity);

        List<String> warnings = new ArrayList<>();
        List<String> detectedManifestFiles = new ArrayList<>();
        List<String> detectedLockfiles = new ArrayList<>();
        List<DependencyEntity> allExtractedDeps = new ArrayList<>();
        Map<String, String> lockfileContents = new HashMap<>();

        // 1. Discover Manifest & Lockfile Files
        List<GitHubApiClient.ManifestEntry> discoveredEntries = gitHubClient.discoverManifestFiles(repoDetails, request.getGithubToken());

        List<GitHubApiClient.ManifestEntry> manifestEntries = new ArrayList<>();
        List<GitHubApiClient.ManifestEntry> lockfileEntries = new ArrayList<>();

        for (GitHubApiClient.ManifestEntry entry : discoveredEntries) {
            if (entry.isLockfile) {
                lockfileEntries.add(entry);
                detectedLockfiles.add(entry.path);
            } else {
                manifestEntries.add(entry);
            }
        }

        // NO synthetic fake fallback for live analysis
        if (manifestEntries.isEmpty()) {
            if (isDemo) {
                // Curated benchmark manifest for explicit demo mode only
                manifestEntries = getCuratedDemoManifests(repoDetails);
            } else {
                runEntity.setStatus(AnalysisStatus.MANIFEST_NOT_FOUND);
                runEntity.setErrorMessage("No supported dependency manifest was found in this repository.");
                analysisRunRepository.save(runEntity);
                throw new ManifestNotFoundException("No supported dependency manifest was found in this repository.");
            }
        }

        // Clean previous files if reanalyzing
        List<DependencyFileEntity> existingFiles = fileRepository.findByRepositoryId(repoEntity.getId());
        if (!existingFiles.isEmpty()) {
            fileRepository.deleteAll(existingFiles);
        }

        // Fetch Lockfile contents
        for (GitHubApiClient.ManifestEntry lockEntry : lockfileEntries) {
            if (lockEntry.rawDownloadUrl != null) {
                String lockContent = gitHubClient.fetchFileContent(lockEntry.rawDownloadUrl, request.getGithubToken());
                if (lockContent != null && !lockContent.trim().isEmpty()) {
                    lockfileContents.put(lockEntry.path, lockContent);
                }
            }
        }

        Set<String> detectedEcosystemNames = new HashSet<>();

        // 2. Parse Manifests & Extract Dependencies
        runEntity.setStatus(AnalysisStatus.EXTRACTING_DEPENDENCIES);
        analysisRunRepository.save(runEntity);

        for (GitHubApiClient.ManifestEntry entry : manifestEntries) {
            Optional<DependencyParser> parserOpt = parserFactory.getParserForFile(entry.path);
            if (parserOpt.isEmpty()) continue;

            DependencyParser parser = parserOpt.get();
            detectedManifestFiles.add(entry.path);
            detectedEcosystemNames.add(parser.getSupportedEcosystem().getDisplayName());

            DependencyFileEntity fileEntity = new DependencyFileEntity(repoEntity, entry.path, parser.getSupportedEcosystem());
            fileEntity = fileRepository.save(fileEntity);

            String content = null;
            if (entry.rawDownloadUrl != null) {
                content = gitHubClient.fetchFileContent(entry.rawDownloadUrl, request.getGithubToken());
            }
            if ((content == null || content.trim().isEmpty()) && isDemo) {
                content = getCuratedDemoContent(entry.path, repoDetails);
            }

            if (content != null && !content.trim().isEmpty()) {
                List<DependencyEntity> parsed = parser.parse(content, entry.path, fileEntity);
                allExtractedDeps.addAll(parsed);
            }
        }

        if (allExtractedDeps.isEmpty()) {
            runEntity.setStatus(AnalysisStatus.MANIFEST_NOT_FOUND);
            runEntity.setErrorMessage("No supported dependency manifest was found in this repository.");
            analysisRunRepository.save(runEntity);
            throw new ManifestNotFoundException("No supported dependency manifest was found in this repository.");
        }

        // 3. Merge Resolved Lockfile Versions
        runEntity.setStatus(AnalysisStatus.RESOLVING_VERSIONS);
        analysisRunRepository.save(runEntity);

        resolvedDependencyMerger.mergeResolvedVersions(allExtractedDeps, lockfileContents);

        // 4. Version Analysis & Security Vulnerability Correlation
        runEntity.setStatus(AnalysisStatus.SECURITY_ANALYSIS);
        analysisRunRepository.save(runEntity);

        // Enrich versions concurrently across all dependencies
        allExtractedDeps.parallelStream().forEach(dep -> {
            dep.setDataSource(dataSource);
            versionAnalysisService.analyzeAndEnrichVersion(dep, isDemo);
            compatibilityService.evaluateCompatibility(dep, allExtractedDeps);
        });

        boolean anyRegistryConnected = allExtractedDeps.stream().anyMatch(d -> d.getLatestVersion() != null);

        // Save Dependency Entities
        List<DependencyEntity> savedDeps = dependencyRepository.saveAll(allExtractedDeps);

        // Scan Vulnerabilities concurrently
        runEntity.setStatus(AnalysisStatus.CALCULATING_RISK);
        analysisRunRepository.save(runEntity);

        java.util.concurrent.ConcurrentHashMap<Long, List<VulnerabilityEntity>> vulnsByDepId = new java.util.concurrent.ConcurrentHashMap<>();
        savedDeps.parallelStream().forEach(dep -> {
            List<VulnerabilityEntity> vList = vulnerabilityService.scanVulnerabilities(dep, isDemo);
            if (vList != null && !vList.isEmpty()) {
                vulnsByDepId.put(dep.getId(), vList);
            }
        });

        boolean anySecurityConnected = !vulnsByDepId.isEmpty();

        List<DependencyDetailDto> detailDtos = new ArrayList<>();
        List<VulnerabilityEntity> allSavedVulns = new ArrayList<>();
        int criticalCount = 0;
        int highCount = 0;
        int moderateCount = 0;
        int lowCount = 0;
        int outdatedCount = 0;

        for (DependencyEntity dep : savedDeps) {
            List<VulnerabilityEntity> vulns = vulnsByDepId.getOrDefault(dep.getId(), Collections.emptyList());

            for (VulnerabilityEntity v : vulns) {
                v.setDependency(dep);
                if (v.getSeverity() == VulnerabilitySeverity.CRITICAL) criticalCount++;
                else if (v.getSeverity() == VulnerabilitySeverity.HIGH) highCount++;
                else if (v.getSeverity() == VulnerabilitySeverity.MODERATE) moderateCount++;
                else if (v.getSeverity() == VulnerabilitySeverity.LOW) lowCount++;
            }
            if (!vulns.isEmpty()) {
                allSavedVulns.addAll(vulnerabilityJpaRepository.saveAll(vulns));
                dep.setVulnerabilities(vulns);
            }

            // Calculate 5-Factor Risk Score
            RiskAssessmentEntity riskEntity = riskEngine.calculateRisk(dep, vulns, false);
            riskEntity = riskAssessmentRepository.save(riskEntity);
            dep.setRiskAssessment(riskEntity);

            if (dep.getVersionDiffType() != VersionDiffType.UP_TO_DATE && dep.getVersionDiffType() != VersionDiffType.UNKNOWN) {
                outdatedCount++;
            }

            // Convert to DTO
            DependencyDetailDto detailDto = mapToDetailDto(dep, riskEntity, vulns, dataSource);
            detailDtos.add(detailDto);
        }

        // 5. Calculate Analysis Confidence Score
        AnalysisConfidenceEngine.ConfidenceResult conf = confidenceEngine.calculateConfidence(
                true,
                !manifestEntries.isEmpty(),
                !lockfileContents.isEmpty(),
                anyRegistryConnected || isDemo,
                anySecurityConnected || isDemo,
                savedDeps
        );

        for (DependencyDetailDto dDto : detailDtos) {
            dDto.setAnalysisConfidence(conf.getLevel());
        }

        // 6. Constraint-Based Greedy Batching Engine
        runEntity.setStatus(AnalysisStatus.OPTIMIZING_BATCHES);
        analysisRunRepository.save(runEntity);

        List<BatchDto> batches = batchingService.generateBatchesFromDtos(detailDtos);
        batchingService.persistBatches(repoEntity, batches, savedDeps);

        // Overall risk score
        double overallScore = detailDtos.isEmpty() ? 0.0 :
                detailDtos.stream().mapToDouble(d -> d.getRiskAssessment().getTotalScore()).average().orElse(0.0);
        overallScore = Math.round(overallScore * 10.0) / 10.0;
        RiskLevel overallLevel = riskEngine.classifyScoreWithThresholds(overallScore, riskEngine.getConfig().getThresholds());

        // Finalize Run Entity
        long duration = System.currentTimeMillis() - startTime;
        runEntity.setStatus(AnalysisStatus.COMPLETED);
        runEntity.setTotalDependencies(savedDeps.size());
        runEntity.setOutdatedCount(outdatedCount);
        runEntity.setVulnerabilityCount(allSavedVulns.size());
        runEntity.setCriticalCount(criticalCount);
        runEntity.setHighCount(highCount);
        runEntity.setBatchCount(batches.size());
        runEntity.setOverallRiskScore(overallScore);
        runEntity.setOverallRiskLevel(overallLevel);
        runEntity.setAnalysisConfidence(conf.getLevel());
        runEntity.setAnalysisConfidenceScore(conf.getScore());
        runEntity.setCompletedAt(LocalDateTime.now());
        analysisRunRepository.save(runEntity);

        // Update Repo Entity
        repoEntity.setLastScannedAt(LocalDateTime.now());
        repositoryJpaRepository.save(repoEntity);

        // Build Response Summary
        RepositorySummaryDto summary = new RepositorySummaryDto();
        summary.setId(repoEntity.getId());
        summary.setOwner(repoEntity.getOwner());
        summary.setName(repoEntity.getName());
        summary.setUrl(repoEntity.getUrl());
        summary.setDefaultBranch(repoEntity.getDefaultBranch());
        summary.setDetectedEcosystems(new ArrayList<>(detectedEcosystemNames));
        summary.setTotalFiles(detectedManifestFiles.size() + detectedLockfiles.size());
        summary.setTotalDependencies(savedDeps.size());
        summary.setOutdatedCount(outdatedCount);
        summary.setVulnerabilityCount(allSavedVulns.size());
        summary.setCriticalCount(criticalCount);
        summary.setHighCount(highCount);
        summary.setModerateCount(moderateCount);
        summary.setLowCount(lowCount);
        summary.setRecommendedBatches(batches.size());
        summary.setOverallRiskScore(overallScore);
        summary.setOverallRiskLevel(overallLevel);
        summary.setDataSource(dataSource);
        summary.setAnalysisConfidence(conf.getLevel());
        summary.setAnalysisConfidenceScore(conf.getScore());
        summary.setAnalysisStatus("COMPLETED");
        summary.setGithubStatus("CONNECTED");
        summary.setRegistryStatus(anyRegistryConnected || isDemo ? "CONNECTED" : "UNAVAILABLE");
        summary.setSecurityStatus(anySecurityConnected || isDemo ? (isDemo ? "DEMO" : "CONNECTED") : "UNAVAILABLE");
        summary.setRiskConfigSnapshot(runEntity.getRiskConfigSnapshotJson());
        summary.setLastScannedAt(repoEntity.getLastScannedAt());

        AnalysisResultDto result = new AnalysisResultDto();
        result.setSummary(summary);
        result.setDependencies(detailDtos);
        result.setBatches(batches);
        result.setDetectedManifestFiles(detectedManifestFiles);
        result.setWarnings(warnings);
        result.setAnalysisDurationMs(duration);

        List<VulnerabilityDto> vDtos = new ArrayList<>();
        for (VulnerabilityEntity v : allSavedVulns) {
            vDtos.add(vulnerabilityService.toDto(v));
        }
        result.setVulnerabilities(vDtos);

        return result;
    }

    private DependencyDetailDto mapToDetailDto(DependencyEntity dep, RiskAssessmentEntity risk, List<VulnerabilityEntity> vulns, String dataSource) {
        DependencyDetailDto dto = new DependencyDetailDto();
        dto.setId(dep.getId());
        dto.setName(dep.getName());
        dto.setGroupOrNamespace(dep.getGroupOrNamespace());
        dto.setCoordinates(dep.getCoordinates());
        dto.setCurrentVersion(dep.getCurrentVersion());
        dto.setDeclaredVersionRange(dep.getDeclaredVersionRange() != null ? dep.getDeclaredVersionRange() : dep.getCurrentVersion());
        dto.setResolvedVersion(dep.getResolvedVersion() != null ? dep.getResolvedVersion() : "UNKNOWN");
        dto.setLatestVersion(dep.getLatestVersion());
        dto.setRecommendedTargetVersion(dep.getRecommendedTargetVersion() != null ? dep.getRecommendedTargetVersion() : dep.getLatestVersion());
        dto.setLockfileSource(dep.getLockfileSource());
        dto.setVersionDiffType(dep.getVersionDiffType());
        dto.setEcosystem(dep.getEcosystem());
        dto.setManifestFile(dep.getDependencyFile() != null ? dep.getDependencyFile().getFilePath() : "manifest");
        dto.setScope(dep.getScope());
        dto.setDirect(dep.isDirect());
        dto.setDev(dep.isDev());
        dto.setDeprecated(dep.isDeprecated());
        dto.setDataSource(dataSource);
        dto.setSecurityPriority(dep.getSecurityPriority() != null ? dep.getSecurityPriority() : "NONE");
        dto.setSecurityDataStatus(dep.getSecurityDataStatus() != null ? dep.getSecurityDataStatus() : "VERIFIED");

        // Security Status label
        if ("SECURITY_DATA_UNAVAILABLE".equalsIgnoreCase(dep.getSecurityDataStatus())) {
            dto.setSecurityStatus("⚠️ Security Data Unavailable");
        } else if (vulns == null || vulns.isEmpty()) {
            dto.setSecurityStatus("🛡️ No known vulnerabilities");
        } else {
            VulnerabilitySeverity maxSev = vulns.stream().map(VulnerabilityEntity::getSeverity).min(Comparator.comparingInt(Enum::ordinal)).orElse(VulnerabilitySeverity.LOW);
            dto.setSecurityStatus("🚨 " + vulns.size() + " Vulnerabilit" + (vulns.size() > 1 ? "ies" : "y") + " (" + maxSev.name() + ")");
        }

        // Risk Assessment DTO
        RiskAssessmentDto rDto = new RiskAssessmentDto();
        rDto.setTotalScore(risk.getTotalScore());
        rDto.setRiskLevel(risk.getRiskLevel());
        rDto.setVersionRisk(risk.getVersionRisk());
        rDto.setSecurityRisk(risk.getSecurityRisk());
        rDto.setCompatibilityRisk(risk.getCompatibilityRisk());
        rDto.setDependencyImpactRisk(risk.getDependencyImpactRisk());
        rDto.setBuildRisk(risk.getBuildRisk());
        rDto.setRecommendation(risk.getRecommendationText());
        rDto.setSecurityPriority(dep.getSecurityPriority());

        // Update Risk Score (0-100 without security conflation)
        RiskWeightsConfig.Weights w = riskEngine.getConfig().getWeights();
        double maxUpdateWeight = w.getVersionChange() + w.getCompatibilityBreaking() + w.getDependencyImpact() + w.getBuildIntegrity();
        double rawUpdateScore = (risk.getVersionRisk() + risk.getCompatibilityRisk() + risk.getDependencyImpactRisk() + risk.getBuildRisk());
        double updateRiskScore = maxUpdateWeight > 0 ?
                Math.min(100.0, Math.round((rawUpdateScore / maxUpdateWeight * 100.0) * 10.0) / 10.0) : rawUpdateScore;
        rDto.setUpdateRiskScore(updateRiskScore);
        rDto.setUpdateRiskLevel(riskEngine.classifyScoreWithThresholds(updateRiskScore, riskEngine.getConfig().getThresholds()));

        // Factor Contributions
        List<RiskAssessmentDto.FactorContributionDto> contributions = new ArrayList<>();
        if (risk.getTotalScore() > 0) {
            if (risk.getVersionRisk() > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Version Change Risk", Math.round(risk.getVersionRisk() * 10.0) / 10.0, Math.round((risk.getVersionRisk() / risk.getTotalScore() * 100.0) * 10.0) / 10.0, "Version drift score"));
            }
            if (risk.getSecurityRisk() > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Security Vulnerability Risk", Math.round(risk.getSecurityRisk() * 10.0) / 10.0, Math.round((risk.getSecurityRisk() / risk.getTotalScore() * 100.0) * 10.0) / 10.0, "CVE vulnerability advisory score"));
            }
            if (risk.getCompatibilityRisk() > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Compatibility Risk", Math.round(risk.getCompatibilityRisk() * 10.0) / 10.0, Math.round((risk.getCompatibilityRisk() / risk.getTotalScore() * 100.0) * 10.0) / 10.0, "Breaking API changes or deprecation"));
            }
            if (risk.getDependencyImpactRisk() > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Dependency Impact", Math.round(risk.getDependencyImpactRisk() * 10.0) / 10.0, Math.round((risk.getDependencyImpactRisk() / risk.getTotalScore() * 100.0) * 10.0) / 10.0, "Runtime blast radius"));
            }
            if (risk.getBuildRisk() > 0) {
                contributions.add(new RiskAssessmentDto.FactorContributionDto("Build Integrity", Math.round(risk.getBuildRisk() * 10.0) / 10.0, Math.round((risk.getBuildRisk() / risk.getTotalScore() * 100.0) * 10.0) / 10.0, "Lockfile/tree conflict"));
            }
        }
        rDto.setFactorContributions(contributions);

        if (risk.getExplanationJson() != null) {
            try {
                rDto.setReasons(objectMapper.readValue(risk.getExplanationJson(), new TypeReference<List<String>>() {}));
            } catch (Exception ignored) {}
        }
        dto.setRiskAssessment(rDto);

        // Vulnerabilities DTO
        List<VulnerabilityDto> vDtos = new ArrayList<>();
        if (vulns != null) {
            for (VulnerabilityEntity v : vulns) {
                vDtos.add(vulnerabilityService.toDto(v));
            }
        }
        dto.setVulnerabilities(vDtos);

        // Authoritative Links
        dto.setLinks(resourceLinkService.generateResourceLinks(dep));
        dto.setRecommendedAction(recommendationService.generateAction(dto));

        return dto;
    }

    private List<GitHubApiClient.ManifestEntry> getCuratedDemoManifests(GitHubApiClient.RepoDetails repo) {
        List<GitHubApiClient.ManifestEntry> list = new ArrayList<>();
        list.add(new GitHubApiClient.ManifestEntry("pom.xml", null));
        list.add(new GitHubApiClient.ManifestEntry("package.json", null));
        list.add(new GitHubApiClient.ManifestEntry("package-lock.json", null, true));
        return list;
    }

    private String getCuratedDemoContent(String path, GitHubApiClient.RepoDetails repo) {
        String lower = path.toLowerCase();
        if (lower.endsWith("pom.xml")) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                   "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">\n" +
                   "  <modelVersion>4.0.0</modelVersion>\n" +
                   "  <groupId>com.example</groupId>\n" +
                   "  <artifactId>vulnerable-microservice-demo</artifactId>\n" +
                   "  <version>1.0.0</version>\n" +
                   "  <properties>\n" +
                   "    <spring.version>5.3.18</spring.version>\n" +
                   "    <log4j.version>2.14.1</log4j.version>\n" +
                   "  </properties>\n" +
                   "  <dependencies>\n" +
                   "    <dependency>\n" +
                   "      <groupId>org.springframework</groupId>\n" +
                   "      <artifactId>spring-core</artifactId>\n" +
                   "      <version>${spring.version}</version>\n" +
                   "    </dependency>\n" +
                   "    <dependency>\n" +
                   "      <groupId>org.apache.logging.log4j</groupId>\n" +
                   "      <artifactId>log4j-core</artifactId>\n" +
                   "      <version>${log4j.version}</version>\n" +
                   "    </dependency>\n" +
                   "    <dependency>\n" +
                   "      <groupId>com.fasterxml.jackson.core</groupId>\n" +
                   "      <artifactId>jackson-databind</artifactId>\n" +
                   "      <version>2.13.0</version>\n" +
                   "    </dependency>\n" +
                   "    <dependency>\n" +
                   "      <groupId>org.springframework.boot</groupId>\n" +
                   "      <artifactId>spring-boot-starter-web</artifactId>\n" +
                   "      <version>2.7.5</version>\n" +
                   "    </dependency>\n" +
                   "    <dependency>\n" +
                   "      <groupId>com.google.guava</groupId>\n" +
                   "      <artifactId>guava</artifactId>\n" +
                   "      <version>31.0-jre</version>\n" +
                   "    </dependency>\n" +
                   "    <dependency>\n" +
                   "      <groupId>org.junit.jupiter</groupId>\n" +
                   "      <artifactId>junit-jupiter-api</artifactId>\n" +
                   "      <version>5.8.2</version>\n" +
                   "      <scope>test</scope>\n" +
                   "    </dependency>\n" +
                   "  </dependencies>\n" +
                   "</project>";
        } else if (lower.endsWith("package.json")) {
            return "{\n" +
                   "  \"name\": \"demo-app\",\n" +
                   "  \"version\": \"1.0.0\",\n" +
                   "  \"dependencies\": {\n" +
                   "    \"express\": \"^4.17.1\",\n" +
                   "    \"lodash\": \"^4.17.15\",\n" +
                   "    \"axios\": \"^0.21.1\",\n" +
                   "    \"cors\": \"^2.8.5\",\n" +
                   "    \"dotenv\": \"^10.0.0\"\n" +
                   "  },\n" +
                   "  \"devDependencies\": {\n" +
                   "    \"mocha\": \"^9.1.0\",\n" +
                   "    \"chai\": \"^4.3.4\",\n" +
                   "    \"nodemon\": \"^2.0.12\"\n" +
                   "  }\n" +
                   "}";
        } else if (lower.endsWith("package-lock.json")) {
            return "{\n" +
                   "  \"name\": \"demo-app\",\n" +
                   "  \"version\": \"1.0.0\",\n" +
                   "  \"lockfileVersion\": 2,\n" +
                   "  \"packages\": {\n" +
                   "    \"node_modules/express\": {\"version\": \"4.17.1\"},\n" +
                   "    \"node_modules/lodash\": {\"version\": \"4.17.15\"},\n" +
                   "    \"node_modules/axios\": {\"version\": \"0.21.1\"},\n" +
                   "    \"node_modules/cors\": {\"version\": \"2.8.5\"},\n" +
                   "    \"node_modules/dotenv\": {\"version\": \"10.0.0\"},\n" +
                   "    \"node_modules/mocha\": {\"version\": \"9.1.0\"},\n" +
                   "    \"node_modules/chai\": {\"version\": \"4.3.4\"},\n" +
                   "    \"node_modules/nodemon\": {\"version\": \"2.0.12\"}\n" +
                   "  }\n" +
                   "}";
        }
        return "";
    }
}
