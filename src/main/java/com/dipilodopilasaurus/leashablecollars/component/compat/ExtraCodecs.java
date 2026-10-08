package com.dipilodopilasaurus.leashablecollars.component.compat;

//? if <1.20.5 {
/*import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Optional;

/^* Vanilla grew this in 1.20.5; below it, the same shape so the owner codec reads either way. ^/
public final class ExtraCodecs {
    private ExtraCodecs() {
    }

    public static <T> Codec<Optional<T>> optionalEmptyMap(Codec<T> codec) {
        return new Codec<>() {
            @Override
            public <O> DataResult<Pair<Optional<T>, O>> decode(DynamicOps<O> ops, O input) {
                return isEmptyMap(ops, input)
                        ? DataResult.success(Pair.of(Optional.empty(), input))
                        : codec.decode(ops, input).map(pair -> pair.mapFirst(Optional::of));
            }

            @Override
            public <O> DataResult<O> encode(Optional<T> value, DynamicOps<O> ops, O prefix) {
                return value.isEmpty() ? DataResult.success(ops.emptyMap()) : codec.encode(value.get(), ops, prefix);
            }
        };
    }

    private static <O> boolean isEmptyMap(DynamicOps<O> ops, O input) {
        return ops.getMap(input).result().map(map -> map.entries().findAny().isEmpty()).orElse(false);
    }
}
*///?}
