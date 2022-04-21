package com.ultreon.mods.betterupdates;

import com.google.gson.*;
import com.ultreon.mods.betterupdates.version.Dependencies;
import com.ultreon.mods.betterupdates.version.Dependency;
import com.ultreon.mods.betterupdates.version.Release;
import com.ultreon.mods.betterupdates.version.Version;
import it.unimi.dsi.fastutil.ints.Int2IntFunctions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An abstract updater used for checking for updates.
 * Other modders can use this to create their own updaters.
 * They need to create an implementation of {@linkplain Version} to make it work with their own version systems -
 *
 * @param <T> an implementation of {@linkplain Version}.
 */
@SuppressWarnings("unused")
public abstract class AbstractUpdater<T extends Version> {
    private static final List<AbstractUpdater<?>> INSTANCES = new ArrayList<>();
    private static final Map<String, AbstractUpdater<?>> MOD_UPDATER_MAP = new HashMap<>();
    public static boolean debug = true;
    private final URL updateUrl;
    private final ModContainer modContainer;
    private T latestVersion = null;
    private URL releaseUrl;
    private Dependencies dependencies = new Dependencies();
    private Release release;

    /**
     * Get a mod container from an instance of an {@linkplain Mod @Mod} annotated class.
     *
     * @param obj an instance of a {@linkplain Mod @Mod} annotated class
     * @return the mod container got from the Object.
     */
    private static ModContainer getModFromObject(Object obj) {
        ModList modList = ModList.get();
        return modList
                .getModContainerByObject(obj)
                .orElseThrow(() -> new IllegalArgumentException("Object is not registered as Mod."));
    }

    public AbstractUpdater(URL url, ModInfo info) {
        this(url, info.getModId());
    }

    public AbstractUpdater(URL url, ModContainer container) {
        this(url, container.getModId());
    }

    public AbstractUpdater(URL url, Object modObject) {
        this(url, getModFromObject(modObject));
    }

    public AbstractUpdater(URL url, String modId) {
        String modIdRepr = modId
                .replaceAll("\n", "\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\"", "\\\"")
                .replaceAll("\\\\", "\\\\");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Mod with id \"" + modIdRepr + "\" not found.");
        this.modContainer = ModList.get().getModContainerById(modId).orElseThrow(() -> illegalArgumentException);
        this.updateUrl = url;

        INSTANCES.add(this);
        MOD_UPDATER_MAP.put(modId, this);
    }

    ///////////////
    // Instances //
    ///////////////

    /**
     * Get the updater that Random Thingz is using.
     *
     * @return the Random Thingz updater.
     */
    public static BetterUpdatesUpdater getUpdaterUpdater() {
        return BetterUpdatesUpdater.getInstance();
    }

    /**
     * Get all updater instances.
     *
     * @return all the updater instances in an array.
     */
    public static AbstractUpdater<?>[] getInstances() {
        return INSTANCES.toArray(new AbstractUpdater[0]);
    }

    /**
     * Get the updater associated with a mod-ID.
     *
     * @param modId the mod-ID.
     * @return the updater/
     */
    public static AbstractUpdater<?> getUpdater(String modId) {
        return MOD_UPDATER_MAP.get(modId);
    }

    ///////////
    // Urls. //
    ///////////
    public URL getReleaseUrl() {
        return releaseUrl;
    }

    public URL getUpdateFileUrl() {
        return updateUrl;
    }

    ///////////////
    // Versions. //
    ///////////////

    /**
     * Parse a version from a string.
     *
     * @param version a stringified version.
     * @return the parsed version.
     */
    public abstract T parseVersion(String version);

    /**
     * Get current mod version, of the mod associated with the updater.
     *
     * @return the current mod's version.
     */
    public abstract T getCurrentVersion();

    /**
     * Get the latest mod version.
     * Will return null if the updates wasn't checked before.
     *
     * @return the latest version of the mod associated with the updater.
     */
    @Nullable
    public T getLatestVersion() {
        return latestVersion;
    }

    ///////////////
    // Mod info. //
    ///////////////

    /**
     * Ge6 mod information, from the mod associated with the updater.
     *
     * @return the mod information.
     */
    public IModInfo getModInfo() {
        return modContainer.getModInfo();
    }

    /////////////////////////////////
    // Has update. / Is up to date //
    /////////////////////////////////

    /**
     * Get if there was an update available after checking.
     * Will return false, if the updates wasn't checked before.
     *
     * @return true if there's an update available, false otherwise.
     */
    public boolean hasUpdate() {
        return latestVersion != null && getCurrentVersion().compareTo(latestVersion) < 0;
    }

    /**
     * @param version the version to check if it's up-to-date.
     * @return true if the given version is up-to-date, false otherwise.
     */
    public boolean isUpToDate(T version) {
        return version.compareTo(latestVersion) < 0;
    }

    /**
     * Check for updates.
     * Will always return an update information, holding information about "is there an update available" and which version is the latest.
     *
     * @return the update information.
     */
    @NotNull
    public UpdateInfo checkForUpdates() {
        try {
            BetterUpdatesMod.LOGGER.info("Checking updates for mod " + getModId());

            // Get minecraft version.
            String id = Minecraft.getInstance().getGame().getVersion().getId();
            System.out.println("Minecraft Version: " + id);

            // Open update url.
            InputStream inputStream = updateUrl.openStream();
            Reader targetReader = new InputStreamReader(inputStream);

            // Get update information.
            Gson gson = new Gson();
            try {
                // Get Minecraft versions.
                JsonObject mcVersions = gson.fromJson(targetReader, JsonObject.class).get("mc_versions").getAsJsonObject();
                if (debug) {
                    BetterUpdatesMod.LOGGER.debug("===================================================");
                    BetterUpdatesMod.LOGGER.debug("Update Data:");
                    BetterUpdatesMod.LOGGER.debug("---------------------------------------------------");
                    BetterUpdatesMod.LOGGER.debug(mcVersions.toString());
                    BetterUpdatesMod.LOGGER.debug("===================================================");
                }

                // Get latest Mod version.
                JsonObject versionIndex = mcVersions.getAsJsonObject(id);
                if (versionIndex == null) {
                    return new UpdateInfo(UpdateStatus.INCOMPATIBLE, null, null);
                }

                JsonObject releaseIndex = versionIndex.getAsJsonObject(getCurrentVersion().isStable() ? "stable" : "unstable");
                JsonPrimitive latestJson = releaseIndex.getAsJsonPrimitive("version");

                // Get version download url.
                JsonPrimitive downloadJson = releaseIndex.getAsJsonPrimitive("download");
                if (releaseIndex.has("dependencies")) {
                    JsonObject dependenciesJson = releaseIndex.getAsJsonObject("dependencies");
                    this.dependencies = getDependencies(dependenciesJson);
                }
                T latestVersion = parseVersion(latestJson.getAsString());
                URL url = new URL(downloadJson.getAsString());

                // Assign values to fields.
                this.latestVersion = latestVersion;
                this.releaseUrl = url;

                this.release = new Release(this, modContainer.getModInfo().getDisplayName(), url, this.dependencies);

                // Check if up to date.
                if (getCurrentVersion().compareTo(latestVersion) < 0) {
                    // Close reader and stream.
                    targetReader.close();
                    inputStream.close();

                    // Return information, there's an update available.
                    return new UpdateInfo(UpdateStatus.UPDATE_AVAILABLE, null, url);
                }

                // Close reader and stream.
                targetReader.close();
                inputStream.close();

                // Return information, it's up-to-date.
                return new UpdateInfo(UpdateStatus.UP_TO_DATE, null, url);
            } catch (IllegalStateException | NullPointerException | IOException | IllegalArgumentException e) {
                // There went something wrong.
                return new UpdateInfo(UpdateStatus.INCOMPATIBLE, e, null);
            }
        } catch (UnknownHostException e) {
            // The server / computer if offline.
            return new UpdateInfo(UpdateStatus.OFFLINE, e, null);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();

            // The server / computer if offline.
            return new UpdateInfo(UpdateStatus.OFFLINE, e, null);
        }
    }

    public String getModId() {
        return modContainer.getModId();
    }

    private Dependencies getDependencies(JsonObject dependenciesJson) throws MalformedURLException {
        Dependencies dependencies = new Dependencies();

        for (Map.Entry<String, JsonElement> entry : dependenciesJson.entrySet()) {
            String modId = entry.getKey();
            JsonElement json = entry.getValue();

            if (json instanceof JsonObject object) {
                URL download = new URL(object.getAsJsonPrimitive("download").getAsString());
                String name = object.getAsJsonPrimitive("name").getAsString();
                if (object.has("dependencies")) {
                    Dependencies subDependencies = getDependencies(object.getAsJsonObject("dependencies"));
                    dependencies.add(new Dependency(modId, name, download, subDependencies));
                    continue;
                }
                dependencies.add(new Dependency(modId, name, download));
            }
        }

        dependencies.lock();
        return dependencies;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public Release getRelease() {
        return release;
    }

    public UpdateDownloader downloadUpdate(Runnable done, UpdateDownloader.OnProgress onProgress) {
        return new UpdateDownloader(releaseUrl, this, done, onProgress, dependencies);
    }

    public File getModFile() {
        return modContainer.getModInfo().getOwningFile().getFile().getFilePath().toFile();
    }

    /**
     * Update status.
     *
     * @author Qboi123
     */
    public enum UpdateStatus {
        INCOMPATIBLE, OFFLINE, UPDATE_AVAILABLE, UP_TO_DATE
    }

    /**
     * Update information.
     *
     * @param status Fields.
     * @author Qboi123
     */
        public record UpdateInfo(UpdateStatus status, Throwable throwable, URL updateUrl) {

            /**
             * Update information constructor.
             *
             * @param status    the update status.
             * @param throwable the throwable thrown when checking for updates.
             */
            public UpdateInfo(UpdateStatus status, Throwable throwable, URL updateUrl) {
                this.status = status;
                this.throwable = throwable;
                this.updateUrl = updateUrl;
                if (throwable != null && !(throwable instanceof IOException)) {
                    throwable.printStackTrace();
                }
            }

            /**
             * Get the update status.
             *
             * @return the update status.
             */
            @Override
            public UpdateStatus status() {
                return status;
            }

            /**
             * Get the throwable thrown when checking for updates.
             *
             * @return the throwable thrown when checking for updates.
             */
            @Override
            public Throwable throwable() {
                return throwable;
            }

        public URL updateUrl() {
            return updateUrl;
        }
    }
}
