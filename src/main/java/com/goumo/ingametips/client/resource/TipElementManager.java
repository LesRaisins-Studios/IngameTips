package com.goumo.ingametips.client.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.TipElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TipElementManager implements ResourceManagerReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Component.class, new Component.Serializer())
            .create();
    private final Map<ResourceLocation, TipElement> tipElements = new HashMap<>();
    private final Map<ResourceLocation, TipElement> customTips = new HashMap<>();

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

    public static TipElement getElement(@NotNull ResourceLocation rl) {
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

    public void saveCustomTips() {
        for(var entry : customTips.entrySet()) {
            saveCustomTip(entry.getKey(), entry.getValue());
        }
    }

    private void saveCustomTip(ResourceLocation rl, TipElement element) {
        if(!element.history) return;
        CustomTipPOJO pojo = new CustomTipPOJO();
        pojo.contents = !element.components.isEmpty() ? element.components.get(0) : Component.empty();
        pojo.fontColor = "0x" + Integer.toHexString(element.fontColor);
        pojo.bgColor = "0x" + Integer.toHexString(element.bgColor);
        pojo.alwaysVisible = element.alwaysVisible;
        pojo.onceOnly = element.onceOnly;
        pojo.hide = element.hide;
        pojo.visibleTime = element.visibleTime;


        try (FileWriter writer = new FileWriter(new File(IngameTips.TIPS, rl.getPath() + ".json"))) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.fillInStackTrace();
            IngameTips.LOGGER.error("Unable to save file: '{}'", IngameTips.UNLCOKED_FILE);
        }
    }

    public void addCustomTip(TipElement ele) {
        customTips.put(ele.id, ele);
    }
}
