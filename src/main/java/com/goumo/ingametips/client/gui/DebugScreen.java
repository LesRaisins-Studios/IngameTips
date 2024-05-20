package com.goumo.ingametips.client.gui;

import com.goumo.ingametips.client.UnlockedTipManager;
import com.goumo.ingametips.client.gui.widget.IconButton;
import com.goumo.ingametips.client.util.TipDisplayUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DebugScreen extends Screen {
    public DebugScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new IconButton((int) (this.width*0.5-25), (int) (this.height*0.4), IconButton.ICON_CROSS, 0xFFC6FCFF, Component.translatable("tip.gui.clear_queue"), (b) -> {
            TipDisplayUtil.clearRenderQueue();
        }));
        this.addRenderableWidget(new IconButton((int) (this.width*0.5+15), (int) (this.height*0.4), IconButton.ICON_HISTORY, 0xFFFF5340, Component.translatable("tip.gui.reset_unlock"), (b) -> {
            UnlockedTipManager.manager.createFile();
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill((int) (this.width*0.5-30), (int) (this.height*0.4-5), (int) (this.width*0.5+30), (int) (this.height*0.4+15), 0x80000000);
        super.render(graphics, mouseX, mouseY, partialTick);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
