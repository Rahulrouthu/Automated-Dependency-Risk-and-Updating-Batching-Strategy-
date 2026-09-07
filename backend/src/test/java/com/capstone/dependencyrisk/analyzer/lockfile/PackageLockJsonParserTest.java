package com.capstone.dependencyrisk.analyzer.lockfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PackageLockJsonParserTest {

    private PackageLockJsonParser parser;

    @BeforeEach
    public void setUp() {
        parser = new PackageLockJsonParser();
    }

    @Test
    public void testCanHandle() {
        assertTrue(parser.canHandle("package-lock.json"));
        assertTrue(parser.canHandle("/path/to/package-lock.json"));
        assertFalse(parser.canHandle("package.json"));
        assertFalse(parser.canHandle("yarn.lock"));
    }

    @Test
    public void testParseV2V3PackagesLockfile() {
        String lockfile = "{\n" +
                "  \"name\": \"test-app\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"lockfileVersion\": 2,\n" +
                "  \"packages\": {\n" +
                "    \"\": {\n" +
                "      \"name\": \"test-app\",\n" +
                "      \"version\": \"1.0.0\"\n" +
                "    },\n" +
                "    \"node_modules/axios\": {\n" +
                "      \"version\": \"1.6.7\",\n" +
                "      \"resolved\": \"https://registry.npmjs.org/axios/-/axios-1.6.7.tgz\"\n" +
                "    },\n" +
                "    \"node_modules/@angular/core\": {\n" +
                "      \"version\": \"17.0.5\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, String> resolved = parser.parseResolvedVersions(lockfile);
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        assertEquals("1.6.7", resolved.get("axios"));
        assertEquals("17.0.5", resolved.get("@angular/core"));
    }

    @Test
    public void testParseV1DependenciesLockfile() {
        String lockfile = "{\n" +
                "  \"name\": \"test-app-v1\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"lockfileVersion\": 1,\n" +
                "  \"dependencies\": {\n" +
                "    \"lodash\": {\n" +
                "      \"version\": \"4.17.21\"\n" +
                "    },\n" +
                "    \"express\": {\n" +
                "      \"version\": \"4.18.2\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Map<String, String> resolved = parser.parseResolvedVersions(lockfile);
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        assertEquals("4.17.21", resolved.get("lodash"));
        assertEquals("4.18.2", resolved.get("express"));
    }
}
