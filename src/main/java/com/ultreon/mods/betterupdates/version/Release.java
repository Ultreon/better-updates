package com.ultreon.mods.betterupdates.version;

import com.ultreon.mods.betterupdates.AbstractUpdater;

import java.net.URL;

public class Release extends Dependency {
    private final AbstractUpdater<?> updater;

    public Release(AbstractUpdater<?> updater, String name, URL download) {
        super(updater.getModInfo().getModId(), name, download);
        this.updater = updater;
    }

    public Release(AbstractUpdater<?> updater, String name, URL download, Dependencies dependencies) {
        super(updater.getModInfo().getModId(), name, download, dependencies);
        this.updater = updater;
    }

    public AbstractUpdater<?> getUpdater() {
        return updater;
    }
}
