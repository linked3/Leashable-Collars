package com.dipilodopilasaurus.leashablecollars.network;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

//? if >=1.20.5 {
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.DataComponentType;
import com.dipilodopilasaurus.leashablecollars.network.compat.ByteBufCodecs;
import com.dipilodopilasaurus.leashablecollars.network.compat.CustomPacketPayload;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
import com.dipilodopilasaurus.leashablecollars.network.compat.UUIDUtil;
*///?}
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.SpeechMode;

import java.util.UUID;

public record PacketTogglePetControl(UUID petId, Control control) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketTogglePetControl> ID =
            new CustomPacketPayload.Type<>(Ids.of("toggle_pet_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketTogglePetControl> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketTogglePetControl::petId,
            ByteBufCodecs.idMapper(Control::byId, Control::networkId), PacketTogglePetControl::control,
            PacketTogglePetControl::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayer player) {
        Compat.level(player).getServer().execute(() -> {
            ServerPlayer owner = player;
            ServerPlayer pet = PetControlHelper.findOnlinePlayer(owner, petId);
            PetControlHelper.ValidationResult result = PetControlHelper.validateOwnerControl(owner, pet);
            if (!result.successful()) {
                if (result.failure() != null) Compat.sendOverlayMessage(owner, result.failure().message());
                return;
            }

            if (control == Control.UNKNOWN) {
                Compat.sendOverlayMessage(owner, Text.translatable("message.playercollars.pet_control.error.invalid_request")
                        .withStyle(ChatFormatting.RED));
                return;
            }

            ItemStack collar = result.activeCollar().collar();
            switch (control) {
                case SPEECH -> {
                    SpeechMode next = Components.getOrDefault(collar, PlayerCollarsMod.SPEECH_MODE_COMPONENT_TYPE, SpeechMode.ALLOWED).next();
                    if (next == SpeechMode.ALLOWED) {
                        Components.remove(collar, PlayerCollarsMod.SPEECH_MODE_COMPONENT_TYPE);
                    } else {
                        Components.set(collar, PlayerCollarsMod.SPEECH_MODE_COMPONENT_TYPE, next);
                    }
                }
                case COMMANDS -> toggleBoolean(collar, PlayerCollarsMod.COMMANDS_BLOCKED_COMPONENT_TYPE);
                case VISION -> toggleBoolean(collar, PlayerCollarsMod.VISION_OBSCURED_COMPONENT_TYPE);
                case MOVEMENT -> toggleBoolean(collar, PlayerCollarsMod.MOVEMENT_RESTRAINED_COMPONENT_TYPE);
                case UNKNOWN -> {
                }
            }
            EquippedAccessories.markChanged(result.activeCollar().pet(), collar);

            Net.sendToClient(owner, PacketOpenPetControl.from(result.activeCollar().pet(), collar));
        });
    }

    private static void toggleBoolean(ItemStack collar, DataComponentType<Boolean> componentType) {
        boolean next = !Components.getOrDefault(collar, componentType, false);
        if (next) {
            Components.set(collar, componentType, true);
        } else {
            Components.remove(collar, componentType);
        }
    }

    public enum Control {
        SPEECH,
        COMMANDS,
        VISION,
        MOVEMENT,
        UNKNOWN;

        public static Control byId(int id) {
            Control[] values = values();
            return id >= 0 && id < MOVEMENT.ordinal() + 1 ? values[id] : UNKNOWN;
        }

        public int networkId() {
            return this == UNKNOWN ? -1 : ordinal();
        }
    }
}
