package com.goumo.ingametips.client.resource;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TipElementPOJO {
    // tip 的内容，第一个Component将作为标题，后面的则是内容
    public List<Component> contents = new ArrayList<>();
    // 弹窗是否永远显示
    public boolean alwaysVisible = false;
    // 弹窗是否仅出现一次
    public boolean onceOnly = false;
    // tip是否应该在list中隐藏
    public boolean hide = false;
    // 没啥用，准备删了
    public boolean history = false;
    // 弹窗的保持时间
    public int visibleTime = 15000;
    // 字体颜色
    public String fontColor = "0xFFC6FCFF";
    // 背景颜色
    public String bgColor = "0xFF000000";

    public TipElementPOJO() {
    }


}
