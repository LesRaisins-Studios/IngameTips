package com.goumo.ingametips.client;

import com.goumo.ingametips.IngameTips;
import com.goumo.ingametips.client.hud.TipHUD;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = IngameTips.MOD_ID)
public class ClientSetupEvent {
    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        // 注册 HUD
        event.registerAboveAll("ingametips_tiphud", new TipHUD());


    }

}
