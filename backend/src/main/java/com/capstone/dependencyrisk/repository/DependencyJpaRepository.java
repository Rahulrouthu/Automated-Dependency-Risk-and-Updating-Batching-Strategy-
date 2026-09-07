package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependencyJpaRepository extends JpaRepository<DependencyEntity, Long> {
    
    @Query("SELECT d FROM DependencyEntity d JOIN d.dependencyFile df WHERE df.repository.id = :repoId")
    List<DependencyEntity> findByRepositoryId(@Param("repoId") Long repoId);

    @Query("SELECT d FROM DependencyEntity d JOIN d.dependencyFile df WHERE df.repository.id = :repoId AND d.ecosystem = :ecosystem")
    List<DependencyEntity> findByRepositoryIdAndEcosystem(@Param("repoId") Long repoId, @Param("ecosystem") EcosystemType ecosystem);
}
