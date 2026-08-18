package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric {
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.minecraft.util.context.ContextKey;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
//?} else {
/*import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

//? if fabric {
public class CollarRenderer implements AccessoryRenderer {
    /**
     * A chestplate pushes the collar out and up. Only the entity knows, and Accessories' render phase
     * no longer has one, so it is captured during extraction.
     */
    private static final ContextKey<Boolean> HAS_CHESTPLATE =
            new ContextKey<>(Ids.of("collar_renderer_has_chestplate"));

    @Override
    public void extractRenderState(ItemStack stack, SlotPath path, AccessoriesStorageLookup storageLookup,
                                   LivingEntity entity, LivingEntityRenderState entityState, AccessoryRenderState accessoryState) {
        accessoryState.setStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE,
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.HEAD, entity));
        accessoryState.setStateData(HAS_CHESTPLATE, hasChestplate(entity));
    }

    @Override
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState,
                                                           EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
        if (!(model instanceof PlayerModel playerModel)) return;

        applyTransform(matrices, playerModel, Boolean.TRUE.equals(accessoryState.getStateData(HAS_CHESTPLATE)));
        AccessoryItemRenderUtil.render(accessoryState, entityState, matrices, collector);
    }
//?} else {
/*public class CollarRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, SubmitNodeCollector collector, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        LivingEntity entity = slotContext.entity();
        applyTransform(matrices, playerModel, hasChestplate(entity));
        AccessoryItemRenderUtil.submit(
                AccessoryItemRenderUtil.createItemRenderState(stack, ItemDisplayContext.HEAD, entity),
                entityState, matrices, collector, light);
    }
*///?}

    private static boolean hasChestplate(LivingEntity entity) {
        return !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    private static void applyTransform(PoseStack matrices, PlayerModel model, boolean hasChestplate) {
        ModelPart body = model.body;
        matrices.translate(body.x * 0.0625f, body.y * 0.0625f, body.z * 0.0625f);
        matrices.mulPose(new Quaternionf().rotateXYZ(body.xRot, body.yRot, body.zRot + (float) Math.PI));
        matrices.scale((hasChestplate ? 0.7f : 0.85f) * body.xScale, 0.85f * body.yScale, (hasChestplate ? 1.1f : 0.85f) * body.zScale);
        matrices.translate(0, hasChestplate ? 0.475 : 0.4125, -0.005);
    }
}
