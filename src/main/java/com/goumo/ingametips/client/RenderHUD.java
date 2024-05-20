package com.goumo.ingametips.client;

import com.goumo.ingametips.client.gui.EmptyScreen;
import com.goumo.ingametips.client.gui.TipListScreen;
import com.goumo.ingametips.client.gui.widget.IconButton;
import com.goumo.ingametips.client.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHUD {
    private static final Minecraft mc = Minecraft.getInstance();

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
