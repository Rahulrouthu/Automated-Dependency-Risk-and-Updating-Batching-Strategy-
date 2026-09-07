package com.capstone.dependencyrisk.analyzer.lockfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NpmShrinkwrapJsonParser implements LockfileParser {

    private final PackageLockJsonParser delegate;

    @Autowired
    public NpmShrinkwrapJsonParser(PackageLockJsonParser delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean canHandle(String filePath) {
        if (filePath == null) return false;
        String lower = filePath.toLowerCase();
        return lower.endsWith("npm-shrinkwrap.json");
    }

    @Override
    public String getLockfileType() {
        return "npm-shrinkwrap.json";
    }

    @Override
    public Map<String, String> parseResolvedVersions(String content) {
        return delegate.parseResolvedVersions(content);
    }
}
