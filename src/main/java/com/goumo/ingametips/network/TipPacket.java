package com.goumo.ingametips.network;

import com.goumo.ingametips.client.util.TipDisplayUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class TipPacket {
    private final ResourceLocation ID;

    public TipPacket(FriendlyByteBuf buffer) {
        ID = buffer.readResourceLocation();
    }

    public TipPacket(ResourceLocation ID) {
        this.ID = ID;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.ID);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> TipDisplayUtil.displayTip(ID, false));
        ctx.get().setPacketHandled(true);
    }
}
