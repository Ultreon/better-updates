package com.ultreon.mods.betterupdates.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class UpdateDownloadedEvent extends Event {
    private final String modId;
    private final ModContainer modContainer;
    private final IModInfo modInfo;

    public UpdateDownloadedEvent(String modId) {
        this.modId = modId;
        this.modContainer = ModList.get().getModContainerById(modId).orElseThrow();
        this.modInfo = modContainer.getModInfo();
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
}
