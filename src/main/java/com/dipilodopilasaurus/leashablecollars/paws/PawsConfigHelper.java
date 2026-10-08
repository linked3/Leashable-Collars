package com.dipilodopilasaurus.leashablecollars.paws;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

/** Server side of the paws configurator: the ownership gate, and the paws stacks the screens edit. */
public final class PawsConfigHelper {
    public static final int BLOCKS = 0;
    public static final int ITEMS = 1;
    public static final int ATTACK = 2;

    private PawsConfigHelper() {
    }

    public static boolean isSection(int section) {
        return section >= BLOCKS && section <= ATTACK;
    }

    /** Empty when the request is refused; the owner has already been told why. */
    public static List<EquippedAccessories.EquippedEntry> validate(ServerPlayer owner, UUID petId) {
        ServerPlayer pet = PetControlHelper.findOnlinePlayer(owner, petId);
        PetControlHelper.ValidationResult result = PetControlHelper.validateOwnerControl(owner, pet);
        if (!result.successful()) {
            if (result.failure() != null) Compat.sendOverlayMessage(owner, result.failure().message());
            return List.of();
        }

        List<EquippedAccessories.EquippedEntry> paws = EquippedAccessories.getEquippedStacks(
                result.activeCollar().pet(), stack -> stack.is(PlayerCollarsMod.PAWS_TAG));
        if (paws.isEmpty()) {
            Compat.sendOverlayMessage(owner, Text.translatable("item.playercollars.paw_configurator.no_paws")
                    .withStyle(ChatFormatting.RED));
        }
        return paws;
    }

    /** Both ends of the change see it -- the owner set it, and the pet is entitled to know. */
    public static void announce(ServerPlayer owner, UUID petId) {
        Component message = Text.translatable("item.playercollars.paw_configurator.success");
        Compat.sendOverlayMessage(owner, message);
        ServerPlayer pet = PetControlHelper.findOnlinePlayer(owner, petId);
        if (pet != null && pet != owner) Compat.sendOverlayMessage(pet, message);
    }
}
