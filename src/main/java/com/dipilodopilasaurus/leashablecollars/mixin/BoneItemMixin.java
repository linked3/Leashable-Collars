package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.21.2 {
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
//?}
//? if >=26.2 {
/*import net.minecraft.resources.ResourceKey;
*///?}
//? if <1.21.2 {
/*import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

//? if >=26.2 {
/*@Mixin(Items.class)
public abstract class BoneItemMixin {
    @Shadow public static Item registerItem(ResourceKey<Item> key, Item.Properties settings) { return null; }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/item/Item;", ordinal = 21))
    private static Item injectEquippableBone(ResourceKey<Item> key) {
        return registerItem(key, new Item.Properties().equippable(EquipmentSlot.HEAD));
    }
}
*///?}
//? if >=1.21.2 && <26.2 {
@Mixin(Items.class)
public abstract class BoneItemMixin {
    @Shadow public static Item registerItem(String id, Item.Properties settings) { return null; }
    @Shadow public static Item registerItem(String id) { return null; }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;)Lnet/minecraft/world/item/Item;"))
    private static Item injectEquippableBone(String id) {
        return id.equals("bone")
                ? registerItem(id, new Item.Properties().equippable(EquipmentSlot.HEAD))
                : registerItem(id);
    }
}
//?}
// equippable is 1.21.2+; below it getEquipmentSlotForItem is the choke point, static until 1.20.5.
//? if >=1.20.5 && <1.21.2 {
/*@Mixin(LivingEntity.class)
public abstract class BoneItemMixin {
    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private void playercollars$boneIsHeadwear(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.is(Items.BONE)) cir.setReturnValue(EquipmentSlot.HEAD);
    }
}
*///?}
//? if <1.20.5 {
/*@Mixin(LivingEntity.class)
public abstract class BoneItemMixin {
    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private static void playercollars$boneIsHeadwear(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.is(Items.BONE)) cir.setReturnValue(EquipmentSlot.HEAD);
    }
}
*///?}
