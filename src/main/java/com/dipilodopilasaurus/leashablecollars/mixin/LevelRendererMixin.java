package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
//? if fabric && >=1.21.9 && <1.21.10 {
/*import com.dipilodopilasaurus.leashablecollars.client.ClientEvents;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    //? if fabric && >=1.21.9 && <1.21.10 {
    /*@Inject(method = "renderLevel", at = @At("TAIL"))
    private void playercollars$renderLevelEnd(CallbackInfo ci) {
        ClientEvents.renderLevelEnd();
    }
    *///?}
}
