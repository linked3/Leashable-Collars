package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/** Shims for vanilla APIs whose shape differs across the versions this tree compiles for. */
public final class Compat {
    private Compat() {
    }

    /** 26.1 added {@code sendOverlayMessage}; before that, {@code displayClientMessage} with the flag. */
    public static void sendOverlayMessage(Player player, Component message) {
        //? if >=26.1 {
        /*player.sendOverlayMessage(message);
        *///?} else
        player.displayClientMessage(message, true);
    }

    /**
     * Vanilla is converting {@code SoundEvents} constants to {@code Holder<SoundEvent>} a few at a time,
     * so two overloads let javac settle it per call site instead of a version guard.
     */
    public static SoundEvent sound(Holder<SoundEvent> holder) {
        return holder.value();
    }

    public static SoundEvent sound(SoundEvent event) {
        return event;
    }

    /** 1.21.5 replaced {@code Inventory.selected} with {@code getSelectedSlot}/{@code getSelectedItem}. */
    public static ItemStack selectedItem(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedItem();
        //?} else {
        /*return inventory.getSelected();
        *///?}
    }

    /** See {@link #selectedItem}. */
    public static int selectedSlot(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else {
        /*return inventory.selected;
        *///?}
    }

    /**
     * 1.21.5 folded the per-component tooltip flag into one {@code tooltip_display} component, so
     * this record lost its second field. Everything here wants it on, which was the old default.
     */
    public static DyedItemColor dyedColor(int rgb) {
        //? if >=1.21.5 {
        return new DyedItemColor(rgb);
        //?} else {
        /*return new DyedItemColor(rgb, true);
        *///?}
    }
}
