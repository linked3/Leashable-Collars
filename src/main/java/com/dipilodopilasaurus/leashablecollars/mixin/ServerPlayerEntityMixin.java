package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import com.dipilodopilasaurus.leashablecollars.DeedHolder;
import com.dipilodopilasaurus.leashablecollars.SignedDeeds;
import com.dipilodopilasaurus.leashablecollars.enchant.AccessoryEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements DeedHolder {
    private static final ThreadLocal<Boolean> playercollars$applyingCollarThorns = ThreadLocal.withInitial(() -> false);

    @Unique
    private SignedDeeds playercollars$deeds = SignedDeeds.EMPTY;

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        //? if >=1.21.8 {
        super(world, gameProfile);
        //?} else {
        /*super(world, pos, yaw, gameProfile);
        *///?}
    }

    @Override
    public SignedDeeds playercollars$deeds() {
        return playercollars$deeds;
    }

    @Override
    public void playercollars$setDeeds(SignedDeeds deeds) {
        playercollars$deeds = deeds;
    }

    // Respawn copies field by field and nothing re-reads save data, so a death would revoke every deed.
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void playercollars$carryDeeds(ServerPlayer previous, boolean keepEverything, CallbackInfo info) {
        playercollars$deeds = ((DeedHolder) previous).playercollars$deeds();
    }

    // 1.21.6 swapped CompoundTag for the ValueInput/ValueOutput pair; see MixinServerPlayerEntity.
    //? if >=1.21.6 {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void playercollars$saveDeeds(ValueOutput output, CallbackInfo info) {
    //?} else {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void playercollars$saveDeeds(CompoundTag output, CallbackInfo info) {
    *///?}
        if (!playercollars$deeds.entries().isEmpty()) {
            SignedDeeds.write(output, playercollars$deeds);
        }
    }

    //? if >=1.21.6 {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void playercollars$loadDeeds(ValueInput input, CallbackInfo info) {
    //?} else {
    /*@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void playercollars$loadDeeds(CompoundTag input, CallbackInfo info) {
    *///?}
        playercollars$deeds = SignedDeeds.read(input);
    }

    // hurtServer() only exists from 1.21.2; below that the pre-server-split hurt() is the same hook.
    //? if >=1.21.2 {
    @Inject(at=@At("TAIL"), method="hurtServer")
    private void checkCollarThorns(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*@Inject(at=@At("TAIL"), method="hurt")
    private void checkCollarThorns(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (playercollars$applyingCollarThorns.get()) return;

        ServerPlayer victim = (ServerPlayer) (Object) this;
        Entity causingEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        if (!(causingEntity instanceof LivingEntity)) return;
        if (playercollars$isSameEntity(causingEntity, victim) || playercollars$isSameEntity(directEntity, victim)) return;

        // Reflected/self-fired projectiles can report the shooter as the causing entity.
        // Applying collar thorns back to the same player re-enters hurtServer until the server overflows the stack.
        if (playercollars$isProjectileOwnedBy(directEntity, victim)) return;

        playercollars$applyingCollarThorns.set(true);
        try {
            //? if >=1.21.2 {
            AccessoryEnchantments.postAttack(world, victim, source);
            //?} else {
            /*AccessoryEnchantments.postAttack((ServerLevel) Compat.level(victim), victim, source);
            *///?}
        } finally {
            playercollars$applyingCollarThorns.set(false);
        }
    }

    // Vanilla ticks armour enchantments here; an accessory slot is no EquipmentSlot, so Healing needs feeding.
    @Inject(method = "tick", at = @At("TAIL"))
    private void playercollars$tickCollarEnchantments(CallbackInfo info) {
        if (Compat.level(this) instanceof ServerLevel world) {
            AccessoryEnchantments.tick(world, this);
        }
    }

    private static boolean playercollars$isSameEntity(Entity entity, Entity target) {
        return entity != null && entity.getUUID().equals(target.getUUID());
    }

    private static boolean playercollars$isProjectileOwnedBy(Entity entity, Entity owner) {
        return entity instanceof Projectile projectile && playercollars$isSameEntity(projectile.getOwner(), owner);
    }
}
