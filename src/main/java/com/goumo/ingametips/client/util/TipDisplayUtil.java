package com.goumo.ingametips.client.util;

import com.goumo.ingametips.client.TipElement;
import com.goumo.ingametips.client.resource.TipElementManager;
import com.goumo.ingametips.client.resource.UnlockedTipManager;
import com.goumo.ingametips.client.gui.DebugScreen;
import com.goumo.ingametips.client.hud.TipHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TipDisplayUtil {
    public static void displayTip(ResourceLocation rl, boolean first) {
        displayTip(TipElementManager.getElement(rl), first);
    }

    public static void displayTip(TipElement element, boolean first) {
        if (element.id==null) return;
        if (element.onceOnly && UnlockedTipManager.getManager().isUnlocked(element.id)) return;

        for (TipElement ele : TipHUD.renderQueue) {
            if (ele.id.equals(element.id)) {
                return;
            }
        }

        if (element.history) {
            if (element.id.getNamespace().equals("ingametips_custom")) {
                UnlockedTipManager.getManager().unlockCustom(element);
            } else {
                UnlockedTipManager.getManager().unlock(element);
            }
        }

        if (first) {
            TipHUD.renderQueue.add(0, element);
        } else {
            TipHUD.renderQueue.add(element);
        }
    }

    public static void displayCustomTip(String title, Component content, int visibleTime, boolean history) {
        TipElement ele = new TipElement();
        ele.id = ResourceLocation.tryParse("ingametips_custom:" + title);
        ele.history = history;
        ele.components.add(Component.literal(title));
        ele.components.add(content);

        if (visibleTime == -1) {
            ele.alwaysVisible = true;
        } else {
            ele.visibleTime = visibleTime;
        }

        TipElementManager.getInstance().addCustomTip(ele);
        displayTip(ele, false);
    }

    public static void forceAdd(TipElement ele, boolean first) {
        for (TipElement q : TipHUD.renderQueue) {
            if (q.id.equals(ele.id)) {
                return;
            }
        }

        if (first) {
            TipHUD.renderQueue.add(0, ele);
        } else {
            TipHUD.renderQueue.add(ele);
        }
    }

    public static void pinTip(ResourceLocation ID) {
        for (int i = 0; i < TipHUD.renderQueue.size(); i++) {
            TipElement ele = TipHUD.renderQueue.get(i);
            if (ele.id.equals(ID)) {
                try {
                    TipElement clone = (TipElement)ele.clone();
                    clone.alwaysVisible = true;
                    TipHUD.renderQueue.set(i, clone);
                    break;
                } catch (CloneNotSupportedException e) {
                    e.fillInStackTrace();
                    break;
                }
            }
        }
    }

    public static void moveToFirst(ResourceLocation ID) {
        if (TipHUD.renderQueue.size() <= 1 || TipHUD.renderQueue.get(0).id.equals(ID)) {
            return;
        }
        for (int i = 0; i < TipHUD.renderQueue.size(); i++) {
            TipElement ele = TipHUD.renderQueue.get(i);
            if (ele.id.equals(ID)) {
                TipHUD.renderQueue.remove(i);
                TipHUD.renderQueue.add(0, ele);
                resetTipAnimation();
                return;
            }
        }
    }

    public static void resetTipAnimation() {
        AnimationUtil.removeAnimation("TipFadeIn");
        AnimationUtil.removeAnimation("TipFadeOut");
        AnimationUtil.removeAnimation("TipVisibleTime");
    }

    public static void clearRenderQueue() {
        TipHUD.renderQueue.clear();
        resetTipAnimation();
    }
    public static void openDebugScreen() {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().setScreen(new DebugScreen());
        }
    }
}
