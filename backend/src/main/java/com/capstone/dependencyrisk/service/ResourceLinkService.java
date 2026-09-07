package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.dto.ResourceLinksDto;
import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceLinkService {

    private static final Map<String, String> MIGRATION_GUIDES = new HashMap<>();

    static {
        MIGRATION_GUIDES.put("spring-boot", "https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide");
        MIGRATION_GUIDES.put("spring-core", "https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x");
        MIGRATION_GUIDES.put("junit", "https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4");
        MIGRATION_GUIDES.put("react", "https://react.dev/blog/2024/04/25/react-19-upgrade-guide");
        MIGRATION_GUIDES.put("axios", "https://github.com/axios/axios/blob/main/MIGRATION_GUIDE.md");
        MIGRATION_GUIDES.put("express", "https://expressjs.com/en/guide/migrating-5.html");
        MIGRATION_GUIDES.put("pydantic", "https://docs.pydantic.dev/latest/migration/");
        MIGRATION_GUIDES.put("sqlalchemy", "https://docs.sqlalchemy.org/en/20/changelog/migration_20.html");
        MIGRATION_GUIDES.put("django", "https://docs.djangoproject.com/en/5.1/releases/");
        MIGRATION_GUIDES.put("fastapi", "https://fastapi.tiangolo.com/release-notes/");
    }

    public ResourceLinksDto generateResourceLinks(DependencyEntity dep) {
        ResourceLinksDto links = new ResourceLinksDto();
        if (dep == null) return links;

        String name = dep.getName().toLowerCase();
        String group = dep.getGroupOrNamespace();
        EcosystemType eco = dep.getEcosystem();

        // 1. Package Registry Page
        if (eco == EcosystemType.NPM) {
            String fullPkg = (group != null && !group.isEmpty()) ? group + "/" + dep.getName() : dep.getName();
            links.setPackageRegistryUrl("https://www.npmjs.com/package/" + fullPkg);
            links.setDocumentationUrl("https://www.npmjs.com/package/" + fullPkg);
            links.setChangelogUrl("https://github.com/" + dep.getName() + "/" + dep.getName() + "/releases");
        } else if (eco == EcosystemType.PYTHON) {
            links.setPackageRegistryUrl("https://pypi.org/project/" + dep.getName() + "/");
            links.setDocumentationUrl("https://pypi.org/project/" + dep.getName() + "/#description");
            links.setChangelogUrl("https://pypi.org/project/" + dep.getName() + "/#history");
        } else if (eco == EcosystemType.MAVEN || eco == EcosystemType.GRADLE) {
            String g = group != null ? group : "org.apache";
            links.setPackageRegistryUrl("https://central.sonatype.com/artifact/" + g + "/" + dep.getName());
            links.setDocumentationUrl("https://mvnrepository.com/artifact/" + g + "/" + dep.getName());
        }

        // 2. Official GitHub Repository & Release URL
        if (dep.getRepositoryUrl() != null && !dep.getRepositoryUrl().isEmpty()) {
            links.setRepositoryUrl(dep.getRepositoryUrl());
            links.setReleaseNotesUrl(dep.getRepositoryUrl() + "/releases");
            links.setChangelogUrl(dep.getRepositoryUrl() + "/blob/main/CHANGELOG.md");
        } else {
            // Curated and heuristic repository URLs
            String repoUrl = estimateRepositoryUrl(group, name, eco);
            if (repoUrl != null) {
                links.setRepositoryUrl(repoUrl);
                links.setReleaseNotesUrl(repoUrl + "/releases");
                links.setChangelogUrl(repoUrl + "/blob/main/CHANGELOG.md");
            }
        }

        // 3. Security Advisory Link
        links.setSecurityAdvisoryUrl("https://osv.dev/list?q=" + dep.getName());

        // 4. Migration Guide Link
        for (Map.Entry<String, String> entry : MIGRATION_GUIDES.entrySet()) {
            if (name.contains(entry.getKey())) {
                links.setMigrationGuideUrl(entry.getValue());
                break;
            }
        }

        // Set homepage
        if (dep.getHomepageUrl() != null && !dep.getHomepageUrl().isEmpty()) {
            links.setHomepageUrl(dep.getHomepageUrl());
        } else {
            links.setHomepageUrl(links.getPackageRegistryUrl());
        }

        return links;
    }

    private String estimateRepositoryUrl(String group, String name, EcosystemType eco) {
        if (name.contains("spring")) {
            return "https://github.com/spring-projects/spring-framework";
        }
        if (name.contains("lodash")) {
            return "https://github.com/lodash/lodash";
        }
        if (name.contains("axios")) {
            return "https://github.com/axios/axios";
        }
        if (name.contains("express")) {
            return "https://github.com/expressjs/express";
        }
        if (name.contains("requests")) {
            return "https://github.com/psf/requests";
        }
        if (name.contains("flask")) {
            return "https://github.com/pallets/flask";
        }
        if (name.contains("junit")) {
            return "https://github.com/junit-team/junit5";
        }
        if (name.contains("jackson")) {
            return "https://github.com/FasterXML/jackson";
        }
        if (name.contains("mockito")) {
            return "https://github.com/mockito/mockito";
        }
        if (group != null && group.startsWith("org.apache")) {
            return "https://github.com/apache/" + name;
        }
        return "https://github.com/search?q=" + name;
    }
}
