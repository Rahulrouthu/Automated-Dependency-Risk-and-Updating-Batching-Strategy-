package com.capstone.dependencyrisk.entity;

public enum VersionDiffType {
    MAJOR("Major Upgrade (Breaking)", 3),
    MINOR("Minor Feature Update", 2),
    PATCH("Patch / Bugfix Update", 1),
    UP_TO_DATE("Up to Date", 0),
    DOWNGRADE("Downgrade", -1),
    UNKNOWN("Unknown / Non-SemVer", 0);

    private final String description;
    private final int severityRank;

    VersionDiffType(String description, int severityRank) {
        this.description = description;
        this.severityRank = severityRank;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverityRank() {
        return severityRank;
    }
}
