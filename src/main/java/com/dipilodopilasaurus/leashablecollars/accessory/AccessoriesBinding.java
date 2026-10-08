//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/^*
 * The Accessories half of a dual-library node. AccessoryItem adds nothing but this interface, so a
 * registered instance is equivalent to extending it and leaves WearableItem library-free.
 ^/
public final class AccessoriesBinding implements Accessory {
    private static final AccessoriesBinding INSTANCE = new AccessoriesBinding();

    private AccessoriesBinding() {
    }

    public static void register() {
        for (Item item : Wearables.all()) {
            AccessoriesAPI.registerAccessory(item, INSTANCE);
        }
    }

    // Unlike the Fabric line, these builds carry no binding-curse check of their own -- there is no
    // CanUnequipCallback for it anywhere in the jar, so the lock has to be enforced here.
    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        return !Enchants.preventsArmorChange(stack);
    }
}
*///?}
