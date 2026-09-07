package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PythonRequirementsParser implements DependencyParser {

    private static final Pattern REQ_TXT_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9_.-]+)\\s*(?:([=><~^!]+)\\s*([a-zA-Z0-9_.-]+(?:\\.[a-zA-Z0-9_.-]+)*))?"
    );

    @Override
    public EcosystemType getSupportedEcosystem() {
        return EcosystemType.PYTHON;
    }

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String normalized = filePath.toLowerCase();
        return normalized.endsWith("requirements.txt") ||
               normalized.endsWith("requirements-dev.txt") ||
               normalized.endsWith("pyproject.toml") ||
               normalized.endsWith("pipfile") ||
               normalized.endsWith("setup.py");
    }

    @Override
    public List<DependencyEntity> parse(String content, String filePath, DependencyFileEntity fileEntity) {
        List<DependencyEntity> dependencies = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return dependencies;
        }

        String normalized = filePath != null ? filePath.toLowerCase() : "";
        if (normalized.endsWith("pyproject.toml")) {
            dependencies.addAll(parsePyProjectToml(content, fileEntity));
        } else {
            dependencies.addAll(parseRequirementsTxt(content, fileEntity));
        }

        return deduplicate(dependencies);
    }

    private List<DependencyEntity> parseRequirementsTxt(String content, DependencyFileEntity fileEntity) {
        List<DependencyEntity> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("-r") || line.startsWith("-i") || line.startsWith("--")) {
                    continue;
                }
                
                // Strip inline comment
                int commentIndex = line.indexOf('#');
                if (commentIndex >= 0) {
                    line = line.substring(0, commentIndex).trim();
                }

                // Strip environment markers: requests>=2.25.0; python_version >= '3.6'
                int markerIndex = line.indexOf(';');
                if (markerIndex >= 0) {
                    line = line.substring(0, markerIndex).trim();
                }

                Matcher matcher = REQ_TXT_PATTERN.matcher(line);
                if (matcher.find()) {
                    String pkgName = matcher.group(1);
                    String op = matcher.group(2);
                    String version = matcher.group(3);

                    if (pkgName != null && !pkgName.isEmpty()) {
                        String declared = (op != null && version != null) ? (op + version) : (version != null ? version : "*");
                        String cleaned = version != null ? cleanVersion(version) : "1.0.0";

                        DependencyEntity entity = new DependencyEntity();
                        entity.setDependencyFile(fileEntity);
                        entity.setEcosystem(EcosystemType.PYTHON);
                        entity.setName(pkgName.trim().toLowerCase());
                        entity.setGroupOrNamespace(null);
                        entity.setDeclaredVersionRange(declared);
                        entity.setCurrentVersion(cleaned);
                        entity.setResolvedVersion(cleaned);
                        entity.setLockfileSource("requirements.txt");
                        entity.setScope("install");
                        entity.setDirect(true);
                        entity.setDev(false);
                        list.add(entity);
                    }
                }
            }
        } catch (Exception ignored) {}
        return list;
    }

    private List<DependencyEntity> parsePyProjectToml(String content, DependencyFileEntity fileEntity) {
        List<DependencyEntity> list = new ArrayList<>();
        Pattern tomlDepPattern = Pattern.compile("(?m)^\\s*([a-zA-Z0-9_.-]+)\\s*=\\s*[\"']([^\"']+)[\"']");
        Matcher matcher = tomlDepPattern.matcher(content);
        while (matcher.find()) {
            String pkg = matcher.group(1);
            String ver = matcher.group(2);
            if (!pkg.equalsIgnoreCase("name") && !pkg.equalsIgnoreCase("version") && !pkg.equalsIgnoreCase("description")) {
                DependencyEntity entity = new DependencyEntity();
                entity.setDependencyFile(fileEntity);
                entity.setEcosystem(EcosystemType.PYTHON);
                entity.setName(pkg.toLowerCase().trim());
                entity.setGroupOrNamespace(null);
                entity.setDeclaredVersionRange(ver != null ? ver.trim() : "*");
                entity.setCurrentVersion(cleanVersion(ver));
                entity.setResolvedVersion(cleanVersion(ver));
                entity.setLockfileSource("pyproject.toml");
                entity.setScope("install");
                entity.setDirect(true);
                list.add(entity);
            }
        }
        return list;
    }

    private String cleanVersion(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "1.0.0";
        return raw.replaceAll("^[=><~^! ]+", "").split(",")[0].trim();
    }

    private List<DependencyEntity> deduplicate(List<DependencyEntity> list) {
        Map<String, DependencyEntity> map = new LinkedHashMap<>();
        for (DependencyEntity d : list) {
            String key = d.getCoordinates().toLowerCase();
            map.putIfAbsent(key, d);
        }
        return new ArrayList<>(map.values());
    }
}
