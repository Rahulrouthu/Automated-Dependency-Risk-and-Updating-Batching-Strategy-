package com.capstone.dependencyrisk.github;

import com.capstone.dependencyrisk.exception.RepositoryAccessDeniedException;
import com.capstone.dependencyrisk.exception.RepositoryNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitHubApiClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubApiClient.class);

    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile(
            "https?://github\\.com/([a-zA-Z0-9_.-]+)/([a-zA-Z0-9_.-]+)(?:/tree/([a-zA-Z0-9_.-]+))?.*"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.github.token:}")
    private String defaultToken;

    @Autowired
    public GitHubApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static class RepoDetails {
        public String owner;
        public String name;
        public String branch;
        public String normalizedUrl;

        public RepoDetails(String owner, String name, String branch) {
            this.owner = owner;
            this.name = name;
            this.branch = branch;
            this.normalizedUrl = "https://github.com/" + owner + "/" + name;
        }
    }

    public static class ManifestEntry {
        public String path;
        public String rawDownloadUrl;
        public boolean isLockfile;

        public ManifestEntry(String path, String rawDownloadUrl) {
            this(path, rawDownloadUrl, false);
        }

        public ManifestEntry(String path, String rawDownloadUrl, boolean isLockfile) {
            this.path = path;
            this.rawDownloadUrl = rawDownloadUrl;
            this.isLockfile = isLockfile;
        }
    }

    public RepoDetails parseGitHubUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Repository URL cannot be blank.");
        }

        String cleaned = rawUrl.trim().replaceAll("\\.git$", "").replaceAll("/$", "");
        Matcher matcher = GITHUB_URL_PATTERN.matcher(cleaned);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub repository URL. Expected format: https://github.com/owner/repository");
        }

        String owner = matcher.group(1);
        String name = matcher.group(2);
        String branch = matcher.group(3);

        return new RepoDetails(owner, name, branch != null ? branch : "main");
    }

    public void validateRepositoryExistence(RepoDetails repo, String customToken) {
        String token = (customToken != null && !customToken.trim().isEmpty()) ? customToken : defaultToken;
        String repoInfoUrl = "https://api.github.com/repos/" + repo.owner + "/" + repo.name;
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(repoInfoUrl, HttpMethod.GET, entity, String.class);
            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                JsonNode root = objectMapper.readTree(res.getBody());
                if (root.has("default_branch") && (repo.branch == null || repo.branch.equals("main"))) {
                    repo.branch = root.get("default_branch").asText();
                }
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new RepositoryNotFoundException("GitHub repository '" + repo.owner + "/" + repo.name + "' was not found or is private.");
        } catch (HttpClientErrorException.Forbidden | HttpClientErrorException.Unauthorized e) {
            log.warn("GitHub API rate limit or access restriction for {}: {}", repo.name, e.getMessage());
            // Probing fallback will be attempted
        } catch (Exception e) {
            log.debug("GitHub repository validation notice: {}", e.getMessage());
        }
    }

    public List<ManifestEntry> discoverManifestFiles(RepoDetails repo, String customToken) {
        List<ManifestEntry> manifests = new ArrayList<>();
        String token = (customToken != null && !customToken.trim().isEmpty()) ? customToken : defaultToken;

        // Query GitHub Tree API
        String activeBranch = repo.branch != null ? repo.branch : "main";
        try {
            String apiUrl = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/git/trees/" + activeBranch + "?recursive=1";
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.has("tree") && root.get("tree").isArray()) {
                    for (JsonNode fileNode : root.get("tree")) {
                        String type = fileNode.has("type") ? fileNode.get("type").asText() : "";
                        String path = fileNode.has("path") ? fileNode.get("path").asText() : "";

                        if ("blob".equalsIgnoreCase(type)) {
                            if (isManifestFile(path)) {
                                String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/" + activeBranch + "/" + path;
                                manifests.add(new ManifestEntry(path, rawUrl, false));
                            } else if (isLockfile(path)) {
                                String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/" + activeBranch + "/" + path;
                                manifests.add(new ManifestEntry(path, rawUrl, true));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("GitHub Tree API not directly accessible for {}. Probing standard manifest paths: {}", repo.name, e.getMessage());
            manifests.addAll(probeStandardManifests(repo, token, activeBranch));
        }

        return manifests;
    }

    private List<ManifestEntry> probeStandardManifests(RepoDetails repo, String token, String branch) {
        List<ManifestEntry> found = new ArrayList<>();
        String[] candidateManifests = {
                "pom.xml", "backend/pom.xml", "server/pom.xml", "app/pom.xml", "api/pom.xml",
                "package.json", "frontend/package.json", "client/package.json", "web/package.json", "backend/package.json", "ui/package.json",
                "requirements.txt", "backend/requirements.txt", "api/requirements.txt", "server/requirements.txt", "app/requirements.txt",
                "pyproject.toml", "build.gradle", "build.gradle.kts"
        };
        String[] candidateLockfiles = {
                "package-lock.json", "frontend/package-lock.json", "client/package-lock.json",
                "npm-shrinkwrap.json", "yarn.lock", "pnpm-lock.yaml"
        };

        for (String file : candidateManifests) {
            String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/" + branch + "/" + file;
            if (checkFileAccessible(repo, file, rawUrl, token)) {
                found.add(new ManifestEntry(file, rawUrl, false));
            }
        }

        for (String lock : candidateLockfiles) {
            String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/" + branch + "/" + lock;
            if (checkFileAccessible(repo, lock, rawUrl, token)) {
                found.add(new ManifestEntry(lock, rawUrl, true));
            }
        }

        if (found.isEmpty() && !"master".equalsIgnoreCase(branch)) {
            for (String file : candidateManifests) {
                String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/master/" + file;
                if (checkFileAccessible(repo, file, rawUrl, token)) {
                    repo.branch = "master";
                    found.add(new ManifestEntry(file, rawUrl, false));
                }
            }
            for (String lock : candidateLockfiles) {
                String rawUrl = "https://raw.githubusercontent.com/" + repo.owner + "/" + repo.name + "/master/" + lock;
                if (checkFileAccessible(repo, lock, rawUrl, token)) {
                    found.add(new ManifestEntry(lock, rawUrl, true));
                }
            }
        }

        return found;
    }

    public boolean isManifestFile(String path) {
        if (path == null) return false;
        String lower = path.toLowerCase().replace('\\', '/');

        // Exclude test fixtures, build artifacts, examples, and deep demo subfolders
        if (lower.contains("node_modules/") || lower.contains("/dist/") || lower.contains("/build/") ||
            lower.contains("/fixtures/") || lower.contains("/fixture/") || lower.contains("/test/") ||
            lower.contains("/tests/") || lower.contains("/examples/") || lower.contains("/example/") ||
            lower.contains("/demo/") || lower.contains("/demos/")) {
            return false;
        }

        return lower.endsWith("pom.xml") ||
               lower.endsWith("package.json") ||
               lower.endsWith("requirements.txt") ||
               lower.endsWith("requirements-dev.txt") ||
               lower.endsWith("pyproject.toml") ||
               lower.endsWith("build.gradle") ||
               lower.endsWith("build.gradle.kts");
    }

    public boolean isLockfile(String path) {
        if (path == null) return false;
        String lower = path.toLowerCase().replace('\\', '/');

        if (lower.contains("node_modules/") || lower.contains("/dist/") || lower.contains("/fixtures/")) {
            return false;
        }

        return lower.endsWith("package-lock.json") ||
               lower.endsWith("npm-shrinkwrap.json") ||
               lower.endsWith("yarn.lock") ||
               lower.endsWith("pnpm-lock.yaml") ||
               lower.endsWith("pnpm-lock.yml");
    }

    private boolean checkFileAccessible(RepoDetails repo, String path, String rawUrl, String token) {
        // 1. Try GitHub Contents API
        try {
            String branch = repo.branch != null ? repo.branch : "main";
            String apiUrl = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/contents/" + path + "?ref=" + branch;
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(apiUrl, HttpMethod.HEAD, entity, String.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (Exception ignored) {}

        // 2. Try CDN URL
        try {
            String branch = repo.branch != null ? repo.branch : "main";
            String cdnUrl = "https://cdn.jsdelivr.net/gh/" + repo.owner + "/" + repo.name + "@" + branch + "/" + path;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Automated-Dependency-Risk-Analyzer/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(cdnUrl, HttpMethod.HEAD, entity, String.class);
            if (res.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (Exception ignored) {}

        // 3. Fallback to rawUrl check
        return checkUrlExists(rawUrl, token);
    }

    private boolean checkUrlExists(String url, String token) {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.HEAD, entity, String.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public String fetchFileContent(RepoDetails repo, String path, String rawUrl, String token) {
        String activeToken = (token != null && !token.trim().isEmpty()) ? token : defaultToken;

        // 1. Primary: GitHub Contents API with Base64 decode (immune to raw.githubusercontent DNS/timeout issues)
        if (repo != null && repo.owner != null && repo.name != null && path != null) {
            try {
                String branch = repo.branch != null ? repo.branch : "main";
                String apiUrl = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/contents/" + path + "?ref=" + branch;
                HttpHeaders headers = createHeaders(activeToken);
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<String> res = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    JsonNode node = objectMapper.readTree(res.getBody());
                    if (node.has("content")) {
                        String encoded = node.get("content").asText().replaceAll("\\s+", "");
                        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
                        return new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
                    }
                }
            } catch (Exception e) {
                log.debug("GitHub Contents API fetch failed for {}: {}", path, e.getMessage());
            }

            // 2. Secondary: Fast CDN Mirror
            try {
                String branch = repo.branch != null ? repo.branch : "main";
                String cdnUrl = "https://cdn.jsdelivr.net/gh/" + repo.owner + "/" + repo.name + "@" + branch + "/" + path;
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Automated-Dependency-Risk-Analyzer/1.0");
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<String> res = restTemplate.exchange(cdnUrl, HttpMethod.GET, entity, String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null && !res.getBody().trim().isEmpty()) {
                    return res.getBody();
                }
            } catch (Exception e) {
                log.debug("CDN mirror fetch failed for {}: {}", path, e.getMessage());
            }
        }

        // 3. Fallback to rawUrl
        if (rawUrl != null) {
            return fetchFileContent(rawUrl, activeToken);
        }
        return null;
    }

    public String fetchFileContent(String rawUrl, String token) {
        try {
            HttpHeaders headers = createHeaders(token);
            String activeToken = (token != null && !token.trim().isEmpty()) ? token : defaultToken;
            if (activeToken != null && !activeToken.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + activeToken.trim());
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> res = restTemplate.exchange(rawUrl, HttpMethod.GET, entity, String.class);
            return res.getBody();
        } catch (Exception e) {
            log.warn("Could not fetch file from {}: {}", rawUrl, e.getMessage());
            return null;
        }
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Automated-Dependency-Risk-Analyzer/1.0");
        String activeToken = (token != null && !token.trim().isEmpty()) ? token : defaultToken;
        if (activeToken != null && !activeToken.trim().isEmpty()) {
            headers.set("Authorization", "Bearer " + activeToken.trim());
        }
        return headers;
    }
}
