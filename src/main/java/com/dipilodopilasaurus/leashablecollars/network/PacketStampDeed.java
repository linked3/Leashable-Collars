package com.dipilodopilasaurus.leashablecollars.network;

import com.dipilodopilasaurus.leashablecollars.FeatureRules;

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
import com.dipilodopilasaurus.leashablecollars.Compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.DeedHolder;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.SignedDeeds;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.Optional;

public record PacketStampDeed(boolean canLeashForcibly) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketStampDeed> ID = new CustomPacketPayload.Type<>(Ids.of("stamp_deed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketStampDeed> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PacketStampDeed::canLeashForcibly, PacketStampDeed::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayer player) {
        Compat.level(player).getServer().execute(() -> {
            if (!FeatureRules.SIGN_NEW_DEEDS.enabled(Compat.level(player))) return;
            ItemStack is = player.getMainHandItem();
            if (!is.isEmpty() && is.is(PlayerCollarsMod.DEED_OF_OWNERSHIP)) {
                OwnerComponent owner = Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                if (owner == null || owner.owned().isPresent()) return;
                String plrName = player.getName().getString();
                is = new ItemStack(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED);
                Components.set(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(
                   owner.uuid(), owner.name(), Optional.of(player.getUUID()), Optional.of(plrName), canLeashForcibly
                ));
                player.getInventory().setItem(Compat.selectedSlot(player.getInventory()), is);
                // The signature, not the paper, is what later force-equips are checked against.
                if (player instanceof DeedHolder holder) {
                    holder.playercollars$signDeed(new SignedDeeds.Entry(owner.uuid(), owner.name(), canLeashForcibly));
                }
            }
        });
    }
}
