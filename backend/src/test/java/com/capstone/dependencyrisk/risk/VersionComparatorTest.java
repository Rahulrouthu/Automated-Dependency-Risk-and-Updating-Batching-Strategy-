package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.entity.VersionDiffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VersionComparatorTest {

    private VersionComparator comparator;

    @BeforeEach
    public void setUp() {
        comparator = new VersionComparator();
    }

    @Test
    public void testMajorVersionDiff() {
        assertEquals(VersionDiffType.MAJOR, comparator.compareVersions("5.3.20", "6.0.0"));
        assertEquals(VersionDiffType.MAJOR, comparator.compareVersions("1.2.3", "2.0.0"));
    }

    @Test
    public void testMinorVersionDiff() {
        assertEquals(VersionDiffType.MINOR, comparator.compareVersions("1.2.3", "1.3.0"));
        assertEquals(VersionDiffType.MINOR, comparator.compareVersions("4.17.15", "4.18.0"));
    }

    @Test
    public void testPatchVersionDiff() {
        assertEquals(VersionDiffType.PATCH, comparator.compareVersions("1.2.3", "1.2.4"));
        assertEquals(VersionDiffType.PATCH, comparator.compareVersions("4.17.20", "4.17.21"));
    }

    @Test
    public void testUpToDate() {
        assertEquals(VersionDiffType.UP_TO_DATE, comparator.compareVersions("2.31.0", "2.31.0"));
    }
}
