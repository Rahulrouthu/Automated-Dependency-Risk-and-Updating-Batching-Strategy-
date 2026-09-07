package com.capstone.dependencyrisk.entity;

public enum EcosystemType {
    MAVEN("Maven", "pom.xml"),
    NPM("npm / Node.js", "package.json"),
    PYTHON("Python", "requirements.txt"),
    GRADLE("Gradle", "build.gradle");

    private final String displayName;
    private final String primaryManifest;

    EcosystemType(String displayName, String primaryManifest) {
        this.displayName = displayName;
        this.primaryManifest = primaryManifest;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPrimaryManifest() {
        return primaryManifest;
    }
}
