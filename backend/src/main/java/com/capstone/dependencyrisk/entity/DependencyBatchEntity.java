package com.capstone.dependencyrisk.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dependency_batches")
public class DependencyBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private RepositoryEntity repository;

    @Column(name = "batch_number", nullable = false)
    private int batchNumber;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BatchCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_risk_level", nullable = false)
    private RiskLevel batchRiskLevel;

    @Column(name = "batch_risk_score", nullable = false)
    private double batchRiskScore;

    @Column(name = "strategy_description", columnDefinition = "TEXT")
    private String strategyDescription;

    @Column(name = "reason_for_grouping", columnDefinition = "TEXT")
    private String reasonForGrouping;

    @Column(name = "execution_commands", columnDefinition = "TEXT")
    private String executionCommands;

    @Column(name = "pr_title")
    private String prTitle;

    @Column(name = "pr_body", columnDefinition = "TEXT")
    private String prBody;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BatchItemEntity> items = new ArrayList<>();

    public DependencyBatchEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepositoryEntity getRepository() { return repository; }
    public void setRepository(RepositoryEntity repository) { this.repository = repository; }

    public int getBatchNumber() { return batchNumber; }
    public void setBatchNumber(int batchNumber) { this.batchNumber = batchNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BatchCategory getCategory() { return category; }
    public void setCategory(BatchCategory category) { this.category = category; }

    public RiskLevel getBatchRiskLevel() { return batchRiskLevel; }
    public void setBatchRiskLevel(RiskLevel batchRiskLevel) { this.batchRiskLevel = batchRiskLevel; }

    public double getBatchRiskScore() { return batchRiskScore; }
    public void setBatchRiskScore(double batchRiskScore) { this.batchRiskScore = batchRiskScore; }

    public String getStrategyDescription() { return strategyDescription; }
    public void setStrategyDescription(String strategyDescription) { this.strategyDescription = strategyDescription; }

    public String getReasonForGrouping() { return reasonForGrouping; }
    public void setReasonForGrouping(String reasonForGrouping) { this.reasonForGrouping = reasonForGrouping; }

    public String getExecutionCommands() { return executionCommands; }
    public void setExecutionCommands(String executionCommands) { this.executionCommands = executionCommands; }

    public String getPrTitle() { return prTitle; }
    public void setPrTitle(String prTitle) { this.prTitle = prTitle; }

    public String getPrBody() { return prBody; }
    public void setPrBody(String prBody) { this.prBody = prBody; }

    public List<BatchItemEntity> getItems() { return items; }
    public void setItems(List<BatchItemEntity> items) { this.items = items; }
}
