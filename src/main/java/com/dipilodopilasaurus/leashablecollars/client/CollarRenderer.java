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
import net.minecraft.world.entity.LivingEntity;
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
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
//?} else {
/*import net.minecraft.world.entity.EquipmentSlot;
*///?}
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
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
/*public class CollarRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference reference, EntityModel<? extends LivingEntity> model,
            PoseStack matrices, MultiBufferSource bufferSource, int light, LivingEntity entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(entity));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, bufferSource, light);
        matrices.popPose();
    }
*///?}
//? if fabric && >=1.21.9 {
public class CollarRenderer implements AccessoryRenderer {

    @Override
    public void extractRenderState(ItemStack stack, SlotPath path, AccessoriesStorageLookup storageLookup,
                                   LivingEntity entity, LivingEntityRenderState entityState, AccessoryRenderState accessoryState) {
        accessoryState.setStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE,
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.HEAD, entity));
    }

    @Override
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
                                                           EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
        if (!(model instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(entityState));
        AccessoryItemRenderUtil.render(accessoryState, entityState, matrices, collector);
        matrices.popPose();
    }
//?}
//? if fabric && >=1.21.5 && <1.21.9 {
/*public class CollarRenderer implements AccessoryRenderer {

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(renderState));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, multiBufferSource, light);
        matrices.popPose();
    }
*///?}
//? if fabric && >=1.21.2 && <1.21.5 {
/*public class CollarRenderer implements AccessoryRenderer {

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(renderState));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, multiBufferSource, light);
        matrices.popPose();
    }
*///?}
//? if fabric && >=1.19 && <1.21.2 {
/*public class CollarRenderer implements AccessoryRenderer {

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(reference.entity()));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, multiBufferSource, light);
        matrices.popPose();
    }
*///?}
//? if !fabric && >=1.21.9 {
/*public class CollarRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, SubmitNodeCollector collector, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        LivingEntity entity = slotContext.entity();
        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(entityState));
        AccessoryItemRenderUtil.submit(
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.HEAD, entity),
                entityState, matrices, collector, light);
        matrices.popPose();
    }
*///?}
//? if !fabric && >=1.21.2 && <1.21.9 {
/*public class CollarRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, MultiBufferSource bufferSource, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(entityState));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, bufferSource, light);
        matrices.popPose();
    }
*///?}
//? if !fabric && <1.21.2 {
/*public class CollarRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
            PoseStack matrices, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource, int light,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        matrices.pushPose();
        applyTransform(matrices, playerModel, hasChestplate(slotContext.entity()));
        AccessoryItemRenderUtil.drawHead(AccessoryItemRenderUtil.stripGlint(stack),
                matrices, bufferSource, light);
        matrices.popPose();
    }
*///?}

    /** From 1.21.2 the render state carries this, the only answer the SlotPath rung has. */
    //? if >=1.21.2 {
    private static boolean hasChestplate(LivingEntityRenderState entityState) {
        return entityState instanceof HumanoidRenderState humanoid && !humanoid.chestEquipment.isEmpty();
    }
    //?} else {
    /*static boolean hasChestplate(LivingEntity entity) {
        return !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }
    *///?}

    static void applyTransform(PoseStack matrices, PlayerModel model, boolean hasChestplate) {
        ModelPart body = model.body;
        matrices.translate(body.x * 0.0625f, body.y * 0.0625f, body.z * 0.0625f);
        AccessoryItemRenderUtil.rotateXYZ(matrices, body.xRot, body.yRot, body.zRot + (float) Math.PI);
        //? if <1.19.3 {
        /*matrices.scale(hasChestplate ? 0.7f : 0.85f, 0.85f, hasChestplate ? 1.1f : 0.85f);
        *///?} else {
        matrices.scale((hasChestplate ? 0.7f : 0.85f) * body.xScale, 0.85f * body.yScale, (hasChestplate ? 1.1f : 0.85f) * body.zScale);
        //?}
        matrices.translate(0, hasChestplate ? 0.475 : 0.4125, -0.005);
    }
}
