package com.dipilodopilasaurus.leashablecollars.command;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/** The recovery path for a deed that was lost or never signed. Operator-only, per upstream 1.7.8. */
public final class CollarCommand {
    private CollarCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("collar")
                .requires(Compat.gameMasterOnly())
                .then(Commands.argument("player_to_give", EntityArgument.player())
                        .then(Commands.argument("player_who_will_own", EntityArgument.player())
                                .then(Commands.argument("owned_player", EntityArgument.player())
                                        .then(Commands.argument("can_leash_forcibly", BoolArgumentType.bool())
                                                .executes(CollarCommand::give))))));
    }

    private static int give(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer recipient = EntityArgument.getPlayer(context, "player_to_give");
        ServerPlayer owner = EntityArgument.getPlayer(context, "player_who_will_own");
        ServerPlayer owned = EntityArgument.getPlayer(context, "owned_player");
        boolean canLeashForcibly = BoolArgumentType.getBool(context, "can_leash_forcibly");

        ItemStack deed = new ItemStack(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED);
        Components.set(deed, PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(
                owner.getUUID(), owner.getName().getString(),
                Optional.of(owned.getUUID()), Optional.of(owned.getName().getString()),
                canLeashForcibly));
        // 26.3 replaced drop's throwerName flag with a Prediction; this runs server-side only.
        //? if >=26.3 {
        /*if (!recipient.getInventory().add(deed)) recipient.drop(deed, false, net.minecraft.util.Prediction.SERVER_ONLY);
        *///?} else {
        if (!recipient.getInventory().add(deed)) recipient.drop(deed, false);
        //?}

        Component yesNo = Text.translatable(canLeashForcibly ? "gui.yes" : "gui.no");
        Compat.commandSuccess(context.getSource(), () -> Text.translatable("command.playercollars.collar.success",
                recipient.getName(), owner.getName(), owned.getName(), yesNo), true);
        return 1;
    }
}
