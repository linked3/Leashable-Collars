package com.dipilodopilasaurus.leashablecollars.neoforge.leash;

import com.dipilodopilasaurus.leashablecollars.neoforge.LeashableCollarsNeoForge;
import com.dipilodopilasaurus.leashablecollars.neoforge.LeashConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerLeashHandler {
    private static final Map<UUID, LeashState> ACTIVE_LEASHES = new ConcurrentHashMap<>();

    // On the target's persistent NBT, so the leash survives a relog. ACTIVE_LEASHES is rebuilt from it
    // on the first server tick the target is loaded for. See onPlayerTick.
    private static final String NBT_ROOT = "playercollars:leash";
    private static final String NBT_HOLDER_TYPE = "holderType";
    private static final String NBT_HOLDER_UUID = "holder";
    private static final String NBT_KNOT_POS = "knotPos";
    private static final String NBT_LOYALTY = "loyalty";
    private static final String HOLDER_TYPE_PLAYER = "player";
    private static final String HOLDER_TYPE_KNOT = "knot";

    private PlayerLeashHandler() {
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (player.level().isClientSide()) {
            return;
        }

        if (target instanceof LeashFenceKnotEntity knot) {
            if (LeashableCollarsNeoForge.blockLeashKnotBreak(player, knot)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
            return;
        }

        if (!(target instanceof net.minecraft.server.level.ServerPlayer serverTarget)) {
            return;
        }

        ItemStack heldStack = player.getItemInHand(event.getHand());
        LeashState leashState = ACTIVE_LEASHES.get(serverTarget.getUUID());

        if (leashState != null && leashState.tryRelease(player)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (heldStack.getItem() != Items.LEAD || leashState != null) {
            return;
        }
        if (LeashableCollarsNeoForge.findOwnedCollar(serverTarget, player.getUUID(), serverTarget.getUUID()) == null) {
            return;
        }

        LeashState newState = new LeashState(serverTarget);
        newState.attach(player);
        ACTIVE_LEASHES.put(serverTarget.getUUID(), newState);
        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return;
        }

        LeashState leashState = ACTIVE_LEASHES.get(serverPlayer.getUUID());
        if (leashState == null) {
            // Rebuild from persisted NBT after a relog or server restart.
            leashState = LeashState.restore(serverPlayer);
            if (leashState == null) {
                return;
            }
            ACTIVE_LEASHES.put(serverPlayer.getUUID(), leashState);
        }

        // Re-bind to the live player instance (it is a fresh object after every relog).
        leashState.target = serverPlayer;

        if (leashState.update()) {
            ACTIVE_LEASHES.remove(serverPlayer.getUUID());
        }
    }

    public static boolean isAttachedFenceBreak(Player player, BlockPos pos) {
        LeashState leashState = ACTIVE_LEASHES.get(player.getUUID());
        return leashState != null && leashState.isAttachedFenceBreak(pos);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        for (LeashFenceKnotEntity knot : player.level().getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(event.getPos()), entity -> event.getPos().equals(entity.blockPosition()))) {
            if (!LeashableCollarsNeoForge.blockLeashKnotBreak(player, knot)) {
                continue;
            }

            event.setUseBlock(TriState.FALSE);
            event.setUseItem(TriState.FALSE);
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }
    }

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        for (LeashFenceKnotEntity knot : player.level().getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(event.getPos()), entity -> event.getPos().equals(entity.blockPosition()))) {
            if (LeashableCollarsNeoForge.blockLeashKnotBreak(player, knot)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    private static final class LeashState {
        private net.minecraft.server.level.ServerPlayer target;
        private LeashProxyEntity proxy;
        private Entity holder;
        private int attachTick;
        private int loyalty;

        // The holder's identity, kept apart from the live reference so it survives a reload.
        private UUID holderPlayerId;
        private BlockPos holderKnotPos;

        private LeashState(net.minecraft.server.level.ServerPlayer target) {
            this.target = target;
        }

        /**
         * {@code null} when the player has no saved leash. The holder is not resolved here --
         * {@link #update()} does that once it can be found.
         */
        private static LeashState restore(net.minecraft.server.level.ServerPlayer target) {
            CompoundTag root = target.getPersistentData();
            if (!root.contains(NBT_ROOT)) {
                return null;
            }
            CompoundTag tag = root.getCompound(NBT_ROOT);
            LeashState state = new LeashState(target);
            state.loyalty = tag.getInt(NBT_LOYALTY);
            String type = tag.getString(NBT_HOLDER_TYPE);
            if (HOLDER_TYPE_KNOT.equals(type)) {
                state.holderKnotPos = BlockPos.of(tag.getLong(NBT_KNOT_POS));
            } else if (tag.hasUUID(NBT_HOLDER_UUID)) {
                state.holderPlayerId = tag.getUUID(NBT_HOLDER_UUID);
            } else {
                // Corrupt entry -- clear it so we stop trying.
                root.remove(NBT_ROOT);
                return null;
            }
            return state;
        }

        private void save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt(NBT_LOYALTY, loyalty);
            if (holderKnotPos != null) {
                tag.putString(NBT_HOLDER_TYPE, HOLDER_TYPE_KNOT);
                tag.putLong(NBT_KNOT_POS, holderKnotPos.asLong());
            } else if (holderPlayerId != null) {
                tag.putString(NBT_HOLDER_TYPE, HOLDER_TYPE_PLAYER);
                tag.putUUID(NBT_HOLDER_UUID, holderPlayerId);
            } else {
                clearSaved();
                return;
            }
            target.getPersistentData().put(NBT_ROOT, tag);
        }

        private void clearSaved() {
            if (target != null) {
                target.getPersistentData().remove(NBT_ROOT);
            }
        }

        private void attach(Entity newHolder) {
            holder = newHolder;
            rememberHolderIdentity(newHolder);
            if (newHolder instanceof Player owner) {
                ItemStack collarStack = LeashableCollarsNeoForge.findOwnedCollar(target, owner.getUUID(), target.getUUID());
                loyalty = collarStack == null ? 0 : Math.min(2, LeashableCollarsNeoForge.getEnchantmentLevel(target.level(), collarStack, LeashableCollarsNeoForge.SHORT_LEASH_ENCHANTMENT));
            }
            if (proxy == null || proxy.proxyIsRemoved()) {
                proxy = new LeashProxyEntity(target);
                proxy.setPos(target.getX(), target.getY(), target.getZ());
                target.level().addFreshEntity(proxy);
            }
            proxy.setLeashedTo(holder, true);
            attachTick = target.tickCount;
            save();
        }

        private void rememberHolderIdentity(Entity newHolder) {
            if (newHolder instanceof LeashFenceKnotEntity knot) {
                holderKnotPos = knot.blockPosition();
                holderPlayerId = null;
            } else if (newHolder instanceof Player player) {
                holderPlayerId = player.getUUID();
                holderKnotPos = null;
            }
        }

        private void detach() {
            holder = null;
            holderPlayerId = null;
            holderKnotPos = null;
            removeProxy();
            clearSaved();
        }

        private void removeProxy() {
            if (proxy != null) {
                if (proxy.isAlive() || !proxy.proxyIsRemoved()) {
                    proxy.proxyRemove();
                }
                proxy = null;
            }
        }

        private void dropLead() {
            target.drop(new ItemStack(Items.LEAD), false, true);
        }

        private boolean tryRelease(Player player) {
            if (holder != player || attachTick + 20 >= target.tickCount) {
                return false;
            }
            if (proxy != null && !proxy.canUnleash(player)) {
                return true;
            }
            if (!player.getAbilities().instabuild) {
                dropLead();
            }
            detach();
            return true;
        }

        /**
         * {@code null} when the holder is offline or its chunk unloaded. Not an error -- the leash pauses.
         */
        private Entity resolveHolder() {
            if (holderKnotPos != null) {
                for (LeashFenceKnotEntity knot : target.level().getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(holderKnotPos), entity -> holderKnotPos.equals(entity.blockPosition()))) {
                    return knot;
                }
                return null;
            }
            if (holderPlayerId != null) {
                MinecraftServer server = target.getServer();
                return server == null ? null : server.getPlayerList().getPlayer(holderPlayerId);
            }
            return null;
        }

        private boolean update() {
            if (target.hasDisconnected()) {
                // Logging out: pause and keep the persisted leash. The proxy self-removes.
                return false;
            }
            if (syncProxyState()) {
                return true;
            }

            // If the holder can't be found the leash pauses; the persisted state stays and we retry.
            Entity resolved = resolveHolder();
            if (resolved == null) {
                if (holderPlayerId == null && holderKnotPos == null) {
                    // Nothing persisted, so nothing to maintain.
                    detach();
                    return true;
                }
                // Holder unavailable -- drop the live proxy link, keep the leash.
                removeProxy();
                holder = null;
                return false;
            }
            if (resolved != holder) {
                attach(resolved);
            }

            if (shouldDropForInvalidState()) {
                boolean shouldDropLead = shouldDropLeadForInvalidState();
                detach();
                if (shouldDropLead) {
                    dropLead();
                }
                return true;
            }

            applyPull();
            return holder == null;
        }

        private boolean shouldDropForInvalidState() {
            // update() already handled a disconnected target, so reaching here means they are online.
            return holder != null && (!holder.isAlive() || !target.isAlive() || target.isVehicle());
        }

        private boolean shouldDropLeadForInvalidState() {
            if (!(holder instanceof LeashFenceKnotEntity knot) || holder.isAlive()) {
                return true;
            }

            return !(knot.level().getBlockState(knot.blockPosition()).getBlock() instanceof FenceBlock);
        }

        private boolean syncProxyState() {
            if (proxy == null) {
                return false;
            }
            if (proxy.proxyIsRemoved()) {
                proxy = null;
                return false;
            }

            Entity actualHolder = holder;
            Entity targetHolder = proxy.getLeashHolder();
            if (targetHolder == null && actualHolder != null) {
                detach();
                return true;
            }
            if (targetHolder != null && targetHolder != actualHolder) {
                attach(targetHolder);
            }
            return false;
        }

        private boolean isAttachedFenceBreak(BlockPos pos) {
            if (holder instanceof LeashFenceKnotEntity knot && pos.equals(knot.blockPosition())) {
                return true;
            }
            return holderKnotPos != null && holderKnotPos.equals(pos);
        }

        private void applyPull() {
            if (holder == null || holder.level() != target.level()) {
                return;
            }

            float distance = target.distanceTo(holder);
            double minDistance = Math.max(LeashConfig.getMinDistanceFloor(), LeashConfig.getMinDistanceBase() - loyalty);
            double maxDistance = LeashConfig.getMaxDistanceBase() - loyalty;
            if (distance < minDistance) {
                return;
            }

            if (distance > maxDistance && target.level().getGameRules().getBoolean(LeashableCollarsNeoForge.PLAYER_LEASHES_BREAK_RULE)) {
                detach();
                dropLead();
                return;
            }

            double dx = (holder.getX() - target.getX()) / distance;
            double dy = (holder.getY() - target.getY()) / distance;
            double dz = (holder.getZ() - target.getZ()) / distance;
            double factor = LeashConfig.getPullFactorBase() + LeashConfig.getPullFactorPerLoyalty() * loyalty;
                double verticalFactor = dy > 0.0D ? LeashConfig.getVerticalFactorUp() : LeashConfig.getVerticalFactorDown();

            target.push(
                    Math.copySign(dx * dx * factor, dx),
                    Math.copySign(dy * dy * factor * verticalFactor, dy),
                    Math.copySign(dz * dz * factor, dz)
            );
            target.connection.send(new ClientboundSetEntityMotionPacket(target));
            target.hasImpulse = false;
        }
    }
}
