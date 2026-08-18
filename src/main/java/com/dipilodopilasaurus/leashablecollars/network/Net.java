package com.dipilodopilasaurus.leashablecollars.network;

//? if fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*///?}
// Two clauses rather than a nested guard: an inactive branch is already a block comment.
//? if !fabric && >=1.21.9 {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?}
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The loader seam for custom packets: where a type is declared, how a receiver is attached, how one is
 * sent. Handlers take a {@link ServerPlayer} because the two loaders' context types share no supertype.
 * NeoForge only registers inside its own event, so these queue for {@link #flush} to replay in order.
 */
public final class Net {
    private Net() {
    }

    //? if !fabric {
    /*private static final List<Consumer<PayloadRegistrar>> PENDING = new ArrayList<>();
    private static final Map<CustomPacketPayload.Type<?>, Consumer<?>> CLIENT_HANDLERS = new HashMap<>();
    *///?}

    /** {@code handler} runs with the player who sent it. */
    public static <T extends CustomPacketPayload> void registerServerbound(
            CustomPacketPayload.Type<T> id,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler) {
        //? if fabric {
        serverboundTypes().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload, context.player()));
        //?} else {
        /*PENDING.add(registrar -> registrar.playToServer(id, codec,
                (payload, context) -> handler.accept(payload, (ServerPlayer) context.player())));
        *///?}
    }

    /** Common init on both sides: the server has to know the type to be able to send it. */
    public static <T extends CustomPacketPayload> void registerClientbound(
            CustomPacketPayload.Type<T> id,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        //? if fabric {
        clientboundTypes().register(id, codec);
        //?} else {
        /*PENDING.add(registrar -> registrar.playToClient(id, codec, (payload, context) -> dispatchToClient(payload)));
        *///?}
    }

    /**
     * Client init only: Fabric's receiver API cannot be touched from common init, while NeoForge wants both
     * halves together and so registers a dispatcher up front that looks the handler up here.
     */
    public static <T extends CustomPacketPayload> void onClientbound(
            CustomPacketPayload.Type<T> id,
            Consumer<T> handler) {
        //? if fabric {
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload));
        //?} else {
        /*CLIENT_HANDLERS.put(id, handler);
        *///?}
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        //? if fabric {
        ServerPlayNetworking.send(player, payload);
        //?} else {
        /*PacketDistributor.sendToPlayer(player, payload);
        *///?}
    }

    /** Client only. NeoForge 21.9 split serverbound out into a client-side {@code ClientPacketDistributor}. */
    public static void sendToServer(CustomPacketPayload payload) {
        //? if fabric {
        ClientPlayNetworking.send(payload);
        //?}
        //? if !fabric && >=1.21.9 {
        /*ClientPacketDistributor.sendToServer(payload);
        *///?}
        //? if !fabric && <1.21.9 {
        /*PacketDistributor.sendToServer(payload);
        *///?}
    }

    //? if fabric {
    // 26.1 renamed Fabric's playC2S/playS2C registries to vanilla's serverbound/clientbound wording.
    // Only the lookup differs, so it is hoisted here instead of guarded at each registration.
    private static PayloadTypeRegistry<RegistryFriendlyByteBuf> serverboundTypes() {
        //? if >=26.1 {
        /*return PayloadTypeRegistry.serverboundPlay();
        *///?} else
        return PayloadTypeRegistry.playC2S();
    }

    private static PayloadTypeRegistry<RegistryFriendlyByteBuf> clientboundTypes() {
        //? if >=26.1 {
        /*return PayloadTypeRegistry.clientboundPlay();
        *///?} else
        return PayloadTypeRegistry.playS2C();
    }
    //?} else {

    /*public static void flush(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        for (Consumer<PayloadRegistrar> pending : PENDING) pending.accept(registrar);
        PENDING.clear();
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void dispatchToClient(T payload) {
        Consumer<T> handler = (Consumer<T>) CLIENT_HANDLERS.get(payload.type());
        if (handler != null) handler.accept(payload);
    }
    *///?}
}
