package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
//? if >=1.20.5 {
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
//?} else {
/*import net.minecraft.world.entity.decoration.HangingEntity;
*///?}
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
//? if >=1.20.5 {
public abstract class MixinLeashKnotEntity extends BlockAttachedEntity {
    private MixinLeashKnotEntity(EntityType<? extends BlockAttachedEntity> entityType, Level world) {
//?} else {
/*public abstract class MixinLeashKnotEntity extends HangingEntity {
    private MixinLeashKnotEntity(EntityType<? extends HangingEntity> entityType, Level world) {
*///?}
        super(entityType, world);
    }

    // 1.21.6 rerouted the knot-break through super.interact; below that a bare discard(), 26.1 a hit pos.
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
        playercollars$blockKnotBreak(player, cir);
    }
    *///?}
    //? if >=1.21.6 && <26.1 {
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
        playercollars$blockKnotBreak(player, cir);
    }
    //?}
    //? if <1.21.6 {
    /*@Inject(
            method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;discard()V"),
            cancellable = true
    )
    private void preventBreakKnot(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        playercollars$blockKnotBreak(player, cir);
    }
    *///?}

    private void playercollars$blockKnotBreak(Player player, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = Compat.level(this);
        if (!world.isClientSide() && PlayerCollarsMod.blockLeashKnotBreak((ServerLevel) world, player, (LeashFenceKnotEntity) (Object) this)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
