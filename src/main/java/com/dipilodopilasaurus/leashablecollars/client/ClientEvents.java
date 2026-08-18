package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if fabric {
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
*///?} else
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
//?} else {
/*import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Client-side counterpart to {@link com.dipilodopilasaurus.leashablecollars.Events}. One hook: once
 * per frame after the world is drawn, with the pose stack and a buffer source. Since 1.21.9 NeoForge's
 * event carries no buffer source, so that branch takes the client's own. Both arguments are ignorable.
 */
public final class ClientEvents {
    private ClientEvents() {
    }

    @FunctionalInterface
    public interface LevelRender {
        void render(PoseStack poses, MultiBufferSource buffers);
    }

    public static void onLevelRenderEnd(LevelRender handler) {
        //? if fabric {
        // 26.1 renamed the whole "world" render API to "level", context type included.
        //? if >=26.1 {
        /*LevelRenderEvents.END_MAIN.register(ctx -> handler.render(ctx.poseStack(), ctx.bufferSource()));
        *///?} else
        WorldRenderEvents.END_MAIN.register(ctx -> handler.render(ctx.matrices(), ctx.consumers()));
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.AfterLevel.class, event -> {
            PoseStack poses = event.getPoseStack();
            if (poses == null) return;
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            handler.render(poses, buffers);
            // Fabric's END_MAIN flushes its own buffer source for us; here the batch is ours to end.
            buffers.endBatch();
        });
        *///?}
    }
}
