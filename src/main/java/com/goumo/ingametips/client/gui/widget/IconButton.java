package com.goumo.ingametips.client.gui.widget;

import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.util.Point;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IconButton extends Button {
    public static final ResourceLocation ICON_LOCATION = new ResourceLocation(IngameTips.MOD_ID, "textures/gui/hud_icon.png");
    public static final Point ICON_MOUSE_LEFT    = new Point(0 ,0 );
    public static final Point ICON_MOUSE_RIGHT   = new Point(10,0 );
    public static final Point ICON_MOUSE_MIDDLE  = new Point(20,0 );
    public static final Point ICON_LOCK          = new Point(10,10);
    public static final Point ICON_RIGHT         = new Point(40,10);
    public static final Point ICON_DOWN          = new Point(50,10);
    public static final Point ICON_LEFT          = new Point(60,10);
    public static final Point ICON_TOP           = new Point(70,10);
    public static final Point ICON_BOX           = new Point(0 ,30);
    public static final Point ICON_BOX_ON        = new Point(10,30);
    public static final Point ICON_CROSS         = new Point(20,30);
    public static final Point ICON_HISTORY       = new Point(30,30);
    public static final Point ICON_TRASH_CAN     = new Point(50,30);

    public final Point currentIcon;
    public final int color;

    public IconButton(int x, int y, Point icon, int color, Component title, OnPress pressedAction) {
        super(x, y, 10, 10, title, pressedAction, DEFAULT_NARRATION);
        this.color = color;
        this.currentIcon = icon;
    }

    public void setXY(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        if (isHovered) {
            graphics.fill(x, y, x+width, y+height, 50 << 24 | color & 0x00FFFFFF);
            renderToolTip(graphics, mouseX, mouseY);
        }

        float r = (color >> 16 & 0xFF) / 255F;
        float g = (color >> 8 & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(r, g, b, alpha);

        graphics.blit(ICON_LOCATION, x, y, currentIcon.X, currentIcon.Y, 10, 10, 80, 80);

        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.disableBlend();
    }



    public void renderToolTip(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        String text = getMessage().getString();
        if (!text.isEmpty()) {
            int textWidth = mc.font.width(text);
            int x = getX();
            int y = getY();
            int renderX = x-textWidth+8;

            if (renderX < 0) {
                graphics.fill(x, y - 12, x+2 + textWidth, y, 50 << 24 | color & 0x00FFFFFF);
                graphics.drawString(mc.font, text, x+2, y-10, color);
            } else {
                graphics.fill(x+8 - textWidth, y - 12, x + 10, y, 50 << 24 | color & 0x00FFFFFF);
                graphics.drawString(mc.font, text, x-textWidth+width, y-10, color);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 258) {
            return false;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
