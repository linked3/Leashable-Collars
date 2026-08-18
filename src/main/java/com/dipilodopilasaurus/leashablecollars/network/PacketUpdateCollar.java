package com.dipilodopilasaurus.leashablecollars.network;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapItemColor;
import com.dipilodopilasaurus.leashablecollars.Compat;
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
     * Claims the collar without disturbing an existing deed binding. The dye screen sends
     * {@link OwnerState#ADD} on every edit, and rebuilding the component from scratch dropped the deed's
     * {@code owned}/{@code ownedName} pair, after which the Collar Lock-inator refuses the collar.
     */
    private static void setOwnerPreservingDeed(ItemStack is, ServerPlayer player) {
        OwnerComponent existing = is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (existing == null) {
            is.set(PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(player.getUUID(), player.getName().getString()));
            return;
        }
        if (!existing.uuid().equals(player.getUUID())) {
            return;
        }
        is.set(PlayerCollarsMod.OWNER_COMPONENT_TYPE,
                new OwnerComponent(player.getUUID(), player.getName().getString(), existing.owned(), existing.ownedName()));
    }

    public void handle(ServerPlayer player) {
        player.level().getServer().execute(() -> {
            ItemStack is = player.getMainHandItem();
            if (!is.isEmpty() && is.getItem() instanceof CollarItem) {
                is.set(DataComponents.DYED_COLOR, Compat.dyedColor(color));
                is.set(DataComponents.MAP_COLOR, new MapItemColor(pawColor));
                if (os == OwnerState.DEL) {
                    is.remove(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                } else if (os == OwnerState.ADD) {
                    setOwnerPreservingDeed(is, player);
                }
            }
        });
    }
}
