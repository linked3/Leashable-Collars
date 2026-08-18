package com.dipilodopilasaurus.leashablecollars.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method="setPosToBed", at = @At("HEAD"), cancellable = true, require=0)
    private void correctDogBedHeight(BlockPos pos, CallbackInfo ci) {
        BlockState state = level().getBlockState(pos);
        if (state.getBlock() instanceof DogBedBlock) {
            Vec3 vec = pos.getBottomCenter();
            Vector3f off = state.getValue(BedBlock.FACING).step().div(10);
            setPos(vec.add(off.x, 0.35, off.z));
            ci.cancel();
        }
    }

    /**
     * The authoritative gate for {@code player_allow_attack_owner}. AttackEntityCallback only sees
     * vanilla melee, so a modded weapon or a projectile walked straight past it; the callback in
     * PlayerCollarsMod now only suppresses the swing itself.
     */
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void blockDamageFromOwnedPet(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Cast needed -- this targets LivingEntity, so the compiler only sees Entity.
        if (!((Object) this instanceof Player owner)) return;
        if (!(source.getEntity() instanceof Player pet) || pet == owner) return;
        if (PlayerCollarsMod.ALLOW_ATTACK_OWNER.get(world)) return;
        if (EquippedAccessories.findOwned(pet, x -> x.is(PlayerCollarsMod.COLLAR_TAG), owner.getUUID(), pet.getUUID()) == null) return;

        Compat.sendOverlayMessage(pet, Component.translatable("message.playercollars.no_attack_owner").withStyle(ChatFormatting.RED));
        cir.setReturnValue(false);
    }
}
