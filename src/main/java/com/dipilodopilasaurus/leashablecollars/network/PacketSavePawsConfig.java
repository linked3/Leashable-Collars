package com.dipilodopilasaurus.leashablecollars.network;

import com.dipilodopilasaurus.leashablecollars.Compat;
//? if >=1.20.5 {
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.core.component.DataComponentType;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.network.compat.ByteBufCodecs;
import com.dipilodopilasaurus.leashablecollars.network.compat.CustomPacketPayload;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
import com.dipilodopilasaurus.leashablecollars.network.compat.UUIDUtil;
import com.dipilodopilasaurus.leashablecollars.component.compat.DataComponentType;
*///?}
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;
import com.dipilodopilasaurus.leashablecollars.paws.PawsFilter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.UUID;

/** The paws screens' Done button. Same shape as {@link PacketPawsConfig}, travelling the other way. */
public record PacketSavePawsConfig(
        UUID petId,
        int section,
        boolean unrestricted,
        List<String> entries,
        boolean attackMobs,
        boolean attackPlayers
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketSavePawsConfig> ID =
            new CustomPacketPayload.Type<>(Ids.of("save_paws_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSavePawsConfig> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketSavePawsConfig::petId,
            ByteBufCodecs.INT, PacketSavePawsConfig::section,
            ByteBufCodecs.BOOL, PacketSavePawsConfig::unrestricted,
            NetCodecs.STRING_LIST, PacketSavePawsConfig::entries,
            ByteBufCodecs.BOOL, PacketSavePawsConfig::attackMobs,
            ByteBufCodecs.BOOL, PacketSavePawsConfig::attackPlayers,
            PacketSavePawsConfig::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayer player) {
        Compat.level(player).getServer().execute(() -> {
            if (!PawsConfigHelper.isSection(section)) return;
            List<EquippedAccessories.EquippedEntry> paws = PawsConfigHelper.validate(player, petId);
            if (paws.isEmpty()) return;

            for (EquippedAccessories.EquippedEntry entry : paws) {
                apply(entry.stack());
            }
            PawsConfigHelper.announce(player, petId);
        });
    }

    private void apply(ItemStack stack) {
        switch (section) {
            case PawsConfigHelper.ATTACK -> {
                Components.set(stack, PlayerCollarsMod.CAN_ATTACK_MOBS_COMPONENT_TYPE, attackMobs);
                Components.set(stack, PlayerCollarsMod.CAN_ATTACK_PLAYERS_COMPONENT_TYPE, attackPlayers);
            }
            case PawsConfigHelper.ITEMS -> {
                PawsFilter<Item> filter = PawsFilter.items();
                write(stack, PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE, unrestricted ? filter.everything() : filter.fromIds(entries));
            }
            default -> {
                PawsFilter<Block> filter = PawsFilter.blocks();
                write(stack, PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE, unrestricted ? filter.everything() : filter.fromIds(entries));
            }
        }
    }

    // An empty allow-list and no component mean the same thing, so the smaller of the two is stored.
    private static <T> void write(ItemStack stack, DataComponentType<List<T>> type, List<T> rules) {
        if (rules.isEmpty()) Components.remove(stack, type);
        else Components.set(stack, type, rules);
    }
}
