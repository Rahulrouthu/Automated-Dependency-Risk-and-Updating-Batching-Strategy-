package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GradleBuildParser implements DependencyParser {

    // Matches: implementation 'org.springframework.boot:spring-boot-starter-web:3.2.0'
    // Matches: implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    // Matches: api 'com.google.guava:guava:31.0-jre'
    private static final Pattern GRADLE_COORD_PATTERN = Pattern.compile(
            "(implementation|api|compileOnly|runtimeOnly|testImplementation|testRuntimeOnly)\\s*\\(?\\s*['\"]([\\w.\\-]+):([\\w.\\-]+)(?::([\\w.\\-${}]+))?['\"]\\s*\\)?"
    );

    @Override
    public EcosystemType getSupportedEcosystem() {
        return EcosystemType.GRADLE;
    }

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String normalized = filePath.toLowerCase();
        return normalized.endsWith("build.gradle") || normalized.endsWith("build.gradle.kts");
    }

    @Override
    public List<DependencyEntity> parse(String content, String filePath, DependencyFileEntity fileEntity) {
        List<DependencyEntity> dependencies = new ArrayList<>();
        if (content == null || content.trim().isEmpty()) {
            return dependencies;
        }

        Matcher matcher = GRADLE_COORD_PATTERN.matcher(content);
        while (matcher.find()) {
            String scope = matcher.group(1);
            String group = matcher.group(2);
            String artifact = matcher.group(3);
            String version = matcher.group(4);

            String cleanedVer = version != null ? version.trim() : "1.0.0";

            DependencyEntity entity = new DependencyEntity();
            entity.setDependencyFile(fileEntity);
            entity.setEcosystem(EcosystemType.GRADLE);
            entity.setGroupOrNamespace(group);
            entity.setName(artifact);
            entity.setDeclaredVersionRange(version != null ? version.trim() : "1.0.0");
            entity.setCurrentVersion(cleanedVer);
            entity.setResolvedVersion(cleanedVer);
            entity.setLockfileSource("build.gradle");
            entity.setScope(scope);
            entity.setDirect(true);
            entity.setDev(scope != null && scope.toLowerCase().contains("test"));
            dependencies.add(entity);
        }

        return deduplicate(dependencies);
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
