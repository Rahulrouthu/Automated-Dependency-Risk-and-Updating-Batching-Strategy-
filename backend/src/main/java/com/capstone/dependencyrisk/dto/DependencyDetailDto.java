package com.capstone.dependencyrisk.dto;

import com.capstone.dependencyrisk.entity.EcosystemType;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import java.util.ArrayList;
import java.util.List;

public class DependencyDetailDto {
    private Long id;
    private String name;
    private String groupOrNamespace;
    private String coordinates;
    private String currentVersion;
    private String declaredVersionRange;
    private String resolvedVersion;
    private String latestVersion;
    private String recommendedTargetVersion;
    private String lockfileSource;
    private VersionDiffType versionDiffType;
    private EcosystemType ecosystem;
    private String manifestFile;
    private String scope;
    private boolean isDirect;
    private boolean isDev;
    private boolean isDeprecated;
    private String securityStatus;
    private String securityPriority = "NONE";
    private String securityDataStatus = "VERIFIED";
    private String dataSource = "LIVE";
    private String analysisConfidence = "HIGH";
    private int majorVersionDifference = 0;
    private int minorVersionDifference = 0;
    private int patchVersionDifference = 0;
    private RiskAssessmentDto riskAssessment;
    private List<VulnerabilityDto> vulnerabilities = new ArrayList<>();
    private ResourceLinksDto links;
    private String recommendedAction;

    public DependencyDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroupOrNamespace() { return groupOrNamespace; }
    public void setGroupOrNamespace(String groupOrNamespace) { this.groupOrNamespace = groupOrNamespace; }

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }

    public String getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }

    public String getDeclaredVersionRange() { return declaredVersionRange; }
    public void setDeclaredVersionRange(String declaredVersionRange) { this.declaredVersionRange = declaredVersionRange; }

    public String getResolvedVersion() { return resolvedVersion; }
    public void setResolvedVersion(String resolvedVersion) { this.resolvedVersion = resolvedVersion; }

    public String getLatestVersion() { return latestVersion; }
    public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }

    public String getRecommendedTargetVersion() { return recommendedTargetVersion; }
    public void setRecommendedTargetVersion(String recommendedTargetVersion) { this.recommendedTargetVersion = recommendedTargetVersion; }

    public String getLockfileSource() { return lockfileSource; }
    public void setLockfileSource(String lockfileSource) { this.lockfileSource = lockfileSource; }

    public VersionDiffType getVersionDiffType() { return versionDiffType; }
    public void setVersionDiffType(VersionDiffType versionDiffType) { this.versionDiffType = versionDiffType; }

    public EcosystemType getEcosystem() { return ecosystem; }
    public void setEcosystem(EcosystemType ecosystem) { this.ecosystem = ecosystem; }

    public String getManifestFile() { return manifestFile; }
    public void setManifestFile(String manifestFile) { this.manifestFile = manifestFile; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public boolean isDirect() { return isDirect; }
    public void setDirect(boolean direct) { isDirect = direct; }

    public boolean isDev() { return isDev; }
    public void setDev(boolean dev) { isDev = dev; }

    public boolean isDeprecated() { return isDeprecated; }
    public void setDeprecated(boolean deprecated) { isDeprecated = deprecated; }

    public String getSecurityStatus() { return securityStatus; }
    public void setSecurityStatus(String securityStatus) { this.securityStatus = securityStatus; }

    public String getSecurityPriority() { return securityPriority; }
    public void setSecurityPriority(String securityPriority) { this.securityPriority = securityPriority; }

    public String getSecurityDataStatus() { return securityDataStatus; }
    public void setSecurityDataStatus(String securityDataStatus) { this.securityDataStatus = securityDataStatus; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getAnalysisConfidence() { return analysisConfidence; }
    public void setAnalysisConfidence(String analysisConfidence) { this.analysisConfidence = analysisConfidence; }

    public int getMajorVersionDifference() { return majorVersionDifference; }
    public void setMajorVersionDifference(int majorVersionDifference) { this.majorVersionDifference = majorVersionDifference; }

    public int getMinorVersionDifference() { return minorVersionDifference; }
    public void setMinorVersionDifference(int minorVersionDifference) { this.minorVersionDifference = minorVersionDifference; }

    public int getPatchVersionDifference() { return patchVersionDifference; }
    public void setPatchVersionDifference(int patchVersionDifference) { this.patchVersionDifference = patchVersionDifference; }

    public RiskAssessmentDto getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(RiskAssessmentDto riskAssessment) { this.riskAssessment = riskAssessment; }

    public List<VulnerabilityDto> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<VulnerabilityDto> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public ResourceLinksDto getLinks() { return links; }
    public void setLinks(ResourceLinksDto links) { this.links = links; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
}
