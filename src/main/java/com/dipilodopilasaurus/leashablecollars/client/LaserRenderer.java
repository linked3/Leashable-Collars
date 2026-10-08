package com.dipilodopilasaurus.leashablecollars.client;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
*///?}
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import com.dipilodopilasaurus.leashablecollars.item.LaserPointerItem;
//? if >=1.19.3 {
import org.joml.Matrix4f;
import org.joml.Vector3f;
//?} else {
/*import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
*///?}

import java.util.Optional;

public final class LaserRenderer {
    private static final int SEGMENTS = 12;
    private static final float CORE_RADIUS = 0.020f;
    private static final float HALO_RADIUS = 0.052f;
    private static final float BEAM_RADIUS = 0.005f;
    private static final float SURFACE_LIFT = 0.006f;
    // World-size dots vanish at range and screen-size ones swallow the wall, so both grow with distance.
    private static final double SPREAD = 0.06;
    private static final double VIEW_LIMIT = 128.0;
    // Lens face centre, through the first-person display transform the model json declares.
    private static final Vector3f MUZZLE_LOCAL = new Vector3f(0.0f, 0.3125f, -0.140625f);
    // GameRenderer draws the hand at a fixed fov while the level uses the player's setting.
    private static final double HAND_FOV = 70.0;
    // Third person: ItemInHandLayer's hand offset plus the lens at third-person scale, off the arm pivot.
    private static final float HAND_OUT = 0.0625f;
    private static final float HAND_REACH = 0.765625f;
    private static final float HAND_LIFT = 0.125f;
    private static final float ARM_PIVOT_OUT = 0.3125f;
    private static final float ARM_PIVOT_UP = 0.125f;
    private static final float CROUCH_ARM_DROP = 0.2f;
    private static final float MODEL_SCALE = 0.9375f;
    private static final float MODEL_ORIGIN = 1.501f;
    // The offset setupRotations adds once the body is horizontal, outside the model scale.
    private static final float SWIM_DROP = -1.0f;
    private static final float SWIM_SHIFT = 0.3f;

    private static final Vector3f muzzleOffset = new Vector3f();
    private static final Matrix4f handSeedInverse = new Matrix4f();

    private LaserRenderer() {
    }

    /** What the hand pass seeds its stack with -- the identity on 1.20.1, an inverted projection from 1.21. */
    public static void captureHandSeed(Matrix4f seed) {
        //? if >=1.19.3 {
        handSeedInverse.set(seed).invert();
        //?} else {
        /*handSeedInverse.load(seed);
        // Legacy invert() multiplies the adjugate by the determinant instead of its reciprocal.
        float determinant = handSeedInverse.adjugateAndDet();
        if (Math.abs(determinant) > 1.0e-6f) handSeedInverse.multiply(1.0f / determinant);
        else handSeedInverse.setIdentity();
        *///?}
    }

    /** The lens, put through the pose the hand renderer is about to draw the pointer with. */
    public static void captureHandPose(Matrix4f pose) {
        // Relative to the seed, so what is left is plain view space; it also drops the view bob.
        //? if >=1.19.3 {
        muzzleOffset.set(MUZZLE_LOCAL).mulPosition(handSeedInverse.mul(pose, new Matrix4f()));
        //?} else {
        /*Matrix4f relative = handSeedInverse.copy();
        relative.multiply(pose);
        Vector4f lens = new Vector4f(MUZZLE_LOCAL);
        lens.transform(relative);
        muzzleOffset.set(lens.x(), lens.y(), lens.z());
        *///?}
    }

    public static void register() {
        ClientEvents.onLevelRenderEnd(LaserRenderer::renderLasers);
    }

    private static void renderLasers(ClientEvents.Frame frame) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        float partial = partialTick(client);
        Vec3 camera = cameraPos();

        for (AbstractClientPlayer player : client.level.players()) {
            if (player.isSpectator() || !player.isUsingItem()) continue;
            ItemStack laser = player.getUseItem();
            if (!laser.is(PlayerCollarsMod.LASER_POINTER_ITEM)) continue;
            if (player.getEyePosition(partial).distanceToSqr(camera) > VIEW_LIMIT * VIEW_LIMIT) continue;

            frame.draw(quads(), (matrix, buffer) -> drawLaser(buffer, matrix, player, laser, camera, partial));
        }
    }

    private static void drawLaser(VertexConsumer buffer, Matrix4f matrix, AbstractClientPlayer player,
                                  ItemStack laser, Vec3 camera, float partial) {
        Minecraft client = Minecraft.getInstance();
        int rgb = Components.dyeColor(laser, LaserPointerItem.DEFAULT_COLOR) & 0xFFFFFF;
        double range = 32.0 * (1.0 + Enchants.level(Compat.level(player).registryAccess(), LaserPointerItem.LASER_REACH_KEY, laser));

        boolean firstPersonSelf = player == client.player && client.options.getCameraType().isFirstPerson();
        // getEyePosition snaps on the crouch pose while the camera eases, so aim from the camera instead.
        Vec3 eye = firstPersonSelf ? camera : player.getEyePosition(partial);
        Vec3 look = player.getViewVector(partial);

        // Never client.hitResult: that one is clamped to vanilla block reach, a fifth of the laser's.
        BlockHitResult block = clip(player, eye, eye.add(look.scale(range)));
        double blockDist = block.getType() == HitResult.Type.BLOCK ? block.getLocation().distanceTo(eye) : range;
        Vec3 hit = entityHit(Compat.level(player), player, eye, eye.add(look.scale(blockDist)));
        Vec3 normal = null;
        if (hit != null) {
            normal = camera.subtract(hit).normalize();
        } else if (block.getType() == HitResult.Type.BLOCK) {
            hit = block.getLocation();
            Direction side = block.getDirection();
            normal = new Vec3(side.getStepX(), side.getStepY(), side.getStepZ());
        }

        Vec3 beamEnd = hit != null ? hit : eye.add(look.scale(range));
        Vec3 start = firstPersonSelf ? firstPersonMuzzle(client, player, camera) : muzzle(player, partial);
        drawBeam(buffer, matrix, start, beamEnd, camera, rgb);
        if (hit != null) {
            drawDot(buffer, matrix, hit, normal, camera, rgb);
        }
    }

    // Entity.pick would re-derive the origin from the eye, and the crouch fix needs this one kept.
    private static BlockHitResult clip(AbstractClientPlayer player, Vec3 from, Vec3 to) {
        return Compat.level(player).clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    // The hand draws after the level, so the offset is a frame old; the camera basis stops it swinging.
    private static Vec3 firstPersonMuzzle(Minecraft client, AbstractClientPlayer player, Vec3 camera) {
        // Spread, not depth: the passes disagree on fov, so the lens sits further off the view axis.
        float spread = (float) (Math.tan(Math.toRadians(levelFov(client, player)) * 0.5)
                / Math.tan(Math.toRadians(HAND_FOV) * 0.5));
        Vector3f world = cameraBasis(muzzleOffset.x() * spread, muzzleOffset.y() * spread, muzzleOffset.z());
        //? if >=1.19.3 {
        world.rotate(mainCamera(client).rotation());
        //?} else {
        /*world.transform(mainCamera(client).rotation());
        *///?}
        return camera.add(world.x(), world.y(), world.z());
    }

    // 1.21 flipped Camera's basis constants: the quaternion now carries them to plain view space.
    private static Vector3f cameraBasis(float x, float y, float z) {
        //? if >=1.21 {
        return new Vector3f(x, y, z);
        //?} else {
        /*return new Vector3f(-x, y, -z);
        *///?}
    }

    // Level fov is the option times the sprint/flight modifier while the hand keeps 70; 1.21.4 added the args.
    private static double levelFov(Minecraft client, AbstractClientPlayer player) {
        //? if >=1.21.4 {
        float modifier = player.getFieldOfViewModifier(true, client.options.fovEffectScale().get().floatValue());
        //?} else {
        /*float modifier = player.getFieldOfViewModifier();
        *///?}
        // GameRenderer eases this over a couple of ticks, so a sprint start leads it by that much.
        //? if >=1.19 {
        return client.options.fov().get() * (double) Mth.clamp(modifier, 0.1f, 1.5f);
        //?} else {
        /*return client.options.fov * Mth.clamp(modifier, 0.1f, 1.5f);
        *///?}
    }

    private static Vec3 entityHit(Level level, Entity shooter, Vec3 from, Vec3 to) {
        Vec3 best = null;
        double bestSqr = Double.MAX_VALUE;
        for (Entity target : level.getEntities(shooter, new AABB(from, to).inflate(1.0), Entity::isPickable)) {
            Optional<Vec3> clip = target.getBoundingBox().inflate(target.getPickRadius()).clip(from, to);
            if (clip.isEmpty()) continue;
            double distSqr = clip.get().distanceToSqr(from);
            if (distSqr < bestSqr) {
                bestSqr = distSqr;
                best = clip.get();
            }
        }
        return best;
    }

    // PlayerArmPoseMixin puts the arm in the bow pose, so the lens runs through that rotation and 0.9375.
    private static Vec3 muzzle(AbstractClientPlayer player, float partial) {
        boolean mainHand = player.getUsedItemHand() == InteractionHand.MAIN_HAND;
        boolean right = (player.getMainArm() == HumanoidArm.RIGHT) == mainHand;
        float arm = right ? 1.0f : -1.0f;
        boolean crouching = player.isCrouching();
        float age = player.tickCount + partial;

        float bodyYaw = bodyRot(player, partial);
        float headYaw = Mth.rotLerp(partial, player.yHeadRotO, player.yHeadRot);
        // The idle bob runs the other way round on a left arm.
        float xRot = (float) (-Math.PI / 2.0) + headXRot(player, partial)
                + (crouching ? 0.4f : 0.0f) + arm * Mth.sin(age * 0.067f) * 0.05f;
        float yRot = -0.1f * arm + Mth.wrapDegrees(headYaw - bodyYaw) * Mth.DEG_TO_RAD;
        float zRot = arm * (Mth.cos(age * 0.09f) * 0.05f + 0.05f);

        Vector3f hand = new Vector3f(-HAND_OUT * arm, HAND_REACH, -HAND_LIFT);
        //? if >=1.19.3 {
        hand.rotateX(xRot).rotateY(yRot).rotateZ(zRot);
        //?} else {
        /*hand.transform(Vector3f.XP.rotation(xRot));
        hand.transform(Vector3f.YP.rotation(yRot));
        hand.transform(Vector3f.ZP.rotation(zRot));
        *///?}
        float modelX = -ARM_PIVOT_OUT * arm + hand.x();
        float modelY = ARM_PIVOT_UP + (crouching ? CROUCH_ARM_DROP : 0.0f) + hand.y();

        // Model space is x-left/y-down about a point 1.501 up; setupRotations works in the body's own frame.
        Vector3f offset = new Vector3f(-modelX * MODEL_SCALE, (MODEL_ORIGIN - modelY) * MODEL_SCALE,
                hand.z() * MODEL_SCALE);
        if (player.isVisuallySwimming()) offset.add(0.0f, SWIM_DROP, SWIM_SHIFT);
        //? if >=1.19.3 {
        offset.rotateX(bodyPitch(player, partial) * Mth.DEG_TO_RAD);
        //?} else {
        /*offset.transform(Vector3f.XP.rotationDegrees(bodyPitch(player, partial)));
        *///?}

        Vec3 forward = Vec3.directionFromRotation(0.0f, bodyYaw);
        Vec3 side = forward.cross(new Vec3(0.0, 1.0, 0.0));
        return player.getPosition(partial)
                .add(side.scale(offset.x()))
                .add(forward.scale(-offset.z()))
                .add(0.0, offset.y(), 0.0);
    }

    // Riding a living vehicle renders the body off the vehicle's yaw, clamped to the head's.
    private static float bodyRot(AbstractClientPlayer player, float partial) {
        if (!(player.getVehicle() instanceof LivingEntity vehicle)) {
            return Mth.rotLerp(partial, player.yBodyRotO, player.yBodyRot);
        }
        float head = Mth.rotLerp(partial, player.yHeadRotO, player.yHeadRot);
        float delta = Mth.clamp(Mth.wrapDegrees(head - Mth.rotLerp(partial, vehicle.yBodyRotO, vehicle.yBodyRot)),
                -85.0f, 85.0f);
        return Math.abs(delta) > 50.0f ? head - delta + delta * 0.2f : head - delta;
    }

    // The bow pose hangs the arm off head.xRot, and the elytra and swim animations overwrite that.
    private static float headXRot(AbstractClientPlayer player, float partial) {
        if (player.isFallFlying()) return (float) (-Math.PI / 4.0);
        float view = player.getViewXRot(partial) * Mth.DEG_TO_RAD;
        float swim = player.getSwimAmount(partial);
        return swim > 0.0f ? Mth.lerp(swim, view, (float) (-Math.PI / 4.0)) : view;
    }

    // Elytra and swimming lay the whole model over, so the arm pivot moves with it.
    private static float bodyPitch(AbstractClientPlayer player, float partial) {
        float view = player.getViewXRot(partial);
        if (player.isFallFlying()) {
            float ticks = player.getFallFlyingTicks() + partial;
            return Mth.clamp(ticks * ticks / 100.0f, 0.0f, 1.0f) * (-90.0f - view);
        }
        float swim = player.getSwimAmount(partial);
        return swim > 0.0f ? Mth.lerp(swim, 0.0f, player.isInWater() ? -90.0f - view : -90.0f) : 0.0f;
    }

    private static void drawDot(VertexConsumer buffer, Matrix4f matrix, Vec3 point, Vec3 normal, Vec3 camera, int rgb) {
        double camDist = point.distanceTo(camera);
        if (camDist < 1.0e-4) return;
        int core = washOut(rgb);
        float scale = (float) (1.0 + camDist * SPREAD);
        Vec3 centre = point.add(normal.scale(SURFACE_LIFT * scale)).subtract(camera);
        Vec3 u = perpendicular(normal);
        Vec3 v = normal.cross(u).normalize();
        float coreRadius = CORE_RADIUS * scale;
        float haloRadius = HALO_RADIUS * scale;

        for (int i = 0; i < SEGMENTS; i++) {
            Vec3 inner0 = rim(centre, u, v, coreRadius, i);
            Vec3 inner1 = rim(centre, u, v, coreRadius, i + 1);
            vertex(buffer, matrix, centre, core, 255);
            vertex(buffer, matrix, centre, core, 255);
            vertex(buffer, matrix, inner0, rgb, 255);
            vertex(buffer, matrix, inner1, rgb, 255);

            vertex(buffer, matrix, inner0, rgb, 190);
            vertex(buffer, matrix, inner1, rgb, 190);
            vertex(buffer, matrix, rim(centre, u, v, haloRadius, i + 1), rgb, 0);
            vertex(buffer, matrix, rim(centre, u, v, haloRadius, i), rgb, 0);
        }
    }

    private static void drawBeam(VertexConsumer buffer, Matrix4f matrix, Vec3 start, Vec3 end, Vec3 camera, int rgb) {
        Vec3 axis = end.subtract(start);
        Vec3 flat = axis.cross(camera.subtract(start.add(axis.scale(0.5))));
        if (flat.lengthSqr() < 1.0e-8) return;

        float width = BEAM_RADIUS * (float) (1.0 + start.distanceTo(camera) * SPREAD * 0.5);
        Vec3 side = flat.normalize().scale(width);
        Vec3 a = start.subtract(camera);
        Vec3 b = end.subtract(camera);

        strip(buffer, matrix, a, b, side.scale(-1.0), side, rgb, 120, 120);
        strip(buffer, matrix, a, b, side, side.scale(3.0), rgb, 110, 0);
        strip(buffer, matrix, a, b, side.scale(-1.0), side.scale(-3.0), rgb, 110, 0);
    }

    private static void strip(VertexConsumer buffer, Matrix4f matrix, Vec3 start, Vec3 end,
                              Vec3 nearSide, Vec3 farSide, int rgb, int nearAlpha, int farAlpha) {
        vertex(buffer, matrix, start.add(nearSide), rgb, nearAlpha);
        vertex(buffer, matrix, end.add(nearSide), rgb, nearAlpha);
        vertex(buffer, matrix, end.add(farSide), rgb, farAlpha);
        vertex(buffer, matrix, start.add(farSide), rgb, farAlpha);
    }

    // The centre of a real dot reads as white however the dye is set.
    private static int washOut(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return ((r + (255 - r) * 3 / 4) << 16) | ((g + (255 - g) * 3 / 4) << 8) | (b + (255 - b) * 3 / 4);
    }

    private static Vec3 rim(Vec3 centre, Vec3 u, Vec3 v, float radius, int step) {
        double angle = step * 2.0 * Math.PI / SEGMENTS;
        return centre.add(u.scale(Math.cos(angle) * radius)).add(v.scale(Math.sin(angle) * radius));
    }

    private static Vec3 perpendicular(Vec3 normal) {
        Vec3 helper = Math.abs(normal.y) < 0.99 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        return normal.cross(helper).normalize();
    }

    private static RenderType quads() {
        //? if >=1.21.11 {
        return RenderTypes.debugQuads();
        //?} elif >=1.19.3 {
        /*return RenderType.debugQuads();
        *///?} else {
        /*return LegacyLaserRenderType.QUADS;
        *///?}
    }

    // 1.21 replaced the vertex(...).color(...).endVertex() chain with a fluent addVertex/setColor pair.
    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 pos, int rgb, int alpha) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        //? if >=1.21 {
        buffer.addVertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).setColor(r, g, b, alpha);
        //?} else {
        /*buffer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).color(r, g, b, alpha).endVertex();
        *///?}
    }

    private static float partialTick(Minecraft client) {
        //? if >=1.21.2 {
        return client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        //?} elif >=1.20.5 {
        /*return client.getTimer().getGameTimeDeltaPartialTick(true);
        *///?} else {
        /*return client.getFrameTime();
        *///?}
    }

    private static Vec3 cameraPos() {
        //? if >=1.21.9 {
        return mainCamera(Minecraft.getInstance()).position();
        //?} else {
        /*return mainCamera(Minecraft.getInstance()).getPosition();
        *///?}
    }

    private static Camera mainCamera(Minecraft client) {
        //? if >=26.2 {
        /*return client.gameRenderer.mainCamera();
        *///?} else {
        return client.gameRenderer.getMainCamera();
        //?}
    }
}
