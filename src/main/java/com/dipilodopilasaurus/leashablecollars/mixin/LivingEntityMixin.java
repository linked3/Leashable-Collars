package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.Text;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
//? if >=1.19.3 {
import org.joml.Vector3f;
//?}
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

    // 26.3 split the method and routes startSleeping through the double overload; both funnel here.
    //? if >=26.3 {
    /*@Inject(method = "setPosToBed(DLnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void correctDogBedHeight(double sleepHeight, BlockPos pos, CallbackInfo ci) {
    *///?} else {
    @Inject(method="setPosToBed", at = @At("HEAD"), cancellable = true, require=0)
    private void correctDogBedHeight(BlockPos pos, CallbackInfo ci) {
    //?}
        BlockState state = Compat.level(this).getBlockState(pos);
        if (state.getBlock() instanceof DogBedBlock) {
            //? if >=1.20.5 && <26.2 {
            Vec3 vec = pos.getBottomCenter();
            //?} else {
            /*Vec3 vec = Vec3.atBottomCenterOf(pos);
            *///?}
            //? if >=1.19.3 {
            Vector3f off = state.getValue(BedBlock.FACING).step().div(10);
            //?} else {
            /*Vec3 off = Vec3.atLowerCornerOf(state.getValue(BedBlock.FACING).getNormal()).scale(0.1f);
            *///?}
            setPos(vec.add(off.x, 0.35, off.z));
            ci.cancel();
        }
    }

    /** The authoritative gate for player_allow_attack_owner; AttackEntityCallback only sees vanilla melee. */
    // hurtServer() only exists from 1.21.2; below that the pre-server-split hurt() is the same hook.
    //? if >=1.21.2 {
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void blockDamageFromOwnedPet(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void blockDamageFromOwnedPet(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        // hurt() below 1.21.2 runs on both sides, and the ServerLevel cast further down would not survive.
        if (Compat.level(this).isClientSide()) return;
        // The paws gate is here rather than only on the swing callback so an arrow cannot walk past it.
        if (source.getEntity() instanceof Player attacker
                && attacker != (Object) this
                && PlayerCollarsMod.pawsBlockAttack(attacker, this)) {
            cir.setReturnValue(false);
            return;
        }
        // Cast needed -- this targets LivingEntity, so the compiler only sees Entity.
        if (!((Object) this instanceof Player owner)) return;
        if (!(source.getEntity() instanceof Player pet) || pet == owner) return;
        //? if >=1.21.2 {
        if (PlayerCollarsMod.ALLOW_ATTACK_OWNER.get(world)) return;
        //?} else {
        /*if (PlayerCollarsMod.ALLOW_ATTACK_OWNER.get((ServerLevel) Compat.level(owner))) return;
        *///?}
        if (EquippedAccessories.findOwned(pet, x -> x.is(PlayerCollarsMod.COLLAR_TAG), owner.getUUID(), pet.getUUID()) == null) return;

        Compat.sendOverlayMessage(pet, Text.translatable("message.playercollars.no_attack_owner").withStyle(ChatFormatting.RED));
        cir.setReturnValue(false);
    }

    //? if <1.21 {
    /*// Spiked has no pre-1.21 effect component, so the hit that landed is where it gets applied.
    @Inject(method = "hurt", at = @At("RETURN"))
    private void applySpikedCollar(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) return;
        if (!(Compat.level(this) instanceof ServerLevel world)) return;
        PlayerCollarsMod.applySpikedEnchantment(world, (LivingEntity) (Object) this, source);
    }
    *///?}
}
