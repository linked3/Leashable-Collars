package com.dipilodopilasaurus.leashablecollars.leash;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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

import java.util.Optional;
import java.util.UUID;

/**
 * The leash a player is wearing, in the form that survives a relog: the holder is a UUID or knot
 * position, since on a restart it may be unloaded or offline.
 */
public record LeashSaveData(Optional<UUID> holderPlayer, Optional<BlockPos> holderKnot, double leashDistance) {
    /** Namespaced so it cannot collide with vanilla's own keys in the same compound. */
    private static final String ROOT = "playercollars:leash";

    public static final Codec<LeashSaveData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("holder").forGetter(LeashSaveData::holderPlayer),
            BlockPos.CODEC.optionalFieldOf("knot").forGetter(LeashSaveData::holderKnot),
            Codec.DOUBLE.fieldOf("distance").forGetter(LeashSaveData::leashDistance)
    ).apply(instance, LeashSaveData::new));

    public static LeashSaveData ofPlayer(UUID holder, double leashDistance) {
        return new LeashSaveData(Optional.of(holder), Optional.empty(), leashDistance);
    }

    public static LeashSaveData ofKnot(BlockPos knot, double leashDistance) {
        return new LeashSaveData(Optional.empty(), Optional.of(knot), leashDistance);
    }

    /** True when neither holder form is present, i.e. there is nothing worth restoring. */
    public boolean isEmpty() {
        return holderPlayer.isEmpty() && holderKnot.isEmpty();
    }

    // 1.21.6 swapped CompoundTag for ValueInput/ValueOutput; both speak codecs, so only the container differs.
    //? if >=1.21.6 {
    public static void write(ValueOutput output, LeashSaveData data) {
        output.store(ROOT, CODEC, data);
    }

    /** The saved leash, or {@code null} if the player had none. */
    public static LeashSaveData read(ValueInput input) {
        return input.read(ROOT, CODEC).orElse(null);
    }
    //?} else {
    /*public static void write(CompoundTag tag, LeashSaveData data) {
        // Not getOrThrow(): its signature differs before 1.20.5, and a malformed leash is not worth
        // failing a world save over. Dropping the leash is the fallback.
        CODEC.encodeStart(NbtOps.INSTANCE, data).result().ifPresent(encoded -> tag.put(ROOT, encoded));
    }

    public static LeashSaveData read(CompoundTag tag) {
        Tag encoded = tag.get(ROOT);
        return encoded == null ? null : CODEC.parse(NbtOps.INSTANCE, encoded).result().orElse(null);
    }
    *///?}
}
