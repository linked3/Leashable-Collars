package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a vanilla bone be worn in the head slot, as jlortiz0's Fabric builds do. Upstream's
 * {@code Item.Properties.equippable} is 1.21.2+, so this injects into
 * {@link LivingEntity#getEquipmentSlotForItem(ItemStack)}, which every route into the slot resolves
 * through. Rendering already works; equip-on-right-click lives in {@code BoneEquipHandler}.
 */
@Mixin(LivingEntity.class)
public abstract class BoneEquippableMixin {
    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private static void playercollars$boneIsHeadwear(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.is(Items.BONE)) {
            cir.setReturnValue(EquipmentSlot.HEAD);
        }
    }
}
