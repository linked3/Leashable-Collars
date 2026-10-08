package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric && <1.19 {
/*import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
*///?}
//? if fabric && >=1.21.9 {
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
//?}
//? if fabric && >=1.21.5 && <1.21.9 {
/*import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
*///?}
//? if fabric && >=1.21.2 && <1.21.5 {
/*import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
*///?}
//? if fabric && >=1.19 && <1.21.2 {
/*import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
*///?}
//? if !fabric && >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
//? if !fabric && >=1.21.2 && <1.21.9 {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
//? if !fabric && <1.21.2 {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
import net.minecraft.client.model.EntityModel;
//? if >=1.21.11 {
import net.minecraft.client.model.player.PlayerModel;
//?} else {
/*import net.minecraft.client.model.PlayerModel;
*///?}
//? if >=1.20 {
import net.minecraft.world.item.ItemDisplayContext;
//?}
import net.minecraft.world.item.ItemStack;

//? if fabric && <1.19 {
/*public class FootPawRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference reference, EntityModel<? extends LivingEntity> model,
            PoseStack matrices, MultiBufferSource bufferSource, int light, LivingEntity entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.21.9 {
public class FootPawRenderer implements AccessoryRenderer {

    @Override
    public void extractRenderState(ItemStack stack, SlotPath path, AccessoriesStorageLookup storageLookup,
                                   LivingEntity entity, LivingEntityRenderState entityState, AccessoryRenderState accessoryState) {
        accessoryState.setStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE,
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.FIXED, entity));
    }

    @Override
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
                                                           EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
        if (!(model instanceof PlayerModel playerModel)) return;

        for (boolean left : new boolean[]{false, true}) {
            matrices.pushPose();
            applyLegTransform(matrices, playerModel, left);
            AccessoryItemRenderUtil.render(accessoryState, entityState, matrices, collector);
            matrices.popPose();
        }
    }
//?}
//? if fabric && >=1.21.5 && <1.21.9 {
/*public class FootPawRenderer implements AccessoryRenderer {

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.21.2 && <1.21.5 {
/*public class FootPawRenderer implements AccessoryRenderer {

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.19 && <1.21.2 {
/*public class FootPawRenderer implements AccessoryRenderer {

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if !fabric && >=1.21.9 {
/*public class FootPawRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, SubmitNodeCollector collector, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        ItemStackRenderState itemState =
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.FIXED, slotContext.entity());
        for (boolean left : new boolean[]{false, true}) {
            matrices.pushPose();
            applyLegTransform(matrices, playerModel, left);
            AccessoryItemRenderUtil.submit(itemState, entityState, matrices, collector, light);
            matrices.popPose();
        }
    }
*///?}
//? if !fabric && >=1.21.2 && <1.21.9 {
/*public class FootPawRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, MultiBufferSource bufferSource, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }
*///?}
//? if !fabric && <1.21.2 {
/*public class FootPawRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
            PoseStack matrices, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource, int light,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }
*///?}

    //? if <1.21.9 {
    /*static void drawLegs(ItemStack stack, PoseStack matrices, MultiBufferSource bufferSource, int light,
                                 PlayerModel model) {
        for (boolean left : new boolean[]{false, true}) {
            matrices.pushPose();
            applyLegTransform(matrices, model, left);
            AccessoryItemRenderUtil.drawFixed(stack, matrices, bufferSource, light);
            matrices.popPose();
        }
    }
    *///?}

    private static void applyLegTransform(PoseStack matrices, PlayerModel model, boolean left) {
        AccessoryItemRenderUtil.transformToBottomFace(matrices, left ? model.leftLeg : model.rightLeg);
        AccessoryItemRenderUtil.rotateXYZ(matrices, (float) (-Math.PI / 2), 0.0F, 0.0F);
        matrices.translate(0.0, 0.0, 0.125);
        matrices.scale(0.75F, 0.75F, 0.75F);
    }
}
