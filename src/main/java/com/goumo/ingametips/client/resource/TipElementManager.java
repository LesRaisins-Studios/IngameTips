package com.goumo.ingametips.client.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.TipElement;
import com.goumo.ingametips.client.resource.pojo.TipElementPOJO;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TipElementManager implements ResourceManagerReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Component.class, new Component.Serializer())
            .setPrettyPrinting()
            .create();
    public static final Component UNLOCKED_TITLE = Component.translatable("tip.gui.locked");
    public static final Component UNLOCKED_CONTENT = Component.translatable("tip.gui.locked.content");
    private final Map<ResourceLocation, TipElement> tipElements = new HashMap<>();
    private final Map<ResourceLocation, TipElement> customTips = new HashMap<>();

    private static TipElementManager instance;

    public static TipElementManager getInstance() {
        if (instance == null) {
            instance = new TipElementManager();
            instance.loadCustomFromFile();
        }
        return instance;
    }

    private TipElementManager() {
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        tipElements.clear();
        Map<ResourceLocation, Resource> resources = manager.listResources("ingametips", rl -> rl.getPath().endsWith(".json"));
        for (var entry : resources.entrySet()) {
            try(Reader stream = entry.getValue().openAsReader()){
                ResourceLocation path = entry.getKey();
                ResourceLocation id = new ResourceLocation(path.getNamespace(), path.getPath().substring(11, path.getPath().length() - 5));

                TipElementPOJO pojo = GSON.fromJson(stream, TipElementPOJO.class);
                TipElement element = parse(id, pojo);

                tipElements.put(id, element);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public List<ResourceLocation> getAllTipIds() {
        List<ResourceLocation> ids = new ArrayList<>();
        ids.addAll(tipElements.keySet());
        ids.addAll(customTips.keySet());
        return ids;
    }

    @Nullable
    public static TipElement getElement(@NotNull ResourceLocation rl) {
        return getInstance().tipElements.getOrDefault(rl, getInstance().customTips.get(rl));
    }

    public TipElement parse(@NotNull ResourceLocation rl, TipElementPOJO pojo) {
        TipElement element = new TipElement();
        element.id = rl;
        element.components.addAll(pojo.contents);
        element.fontColor = Integer.parseUnsignedInt(pojo.fontColor.startsWith("0x") ? pojo.fontColor.substring(2) : pojo.fontColor, 16);
        element.bgColor = Integer.parseUnsignedInt(pojo.bgColor.startsWith("0x") ? pojo.bgColor.substring(2) : pojo.bgColor, 16);
        element.alwaysVisible = pojo.alwaysVisible;
        element.onceOnly = pojo.onceOnly;
        element.hide = pojo.hide;
        element.visibleTime = Math.max(pojo.visibleTime, 0);
        element.unlockText = pojo.unlockText;
        element.unlockHint = pojo.unlockHint;

        return element;
    }

    public void saveCustomTips() {
        for(var entry : customTips.entrySet()) {
            saveCustomTip(entry.getKey(), entry.getValue());
        }
    }

    public void saveCustomTip(ResourceLocation rl, TipElement element) {
        TipElementPOJO pojo = new TipElementPOJO();
        pojo.contents.addAll(element.components);
        pojo.fontColor = "0x" + Integer.toHexString(element.fontColor);
        pojo.bgColor = "0x" + Integer.toHexString(element.bgColor);
        pojo.alwaysVisible = element.alwaysVisible;
        pojo.onceOnly = element.onceOnly;
        pojo.hide = element.hide;
        pojo.visibleTime = element.visibleTime;


        try (FileWriter writer = new FileWriter(new File(IngameTips.TIPS, rl.getPath() + ".json"))) {
            GSON.toJson(pojo, writer);
        } catch (IOException e) {
            e.fillInStackTrace();
            IngameTips.LOGGER.error("Unable to save file: '{}'", IngameTips.UNLCOKED_FILE);
        }
    }

    public void addCustomTip(TipElement ele) {
        customTips.put(ele.id, ele);
    }

    public void loadCustomFromFile() {
        customTips.clear();
        if (IngameTips.TIPS.mkdirs()) {
            IngameTips.LOGGER.info("Config path created");
        }
        File[] files = IngameTips.TIPS.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try(Reader stream = new java.io.FileReader(file)){
                    ResourceLocation path = new ResourceLocation(IngameTips.MOD_ID, file.getName().substring(0, file.getName().length() - 5));
                    TipElementPOJO pojo = GSON.fromJson(stream, TipElementPOJO.class);
                    TipElement element = parse(path, pojo);
                    customTips.put(path, element);
                } catch (IOException e) {
                    e.fillInStackTrace();
                    IngameTips.LOGGER.error("Unable to load file: '{}'", IngameTips.UNLCOKED_FILE);
                }
            }
        }
    }
}
