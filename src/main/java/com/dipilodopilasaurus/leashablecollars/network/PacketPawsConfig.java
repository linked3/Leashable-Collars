package com.dipilodopilasaurus.leashablecollars.network;

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
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;
import com.dipilodopilasaurus.leashablecollars.paws.PawsFilter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.UUID;

/** One paws section's state on its way to the screen; the two attack flags ride along unused. */
public record PacketPawsConfig(
        UUID petId,
        int section,
        boolean unrestricted,
        List<String> entries,
        boolean attackMobs,
        boolean attackPlayers
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketPawsConfig> ID =
            new CustomPacketPayload.Type<>(Ids.of("paws_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPawsConfig> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketPawsConfig::petId,
            ByteBufCodecs.INT, PacketPawsConfig::section,
            ByteBufCodecs.BOOL, PacketPawsConfig::unrestricted,
            NetCodecs.STRING_LIST, PacketPawsConfig::entries,
            ByteBufCodecs.BOOL, PacketPawsConfig::attackMobs,
            ByteBufCodecs.BOOL, PacketPawsConfig::attackPlayers,
            PacketPawsConfig::new);

    public static PacketPawsConfig from(UUID petId, int section, ItemStack paws) {
        if (section == PawsConfigHelper.ATTACK) {
            return new PacketPawsConfig(petId, section, false, List.of(),
                    Components.getOrDefault(paws, PlayerCollarsMod.CAN_ATTACK_MOBS_COMPONENT_TYPE, true),
                    Components.getOrDefault(paws, PlayerCollarsMod.CAN_ATTACK_PLAYERS_COMPONENT_TYPE, true));
        }
        if (section == PawsConfigHelper.ITEMS) {
            PawsFilter<Item> filter = PawsFilter.items();
            var rules = Components.get(paws, PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE);
            return new PacketPawsConfig(petId, section, filter.allowsEverything(rules), filter.approvedIds(rules), true, true);
        }
        PawsFilter<Block> filter = PawsFilter.blocks();
        var rules = Components.get(paws, PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE);
        return new PacketPawsConfig(petId, section, filter.allowsEverything(rules), filter.approvedIds(rules), true, true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
