//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

import com.dipilodopilasaurus.leashablecollars.EquippedAccessories.EquippedEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/^*
 * Curios-side equipment access, split out so nothing loads it unless Curios is installed. Every
 * method mirrors the one EquippedAccessories names, on the same terms.
 ^/
public final class CuriosAccess {
    private CuriosAccess() {
    }

    public static List<EquippedEntry> equipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return List.of();

        List<EquippedEntry> entries = new ArrayList<>();
        for (SlotResult result : handler.findCurios(predicate)) {
            SlotContext context = result.slotContext();
            // Curios' setter is void and does not refuse, so the write always reports success.
            entries.add(new EquippedEntry(result.stack(), stack -> {
                handler.setEquippedCurio(context.identifier(), context.index(), stack);
                return true;
            }));
        }
        return entries;
    }

    public static boolean hasEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        return handler != null && handler.isEquipped(predicate);
    }

    public static ItemStack firstEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return null;

        return handler.findFirstCurio(predicate).map(SlotResult::stack).orElse(null);
    }

    public static boolean attemptToEquip(LivingEntity entity, ItemStack stack, String slot) {
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (handler == null) return false;
        ICurioStacksHandler slots = handler.getStacksHandler(slot).orElse(null);
        if (slots == null) return false;

        IDynamicStackHandler stacks = slots.getStacks();
        for (int i = 0; i < stacks.getSlots(); i++) {
            if (!stacks.getStackInSlot(i).isEmpty()) continue;
            stacks.setStackInSlot(i, stack);
            return true;
        }
        return false;
    }
}
*///?}
