package com.capstone.dependencyrisk.analyzer.lockfile;

import com.capstone.dependencyrisk.entity.DependencyEntity;
import com.capstone.dependencyrisk.entity.EcosystemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ResolvedDependencyMergerTest {

    private ResolvedDependencyMerger merger;

    @BeforeEach
    public void setUp() {
        PackageLockJsonParser pkgLock = new PackageLockJsonParser();
        NpmShrinkwrapJsonParser shrinkwrap = new NpmShrinkwrapJsonParser(pkgLock);
        YarnLockParser yarn = new YarnLockParser();
        PnpmLockParser pnpm = new PnpmLockParser();
        merger = new ResolvedDependencyMerger(Arrays.asList(pkgLock, shrinkwrap, pnpm, yarn));
    }

    @Test
    public void testMergeResolvedVersionFromPackageLock() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("axios");
        dep.setEcosystem(EcosystemType.NPM);
        dep.setDeclaredVersionRange("^1.6.0");
        dep.setCurrentVersion("1.6.0");

        String lockContent = "{\n" +
                "  \"packages\": {\n" +
                "    \"node_modules/axios\": { \"version\": \"1.6.7\" }\n" +
                "  }\n" +
                "}";

        Map<String, String> lockfiles = Collections.singletonMap("package-lock.json", lockContent);
        merger.mergeResolvedVersions(Collections.singletonList(dep), lockfiles);

        assertEquals("^1.6.0", dep.getDeclaredVersionRange());
        assertEquals("1.6.7", dep.getResolvedVersion());
        assertEquals("1.6.7", dep.getCurrentVersion());
        assertEquals("package-lock.json", dep.getLockfileSource());
    }

    @Test
    public void testUnknownResolvedWhenNoLockfile() {
        DependencyEntity dep = new DependencyEntity();
        dep.setName("axios");
        dep.setEcosystem(EcosystemType.NPM);
        dep.setDeclaredVersionRange("^1.6.0");
        dep.setCurrentVersion("1.6.0");

        merger.mergeResolvedVersions(Collections.singletonList(dep), Collections.emptyMap());

        assertEquals("^1.6.0", dep.getDeclaredVersionRange());
        assertEquals("UNKNOWN", dep.getResolvedVersion());
        assertEquals("NONE", dep.getLockfileSource());
    }
}
