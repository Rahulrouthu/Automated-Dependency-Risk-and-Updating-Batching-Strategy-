package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dependency_files")
public class DependencyFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private RepositoryEntity repository;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EcosystemType ecosystem;

    @Column(name = "dependency_count")
    private int dependencyCount = 0;

    @OneToMany(mappedBy = "dependencyFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DependencyEntity> dependencies = new ArrayList<>();

    public DependencyFileEntity() {}

    public DependencyFileEntity(RepositoryEntity repository, String filePath, EcosystemType ecosystem) {
        this.repository = repository;
        this.filePath = filePath;
        this.ecosystem = ecosystem;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepositoryEntity getRepository() { return repository; }
    public void setRepository(RepositoryEntity repository) { this.repository = repository; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public EcosystemType getEcosystem() { return ecosystem; }
    public void setEcosystem(EcosystemType ecosystem) { this.ecosystem = ecosystem; }

    public int getDependencyCount() { return dependencyCount; }
    public void setDependencyCount(int dependencyCount) { this.dependencyCount = dependencyCount; }

    public List<DependencyEntity> getDependencies() { return dependencies; }
    public void setDependencies(List<DependencyEntity> dependencies) { this.dependencies = dependencies; }
}
