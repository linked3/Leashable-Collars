package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.component.Components;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public final class PetControlHelper {
    public static final double MAX_CONTROL_DISTANCE = 16.0D;
    private static final double MAX_CONTROL_DISTANCE_SQUARED = MAX_CONTROL_DISTANCE * MAX_CONTROL_DISTANCE;

    private PetControlHelper() {
    }

    public record ActiveCollar(ServerPlayer pet, ItemStack collar, OwnerComponent owner) {
    }

    public enum ValidationFailure {
        PET_UNAVAILABLE("message.playercollars.pet_control.error.pet_unavailable"),
        TOO_FAR("message.playercollars.pet_control.error.too_far"),
        NOT_OWNER("message.playercollars.pet_control.error.not_owner");

        private final String translationKey;

        ValidationFailure(String translationKey) {
            this.translationKey = translationKey;
        }

        public Component message() {
            return Text.translatable(translationKey).withStyle(ChatFormatting.RED);
        }
    }

    public record ValidationResult(@Nullable ActiveCollar activeCollar, @Nullable ValidationFailure failure) {
        public static ValidationResult success(ActiveCollar activeCollar) {
            return new ValidationResult(activeCollar, null);
        }

        public static ValidationResult fail(ValidationFailure failure) {
            return new ValidationResult(null, failure);
        }

        public boolean successful() {
            return activeCollar != null;
        }
    }

    public static ValidationResult validateOwnerControl(ServerPlayer owner, @Nullable ServerPlayer pet) {
        if (pet == null || !pet.isAlive() || pet.isRemoved()) {
            return ValidationResult.fail(ValidationFailure.PET_UNAVAILABLE);
        }
        if (Compat.level(owner) != Compat.level(pet)) {
            return ValidationResult.fail(ValidationFailure.PET_UNAVAILABLE);
        }
        if (owner.distanceToSqr(pet) > MAX_CONTROL_DISTANCE_SQUARED) {
            return ValidationResult.fail(ValidationFailure.TOO_FAR);
        }

        ItemStack collar = EquippedAccessories.findOwned(pet, stack -> stack.is(PlayerCollarsMod.COLLAR_TAG), owner.getUUID(), pet.getUUID());
        if (collar == null) {
            return ValidationResult.fail(ValidationFailure.NOT_OWNER);
        }

        OwnerComponent ownerComponent = Components.get(collar, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (ownerComponent == null || !ownerComponent.uuid().equals(owner.getUUID()) || !isAssignedToWearer(ownerComponent, pet.getUUID())) {
            return ValidationResult.fail(ValidationFailure.NOT_OWNER);
        }

        return ValidationResult.success(new ActiveCollar(pet, collar, ownerComponent));
    }

    @Nullable
    public static ItemStack findControlledCollar(LivingEntity pet) {
        return EquippedAccessories.firstEquipped(pet, stack -> {
            if (!stack.is(PlayerCollarsMod.COLLAR_TAG)) return false;
            OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            return owner != null && isAssignedToWearer(owner, pet.getUUID());
        });
    }

    public static PetControlOptions getOptions(LivingEntity pet) {
        return PetControlOptions.fromCollar(findControlledCollar(pet));
    }

    public static boolean isSpeechRestricted(ServerPlayer pet) {
        return getOptions(pet).speechMode() != SpeechMode.ALLOWED;
    }

    public static boolean handleBlockedSpeech(ServerPlayer pet) {
        ItemStack collar = findControlledCollar(pet);
        if (collar == null) return false;

        SpeechMode speechMode = Components.getOrDefault(collar, PlayerCollarsMod.SPEECH_MODE_COMPONENT_TYPE, SpeechMode.ALLOWED);
        if (speechMode == SpeechMode.ALLOWED) return false;

        if (speechMode == SpeechMode.MUFFLED) {
            notifyOnlineOwnerOfMuffledSpeech(pet, collar);
            Compat.sendOverlayMessage(pet, Text.translatable("message.playercollars.pet_control.speech.muffled.self")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            Compat.sendOverlayMessage(pet, Text.translatable("message.playercollars.pet_control.speech.silenced.self")
                    .withStyle(ChatFormatting.RED));
        }
        return true;
    }

    public static boolean handleBlockedCommand(ServerPlayer pet) {
        if (!getOptions(pet).commandsBlocked()) return false;
        Compat.sendOverlayMessage(pet, Text.translatable("message.playercollars.pet_control.commands.blocked")
                .withStyle(ChatFormatting.RED));
        return true;
    }

    public static boolean isVisionObscured(ServerPlayer pet) {
        return getOptions(pet).visionObscured();
    }

    public static boolean isMovementRestrained(ServerPlayer pet) {
        return getOptions(pet).movementRestrained();
    }

    public static boolean isMovementRestrainedClient(LivingEntity pet) {
        return EquippedAccessories.firstEquipped(pet, stack ->
                stack.is(PlayerCollarsMod.COLLAR_TAG)
                        && Components.getOrDefault(stack, PlayerCollarsMod.MOVEMENT_RESTRAINED_COMPONENT_TYPE, false)) != null;
    }

    public static ServerPlayer findOnlinePlayer(ServerPlayer requester, UUID target) {
        return Compat.level(requester).getServer().getPlayerList().getPlayer(target);
    }

    private static void notifyOnlineOwnerOfMuffledSpeech(ServerPlayer pet, ItemStack collar) {
        OwnerComponent owner = Components.get(collar, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner == null) return;

        ServerPlayer ownerPlayer = Compat.level(pet).getServer().getPlayerList().getPlayer(owner.uuid());
        if (ownerPlayer == null) return;

        Compat.sendOverlayMessage(ownerPlayer, Text.translatable(
                "message.playercollars.pet_control.speech.muffled.owner",
                pet.getDisplayName()
        ).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static boolean isAssignedToWearer(OwnerComponent owner, UUID wearer) {
        Optional<UUID> owned = owner.owned();
        return owned.isEmpty() || owned.get().equals(wearer);
    }
}
