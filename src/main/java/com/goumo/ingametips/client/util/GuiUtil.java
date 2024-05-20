package com.goumo.ingametips.client.util;

import com.goumo.ingametips.client.gui.widget.IconButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<String, List<String>> textWrapCache = new HashMap<>();
    private static int leftClicked = 0;

    public static boolean renderIconButton(GuiGraphics graphics, Point icon, int mouseX, int mouseY, int x, int y, int color, int BGColor) {
        if (color != 0 && isMouseIn(mouseX, mouseY, x, y, 10, 10)) {
            graphics.fill(x, y, x+10, y+10, 50 << 24 | color & 0x00FFFFFF);
        } else if (BGColor != 0) {
            graphics.fill(x, y, x+10, y+10, BGColor);
        }
        return renderButton(graphics, mouseX, mouseY, x, y, 10, 10, icon.X, icon.Y, 10, 10, 80, 80, color, IconButton.ICON_LOCATION);
    }

    public static boolean renderButton(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h,
                                       float uOffset, float vOffset, int uWidth, int vHeight, int textureW, int textureH, int color, ResourceLocation resourceLocation) {
        if (color != 0) {
            float alpha = (color >> 24 & 0xFF) / 255F;
            float r = (color >> 16 & 0xFF) / 255F;
            float g = (color >> 8 & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShaderColor(r, g, b, alpha);

            graphics.blit(resourceLocation, x, y, w, h, uOffset, vOffset, uWidth, vHeight, textureW, textureH);
            RenderSystem.disableBlend();
        } else {
            graphics.blit(resourceLocation, x, y, w, h, uOffset, vOffset, uWidth, vHeight, textureW, textureH);
        }

        RenderSystem.setShaderColor(1,1,1,1);

        return isMouseIn(mouseX, mouseY, x, y, w, h) && isLeftClicked();
    }

    public static void renderIcon(GuiGraphics graphics, Point icon, int x, int y, int color) {
        if (color != 0) {
            float alpha = (color >> 24 & 0xFF) / 255F;
            float r = (color >> 16 & 0xFF) / 255F;
            float g = (color >> 8 & 0xFF) / 255F;
            float b = (color & 0xFF) / 255F;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShaderColor(r, g, b, alpha);
            graphics.blit(IconButton.ICON_LOCATION, x, y, 10, 10, icon.X, icon.Y, 10, 10, 80, 80);
            RenderSystem.disableBlend();
        } else {
            RenderSystem.setShaderTexture(0, IconButton.ICON_LOCATION);
            graphics.blit(IconButton.ICON_LOCATION, x, y, 10, 10, icon.X, icon.Y, 10, 10, 80, 80);
        }
        RenderSystem.setShaderColor(1,1,1,1);
    }

    public static boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + h;
    }

    public static boolean isLeftDown() {
        return GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), 0) == 1;
    }

    public static boolean isLeftClicked() {
        if (isLeftDown()) {
            leftClicked++;
            return leftClicked == 1;
        } else {
            leftClicked = 0;
            return false;
        }
    }

    public static int getMouseX() {
        return (int)(mc.mouseHandler.xpos() * (double)mc.getWindow().getGuiScaledWidth() / (double)mc.getWindow().getScreenWidth());
    }

    public static int getMouseY() {
        return (int)(mc.mouseHandler.ypos() * (double)mc.getWindow().getGuiScaledHeight() / (double)mc.getWindow().getScreenHeight());
    }

    public static boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(mc.getWindow().getWindow(), key) == 1;
    }

    public static int formatAndDraw(Component component, GuiGraphics graphics, Font font, float x, float y, int maxWidth, int color, int lineSpace, boolean shadow) {
        return formatAndDraw(component.getString(), graphics, font, x, y, maxWidth, color, lineSpace, shadow);
    }

    public static int formatAndDraw(String text, GuiGraphics graphics, Font font, float x, float y, int maxWidth, int color, int lineSpace, boolean shadow) {
        text = text.replaceAll("&(?!&)", "§")
                   .replaceAll("\\$GAMEPATH\\$", FMLPaths.GAMEDIR.get().toString().replaceAll("\\\\", "\\\\\\\\"));

        return drawWrapString(text, graphics, font, x, y, maxWidth, color, lineSpace, shadow);
    }

    public static int drawWrapText(Component component, GuiGraphics graphics, Font font, float x, float y, int maxWidth, int color, int lineSpace, boolean shadow) {
        return drawWrapString(component.getString(), graphics, font, x, y, maxWidth, color, lineSpace, shadow);
    }

    public static int drawWrapString(String text, GuiGraphics graphics, Font font, float x, float y, int maxWidth, int color, int lineSpace, boolean shadow) {
        List<String> lines = wrapString(text, font, maxWidth);

        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) {
                if (shadow) {
                    graphics.drawString(font, lines.get(i), (int) x, (int) y, color);
                } else {
                    graphics.drawString(font, lines.get(i), (int) x, (int) y, color, false);
                }
            } else {
                if (shadow) {
                    graphics.drawString(font, lines.get(i), (int) x, (int) (y + (i * lineSpace)), color);
                } else {
                    graphics.drawString(font, lines.get(i), x, y + (i * lineSpace), color, false);
                }
            }
        }

        return lines.size();
    }

    public static List<String> wrapString(String text, Font font, int maxWidth) {
        List<String> lines = new ArrayList<>();
        boolean addToCache = false;
        maxWidth = Math.max(1, maxWidth);
        if (textWrapCache.size() > 1024) textWrapCache.clear();

        if (textWrapCache.containsKey(text + maxWidth)) {
            lines = new ArrayList<>(textWrapCache.get(text + maxWidth));
        } else {
            StringBuilder line = new StringBuilder();
            String[] words = text.split(" ");
            for (String word : words) {
                if (font.width(word) > maxWidth) {
                    for (char c : word.toCharArray()) {
                        String potentialLine = line.toString() + c;
                        int width = font.width(potentialLine);

                        if (width > maxWidth) {
                            if (line.toString().endsWith("§")) {
                                line = new StringBuilder(line.substring(0, line.length() - 1));
                                lines.add(line.toString());
                                line = new StringBuilder("§" + c);
                            } else {
                                lines.add(line.toString());
                                line = new StringBuilder(String.valueOf(c));
                            }
                        } else {
                            line = new StringBuilder(potentialLine);
                        }
                    }
                    line.append(" ");
                } else {
                    String potentialLine = line + word + " ";
                    int width = font.width(potentialLine);

                    if (width > maxWidth) {
                        if (line.toString().endsWith("§")) {
                            line = new StringBuilder(line.substring(0, line.length() - 1));
                            lines.add(line.toString());
                            line = new StringBuilder("§" + word + " ");
                        } else {
                            lines.add(line.toString());
                            line = new StringBuilder(word + " ");
                        }
                    } else {
                        line = new StringBuilder(potentialLine);
                    }
                }

            }

            if (!line.isEmpty()) {
                lines.add(line.toString());
            }

            //为每行开头添加生效的格式化代码
            Pattern pattern = Pattern.compile("§.");
            StringBuilder formattingCode = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, formattingCode + lines.get(i));

                Matcher matcher = pattern.matcher(lines.get(i).substring(formattingCode.length()));
                while (matcher.find() && formattingCode.length() < 32) {
                    if (matcher.group().equals("§r")) {
                        formattingCode = new StringBuilder();
                    } else {
                        formattingCode.append(matcher.group());
                    }
                }
            }
            addToCache = true;
        }

        if (addToCache) {
            textWrapCache.put(text + maxWidth, lines);
        }

        return lines;
    }
}
