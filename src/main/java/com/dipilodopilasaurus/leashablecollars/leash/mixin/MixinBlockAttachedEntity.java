package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
//? if >=1.20.5 {
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
//?} else {
/*import net.minecraft.world.entity.decoration.HangingEntity;
*///?}
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
//? if <1.21.2 {
/*import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
*///?}
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MixinLeashKnotEntity's interact guard misses melee, arrows and explosions, and hurt is declared here.
//? if >=1.20.5 {
@Mixin(BlockAttachedEntity.class)
//?} else {
/*@Mixin(HangingEntity.class)
*///?}
public abstract class MixinBlockAttachedEntity {
    //? if >=1.21.2 {
    @Inject(
            method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void playercollars$guardKnot(ServerLevel world, DamageSource source, float amount,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (playercollars$knotIsHeld(world, source)) cir.setReturnValue(false);
    }
    //?} else {
    /*@Inject(
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void playercollars$guardKnot(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Level level = Compat.level((Entity) (Object) this);
        if (level instanceof ServerLevel world && playercollars$knotIsHeld(world, source)) cir.setReturnValue(false);
    }
    *///?}

    private boolean playercollars$knotIsHeld(ServerLevel world, DamageSource source) {
        return (Object) this instanceof LeashFenceKnotEntity knot
                && source.getEntity() instanceof Player player
                && PlayerCollarsMod.blockLeashKnotBreak(world, player, knot);
    }
}
