package com.dipilodopilasaurus.leashablecollars.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >=26.3 {
/*import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
*///?}
//? if <26.3 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
//?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? if >=1.19.4 && <26.3 {
import net.minecraft.world.item.ItemDisplayContext;
//?}
//? if <1.19.4 {
/*import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
*///?}
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.client.LaserRenderer;
import org.spongepowered.asm.mixin.Mixin;
//? if <26.3 {
import org.spongepowered.asm.mixin.Unique;
//?}
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Only the hand renderer knows where the bob and swing drew the pointer, so it hands the pose over.
//? if >=26.3 {
/*@Mixin(FirstPersonHandsAndItemsRenderer.class)
*///?} else {
@Mixin(ItemInHandRenderer.class)
//?}
public class ItemInHandRendererMixin {

    //? if <26.3 {
    @Unique
    private static void keepLaserPose(LivingEntity holder, ItemStack stack, boolean firstPerson, PoseStack poses) {
        if (!firstPerson || holder != Minecraft.getInstance().player) return;
        if (stack.is(PlayerCollarsMod.LASER_POINTER_ITEM)) LaserRenderer.captureHandPose(poses.last().pose());
    }
    //?}

    // The stack handed down differs per version, so the seed is taken here; 26.2 renamed the method to
    // submit*, and 26.3 swapped the player and light arguments for render states.
    //? if >=26.3 {
    /*@Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void playercollars$keepLaserSeed(float partialTick, PoseStack poses, SubmitNodeCollector collector,
                                             PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state,
                                             CallbackInfo ci) {
        LaserRenderer.captureHandSeed(poses.last().pose());
    }
    *///?} elif >=26.2 {
    /*@Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void playercollars$keepLaserSeed(float partialTick, PoseStack poses, SubmitNodeCollector collector,
                                             LocalPlayer player, int light, CallbackInfo ci) {
        LaserRenderer.captureHandSeed(poses.last().pose());
    }
    *///?} elif >=1.21.9 {
    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void playercollars$keepLaserSeed(float partialTick, PoseStack poses, SubmitNodeCollector collector,
                                             LocalPlayer player, int light, CallbackInfo ci) {
        LaserRenderer.captureHandSeed(poses.last().pose());
    }
    //?} else {
    /*@Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void playercollars$keepLaserSeed(float partialTick, PoseStack poses, MultiBufferSource.BufferSource buffers,
                                             LocalPlayer player, int light, CallbackInfo ci) {
        LaserRenderer.captureHandSeed(poses.last().pose());
    }
    *///?}

    // 1.21.5 dropped the left-hand flag, 1.21.9 swapped the buffer source for the submit collector, and
    // 26.3 deleted renderItem, so the pose is taken at submitArmWithItem's submit call instead.
    //? if >=26.3 {
    /*@Inject(method = "submitArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void playercollars$keepLaserPose(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state,
                                             float partialTicks, float xRot, InteractionHand hand, float attack,
                                             ItemStack itemStack, float inverseArmHeight, PoseStack poses,
                                             SubmitNodeCollector collector, int lightCoords, CallbackInfo ci) {
        if (itemStack.is(PlayerCollarsMod.LASER_POINTER_ITEM)) LaserRenderer.captureHandPose(poses.last().pose());
    }
    *///?} elif >=1.21.9 {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void playercollars$keepLaserPose(LivingEntity holder, ItemStack stack, ItemDisplayContext context,
                                             PoseStack poses, SubmitNodeCollector collector, int light, CallbackInfo ci) {
        keepLaserPose(holder, stack, context.firstPerson(), poses);
    }
    //?} elif >=1.21.5 {
    /*@Inject(method = "renderItem", at = @At("HEAD"))
    private void playercollars$keepLaserPose(LivingEntity holder, ItemStack stack, ItemDisplayContext context,
                                             PoseStack poses, MultiBufferSource buffers, int light, CallbackInfo ci) {
        keepLaserPose(holder, stack, context.firstPerson(), poses);
    }
    *///?} elif >=1.19.4 {
    /*@Inject(method = "renderItem", at = @At("HEAD"))
    private void playercollars$keepLaserPose(LivingEntity holder, ItemStack stack, ItemDisplayContext context,
                                             boolean leftHand, PoseStack poses, MultiBufferSource buffers, int light,
                                             CallbackInfo ci) {
        keepLaserPose(holder, stack, context.firstPerson(), poses);
    }
    *///?} else {
    /*@Inject(method = "renderItem", at = @At("HEAD"))
    private void playercollars$keepLaserPose(LivingEntity holder, ItemStack stack, TransformType context,
                                             boolean leftHand, PoseStack poses, MultiBufferSource buffers, int light,
                                             CallbackInfo ci) {
        keepLaserPose(holder, stack, context.firstPerson(), poses);
    }
    *///?}
}
