package com.ultreon.mods.betterupdates.event;

import com.ultreon.mods.betterupdates.AbstractUpdater;
import net.minecraftforge.eventbus.api.Event;

public class UpdateEvent extends Event {
    private final AbstractUpdater<?> updater;

    public UpdateEvent(AbstractUpdater<?> updater) {
        this.updater = updater;
    }

    public AbstractUpdater<?> getUpdater() {
        return updater;
    }
}
