package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/** A player who can sign deeds. Implemented by ServerPlayerEntityMixin; see SignedDeeds. */
public interface DeedHolder {
    SignedDeeds playercollars$deeds();

    void playercollars$setDeeds(SignedDeeds deeds);

    default void playercollars$signDeed(SignedDeeds.Entry deed) {
        playercollars$setDeeds(playercollars$deeds().with(deed));
    }

    /** The deed {@code owned} signed most recently naming {@code owner}, or null if there is none. */
    static @Nullable SignedDeeds.Entry between(UUID owner, Entity owned) {
        return owned instanceof DeedHolder holder ? holder.playercollars$deeds().from(owner) : null;
    }
}
