package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric && >=1.21.9 {
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.client.rendering.Side;
//?}
// Accessories moved AccessoryRenderer/Side into renderers/ and rendering/ at 1.3.4-beta+1.21.5.
//? if fabric && >=1.21.5 && <1.21.9 {
/*import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.client.rendering.Side;
*///?}
//? if fabric && >=1.19 && <1.21.5 {
/*import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.Side;
*///?}
//? if !fabric || <1.19 {
/*import net.minecraft.util.Mth;
*///?}
//? if <1.19.3 {
/*import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.block.model.ItemTransforms;
*///?} else {
import org.joml.Quaternionf;
//?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
//? if >=1.20 {
import net.minecraft.world.item.ItemDisplayContext;
//?}
import net.minecraft.world.item.ItemStack;

/** Shared plumbing for the three accessory renderers; they are per-loader, the placement maths is not. */
final class AccessoryItemRenderUtil {
    private AccessoryItemRenderUtil() {
    }

    //? if >=1.21.9 {
    static ItemStackRenderState createItemRenderState(ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity) {
        ItemStackRenderState itemRenderState = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForLiving(itemRenderState, stripGlint(stack), displayContext, entity);
        return itemRenderState;
    }

    static void submit(ItemStackRenderState itemRenderState, LivingEntityRenderState entityState,
                       PoseStack matrices, SubmitNodeCollector collector, int light) {
        itemRenderState.submit(matrices, collector, light, OverlayTexture.NO_OVERLAY, entityState.outlineColor);
    }
    //?}

    //? if fabric && >=1.21.9 {
    static void render(AccessoryRenderState accessoryState, LivingEntityRenderState entityState, PoseStack matrices, SubmitNodeCollector collector) {
        ItemStackRenderState itemRenderState = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE);
        if (itemRenderState == null) {
            return;
        }
        submit(itemRenderState, entityState, matrices, collector, entityState.getStateData(AccessoriesRenderStateKeys.LIGHT));
    }
    //?}

    /** Curse of Binding and the collar's own enchantments must not make the worn model shimmer. */
    //? if >=1.20.5 {
    static ItemStack stripGlint(ItemStack stack) {
        ItemStack renderStack = stack.copy();
        renderStack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        renderStack.remove(DataComponents.ENCHANTMENTS);
        return renderStack;
    }
    //?} else {
    /*static ItemStack stripGlint(ItemStack stack) {
        // No data components before 1.20.5, and the enchantment glint is not suppressible without them.
        return stack;
    }
    *///?}

    // Pre-render-rework single-call draw; the rendered entity is always in the client level.
    //? if >=1.20 && <1.21.9 {
    /*static void draw(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices,
                     MultiBufferSource bufferSource, int light) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, displayContext, light, OverlayTexture.NO_OVERLAY,
                matrices, bufferSource, Minecraft.getInstance().level, 0);
    }
    *///?}

    //? if <1.21.9 {
    /*static void drawHead(ItemStack stack, PoseStack matrices, MultiBufferSource bufferSource, int light) {
        //? if <1.20 {
        /^Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD,
                light, OverlayTexture.NO_OVERLAY, matrices, bufferSource, 0);
        ^///?} else {
        draw(stack, ItemDisplayContext.HEAD, matrices, bufferSource, light);
        //?}
    }

    static void drawFixed(ItemStack stack, PoseStack matrices, MultiBufferSource bufferSource, int light) {
        //? if <1.20 {
        /^Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED,
                light, OverlayTexture.NO_OVERLAY, matrices, bufferSource, 0);
        ^///?} else {
        draw(stack, ItemDisplayContext.FIXED, matrices, bufferSource, light);
        //?}
    }
    *///?}

    static void rotateXYZ(PoseStack matrices, float x, float y, float z) {
        // 26.3 took the quaternion overload off mulPose and left only rotate.
        //? if >=26.3 {
        /*matrices.rotate(new Quaternionf().rotateXYZ(x, y, z));
        *///?} elif <1.19.3 {
        /*matrices.mulPose(Quaternion.fromXYZ(x, y, z));
        *///?} else {
        matrices.mulPose(new Quaternionf().rotateXYZ(x, y, z));
        //?}
    }

    /**
     * Centre of {@code part}'s bottom face, +Y out of it -- the anchor every offset here is measured
     * from. The Curios branch reproduces Accessories' transformToFace step for step.
     */
    static void transformToBottomFace(PoseStack matrices, ModelPart part) {
        //? if fabric && >=1.19 {
        AccessoryRenderer.transformToFace(matrices, part, Side.BOTTOM);
        //?} else {
        /*Bounds bounds = bounds(part);
        part.translateAndRotate(matrices);
        matrices.scale(0.0625F, 0.0625F, 0.0625F);
        // Side.BOTTOM is (0,-1,0): centre on X and Z, far face on Y, model space having Y point down.
        matrices.translate(Mth.lerp(0.5, bounds.minX, bounds.maxX), bounds.maxY, Mth.lerp(0.5, bounds.minZ, bounds.maxZ));
        matrices.scale(8.0F, 8.0F, 8.0F);
        rotateXYZ(matrices, (float) Math.PI, 0.0F, 0.0F);
        *///?}
    }

    //? if !fabric || <1.19 {
    /*private record Bounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
    }

    private static Bounds bounds(ModelPart part) {
        float minX = 0.0F, minY = 0.0F, minZ = 0.0F, maxX = 0.0F, maxY = 0.0F, maxZ = 0.0F;
        for (ModelPart.Cube cube : part.cubes) {
            minX = Math.min(minX, Math.min(cube.minX, cube.maxX));
            minY = Math.min(minY, Math.min(cube.minY, cube.maxY));
            minZ = Math.min(minZ, Math.min(cube.minZ, cube.maxZ));
            maxX = Math.max(maxX, Math.max(cube.minX, cube.maxX));
            maxY = Math.max(maxY, Math.max(cube.minY, cube.maxY));
            maxZ = Math.max(maxZ, Math.max(cube.minZ, cube.maxZ));
        }
        return new Bounds(minX, minY, minZ, maxX, maxY, maxZ);
    }
    *///?}
}
