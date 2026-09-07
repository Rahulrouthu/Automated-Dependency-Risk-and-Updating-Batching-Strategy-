package com.capstone.dependencyrisk.analyzer.lockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PnpmLockParser implements LockfileParser {

    private static final Logger log = LoggerFactory.getLogger(PnpmLockParser.class);
    // Matches '/@angular/core@17.0.0:' or '/axios@1.6.7:' or 'axios@1.6.7:'
    private static final Pattern PNPM_ENTRY_PATTERN = Pattern.compile("^\\s*['\"]?/?(@?[a-zA-Z0-9_.-]+(?:/[a-zA-Z0-9_.-]+)?)@([0-9a-zA-Z_.-]+)['\"]?:");

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String lower = filePath.toLowerCase();
        return lower.endsWith("pnpm-lock.yaml") || lower.endsWith("pnpm-lock.yml");
    }

    @Override
    public String getLockfileType() {
        return "pnpm-lock.yaml";
    }

    @Override
    public Map<String, String> parseResolvedVersions(String content) {
        Map<String, String> resolved = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            return resolved;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            boolean inPackagesSection = false;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.equals("packages:") || trimmed.startsWith("packages:")) {
                    inPackagesSection = true;
                    continue;
                }

                if (inPackagesSection) {
                    // Check if we exited packages section (top-level key with no indent)
                    if (!line.startsWith(" ") && !line.startsWith("\t") && !trimmed.isEmpty()) {
                        inPackagesSection = false;
                        continue;
                    }

                    Matcher m = PNPM_ENTRY_PATTERN.matcher(trimmed);
                    if (m.find()) {
                        String pkgName = m.group(1).trim();
                        String version = m.group(2).trim();
                        // Strip pnpm peer hash suffix e.g. 1.6.7(peer@x)
                        if (version.contains("(")) {
                            version = version.substring(0, version.indexOf("("));
                        }
                        if (!pkgName.isEmpty() && !version.isEmpty()) {
                            resolved.putIfAbsent(pkgName, version);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse pnpm-lock.yaml: {}", e.getMessage());
        }

        return resolved;
    }
}
