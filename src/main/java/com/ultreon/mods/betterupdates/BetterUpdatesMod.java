package com.ultreon.mods.betterupdates;

import com.ultreon.mods.betterupdates.client.screen.UpdateAvailableScreen;
import com.ultreon.mods.betterupdates.common.Ticker;
import com.ultreon.mods.betterupdates.version.MavenVersion;
import com.ultreon.mods.betterupdates.version.UltreonVersion;
import com.ultreon.mods.betterupdates.version.Version;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unused")
@Mod(BetterUpdatesMod.MOD_ID)
public class BetterUpdatesMod {
    public static final Logger LOGGER = LogManager.getLogger("Better Updates");
    public static final String MOD_ID = "better_updates";
    public static final String MOD_VERSION;
    public static final String MOD_DESCRIPTION;
    public static final String MOD_DISPLAY_NAME;
    private static final String MOD_LICENSE;
    public static final UltreonVersion VERSION;
    private static BetterUpdatesMod instance;

    private ClientSide clientSide = null;
    private ServerSide serverSide = null;

    public static String getModLicense() {
        return MOD_LICENSE;
    }

    static {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElseThrow();
        IModInfo info = container.getModInfo();

        MOD_VERSION = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
        MOD_DESCRIPTION = info.getDescription();
        MOD_DISPLAY_NAME = info.getDisplayName();
        MOD_LICENSE = info.getOwningFile().getLicense();

        if (Objects.equals(MOD_VERSION, "0.0NONE")) {
            VERSION = new UltreonVersion("1.0.0-devTest1");
        } else {
            VERSION = new UltreonVersion(MOD_VERSION);
        }

        Config.initialize();
    }

    public BetterUpdatesMod() {
        instance = this;

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientSide = new ClientSide());
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> serverSide = new ServerSide());
    }

    public static boolean isModDev() {
        return !FMLEnvironment.production;
    }

    public void setup(FMLCommonSetupEvent event) {
        for (IModFileInfo info : ModList.get().getModFiles()) {
            IConfigurable properties = info.getConfig();
            Optional<String> betterUpdatesUrl1 = properties.getConfigElement("betterUpdatesUrl");
            betterUpdatesUrl1.ifPresent((url) -> {
                final URL realURL;
                try {
                    realURL = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return;
                }
                for (final IModInfo modInfo : info.getMods()) {
                    new AbstractUpdater<>(realURL, modInfo.getModId()) {
                        private final MavenVersion current = new MavenVersion(modInfo.getVersion());

                        @Override
                        public MavenVersion parseVersion(String version) {
                            return new MavenVersion(new DefaultArtifactVersion(version));
                        }

                        @Override
                        public MavenVersion getCurrentVersion() {
                            return current;
                        }
                    };
                    LOGGER.info("Better Updates URL for '" + modInfo.getModId() + "' is: " + url);
                }
            });
        }
    }

    public static boolean isDevtest() {
        return VERSION.isDevTest();
    }

    public static BetterUpdatesMod getInstance() {
        return instance;
    }

    @OnlyIn(Dist.CLIENT)
    public ClientSide getClientSide() {
        return clientSide;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public ServerSide getServerSide() {
        return serverSide;
    }

    public static class ClientSide {
        private static final List<AbstractUpdater<?>> SHOWN_UPDATES = new ArrayList<>();

        private ClientSide() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        /**
         * On screen initialize event.
         * Catches the main menu initialization.
         *
         * @param event a {@linkplain ScreenEvent.InitScreenEvent.Post} event.
         */
        @SubscribeEvent
        public void onScreenInit(ScreenEvent.InitScreenEvent.Post event) {
            // Get gui and the Minecraft instance.
            Minecraft mc = Minecraft.getInstance();
            Screen gui = event.getScreen();

            // Is the gui the main menu?
            if (gui instanceof TitleScreen) {
                checkForUpdates(mc, gui);
            }
        }

        /**
         * Check for Random Thingz updates, then show the update available screen.
         *
         * @param mc  the minecraft instance.
         * @param gui the current gui.
         */
        public void checkForUpdates(Minecraft mc, Screen gui) {
            // Check for updates.
            AbstractUpdater<?> updaterToCheck = null;
            for (AbstractUpdater<?> updater : AbstractUpdater.getInstances()) {
                if (SHOWN_UPDATES.contains(updater)) continue;
                @NotNull AbstractUpdater.UpdateInfo info = updater.checkForUpdates();
                if (info.status() == AbstractUpdater.UpdateStatus.UPDATE_AVAILABLE) {
                    updaterToCheck = updater;
                    break;
                }
            }

            if (updaterToCheck == null) return;

            // Get Random Thingz updater instance.
            AbstractUpdater<?> updater = updaterToCheck;

            // Check for Random Thingz updates.
            AbstractUpdater.UpdateInfo updateInfo = updater.checkForUpdates();

            // Is there an update available?
            if (updateInfo.status() == AbstractUpdater.UpdateStatus.UPDATE_AVAILABLE) {
                // Show the update available screen.
                mc.pushGuiLayer(new UpdateAvailableScreen(gui, updater));
            }
            SHOWN_UPDATES.add(updater);

            // Set updater debug to false.
            AbstractUpdater.debug = false;
        }
    }

    public static class ServerSide {
        private static final int delay = 3600;
        private static final int tickDelay = 40 * delay;
        private static final HashMap<AbstractUpdater<?>, Version> latestKnownMap = new HashMap<>();
        private static final Ticker ticker = new Ticker(tickDelay - 1);

        @SubscribeEvent
        public void serverTick(TickEvent.ServerTickEvent event) {
            if (isDevtest()) {
                return;
            }

            if (FMLEnvironment.dist == Dist.CLIENT) {
                return;
            }

            ticker.advance();

            if (ticker.getCurrentTicks() >= (tickDelay)) {
                ticker.reset();

                LOGGER.info("Checking for mod updates...");

                AbstractUpdater<?>[] updaters = AbstractUpdater.getInstances();
                for (AbstractUpdater<?> updater : updaters) {
                    AbstractUpdater.UpdateInfo updateInfo = updater.checkForUpdates();
                    Version latest = updater.getLatestVersion();
                    if (!latestKnownMap.containsKey(updater)) {
                        latestKnownMap.put(updater, updater.getCurrentVersion());
                    }
                    Version latestKnown = latestKnownMap.get(updater);
                    if (latestKnown == null || latest == null) {
                        continue;  // Todo: show error notification.
                    }

                    if (latestKnown.compareTo(latest) < 0) {
                        latestKnownMap.put(updater, latest);

                        if (updateInfo.status() == AbstractUpdater.UpdateStatus.UPDATE_AVAILABLE) {
                            BetterUpdatesMod.LOGGER.info("Update available for " + updater.getModInfo().getModId());
                        }
                    }
                }
            }
        }

    }

}
