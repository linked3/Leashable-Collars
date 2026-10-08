package com.dipilodopilasaurus.leashablecollars.network.compat;

//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/^* See {@link CustomPacketPayload}. Members are added as packets come to need them. ^/
public final class ByteBufCodecs {
    private ByteBufCodecs() {
    }

    public static final StreamCodec<FriendlyByteBuf, Boolean> BOOL = of(FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
    public static final StreamCodec<FriendlyByteBuf, Integer> INT = of(FriendlyByteBuf::readInt, FriendlyByteBuf::writeInt);
    public static final StreamCodec<FriendlyByteBuf, Double> DOUBLE = of(FriendlyByteBuf::readDouble, FriendlyByteBuf::writeDouble);
    public static final StreamCodec<FriendlyByteBuf, String> STRING_UTF8 = of(FriendlyByteBuf::readUtf, FriendlyByteBuf::writeUtf);

    public static <T> StreamCodec<FriendlyByteBuf, List<T>> list(StreamCodec<FriendlyByteBuf, T> element, int maxSize) {
        return of(buffer -> {
            int size = buffer.readVarInt();
            if (size > maxSize) throw new IllegalStateException("list too long: " + size);
            List<T> values = new ArrayList<>(Math.min(size, 1024));
            for (int i = 0; i < size; i++) values.add(element.decode(buffer));
            return values;
        }, (buffer, values) -> {
            buffer.writeVarInt(values.size());
            for (T value : values) element.encode(buffer, value);
        });
    }

    public static <T> StreamCodec<FriendlyByteBuf, T> idMapper(IntFunction<T> fromId, ToIntFunction<T> toId) {
        return of(buffer -> fromId.apply(buffer.readVarInt()), (buffer, value) -> buffer.writeVarInt(toId.applyAsInt(value)));
    }

    private static <T> StreamCodec<FriendlyByteBuf, T> of(Reader<T> reader, Writer<T> writer) {
        return new StreamCodec<>() {
            @Override
            public T decode(FriendlyByteBuf buffer) {
                return reader.read(buffer);
            }

            @Override
            public void encode(FriendlyByteBuf buffer, T value) {
                writer.write(buffer, value);
            }
        };
    }

    @FunctionalInterface
    private interface Reader<T> {
        T read(FriendlyByteBuf buffer);
    }

    @FunctionalInterface
    private interface Writer<T> {
        void write(FriendlyByteBuf buffer, T value);
    }
}
*///?}
