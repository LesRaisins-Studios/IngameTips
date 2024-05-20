package com.goumo.ingametips.client;

import com.goumo.ingametips.IngameTips;
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
    public boolean history = false;
    public int visibleTime = 30000;
    public int fontColor = 0xFFC6FCFF;
    public int bgColor = 0xFF000000;

    public TipElement() {
    }

    public TipElement(List<Component> contents) {
        this.components = contents;
    }

    public TipElement(ResourceLocation id) {
        this.id = id;
    }

    public void replaceToError(File filePath, String type) {
        components = new ArrayList<>();
        components.add(Component.translatable("tip." + IngameTips.MOD_ID + ".error." + type));
        components.add(Component.literal(filePath.getPath()));
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
