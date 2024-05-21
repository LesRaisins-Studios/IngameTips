package com.goumo.ingametips.client.resource;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.TipElement;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class UnlockedTipManager {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    private List<ResourceLocation> visible;
    private List<ResourceLocation> hide;
    private List<ResourceLocation> custom;
    private static UnlockedTipManager manager;
    public static String error = "";

    public static UnlockedTipManager getManager() {
        if(manager ==null){
            manager = new UnlockedTipManager();
            if (IngameTips.TIPS.mkdirs()) {
                LOGGER.info("Config path created");
            }
            manager.loadFromFile();
        }
        return manager;
    }

    private UnlockedTipManager() {
        reset();
    }

    public static void setManager(UnlockedTipManager manager) {
        UnlockedTipManager.manager = manager;
    }

    public void loadFromFile() {
        if (!IngameTips.UNLCOKED_FILE.exists()) {
            createFile();
            return;
        }

        LOGGER.debug("Loading unlocked tips");
        try (FileReader reader = new FileReader(IngameTips.UNLCOKED_FILE)) {
            UnlockedTipManager fileManager = GSON.fromJson(reader, UnlockedTipManager.class);
            this.visible = fileManager.visible;
            this.hide = fileManager.hide;
            this.custom = fileManager.custom;

        } catch (IOException | JsonSyntaxException e) {
            e.fillInStackTrace();
            error = "load";
            LOGGER.error("Unable to load file: '{}'", IngameTips.UNLCOKED_FILE);
            createFile();
        }
    }
    // 存储解锁信息至配置文件
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(IngameTips.UNLCOKED_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.fillInStackTrace();
            error = "save";
            LOGGER.error("Unable to save file: '{}'", IngameTips.UNLCOKED_FILE);
        }
    }

    public void createFile() {
        if (IngameTips.UNLCOKED_FILE.exists()) {
            File backupFile = new File(IngameTips.UNLCOKED_FILE + ".bak");
            try {
                Files.copy(IngameTips.UNLCOKED_FILE.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.warn("Old file has been saved as '{}'", backupFile);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }

        LOGGER.debug("Creating file: '{}'", IngameTips.UNLCOKED_FILE);
        try (FileWriter writer = new FileWriter(IngameTips.UNLCOKED_FILE)) {
            reset();
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public List<ResourceLocation> getVisible() {
        return visible;
    }

    public List<ResourceLocation> getHide() {
        return hide;
    }

    public List<ResourceLocation> getCustom() {
        return custom;
    }

    public void unlock(TipElement element) {
        if (isUnlocked(element.id)) return;
        if (element.hide) {
            this.hide.add(element.id);
        } else {
            this.visible.add(element.id);
        }
        saveToFile();
    }

    public void unlockCustom(TipElement element) {
        this.custom.add(element.id);
        saveToFile();
    }

    public void removeUnlocked(ResourceLocation ID) {
        this.visible.remove(ID);
        this.hide.remove(ID);
        this.custom.remove(ID);
        saveToFile();
    }

    public boolean isUnlocked(ResourceLocation ID) {
        return visible.contains(ID) || hide.contains(ID) || custom.contains(ID);
    }

    public void reset() {
        this.visible = new ArrayList<>();
        this.hide = new ArrayList<>();
        this.custom = new ArrayList<>();
    }


}
