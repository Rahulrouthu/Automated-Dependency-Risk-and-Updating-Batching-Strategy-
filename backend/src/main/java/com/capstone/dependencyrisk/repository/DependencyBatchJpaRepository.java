package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.DependencyBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependencyBatchJpaRepository extends JpaRepository<DependencyBatchEntity, Long> {
    
    List<DependencyBatchEntity> findByRepositoryIdOrderByBatchNumberAsc(Long repositoryId);

    @Modifying
    @Query("DELETE FROM DependencyBatchEntity b WHERE b.repository.id = :repoId")
    void deleteByRepositoryId(@Param("repoId") Long repoId);
}
