package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dependencies")
public class DependencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_file_id", nullable = false)
    private DependencyFileEntity dependencyFile;

    @Column(nullable = false)
    private String name;

    @Column(name = "group_or_namespace")
    private String groupOrNamespace;

    @Column(name = "current_version", nullable = false)
    private String currentVersion;

    @Column(name = "declared_version_range")
    private String declaredVersionRange;

    @Column(name = "resolved_version")
    private String resolvedVersion;

    @Column(name = "latest_version")
    private String latestVersion;

    @Column(name = "recommended_target_version")
    private String recommendedTargetVersion;

    @Column(name = "lockfile_source")
    private String lockfileSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "version_diff_type")
    private VersionDiffType versionDiffType = VersionDiffType.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EcosystemType ecosystem;

    @Column(name = "scope")
    private String scope = "compile";

    @Column(name = "is_direct")
    private boolean isDirect = true;

    @Column(name = "is_dev")
    private boolean isDev = false;

    @Column(name = "is_deprecated")
    private boolean isDeprecated = false;

    @Column(name = "security_priority")
    private String securityPriority = "NONE";

    @Column(name = "security_data_status")
    private String securityDataStatus = "VERIFIED";

    @Column(name = "data_source")
    private String dataSource = "LIVE";

    @Column(name = "analysis_confidence")
    private String analysisConfidence = "HIGH";

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "changelog_url")
    private String changelogUrl;

    @Column(name = "documentation_url")
    private String documentationUrl;

    @Column(name = "migration_guide_url")
    private String migrationGuideUrl;

    @OneToOne(mappedBy = "dependency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RiskAssessmentEntity riskAssessment;

    @OneToMany(mappedBy = "dependency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VulnerabilityEntity> vulnerabilities = new ArrayList<>();

    public DependencyEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DependencyFileEntity getDependencyFile() { return dependencyFile; }
    public void setDependencyFile(DependencyFileEntity dependencyFile) { this.dependencyFile = dependencyFile; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroupOrNamespace() { return groupOrNamespace; }
    public void setGroupOrNamespace(String groupOrNamespace) { this.groupOrNamespace = groupOrNamespace; }

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

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public boolean isDirect() { return isDirect; }
    public void setDirect(boolean direct) { isDirect = direct; }

    public boolean isDev() { return isDev; }
    public void setDev(boolean dev) { isDev = dev; }

    public boolean isDeprecated() { return isDeprecated; }
    public void setDeprecated(boolean deprecated) { isDeprecated = deprecated; }

    public String getSecurityPriority() { return securityPriority; }
    public void setSecurityPriority(String securityPriority) { this.securityPriority = securityPriority; }

    public String getSecurityDataStatus() { return securityDataStatus; }
    public void setSecurityDataStatus(String securityDataStatus) { this.securityDataStatus = securityDataStatus; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getAnalysisConfidence() { return analysisConfidence; }
    public void setAnalysisConfidence(String analysisConfidence) { this.analysisConfidence = analysisConfidence; }

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public String getHomepageUrl() { return homepageUrl; }
    public void setHomepageUrl(String homepageUrl) { this.homepageUrl = homepageUrl; }

    public String getChangelogUrl() { return changelogUrl; }
    public void setChangelogUrl(String changelogUrl) { this.changelogUrl = changelogUrl; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public String getMigrationGuideUrl() { return migrationGuideUrl; }
    public void setMigrationGuideUrl(String migrationGuideUrl) { this.migrationGuideUrl = migrationGuideUrl; }

    public RiskAssessmentEntity getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(RiskAssessmentEntity riskAssessment) { this.riskAssessment = riskAssessment; }

    public List<VulnerabilityEntity> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<VulnerabilityEntity> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public String getCoordinates() {
        if (groupOrNamespace != null && !groupOrNamespace.trim().isEmpty()) {
            return groupOrNamespace + ":" + name;
        }
        return name;
    }
}
