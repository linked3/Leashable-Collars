package com.dipilodopilasaurus.leashablecollars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if >=1.19 {
import net.minecraft.core.UUIDUtil;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.UUIDUtil;
*///?}
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
*///?}
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Every deed a player signed as the one being owned, kept on that player so consent outlives the
 * collar it was crafted into. Signing again for the same owner replaces the old entry.
 */
public record SignedDeeds(List<Entry> entries) {
    /** Namespaced so it cannot collide with vanilla's own keys in the same compound. */
    private static final String ROOT = "playercollars:deeds";

    public static final SignedDeeds EMPTY = new SignedDeeds(List.of());

    public record Entry(UUID owner, String ownerName, boolean canLeashForcibly) {
    }

    private static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(Entry::owner),
            Codec.STRING.fieldOf("owner_name").forGetter(Entry::ownerName),
            Codec.BOOL.fieldOf("can_leash_forcibly").forGetter(Entry::canLeashForcibly)
    ).apply(instance, Entry::new));

    public static final Codec<SignedDeeds> CODEC =
            ENTRY_CODEC.listOf().xmap(SignedDeeds::new, SignedDeeds::entries);

    public @Nullable Entry from(UUID owner) {
        for (Entry entry : entries) {
            if (entry.owner().equals(owner)) return entry;
        }
        return null;
    }

    public SignedDeeds with(Entry signed) {
        List<Entry> next = new ArrayList<>(entries.size() + 1);
        for (Entry entry : entries) {
            if (!entry.owner().equals(signed.owner())) next.add(entry);
        }
        next.add(signed);
        return new SignedDeeds(List.copyOf(next));
    }

    // 1.21.6 swapped CompoundTag for the ValueInput/ValueOutput pair. See LeashSaveData.
    //? if >=1.21.6 {
    public static void write(ValueOutput output, SignedDeeds deeds) {
        output.store(ROOT, CODEC, deeds);
    }

    public static SignedDeeds read(ValueInput input) {
        return input.read(ROOT, CODEC).orElse(EMPTY);
    }
    //?} else {
    /*public static void write(CompoundTag tag, SignedDeeds deeds) {
        CODEC.encodeStart(NbtOps.INSTANCE, deeds).result().ifPresent(encoded -> tag.put(ROOT, encoded));
    }

    public static SignedDeeds read(CompoundTag tag) {
        Tag encoded = tag.get(ROOT);
        return encoded == null ? EMPTY : CODEC.parse(NbtOps.INSTANCE, encoded).result().orElse(EMPTY);
    }
    *///?}
}
