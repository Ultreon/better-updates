package com.ultreon.mods.betterupdates.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class UpdateFailedEvent extends Event {
    private final String modId;
    private final ModContainer modContainer;
    private final IModInfo modInfo;
    private final Exception exception;

    public UpdateFailedEvent(String modId, Exception exception) {
        this.modId = modId;
        this.modContainer = ModList.get().getModContainerById(modId).orElseThrow();
        this.modInfo = modContainer.getModInfo();
        this.exception = exception;
    }

    public String getModId() {
        return modId;
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public IModInfo getModInfo() {
        return modInfo;
    }

    public Exception getException() {
        return exception;
    }
}
