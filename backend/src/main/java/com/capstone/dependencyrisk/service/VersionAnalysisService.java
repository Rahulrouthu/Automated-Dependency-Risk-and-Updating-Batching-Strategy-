package com.capstone.dependencyrisk.service;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import com.capstone.dependencyrisk.entity.VersionDiffType;
import com.capstone.dependencyrisk.risk.VersionComparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VersionAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(VersionAnalysisService.class);

    private final VersionComparator versionComparator;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, String> registryCache = new ConcurrentHashMap<>();

    @Value("${app.registries.npm-url:https://registry.npmjs.org}")
    private String npmRegistryUrl;

    @Value("${app.registries.pypi-url:https://pypi.org/pypi}")
    private String pypiRegistryUrl;

    @Value("${app.registries.maven-repo1-url:https://repo1.maven.org/maven2}")
    private String mavenRepoUrl;

    @Autowired
    public VersionAnalysisService(VersionComparator versionComparator, RestTemplate restTemplate) {
        this.versionComparator = versionComparator;
        this.restTemplate = restTemplate;
    }

    public boolean analyzeAndEnrichVersion(DependencyEntity dependency, boolean isDemoMode) {
        if (dependency == null || dependency.getCurrentVersion() == null) return false;

        String cacheKey = dependency.getEcosystem() + ":" + dependency.getCoordinates();
        String latestVersion = registryCache.computeIfAbsent(cacheKey, k -> fetchLatestVersionFromRegistry(dependency));
        boolean registrySuccess = (latestVersion != null && !latestVersion.isEmpty());

        if (!registrySuccess && isDemoMode) {
            latestVersion = fallbackLatestVersion(dependency);
        }

        if (latestVersion != null && !latestVersion.isEmpty()) {
            dependency.setLatestVersion(latestVersion);
            dependency.setRecommendedTargetVersion(latestVersion);
            VersionDiffType diffType = versionComparator.compareVersions(dependency.getCurrentVersion(), latestVersion);
            dependency.setVersionDiffType(diffType);
        } else {
            dependency.setLatestVersion(dependency.getCurrentVersion());
            dependency.setRecommendedTargetVersion(dependency.getCurrentVersion());
            dependency.setVersionDiffType(VersionDiffType.UP_TO_DATE);
        }

        return registrySuccess;
    }

    private String fetchLatestVersionFromRegistry(DependencyEntity dep) {
        try {
            if (dep.getEcosystem() == EcosystemType.NPM) {
                String pkgName = dep.getCoordinates();
                String url = npmRegistryUrl + "/" + pkgName;
                ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    if (root.has("dist-tags") && root.get("dist-tags").has("latest")) {
                        String latest = root.get("dist-tags").get("latest").asText();
                        if (root.has("versions") && root.get("versions").has(latest) && root.get("versions").get(latest).has("deprecated")) {
                            dep.setDeprecated(true);
                        }
                        if (root.has("homepage")) {
                            dep.setHomepageUrl(root.get("homepage").asText());
                        }
                        return latest;
                    }
                }
            } else if (dep.getEcosystem() == EcosystemType.PYTHON) {
                String pkgName = dep.getName().toLowerCase();
                String url = pypiRegistryUrl + "/" + pkgName + "/json";
                ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    if (root.has("info") && root.get("info").has("version")) {
                        String latest = root.get("info").get("version").asText();
                        if (root.get("info").has("project_urls") && root.get("info").get("project_urls").has("Homepage")) {
                            dep.setHomepageUrl(root.get("info").get("project_urls").get("Homepage").asText());
                        }
                        return latest;
                    }
                }
            } else if (dep.getEcosystem() == EcosystemType.MAVEN || dep.getEcosystem() == EcosystemType.GRADLE) {
                String group = dep.getGroupOrNamespace() != null ? dep.getGroupOrNamespace() : "org.apache";
                String artifact = dep.getName();
                String groupPath = group.replace('.', '/');
                String url = mavenRepoUrl + "/" + groupPath + "/" + artifact + "/maven-metadata.xml";
                ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(res.getBody().getBytes(StandardCharsets.UTF_8)));
                    NodeList releaseNodes = doc.getElementsByTagName("release");
                    if (releaseNodes.getLength() > 0) {
                        return releaseNodes.item(0).getTextContent().trim();
                    }
                    NodeList latestNodes = doc.getElementsByTagName("latest");
                    if (latestNodes.getLength() > 0) {
                        return latestNodes.item(0).getTextContent().trim();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not retrieve live registry version for {}: {}", dep.getCoordinates(), e.getMessage());
        }
        return null;
    }

    private String fallbackLatestVersion(DependencyEntity dep) {
        String current = dep.getCurrentVersion();
        if (current == null || current.isEmpty() || "RELEASE".equalsIgnoreCase(current)) {
            return "3.3.4";
        }
        VersionComparator.ParsedVersion pv = new VersionComparator.ParsedVersion(current);
        if (dep.getName().toLowerCase().contains("spring")) {
            return "6.2.3";
        }
        if (dep.getName().toLowerCase().contains("express")) {
            return "4.21.2";
        }
        if (dep.getName().toLowerCase().contains("lodash")) {
            return "4.17.21";
        }
        if (dep.getName().toLowerCase().contains("axios")) {
            return "1.7.9";
        }
        if (dep.getName().toLowerCase().contains("requests")) {
            return "2.32.3";
        }
        if (dep.getName().toLowerCase().contains("flask")) {
            return "3.1.0";
        }
        if (dep.getName().toLowerCase().contains("junit")) {
            return "5.11.4";
        }
        return pv.major + "." + (pv.minor + 1) + ".0";
    }
}
