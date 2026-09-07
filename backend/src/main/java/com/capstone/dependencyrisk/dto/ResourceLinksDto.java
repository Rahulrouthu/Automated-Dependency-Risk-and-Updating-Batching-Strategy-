package com.capstone.dependencyrisk.dto;

import java.util.List;

public class ResourceLinksDto {
    private String repositoryUrl;
    private String homepageUrl;
    private String documentationUrl;
    private String changelogUrl;
    private String releaseNotesUrl;
    private String packageRegistryUrl;
    private String securityAdvisoryUrl;
    private String migrationGuideUrl;

    public ResourceLinksDto() {}

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public String getHomepageUrl() { return homepageUrl; }
    public void setHomepageUrl(String homepageUrl) { this.homepageUrl = homepageUrl; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public String getChangelogUrl() { return changelogUrl; }
    public void setChangelogUrl(String changelogUrl) { this.changelogUrl = changelogUrl; }

    public String getReleaseNotesUrl() { return releaseNotesUrl; }
    public void setReleaseNotesUrl(String releaseNotesUrl) { this.releaseNotesUrl = releaseNotesUrl; }

    public String getPackageRegistryUrl() { return packageRegistryUrl; }
    public void setPackageRegistryUrl(String packageRegistryUrl) { this.packageRegistryUrl = packageRegistryUrl; }

    public String getSecurityAdvisoryUrl() { return securityAdvisoryUrl; }
    public void setSecurityAdvisoryUrl(String securityAdvisoryUrl) { this.securityAdvisoryUrl = securityAdvisoryUrl; }

    public String getMigrationGuideUrl() { return migrationGuideUrl; }
    public void setMigrationGuideUrl(String migrationGuideUrl) { this.migrationGuideUrl = migrationGuideUrl; }
}
