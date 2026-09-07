package com.capstone.dependencyrisk.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class GitHubUrlValidationTest {

    private GitHubApiClient client;

    @BeforeEach
    public void setUp() {
        client = new GitHubApiClient(new RestTemplate());
    }

    @Test
    public void testValidGitHubUrls() {
        GitHubApiClient.RepoDetails r1 = client.parseGitHubUrl("https://github.com/spring-projects/spring-petclinic");
        assertEquals("spring-projects", r1.owner);
        assertEquals("spring-petclinic", r1.name);
        assertEquals("main", r1.branch);

        GitHubApiClient.RepoDetails r2 = client.parseGitHubUrl("https://github.com/expressjs/express.git");
        assertEquals("expressjs", r2.owner);
        assertEquals("express", r2.name);

        GitHubApiClient.RepoDetails r3 = client.parseGitHubUrl("https://github.com/psf/requests/tree/develop");
        assertEquals("psf", r3.owner);
        assertEquals("requests", r3.name);
        assertEquals("develop", r3.branch);
    }

    @Test
    public void testInvalidGitHubUrls() {
        assertThrows(IllegalArgumentException.class, () -> client.parseGitHubUrl(""));
        assertThrows(IllegalArgumentException.class, () -> client.parseGitHubUrl("   "));
        assertThrows(IllegalArgumentException.class, () -> client.parseGitHubUrl("https://gitlab.com/owner/repo"));
        assertThrows(IllegalArgumentException.class, () -> client.parseGitHubUrl("not-a-url"));
    }
}
