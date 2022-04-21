package com.ultreon.mods.betterupdates.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.List;

/**
 * Minecraft Graphics utilities.
 *
 * @author Qboi123
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MCGraphics {
    private final PoseStack pose;
    @Nonnull
    private final Screen screen;
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final ItemRenderer itemRenderer;
    private Font font;
    private Color color = Color.black;

    public MCGraphics(PoseStack matrixStack, Font font) {
        this(matrixStack, font, null);
    }

    public MCGraphics(PoseStack matrixStack, Font font, @Nullable Screen screen) {
        this.pose = matrixStack;
        this.font = font;
        this.mc = Minecraft.getInstance();
        this.screen = screen == null ? (this.mc.screen == null ? new Screen(new TextComponent("LOL WHAT DO YOU EXPECT???")) {} : this.mc.screen) : screen;
        this.textureManager = this.mc.getTextureManager();
        this.itemRenderer = this.mc.getItemRenderer();
    }

    public void drawString(String string, int x, int y) {
        drawString(string, x, y, false);
    }

    public void drawString(String string, int x, int y, boolean shadow) {
        if (shadow) {
            font.drawShadow(pose, string, x, y, color.getRGB());
        } else {
            font.draw(pose, string, x, y, color.getRGB());
        }
    }

    public void drawString(String string, int x, int y, Color color) {
        drawString(string, x, y, color, false);
    }

    public void drawString(String string, int x, int y, Color color, boolean shadow) {
        if (shadow) {
            font.drawShadow(pose, string, x, y, color.getRGB());
        } else {
            font.draw(pose, string, x, y, color.getRGB());
        }
    }

    public void drawTexture(int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(pose, x, y, width, height, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
    }

    public void drawTexture(int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(pose, x, y, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
    }

    public void drawTexture(int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(pose, x, y, uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    public void drawTexture(int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        Screen.blit(pose, x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureHeight, textureWidth);
    }

    public void drawTexture(Pos pos, Size size, UV uv, SizeUV sizeUV, Size textureSize, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Pos pos, Size size, UV uv, SizeUV sizeUV, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, 256, 256, resource);
    }

    public void drawTexture(Rect rect, UV uv, SizeUV sizeUV, Size textureSize, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Rect rect, UV uv, SizeUV sizeUV, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, 256, 256, resource);
    }

    public void drawTexture(Pos pos, Size size, UVBox uvBox, Size textureSize, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Pos pos, Size size, UVBox uvBox, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, 256, 256, resource);
    }

    public void drawTexture(Rect rect, UVBox uvBox, Size textureSize, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Rect rect, UVBox uvBox, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, 256, 256, resource);
    }

    public void drawTexture(Pos pos, UV uv, SizeUV sizeUV, Size textureSize, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Pos pos, Size size, UV uv, Size textureSize, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uv.u, uv.v, size.width, size.height, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Rect rect, UV uv, Size textureSize, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uv.u, uv.v, rect.width, rect.height, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Pos pos, Size size, UV uv, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, size.width, size.height, uv.u, uv.v, size.width, size.height, 256, 256, resource);
    }

    public void drawTexture(Rect rect, UV uv, ResourceLocation resource) {
        this.drawTexture(rect.x, rect.y, rect.width, rect.height, uv.u, uv.v, rect.width, rect.height, 256, 256, resource);
    }

    public void drawTexture(Pos pos, UVBox uvBox, Size textureSize, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, textureSize.width, textureSize.height, resource);
    }

    public void drawTexture(Pos pos, UV uv, SizeUV sizeUV, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, resource);
    }

    public void drawTexture(Pos pos, UVBox uvBox, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, uvBox.u, uvBox.v, uvBox.uWidth, uvBox.vHeight, resource);
    }

    public void drawTexture(Pos pos, int blitOffset, UV uv, SizeUV sizeUV, Size size, ResourceLocation resource) {
        this.drawTexture(pos.x, pos.y, blitOffset, size.width, size.height, uv.u, uv.v, sizeUV.uWidth, sizeUV.vHeight, resource);
    }

    public final void drawCenteredString(String text, float x, float y, Color color) {
        drawCenteredString(text, x, y, color, false);
    }

    public final void drawCenteredString(Component text, float x, float y, Color color) {
        drawCenteredString(text, x, y, color, false);
    }

    public Pos pos(int x, int y) {
        return new Pos(x, y);
    }

    public UV uv(int u, int v) {
        return new UV(u, v);
    }

    public Size size(int w, int h) {
        return new Size(w, h);
    }

    public SizeUV sizeUV(int w, int h) {
        return new SizeUV(w, h);
    }

    public Rect rect(int x, int y, int w, int h) {
        return new Rect(x, y, w, h);
    }

    public UVBox uvBox(int u, int v, int w, int h) {
        return new UVBox(u, v, w, h);
    }

    public static class Pos {
        public int x;
        public int y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class UV {
        public int u;
        public int v;

        public UV(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public int getU() {
            return u;
        }

        public void setU(int u) {
            this.u = u;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }
    }

    public static class Size {
        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class SizeUV {
        public int uWidth;
        public int vHeight;

        public SizeUV(int uWidth, int uHeight) {
            this.uWidth = uWidth;
            this.vHeight = uHeight;
        }

        public int getUWidth() {
            return uWidth;
        }

        public void setUWidth(int uWidth) {
            this.uWidth = uWidth;
        }

        public int getVHeight() {
            return vHeight;
        }

        public void setVHeight(int vHeight) {
            this.vHeight = vHeight;
        }
    }

    public static class Rect {
        public int x;
        public int y;
        public int width;
        public int height;

        public Rect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static class UVBox {
        public int u;
        public int v;
        public int uWidth;
        public int vHeight;

        public UVBox(int u, int v, int uWidth, int vHeight) {
            this.u = u;
            this.v = v;
            this.uWidth = uWidth;
            this.vHeight = vHeight;
        }

        public int getU() {
            return u;
        }

        public void setU(int u) {
            this.u = u;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public int getUWidth() {
            return uWidth;
        }

        public void setUWidth(int uWidth) {
            this.uWidth = uWidth;
        }

        public int getVHeight() {
            return vHeight;
        }

        public void setVHeight(int vHeight) {
            this.vHeight = vHeight;
        }
    }

    @SuppressWarnings("RedundantCast")
    public final void drawCenteredString(String text, float x, float y, Color color, boolean shadow) {
        if (shadow) {
            font.drawShadow(pose, text, (float) (x - font.width(text) / 2), y, color.getRGB());
        } else {
            font.draw(pose, text, (float) (x - font.width(text) / 2), y, color.getRGB());
        }
    }

    @SuppressWarnings("RedundantCast")
    public final void drawCenteredString(Component text, float x, float y, Color color, boolean shadow) {
        if (shadow) {
            font.drawShadow(pose, text, (float) (x - font.width(text) / 2), y, color.getRGB());
        } else {
            font.draw(pose, text, (float) (x - font.width(text) / 2), y, color.getRGB());
        }
    }

    /**
     * Draws an ItemStack.
     * <p>
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    public final void drawItemStack(PoseStack pose, ItemStack stack, int x, int y, String altText) {
        pose.translate(0, 0, 32);
        this.itemRenderer.blitOffset = 200.0F;

        Font font = this.font;

        this.itemRenderer.renderAndDecorateItem(stack, x, y);
        this.itemRenderer.renderGuiItemDecorations(font, stack, x, y - (stack.isEmpty() ? 0 : 8), altText);
        this.itemRenderer.blitOffset = 0.0F;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void renderBackground(boolean forceTransparent) {
        if (forceTransparent) {
            screen.fillGradient(pose, 0, 0, screen.width, screen.height, -1072689136, -804253680);
            MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(screen, pose));
        } else {
            screen.renderBackground(this.pose);
        }
    }

    @Deprecated
    public void renderTooltip(ItemStack stack, Point point) {
//        if (gui instanceof AdvancedScreen) {
//            gui.renderTooltip(pose, stack, point.x, point.y);
//        }
    }

    public void renderTooltip(Component text, Point point) {
        screen.renderTooltip(pose, text, point.x, point.y);
    }

    public void renderTooltip(List<? extends FormattedCharSequence> tooltips, Point point) {
        screen.renderTooltip(pose, tooltips, point.x, point.y);
    }

    public PoseStack getPoseStack() {
        return pose;
    }
}
