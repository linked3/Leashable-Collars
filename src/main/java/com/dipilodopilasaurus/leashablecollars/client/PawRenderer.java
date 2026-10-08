package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric && <1.19 || !fabric && <1.21.2 {
/*import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import java.util.List;
*///?}
//? if !fabric && <1.21.2 {
/*import java.util.ArrayList;
*///?}
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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
//?}
//? if fabric && >=1.21.5 && <1.21.9 {
/*import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
*///?}
//? if fabric && >=1.21.2 && <1.21.5 {
/*import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
*///?}
//? if fabric && >=1.19 && <1.21.2 {
/*import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
*///?}
//? if !fabric && >=1.21.2 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
//? if !fabric && >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?}
//? if !fabric && >=1.21.2 && <1.21.9 {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
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
/*public class PawRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference reference, EntityModel<? extends LivingEntity> model,
            PoseStack matrices, MultiBufferSource bufferSource, int light, LivingEntity entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;

        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.21.9 {
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
//?}
//? if fabric && >=1.21.5 && <1.21.9 {
/*public class PawRenderer implements AccessoryRenderer {

    // Accessories calls this without asking shouldRenderInFirstPerson, so the override swaps hands.
    @Override
    public <S extends LivingEntityRenderState> void renderOnFirstPerson(HumanoidArm arm, ItemStack stack, SlotPath path,
            PoseStack matrices, EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light,
            float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel,
                arm == HumanoidArm.LEFT, true);
    }

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.21.2 && <1.21.5 {
/*public class PawRenderer implements AccessoryRenderer {

    // Accessories calls this without asking shouldRenderInFirstPerson, so the override swaps hands.
    @Override
    public <S extends LivingEntityRenderState> void renderOnFirstPerson(HumanoidArm arm, ItemStack stack,
            SlotReference reference, PoseStack matrices, EntityModel<S> model, S renderState,
            MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel,
                arm == HumanoidArm.LEFT, true);
    }

    @Override
    public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if fabric && >=1.19 && <1.21.2 {
/*public class PawRenderer implements AccessoryRenderer {

    // Pre-1.4 Accessories has no ARM render-state flag; first person is a separate override instead.
    @Override
    public boolean shouldRenderInFirstPerson(HumanoidArm arm, ItemStack stack, SlotReference reference) {
        return true;
    }

    @Override
    public <M extends LivingEntity> void renderOnFirstPerson(HumanoidArm arm, ItemStack stack, SlotReference reference,
            PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel,
                arm == HumanoidArm.LEFT, true);
    }

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;
        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
*///?}
//? if !fabric && >=1.21.9 {
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

    private static void drawArm(ItemStackRenderState itemState, LivingEntityRenderState entityState, PoseStack matrices,
                                SubmitNodeCollector collector, int light, PlayerModel model, boolean left, boolean firstPerson) {
        matrices.pushPose();
        applyArmTransform(matrices, model, left, firstPerson);
        AccessoryItemRenderUtil.submit(itemState, entityState, matrices, collector, light);
        matrices.popPose();
    }
*///?}
//? if !fabric && >=1.21.2 && <1.21.9 {
/*public class PawRenderer implements ICurioRenderer {

    @Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack matrices, MultiBufferSource bufferSource, int light,
            S entityState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }

    @Override
    public void renderFirstPersonHand(ItemStack stack, SlotContext slotContext, HumanoidArm arm, PoseStack matrices,
                                      MultiBufferSource bufferSource, PlayerRenderState entityState,
                                      AbstractClientPlayer clientPlayer, int light) {
        PlayerModel playerModel = playerModelOf(clientPlayer);
        if (playerModel == null) return;

        drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel,
                arm == HumanoidArm.LEFT, true);
    }
*///?}
//? if !fabric && <1.21.2 {
/*public class PawRenderer implements ICurioRenderer {

    // On 1.18.2, PlayerArmPoseMixin supplies the missing first-person callback.
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
            PoseStack matrices, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource, int light,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(renderLayerParent.getModel() instanceof PlayerModel playerModel)) return;

        drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, bufferSource, light, playerModel);
    }
*///?}

    // Trinkets has no first-person hook, nor Curios before 1.21.2; there PlayerArmPoseMixin calls this.
    //? if fabric && <1.19 || !fabric && <1.21.2 {
    /*public static boolean renderFirstPerson(LivingEntity player, PlayerModel model, boolean left,
                                            PoseStack matrices, MultiBufferSource buffers, int light) {
        List<ItemStack> paws = visiblePaws(player);
        for (ItemStack stack : paws) {
            drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, buffers, light, model, left, true);
        }
        return !paws.isEmpty();
    }
    *///?}

    //? if fabric && <1.19 {
    /*private static List<ItemStack> visiblePaws(LivingEntity player) {
        return com.dipilodopilasaurus.leashablecollars.EquippedAccessories.getEquipped(
                player, stack -> stack.getItem() instanceof PawsItem);
    }
    *///?}

    // Read off Curios directly: where Accessories is installed too it brings its own hook, so no double draw.
    //? if !fabric && <1.21.2 {
    /*private static List<ItemStack> visiblePaws(LivingEntity player) {
        List<ItemStack> paws = new ArrayList<>();
        //? if <1.19 {
        /^var handler = top.theillusivec4.curios.api.CuriosApi.getCuriosHelper().getCuriosHandler(player).orElse(null);
        ^///?} else {
        var handler = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).orElse(null);
        //?}
        if (handler == null) return paws;
        for (var slots : handler.getCurios().values()) {
            for (int index = 0; index < slots.getStacks().getSlots(); index++) {
                ItemStack stack = slots.getCosmeticStacks().getStackInSlot(index);
                // Curios renders cosmetics even when the functional slot's render toggle is off.
                if (stack.isEmpty() && index < slots.getRenders().size() && slots.getRenders().get(index)) {
                    stack = slots.getStacks().getStackInSlot(index);
                }
                if (stack.getItem() instanceof PawsItem) paws.add(stack);
            }
        }
        return paws;
    }
    *///?}

    // Curios hands first person its own call and no model, so the player's own comes from the dispatcher.
    //? if !fabric && >=1.21.2 {
    /*@Nullable
    private static PlayerModel playerModelOf(AbstractClientPlayer player) {
        EntityRenderer<?, ?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (!(renderer instanceof LivingEntityRenderer<?, ?, ?> living)
                || !(living.getModel() instanceof PlayerModel model)) {
            return null;
        }
        // Curios renders before vanilla poses the hand, so the model still carries whatever drew it last
        // -- the inventory preview, swing and all. These are renderHand's own three steps, in its order.
        model.resetPose();
        model.leftArm.zRot = -0.1F;
        model.rightArm.zRot = 0.1F;
        return model;
    }
    *///?}

    //? if <1.21.9 {
    /*static void drawArms(ItemStack stack, PoseStack matrices, MultiBufferSource bufferSource, int light,
                                 PlayerModel model) {
        drawArm(stack, matrices, bufferSource, light, model, false, false);
        drawArm(stack, matrices, bufferSource, light, model, true, false);
    }

    static void drawArm(ItemStack stack, PoseStack matrices, MultiBufferSource bufferSource, int light,
                                PlayerModel model, boolean left, boolean firstPerson) {
        matrices.pushPose();
        applyArmTransform(matrices, model, left, firstPerson);
        AccessoryItemRenderUtil.drawFixed(stack, matrices, bufferSource, light);
        matrices.popPose();
    }
    *///?}

    /** Constants are upstream v1.7.4's, known-good against Accessories' transformToFace anchor. */
    private static void applyArmTransform(PoseStack matrices, PlayerModel model, boolean left, boolean firstPerson) {
        AccessoryItemRenderUtil.transformToBottomFace(matrices, left ? model.leftArm : model.rightArm);
        boolean slim = model.slim;
        if (firstPerson) {
            AccessoryItemRenderUtil.rotateXYZ(matrices, (float) Math.PI, 0.0F, 0.0F);
            matrices.translate(left ? 0.0 : 0.015625, -0.1875, left ? -0.135 : -0.14);
            matrices.scale(slim ? 0.59375F : 0.75F, 0.75F, 1.03125F);
        } else {
            AccessoryItemRenderUtil.rotateXYZ(matrices, (float) Math.PI, (float) (left ? Math.PI : -Math.PI) / 2.0F, 0.0F);
            matrices.translate(0.0, -0.1875, -0.125);
            matrices.scale(0.75F, 0.625F, slim ? 0.875F : 1.03125F);
        }
    }
}
