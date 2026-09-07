package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repositories")
public class RepositoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "default_branch")
    private String defaultBranch = "main";

    @Column(name = "detected_ecosystems")
    private String detectedEcosystems;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_scanned_at")
    private LocalDateTime lastScannedAt;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DependencyFileEntity> dependencyFiles = new ArrayList<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DependencyBatchEntity> batches = new ArrayList<>();

    public RepositoryEntity() {}

    public RepositoryEntity(String owner, String name, String url, String defaultBranch) {
        this.owner = owner;
        this.name = name;
        this.url = url;
        this.defaultBranch = defaultBranch;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }

    public String getDetectedEcosystems() { return detectedEcosystems; }
    public void setDetectedEcosystems(String detectedEcosystems) { this.detectedEcosystems = detectedEcosystems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastScannedAt() { return lastScannedAt; }
    public void setLastScannedAt(LocalDateTime lastScannedAt) { this.lastScannedAt = lastScannedAt; }

    public List<DependencyFileEntity> getDependencyFiles() { return dependencyFiles; }
    public void setDependencyFiles(List<DependencyFileEntity> dependencyFiles) { this.dependencyFiles = dependencyFiles; }

    public List<DependencyBatchEntity> getBatches() { return batches; }
    public void setBatches(List<DependencyBatchEntity> batches) { this.batches = batches; }
}
