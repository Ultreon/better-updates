package com.ultreon.mods.betterupdates.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.betterupdates.AbstractUpdater;
import com.ultreon.mods.betterupdates.BetterUpdatesMod;
import com.ultreon.mods.betterupdates.client.screen.UpdateScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class UpdateButton extends BetterButton {
    private final AbstractUpdater<?> updater;

    public UpdateButton(AbstractUpdater<?> updater, int x, int y, int width) {
        super(x, y, width, new TranslatableComponent("button." + updater.getModInfo().getModId() + ".update"), (button) -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new UpdateScreen(mc.screen, updater::downloadUpdate, updater.getModFile(), updater.getReleaseUrl(), updater.getDependencies()));
        });
        this.updater = updater;
        this.active = updater == AbstractUpdater.getUpdaterUpdater() ? !BetterUpdatesMod.isDevtest() && updater.hasUpdate() : updater.hasUpdate();
    }

    public UpdateButton(AbstractUpdater<?> updater, int x, int y, int width, OnTooltip onTooltip) {
        super(x, y, width, new TranslatableComponent("button." + updater.getModInfo().getModId() + ".update"), (button) -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new UpdateScreen(mc.screen, updater::downloadUpdate, updater.getModFile(), updater.getReleaseUrl(), updater.getDependencies()));
        }, onTooltip);
        this.updater = updater;
        this.active = this.updater.hasUpdate();
    }

    public AbstractUpdater<?> getUpdater() {
        return updater;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.active = this.updater.hasUpdate();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
