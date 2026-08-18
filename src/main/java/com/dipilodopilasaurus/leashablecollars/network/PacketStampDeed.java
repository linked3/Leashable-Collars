package com.dipilodopilasaurus.leashablecollars.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.Optional;

public class PacketStampDeed implements CustomPacketPayload {
    public static final PacketStampDeed INSTANCE = new PacketStampDeed();
    public static final CustomPacketPayload.Type<PacketStampDeed> ID = new CustomPacketPayload.Type<>(Ids.of("stamp_deed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketStampDeed> CODEC = StreamCodec.unit(INSTANCE);

    private PacketStampDeed() {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayer player) {
        player.level().getServer().execute(() -> {
            ItemStack is = player.getMainHandItem();
            if (!is.isEmpty() && is.is(PlayerCollarsMod.DEED_OF_OWNERSHIP)) {
                OwnerComponent owner = is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                if (owner == null || owner.owned().isPresent()) return;
                String plrName = player.getName().getString();
                is = new ItemStack(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED);
                is.set(PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(
                   owner.uuid(), owner.name(), Optional.of(player.getUUID()), Optional.of(plrName)
                ));
                player.getInventory().setItem(player.getInventory().getSelectedSlot(), is);
            }
        });
    }
}
