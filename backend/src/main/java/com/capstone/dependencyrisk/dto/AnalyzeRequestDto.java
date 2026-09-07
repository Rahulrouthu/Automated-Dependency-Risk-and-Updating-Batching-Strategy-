package com.capstone.dependencyrisk.dto;

public class AnalyzeRequestDto {
    private String repositoryUrl;
    private String branch;
    private String githubToken;
    private boolean forceReanalyze = false;
    private boolean demoMode = false;

    public AnalyzeRequestDto() {}

    public AnalyzeRequestDto(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getGithubToken() { return githubToken; }
    public void setGithubToken(String githubToken) { this.githubToken = githubToken; }

    public boolean isForceReanalyze() { return forceReanalyze; }
    public void setForceReanalyze(boolean forceReanalyze) { this.forceReanalyze = forceReanalyze; }

    public boolean isDemoMode() { return demoMode; }
    public void setDemoMode(boolean demoMode) { this.demoMode = demoMode; }
}
