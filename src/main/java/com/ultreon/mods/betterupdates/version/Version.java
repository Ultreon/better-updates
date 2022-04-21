package com.ultreon.mods.betterupdates.version;

public interface Version extends Comparable<Version> {
    boolean isStable();

    default boolean isUnstable() {
        return !isStable();
    }

    default boolean isReallyUnstable() {
        return false;
    }

    String toString();

    String toLocalizedString();

    default boolean canReceiveUpdates() {
        return true;
    }
}
