package com.goumo.ingametips.client.resource;

import java.util.ArrayList;
import java.util.List;

public class TipElementPOJO {
    public List<String> contents = new ArrayList<>();
    public boolean alwaysVisible = false;
    public boolean onceOnly = false;
    public boolean hide = false;
    public boolean history = false;
    public int visibleTime = 30000;
    public String fontColor = "0xFFC6FCFF";
    public String bgColor = "0xFF000000";

    public TipElementPOJO() {
    }


}
