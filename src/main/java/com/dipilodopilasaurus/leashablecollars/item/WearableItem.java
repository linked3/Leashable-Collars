package com.dipilodopilasaurus.leashablecollars.item;

//? if fabric {
import io.wispforest.accessories.api.core.AccessoryItem;
//?} else {
/*import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Base for every item this mod puts in an accessory slot. Which slot is data on both libraries --
 * {@code data/accessories/tags/item/} and {@code data/curios/tags/item/} -- so all this has to do is
 * make the item recognisable as a wearable, plus bring Curios up to Accessories' default behaviour.
 */
//? if fabric {
public class WearableItem extends AccessoryItem {
//?} else {
/*public class WearableItem extends Item implements ICurioItem {
*///?}
    public WearableItem(Item.Properties properties) {
        super(properties);
    }

    //? if !fabric {
    /*// Accessories equips from a right-click by default; Curios does not.
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    // Accessories honours the binding curse itself. Without this on Curios a locked collar just comes
    // back off, and the golden spatula (the intended way out) is pointless.
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
    }
    *///?}
}
