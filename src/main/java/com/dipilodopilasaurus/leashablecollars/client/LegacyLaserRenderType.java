package com.dipilodopilasaurus.leashablecollars.client;

//? if <1.19.3 {
/*import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

final class LegacyLaserRenderType extends RenderType {
    static final RenderType QUADS = create("playercollars_laser", DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));

    private LegacyLaserRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int size,
                                  boolean crumbling, boolean sorting, Runnable setup, Runnable clear) {
        super(name, format, mode, size, crumbling, sorting, setup, clear);
    }
}
*///?}
