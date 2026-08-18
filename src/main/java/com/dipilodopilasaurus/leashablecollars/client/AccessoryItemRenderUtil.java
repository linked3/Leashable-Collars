package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric {
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.client.rendering.Side;
//?} else {
/*import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Shared plumbing for the three accessory renderers. The renderer classes are per-loader because
 * Accessories splits extract from render while Curios does both in one pass, but the placement maths
 * -- here and in each renderer -- is not.
 */
final class AccessoryItemRenderUtil {
    private AccessoryItemRenderUtil() {
    }

    /** Glint stripped, so Curse of Binding and the collar enchantments don't make the worn model shimmer. */
    static ItemStackRenderState createItemRenderState(ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity) {
        ItemStack renderStack = stack.copy();
        renderStack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        renderStack.remove(DataComponents.ENCHANTMENTS);

        ItemStackRenderState itemRenderState = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForLiving(itemRenderState, renderStack, displayContext, entity);
        return itemRenderState;
    }

    static void submit(ItemStackRenderState itemRenderState, LivingEntityRenderState entityState,
                       PoseStack matrices, SubmitNodeCollector collector, int light) {
        itemRenderState.submit(matrices, collector, light, OverlayTexture.NO_OVERLAY, entityState.outlineColor);
    }

    //? if fabric {
    static void render(AccessoryRenderState accessoryState, LivingEntityRenderState entityState, PoseStack matrices, SubmitNodeCollector collector) {
        ItemStackRenderState itemRenderState = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE);
        if (itemRenderState == null) {
            return;
        }
        submit(itemRenderState, entityState, matrices, collector, entityState.getStateData(AccessoriesRenderStateKeys.LIGHT));
    }
    //?}

    /**
     * Centre of {@code part}'s bottom face, +Y pointing out of it -- the anchor every offset in this
     * package is measured from. The NeoForge branch reproduces Accessories'
     * {@code transformToFace(part, Side.BOTTOM)} step for step, since the offsets were tuned against it.
     */
    static void transformToBottomFace(PoseStack matrices, ModelPart part) {
        //? if fabric {
        AccessoryRenderer.transformToFace(matrices, part, Side.BOTTOM);
        //?} else {
        /*part.translateAndRotate(matrices);
        Vec3 min = Vec3.ZERO;
        Vec3 max = Vec3.ZERO;
        for (ModelPart.Cube cube : part.cubes) {
            min = new Vec3(Math.min(min.x, Math.min(cube.minX, cube.maxX)),
                    Math.min(min.y, Math.min(cube.minY, cube.maxY)),
                    Math.min(min.z, Math.min(cube.minZ, cube.maxZ)));
            max = new Vec3(Math.max(max.x, Math.max(cube.minX, cube.maxX)),
                    Math.max(max.y, Math.max(cube.minY, cube.maxY)),
                    Math.max(max.z, Math.max(cube.minZ, cube.maxZ)));
        }
        matrices.scale(0.0625F, 0.0625F, 0.0625F);
        // Side.BOTTOM is (0,-1,0): centre on X and Z, far face on Y, model space having Y point down.
        matrices.translate(Mth.lerp(0.5, min.x, max.x), Mth.lerp(1.0, min.y, max.y), Mth.lerp(0.5, min.z, max.z));
        matrices.scale(8.0F, 8.0F, 8.0F);
        matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
        *///?}
    }
}
