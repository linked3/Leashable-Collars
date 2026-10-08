package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.component.Components;

import net.minecraft.world.item.ItemStack;

public record PetControlOptions(
        SpeechMode speechMode,
        boolean commandsBlocked,
        boolean visionObscured,
        boolean movementRestrained
) {
    public static final PetControlOptions DEFAULT = new PetControlOptions(SpeechMode.ALLOWED, false, false, false);

    public static PetControlOptions fromCollar(ItemStack collar) {
        if (collar == null || collar.isEmpty()) return DEFAULT;
        return new PetControlOptions(
                Components.getOrDefault(collar, PlayerCollarsMod.SPEECH_MODE_COMPONENT_TYPE, SpeechMode.ALLOWED),
                Components.getOrDefault(collar, PlayerCollarsMod.COMMANDS_BLOCKED_COMPONENT_TYPE, false),
                Components.getOrDefault(collar, PlayerCollarsMod.VISION_OBSCURED_COMPONENT_TYPE, false),
                Components.getOrDefault(collar, PlayerCollarsMod.MOVEMENT_RESTRAINED_COMPONENT_TYPE, false)
        );
    }
}
