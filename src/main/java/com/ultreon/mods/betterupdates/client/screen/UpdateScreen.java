package com.ultreon.mods.betterupdates.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import com.ultreon.mods.betterupdates.BetterUpdatesMod;
import com.ultreon.mods.betterupdates.UpdateDownloader;
import com.ultreon.mods.betterupdates.client.widget.Progressbar;
import com.ultreon.mods.betterupdates.version.Dependencies;
import com.ultreon.mods.betterupdates.version.Dependency;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BetterUpdatesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class UpdateScreen extends Screen {
    private static boolean initializedAlready = false;
    private final MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen backScreen;
    private final URL downloadUrl;
    @SuppressWarnings("FieldCanBeLocal")
    private final Set<Dependency> dependencies;
    private final BiFunction<Runnable, UpdateDownloader.OnProgress, UpdateDownloader> downloaderSup;
    private int ticksUntilEnable;
    private long downloaded;
    private long totalSize = -1L;
    private Button done;
    private Progressbar progressbar;
    private Thread downloadThread;
    private int blockSize = 1024;

    private final File originalFile;
    private UpdateDownloader downloader;
    private boolean failed;

    public UpdateScreen(Screen backScreen, BiFunction<Runnable, UpdateDownloader.OnProgress, UpdateDownloader> downloaderSup, File originalFile, URL downloadUrl, Dependencies dependencies) {
        super(new TranslatableComponent("screen.better_updates.downloading_update.title"));
        this.backScreen = backScreen;
        this.downloadUrl = downloadUrl;
        this.dependencies = dependencies.getAll();
        this.originalFile = originalFile;
        this.downloaderSup = downloaderSup;
    }

    protected void init() {
        super.init();

        NarratorStatus narratorStatus = Objects.requireNonNull(this.minecraft).options.narratorStatus;

        if (narratorStatus == NarratorStatus.SYSTEM || narratorStatus == NarratorStatus.ALL) {
            Narrator.getNarrator().say("Downloading Update", true);
        }

        this.clearWidgets();

        this.done = this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, CommonComponents.GUI_DONE, (p_213004_1_) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(this.failed ? new UpdateFailedScreen(this.backScreen) : new UpdateDownloadedScreen(originalFile, this.backScreen));
            }
        }));

        this.progressbar = this.addRenderableWidget(new Progressbar(this.width / 2 - 91, 120, 0, 100));

        this.done.active = false;
        initializedAlready = true;

        this.downloader = downloaderSup.apply(() -> {
            UpdateDownloader.DownloadState downloadState = downloader.getDownloadState();
            if (downloadState == UpdateDownloader.DownloadState.FAILED) {
                failed = true;
                this.done.active = true;
            }
        }, (progress, max) -> {
            this.progressbar.setValue(progress);
            this.progressbar.setLength(max);
        });
    }

    public UpdateDownloader getDownloader() {
        return downloader;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public long getDownloaded() {
        return progressbar.getValue();
    }

    public long getTotalSize() {
        return progressbar.getLength();
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 70, 0xffffff);
        int i;
        if (downloader.getTotalSize() != -1L) {
            if (downloader.getTotalSize() == 0) {
                i = 0;
            } else {
                i = 100 * this.downloader.getDownloaded() / this.downloader.getTotalSize();
            }
            drawCenteredString(matrixStack, this.font, new TranslatableComponent("screen.better_updates.downloading_update.description", this.downloader.getDownloaded(), this.downloader.getTotalSize(), i), this.width / 2, 90, 0xbfbfbf);
        }

        this.message.renderCentered(matrixStack, this.width / 2, 90);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void tick() {
        super.tick();
        this.downloaded = progressbar.getValue();
        this.totalSize = progressbar.getLength();
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static boolean isInitializedAlready() {
        return initializedAlready;
    }

    public Thread getDownloadThread() {
        return downloadThread;
    }
}
