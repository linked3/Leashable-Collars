package com.dipilodopilasaurus.leashablecollars.leash.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.mojang.authlib.GameProfile;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.leash.LeashImpl;
import com.dipilodopilasaurus.leashablecollars.leash.LeashProxyEntity;
import com.dipilodopilasaurus.leashablecollars.leash.LeashSaveData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayerEntity extends Player implements LeashImpl {
    @Shadow public abstract boolean hasDisconnected();

    @Shadow public abstract ServerLevel level();

    @Shadow public ServerGamePacketListenerImpl connection;
    @Unique
    private LeashProxyEntity leashplayers$proxy;
    @Unique
    private Entity leashplayers$holder;
    // The holder's identity, saved alongside the live reference above. At most one is ever set;
    // both null means this player is not leashed. See LeashSaveData.
    @Unique
    private UUID leashplayers$holderId;
    @Unique
    private BlockPos leashplayers$knotPos;
    @Unique
    private int leashplayers$lastage;
    @Unique
    private double leashplayer$loyalty;
    @Unique
    private static final double FIREWORK_SEARCH_RADIUS = 128.0;
    @Unique
    private static final ResourceKey<Enchantment> SHORT_LEASH_KEY = ResourceKey.create(Registries.ENCHANTMENT, Ids.of("short_leash"));

    public MixinServerPlayerEntity(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Unique
    private void leashplayers$update() {
        // A target on their way out keeps everything -- addAdditionalSaveData runs a moment later and
        // the proxy self-removes. Tearing down here is what used to eat the lead on every relog.
        if (hasDisconnected()) return;

        // Death still breaks the leash, as it did before any of this was persisted.
        if (!isAlive()) {
            if (leashplayers$holder != null || leashplayers$isLeashSaved()) {
                leashplayers$detach();
                leashplayers$drop();
            }
            return;
        }

        // Before the proxy, not after: a logged-out holder leaves the proxy's leash holder null, which
        // the sync below would read as an in-world unleash and drop the lead.
        if (leashplayers$isLeashSaved() && !leashplayers$resolveAndAttach()) {
            return;
        }

        if (leashplayers$proxy != null) {
            if (leashplayers$proxy.proxyIsRemoved()) {
                leashplayers$proxy = null;
            }
            else {
                Entity holderActual = leashplayers$holder;
                Entity holderTarget = leashplayers$proxy.getLeashHolder();

                if (holderTarget == null && holderActual != null) {
                    leashplayers$detach();
                    leashplayers$drop();
                }
                else if (holderTarget != holderActual) {
                    leashplayers$attach(holderTarget);
                }
            }
        }

        if (leashplayers$holder != null && !leashplayers$holder.isAlive()) {
            leashplayers$detach();
            leashplayers$drop();
        }

        leashplayers$apply();
    }

    /** True when a holder identity is saved, whether or not that holder is reachable right now. */
    @Unique
    private boolean leashplayers$isLeashSaved() {
        return leashplayers$holderId != null || leashplayers$knotPos != null;
    }

    /**
     * Binds the live holder for the saved identity. {@code false} means the holder is offline or its
     * chunk unloaded -- not an error, and it must not break the leash, which resumes on their return.
     */
    @Unique
    private boolean leashplayers$resolveAndAttach() {
        Entity resolved = leashplayers$resolveHolder();
        if (resolved == null) {
            leashplayers$holder = null;
            leashplayers$removeProxy();
            return false;
        }
        if (resolved != leashplayers$holder) {
            leashplayers$attach(resolved);
        }
        return true;
    }

    @Unique
    private Entity leashplayers$resolveHolder() {
        BlockPos knotPos = leashplayers$knotPos;
        if (knotPos != null) {
            for (LeashFenceKnotEntity knot : level().getEntitiesOfClass(
                    LeashFenceKnotEntity.class, new AABB(knotPos), other -> knotPos.equals(other.blockPosition()))) {
                return knot;
            }
            return null;
        }
        if (leashplayers$holderId != null) {
            MinecraftServer server = level().getServer();
            return server == null ? null : server.getPlayerList().getPlayer(leashplayers$holderId);
        }
        return null;
    }

    @Unique
    private void leashplayers$apply() {
        Entity holder = leashplayers$holder;
        if (holder == null) return;
        if (holder.level() != level()) {
            leashplayers$detach();
            leashplayers$drop();
            return;
        }

        InteractionResult result;
        if (Math.abs(getY() - holder.getY()) > 6 + leashplayer$loyalty) {
            result = InteractionResult.FAIL;
        } else {
            // Don't pull on the Y axis - it'll make the unfortunate player fly all over the place
            Vec3 pos = new Vec3(holder.getX(), getY(), holder.getZ());
            result = PlayerCollarsMod.pullPlayerTowards((ServerPlayer) (Object) this, pos,
                    leashplayer$loyalty, leashplayer$loyalty + 6, (x) -> Math.min(0.15 * (x - leashplayer$loyalty), 0.375) / x);
        }

        if (result == InteractionResult.FAIL) {
            if (PlayerCollarsMod.PLAYER_LEASHES_BREAK_RULE.get(level())) {
                leashplayers$detach();
                leashplayers$drop();
            } else {
                // leashplayers$killFireworksOfPlayer(); // Ended up not using this
                this.setDeltaMovement(Vec3.ZERO);
                leashplayers$proxy.setPos(holder.position());
                leashplayers$proxy.setYRot(leashplayers$proxy.getYRot());
                leashplayers$proxy.setXRot(leashplayers$proxy.getXRot());
                connection.teleport(holder.getX(), holder.getY(), holder.getZ(), getYRot(), getXRot());
            }
        }
    }

    @Unique
    private void leashplayers$killFireworksOfPlayer() {
        for (FireworkRocketEntity rocket : level().getEntitiesOfClass(
                FireworkRocketEntity.class,
                getBoundingBox().inflate(FIREWORK_SEARCH_RADIUS),
                rocket -> true
        )) {
            Entity owner = rocket.getOwner();
            if (owner != null) {
                UUID ownerUUID = owner.getUUID();
                if (ownerUUID != null && ownerUUID.equals(this.getUUID())) {
                    rocket.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
    }

    @Unique
    private void leashplayers$attach(Entity entity) {
        leashplayers$holder = entity;
        leashplayers$rememberHolder(entity);

        // A paused leash can outlive its proxy, and setLeashedTo on a removed entity silently does
        // nothing, so the leash would never appear.
        if (leashplayers$proxy == null || leashplayers$proxy.proxyIsRemoved()) {
            leashplayers$proxy = new LeashProxyEntity(this);
            level().addFreshEntity(leashplayers$proxy);
        }
        leashplayers$proxy.setLeashedTo(leashplayers$holder, true);

        if (this.isPassenger() && !PlayerCollarsMod.LEASHED_PLAYERS_RIDE_ENTITIES.get(this.level())) {
            this.stopRiding();
        }

        leashplayers$lastage = tickCount;
    }

    /**
     * Records what the holder is, so the leash survives a restart. Anything but a player or a fence
     * knot leaves both fields null, which switches persistence off and keeps the old behaviour.
     */
    @Unique
    private void leashplayers$rememberHolder(Entity entity) {
        if (entity instanceof LeashFenceKnotEntity knot) {
            leashplayers$knotPos = knot.blockPosition();
            leashplayers$holderId = null;
        } else if (entity instanceof Player player) {
            leashplayers$holderId = player.getUUID();
            leashplayers$knotPos = null;
        } else {
            leashplayers$holderId = null;
            leashplayers$knotPos = null;
        }
    }

    @Unique
    private void leashplayers$detach() {
        leashplayers$holder = null;
        leashplayers$holderId = null;
        leashplayers$knotPos = null;
        leashplayers$removeProxy();
    }

    @Unique
    private void leashplayers$removeProxy() {
        if (leashplayers$proxy != null) {
            if (leashplayers$proxy.isAlive() || !leashplayers$proxy.proxyIsRemoved()) {
                leashplayers$proxy.proxyRemove();
            }
            leashplayers$proxy = null;
        }
    }

    @Unique
    private void leashplayers$drop() {
        drop(new ItemStack(Items.LEAD), false, true);
    }

    /** The leash in savable form, or {@code null} when there is nothing to save. */
    @Unique
    private LeashSaveData leashplayers$toSaveData() {
        // A dead player's leash breaks on the next tick anyway, so this would restore a dead leash.
        if (!isAlive()) return null;
        if (leashplayers$knotPos != null) return LeashSaveData.ofKnot(leashplayers$knotPos, leashplayer$loyalty);
        if (leashplayers$holderId != null) return LeashSaveData.ofPlayer(leashplayers$holderId, leashplayer$loyalty);
        return null;
    }

    @Unique
    private void leashplayers$fromSaveData(LeashSaveData data) {
        if (data == null || data.isEmpty()) return;
        leashplayers$holderId = data.holderPlayer().orElse(null);
        leashplayers$knotPos = data.holderKnot().orElse(null);
        leashplayer$loyalty = data.leashDistance();
        // Not resolved here -- on a world load the holder is very likely still logging in.
        // leashplayers$update() picks it up on the first tick that can find them.
    }

    @Unique
    private double leashplayers$getLeashDistance(ItemStack collar) {
        double distance = getAttributeValue(PlayerCollarsMod.ATTR_LEASH_DISTANCE);
        Holder.Reference<Enchantment> enchantment = level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(SHORT_LEASH_KEY)
                .orElse(null);
        if (enchantment != null) {
            distance -= EnchantmentHelper.getItemEnchantmentLevel(enchantment, collar);
        }
        return Mth.clamp(distance, 2.0, 16.0);
    }

    // 1.21.6 swapped CompoundTag for the ValueInput/ValueOutput pair. Only the parameter type differs;
    // the selector is unambiguous either way.
    //? if >=1.21.6 {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void leashplayers$save(ValueOutput output, CallbackInfo info) {
    //?} else {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void leashplayers$save(CompoundTag output, CallbackInfo info) {
    *///?}
        LeashSaveData data = leashplayers$toSaveData();
        if (data != null) {
            LeashSaveData.write(output, data);
        }
    }

    //? if >=1.21.6 {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void leashplayers$load(ValueInput input, CallbackInfo info) {
    //?} else {
    /*@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void leashplayers$load(CompoundTag input, CallbackInfo info) {
    *///?}
        leashplayers$fromSaveData(LeashSaveData.read(input));
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void leashplayers$tick(CallbackInfo info) {
        leashplayers$update();
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("HEAD"), cancellable = true)
    private void leashplayers$startriding(Entity entity, boolean force, boolean teleport, CallbackInfoReturnable<Boolean> cir) {

        boolean isLeashed = this.leashplayers$getProxyLeashHolder() != null;
        boolean disallowMount = !PlayerCollarsMod.LEASHED_PLAYERS_RIDE_ENTITIES.get(this.level());

        if (isLeashed && disallowMount) {
            Compat.sendOverlayMessage(this, Component.translatable("message.playercollars.no_ride_entity"));
            cir.cancel();
        }
    }

    @Override
    public Entity leashplayers$getProxyLeashHolder() {
        return leashplayers$proxy == null ? null : leashplayers$proxy.getLeashHolder();
    }

    @Override
    public InteractionResult leashplayers$interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.LEAD && leashplayers$holder == null) {
            ItemStack is = EquippedAccessories.findOwned(this, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG), player.getUUID(), getUUID());
            if (is == null) {
                // Without a message the lead just silently does nothing, which players read as the
                // leash being broken rather than as missing ownership.
                Compat.sendOverlayMessage(player, Component.translatable(
                        EquippedAccessories.hasEquipped(this, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG))
                                ? "message.playercollars.leash.not_owner"
                                : "message.playercollars.leash.no_collar").withStyle(ChatFormatting.RED));
                return InteractionResult.PASS;
            }
            leashplayer$loyalty = leashplayers$getLeashDistance(is);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            leashplayers$attach(player);
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() == Items.LEAD && leashplayers$holder != null && leashplayers$holder != player) {
            Compat.sendOverlayMessage(player, Component.translatable("message.playercollars.leash.already_leashed").withStyle(ChatFormatting.RED));
            return InteractionResult.PASS;
        }

        // Only an empty hand or another lead unleashes -- unlike a vanilla mob, a leashed player is
        // still something you want to use items on. Any held item used to drop the leash instead.
        boolean unleashingItem = stack.isEmpty() || stack.getItem() == Items.LEAD;
        if (unleashingItem && leashplayers$holder == player && leashplayers$lastage + 20 < tickCount) {
            if (!player.isCreative()) {
                leashplayers$drop();
            }
            leashplayers$detach();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
