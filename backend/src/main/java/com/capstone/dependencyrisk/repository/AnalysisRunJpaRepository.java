package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.AnalysisRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisRunJpaRepository extends JpaRepository<AnalysisRunEntity, Long> {
    List<AnalysisRunEntity> findByRepositoryIdOrderByStartedAtDesc(Long repositoryId);
    Optional<AnalysisRunEntity> findFirstByRepositoryIdOrderByStartedAtDesc(Long repositoryId);
}
