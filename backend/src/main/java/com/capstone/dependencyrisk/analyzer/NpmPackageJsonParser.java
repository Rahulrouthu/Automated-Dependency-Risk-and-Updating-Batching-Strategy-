package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NpmPackageJsonParser implements DependencyParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public EcosystemType getSupportedEcosystem() {
        return EcosystemType.NPM;
    }

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String normalized = filePath.toLowerCase();
        return normalized.endsWith("package.json");
    }

    @Override
    public List<DependencyEntity> parse(String content, String filePath, DependencyFileEntity fileEntity) {
        List<DependencyEntity> dependencies = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return dependencies;
        }

        try {
            JsonNode root = objectMapper.readTree(content);

            // 1. Direct dependencies
            if (root.has("dependencies") && root.get("dependencies").isObject()) {
                JsonNode deps = root.get("dependencies");
                deps.fieldNames().forEachRemaining(pkgName -> {
                    String rawVersion = deps.get(pkgName).asText();
                    dependencies.add(buildEntity(pkgName, rawVersion, "production", true, false, fileEntity));
                });
            }

            // 2. Dev dependencies
            if (root.has("devDependencies") && root.get("devDependencies").isObject()) {
                JsonNode devDeps = root.get("devDependencies");
                devDeps.fieldNames().forEachRemaining(pkgName -> {
                    String rawVersion = devDeps.get(pkgName).asText();
                    dependencies.add(buildEntity(pkgName, rawVersion, "development", true, true, fileEntity));
                });
            }

            // 3. Peer dependencies
            if (root.has("peerDependencies") && root.get("peerDependencies").isObject()) {
                JsonNode peerDeps = root.get("peerDependencies");
                peerDeps.fieldNames().forEachRemaining(pkgName -> {
                    String rawVersion = peerDeps.get(pkgName).asText();
                    dependencies.add(buildEntity(pkgName, rawVersion, "peer", true, false, fileEntity));
                });
            }

        } catch (Exception e) {
            // Robust regex fallback
            dependencies.addAll(parseNpmWithRegex(content, fileEntity));
        }

        return deduplicate(dependencies);
    }

    private DependencyEntity buildEntity(String rawPkgName, String rawVersion, String scope, boolean isDirect, boolean isDev, DependencyFileEntity fileEntity) {
        DependencyEntity entity = new DependencyEntity();
        entity.setDependencyFile(fileEntity);
        entity.setEcosystem(EcosystemType.NPM);

        // Handle scoped npm packages: @angular/core -> group: @angular, name: core
        if (rawPkgName.startsWith("@") && rawPkgName.contains("/")) {
            String[] parts = rawPkgName.split("/", 2);
            entity.setGroupOrNamespace(parts[0]);
            entity.setName(parts[1]);
        } else {
            entity.setGroupOrNamespace(null);
            entity.setName(rawPkgName);
        }

        entity.setDeclaredVersionRange(rawVersion != null ? rawVersion.trim() : "*");
        // Initial fallback for currentVersion before lockfile resolution
        entity.setCurrentVersion(cleanSemVer(rawVersion));
        entity.setResolvedVersion("UNKNOWN");
        entity.setScope(scope);
        entity.setDirect(isDirect);
        entity.setDev(isDev);
        return entity;
    }

    private String cleanSemVer(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "1.0.0";
        String cleaned = raw.trim()
                .replaceAll("^[\\^~>=<| ]+", "")
                .replaceAll("^[vV]", "")
                .split(" ")[0]
                .trim();
        return cleaned.isEmpty() ? "1.0.0" : cleaned;
    }

    private List<DependencyEntity> parseNpmWithRegex(String content, DependencyFileEntity fileEntity) {
        List<DependencyEntity> list = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"(@?[a-zA-Z0-9_.-]+(?:/[a-zA-Z0-9_.-]+)?)\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String pkg = matcher.group(1);
            String ver = matcher.group(2);
            if (!pkg.equalsIgnoreCase("name") && !pkg.equalsIgnoreCase("version") && !pkg.equalsIgnoreCase("description")) {
                list.add(buildEntity(pkg, ver, "dependencies", true, false, fileEntity));
            }
        }
        return list;
    }

    private List<DependencyEntity> deduplicate(List<DependencyEntity> list) {
        Map<String, DependencyEntity> map = new LinkedHashMap<>();
        for (DependencyEntity d : list) {
            String key = d.getCoordinates();
            map.putIfAbsent(key, d);
        }
        return new ArrayList<>(map.values());
    }
}
