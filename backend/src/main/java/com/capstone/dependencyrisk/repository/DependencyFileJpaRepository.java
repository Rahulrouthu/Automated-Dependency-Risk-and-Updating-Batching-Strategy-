package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependencyFileJpaRepository extends JpaRepository<DependencyFileEntity, Long> {
    List<DependencyFileEntity> findByRepositoryId(Long repositoryId);
}
