//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

import com.dipilodopilasaurus.leashablecollars.EquippedAccessories.EquippedEntry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/^*
 * Accessories-side equipment access, split out so nothing loads it unless Accessories is installed.
 * Every method mirrors the one EquippedAccessories names, on the same terms.
 ^/
public final class AccessoriesAccess {
    private AccessoriesAccess() {
    }

    public static List<EquippedEntry> equipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return List.of();

        List<EquippedEntry> entries = new ArrayList<>();
        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            entries.add(new EquippedEntry(reference.stack(), stack -> reference.reference().setStack(stack)));
        }
        return entries;
    }

    public static boolean hasEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.isEquipped(predicate);
    }

    public static ItemStack firstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return null;

        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            return reference.stack();
        }
        return null;
    }

    public static boolean attemptToEquip(LivingEntity entity, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.attemptToEquipAccessory(stack) != null;
    }

    // Accessories diffs a slot against the very stack instance it holds, so a component written in
    // place is never synced until the slot is set again.
    public static void markChanged(LivingEntity entity, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return;

        for (SlotEntryReference reference : capability.getEquipped(equipped -> equipped == stack)) {
            reference.reference().setStack(stack);
        }
    }
}
*///?}
