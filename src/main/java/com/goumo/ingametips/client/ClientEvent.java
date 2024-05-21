package com.goumo.ingametips.client;

import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.gui.EmptyScreen;
import com.goumo.ingametips.client.gui.TipListScreen;
import com.goumo.ingametips.client.gui.widget.IconButton;
import com.goumo.ingametips.client.hud.TipHUD;
import com.goumo.ingametips.client.resource.TipElementManager;
import com.goumo.ingametips.client.resource.UnlockedTipManager;
import com.goumo.ingametips.client.util.GuiUtil;
import com.goumo.ingametips.client.util.TipDisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvent {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderScoreboard(RenderGuiOverlayEvent.Pre event) {
        if(!event.getOverlay().id().equals(VanillaGuiOverlay.SCOREBOARD.id()))return;
        if(!TipHUD.renderQueue.isEmpty()) {
            Screen gui = mc.screen;
            if (gui instanceof PauseScreen || gui instanceof EmptyScreen || gui instanceof TipListScreen) {
                return;
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderOnGUI(ScreenEvent.Render.Post event) {
        Screen gui = event.getScreen();
        if (gui instanceof PauseScreen || gui instanceof ChatScreen || gui instanceof EmptyScreen) {
            int x = mc.getWindow().getGuiScaledWidth()-12;
            int y = mc.getWindow().getGuiScaledHeight()-26;
            if (GuiUtil.renderIconButton(event.getGuiGraphics(), IconButton.ICON_HISTORY, GuiUtil.getMouseX(), GuiUtil.getMouseY(), x, y, 0xFFFFFFFF, 0x80000000)) {
                mc.setScreen(new TipListScreen(gui));
            }
        }
    }

    @SubscribeEvent
    public static void displayFileReadError(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen && !UnlockedTipManager.error.isEmpty()) {
            TipElement ele = new TipElement();
            ele.replaceToError(IngameTips.UNLCOKED_FILE, UnlockedTipManager.error);
            TipDisplayUtil.displayTip(ele, true);
            UnlockedTipManager.error = "";
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        TipDisplayUtil.clearRenderQueue();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TipDisplayUtil.clearRenderQueue();
        TipElementManager.getInstance().saveCustomTips();
    }
}
