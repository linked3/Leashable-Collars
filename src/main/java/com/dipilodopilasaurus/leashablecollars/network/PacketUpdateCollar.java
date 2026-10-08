package com.dipilodopilasaurus.leashablecollars.network;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.component.Components;

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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.item.CollarItem;

public record PacketUpdateCollar(OwnerState os, int pawColor, int color) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketUpdateCollar> ID = new CustomPacketPayload.Type<>(Ids.of("update_collar"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateCollar> CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(OwnerState::fromInt, OwnerState::ordinal), PacketUpdateCollar::os,
            ByteBufCodecs.INT, PacketUpdateCollar::pawColor,
            ByteBufCodecs.INT, PacketUpdateCollar::color,
            PacketUpdateCollar::new);
    public PacketUpdateCollar(ItemStack is, OwnerState os) {
        this(os, CollarItem.getPawColor(is), CollarItem.getColor(is));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public enum OwnerState {
        NOP, DEL, ADD;

        public static OwnerState fromInt(int ind) {
            return OwnerState.values()[ind];
        }
    }

    /**
     * Claims the collar without disturbing an existing deed binding -- rebuilding the component dropped
     * the deed's owned/ownedName pair, after which the Collar Lock-inator refuses the collar.
     */
    private static void setOwnerPreservingDeed(ItemStack is, ServerPlayer player) {
        OwnerComponent existing = Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (existing == null) {
            Components.set(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(player.getUUID(), player.getName().getString()));
            return;
        }
        if (!existing.uuid().equals(player.getUUID())) {
            return;
        }
        Components.set(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE,
                new OwnerComponent(player.getUUID(), player.getName().getString(), existing.owned(), existing.ownedName()));
    }

    public void handle(ServerPlayer player) {
        Compat.level(player).getServer().execute(() -> {
            ItemStack is = player.getMainHandItem();
            if (!is.isEmpty() && is.getItem() instanceof CollarItem) {
                Components.setDyeColor(is, color);
                Components.setPawColor(is, pawColor);
                if (os == OwnerState.DEL) {
                    Components.remove(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                } else if (os == OwnerState.ADD) {
                    setOwnerPreservingDeed(is, player);
                }
            }
        });
    }
}
