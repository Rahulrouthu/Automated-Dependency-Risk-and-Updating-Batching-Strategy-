package com.capstone.dependencyrisk.analyzer;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PythonRequirementsParserTest {

    private PythonRequirementsParser parser;

    @BeforeEach
    public void setUp() {
        parser = new PythonRequirementsParser();
    }

    @Test
    public void testParseRequirementsTxt() {
        String reqContent = "# Core dependencies\n" +
                "requests==2.31.0\n" +
                "flask>=2.2.0\n" +
                "pydantic~=1.10.0 # data validation\n";

        List<DependencyEntity> list = parser.parse(reqContent, "requirements.txt", null);
        assertNotNull(list);
        assertEquals(3, list.size());

        DependencyEntity reqs = list.stream().filter(d -> d.getName().equals("requests")).findFirst().orElse(null);
        assertNotNull(reqs);
        assertEquals("2.31.0", reqs.getCurrentVersion());
        assertEquals(EcosystemType.PYTHON, reqs.getEcosystem());
    }
}
