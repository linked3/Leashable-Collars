package com.dipilodopilasaurus.leashablecollars.inventory;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import net.minecraft.world.entity.player.Player;

/** Which of a pet's inventory slots the owner pinned; entity tags carry it, so the pet cannot tamper. */
public final class InventoryLockManager {
    public static final int LOCKABLE_SLOT_COUNT = 36;
    private static final String LOCK_TAG_PREFIX = "playercollars:inventory_lock:";

    private InventoryLockManager() {
    }

    public static boolean isLocked(Player player, int inventorySlot) {
        if (!FeatureRules.CAN_ACCESS_OWNED_INVENTORY.enabled(Compat.level(player))) return false;
        //? if >=26.1 {
        /*if (!isLockable(inventorySlot) || !player.entityTags().contains(tag(inventorySlot))) return false;
        *///?} else
        if (!isLockable(inventorySlot) || !player.getTags().contains(tag(inventorySlot))) return false;
        // A lock on a slot whose item is gone would pin the empty space, so it clears itself.
        if (player.getInventory().getItem(inventorySlot).isEmpty()) {
            player.removeTag(tag(inventorySlot));
            return false;
        }
        return true;
    }

    public static boolean toggle(Player player, int inventorySlot) {
        if (!isLockable(inventorySlot)) return false;
        boolean locked = !isLocked(player, inventorySlot);
        setLocked(player, inventorySlot, locked);
        return locked;
    }

    public static void setLocked(Player player, int inventorySlot, boolean locked) {
        if (!isLockable(inventorySlot)) return;
        if (locked) player.addTag(tag(inventorySlot));
        else player.removeTag(tag(inventorySlot));
    }

    public static void clearEmptyLocks(Player player) {
        for (int slot = 0; slot < LOCKABLE_SLOT_COUNT; slot++) {
            if (player.getInventory().getItem(slot).isEmpty()) setLocked(player, slot, false);
        }
    }

    private static boolean isLockable(int inventorySlot) {
        return inventorySlot >= 0 && inventorySlot < LOCKABLE_SLOT_COUNT;
    }

    private static String tag(int inventorySlot) {
        return LOCK_TAG_PREFIX + inventorySlot;
    }
}
