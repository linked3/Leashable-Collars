package com.dipilodopilasaurus.leashablecollars.network.compat;

// Vanilla's UUIDUtil predates 1.20.5 but has no stream codec, so this shadows it.
//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {
    }

    public static final StreamCodec<FriendlyByteBuf, UUID> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UUID decode(FriendlyByteBuf buffer) {
            return buffer.readUUID();
        }

        @Override
        public void encode(FriendlyByteBuf buffer, UUID value) {
            buffer.writeUUID(value);
        }
    };
}
*///?}
