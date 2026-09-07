package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "batch_items")
public class BatchItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private DependencyBatchEntity batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_id", nullable = false)
    private DependencyEntity dependency;

    @Column(name = "action_description")
    private String actionDescription;

    public BatchItemEntity() {}

    public BatchItemEntity(DependencyBatchEntity batch, DependencyEntity dependency, String actionDescription) {
        this.batch = batch;
        this.dependency = dependency;
        this.actionDescription = actionDescription;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DependencyBatchEntity getBatch() { return batch; }
    public void setBatch(DependencyBatchEntity batch) { this.batch = batch; }

    public DependencyEntity getDependency() { return dependency; }
    public void setDependency(DependencyEntity dependency) { this.dependency = dependency; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }
}
