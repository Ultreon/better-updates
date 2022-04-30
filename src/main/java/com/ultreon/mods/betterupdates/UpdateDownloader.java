package com.ultreon.mods.betterupdates;

import com.ultreon.mods.betterupdates.event.UpdateDownloadedEvent;
import com.ultreon.mods.betterupdates.event.UpdateFailedEvent;
import com.ultreon.mods.betterupdates.version.Dependency;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class UpdateDownloader extends Thread implements Runnable {
    private final URL downloadUrl;
    private final Runnable done;
    private final AbstractUpdater<?> updater;
    private int totalSize = 0;
    private int downloaded;
    private DownloadState state = DownloadState.INITIATING;
    private final OnProgress progressbar;
    private final Set<Dependency> dependencies;
    private final int blockSize;

    public UpdateDownloader(URL downloadUrl, AbstractUpdater<?> updater, Runnable done, OnProgress progressbar, Set<Dependency> dependencies) {
        this(downloadUrl, updater, done, progressbar, dependencies, Config.blockSize.get());
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    @NotNull
    public UpdateDownloader.DownloadState getDownloadState() {
        return state;
    }

    public UpdateDownloader(URL downloadUrl, AbstractUpdater<?> updater, Runnable done, OnProgress progressbar, Set<Dependency> dependencies, int blockSize) {
        super("BetterUpdatesDownloader " + updater.getModId());
        this.updater = updater;
        this.downloadUrl = downloadUrl;
        this.done = done;
        this.progressbar = progressbar;
        this.dependencies = dependencies;
        this.blockSize = blockSize;
    }


    public void run() {
        download(downloadUrl);

        for (Dependency dependency : this.dependencies) {
            download(dependency.getDownload());
        }

        this.done.run();
    }

    private void download(URL url) {

        OutputStream updateStream = null;
        InputStream inputStream = null;
        try {
            BetterUpdatesMod.LOGGER.info("Opening connection to the update file.");
            state = DownloadState.CONNECTING;
            URLConnection urlConnection = url.openConnection();

            String headerField = urlConnection.getHeaderField("Content-Length");
            this.totalSize = Integer.parseInt(headerField);
            progressbar.onProgress(0, totalSize);
            BetterUpdatesMod.LOGGER.info("Total download size is: " + this.totalSize);

            // Url Input stream
            BetterUpdatesMod.LOGGER.info("Loading input stream for connection...");
            inputStream = urlConnection.getInputStream();

            // Update folder.
            File updateFolder = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "updates");
            if (!updateFolder.exists()) {
                BetterUpdatesMod.LOGGER.info("Update folder doesn't exists, creating one...");
                if (!updateFolder.mkdirs()) {
                    throw new RuntimeException("Can't create update folder.");
                }
            }

            // Update file.
            String[] split = url.getPath().split("/");
            File updateFile = new File(updateFolder.getAbsolutePath(), split[split.length - 1]);
            if (updateFile.exists()) {
                BetterUpdatesMod.LOGGER.info("Update file already exists, deleting...");
                if (!updateFile.delete()) {
                    throw new RuntimeException("Can't create update folder.");
                }
            }

            BetterUpdatesMod.LOGGER.info("Creating file...");
            if (!updateFile.createNewFile()) {
                throw new RuntimeException("Can't create update folder.");
            }
            updateStream = new FileOutputStream(updateFile);

            // Set initial offset value.
            int offset = 0;

            // Get current block size.
            int currentBlockSize = blockSize;

            BetterUpdatesMod.LOGGER.info("Download started!");
            BetterUpdatesMod.LOGGER.info("Block size: " + currentBlockSize);

            // Read data.
            byte[] block = new byte[currentBlockSize];
            int read = read(inputStream, block, currentBlockSize);

            state = DownloadState.DOWNLOADING;

            // Write data.
            if (read != -1) {
                updateStream.write(block, 0, read);
                updateStream.flush();

                // Advance in offset.
                offset += read;
                downloaded = offset;
                progressbar.onProgress(offset, totalSize);
            }

            // Read other bytes.
            while (read != -1) {
                // Read data.
                block = new byte[currentBlockSize];
                read = read(inputStream, block, currentBlockSize);
                if (read == -1) {
                    break;
                }

                // Write data.
                updateStream.write(block, 0, read);
                updateStream.flush();

                // Advance in offset.
                offset += read;
                downloaded = offset;
                progressbar.onProgress(offset, totalSize);
            }

            // Close remote input stream.
            inputStream.close();

            // Flush and close local output stream.
            updateStream.flush();
            updateStream.close();

            MinecraftForge.EVENT_BUS.post(new UpdateDownloadedEvent(updater.getModId()));

            state = DownloadState.DONE;
        } catch (IOException | RuntimeException e) {
            // An error occurred.
            e.printStackTrace();

            MinecraftForge.EVENT_BUS.post(new UpdateFailedEvent(updater.getModId(), e));

            state = DownloadState.FAILED;

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            if (updateStream != null) {
                try {
                    updateStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        this.done.run();
    }

    public int read(InputStream stream, byte[] b, int len) throws IOException {
        int off = 0;

        int c = stream.read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;

        int i = 1;
        try {
            for (; i < len; i++) {
                c = stream.read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte) c;
            }
        } catch (IOException ignored) {

        }
        return i;
    }

    @FunctionalInterface
    public interface OnProgress {
        void onProgress(int progress, int max);
    }

    public enum DownloadState {
        INITIATING,
        CONNECTING,
        DOWNLOADING,
        DONE,
        FAILED,
    }
}
