package com.dipilodopilasaurus.leashablecollars.component.compat;

//? if <1.20.5 {
/*import com.mojang.serialization.Codec;

/^* Pre-1.20.5 stand-in: a codec plus the stack-tag path the value is persisted at. See Components. ^/
public record DataComponentType<T>(Codec<T> codec, String... path) {
    public static <T> DataComponentType<T> at(Codec<T> codec, String... path) {
        return new DataComponentType<>(codec, path);
    }
}
*///?}
