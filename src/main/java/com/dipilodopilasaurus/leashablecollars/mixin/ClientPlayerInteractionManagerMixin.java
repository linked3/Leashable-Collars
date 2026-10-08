package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
//? if <1.19 {
/*import net.minecraft.client.multiplayer.ClientLevel;
*///?}
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private Minecraft minecraft;

    //? if <1.19 {
    /*@Inject(method="useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", at=@At("HEAD"), cancellable = true)
    private void playercollars$cancelPawInteractions(LocalPlayer player, ClientLevel level, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    *///?} else {
    @Inject(method="useItemOn", at=@At("HEAD"), cancellable = true)
    private void playercollars$cancelPawInteractions(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //?}
        if (player.isSpectator()) return;
        BlockState block = this.minecraft.level.getBlockState(hitResult.getBlockPos());
        if (PawsItem.shouldPreventBlockInteraction(player, block, player.getItemInHand(hand), player.isShiftKeyDown())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
