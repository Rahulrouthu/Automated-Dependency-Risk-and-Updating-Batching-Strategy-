package com.capstone.dependencyrisk.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResultDto {
    private RepositorySummaryDto summary;
    private List<DependencyDetailDto> dependencies = new ArrayList<>();
    private List<VulnerabilityDto> vulnerabilities = new ArrayList<>();
    private List<BatchDto> batches = new ArrayList<>();
    private List<String> detectedManifestFiles = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private long analysisDurationMs;

    public AnalysisResultDto() {}

    public RepositorySummaryDto getSummary() { return summary; }
    public void setSummary(RepositorySummaryDto summary) { this.summary = summary; }

    public List<DependencyDetailDto> getDependencies() { return dependencies; }
    public void setDependencies(List<DependencyDetailDto> dependencies) { this.dependencies = dependencies; }

    public List<VulnerabilityDto> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<VulnerabilityDto> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public List<BatchDto> getBatches() { return batches; }
    public void setBatches(List<BatchDto> batches) { this.batches = batches; }

    public List<String> getDetectedManifestFiles() { return detectedManifestFiles; }
    public void setDetectedManifestFiles(List<String> detectedManifestFiles) { this.detectedManifestFiles = detectedManifestFiles; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public long getAnalysisDurationMs() { return analysisDurationMs; }
    public void setAnalysisDurationMs(long analysisDurationMs) { this.analysisDurationMs = analysisDurationMs; }
}
