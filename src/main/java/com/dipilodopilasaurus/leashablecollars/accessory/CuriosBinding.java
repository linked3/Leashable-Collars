//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/^*
 * The Curios half of a dual-library node. WearableItem cannot implement this directly there, so the
 * behaviour moves here and is attached per item only once Curios is known to be loaded.
 ^/
public final class CuriosBinding implements ICurioItem {
    private static final CuriosBinding INSTANCE = new CuriosBinding();

    private CuriosBinding() {
    }

    public static void register() {
        for (Item item : Wearables.all()) {
            CuriosApi.registerCurio(item, INSTANCE);
        }
    }

    // Curios' handler runs on RightClickItem, ahead of use(), so a sneaking equip would swallow the
    // collar's dye screen.
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !slotContext.entity().isShiftKeyDown();
    }

    // Without this a locked collar just comes back off, and the golden spatula is pointless.
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return !Enchants.preventsArmorChange(stack);
    }
}
*///?}
