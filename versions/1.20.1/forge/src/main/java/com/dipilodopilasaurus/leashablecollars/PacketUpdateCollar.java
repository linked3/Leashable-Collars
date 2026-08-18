package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.item.CollarItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateCollar {
    private final int pawColor;
    private final int color;
    private final OwnerState ownerState;

    public PacketUpdateCollar(ItemStack stack, OwnerState ownerState) {
        CollarItem item = LeashableCollars.COLLAR_ITEM.get();
        this.pawColor = item.getPawColor(stack);
        this.color = item.getColor(stack);
        this.ownerState = ownerState;
    }

    public PacketUpdateCollar(FriendlyByteBuf buffer) {
        this.color = buffer.readInt();
        this.pawColor = buffer.readInt();
        this.ownerState = buffer.readEnum(OwnerState.class);
    }

    public enum OwnerState {
        NOP,
        DEL,
        ADD
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.color);
        buffer.writeInt(this.pawColor);
        buffer.writeEnum(this.ownerState);
    }

    /**
     * Claims the collar without disturbing an existing deed binding. The dye screen sends
     * {@link OwnerState#ADD} on every edit, and rebuilding the tag from scratch dropped the deed's
     * {@code owned}/{@code owned_name} pair, after which the Collar Lock-inator refuses the collar.
     */
    private static void setOwnerPreservingDeed(ItemStack stack, Player player) {
        OwnerData existing = CollarItem.getOwnerData(stack);
        if (existing == null) {
            CollarItem.setOwnerData(stack, new OwnerData(player.getUUID(), player.getName().getString()));
            return;
        }
        if (!existing.uuid().equals(player.getUUID())) {
            return;
        }
        CollarItem.setOwnerData(stack, new OwnerData(player.getUUID(), player.getName().getString(), existing.owned(), existing.ownedName()));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (player == null) {
                return;
            }

            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty() && stack.getItem() instanceof CollarItem item) {
                item.setColor(stack, color);
                item.setPawColor(stack, pawColor);
                if (ownerState == OwnerState.DEL) {
                    item.setOwner(stack, null, null);
                } else if (ownerState == OwnerState.ADD) {
                    setOwnerPreservingDeed(stack, player);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
