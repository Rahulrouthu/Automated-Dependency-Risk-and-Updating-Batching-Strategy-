package com.capstone.dependencyrisk.entity;

public enum BatchCategory {
    URGENT_SECURITY("Urgent Security Hotfix", "Dedicated high-priority batch targeting critical and high vulnerabilities."),
    SAFE_PATCHES("Safe Automated Patches", "Low-risk patch updates that can be safely grouped and auto-merged after CI passes."),
    MINOR_UPDATES("Feature & Minor Updates", "Backward-compatible minor releases requiring standard integration verification."),
    ISOLATED_MAJOR("Isolated Major Upgrades", "High-risk breaking major version upgrades isolated individually for deep migration.");

    private final String title;
    private final String description;

    BatchCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
