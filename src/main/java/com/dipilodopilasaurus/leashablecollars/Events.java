package com.dipilodopilasaurus.leashablecollars;

//? if fabric {
//? if >=1.19 {
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//?} else {
/*import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
*///?}
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
//?}
//? if neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
*///?}
//? if neoforge && >=26.1 {
/*import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
*///?} elif neoforge {
/*import net.neoforged.neoforge.event.level.BlockEvent;
*///?}
//? if forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//? if >=1.19 {
import net.minecraftforge.event.level.BlockEvent;
//?} else {
/^import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
^///?}
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
*///?}
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
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
 * Loader seam for gameplay events, shaped like Fabric's since that's what the handler bodies target.
 * On the bus loaders cancellation stands in for allow/deny, and a PASS cancels nothing.
 */
public final class Events {
    private Events() {
    }

    //? if forge {
    /*// Forge's IEventBus has no (Class, Consumer) overload; the four-arg one names the type instead.
    private static <T extends Event> void listen(Class<T> type, Consumer<T> handler) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, handler);
    }
    *///?}

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

    public static void onRegisterCommands(Consumer<CommandDispatcher<CommandSourceStack>> handler) {
        //? if fabric {
        //? if >=1.19 {
        CommandRegistrationCallback.EVENT.register((dispatcher, registries, environment) -> handler.accept(dispatcher));
        //?} else {
        /*CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> handler.accept(dispatcher));
        *///?}
        //?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> handler.accept(event.getDispatcher()));
        *///?}
        //? if forge {
        /*listen(RegisterCommandsEvent.class, event -> handler.accept(event.getDispatcher()));
        *///?}
    }

    public static void onServerTickEnd(Consumer<MinecraftServer> handler) {
        //? if fabric {
        ServerTickEvents.END_SERVER_TICK.register(handler::accept);
        //?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> handler.accept(event.getServer()));
        *///?}
        //? if forge {
        /*listen(TickEvent.ServerTickEvent.class, event -> {
            if (event.phase == TickEvent.Phase.END) {
                //? if >=1.19 {
                handler.accept(event.getServer());
                //?} else {
                /^handler.accept(ServerLifecycleHooks.getCurrentServer());
                ^///?}
            }
        });
        *///?}
    }

    public static void onBlockBreakBefore(BlockBreakBefore handler) {
        //? if fabric {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> handler.allow(level, player, pos));
        //?}
        // 26.1 moved the event to its own class under event.level.block.
        //? if neoforge && >=26.1 {
        /*NeoForge.EVENT_BUS.addListener(BreakBlockEvent.class, event -> {
            if (!handler.allow(Compat.level(event.getPlayer()), event.getPlayer(), event.getPos())) {
                event.setCanceled(true);
            }
        });
        *///?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(BlockEvent.BreakEvent.class, event -> {
            if (!handler.allow(Compat.level(event.getPlayer()), event.getPlayer(), event.getPos())) {
                event.setCanceled(true);
            }
        });
        *///?}
        //? if forge {
        /*listen(BlockEvent.BreakEvent.class, event -> {
            if (!handler.allow(Compat.level(event.getPlayer()), event.getPlayer(), event.getPos())) {
                event.setCanceled(true);
            }
        });
        *///?}
    }

    public static void onUseEntity(UseEntity handler) {
        //? if fabric {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> handler.interact(player, level, hand, entity));
        //?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, event -> {
            InteractionResult result = handler.interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
        //? if forge {
        /*listen(PlayerInteractEvent.EntityInteract.class, event -> {
            //? if >=1.19 {
            InteractionResult result = handler.interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
            //?} else {
            /^InteractionResult result = handler.interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget());
            ^///?}
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
        //?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(AttackEntityEvent.class, event -> {
            InteractionResult result = handler.attack(event.getEntity(), Compat.level(event.getEntity()), event.getTarget());
            // No result on this event, so cancelling is the only signal: FAIL alone stops the attack.
            if (result == InteractionResult.FAIL) event.setCanceled(true);
        });
        *///?}
        //? if forge {
        /*listen(AttackEntityEvent.class, event -> {
            //? if >=1.19 {
            InteractionResult result = handler.attack(event.getEntity(), Compat.level(event.getEntity()), event.getTarget());
            //?} else {
            /^InteractionResult result = handler.attack(event.getPlayer(), Compat.level(event.getPlayer()), event.getTarget());
            ^///?}
            // No result on this event, so cancelling is the only signal: FAIL alone stops the attack.
            if (result == InteractionResult.FAIL) event.setCanceled(true);
        });
        *///?}
    }

    public static void onUseItem(UseItem handler) {
        //? if fabric && >=1.21.2 {
        UseItemCallback.EVENT.register(handler::use);
        //?} elif fabric {
        /*UseItemCallback.EVENT.register((player, level, hand) ->
                Compat.useResult(handler.use(player, level, hand), player.getItemInHand(hand)));
        *///?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickItem.class, event -> {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
        //? if forge {
        /*listen(PlayerInteractEvent.RightClickItem.class, event -> {
            //? if >=1.19 {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand());
            //?} else {
            /^InteractionResult result = handler.use(event.getPlayer(), event.getWorld(), event.getHand());
            ^///?}
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
        //?}
        //? if neoforge {
        /*NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
        //? if forge {
        /*listen(PlayerInteractEvent.RightClickBlock.class, event -> {
            //? if >=1.19 {
            InteractionResult result = handler.use(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
            //?} else {
            /^InteractionResult result = handler.use(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec());
            ^///?}
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        });
        *///?}
    }
}
