package com.goumo.ingametips;

import com.goumo.ingametips.client.TipHandler;
import com.goumo.ingametips.item.DebugItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(IngameTips.MOD_ID)
public class IngameTips {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ingametips";
    public static final File CONFIG_PATH = new File(FMLPaths.CONFIGDIR.get().toFile(), "ingametips");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item", DebugItem::new);

    public IngameTips() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        if (CONFIG_PATH.mkdirs()) {
            LOGGER.info("Path: '{}' created", IngameTips.CONFIG_PATH);
        }
        TipHandler.loadUnlockedFromFile();
    }
}