package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.network.Net;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.PetControlOptions;
import com.dipilodopilasaurus.leashablecollars.network.PacketOpenPetControl;
import com.dipilodopilasaurus.leashablecollars.network.PacketTogglePetControl;

import java.util.UUID;

public class PetControlScreen extends Screen {
    private final UUID petId;
    private String petName;
    private PetControlOptions options;
    private Component titleText;
    private Button speechButton;
    private Button commandsButton;
    private Button visionButton;
    private Button movementButton;

    public PetControlScreen(PacketOpenPetControl payload) {
        super(Text.translatable("gui.playercollars.pet_control.title", payload.petName()));
        this.petId = payload.petId();
        this.petName = payload.petName();
        this.options = payload.options();
        this.titleText = Text.translatable("gui.playercollars.pet_control.title", petName);
    }

    public boolean isFor(UUID petId) {
        return this.petId.equals(petId);
    }

    public void update(PacketOpenPetControl payload) {
        this.petName = payload.petName();
        this.options = payload.options();
        this.titleText = Text.translatable("gui.playercollars.pet_control.title", petName);
        refreshButtons();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 90;
        int y = this.height / 2 - 48;

        speechButton = addRenderableWidget(GuiCompat.button(Text.empty(), button ->
                Net.sendToServer(new PacketTogglePetControl(petId, PacketTogglePetControl.Control.SPEECH)),
                x, y, 180, 20));
        commandsButton = addRenderableWidget(GuiCompat.button(Text.empty(), button ->
                Net.sendToServer(new PacketTogglePetControl(petId, PacketTogglePetControl.Control.COMMANDS)),
                x, y + 24, 180, 20));
        visionButton = addRenderableWidget(GuiCompat.button(Text.empty(), button ->
                Net.sendToServer(new PacketTogglePetControl(petId, PacketTogglePetControl.Control.VISION)),
                x, y + 48, 180, 20));
        movementButton = addRenderableWidget(GuiCompat.button(Text.empty(), button ->
                Net.sendToServer(new PacketTogglePetControl(petId, PacketTogglePetControl.Control.MOVEMENT)),
                x, y + 72, 180, 20));

        addRenderableWidget(GuiCompat.button(Text.translatable("gui.playercollars.paw_configurator.open"), button ->
                GuiCompat.setScreen(minecraft, new PawsSelectScreen(petId, petName, this)),
                x, y + 100, 180, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.done"), button -> onClose(), x + 40, y + 128, 100, 20));
        refreshButtons();
    }

    private void refreshButtons() {
        if (speechButton == null) return;

        speechButton.setMessage(Text.translatable(switch (options.speechMode()) {
            case ALLOWED -> "gui.playercollars.pet_control.speech.allowed";
            case MUFFLED -> "gui.playercollars.pet_control.speech.muffled";
            case SILENCED -> "gui.playercollars.pet_control.speech.silenced";
        }));
        commandsButton.setMessage(Text.translatable(options.commandsBlocked()
                ? "gui.playercollars.pet_control.commands.blocked"
                : "gui.playercollars.pet_control.commands.allowed"));
        visionButton.setMessage(Text.translatable(options.visionObscured()
                ? "gui.playercollars.pet_control.vision.obscured"
                : "gui.playercollars.pet_control.vision.normal"));
        movementButton.setMessage(Text.translatable(options.movementRestrained()
                ? "gui.playercollars.pet_control.movement.restrained"
                : "gui.playercollars.pet_control.movement.allowed"));
    }

    // Signature guarded, body shared -- see GuiCompat.
    //? if >=26.1 {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
    *///?} elif >=1.20 {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    //?} else {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    *///?}
        GuiCompat.centeredText(context, font, titleText, this.width / 2, this.height / 2 - 78, -1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
