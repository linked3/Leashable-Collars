package com.dipilodopilasaurus.leashablecollars.network.compat;

// Vanilla's payload/codec networking arrived in 1.20.5; this reproduces just the surface used here.
//? if <1.20.5 {
/*import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
    Type<? extends CustomPacketPayload> type();

    record Type<T extends CustomPacketPayload>(ResourceLocation id) {
    }
}
*///?}
