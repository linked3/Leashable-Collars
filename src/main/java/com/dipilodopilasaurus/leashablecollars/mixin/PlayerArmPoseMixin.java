package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
//?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
*///?} else {
/*import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if fabric && <1.19 || !fabric && <1.21.2 {
/*import com.dipilodopilasaurus.leashablecollars.client.PawRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

// Vanilla has no arm pose for a use animation it does not know, so the pointer aims at the floor in
// third person; the bow pose runs the arm along the look vector.
//? if >=1.21.9 {
@Mixin(AvatarRenderer.class)
public class PlayerArmPoseMixin {

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
            at = @At("RETURN"), cancellable = true)
    private static void playercollars$aimLaserPointer(Avatar player, ItemStack stack, InteractionHand hand,
                                                      CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (stack.is(PlayerCollarsMod.LASER_POINTER_ITEM) && player.isUsingItem() && player.getUsedItemHand() == hand) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
//?} elif neoforge && >=1.21.4 && <1.21.5 {
/*@Mixin(PlayerRenderer.class)
public class PlayerArmPoseMixin {

    // NeoForge 1.21.4 alone routes the lookup through a four-argument overload of its own and leaves
    // vanilla's three-argument one deprecated and uncalled, so the shorter selector never fires.
    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/client/model/HumanoidModel$ArmPose;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
            at = @At("RETURN"), cancellable = true)
    private static void playercollars$aimLaserPointer(Player player, ItemStack stack, InteractionHand hand,
                                                      HumanoidModel.ArmPose extensionPose,
                                                      CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (stack.is(PlayerCollarsMod.LASER_POINTER_ITEM) && player.isUsingItem() && player.getUsedItemHand() == hand) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
*///?} elif >=1.21.4 {
/*@Mixin(PlayerRenderer.class)
public class PlayerArmPoseMixin {

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
            at = @At("RETURN"), cancellable = true)
    private static void playercollars$aimLaserPointer(Player player, ItemStack stack, InteractionHand hand,
                                                      CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (stack.is(PlayerCollarsMod.LASER_POINTER_ITEM) && player.isUsingItem() && player.getUsedItemHand() == hand) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
*///?} else {
/*@Mixin(PlayerRenderer.class)
public class PlayerArmPoseMixin {

    //? if fabric && <1.19 || !fabric && <1.21.2 {
    /^// Vanilla has posed the arm here; cancelling skips both the skin and sleeve only when paws draw.
    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V", ordinal = 0),
            cancellable = true)
    private void playercollars$renderPaws(PoseStack matrices, MultiBufferSource buffers, int light,
                                         AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        PlayerModel model = ((PlayerRenderer) (Object) this).getModel();
        if (PawRenderer.renderFirstPerson(player, model, arm == model.leftArm, matrices, buffers, light)) {
            ci.cancel();
        }
    }
    ^///?}

    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void playercollars$aimLaserPointer(AbstractClientPlayer player, InteractionHand hand,
                                                      CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (player.isUsingItem() && player.getUsedItemHand() == hand
                && player.getItemInHand(hand).is(PlayerCollarsMod.LASER_POINTER_ITEM)) {
            cir.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        }
    }
}
*///?}
