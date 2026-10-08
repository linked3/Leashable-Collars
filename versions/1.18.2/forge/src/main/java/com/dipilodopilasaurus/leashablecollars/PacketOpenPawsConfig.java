package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.client.screen.PawsConfigMenu;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketOpenPawsConfig {
    private final UUID targetUuid;
    private final boolean heldItems;

    public PacketOpenPawsConfig(UUID targetUuid, boolean heldItems) {
        this.targetUuid = targetUuid;
        this.heldItems = heldItems;
    }

    public PacketOpenPawsConfig(FriendlyByteBuf buffer) {
        this.targetUuid = buffer.readUUID();
        this.heldItems = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(targetUuid);
        buffer.writeBoolean(heldItems);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }

            Player target = player.getLevel().getPlayerByUUID(targetUuid);
            if (target == null) {
                return;
            }

            if (!canConfigure(player, target)) {
                player.displayClientMessage(new net.minecraft.network.chat.TranslatableComponent("item.playercollars.paw_configurator.no_set_non_owner"), true);
                return;
            }

            List<ItemStack> pawStacks = findPawStacks(target);

            if (pawStacks.isEmpty()) {
                player.displayClientMessage(new net.minecraft.network.chat.TranslatableComponent("item.playercollars.paw_configurator.no_paws"), true);
                return;
            }

            PawsItem pawsItem = (PawsItem) pawStacks.get(0).getItem();
            List<PawConfigEntry> initialData = heldItems
                    ? pawsItem.getHeldItemsConfig(pawStacks.get(0))
                    : pawsItem.getCanInteractConfig(pawStacks.get(0));
            List<PawConfigEntry> menuData = List.copyOf(initialData);
            Component title = new net.minecraft.network.chat.TranslatableComponent(heldItems ? "gui.playercollars.paw_configurator.item.title" : "gui.playercollars.paw_configurator.block.title", target.getName());

            NetworkHooks.openGui(player, new PawsConfigMenu.Provider(title, heldItems, menuData, pawStacks), buffer -> {
                buffer.writeBoolean(heldItems);
                PawConfigEntry.writeList(buffer, menuData);
            });
        });
        context.get().setPacketHandled(true);
    }

    private boolean canConfigure(ServerPlayer player, Player target) {
        return LeashableCollars.findOwnedCollar(target, player.getUUID(), targetUuid) != null;
    }

    private List<ItemStack> findPawStacks(Player target) {
        List<ItemStack> pawStacks = new ArrayList<>();
        for (SlotResult slotResult : CuriosApi.getCuriosHelper().findCurios(target, "hands")) {
            if (slotResult.stack().getItem() instanceof PawsItem) {
                pawStacks.add(slotResult.stack());
            }
        }
        return pawStacks;
    }
}