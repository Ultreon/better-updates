package com.ultreon.mods.betterupdates.version;

import net.minecraftforge.common.util.MavenVersionStringHelper;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
public class MavenVersion implements Version {
    private final ArtifactVersion artifactVersion;

    public MavenVersion(ArtifactVersion artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    @Override
    public boolean isStable() {
        return true;
    }

    @Override
    public String toString() {
        return MavenVersionStringHelper.artifactVersionToString(artifactVersion);
    }

    @Override
    public String toLocalizedString() {
        return toString();
    }

    @Override
    public int compareTo(@NotNull Version o) {
        if (o instanceof MavenVersion mavenVersion) {
            return artifactVersion.compareTo(mavenVersion.artifactVersion);
        }
        throw new IllegalArgumentException("Expect to find '" + getClass().getName() + "' got instead: '" + o.getClass().getName() + "'");
    }
}
