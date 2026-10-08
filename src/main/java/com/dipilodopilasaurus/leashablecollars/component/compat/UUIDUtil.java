package com.dipilodopilasaurus.leashablecollars.component.compat;

//? if <1.19 {
/*import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.core.SerializableUUID;

import java.util.UUID;
import java.util.function.Function;

public final class UUIDUtil {
    public static final Codec<UUID> CODEC = SerializableUUID.CODEC;
    public static final Codec<UUID> AUTHLIB_CODEC = Codec.either(CODEC,
            Codec.STRING.comapFlatMap(UUIDUtil::parse, UUID::toString))
            .xmap(value -> value.map(Function.identity(), Function.identity()), Either::left);

    private UUIDUtil() {
    }

    private static DataResult<UUID> parse(String value) {
        try {
            return DataResult.success(UUIDTypeAdapter.fromString(value), Lifecycle.stable());
        } catch (IllegalArgumentException exception) {
            return DataResult.error("Invalid UUID '" + value + "': " + exception.getMessage());
        }
    }
}
*///?}
