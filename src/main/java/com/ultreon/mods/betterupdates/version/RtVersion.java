package com.ultreon.mods.betterupdates.version;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class RtVersion implements Version {
    private final int version;
    private final int release;
    private final Stage stage;
    private final int stageRelease;
    private boolean devTest;

    public static final RtVersion EMPTY = new RtVersion(0, 0, 0, Stage.ALPHA, 0);

    /**
     * @param s the version to parse.
     * @throws IllegalArgumentException when an invalid version has given.
     */
    public RtVersion(String s) {
        // String to be scanned to find the pattern.
        String pattern = "(\\d*)\\.(\\d*)-(a|alpha|b|beta|rc|pre|pre-release|r|release)(?:[-.]|)(\\d*)"; // 1.0-alpha4 // 5.4-release7

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(s);
        if (m.find()) {
            version = Integer.parseInt(m.group(1));
            release = Integer.parseInt(m.group(2));

            switch (m.group(3)) {
                case "alpha", "a" -> stage = Stage.ALPHA;
                case "beta", "b" -> stage = Stage.BETA;
                case "pre", "pre-release", "rc" -> stage = Stage.PRE;
                case "release", "r" -> stage = Stage.RELEASE;
                default -> throw new InternalError("Regex has invalid output.");
            }

            stageRelease = Integer.parseInt(m.group(4));
        } else {
            throw new IllegalArgumentException("Invalid version,");
        }
    }

    public RtVersion(int version, int release, String stage, int stageRelease) {
        this(version, release, stage, stageRelease, false);
    }

    public RtVersion(int version, int release, String stage, int stageRelease, boolean devTest) {
        this.version = version;
        this.release = release;
        this.devTest = devTest;
        switch (stage) {
            case "alpha", "a" -> this.stage = Stage.ALPHA;
            case "beta", "b" -> this.stage = Stage.BETA;
            case "pre", "rc" -> this.stage = Stage.PRE;
            case "release", "r" -> this.stage = Stage.RELEASE;
            default -> throw new InternalError("Invalid RandomThingz version stage!");
        }

        this.stageRelease = stageRelease;
    }

    public RtVersion(int version, int release, int buildNumber, Stage stage, int stageRelease) {
        this.version = version;
        this.release = release;
        this.stage = stage;
        this.stageRelease = stageRelease;
    }

    @Override
    public boolean isStable() {
        return stage == Stage.RELEASE;
    }

    @Override
    public boolean isUnstable() {
        return stage != Stage.RELEASE;
    }

    @Override
    public boolean isReallyUnstable() {
        return stage != Stage.RELEASE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append('.');
        sb.append(release).append('-');
        sb.append(stage.name().toLowerCase());
        sb.append(stageRelease);
        if (devTest) {
            sb.append("-DEVTEST");
        }
        return sb.toString();
    }

    public String toLocalizedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        sb.append('.');
        sb.append(release);

        switch (stage) {
            case ALPHA:
                sb.append("Alpha");
            case BETA:
                sb.append("Beta");
            case PRE:
                sb.append("Pre");
            case RELEASE:
                sb.append("Release");
            default:
                sb.append("UNKNOWN");
        }

        sb.append(' ');
        sb.append(stageRelease);
        if (devTest) {
            sb.append(" Dev-Test");
        }

        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull Version o) {
        if (!(o instanceof RtVersion version)) {
            throw new IllegalArgumentException("Can't compare other than RtVersion");
        }

        int versionCompare = Integer.compare(this.version, version.version);
        if (versionCompare == 0) {
            int releaseCompare = Integer.compare(this.release, version.release);
            if (releaseCompare == 0) {
                int stageCompare = Integer.compare(this.stage.ordinal(), version.stage.ordinal());
                if (stageCompare == 0) {
                    return Integer.compare(this.stageRelease, version.stageRelease);
                }
                return stageCompare;
            }
            return releaseCompare;
        }
        return versionCompare;
    }

    public int getVersion() {
        return version;
    }

    public int getRelease() {
        return release;
    }

    public Stage getStage() {
        return stage;
    }

    public int getStageRelease() {
        return stageRelease;
    }

    public enum Stage {
        ALPHA,
        BETA,
        PRE,
        RELEASE
    }
}
