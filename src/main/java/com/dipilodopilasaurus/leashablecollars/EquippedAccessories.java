package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.component.Components;

//? if fabric && <1.19 {
/*import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.SlotReference;
*///?} elif fabric {
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
//?} elif dual {
/*import com.dipilodopilasaurus.leashablecollars.accessory.AccessoriesAccess;
import com.dipilodopilasaurus.leashablecollars.accessory.AccessoryLibraries;
import com.dipilodopilasaurus.leashablecollars.accessory.CuriosAccess;
*///?} else {
/*import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Equipment access across Trinkets, Accessories and Curios, writes back to the owning slot included. */
public final class EquippedAccessories {
    private EquippedAccessories() {
    }

    /** The equipment libraries share no slot type; callers only need the stack and a write-back. */
    public record EquippedEntry(ItemStack stack, StackSetter setter) {
        /** @return false if the library refused the write. */
        public boolean setStack(ItemStack stack) {
            return setter.set(stack);
        }
    }

    @FunctionalInterface
    public interface StackSetter {
        boolean set(ItemStack stack);
    }

    public static List<ItemStack> getEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        List<ItemStack> stacks = new ArrayList<>();
        for (EquippedEntry entry : getEquippedStacks(entity, predicate)) {
            stacks.add(entry.stack());
        }
        return stacks;
    }

    /** Entries rather than bare stacks, for callers that need to write back into the slot. */
    public static List<EquippedEntry> getEquippedStacks(LivingEntity entity, Predicate<ItemStack> predicate) {
        //? if fabric && <1.19 {
        /*var component = TrinketsApi.getTrinketComponent(entity).orElse(null);
        if (component == null) return List.of();
        List<EquippedEntry> entries = new ArrayList<>();
        for (var equipped : component.getEquipped(predicate)) {
            SlotReference reference = equipped.getA();
            entries.add(new EquippedEntry(equipped.getB(), stack -> {
                reference.inventory().setItem(reference.index(), stack);
                return true;
            }));
        }
        return entries;
        *///?} elif forge && <1.19 {
        /*List<EquippedEntry> entries = new ArrayList<>();
        for (SlotResult result : CuriosApi.getCuriosHelper().findCurios(entity, predicate)) {
            SlotContext context = result.slotContext();
            entries.add(new EquippedEntry(result.stack(), stack -> {
                CuriosApi.getCuriosHelper().setEquippedCurio(entity, context.identifier(), context.index(), stack);
                return true;
            }));
        }
        return entries;
        *///?} elif fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return List.of();

        List<EquippedEntry> entries = new ArrayList<>();
        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            entries.add(toEntry(reference));
        }
        return entries;
        //?} elif dual {
        /*List<EquippedEntry> entries = new ArrayList<>();
        if (AccessoryLibraries.curios()) entries.addAll(CuriosAccess.equipped(entity, predicate));
        if (AccessoryLibraries.accessories()) entries.addAll(AccessoriesAccess.equipped(entity, predicate));
        return entries;
        *///?} else {
        /*ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return List.of();

        List<EquippedEntry> entries = new ArrayList<>();
        for (SlotResult result : handler.findCurios(predicate)) {
            entries.add(toEntry(handler, result));
        }
        return entries;
        *///?}
    }

    public static List<EquippedEntry> getAllEquippedStacks(LivingEntity entity) {
        return getEquippedStacks(entity, stack -> true);
    }

    public static void forEachEquipped(LivingEntity entity, Consumer<ItemStack> consumer) {
        for (EquippedEntry entry : getAllEquippedStacks(entity)) {
            consumer.accept(entry.stack());
        }
    }

    public static boolean hasEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        //? if fabric && <1.19 {
        /*return TrinketsApi.getTrinketComponent(entity).map(component -> component.isEquipped(predicate)).orElse(false);
        *///?} elif forge && <1.19 {
        /*return CuriosApi.getCuriosHelper().findFirstCurio(entity, predicate).isPresent();
        *///?} elif fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.isEquipped(predicate);
        //?} elif dual {
        /*return AccessoryLibraries.curios() && CuriosAccess.hasEquipped(entity, predicate)
                || AccessoryLibraries.accessories() && AccessoriesAccess.hasEquipped(entity, predicate);
        *///?} else {
        /*ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        return handler != null && handler.isEquipped(predicate);
        *///?}
    }

    /** Puts the stack in the first slot that takes it; Curios has no "any free slot" call, so it gets an id. */
    public static boolean attemptToEquip(LivingEntity entity, ItemStack stack, String curiosSlot) {
        //? if fabric && <1.19 {
        /*var component = TrinketsApi.getTrinketComponent(entity).orElse(null);
        if (component == null || stack.isEmpty()) return false;
        for (var group : component.getInventory().values()) {
            for (var inventory : group.values()) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    SlotReference reference = new SlotReference(inventory, i);
                    if (inventory.getItem(i).isEmpty() && TrinketSlot.canInsert(stack, reference, entity)) {
                        inventory.setItem(i, stack);
                        return true;
                    }
                }
            }
        }
        return false;
        *///?} elif fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.attemptToEquipAccessory(stack) != null;
        //?} elif dual {
        /*// Accessories first: it picks any slot that accepts the stack, where Curios needs a named one.
        if (AccessoryLibraries.accessories() && AccessoriesAccess.attemptToEquip(entity, stack)) return true;
        return AccessoryLibraries.curios() && CuriosAccess.attemptToEquip(entity, stack, curiosSlot);
        *///?} else {
        /*//? if <1.19 {
        ICuriosItemHandler handler = CuriosApi.getCuriosHelper().getCuriosHandler(entity).orElse(null);
        //?} else {
        /^ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        ^///?}
        if (handler == null) return false;
        ICurioStacksHandler slots = handler.getStacksHandler(curiosSlot).orElse(null);
        if (slots == null) return false;

        IDynamicStackHandler stacks = slots.getStacks();
        for (int i = 0; i < stacks.getSlots(); i++) {
            if (!stacks.getStackInSlot(i).isEmpty()) continue;
            stacks.setStackInSlot(i, stack);
            return true;
        }
        return false;
        *///?}
    }

    /**
     * Accessories diffs a slot against the stack instance it holds, so an in-place component write never
     * reaches the client; Curios diffs against a copy and needs nothing.
     */
    public static void markChanged(LivingEntity entity, ItemStack stack) {
        //? if fabric && >=1.19 {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return;

        for (SlotEntryReference reference : capability.getEquipped(equipped -> equipped == stack)) {
            reference.reference().setStack(stack);
        }
        //?} elif dual {
        /*// Curios compares against a copy each tick and needs nothing.
        if (AccessoryLibraries.accessories()) AccessoriesAccess.markChanged(entity, stack);
        *///?} else {
        /*// Trinkets and Curios compare against a copy each tick.
        *///?}
    }

    /** Ownership lives in {@link OwnerComponent}, so this has no library equivalent. */
    @Nullable
    public static ItemStack findOwned(LivingEntity entity, Predicate<ItemStack> predicate, UUID owner, UUID wearer) {
        return firstEquipped(entity, stack -> {
            if (!predicate.test(stack)) return false;
            OwnerComponent ownerComponent = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            return ownerComponent != null
                    && ownerComponent.uuid().equals(owner)
                    && (ownerComponent.owned().isEmpty() || ownerComponent.owned().get().equals(wearer));
        });
    }

    @Nullable
    public static ItemStack firstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        //? if <1.19 {
        /*for (EquippedEntry entry : getEquippedStacks(entity, predicate)) return entry.stack();
        return null;
        *///?} elif fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return null;

        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            return reference.stack();
        }
        return null;
        //?} elif dual {
        /*if (AccessoryLibraries.curios()) {
            ItemStack found = CuriosAccess.firstEquipped(entity, predicate);
            if (found != null) return found;
        }
        return AccessoryLibraries.accessories() ? AccessoriesAccess.firstEquipped(entity, predicate) : null;
        *///?} else {
        /*ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return null;

        return handler.findFirstCurio(predicate).map(SlotResult::stack).orElse(null);
        *///?}
    }

    //? if fabric && >=1.19 {
    private static EquippedEntry toEntry(SlotEntryReference reference) {
        return new EquippedEntry(reference.stack(), stack -> reference.reference().setStack(stack));
    }
    //?} elif !fabric && >=1.19 && !dual {
    /*private static EquippedEntry toEntry(ICuriosItemHandler handler, SlotResult result) {
        SlotContext context = result.slotContext();
        // Curios' setter is void and does not refuse, so the write always reports success.
        return new EquippedEntry(result.stack(), stack -> {
            handler.setEquippedCurio(context.identifier(), context.index(), stack);
            return true;
        });
    }
    *///?}
}
