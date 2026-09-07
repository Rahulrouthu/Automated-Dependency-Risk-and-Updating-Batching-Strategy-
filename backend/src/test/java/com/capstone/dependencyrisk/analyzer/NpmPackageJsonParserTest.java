package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NpmPackageJsonParserTest {

    private NpmPackageJsonParser parser;

    @BeforeEach
    public void setUp() {
        parser = new NpmPackageJsonParser();
    }

    @Test
    public void testParseNpmPackageJson() {
        String packageJson = "{\n" +
                "  \"name\": \"my-app\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"dependencies\": {\n" +
                "    \"express\": \"^4.18.2\",\n" +
                "    \"@angular/core\": \"~15.2.0\"\n" +
                "  },\n" +
                "  \"devDependencies\": {\n" +
                "    \"jest\": \"^29.0.0\"\n" +
                "  }\n" +
                "}";

        List<DependencyEntity> list = parser.parse(packageJson, "package.json", null);
        assertNotNull(list);
        assertEquals(3, list.size());

        DependencyEntity express = list.stream().filter(d -> d.getName().equals("express")).findFirst().orElse(null);
        assertNotNull(express);
        assertEquals("4.18.2", express.getCurrentVersion());
        assertEquals("^4.18.2", express.getDeclaredVersionRange());
        assertEquals("UNKNOWN", express.getResolvedVersion());
        assertEquals(EcosystemType.NPM, express.getEcosystem());

        DependencyEntity angular = list.stream().filter(d -> d.getName().equals("core")).findFirst().orElse(null);
        assertNotNull(angular);
        assertEquals("@angular", angular.getGroupOrNamespace());
        assertEquals("15.2.0", angular.getCurrentVersion());
        assertEquals("~15.2.0", angular.getDeclaredVersionRange());
    }
}
