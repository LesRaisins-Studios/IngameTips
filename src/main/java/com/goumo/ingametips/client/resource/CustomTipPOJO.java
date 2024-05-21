package com.goumo.ingametips.client.resource;

import net.minecraft.network.chat.Component;

public class CustomTipPOJO {
    public Component contents = Component.empty();
    public boolean alwaysVisible = false;
    public boolean onceOnly = false;
    public boolean hide = false;
    public boolean history = false;
    public int visibleTime = 15000;
    public String fontColor = "0xFFC6FCFF";
    public String bgColor = "0xFF000000";

    public CustomTipPOJO() {
    }
}
