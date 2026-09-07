package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.EcosystemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ParserFactory {

    private final List<DependencyParser> parsers;

    @Autowired
    public ParserFactory(List<DependencyParser> parsers) {
        this.parsers = parsers;
    }

    public Optional<DependencyParser> getParserForFile(String filePath) {
        if (filePath == null) return Optional.empty();
        return parsers.stream()
                .filter(p -> p.canHandle(filePath))
                .findFirst();
    }

    public Optional<DependencyParser> getParserForEcosystem(EcosystemType ecosystem) {
        if (ecosystem == null) return Optional.empty();
        return parsers.stream()
                .filter(p -> p.getSupportedEcosystem() == ecosystem)
                .findFirst();
    }
}
