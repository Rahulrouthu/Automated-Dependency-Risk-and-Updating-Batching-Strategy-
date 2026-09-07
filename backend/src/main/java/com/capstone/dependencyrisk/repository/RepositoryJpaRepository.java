package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryJpaRepository extends JpaRepository<RepositoryEntity, Long> {
    Optional<RepositoryEntity> findByOwnerAndName(String owner, String name);
    Optional<RepositoryEntity> findByUrl(String url);
}
