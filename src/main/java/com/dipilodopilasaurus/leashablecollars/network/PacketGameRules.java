package com.dipilodopilasaurus.leashablecollars.network;

//? if >=1.20.5 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.network.compat.ByteBufCodecs;
import com.dipilodopilasaurus.leashablecollars.network.compat.CustomPacketPayload;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
*///?}
import com.dipilodopilasaurus.leashablecollars.Ids;

public record PacketGameRules(int mask) implements CustomPacketPayload {
    public static final Type<PacketGameRules> ID = new Type<>(Ids.of("gamerules_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGameRules> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketGameRules::mask, PacketGameRules::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
