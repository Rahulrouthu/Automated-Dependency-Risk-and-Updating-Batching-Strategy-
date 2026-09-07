package com.capstone.dependencyrisk.repository;

import com.capstone.dependencyrisk.entity.BatchItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchItemJpaRepository extends JpaRepository<BatchItemEntity, Long> {

    @Modifying
    @Query("DELETE FROM BatchItemEntity b WHERE b.batch.repository.id = :repoId")
    void deleteByRepositoryId(@Param("repoId") Long repoId);
}
