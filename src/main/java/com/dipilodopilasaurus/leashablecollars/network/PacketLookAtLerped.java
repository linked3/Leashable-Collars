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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.Ids;

public record PacketLookAtLerped(double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketLookAtLerped> ID = new CustomPacketPayload.Type<>(Ids.of("look_at"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketLookAtLerped> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, PacketLookAtLerped::x,
            ByteBufCodecs.DOUBLE, PacketLookAtLerped::y,
            ByteBufCodecs.DOUBLE, PacketLookAtLerped::z,
            PacketLookAtLerped::new);

    public PacketLookAtLerped(Entity p_132783_) {
        this(p_132783_.getX(), p_132783_.getEyeY(), p_132783_.getZ());
    }

    public Vec3 vec() {
        return new Vec3(x, y, z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
