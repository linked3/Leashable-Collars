package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric {
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
//?} else {
/*import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

//? if fabric {
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
//?} else {
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

    private static void applyLegTransform(PoseStack matrices, PlayerModel model, boolean left) {
        AccessoryItemRenderUtil.transformToBottomFace(matrices, left ? model.leftLeg : model.rightLeg);
        matrices.mulPose(new Quaternionf().rotateXYZ((float) (-Math.PI / 2), 0.0F, 0.0F));
        matrices.translate(0.0, 0.0, 0.125);
        matrices.scale(0.75F, 0.75F, 0.75F);
    }
}
