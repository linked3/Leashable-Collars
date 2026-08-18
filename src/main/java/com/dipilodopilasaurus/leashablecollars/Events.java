package com.dipilodopilasaurus.leashablecollars;

//? if fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
//?} else {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

/**
 * The loader seam for gameplay events. The callbacks are shaped like Fabric's because that is what the
 * handler bodies were written against; NeoForge's cancellation stands in for the allow/deny boolean, and
 * a {@code PASS} cancels nothing.
 */
public final class Events {
    private Events() {
    }

    @FunctionalInterface
    public interface BlockBreakBefore {
        /** @return false to veto the break. */
        boolean allow(Level level, Player player, BlockPos pos);
    }

    @FunctionalInterface
    public interface UseEntity {
        InteractionResult interact(Player player, Level level, InteractionHand hand, Entity entity);
    }

    @FunctionalInterface
    public interface AttackEntity {
        InteractionResult attack(Player player, Level level, Entity entity);
    }

    @FunctionalInterface
    public interface UseItem {
        InteractionResult use(Player player, Level level, InteractionHand hand);
    }

    @FunctionalInterface
    public interface UseBlock {
        InteractionResult use(Player player, Level level, InteractionHand hand, BlockHitResult hit);
    }

    public static void onServerTickEnd(Consumer<MinecraftServer> handler) {
        //? if fabric {
        ServerTickEvents.END_SERVER_TICK.register(handler::accept);
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> handler.accept(event.getServer()));
        *///?}
    }

    public static void onBlockBreakBefore(BlockBreakBefore handler) {
        //? if fabric {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> handler.allow(level, player, pos));
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(BlockEvent.BreakEvent.class, event -> {
            if (!handler.allow(event.getPlayer().level(), event.getPlayer(), event.getPos())) {
                event.setCanceled(true);
            }
        });
        *///?}
    }

    public static void onUseEntity(UseEntity handler) {
        //? if fabric {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> handler.interact(player, level, hand, entity));
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, event -> {
            InteractionResult result = handler.interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
    }

    public static void onAttackEntity(AttackEntity handler) {
        //? if fabric {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> handler.attack(player, level, entity));
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(AttackEntityEvent.class, event -> {
            InteractionResult result = handler.attack(event.getEntity(), event.getEntity().level(), event.getTarget());
            // No result on this event, so cancelling is the only signal: FAIL alone stops the attack.
            if (result == InteractionResult.FAIL) event.setCanceled(true);
        });
        *///?}
    }

    public static void onUseItem(UseItem handler) {
        //? if fabric {
        UseItemCallback.EVENT.register(handler::use);
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickItem.class, event -> {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
    }

    public static void onUseBlock(UseBlock handler) {
        //? if fabric {
        UseBlockCallback.EVENT.register(handler::use);
        //?} else {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
    }
}
