package com.dipilodopilasaurus.leashablecollars.leash;

import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.world.entity.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
//? if >=26.2 {
/*import net.minecraft.world.entity.EntityTypes;
*///?}
import net.minecraft.world.entity.LivingEntity;
//? if >=1.21.11 {
import net.minecraft.world.entity.animal.turtle.Turtle;
//?} else {
/*import net.minecraft.world.entity.animal.Turtle;
*///?}
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import com.mojang.math.Constants;
import java.util.Objects;

public final class LeashProxyEntity extends Turtle {
    private final LivingEntity target;
    private static final EntityDimensions DIMENSIONS = EntityDimensions.fixed(Constants.EPSILON, Constants.EPSILON);
    public static final String MARKER_TAG = "playercollars.leash_anchor";

    private boolean proxyUpdate() {
        if (proxyIsRemoved()) return false;

        if (target == null) return true;
        if (Compat.level(target) != Compat.level(this) || !target.isAlive()) return true;

        Vec3 posActual = this.position();
        //? if >=1.19 {
        float visualYaw = target.getVisualRotationYInDegrees();
        //?} else {
        /*float visualYaw = target.yBodyRot;
        *///?}
        Vec3 posTarget = switch (target.getPose()) {
            // No point in making cases for SPIN_ATTACK since leashed players can't use it
            case CROUCHING: yield new Vec3(0.0D, 1.1D, -0.15D);
            case SWIMMING: yield Vec3.directionFromRotation(0, visualYaw).scale(0.35).add(0, 0.2, -0.1);
            case FALL_FLYING: yield new Vec3(0, 1.3, -0.15).xRot(-(float) Math.toRadians(90 + target.getXRot()))
                    .yRot(-(float) Math.toRadians(visualYaw));
            case SLEEPING: if (target.getBedOrientation() != null)
                    //? if >=1.19.3 {
                    yield new Vec3(target.getBedOrientation().step().mul(-0.2f)).add(0, 0.1, -0.15);
                    //?} else {
                    /*yield Vec3.atLowerCornerOf(target.getBedOrientation().getNormal()).scale(-0.2f).add(0, 0.1, -0.15);
                    *///?}
            default: yield new Vec3(0.0D, 1.3D, -0.15D);
        };
        posTarget = posTarget.scale(target.getScale()).add(target.position());

        if (!Objects.equals(posActual, posTarget)) {
            setRot(0.0F, 0.0F);
            setPosRaw(posTarget.x, posTarget.y, posTarget.z);
            setBoundingBox(DIMENSIONS.makeBoundingBox(target.position()));
        }

        return false;
    }

    @NotNull
    public LivingEntity getLeashTarget() {
        return target;
    }

    @Override
    public void tick() {
        if (Compat.level(this).isClientSide()) return;
        if (proxyUpdate() && !proxyIsRemoved()) {
            proxyRemove();
        }
    }

    public boolean proxyIsRemoved() {
        return this.isRemoved();
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player player) {
        return true;
    }

    public void proxyRemove() {
        super.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void remove(RemovalReason reason) {
    }

    @Override
    public boolean shouldBeSaved() {
        // Recreated on attach, so it must stay out of chunk save data -- a restart would reload an orphan.
        return false;
    }

    public static final String TEAM_NAME = "leashplayersimpl";

    public LeashProxyEntity(@NotNull LivingEntity target) {
        //? if >=26.2 {
        /*super(EntityTypes.TURTLE, Compat.level(target));
        *///?} else {
        super(EntityType.TURTLE, Compat.level(target));
        //?}
        this.target = target;

        setHealth(1.0F);
        // 26.3 split invulnerability into a permanent flag and a timed one.
        //? if >=26.3 {
        /*setPermanentlyInvulnerable(true);
        *///?} else {
        setInvulnerable(true);
        //?}
        setBaby(true);
        setInvisible(true);
        setNoAi(true);
        setNoGravity(true);
        setSilent(true);
        setCustomNameVisible(false);
        addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
        addTag(MARKER_TAG);
        noPhysics = true;

        MinecraftServer server = Compat.level(this).getServer();
        if (server != null) {
            ServerScoreboard scoreboard = server.getScoreboard();

            PlayerTeam team = scoreboard.getPlayerTeam(TEAM_NAME);
            if (team == null) {
                team = scoreboard.addPlayerTeam(TEAM_NAME);
            }
            if (team.getCollisionRule() != PlayerTeam.CollisionRule.NEVER) {
                team.setCollisionRule(PlayerTeam.CollisionRule.NEVER);
            }

            scoreboard.addPlayerToTeam(getScoreboardName(), team);
        }
        proxyUpdate();
    }

    @Override
    public float getHealth() {
        return 1.0F;
    }

    //? if >=1.21.2 {
    @Override
    public void dropLeash() {
        // Player interaction owns dropping the lead item; the proxy must not.
    }

    @Override
    public void removeLeash() {
    }
    //?} else {
    /*@Override
    public void dropLeash(boolean sendPacket, boolean dropItem) {
        // Player interaction owns dropping the lead item; the proxy must not.
    }
    *///?}

    @Override
    //? if >=1.20.5 {
    public boolean canBeLeashed() {
    //?} else {
    /*public boolean canBeLeashed(Player player) {
    *///?}
        return false;
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected void doPush(Entity entity) {
    }

    // 1.21.6 swapped CompoundTag for the ValueInput/ValueOutput pair.
    @Override
    //? if >=1.21.6 {
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Team", TEAM_NAME);
    }
    //?} else {
    /*public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Team", TEAM_NAME);
    }
    *///?}

    @Override
    public void push(Entity entity) {
    }

    @Override
    public void playerTouch(Player player) {
    }
}
