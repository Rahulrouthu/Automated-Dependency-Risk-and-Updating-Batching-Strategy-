package com.capstone.dependencyrisk.analyzer.lockfile;

import java.util.Map;

public interface LockfileParser {

    /**
     * Checks if this parser can handle the given file path.
     */
    boolean canHandle(String filePath);

    /**
     * Parses the lockfile content and returns a map of package name/coordinates -> resolved exact version.
     */
    Map<String, String> parseResolvedVersions(String content);

    /**
     * Lockfile format name (e.g., package-lock.json, yarn.lock, pnpm-lock.yaml).
     */
    String getLockfileType();
}
