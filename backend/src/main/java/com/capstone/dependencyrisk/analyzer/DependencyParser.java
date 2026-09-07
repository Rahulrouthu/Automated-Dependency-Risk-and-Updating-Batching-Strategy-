package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.DependencyFileEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;

import java.util.List;

public interface DependencyParser {
    EcosystemType getSupportedEcosystem();
    boolean canHandle(String filePath);
    List<DependencyEntity> parse(String content, String filePath, DependencyFileEntity fileEntity);
}
