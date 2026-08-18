package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
//? if >=1.21.5 {
import net.minecraft.world.entity.animal.turtle.Turtle;
//?} else {
/*import net.minecraft.world.entity.animal.Turtle;
*///?}
import net.minecraft.world.level.Level;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.world.scores.PlayerTeam;
import com.dipilodopilasaurus.leashablecollars.leash.LeashProxyEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Turtle.class)
public abstract class MixinTurtleEntity extends Animal {
    protected MixinTurtleEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    //? if >=1.21.6 {
    private void leashplayers$onReadCustomDataFromNbt(ValueInput input, CallbackInfo info) {
    //?} else {
    /*private void leashplayers$onReadCustomDataFromNbt(CompoundTag tag, CallbackInfo info) {
    *///?}
        MinecraftServer server = level().getServer();
        if (server == null) return;

        PlayerTeam team = server.getScoreboard().getPlayersTeam(getScoreboardName());
        if (team != null && Objects.equals(team.getName(), LeashProxyEntity.TEAM_NAME)) {
            // 1.21.5 dropped dropLeash's broadcast flags; 1.21.6 made kill take a level.
            //? if >=1.21.5 {
            dropLeash();
            //?} else {
            /*dropLeash(true, true);
            *///?}
            setInvulnerable(false);
            //? if >=1.21.6 {
            kill((ServerLevel) level());
            //?} else {
            /*kill();
            *///?}
        }
    }
}
