package com.goumo.ingametips.client.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goumo.ingametips.client.TipElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TipElementManager implements ResourceManagerReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private final Map<ResourceLocation, TipElement> tipElements = new HashMap<>();

    private static TipElementManager instance;

    public static TipElementManager getInstance() {
        if (instance == null) {
            instance = new TipElementManager();
        }
        return instance;
    }

    private TipElementManager() {
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        tipElements.clear();
        Map<ResourceLocation, Resource> resources = manager.listResources("tips", rl -> rl.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            try(Reader stream = entry.getValue().openAsReader()){
                ResourceLocation path = entry.getKey();
                ResourceLocation id = new ResourceLocation(path.getNamespace(), path.getPath().substring(5, path.getPath().length() - 5));

                TipElementPOJO pojo = GSON.fromJson(stream, TipElementPOJO.class);
                TipElement element = parse(id, pojo);

                tipElements.put(id, element);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static TipElement getElement(ResourceLocation rl) {
        return getInstance().tipElements.get(rl);
    }

    public TipElement parse(@NotNull ResourceLocation rl, TipElementPOJO pojo) {
        TipElement element = new TipElement();
        element.id = rl;
        for(var content : pojo.contents) {
            element.components.add(Component.translatable(content));
        }
        element.fontColor = Integer.parseUnsignedInt(pojo.fontColor.startsWith("0x") ? pojo.fontColor.substring(2) : pojo.fontColor, 16);
        element.bgColor = Integer.parseUnsignedInt(pojo.bgColor.startsWith("0x") ? pojo.bgColor.substring(2) : pojo.bgColor, 16);
        element.alwaysVisible = pojo.alwaysVisible;
        element.onceOnly = pojo.onceOnly;
        element.hide = pojo.hide;
        element.visibleTime = Math.max(pojo.visibleTime, 0);
        element.history = true;

        return element;
    }

}
