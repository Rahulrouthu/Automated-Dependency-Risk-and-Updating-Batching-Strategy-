package com.capstone.dependencyrisk.risk;

import com.capstone.dependencyrisk.entity.VersionDiffType;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VersionComparator {

    private static final Pattern SEMVER_PATTERN = Pattern.compile(
            "^[vV]?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[.\\-]([a-zA-Z0-9.\\-]+))?$"
    );

    public static class ParsedVersion implements Comparable<ParsedVersion> {
        public int major = 0;
        public int minor = 0;
        public int patch = 0;
        public String preRelease = null;
        public String raw;

        public ParsedVersion(String raw) {
            this.raw = raw != null ? raw.trim() : "0.0.0";
            parse();
        }

        private void parse() {
            String cleaned = raw.replaceAll("^[vV^~>=<]+", "").split("[+ ]")[0].trim();
            Matcher matcher = SEMVER_PATTERN.matcher(cleaned);
            if (matcher.find()) {
                try {
                    if (matcher.group(1) != null) major = Integer.parseInt(matcher.group(1));
                    if (matcher.group(2) != null) minor = Integer.parseInt(matcher.group(2));
                    if (matcher.group(3) != null) patch = Integer.parseInt(matcher.group(3));
                    if (matcher.group(4) != null) preRelease = matcher.group(4);
                } catch (NumberFormatException ignored) {}
            }
        }

        @Override
        public int compareTo(ParsedVersion o) {
            if (this.major != o.major) return Integer.compare(this.major, o.major);
            if (this.minor != o.minor) return Integer.compare(this.minor, o.minor);
            if (this.patch != o.patch) return Integer.compare(this.patch, o.patch);
            if (this.preRelease == null && o.preRelease != null) return 1; // 1.0.0 > 1.0.0-rc1
            if (this.preRelease != null && o.preRelease == null) return -1;
            return 0;
        }
    }

    public VersionDiffType compareVersions(String currentVer, String targetVer) {
        if (currentVer == null || targetVer == null) {
            return VersionDiffType.UNKNOWN;
        }

        String curClean = currentVer.trim();
        String tgtClean = targetVer.trim();

        if (curClean.equalsIgnoreCase(tgtClean)) {
            return VersionDiffType.UP_TO_DATE;
        }

        ParsedVersion cur = new ParsedVersion(curClean);
        ParsedVersion tgt = new ParsedVersion(tgtClean);

        int cmp = tgt.compareTo(cur);
        if (cmp < 0) {
            return VersionDiffType.DOWNGRADE;
        }
        if (cmp == 0) {
            return VersionDiffType.UP_TO_DATE;
        }

        if (tgt.major > cur.major) {
            return VersionDiffType.MAJOR;
        }
        if (tgt.minor > cur.minor) {
            return VersionDiffType.MINOR;
        }
        if (tgt.patch > cur.patch) {
            return VersionDiffType.PATCH;
        }

        return VersionDiffType.PATCH;
    }

    public boolean isMajorJump(String currentVer, String targetVer) {
        return compareVersions(currentVer, targetVer) == VersionDiffType.MAJOR;
    }
}
