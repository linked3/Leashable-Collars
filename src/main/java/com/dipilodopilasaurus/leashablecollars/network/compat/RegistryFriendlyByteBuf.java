package com.dipilodopilasaurus.leashablecollars.network.compat;

// Pre-1.20.5 a packet buffer carries no registry access, and nothing in these packets needs them.
//? if <1.20.5 {
/*import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
    public RegistryFriendlyByteBuf(ByteBuf source) {
        super(source);
    }
}
*///?}
