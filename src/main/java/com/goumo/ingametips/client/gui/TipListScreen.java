package com.goumo.ingametips.client.gui;

import com.goumo.ingametips.client.TipElement;
import com.goumo.ingametips.client.resource.TipElementManager;
import com.goumo.ingametips.client.resource.UnlockedTipManager;
import com.goumo.ingametips.client.gui.widget.IconButton;
import com.goumo.ingametips.client.util.AnimationUtil;
import com.goumo.ingametips.client.util.GuiUtil;
import com.goumo.ingametips.client.util.TipDisplayUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TipListScreen extends Screen {
    private final boolean background;
    private final Map<ResourceLocation, List<String>> customTipList = new HashMap<>();

    private List<ResourceLocation> tipList;
    private TipElement selectEle = null;
    private int GuiHeight = 0;
    private int listHeight = 0;
    private int textHeight = 0;
    private double lastMouseY = 0;
    private double listScroll = 0;
    private double textScroll = 0;
    private double displayListScroll = 0;
    private double displayTextScroll = 0;
    private final Screen previous;

    public static ResourceLocation select = null;

    public TipListScreen(Screen previous) {
        super(Component.empty());
        this.background = previous instanceof PauseScreen;
        this.previous = previous;
    }

    @Override
    public void init() {
        this.addRenderableWidget(new IconButton(0, 0, IconButton.ICON_CROSS, 0xFFC6FCFF, Component.translatable("tip.gui.close"), (button) -> {
            onClose();
        }));
        this.addRenderableWidget(new IconButton(0, 0, IconButton.ICON_LOCK, 0xFFC6FCFF, Component.translatable("tip.gui.pin"), (button) -> {
            TipDisplayUtil.forceAdd(selectEle, true);
        }));

        tipList = new ArrayList<>(UnlockedTipManager.getManager().getVisible());
//        UnlockedTipManager.manager.getCustom().forEach((c) -> {
//            customTipList.put(c.get(0), c);
//            tipList.add(c.get(0));
//        });

        if (!tipList.contains(select)) {
            select = null;
        }

        GuiHeight = (int)(height*0.8F);
        listHeight = tipList.size()*16;
        textHeight = 0;
        setSelect(select);

        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float fadeIn = AnimationUtil.calcFadeIn(400, "TipListGuiFading", false);
        int BGColor = (int)(fadeIn * (background ? 128 : 77)) << 24;
        int x = width - (int)(width*0.6F*fadeIn);
        int y = height - (int)(height*0.9F*fadeIn);
        int lx = (int)(width*0.99F);
        int ly = (int)(height*0.9F);

        if (background) {
            renderBackground(graphics);
        }

        graphics.fill(x, y-16, lx, y-2, BGColor);
        graphics.fill(lx, y-16, lx+1, y-2, 0xFFC6FCFF);
        graphics.fill(x, y, lx, ly, BGColor);
        graphics.fill(lx, ly, lx+1, y, 0xFFC6FCFF);
        if (fadeIn == 1.0F && select!=null) {
            renderTipContent(graphics, lx, y);
        }

        IconButton closeButton = (IconButton)this.renderables.get(0);
        closeButton.setAlpha(fadeIn);
        closeButton.setXY(lx-12, y-14);

        IconButton lockButton = (IconButton)this.renderables.get(1);
        lockButton.setAlpha(fadeIn);
        lockButton.setXY(lx-27, y-14);

        if (fadeIn > 0.5F && !tipList.isEmpty()) {
            double scale = minecraft.getWindow().getGuiScale();

            PoseStack ps = graphics.pose();

            ps.pushPose();
            ps.translate(0, displayListScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F)*scale), (int)(width*scale), (int)((GuiHeight +16)*scale));
            renderList(graphics, tipList, (int)(width*0.05F), (int)(height*0.1F)-16, mouseX, mouseY);
            RenderSystem.disableScissor();
            ps.popPose();

            if (listHeight > GuiHeight +16) {
                renderScrollBar(graphics, mouseX, mouseY, (int)(width*0.05F)-8, (int)(height*0.1F)-16, 4, GuiHeight +16, listHeight);
            } else {
                setListScroll(0);
            }
        }
        
        displayListScroll = displayListScroll + (listScroll - displayListScroll)*0.2;
        displayTextScroll = displayTextScroll + (textScroll - displayTextScroll)*0.2;
        lastMouseY = mouseY;

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderList(GuiGraphics graphics, List<ResourceLocation> list, int x, int y, int mouseX, int mouseY) {
        //TODO widget
        int BGOutline = -4;

        for (int i = 0; i < list.size(); i++) {
            if (y+i*16+ displayListScroll > height*0.9F || y+i*16+ displayListScroll +32 < height*0.1F) {
                //超出绘制区域
                continue;
            }
            int BGWidth = (int)(width*0.3);
            float progress = 0;

            if (i == 0) {
                progress = AnimationUtil.calcFadeIn(300, "TipListGuiList" + list.get(i), false);
            } else if (AnimationUtil.getProgress("TipListGuiList" + list.get(i-1)) > 0.075F || y+i*16+ displayListScroll < height*0.1F+16) {
                progress = AnimationUtil.calcFadeIn(300, "TipListGuiList" + list.get(i), false);
            }

            if (progress != 0) {
                int fontColor = Mth.clamp((int)(progress * 255), 0x04, 0xFF) << 24 | 0xFFC6FCFF & 0x00FFFFFF;
                int BGAlpha = background ? 128 : 77;
                int BGColor = Mth.clamp((int) (progress * BGAlpha), 0x04, 0xFF) << 24;
                float selOffset;

                if (list.get(i).equals(select)) {
                    AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    selOffset = AnimationUtil.calcFadeIn(100, "TipListListSel" + list.get(i), false) * 10;
                    if (selOffset == 0) {
                        AnimationUtil.removeAnimation("TipListSelColor");
                    }

                } else if (GuiUtil.isMouseIn(mouseX, mouseY, x, (int)(y + i*16 + displayListScroll), BGWidth+3, 9-BGOutline)) {
                    AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    selOffset = AnimationUtil.calcFadeIn(100, "TipListListSel" + list.get(i), false) * 10;
                    if (GuiUtil.isLeftClicked()) {
                        setSelect(list.get(i));
                        AnimationUtil.removeAnimation("TipListSelColor");
                    }

                } else {
                    float last = AnimationUtil.getFadeIn("TipListListSel" + list.get(i));
                    float cal = last-(AnimationUtil.calcFadeOut(100, "TipListListSelD" + list.get(i), false));
                    selOffset = Math.max(0, cal*10);
                    if (selOffset == 0) {
                        AnimationUtil.removeAnimation("TipListListSel" + list.get(i));
                        AnimationUtil.removeAnimation("TipListListSelD" + list.get(i));
                    }
                }

                PoseStack ps = graphics.pose();

                ps.pushPose();
                ps.translate(x*progress + selOffset-BGOutline, y-BGOutline + i*16, 0);
                if (list.get(i).equals(select)) {
                    float selColorP = AnimationUtil.calcFadeIn(200, "TipListSelColor", false);
                    int selColor = Mth.clamp((int)(selColorP * BGAlpha), 0x04, 0xFF) << 24 | 0xFFC6FCFF & 0x00FFFFFF;
                    graphics.fill(BGOutline, BGOutline, BGWidth, 10, selColor);
                } else {
                    graphics.fill(BGOutline, BGOutline, BGWidth, 10, BGColor);
                }
                graphics.fill(BGOutline, BGOutline, BGOutline+1, BGOutline + 10-BGOutline, fontColor);

                ResourceLocation rl = list.get(i);

                TipElement ele = TipElementManager.getElement(rl);

                if(ele == null || ele.components.isEmpty()) {
                    continue;
                }

                Component component = ele.components.get(0);

//                if (font.width(component) > BGWidth) {
//                    String s = component.getString().substring(0, Math.min(component.getString().length(), BGWidth / 6)) + "...";
//                }

                if (list.get(i).equals(select)) {
                    graphics.drawString(font, component, 0, 0, fontColor);
                } else {
                    graphics.drawString(font, component, 0, 0, fontColor, false);
                }
                ps.popPose();
            }
        }
    }

    private void renderTipContent(GuiGraphics graphics, int x, int y) { //TODO 搜索和分组
//        boolean custom = select.startsWith("*custom*");
        if (selectEle == null || !selectEle.id.equals(select)) {
            selectEle = TipElementManager.getElement(select);
        }

        if(selectEle == null) {
            return;
        }

        if (selectEle.hide) {
            //移除不应该存在的提示
            remove(selectEle.id);
            return;
        }

        float textFading = AnimationUtil.calcFadeIn(200, "TipListTextFading", false);
        int textColor = Math.max((int)(textFading * 255), 0x04) << 24 | selectEle.fontColor & 0x00FFFFFF;
        int boxWidth = (int)(width*0.4F);
        double scale = minecraft.getWindow().getGuiScale();

        PoseStack ps = graphics.pose();
        ps.pushPose();

        if (font.width(selectEle.components.get(0).getString()) > x-32 - boxWidth) {
            ps.translate(0, displayTextScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F+4)*scale), (int)(width*scale), (int)((GuiHeight -8)*scale));
            int line = 0;
            for (int i = 0; i < selectEle.components.size(); i++) {
                line += 1 + GuiUtil.formatAndDraw(selectEle.components.get(i), graphics, font, boxWidth + 4, y+4 + line*12,
                        x-8 - boxWidth, textColor, 12, false);
            }
            textHeight = line*12;

        } else if (selectEle.components.size() > 1) {
            graphics.drawString(font, selectEle.components.get(0), boxWidth + 4, y - 12, textColor);
            ps.translate(0, displayTextScroll, 0);
            RenderSystem.enableScissor(0, (int)((int)(height*0.1F+4)*scale), (int)(width*scale), (int)((GuiHeight -8)*scale));
            int line = 0;
            for (int i = 1; i < selectEle.components.size(); i++) {
                line += 1 + GuiUtil.formatAndDraw(selectEle.components.get(i), graphics, font, boxWidth + 4, y+4 + line*12,
                        x-8 - boxWidth, textColor, 12, false);
            }
            textHeight = line*12;

        } else {
            graphics.drawString(font, selectEle.components.get(0), boxWidth + 4, y - 12, textColor);
        }

        RenderSystem.disableScissor();
        ps.popPose();

        if (textHeight > GuiHeight && -displayTextScroll < textHeight-GuiHeight-1) {
            float animation = AnimationUtil.calcBounce(1000, "TipListDownArrow", true)*2;
            ps.pushPose();
            ps.translate(width*0.99F-14, height*0.9F-16-animation, 0);
            GuiUtil.renderIcon(graphics, IconButton.ICON_DOWN, 0, 0, 0xFFC6FCFF);
            ps.popPose();
        }
    }

    private void renderScrollBar(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h, int totalHeight){
        float maxHeight = totalHeight-h;
        int barHeight = (int)Math.max(32, h/(maxHeight+h) * h);
        float barY = (float)((-(displayListScroll-1)/maxHeight)*(h-barHeight));

        if (isDragging() || GuiUtil.isMouseIn(mouseX, mouseY, x, y+(int)barY, w, barHeight) && GuiUtil.isLeftClicked()) {
            setDragging(GuiUtil.isLeftDown());
            if (GuiUtil.isMouseIn(mouseX, mouseY, 0, y, width, h)) {
                setListScroll(listScroll - (mouseY-lastMouseY)*(maxHeight/(h-barHeight)));
            }
        }

        PoseStack ps = graphics.pose();

        graphics.fill(x, y, x+w, y+h, (background ? 128 : 77) << 24);

        ps.pushPose();
        ps.translate(0, barY, 0);
        graphics.fill(x, y, x+w, y+barHeight, 0xFFC6FCFF);
        ps.popPose();
    }

    private void remove(ResourceLocation ID) {
        UnlockedTipManager.getManager().removeUnlocked(ID);
        tipList.remove(select);
        setSelect(null);
        listHeight = tipList.size()*16;
        selectEle = null;
    }

    private void setSelect(@Nullable ResourceLocation s) {
        if (s!=null) {
            int target = tipList.indexOf(s) * 16;
            if (target >= -16) {
                if (target + listScroll < 0) {
                    setListScroll(listScroll + (-listScroll - target));
                } else if (target + listScroll > GuiHeight) {
                    setListScroll(listScroll + (-listScroll - target + GuiHeight));
                }
            }
        } else {
            setListScroll(listScroll);
        }
        select = s;
        setTextScroll(0);

        IconButton button = (IconButton)this.renderables.get(1);
        button.visible = select!=null;
    }

    private void setListScroll(double listScroll) {
        this.listScroll = listScroll == 0 ? 0 : Mth.clamp(listScroll, -listHeight + GuiHeight +16, 0);
    }
    
    private void setTextScroll(double textScroll) {
        this.textScroll = textScroll == 0 ? 0 : Mth.clamp(textScroll, -textHeight + GuiHeight, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (listHeight > GuiHeight +16) {
            if (GuiUtil.isMouseIn((int)mouseX, (int)mouseY, 0, (int)(height*0.1F)-16, (int)(width*0.4F), GuiHeight +16)) {
                setListScroll(listScroll + delta*48);
            }
        }
        if (textHeight > GuiHeight) {
            if (GuiUtil.isMouseIn((int)mouseX, (int)mouseY, (int)(width*0.4F), (int)(height*0.1F), (int)(width*0.59F), GuiHeight)) {
                setTextScroll(textScroll + delta*32);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (tipList.isEmpty()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        int sel = tipList.indexOf(select);
        return switch (keyCode) {
            case 258 -> {
                if (modifiers == 1) {
                    if (sel > 0) {
                        setSelect(tipList.get(sel - 1));
                    } else {
                        setSelect(tipList.get(tipList.size() - 1));
                    }
                } else {
                    if (sel < tipList.size() - 1) {
                        setSelect(tipList.get(sel + 1));
                    } else {
                        setSelect(tipList.get(0));
                    }
                }
                yield true;
            }
            case 83, 264 -> {
                if (sel < tipList.size() - 1) {
                    setSelect(tipList.get(sel + 1));
                } else {
                    setSelect(tipList.get(0));
                }
                yield true;
            }
            case 87, 265 -> {
                if (sel > 0) {
                    setSelect(tipList.get(sel - 1));
                } else {
                    setSelect(tipList.get(tipList.size() - 1));
                }
                yield true;
            }
            case 69 -> {
                onClose();
                yield true;
            }
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    @Override
    public void onClose() {
        super.onClose();

        AnimationUtil.removeAnimation("TipListSelColor");
        AnimationUtil.removeAnimation("TipListDownArrow");
        AnimationUtil.removeAnimation("TipListGuiFading");
        AnimationUtil.removeAnimation("TipListTextFading");
        tipList.forEach((name) -> {
            AnimationUtil.removeAnimation("TipListGuiList" + name);
            AnimationUtil.removeAnimation("TipListListSel" + name);
            AnimationUtil.removeAnimation("TipListListSelD" + name);
        });

        TipDisplayUtil.resetTipAnimation();

        if(minecraft != null && previous != null) {
            minecraft.setScreen(previous);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return background;
    }
}