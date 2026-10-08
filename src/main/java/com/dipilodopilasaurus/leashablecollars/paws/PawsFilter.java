package com.dipilodopilasaurus.leashablecollars.paws;

//? if >=1.19.3 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/** One paws allow-list; an absent component keeps pre-configurator behaviour, {@link #everything()} opts out. */
public record PawsFilter<T>(Registry<T> registry, TagKey<T> allMarker, BiPredicate<T, TagKey<T>> inTag) {
    /** Built per call: the marker tag is a {@link PlayerCollarsMod} field, so a static would race its clinit. */
    public static PawsFilter<Block> blocks() {
        return new PawsFilter<>(BuiltInRegistries.BLOCK, PlayerCollarsMod.PAWS_ALL_BLOCKS_MARKER,
                (block, tag) -> block.defaultBlockState().is(tag));
    }

    /** See {@link #blocks()}. */
    public static PawsFilter<Item> items() {
        return new PawsFilter<>(BuiltInRegistries.ITEM, PlayerCollarsMod.PAWS_ALL_ITEMS_MARKER,
                (item, tag) -> new ItemStack(item).is(tag));
    }

    /** Tags are hashed so a stored entry decodes back to the side it was written as. */
    public static <V> Codec<List<Either<TagKey<V>, ResourceKey<V>>>> codec(ResourceKey<? extends Registry<V>> registryKey) {
        return Codec.either(TagKey.hashedCodec(registryKey), ResourceKey.codec(registryKey)).listOf();
    }

    public List<Either<TagKey<T>, ResourceKey<T>>> everything() {
        return List.of(Either.left(allMarker));
    }

    public boolean allowsEverything(@Nullable List<Either<TagKey<T>, ResourceKey<T>>> rules) {
        return rules != null && rules.contains(Either.<TagKey<T>, ResourceKey<T>>left(allMarker));
    }

    public boolean allows(@Nullable List<Either<TagKey<T>, ResourceKey<T>>> rules, T value) {
        if (rules == null) return false;
        if (allowsEverything(rules)) return true;

        ResourceKey<T> key = registry.getResourceKey(value).orElse(null);
        if (key == null) return false;
        for (Either<TagKey<T>, ResourceKey<T>> rule : rules) {
            if (rule.map(tag -> inTag.test(value, tag), key::equals)) return true;
        }
        return false;
    }

    /** Every registry entry the rules resolve to, as ids -- the form the config screen is handed. */
    public List<String> approvedIds(@Nullable List<Either<TagKey<T>, ResourceKey<T>>> rules) {
        if (rules == null || allowsEverything(rules)) return List.of();

        List<String> ids = new ArrayList<>();
        for (T value : registry) {
            if (allows(rules, value)) ids.add(registry.getKey(value).toString());
        }
        return ids;
    }

    /** The inverse of {@link #approvedIds}; unparseable or unknown ids are dropped. */
    public List<Either<TagKey<T>, ResourceKey<T>>> fromIds(List<String> ids) {
        List<Either<TagKey<T>, ResourceKey<T>>> rules = new ArrayList<>(ids.size());
        for (String text : ids) {
            var id = Ids.parse(text);
            if (id == null || !registry.containsKey(id)) continue;
            rules.add(Either.right(ResourceKey.create(registry.key(), id)));
        }
        return rules;
    }
}
