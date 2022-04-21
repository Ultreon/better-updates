package com.ultreon.mods.betterupdates.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UltreonVersion implements Version {
    private final int version;
    private final int release;
    private final int bugfix;
    @Nullable
    private final Stage stage;
    private int devTestId = 0;
    private final boolean devTest;

    public static final UltreonVersion EMPTY = new UltreonVersion(0, 0, 0, 1);

    /**
     * @param s the version to parse.
     * @throws IllegalArgumentException when an invalid version has given.
     */
    public UltreonVersion(String s) {
        // String to be scanned to find the pattern.
        String pattern = "^(\\d+)\\.(\\d+)\\.(\\d+)$"; // 1.0-alpha4 // 5.4-release7

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(s);
        if (m.find()) {
            version = Integer.parseInt(m.group(1));
            release = Integer.parseInt(m.group(2));
            bugfix = Integer.parseInt(m.group(3));
            stage = Stage.RELEASE;
            devTest = false;
            devTestId = -1;
        } else {
            pattern = "^(\\d+)\\.(\\d+)\\.(\\d+)-(alpha|beta|pre|rc)$"; // 1.0-alpha4 // 5.4-release7
            r = Pattern.compile(pattern);
            m = r.matcher(s);
            if (m.find()) {
                version = Integer.parseInt(m.group(1));
                release = Integer.parseInt(m.group(2));
                bugfix = Integer.parseInt(m.group(3));
                switch (m.group(4)) {
                    case "alpha" -> stage = Stage.ALPHA;
                    case "beta" -> stage = Stage.BETA;
                    case "pre", "rc" -> stage = Stage.PRE;
                    default -> throw new InternalError("Regex has invalid output.");
                }
                devTest = false;
                devTestId = -1;
            } else {
                pattern = "^(\\d+)\\.(\\d+)\\.(\\d+)-devTest(\\d+)$"; // 1.0-alpha4 // 5.4-release7
                r = Pattern.compile(pattern);
                m = r.matcher(s);
                if (m.find()) {
                    version = Integer.parseInt(m.group(1));
                    release = Integer.parseInt(m.group(2));
                    bugfix = Integer.parseInt(m.group(3));
                    stage = null;
                    devTest = true;
                    devTestId = Integer.parseInt(m.group(4));
                } else {
                    throw new IllegalArgumentException("Invalid version, expected to find using regex '(\\d+)\\.(\\d+)\\.(\\d+)' or '(\\d+)\\.(\\d+)\\.(\\d+)-(alpha|beta|pre|rc)'");
                }
            }
        }
    }

    public UltreonVersion(int version, String stage) {
        this(version, 0, stage);
    }

    public UltreonVersion(int version, int release, String stage) {
        this(version, release, 0, stage);
    }

    public UltreonVersion(int version, int release, int bugfix, String stage) {
        this.version = Math.max(version, 0);
        this.release = Math.max(release, 0);
        this.bugfix = Math.max(bugfix, 0);
        switch (stage) {
            case "alpha" -> this.stage = Stage.ALPHA;
            case "beta" -> this.stage = Stage.BETA;
            case "pre", "rc" -> this.stage = Stage.PRE;
            case "release", "" -> this.stage = Stage.RELEASE;
            default -> throw new InternalError("Invalid RandomThingz version stage!");
        }
        this.devTest = false;
    }

    public UltreonVersion(int version, @NotNull Stage stage) {
        this(version, 0, stage);
    }

    public UltreonVersion(int version, int release, @NotNull Stage stage) {
        this(version, release, 0, stage);
    }

    public UltreonVersion(int version, int release, int bugfix, @NotNull Stage stage) {
        this.version = Math.max(version, 0);
        this.release = Math.max(release, 0);
        this.bugfix = Math.max(bugfix, 0);
        this.stage = Objects.requireNonNull(stage, () -> "Stage is null for a non-devtest version, which shouldn't be.");
        this.devTest = false;
        this.devTestId = -1;
    }

    /**
     * Ultreon Team DevTest version
     *
     * @param version
     * @param devTestId
     */
    public UltreonVersion(int version, int devTestId) {
        this(version, 0, devTestId);
    }

    /**
     * Ultreon Team DevTest version
     *
     * @param version
     * @param release
     * @param devTestId
     */
    public UltreonVersion(int version, int release, int devTestId) {
        this(version, release, 0, devTestId);
    }

    /**
     * Ultreon Team DevTest version
     */
    public UltreonVersion(int version, int release, int bugfix, int devTestId) {
        this.version = Math.max(version, 0);
        this.release = Math.max(release, 0);
        this.bugfix = Math.max(bugfix, 0);
        this.stage = null;
        this.devTest = true;
        this.devTestId = Math.max(devTestId, 1);
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
        sb.append(release);
        if (stage != Stage.RELEASE) {
            sb.append('-');
            sb.append(stage.name().toLowerCase());
        }
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
        sb.append('.');
        sb.append(bugfix);

        if (stage != null) {
            switch (stage) {
                case ALPHA -> sb.append(" Alpha");
                case BETA -> sb.append(" Beta");
                case PRE -> sb.append(" RC");
            }
        }

        if (devTest) {
            sb.append(" DevTest ");
            sb.append(Math.max(devTestId, 1));
        }

        return sb.toString();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int compareTo(@NotNull Version o) {
        if (!(o instanceof UltreonVersion version)) {
            throw new IllegalArgumentException("Can't compare other than RtVersion");
        }

        int versionCompare = Integer.compare(this.version, version.version);
        if (versionCompare == 0) {
            int releaseCompare = Integer.compare(this.release, version.release);
            if (releaseCompare == 0) {
                int bugfixCompare = Integer.compare(this.bugfix, version.bugfix);
                if (bugfixCompare == 0) {
                    if (this.devTest && !version.devTest) {
                        return -1;
                    } else if (!this.devTest && version.devTest) {
                        return 1;
                    } else if (this.devTest && version.devTest) {
                        return Integer.compare(this.devTestId, version.devTestId);
                    } else if (this.stage != null && version.stage != null) {
                        return Integer.compare(this.stage.ordinal(), version.stage.ordinal());
                    } else {
                        return 0;
                    }
                }
                return bugfixCompare;
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

    public int getBugfix() {
        return bugfix;
    }

    @Nullable
    public Stage getStage() {
        return stage;
    }

    public boolean isDevTest() {
        return devTest;
    }

    public int getDevTestId() {
        return devTestId;
    }

    @Nullable
    public Integer getDevTestIdOrNull() {
        return devTest ? devTestId : null;
    }

    public enum Stage {
        ALPHA("alpha"),
        BETA("beta"),
        PRE("pre"),
        RELEASE("");

        private final String name;

        Stage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
