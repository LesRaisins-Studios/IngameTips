package com.goumo.ingametips.network;

import com.goumo.ingametips.client.util.TipDisplayUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CustomTipPacket {
    private final String title;
    private final Component content;
    private final int visibleTime;
    private final boolean history;

    public CustomTipPacket(FriendlyByteBuf buffer) {
        title = buffer.readUtf();
        content = buffer.readComponent();
        visibleTime = buffer.readInt();
        history = buffer.readBoolean();
    }

    public CustomTipPacket(String title, Component content, int visibleTime, boolean history) {
        this.title = title;
        this.content = content;
        this.visibleTime = visibleTime;
        this.history = history;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.title);
        buffer.writeComponent(this.content);
        buffer.writeInt(this.visibleTime);
        buffer.writeBoolean(this.history);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(
                () -> TipDisplayUtil.displayCustomTip(title, content, visibleTime, history)
        );
        ctx.get().setPacketHandled(true);
    }
}
