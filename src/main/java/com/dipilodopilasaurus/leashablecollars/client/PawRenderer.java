package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric {
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
//?} else {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

//? if fabric {
public class PawRenderer implements AccessoryRenderer {

    @Override
    public void extractRenderState(ItemStack stack, SlotPath path, AccessoriesStorageLookup storageLookup,
                                   LivingEntity entity, LivingEntityRenderState entityState, AccessoryRenderState accessoryState) {
        accessoryState.setStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE,
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.FIXED, entity));
    }

    /** Paws draw in first person -- you see them instead of your hands -- so an arm-only pass is not skipped. */
    @Override
    public boolean shouldRender(ItemStack stack, SlotPath path, AccessoriesStorageLookup storageLookup,
                                LivingEntity entity, LivingEntityRenderState entityState, boolean isRenderingEnabled) {
        return entityState.hasStateData(AccessoriesRenderStateKeys.ARM)
                ? isRenderingEnabled
                : AccessoryRenderer.super.shouldRender(stack, path, storageLookup, entity, entityState, isRenderingEnabled);
    }

    @Override
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
                                                           EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
        if (!(model instanceof PlayerModel playerModel)) return;

        // Accessories runs the first-person arm through the same render call, flagged on the state.
        HumanoidArm firstPersonArm = entityState.getStateData(AccessoriesRenderStateKeys.ARM);
        if (firstPersonArm != null) {
            drawArm(accessoryState, entityState, matrices, collector, playerModel, firstPersonArm == HumanoidArm.LEFT, true);
        } else {
            drawArm(accessoryState, entityState, matrices, collector, playerModel, false, false);
            drawArm(accessoryState, entityState, matrices, collector, playerModel, true, false);
        }
    }

    private static void drawArm(AccessoryRenderState accessoryState, LivingEntityRenderState entityState, PoseStack matrices,
                                SubmitNodeCollector collector, PlayerModel model, boolean left, boolean firstPerson) {
        matrices.pushPose();
        applyArmTransform(matrices, model, left, firstPerson);
        AccessoryItemRenderUtil.render(accessoryState, entityState, matrices, collector);
        matrices.popPose();
    }
//?} else {
/*public class PawRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, SubmitNodeCollector collector, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        ItemStackRenderState itemState =
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.FIXED, slotContext.entity());
        drawArm(itemState, entityState, matrices, collector, light, playerModel, false, false);
        drawArm(itemState, entityState, matrices, collector, light, playerModel, true, false);
    }

    /^*
     * Paws draw in first person -- you see them instead of your hands. Curios gives this its own call
     * and hands over no model, so the player's own comes from the dispatcher.
     ^/
    @Override
    public void renderFirstPersonHand(ItemStack stack, SlotContext slotContext, HumanoidArm arm, PoseStack matrices,
                                      SubmitNodeCollector collector, AvatarRenderState entityState,
                                      AbstractClientPlayer clientPlayer, int light) {
        PlayerModel playerModel = playerModelOf(clientPlayer);
        if (playerModel == null) return;

        ItemStackRenderState itemState =
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.FIXED, clientPlayer);
        drawArm(itemState, entityState, matrices, collector, light, playerModel, arm == HumanoidArm.LEFT, true);
    }

    @Nullable
    private static PlayerModel playerModelOf(AbstractClientPlayer player) {
        EntityRenderer<?, ?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        return renderer instanceof LivingEntityRenderer<?, ?, ?> living && living.getModel() instanceof PlayerModel model
                ? model
                : null;
    }

    private static void drawArm(ItemStackRenderState itemState, LivingEntityRenderState entityState, PoseStack matrices,
                                SubmitNodeCollector collector, int light, PlayerModel model, boolean left, boolean firstPerson) {
        matrices.pushPose();
        applyArmTransform(matrices, model, left, firstPerson);
        AccessoryItemRenderUtil.submit(itemState, entityState, matrices, collector, light);
        matrices.popPose();
    }
*///?}

    /**
     * Constants are upstream v1.7.4's, which are known-good against Accessories' {@code transformToFace}
     * anchor. Re-tune from here; the older Trinkets numbers were measured against a different transform.
     */
    private static void applyArmTransform(PoseStack matrices, PlayerModel model, boolean left, boolean firstPerson) {
        AccessoryItemRenderUtil.transformToBottomFace(matrices, left ? model.leftArm : model.rightArm);
        if (firstPerson) {
            matrices.mulPose(new Quaternionf().rotateXYZ((float) Math.PI, 0.0F, 0.0F));
            matrices.translate(left ? 0.0 : 0.015625, -0.1875, left ? -0.135 : -0.14);
            matrices.scale(model.slim ? 0.59375F : 0.75F, 0.75F, 1.03125F);
        } else {
            matrices.mulPose(new Quaternionf().rotateXYZ((float) Math.PI, (float) (left ? Math.PI : -Math.PI) / 2.0F, 0.0F));
            matrices.translate(0.0, -0.1875, -0.125);
            matrices.scale(0.75F, 0.625F, model.slim ? 0.875F : 1.03125F);
        }
    }
}
