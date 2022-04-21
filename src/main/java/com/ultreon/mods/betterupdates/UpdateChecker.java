package com.ultreon.mods.betterupdates;

import com.ultreon.mods.betterupdates.client.screen.UpdateAvailableScreen;
import com.ultreon.mods.betterupdates.client.toast.UpdateToast;
import com.ultreon.mods.betterupdates.common.Ticker;
import com.ultreon.mods.betterupdates.version.Version;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.function.BiConsumer;

@SuppressWarnings({"unused"})
public class UpdateChecker {
    private static final HashMap<AbstractUpdater<?>, Version> latestKnownMap = new HashMap<>();
    private static final Ticker ticker = new Ticker(86400);
    private final BetterUpdatesMod mod;
    private Thread thread;

    UpdateChecker(BetterUpdatesMod mod) {
        this.mod = mod;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Screen gui = mc.screen;

        if (gui == null && mc.level == null) return;
        if (gui instanceof UpdateAvailableScreen) return;
        if (thread != null && thread.isAlive()) return;

        ticker.advance();

        if (ticker.getCurrentTicks() >= (getTickDelay())) {
            ticker.reset();

            this.thread = new Thread(() -> checkForUpdates((updater, url) -> {
                // Update available.
                BetterUpdatesMod.LOGGER.info("Update available for " + updater.getModInfo().getModId() + ".");
                UpdateToast toast = new UpdateToast(updater);
                mc.getToasts().addToast(toast);
            }), "BetterUpdates UpdateChecker");
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (thread != null && thread.isAlive()) return;

        ticker.advance();

        if (ticker.getCurrentTicks() >= (getTickDelay())) {
            ticker.reset();

            this.thread = new Thread(() -> checkForUpdates((updater, url) -> {
                if (Config.updateWithoutAsking.get()) {
                    BetterUpdatesMod.LOGGER.info("Downloading update for " + updater.getModInfo().getDisplayName());
                    updater.downloadUpdate(
                        () -> BetterUpdatesMod.LOGGER.info("Downloaded update for " + updater.getModInfo().getDisplayName()),
                        (progress, max) -> {

                    });
                } else {
                    BetterUpdatesMod.LOGGER.info("Update available for " + updater.getModInfo().getDisplayName() + ": " + url);
                }
            }), "BetterUpdates UpdateChecker");
        }
    }

    public void checkForUpdates(BiConsumer<AbstractUpdater<?>, URL> onUpdate) {
        BetterUpdatesMod.LOGGER.info("Checking for updates...");

        int updatesFound = 0;

        AbstractUpdater<?>[] updaters = AbstractUpdater.getInstances();
        for (AbstractUpdater<?> updater : updaters) {
            Version current = updater.getCurrentVersion();
            if (!current.canReceiveUpdates()) {
                continue;
            }
            AbstractUpdater.UpdateInfo info = updater.checkForUpdates();
            Version latest = updater.getLatestVersion();
            if (!latestKnownMap.containsKey(updater)) {
                latestKnownMap.put(updater, current);
            }
            Version latestKnown = latestKnownMap.get(updater);
            if (latestKnown == null || latest == null) {
                continue;  // Todo: show error notification.
            }

            if (latestKnown.compareTo(latest) < 0) {
                // New version found.
                latestKnownMap.put(updater, latest);

                if (Config.showToasts.get() && info.status() == AbstractUpdater.UpdateStatus.UPDATE_AVAILABLE) {
                    // Update available.
                    updatesFound++;

                    onUpdate.accept(updater, info.updateUrl());
                }
            }
        }

        BetterUpdatesMod.LOGGER.info("Found " + updatesFound + " updates.");
    }

    private static int getTickDelay() {
        return Config.checkInterval.get() * 20;
    }

    public BetterUpdatesMod getBetterUpdatesMod() {
        return mod;
    }
}
