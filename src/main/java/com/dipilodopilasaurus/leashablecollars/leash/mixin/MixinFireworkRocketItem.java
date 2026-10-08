package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.level.Level;
import com.dipilodopilasaurus.leashablecollars.leash.LeashImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class MixinFireworkRocketItem {
    // 1.21.2 dropped the stack from Item.use's return, which is part of the selector descriptor.
    //? if >=1.21.2 {
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
    private void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (user instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
    //?} else {
    /*@Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", at = @At("HEAD"), cancellable = true)
    private void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (user instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null) {
            cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
        }
    }
    *///?}
}
