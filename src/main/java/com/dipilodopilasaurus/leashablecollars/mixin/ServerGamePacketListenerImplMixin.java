package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.network.protocol.PacketUtils;
//? if >=1.19 {
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
//?} else {
/*import net.minecraft.server.network.TextFilter;
*///?}
//? if >=1.20.5 {
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
//?}
//? if >=1.19 {
import net.minecraft.network.protocol.game.ServerboundChatPacket;
//?}
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//? if >=1.21.4 {
import net.minecraft.world.entity.player.Input;
//?}
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    // Loom skips a descriptor-less selector without failing the build, so the descriptor is mandatory.
    //? if >=1.21.9 {
    private static final String TRY_HANDLE_CHAT =
            "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;tryHandleChat(Ljava/lang/String;ZLjava/lang/Runnable;)V";
    //?} elif >=1.20.5 {
    /*private static final String TRY_HANDLE_CHAT =
            "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;tryHandleChat(Ljava/lang/String;Ljava/lang/Runnable;)V";
    *///?}

    @Shadow public ServerPlayer player;

    //? if >=1.21.11 {
    @Shadow public abstract boolean hasClientLoaded();
    //?}

    //? if >=1.19 {
    @Inject(
            method = "handleChat",
            //? if >=1.20.5 {
            at = @At(value = "INVOKE", target = TRY_HANDLE_CHAT),
            //?} else {
            /*// tryHandleChat still takes the signature bundle below 1.20.5, so this one goes in at the top.
            at = @At("HEAD"),
            *///?}
            cancellable = true
    )
    private void playercollars$blockRestrictedSpeech(ServerboundChatPacket packet, CallbackInfo ci) {
        if (PetControlHelper.handleBlockedSpeech(player)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "handleChatCommand",
            //? if >=1.20.5 {
            at = @At(value = "INVOKE", target = TRY_HANDLE_CHAT),
            //?} else {
            /*// tryHandleChat still takes the signature bundle below 1.20.5, so this one goes in at the top.
            at = @At("HEAD"),
            *///?}
            cancellable = true
    )
    private void playercollars$blockUnsignedCommand(ServerboundChatCommandPacket packet, CallbackInfo ci) {
        if (PetControlHelper.handleBlockedCommand(player)) {
            ci.cancel();
        }
    }

    //?} else {
    /*// This overload receives normalized chat and commands on the server thread after text filtering.
    @Inject(method = "handleChat(Lnet/minecraft/server/network/TextFilter$FilteredText;)V",
            at = @At("HEAD"), cancellable = true)
    private void playercollars$blockLegacyChat(TextFilter.FilteredText message, CallbackInfo ci) {
        boolean blocked = message.getRaw().startsWith("/")
                ? PetControlHelper.handleBlockedCommand(player)
                : PetControlHelper.handleBlockedSpeech(player);
        if (blocked) ci.cancel();
    }
    *///?}

    // Command signing split into its own packet in 1.20.5; below it handleChatCommand covers both.
    //? if >=1.20.5 {
    @Inject(
            method = "handleSignedChatCommand",
            at = @At(value = "INVOKE", target = TRY_HANDLE_CHAT),
            cancellable = true
    )
    private void playercollars$blockSignedCommand(ServerboundChatCommandSignedPacket packet, CallbackInfo ci) {
        if (PetControlHelper.handleBlockedCommand(player)) {
            ci.cancel();
        }
    }
    //?}
    @Inject(method = "handlePlayerInput", at = @At("HEAD"), cancellable = true)
    private void playercollars$restrainPlayerInput(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
        if (!PetControlHelper.isMovementRestrained(player)) return;

        // 1.21.4 swapped the setter for Input, 1.21.6 dropped serverLevel(); hasClientLoaded() is 1.21.11+.
        //? if >=1.21.11 {
        PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl) (Object) this, player.level());
        player.setLastClientInput(Input.EMPTY);
        if (hasClientLoaded()) {
            player.resetLastActionTime();
            player.setShiftKeyDown(false);
        }
        //?} elif >=1.21.6 {
        /*PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl) (Object) this, player.level());
        player.setLastClientInput(Input.EMPTY);
        player.resetLastActionTime();
        player.setShiftKeyDown(false);
        *///?} elif >=1.21.4 {
        /*PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl) (Object) this, Compat.serverLevel(player));
        player.setLastClientInput(Input.EMPTY);
        player.resetLastActionTime();
        player.setShiftKeyDown(false);
        *///?} else {
        /*PacketUtils.ensureRunningOnSameThread(packet, (ServerGamePacketListenerImpl) (Object) this, Compat.serverLevel(player));
        player.setPlayerInput(0.0F, 0.0F, false, false);
        player.resetLastActionTime();
        player.setShiftKeyDown(false);
        *///?}
        ci.cancel();
    }
}
