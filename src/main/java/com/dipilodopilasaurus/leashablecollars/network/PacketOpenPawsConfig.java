package com.dipilodopilasaurus.leashablecollars.network;

import com.dipilodopilasaurus.leashablecollars.Compat;
//? if >=1.20.5 {
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.network.compat.ByteBufCodecs;
import com.dipilodopilasaurus.leashablecollars.network.compat.CustomPacketPayload;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
import com.dipilodopilasaurus.leashablecollars.network.compat.UUIDUtil;
*///?}
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

/** The screen asking the server to open a paws section; the answer comes back as {@link PacketPawsConfig}. */
public record PacketOpenPawsConfig(UUID petId, int section) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketOpenPawsConfig> ID =
            new CustomPacketPayload.Type<>(Ids.of("open_paws_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenPawsConfig> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketOpenPawsConfig::petId,
            ByteBufCodecs.INT, PacketOpenPawsConfig::section,
            PacketOpenPawsConfig::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayer player) {
        Compat.level(player).getServer().execute(() -> {
            if (!PawsConfigHelper.isSection(section)) return;
            List<EquippedAccessories.EquippedEntry> paws = PawsConfigHelper.validate(player, petId);
            if (paws.isEmpty()) return;
            Net.sendToClient(player, PacketPawsConfig.from(petId, section, paws.get(0).stack()));
        });
    }
}
