package com.dipilodopilasaurus.leashablecollars.client;

//? if (fabric && >=1.21.10) || (neoforge && >=26.2) {
import com.mojang.blaze3d.vertex.PoseStack;
//?}
import com.mojang.blaze3d.vertex.VertexConsumer;
//? if fabric {
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
*///?} elif >=1.21.10 {
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
//?} elif <1.21.9 {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
*///?}
//?}
//? if neoforge && >=26.2 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}
//? if forge {
/*import net.minecraftforge.common.MinecraftForge;
*///?}
//? if forge && >=1.19 {
/*import net.minecraftforge.client.event.RenderLevelStageEvent;
*///?} elif forge {
/*import net.minecraftforge.client.event.RenderLevelLastEvent;
*///?}
//? if (fabric && <1.21.10) || (!fabric && <26.2) {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? if >=1.19.3 && <1.21 {
/*import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
*///?}
//? if (fabric && >=1.21 && <1.21.10) || (!fabric && >=1.21 && <26.2) {
/*import org.joml.Quaternionf;
*///?}
//? if fabric && >=26.1 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
*///?} elif fabric && >=1.21.10 {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
//? if >=1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderType;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
*///?}
//? if >=1.19.3 {
import org.joml.Matrix4f;
//?} else {
/*import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
*///?}

import java.util.function.Consumer;

/**
 * Client counterpart to {@link com.dipilodopilasaurus.leashablecollars.Events}: one hook, once per
 * frame after the world is drawn. 26.2 made level drawing submit-then-render, hence {@link Frame}.
 */
public final class ClientEvents {
    private ClientEvents() {
    }

    /** Camera-relative geometry for one render type. The matrix is the frame's pose, already applied. */
    @FunctionalInterface
    public interface Geometry {
        void draw(Matrix4f pose, VertexConsumer buffer);
    }

    /** One frame's drawing surface; from 26.2 {@code draw} submits a node, so nothing may be held past it. */
    @FunctionalInterface
    public interface Frame {
        void draw(RenderType type, Geometry geometry);
    }

    // Fabric 1.21.9 temporarily shipped without world render events.
    //? if fabric && >=1.21.9 && <1.21.10 {
    /*private static final java.util.List<Consumer<Frame>> RENDER_HANDLERS = new java.util.ArrayList<>();

    public static void renderLevelEnd() {
        for (Consumer<Frame> handler : RENDER_HANDLERS) emit(handler, viewRotation());
    }
    *///?}

    public static void onLevelRenderEnd(Consumer<Frame> handler) {
        //? if fabric {
        // 26.1 renamed the world render API to level and its bufferSource() is a shim 26.2 drops, so 26.x
        // submits on both loaders. END_MAIN is too late -- a node handed over there waits a frame.
        //? if >=26.1 {
        /*LevelRenderEvents.COLLECT_SUBMITS.register(ctx -> submit(handler, ctx.poseStack(), ctx.submitNodeCollector()));
        *///?} elif >=1.21.10 {
        WorldRenderEvents.END_MAIN.register(ctx -> emit(handler, ctx.matrices(), ctx.consumers()));
        //?} elif >=1.21.9 {
        /*RENDER_HANDLERS.add(handler);
        *///?} else {
        /*WorldRenderEvents.END.register(ctx -> emit(handler, viewRotation()));
        *///?}
        //?} else {
        /*registerAfterLevel(handler);
        *///?}
    }

    //? if fabric && >=26.1 {
    /*private static void submit(Consumer<Frame> handler, PoseStack poses, SubmitNodeCollector collector) {
        handler.accept((type, geometry) -> collector.submitCustomGeometry(
                poses, type, (pose, buffer) -> geometry.draw(pose.pose(), buffer)));
    }
    *///?} elif fabric && >=1.21.10 {
    private static void emit(Consumer<Frame> handler, PoseStack poses, MultiBufferSource buffers) {
        handler.accept((type, geometry) -> geometry.draw(poses.last().pose(), buffers.getBuffer(type)));
    }
    //?}

    // A guard can't nest in an inactive branch: 26.2 replaced the event, 1.21.6 split it per stage.
    //? if neoforge && >=26.2 {
    /*private static void registerAfterLevel(Consumer<Frame> handler) {
        NeoForge.EVENT_BUS.addListener(SubmitCustomGeometryEvent.class, event -> {
            SubmitNodeCollector collector = event.getSubmitNodeCollector();
            PoseStack poses = event.getPoseStack();
            handler.accept((type, geometry) -> collector.submitCustomGeometry(
                    poses, type, (pose, buffer) -> geometry.draw(pose.pose(), buffer)));
        });
    }
    *///?}

    //? if neoforge && >=1.21.6 && <26.2 {
    /*private static void registerAfterLevel(Consumer<Frame> handler) {
        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.AfterLevel.class,
                event -> emit(handler, viewRotation()));
    }
    *///?}

    //? if neoforge && <1.21.6 {
    /*private static void registerAfterLevel(Consumer<Frame> handler) {
        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.class, event -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                emit(handler, viewRotation());
            }
        });
    }
    *///?}

    //? if forge && >=1.19 {
    /*private static void registerAfterLevel(Consumer<Frame> handler) {
        MinecraftForge.EVENT_BUS.addListener((RenderLevelStageEvent event) -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                emit(handler, viewRotation());
            }
        });
    }
    *///?}

    //? if forge && <1.19 {
    /*private static void registerAfterLevel(Consumer<Frame> handler) {
        MinecraftForge.EVENT_BUS.addListener((RenderLevelLastEvent event) ->
                emit(handler, event.getPoseStack().last().pose().copy()));
    }
    *///?}

    // Modern end-of-level hooks need a rebuilt view rotation; legacy Forge supplies its camera pose.
    //? if (fabric && <1.21.10) || (!fabric && <26.2) {
    /*private static void emit(Consumer<Frame> handler, Matrix4f pose) {
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        handler.accept((type, geometry) -> geometry.draw(pose, buffers.getBuffer(type)));
        // Fabric's END_MAIN flushes its own buffer source; on this path the batch has to be ended here.
        buffers.endBatch();
    }
    *///?}

    // Not the event's camera: 1.21.9 dropped that accessor, and the main camera is what these hooks got.
    //? if (fabric && >=1.21 && <1.21.10) || (!fabric && >=1.21 && <26.2) {
    /*private static Matrix4f viewRotation() {
        Quaternionf rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        return new Matrix4f().rotation(rotation.conjugate(new Quaternionf()));
    }
    *///?}

    // 1.21 turned Camera's quaternion into plain view space; below it its conjugate is not the view matrix.
    //? if >=1.19.3 && <1.21 {
    /*private static Matrix4f viewRotation() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return new Matrix4f().rotateX(camera.getXRot() * Mth.DEG_TO_RAD)
                .rotateY((camera.getYRot() + 180.0f) * Mth.DEG_TO_RAD);
    }
    *///?}

    //? if <1.19.3 {
    /*private static Matrix4f viewRotation() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Matrix4f rotation = new Matrix4f(Vector3f.XP.rotationDegrees(camera.getXRot()));
        rotation.multiply(Vector3f.YP.rotationDegrees(camera.getYRot() + 180.0f));
        return rotation;
    }
    *///?}
}
