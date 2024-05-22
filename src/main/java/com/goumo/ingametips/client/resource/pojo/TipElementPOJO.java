package com.goumo.ingametips.client.resource.pojo;

import com.goumo.ingametips.client.resource.TipElementManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TipElementPOJO {
    // tip 分类
    public ResourceLocation tab = new ResourceLocation("ingametips", "default");
    // tip 的内容，第一个Component将作为标题，后面的则是内容
    public List<Component> contents = new ArrayList<>();
    // 弹窗是否永远显示
    public boolean alwaysVisible = false;
    // 弹窗是否仅出现一次
    public boolean onceOnly = false;
    // 未解释的tip是否应该在list中隐藏，否则将显示“条目未解锁”
    public boolean hide = false;
    // 未解锁的tip的标题
    public Component unlockText = TipElementManager.UNLOCKED_TITLE;
    // 未解锁的tip的提示
    public Component unlockHint = TipElementManager.UNLOCKED_CONTENT;

    // 弹窗的保持时间
    public int visibleTime = 15000;
    // 字体颜色
    public String fontColor = "0xFFC6FCFF";
    // 背景颜色
    public String bgColor = "0xFF000000";

    public TipElementPOJO() {
    }


}
