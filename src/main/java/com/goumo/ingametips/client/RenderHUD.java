package com.goumo.ingametips.client;

import com.goumo.ingametips.client.gui.EmptyScreen;
import com.goumo.ingametips.client.gui.TipListScreen;
import com.goumo.ingametips.client.gui.widget.IconButton;
import com.goumo.ingametips.client.hud.TipHUD;
import com.goumo.ingametips.client.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHUD {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final List<TipElement> renderQueue = new ArrayList<>();
    public static TipHUD currentTip;

//    @SubscribeEvent
//    public static void renderOnHUD(RenderGameOverlayEvent.Post event) {
//        if (renderQueue.isEmpty()) return;
//        if (mc.screen != null) {
//            if (!(mc.screen instanceof ChatScreen) && !(mc.screen instanceof EmptyScreen) && !(mc.screen instanceof DebugScreen)) {
//                return;
//            }
//        }
//
//
//        if (currentTip == null) {
//            currentTip = new TipHUD(event.getMatrixStack(), renderQueue.get(0));
//        }
//
//        if (!currentTip.visible) {
//            if (renderQueue.size() <= 1 && mc.screen instanceof EmptyScreen) {
//                mc.popGuiLayer();
//            }
//            TipDisplayUtil.removeCurrent();
//            return;
//
//        } else if (!GuiUtil.isKeyDown(258) && mc.screen instanceof EmptyScreen) {
//            mc.popGuiLayer();
//        }
//
//        currentTip.render(false);
//    }

    @SubscribeEvent
    public static void renderOnGUI(ScreenEvent.Render.Post event) {
        Screen gui = event.getScreen();
        if (gui instanceof PauseScreen || gui instanceof ChatScreen || gui instanceof EmptyScreen) {
            int x = mc.getWindow().getGuiScaledWidth()-12;
            int y = mc.getWindow().getGuiScaledHeight()-26;
            if (GuiUtil.renderIconButton(event.getGuiGraphics(), IconButton.ICON_HISTORY, GuiUtil.getMouseX(), GuiUtil.getMouseY(), x, y, 0xFFFFFFFF, 0x80000000)) {
                mc.setScreen(new TipListScreen(gui instanceof PauseScreen));
            }
        }
    }
}
