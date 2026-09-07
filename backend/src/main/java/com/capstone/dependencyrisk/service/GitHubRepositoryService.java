package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.dto.*;
import com.capstone.dependencyrisk.entity.*;
import com.capstone.dependencyrisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubRepositoryService {

    private final RepositoryJpaRepository repositoryJpaRepository;
    private final RepositoryScannerService scannerService;
    private final DependencyJpaRepository dependencyRepository;
    private final VulnerabilityJpaRepository vulnerabilityRepository;
    private final DependencyBatchJpaRepository batchRepository;
    private final AnalysisRunJpaRepository analysisRunRepository;
    private final ReportService reportService;
    private final BatchingService batchingService;

    @Autowired
    public GitHubRepositoryService(
            RepositoryJpaRepository repositoryJpaRepository,
            RepositoryScannerService scannerService,
            DependencyJpaRepository dependencyRepository,
            VulnerabilityJpaRepository vulnerabilityRepository,
            DependencyBatchJpaRepository batchRepository,
            AnalysisRunJpaRepository analysisRunRepository,
            ReportService reportService,
            BatchingService batchingService) {
        this.repositoryJpaRepository = repositoryJpaRepository;
        this.scannerService = scannerService;
        this.dependencyRepository = dependencyRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.batchRepository = batchRepository;
        this.analysisRunRepository = analysisRunRepository;
        this.reportService = reportService;
        this.batchingService = batchingService;
    }

    public AnalysisResultDto analyzeRepository(AnalyzeRequestDto request) {
        return scannerService.scanRepository(request);
    }

    public List<RepositorySummaryDto> getRecentRepositories() {
        return repositoryJpaRepository.findAll().stream().map(r -> {
            RepositorySummaryDto s = new RepositorySummaryDto();
            s.setId(r.getId());
            s.setOwner(r.getOwner());
            s.setName(r.getName());
            s.setUrl(r.getUrl());
            s.setDefaultBranch(r.getDefaultBranch());
            s.setLastScannedAt(r.getLastScannedAt());

            analysisRunRepository.findFirstByRepositoryIdOrderByStartedAtDesc(r.getId()).ifPresent(run -> {
                s.setTotalDependencies(run.getTotalDependencies());
                s.setOutdatedCount(run.getOutdatedCount());
                s.setVulnerabilityCount(run.getVulnerabilityCount());
                s.setCriticalCount(run.getCriticalCount());
                s.setHighCount(run.getHighCount());
                s.setRecommendedBatches(run.getBatchCount());
                s.setOverallRiskScore(run.getOverallRiskScore());
                s.setOverallRiskLevel(run.getOverallRiskLevel());
                s.setDataSource(run.getDataSource() != null ? run.getDataSource() : "LIVE");
                s.setAnalysisConfidence(run.getAnalysisConfidence() != null ? run.getAnalysisConfidence() : "HIGH");
                s.setAnalysisConfidenceScore(run.getAnalysisConfidenceScore());
                s.setAnalysisStatus(run.getStatus().name());
                s.setRiskConfigSnapshot(run.getRiskConfigSnapshotJson());
            });

            return s;
        }).collect(Collectors.toList());
    }

    public ReportDto getReport(Long repoId) {
        RepositoryEntity repo = repositoryJpaRepository.findById(repoId).orElse(null);
        if (repo == null) return null;

        AnalysisRunEntity run = analysisRunRepository.findFirstByRepositoryIdOrderByStartedAtDesc(repoId).orElse(null);
        List<DependencyBatchEntity> batchEntities = batchRepository.findByRepositoryIdOrderByBatchNumberAsc(repoId);
        List<DependencyEntity> depEntities = dependencyRepository.findByRepositoryId(repoId);

        RepositorySummaryDto summary = new RepositorySummaryDto();
        summary.setId(repo.getId());
        summary.setOwner(repo.getOwner());
        summary.setName(repo.getName());
        summary.setUrl(repo.getUrl());
        summary.setDefaultBranch(repo.getDefaultBranch());
        if (run != null) {
            summary.setTotalDependencies(run.getTotalDependencies());
            summary.setOutdatedCount(run.getOutdatedCount());
            summary.setVulnerabilityCount(run.getVulnerabilityCount());
            summary.setCriticalCount(run.getCriticalCount());
            summary.setHighCount(run.getHighCount());
            summary.setOverallRiskScore(run.getOverallRiskScore());
            summary.setOverallRiskLevel(run.getOverallRiskLevel());
            summary.setDataSource(run.getDataSource() != null ? run.getDataSource() : "LIVE");
            summary.setAnalysisConfidence(run.getAnalysisConfidence() != null ? run.getAnalysisConfidence() : "HIGH");
            summary.setAnalysisConfidenceScore(run.getAnalysisConfidenceScore());
            summary.setAnalysisStatus(run.getStatus().name());
            summary.setRiskConfigSnapshot(run.getRiskConfigSnapshotJson());
        }

        List<BatchDto> batchDtos = new ArrayList<>();
        for (DependencyBatchEntity b : batchEntities) {
            BatchDto dto = new BatchDto();
            dto.setId(b.getId());
            dto.setBatchNumber(b.getBatchNumber());
            dto.setTitle(b.getTitle());
            dto.setCategory(b.getCategory());
            dto.setBatchRiskLevel(b.getBatchRiskLevel());
            dto.setBatchRiskScore(b.getBatchRiskScore());
            dto.setStrategyDescription(b.getStrategyDescription());
            dto.setReasonForGrouping(b.getReasonForGrouping());
            dto.setExecutionCommands(b.getExecutionCommands());
            dto.setPrTitle(b.getPrTitle());
            dto.setPrBody(b.getPrBody());
            batchDtos.add(dto);
        }

        List<DependencyDetailDto> depDtos = new ArrayList<>();
        for (DependencyEntity d : depEntities) {
            DependencyDetailDto dDto = new DependencyDetailDto();
            dDto.setId(d.getId());
            dDto.setName(d.getName());
            dDto.setGroupOrNamespace(d.getGroupOrNamespace());
            dDto.setCoordinates(d.getCoordinates());
            dDto.setCurrentVersion(d.getCurrentVersion());
            dDto.setDeclaredVersionRange(d.getDeclaredVersionRange());
            dDto.setResolvedVersion(d.getResolvedVersion());
            dDto.setLatestVersion(d.getLatestVersion());
            dDto.setRecommendedTargetVersion(d.getRecommendedTargetVersion());
            dDto.setEcosystem(d.getEcosystem());
            dDto.setSecurityPriority(d.getSecurityPriority());
            dDto.setSecurityDataStatus(d.getSecurityDataStatus());
            dDto.setDataSource(d.getDataSource());
            depDtos.add(dDto);
        }

        return reportService.generateExecutiveReport(summary, depDtos, batchDtos);
    }

    public List<PresetRepoDto> getPresets() {
        List<PresetRepoDto> presets = new ArrayList<>();
        presets.add(new PresetRepoDto("Spring PetClinic", "https://github.com/spring-projects/spring-petclinic", "Popular Enterprise Java & Spring Boot reference web application with Maven", EcosystemType.MAVEN, "Enterprise Java"));
        presets.add(new PresetRepoDto("Express.js API", "https://github.com/expressjs/express", "Fast, unopinionated, minimalist web framework for Node.js with npm ecosystem", EcosystemType.NPM, "Node.js / Express"));
        presets.add(new PresetRepoDto("Python Requests Library", "https://github.com/psf/requests", "Standard HTTP library for Python with requirements.txt manifest", EcosystemType.PYTHON, "Python / PyPI"));
        presets.add(new PresetRepoDto("Vulnerable Microservice Demo", "https://github.com/devops-capstone/vulnerable-microservice-demo", "Multi-ecosystem benchmark demo containing known CVEs, Log4j2, Spring4Shell, and breaking major versions", EcosystemType.MAVEN, "Security Audit"));
        return presets;
    }
}
