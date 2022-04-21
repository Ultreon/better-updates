package com.ultreon.mods.betterupdates;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
@Mod.EventBusSubscriber(modid = BetterUpdatesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    public static final ForgeConfigSpec.BooleanValue showToasts;
    public static final ForgeConfigSpec.BooleanValue checkForBetterUpdates;
    public static final ForgeConfigSpec.BooleanValue installInModsFolder;
    public static final ForgeConfigSpec.BooleanValue deleteOldFile;
    public static final ForgeConfigSpec.BooleanValue updateToUnstable;
    public static final ForgeConfigSpec.IntValue checkInterval;
    public static final ForgeConfigSpec.IntValue blockSize;
    public static final ForgeConfigSpec.BooleanValue updateWithoutAsking;

    private static final ForgeConfigSpec clientSpec;
    private static final ForgeConfigSpec commonSpec;
    private static final ForgeConfigSpec serverSpec;

    private static final ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();



    static {
        // Client
        showToasts = clientBuilder
                .comment("Install in mods folder instead of updates folder.")
                .define("showToasts", true);
        clientSpec = clientBuilder.build();

        // Common
        checkForBetterUpdates = commonBuilder
                .comment("Check for updates for the Better Updates mod itself.")
                .define("checkForBetterUpdates", true);
        installInModsFolder = commonBuilder
                .comment("Installs in the mods folder instead of updates folder.")
                .define("installInModsFolder", true);
        deleteOldFile = commonBuilder
                .comment("Updates will be installed in the mods folder, and deletes the old file after exiting.")
                .define("deleteOldFile", false);
        updateToUnstable = commonBuilder
                .comment("Updates to unstable versions instead of stable ones.")
                .define("updateToUnstable", true);
        checkInterval = commonBuilder
                .comment("Interval to check for updates in seconds.")
                .defineInRange("checkInterval", FMLEnvironment.dist.isClient() ? 10 : 1800, FMLEnvironment.dist.isClient() ? 10 : 600, 3600);
        blockSize = commonBuilder
                .comment("Size per downloaded part.")
                .defineInRange("blockSize", 8192, 1024, 65536);
        commonSpec = commonBuilder.build();

        // Server
        updateWithoutAsking = serverBuilder
                .comment("Installs the update without asking.")
                .define("updateWithoutAsking", true);
        serverSpec = serverBuilder.build();
    }

    private Config() {
        throw new IllegalAccessError("Instantiation of utility class.");
    }

    public static void initialize() {
        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
        if (FMLEnvironment.dist.isDedicatedServer()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        }
    }

    public static void sync() {

    }

    @SubscribeEvent
    public static void sync(ModConfigEvent.Loading event) {
        sync();
    }

    @SubscribeEvent
    public static void sync(ModConfigEvent.Reloading event) {
        sync();
    }

    public static void save() {
        clientSpec.save();
        commonSpec.save();
        serverSpec.save();
    }
}
