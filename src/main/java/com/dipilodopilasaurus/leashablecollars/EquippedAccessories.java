package com.dipilodopilasaurus.leashablecollars;

//? if fabric {
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
//?} else {
/*import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The loader seam for accessory slots, and the only place in the tree that names a library: no one library
 * covers this Minecraft range, so Fabric uses Accessories and Forge/NeoForge use Curios.
 */
public final class EquippedAccessories {
    private EquippedAccessories() {
    }

    /** Both libraries hand out something richer, but the two share no supertype and nothing here needs the rest. */
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
        //? if fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return List.of();

        List<EquippedEntry> entries = new ArrayList<>();
        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            entries.add(toEntry(reference));
        }
        return entries;
        //?} else {
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
        //? if fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.isEquipped(predicate);
        //?} else {
        /*ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        return handler != null && handler.isEquipped(predicate);
        *///?}
    }

    /** Ownership lives in our own {@link OwnerComponent}, so this has no library equivalent. */
    @Nullable
    public static ItemStack findOwned(LivingEntity entity, Predicate<ItemStack> predicate, UUID owner, UUID wearer) {
        return firstEquipped(entity, stack -> {
            if (!predicate.test(stack)) return false;
            OwnerComponent ownerComponent = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            return ownerComponent != null
                    && ownerComponent.uuid().equals(owner)
                    && (ownerComponent.owned().isEmpty() || ownerComponent.owned().get().equals(wearer));
        });
    }

    @Nullable
    public static ItemStack firstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        //? if fabric {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) return null;

        for (SlotEntryReference reference : capability.getEquipped(predicate)) {
            return reference.stack();
        }
        return null;
        //?} else {
        /*ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return null;

        return handler.findFirstCurio(predicate).map(SlotResult::stack).orElse(null);
        *///?}
    }

    //? if fabric {
    private static EquippedEntry toEntry(SlotEntryReference reference) {
        return new EquippedEntry(reference.stack(), stack -> reference.reference().setStack(stack));
    }
    //?} else {
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
