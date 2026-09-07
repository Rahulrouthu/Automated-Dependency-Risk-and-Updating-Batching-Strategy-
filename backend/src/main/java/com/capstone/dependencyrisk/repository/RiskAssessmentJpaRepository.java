package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.RiskAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskAssessmentJpaRepository extends JpaRepository<RiskAssessmentEntity, Long> {
    Optional<RiskAssessmentEntity> findByDependencyId(Long dependencyId);
}
