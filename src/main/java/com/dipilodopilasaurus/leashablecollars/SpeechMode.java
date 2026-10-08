package com.dipilodopilasaurus.leashablecollars;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SpeechMode implements StringRepresentable {
    ALLOWED("allowed"),
    MUFFLED("muffled"),
    SILENCED("silenced");

    //? if >=1.19 {
    public static final Codec<SpeechMode> CODEC = StringRepresentable.fromEnum(SpeechMode::values);
    //?} else {
    /*public static final Codec<SpeechMode> CODEC = StringRepresentable.fromEnum(SpeechMode::values, name -> {
        for (SpeechMode mode : values()) {
            if (mode.serializedName.equals(name)) return mode;
        }
        return null;
    });
    *///?}

    private final String serializedName;

    SpeechMode(String serializedName) {
        this.serializedName = serializedName;
    }

    public SpeechMode next() {
        return switch (this) {
            case ALLOWED -> MUFFLED;
            case MUFFLED -> SILENCED;
            case SILENCED -> ALLOWED;
        };
    }

    public static SpeechMode byId(int id) {
        SpeechMode[] values = values();
        return id >= 0 && id < values.length ? values[id] : ALLOWED;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
