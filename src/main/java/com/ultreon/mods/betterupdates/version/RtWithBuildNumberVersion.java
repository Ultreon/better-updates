package com.ultreon.mods.betterupdates.version;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtWithBuildNumberVersion implements Version {
    private final int version;
    private final int release;
    private final int buildNumber;
    private final Stage stage;
    private final int stageRelease;

    private final boolean devTest;

    public static final RtWithBuildNumberVersion EMPTY = new RtWithBuildNumberVersion(0, 0, 0, Stage.ALPHA, 0);

    /**
     * @param s the version to parse.
     * @throws IllegalArgumentException when an invalid version has given.
     */
    public RtWithBuildNumberVersion(String s) {
        // String to be scanned to find the pattern.
        String pattern = "(\\d*)\\.(\\d*)\\.(\\d*)-(a|alpha|b|beta|rc|pre|pre-release|r|release)(?:[-.]|)(\\d*)"; // 1.0-alpha4 // 5.4-release7

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(s);
        if (m.find()) {
            version = Integer.parseInt(m.group(1));
            release = Integer.parseInt(m.group(2));
            buildNumber = Integer.parseInt(m.group(3));

            switch (m.group(4)) {
                case "alpha", "a" -> stage = Stage.ALPHA;
                case "beta", "b" -> stage = Stage.BETA;
                case "pre", "pre-release", "rc" -> stage = Stage.PRE;
                case "release", "r" -> stage = Stage.RELEASE;
                default -> throw new InternalError("Regex has invalid output.");
            }

            stageRelease = Integer.parseInt(m.group(5));
        } else {
            throw new IllegalArgumentException("Invalid version,");
        }
        devTest = false;
    }

    public RtWithBuildNumberVersion(int version, int release, int buildNumber, String stage, int stageRelease) {
        this(version, release, buildNumber, stage, stageRelease, false);;
    }

    public RtWithBuildNumberVersion(int version, int release, int buildNumber, String stage, int stageRelease, boolean devTest) {
        this.version = version;
        this.release = release;
        switch (stage) {
            case "alpha", "a" -> this.stage = Stage.ALPHA;
            case "beta", "b" -> this.stage = Stage.BETA;
            case "pre", "rc" -> this.stage = Stage.PRE;
            case "release", "r" -> this.stage = Stage.RELEASE;
            default -> throw new InternalError("Invalid RandomThingz version stage!");
        }

        this.stageRelease = stageRelease;
        this.buildNumber = buildNumber;
        this.devTest = devTest;
    }

    public RtWithBuildNumberVersion(int version, int release, int buildNumber, Stage stage, int stageRelease) {
        this(version, release, buildNumber, stage, stageRelease, false);
    }

    public RtWithBuildNumberVersion(int version, int release, int buildNumber, Stage stage, int stageRelease, boolean devTest) {
        this.version = version;
        this.release = release;
        this.stage = stage;
        this.stageRelease = stageRelease;
        this.buildNumber = buildNumber;
        this.devTest = devTest;
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
        sb.append(version);
        sb.append('.');
        sb.append(release);
        sb.append('.');
        sb.append(buildNumber);
        sb.append('-');
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
        sb.append(" (Build ");
        sb.append(buildNumber);
        sb.append(") ");

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
        if (!(o instanceof RtWithBuildNumberVersion version)) {
            throw new IllegalArgumentException("Can't compare other than QVersion");
        }

        return Integer.compare(this.buildNumber, version.buildNumber);
    }

    public int getVersion() {
        return version;
    }

    public int getRelease() {
        return release;
    }

    public int getBuildNumber() {
        return buildNumber;
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
