package com.goumo.ingametips.client;

import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.resource.TipElementManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TipElement implements Cloneable {
    public List<Component> components = new ArrayList<>();
    public ResourceLocation id;
    public boolean alwaysVisible = false;
    public boolean onceOnly = false;
    public boolean hide = false;
    public int visibleTime = 30000;
    public int fontColor = 0xFFC6FCFF;
    public int bgColor = 0xFF000000;
    public Component unlockText = TipElementManager.UNLOCKED_TITLE;
    // 未解锁的tip的提示
    public Component unlockHint = TipElementManager.UNLOCKED_CONTENT;

    public TipElement() {
    }

    public TipElement(List<Component> contents) {
        this.components = contents;
    }

    public TipElement(ResourceLocation id) {
        this.id = id;
    }
    public void replaceToError(File filePath, String type) {
        replaceToError(type, filePath.getPath());
    }

    public void replaceToError(ResourceLocation rl, String type) {
        replaceToError(type, rl.toString());
    }

    private void replaceToError(String type, String path) {
        id = new ResourceLocation(IngameTips.MOD_ID, "error");
        components = new ArrayList<>();
        components.add(Component.translatable("tip." + IngameTips.MOD_ID + ".error." + type));
        components.add(Component.literal(path));
        components.add(Component.translatable("tip.ingametips.error.desc"));

        fontColor = 0xFFFF5340;
        bgColor = 0xFF000000;
        alwaysVisible = true;
        onceOnly = false;
        hide = true;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
