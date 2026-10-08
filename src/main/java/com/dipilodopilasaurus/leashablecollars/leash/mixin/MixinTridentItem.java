package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import com.dipilodopilasaurus.leashablecollars.leash.LeashImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if >=1.21.2 {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} else {
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

/**
 * Stops Riptide boosting a leashed player; throwing still works. Three eras as siblings -- 1.21.2 gave
 * releaseUsing a boolean return, 1.21 moved the level onto a component, and neither guard nests.
 */
@Mixin(TridentItem.class)
public class MixinTridentItem {
    //? if >=1.21.2 {
    @Inject(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        if (!(user instanceof Player player)) return;

        if (player instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null
                && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0.0F) {
            cir.setReturnValue(false);
        }
    }
    //?}
    //? if >=1.21 && <1.21.2 {
    /*@Inject(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (!(user instanceof Player player)) return;

        if (player instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null
                && EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0.0F) {
            ci.cancel();
        }
    }
    *///?}
    //? if <1.21 {
    /*@Inject(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (!(user instanceof Player player)) return;

        if (player instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null
                && EnchantmentHelper.getRiptide(stack) > 0) {
            ci.cancel();
        }
    }
    *///?}
}
