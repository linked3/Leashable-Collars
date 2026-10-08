package com.dipilodopilasaurus.leashablecollars.network;

//? if fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?}
//? if fabric && >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
//? if fabric && <1.20.5 {
/*import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
*///?}
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
*///?}
//? if forge {
/*import com.dipilodopilasaurus.leashablecollars.Ids;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;
*///?}
//? if !fabric {
/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*///?}
// Two clauses rather than a nested guard: an inactive branch is already a block comment.
//? if !fabric && >=1.21.7 {
/*import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?}
//? if >=1.20.5 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.network.compat.CustomPacketPayload;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
*///?}
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Loader seam for custom packets: where a type is declared, how a receiver attaches, how one is sent.
 * NeoForge only registers inside its own event, so these queue for {@link #flush} to replay in order.
 */
public final class Net {
    private Net() {
    }

    //? if neoforge {
    /*private static final List<Consumer<PayloadRegistrar>> PENDING = new ArrayList<>();
    *///?}
    //? if forge {
    /*// SimpleChannel dispatches by message class, so every payload travels as one Envelope and this
    // class does the discriminating itself, on an index assigned in registration order.
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Ids.of("net"), () -> "1", "1"::equals, "1"::equals);
    private static final List<StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload>> CODECS = new ArrayList<>();
    private static final List<CustomPacketPayload.Type<?>> TYPES = new ArrayList<>();
    private static final Map<Integer, BiConsumer<CustomPacketPayload, ServerPlayer>> SERVER_HANDLERS = new HashMap<>();

    static {
        CHANNEL.registerMessage(0, Envelope.class, Net::write, Net::read, Net::dispatch);
    }
    *///?}
    //? if !fabric {
    /*private static final Map<CustomPacketPayload.Type<?>, Consumer<?>> CLIENT_HANDLERS = new HashMap<>();
    *///?}
    //? if fabric && <1.20.5 {
    /*// Fabric's 1.20.1 channels carry a raw buffer, so the codec a payload was registered with has to
    // be found again at send and receive time.
    private static final Map<CustomPacketPayload.Type<?>, StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload>> CODECS = new HashMap<>();
    *///?}

    /** {@code handler} runs with the player who sent it. */
    public static <T extends CustomPacketPayload> void registerServerbound(
            CustomPacketPayload.Type<T> id,
            StreamCodec<RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, ServerPlayer> handler) {
        //? if fabric && >=1.20.5 {
        serverboundTypes().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload, context.player()));
        //?}
        //? if fabric && <1.20.5 {
        /*remember(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id.id(), (server, player, listener, buffer, responseSender) -> {
            // Decode here, not on the main thread: the buffer is released as soon as this returns.
            T payload = codec.decode(new RegistryFriendlyByteBuf(buffer));
            server.execute(() -> handler.accept(payload, player));
        });
        *///?}
        //? if neoforge {
        /*PENDING.add(registrar -> registrar.playToServer(id, codec,
                (payload, context) -> handler.accept(payload, (ServerPlayer) context.player())));
        *///?}
        //? if forge {
        /*@SuppressWarnings("unchecked")
        BiConsumer<CustomPacketPayload, ServerPlayer> erased = (BiConsumer<CustomPacketPayload, ServerPlayer>) handler;
        SERVER_HANDLERS.put(assignIndex(id, codec), erased);
        *///?}
    }

    /** Common init on both sides: the server has to know the type to be able to send it. */
    public static <T extends CustomPacketPayload> void registerClientbound(
            CustomPacketPayload.Type<T> id,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        //? if fabric && >=1.20.5 {
        clientboundTypes().register(id, codec);
        //?}
        //? if fabric && <1.20.5 {
        /*remember(id, codec);
        *///?}
        //? if neoforge {
        /*PENDING.add(registrar -> registrar.playToClient(id, codec, (payload, context) -> dispatchToClient(payload)));
        *///?}
        //? if forge {
        /*assignIndex(id, codec);
        *///?}
    }

    /** Client init only: Fabric's receiver API cannot be touched from common init, NeoForge wants both halves. */
    public static <T extends CustomPacketPayload> void onClientbound(
            CustomPacketPayload.Type<T> id,
            Consumer<T> handler) {
        //? if fabric && >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload));
        //?}
        //? if fabric && <1.20.5 {
        /*ClientPlayNetworking.registerGlobalReceiver(id.id(), (client, listener, buffer, responseSender) -> {
            T payload = decode(id, buffer);
            client.execute(() -> handler.accept(payload));
        });
        *///?}
        //? if !fabric {
        /*CLIENT_HANDLERS.put(id, handler);
        *///?}
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        //? if fabric && >=1.20.5 {
        ServerPlayNetworking.send(player, payload);
        //?}
        //? if fabric && <1.20.5 {
        /*ServerPlayNetworking.send(player, payload.type().id(), encode(payload));
        *///?}
        //? if neoforge {
        /*PacketDistributor.sendToPlayer(player, payload);
        *///?}
        //? if forge {
        /*CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), envelope(payload));
        *///?}
    }

    /** Client only. NeoForge 21.7 split serverbound out into a client-side {@code ClientPacketDistributor}. */
    public static void sendToServer(CustomPacketPayload payload) {
        //? if fabric && >=1.20.5 {
        ClientPlayNetworking.send(payload);
        //?}
        //? if fabric && <1.20.5 {
        /*ClientPlayNetworking.send(payload.type().id(), encode(payload));
        *///?}
        //? if !fabric && >=1.21.7 {
        /*ClientPacketDistributor.sendToServer(payload);
        *///?}
        //? if neoforge && <1.21.7 {
        /*PacketDistributor.sendToServer(payload);
        *///?}
        //? if forge {
        /*CHANNEL.sendToServer(envelope(payload));
        *///?}
    }

    //? if fabric && >=1.20.5 {
    // 26.1 renamed Fabric's playC2S/playS2C to serverbound/clientbound, so the lookup is hoisted here.
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
    //?}

    //? if fabric && <1.20.5 {
    /*private static <T extends CustomPacketPayload> void remember(
            CustomPacketPayload.Type<T> id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload> erased =
                (StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload>) codec;
        CODECS.put(id, erased);
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> T decode(CustomPacketPayload.Type<T> id, FriendlyByteBuf buffer) {
        return (T) CODECS.get(id).decode(new RegistryFriendlyByteBuf(buffer));
    }

    private static FriendlyByteBuf encode(CustomPacketPayload payload) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer());
        CODECS.get(payload.type()).encode(buffer, payload);
        return buffer;
    }
    *///?}

    //? if neoforge {
    /*public static void flush(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        for (Consumer<PayloadRegistrar> pending : PENDING) pending.accept(registrar);
        PENDING.clear();
    }
    *///?}

    //? if forge {
    /*private record Envelope(int index, CustomPacketPayload payload) {
    }

    // Both sides walk the same registration calls in the same order, so the index agrees without
    // being written down anywhere.
    @SuppressWarnings("unchecked")
    private static int assignIndex(CustomPacketPayload.Type<?> id, StreamCodec<RegistryFriendlyByteBuf, ?> codec) {
        int index = TYPES.indexOf(id);
        if (index >= 0) return index;
        TYPES.add(id);
        CODECS.add((StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload>) codec);
        return TYPES.size() - 1;
    }

    private static Envelope envelope(CustomPacketPayload payload) {
        int index = TYPES.indexOf(payload.type());
        if (index < 0) throw new IllegalArgumentException("unregistered payload " + payload.type().id());
        return new Envelope(index, payload);
    }

    private static void write(Envelope envelope, FriendlyByteBuf buffer) {
        buffer.writeVarInt(envelope.index());
        CODECS.get(envelope.index()).encode(new RegistryFriendlyByteBuf(buffer), envelope.payload());
    }

    private static Envelope read(FriendlyByteBuf buffer) {
        int index = buffer.readVarInt();
        return new Envelope(index, CODECS.get(index).decode(new RegistryFriendlyByteBuf(buffer)));
    }

    private static void dispatch(Envelope envelope, Supplier<NetworkEvent.Context> source) {
        NetworkEvent.Context context = source.get();
        BiConsumer<CustomPacketPayload, ServerPlayer> serverHandler = SERVER_HANDLERS.get(envelope.index());
        if (serverHandler != null && context.getSender() != null) {
            context.enqueueWork(() -> serverHandler.accept(envelope.payload(), context.getSender()));
        } else {
            context.enqueueWork(() -> dispatchToClient(envelope.payload()));
        }
        context.setPacketHandled(true);
    }
    *///?}

    //? if !fabric {
    /*@SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void dispatchToClient(T payload) {
        Consumer<T> handler = (Consumer<T>) CLIENT_HANDLERS.get(payload.type());
        if (handler != null) handler.accept(payload);
    }
    *///?}
}
