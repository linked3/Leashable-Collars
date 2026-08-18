package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//? if >=26.1 {
/*import net.minecraft.world.phys.Vec3;
*///?}
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeashFenceKnotEntity.class)
public abstract class MixinLeashKnotEntity extends BlockAttachedEntity {
    private MixinLeashKnotEntity(EntityType<? extends BlockAttachedEntity> entityType, Level world) {
        super(entityType, world);
    }

    // 26.1 added a hit-position parameter to Entity.interact, and a descriptor mismatch fails silently at
    // apply time rather than at compile. The ordinal holds either way -- both versions call it twice.
    //? if >=26.1 {
    /*@Inject(
            method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void preventBreakKnot(Player player, InteractionHand hand, Vec3 hitPos, CallbackInfoReturnable<InteractionResult> cir) {
    *///?} else
    @Inject(
            method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void preventBreakKnot(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = level();
        if (!world.isClientSide() && PlayerCollarsMod.blockLeashKnotBreak((ServerLevel) world, player, (LeashFenceKnotEntity) (Object) this)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
