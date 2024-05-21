package com.goumo.ingametips.client.util;

import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.TipElement;
import com.goumo.ingametips.client.resource.TipElementManager;
import com.goumo.ingametips.client.resource.UnlockedTipManager;
import com.goumo.ingametips.client.gui.DebugScreen;
import com.goumo.ingametips.client.hud.TipHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TipDisplayUtil {
    /** Display a tip with the given resource location.
     * @param rl The resource location of the tip.
     * @param first Whether the tip should be added to the front of the queue.
     * */
    public static void displayTip(ResourceLocation rl, boolean first) {
        TipElement element = TipElementManager.getElement(rl);
        if(element == null) {
            element = new TipElement();
            element.replaceToError(rl, "not_exists");
        }

        displayTip(element, first);
    }

    public static void displayTip(@NotNull TipElement element, boolean first) {
        if (element.id==null) return;
        if (element.onceOnly && UnlockedTipManager.getManager().isUnlocked(element.id)) return;

        for (TipElement ele : TipHUD.renderQueue) {
            if (ele.id.equals(element.id)) {
                return;
            }
        }

        UnlockedTipManager.getManager().unlock(element);

        if (first) {
            TipHUD.renderQueue.add(0, element);
        } else {
            TipHUD.renderQueue.add(element);
        }
    }

    public static void displayCustomTip(String id, Component title, Component content, int visibleTime) {
        TipElement ele = new TipElement();
        ele.id = new ResourceLocation(IngameTips.MOD_ID, id);
        ele.components.add(title);
        ele.components.add(content);

        if (visibleTime == -1) {
            ele.alwaysVisible = true;
        } else {
            ele.visibleTime = visibleTime;
        }

        TipElementManager.getInstance().addCustomTip(ele);
        TipElementManager.getInstance().saveCustomTip(ele.id, ele);
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
