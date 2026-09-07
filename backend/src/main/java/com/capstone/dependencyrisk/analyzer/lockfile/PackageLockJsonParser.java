package com.capstone.dependencyrisk.analyzer.lockfile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class PackageLockJsonParser implements LockfileParser {

    private static final Logger log = LoggerFactory.getLogger(PackageLockJsonParser.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String lower = filePath.toLowerCase();
        return lower.endsWith("package-lock.json");
    }

    @Override
    public String getLockfileType() {
        return "package-lock.json";
    }

    @Override
    public Map<String, String> parseResolvedVersions(String content) {
        Map<String, String> resolved = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            return resolved;
        }

        try {
            JsonNode root = objectMapper.readTree(content);

            // 1. Check v2 / v3 format: "packages" object
            if (root.has("packages") && root.get("packages").isObject()) {
                JsonNode packagesNode = root.get("packages");
                Iterator<Map.Entry<String, JsonNode>> fields = packagesNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String key = entry.getKey(); // e.g. "node_modules/axios" or "node_modules/@angular/core"
                    JsonNode pkgDetails = entry.getValue();

                    if (key != null && key.contains("node_modules/") && pkgDetails.has("version")) {
                        String rawPkgName = key.substring(key.lastIndexOf("node_modules/") + "node_modules/".length());
                        String version = pkgDetails.get("version").asText().trim();
                        if (!rawPkgName.isEmpty() && !version.isEmpty()) {
                            resolved.putIfAbsent(rawPkgName, version);
                        }
                    }
                }
            }

            // 2. Check v1 format (or v2 fallback): "dependencies" object
            if (root.has("dependencies") && root.get("dependencies").isObject()) {
                parseDependenciesObject(root.get("dependencies"), resolved);
            }

        } catch (Exception e) {
            log.warn("Failed to parse package-lock.json: {}", e.getMessage());
        }

        return resolved;
    }

    private void parseDependenciesObject(JsonNode node, Map<String, String> resolved) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String pkgName = entry.getKey();
            JsonNode depInfo = entry.getValue();

            if (depInfo.has("version")) {
                String version = depInfo.get("version").asText().trim();
                if (!pkgName.isEmpty() && !version.isEmpty()) {
                    resolved.putIfAbsent(pkgName, version);
                }
            }

            // Recurse for nested dependencies if any
            if (depInfo.has("dependencies") && depInfo.get("dependencies").isObject()) {
                parseDependenciesObject(depInfo.get("dependencies"), resolved);
            }
        }
    }
}
