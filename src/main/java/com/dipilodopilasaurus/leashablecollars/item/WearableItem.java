package com.dipilodopilasaurus.leashablecollars.item;

//? if fabric && >=1.21.5 {
import io.wispforest.accessories.api.core.AccessoryItem;
//?}
//? if fabric && >=1.19 && <1.21.5 {
/*import io.wispforest.accessories.api.AccessoryItem;
*///?}
//? if fabric && <1.19 {
/*import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.world.entity.LivingEntity;
*///?}
//? if !fabric && !dual || <1.19 {
/*import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
*///?}
//? if !fabric && !dual {
/*import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Wearable registration and binding-curse checks per equipment library; the item itself stays library-free. */
//? if fabric && <1.19 {
/*public class WearableItem extends TrinketItem {
*///?} elif fabric {
public class WearableItem extends AccessoryItem {
//?} elif dual {
/*public class WearableItem extends Item {
*///?} else {
/*public class WearableItem extends Item implements ICurioItem {
*///?}
    public WearableItem(Item.Properties properties) {
        super(properties);
    }

    //? if fabric && <1.19 {
    /*@Override
    public boolean canUnequip(ItemStack stack, SlotReference reference, LivingEntity entity) {
        return !Enchants.preventsArmorChange(stack);
    }
    *///?}

    //? if !fabric && !dual {
    /*// Accessories equips from a right-click by default; Curios does not. Curios' handler runs on
    // RightClickItem, ahead of use(), so a sneaking equip would swallow the collar's dye screen.
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !slotContext.entity().isShiftKeyDown();
    }

    // Accessories honours the binding curse itself; without this on Curios a locked collar just comes off.
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return !Enchants.preventsArmorChange(stack);
    }
    *///?}
}
