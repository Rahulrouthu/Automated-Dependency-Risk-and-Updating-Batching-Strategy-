package com.capstone.dependencyrisk.analyzer.lockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YarnLockParser implements LockfileParser {

    private static final Logger log = LoggerFactory.getLogger(YarnLockParser.class);
    private static final Pattern YARN_HEADER_PATTERN = Pattern.compile("^\"?(@?[a-zA-Z0-9_.-]+(?:/[a-zA-Z0-9_.-]+)?)@[^\":]+(?:\"?|:.*)$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\s*version\\s+[\"']?([^\"'\r\n]+)[\"']?");

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String lower = filePath.toLowerCase();
        return lower.endsWith("yarn.lock");
    }

    @Override
    public String getLockfileType() {
        return "yarn.lock";
    }

    @Override
    public Map<String, String> parseResolvedVersions(String content) {
        Map<String, String> resolved = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            return resolved;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            List<String> currentPackages = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                // If line does not start with whitespace, it is an entry header:
                // e.g. "axios@^1.6.0", "axios@1.6.0":
                if (!line.startsWith(" ") && !line.startsWith("\t")) {
                    currentPackages.clear();
                    String[] entries = line.split(",");
                    for (String entry : entries) {
                        String cleanEntry = entry.trim().replace("\"", "").replace(":", "");
                        // Extract package name before @ (handling scoped packages like @angular/core@^1.0.0)
                        int atIndex = cleanEntry.lastIndexOf('@');
                        if (atIndex > 0) {
                            String pkgName = cleanEntry.substring(0, atIndex).trim();
                            if (!pkgName.isEmpty()) {
                                currentPackages.add(pkgName);
                            }
                        }
                    }
                } else if (trimmed.startsWith("version")) {
                    Matcher m = VERSION_PATTERN.matcher(trimmed);
                    if (m.find()) {
                        String ver = m.group(1).trim();
                        for (String pkg : currentPackages) {
                            resolved.putIfAbsent(pkg, ver);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse yarn.lock: {}", e.getMessage());
        }

        return resolved;
    }
}
