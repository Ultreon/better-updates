package com.ultreon.mods.betterupdates.event;

import com.ultreon.mods.betterupdates.AbstractUpdater;

import java.net.URL;

public class UpdateAvailableEvent extends UpdateEvent {
    private final String modId;
    private final URL downloadUrl;

    public UpdateAvailableEvent(AbstractUpdater<?> updater, String modId, URL downloadUrl) {
        super(updater);
        this.modId = modId;
        this.downloadUrl = downloadUrl;
    }

    public String getModId() {
        return modId;
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }
}
