package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.item.LaserPointerItem;
import org.joml.Matrix4f;

public class LaserRenderer {
    // The size of your laser dot!
    private static final float DOT_SIZE = 0.05f;
    // Push the square out from the block by a tiny fraction so it doesn't clip into the wall.
    private static final float Z_FIGHT_OFFSET = 0.015f;

    public static void register() {
        // This event runs every single frame right before the screen is finished drawing!
        ClientEvents.onLevelRenderEnd(LaserRenderer::renderLasers);
    }

    private static void renderLasers(PoseStack matrices, MultiBufferSource buffers) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        // Loop through EVERY player the client can see!
        for (AbstractClientPlayer player : client.level.players()) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            ItemStack laser = mainHand.is(PlayerCollarsMod.LASER_POINTER_ITEM)
                    ? mainHand
                    : (offHand.is(PlayerCollarsMod.LASER_POINTER_ITEM) ? offHand : null);
            if (laser == null) continue;

            double maxDistance = 32.0 * (1.0 + getReachLevel(client, laser));
            HitResult hit = player == client.player && client.hitResult != null
                    ? client.hitResult
                    : player.pick(maxDistance, client.getDeltaTracker().getGameTimeDeltaPartialTick(true), false);

            // It only renders if it successfully hits a block within range!
            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit
                    && hit.getLocation().distanceToSqr(player.getEyePosition()) <= maxDistance * maxDistance) {
                drawLaserSquare(matrices, buffers, blockHit);
            }
        }
    }

    private static int getReachLevel(Minecraft client, ItemStack laser) {
        if (client.level == null) return 0;
        return client.level.registryAccess().lookup(Registries.ENCHANTMENT)
                .flatMap(registry -> registry.get(LaserPointerItem.LASER_REACH_KEY))
                .map(entry -> EnchantmentHelper.getItemEnchantmentLevel(entry, laser))
                .orElse(0);
    }

    private static void drawLaserSquare(PoseStack matrices, MultiBufferSource buffers, BlockHitResult hit) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        Vec3 hitPos = hit.getLocation();
        Direction side = hit.getDirection();

        matrices.pushPose();

        // 1. Move to the exact spot we hit, relative to the camera
        matrices.translate(hitPos.x - cameraPos.x, hitPos.y - cameraPos.y, hitPos.z - cameraPos.z);

        // 2. Nudge it off the face so it doesn't Z-fight with the block
        matrices.translate(side.getStepX() * Z_FIGHT_OFFSET, side.getStepY() * Z_FIGHT_OFFSET, side.getStepZ() * Z_FIGHT_OFFSET);

        // 3. Rotate the matrix so our square lies perfectly flat against the block face!
        matrices.mulPose(side.getRotation());

        Matrix4f positionMatrix = matrices.last().pose();
        VertexConsumer buffer = buffers.getBuffer(RenderTypes.debugQuads());

        // 4. Draw the 4 corners of our bright green square! (R, G, B, Alpha)
        buffer.addVertex(positionMatrix, -DOT_SIZE, -DOT_SIZE, 0).setColor(0, 255, 0, 255);
        buffer.addVertex(positionMatrix, -DOT_SIZE, DOT_SIZE, 0).setColor(0, 255, 0, 255);
        buffer.addVertex(positionMatrix, DOT_SIZE, DOT_SIZE, 0).setColor(0, 255, 0, 255);
        buffer.addVertex(positionMatrix, DOT_SIZE, -DOT_SIZE, 0).setColor(0, 255, 0, 255);

        matrices.popPose();
    }
}
