package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.client.Minecraft;
//? if >=1.21.4 {
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
//?}
//? if >=1.21.5 {
import net.minecraft.world.phys.Vec2;
//?}
import net.minecraft.client.player.KeyboardInput;
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.21.4 moved the movement keys onto ClientInput; 1.21.5 replaced its two impulse floats with one vector.
//? if >=1.21.5 {
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    @Inject(method = "tick", at = @At("TAIL"))
    private void playercollars$suppressRestrainedInput(CallbackInfo ci) {
        if (!playercollars$restrained()) return;
        this.keyPresses = Input.EMPTY;
        this.moveVector = Vec2.ZERO;
    }
//?} elif >=1.21.4 {
/*@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    @Inject(method = "tick", at = @At("TAIL"))
    private void playercollars$suppressRestrainedInput(CallbackInfo ci) {
        if (!playercollars$restrained()) return;
        this.keyPresses = Input.EMPTY;
        this.leftImpulse = 0.0F;
        this.forwardImpulse = 0.0F;
    }
*///?} else {
/*@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends net.minecraft.client.player.Input {
    //? if <=1.18.2 {
    /^@Inject(method = "tick(Z)V", at = @At("TAIL"))
    private void playercollars$suppressRestrainedInput(boolean slowDown, CallbackInfo ci) {
    ^///?} else {
    @Inject(method = "tick(ZF)V", at = @At("TAIL"))
    private void playercollars$suppressRestrainedInput(boolean slowDown, float sneakSpeed, CallbackInfo ci) {
    //?}
        if (!playercollars$restrained()) return;
        this.leftImpulse = 0.0F;
        this.forwardImpulse = 0.0F;
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.jumping = false;
        this.shiftKeyDown = false;
    }
*///?}

    private static boolean playercollars$restrained() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && PetControlHelper.isMovementRestrainedClient(client.player);
    }
}
