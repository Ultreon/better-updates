package com.ultreon.mods.betterupdates.client.toast;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.betterupdates.AbstractUpdater;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateToast implements Toast {
    private ImmutableList<FormattedCharSequence> subtitle;
    private Component title;
    private final int fadeOutTicks;

    private long firstDrawTime;
    private boolean newDisplay;

    public UpdateToast(AbstractUpdater<?> updater) {
        this.title = new TranslatableComponent("toasts.better_updates.update_available.title");
        this.subtitle = nullToEmpty(new TextComponent(updater.getModInfo().getDisplayName()));
        this.fadeOutTicks = 160;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component component) {
        return component == null ? ImmutableList.of() : ImmutableList.of(component.getVisualOrderText());
    }

    @Override
    public int width() {
        return this.fadeOutTicks;
    }

    @NotNull
    public Toast.Visibility render(@NotNull PoseStack pose, @NotNull ToastComponent component, long ticks) {
        if (this.newDisplay) {
            this.firstDrawTime = ticks;
            this.newDisplay = false;
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int width = this.width();
        if (width == 160 && this.subtitle.size() <= 1) {
            component.blit(pose, 0, 0, 0, 64, width, this.height());
        } else {
            int k = this.height() + Math.max(0, this.subtitle.size() - 1) * 12;
            int i1 = Math.min(4, k - 28);
            this.blitTextures(pose, component, width, 0, 0, 28);

            for (int j1 = 28; j1 < k - i1; j1 += 10) {
                this.blitTextures(pose, component, width, 16, j1, Math.min(16, k - j1 - i1));
            }

            this.blitTextures(pose, component, width, 32 - i1, k - i1, i1);
        }

        if (this.subtitle == null) {
            component.getMinecraft().font.draw(pose, this.title, 18f, 12f, -256);
        } else {
            component.getMinecraft().font.draw(pose, this.title, 18f, 7f, -256);

            for (int k1 = 0; k1 < this.subtitle.size(); ++k1) {
                component.getMinecraft().font.draw(pose, this.subtitle.get(k1), 18f, (float) (18 + k1 * 12), -1);
            }
        }

        return ticks - this.firstDrawTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }

    private void blitTextures(PoseStack pose, ToastComponent component, int p_238533_3_, int p_238533_4_, int p_238533_5_, int p_238533_6_) {
        int i = p_238533_4_ == 0 ? 20 : 5;
        int j = Math.min(60, p_238533_3_ - i);
        component.blit(pose, 0, p_238533_5_, 0, 64 + p_238533_4_, i, p_238533_6_);

        for (int k = i; k < p_238533_3_ - j; k += 64) {
            component.blit(pose, k, p_238533_5_, 32, 64 + p_238533_4_, Math.min(64, p_238533_3_ - k - j), p_238533_6_);
        }

        component.blit(pose, p_238533_3_ - j, p_238533_5_, 160 - j, 64 + p_238533_4_, j, p_238533_6_);
    }

    public void setDisplayedText(Component title, @Nullable Component subtitle) {
        this.title = title;
        this.subtitle = nullToEmpty(subtitle);
        this.newDisplay = true;
    }
}
