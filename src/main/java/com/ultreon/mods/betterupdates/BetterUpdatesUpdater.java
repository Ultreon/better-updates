package com.ultreon.mods.betterupdates;

import com.ultreon.mods.betterupdates.version.UltreonVersion;

import java.net.MalformedURLException;
import java.net.URL;

public class BetterUpdatesUpdater extends AbstractUpdater<UltreonVersion> {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/Ultreon/better-updates/master/update.json";
    private static final BetterUpdatesUpdater INSTANCE = new BetterUpdatesUpdater();

    @SuppressWarnings({"unused", "SameParameterValue"})
    private static URL getUrl(String s) {
        try {
            return new URL(UPDATE_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private BetterUpdatesUpdater() {
        super(getUrl(UPDATE_URL), BetterUpdatesMod.getInstance());
    }

    static BetterUpdatesUpdater getInstance() {
        return INSTANCE;
    }

    @Override
    public UltreonVersion parseVersion(String version) {
        return new UltreonVersion(version);
    }

    @Override
    public UltreonVersion getCurrentVersion() {
        return BetterUpdatesMod.VERSION;
    }
}
