package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MavenPomParserTest {

    private MavenPomParser parser;

    @BeforeEach
    public void setUp() {
        parser = new MavenPomParser();
    }

    @Test
    public void testParseMavenPomWithProperties() {
        String pomXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "  <groupId>com.example</groupId>\n" +
                "  <artifactId>test-app</artifactId>\n" +
                "  <version>1.0.0</version>\n" +
                "  <properties>\n" +
                "    <spring.version>5.3.20</spring.version>\n" +
                "  </properties>\n" +
                "  <dependencies>\n" +
                "    <dependency>\n" +
                "      <groupId>org.springframework</groupId>\n" +
                "      <artifactId>spring-core</artifactId>\n" +
                "      <version>${spring.version}</version>\n" +
                "    </dependency>\n" +
                "    <dependency>\n" +
                "      <groupId>junit</groupId>\n" +
                "      <artifactId>junit</artifactId>\n" +
                "      <version>4.13.2</version>\n" +
                "      <scope>test</scope>\n" +
                "    </dependency>\n" +
                "  </dependencies>\n" +
                "</project>";

        List<DependencyEntity> list = parser.parse(pomXml, "pom.xml", null);
        assertNotNull(list);
        assertEquals(2, list.size());

        DependencyEntity springDep = list.stream().filter(d -> d.getName().equals("spring-core")).findFirst().orElse(null);
        assertNotNull(springDep);
        assertEquals("5.3.20", springDep.getCurrentVersion());
        assertEquals("org.springframework", springDep.getGroupOrNamespace());
        assertEquals(EcosystemType.MAVEN, springDep.getEcosystem());

        DependencyEntity junitDep = list.stream().filter(d -> d.getName().equals("junit")).findFirst().orElse(null);
        assertNotNull(junitDep);
        assertTrue(junitDep.isDev());
    }
}
