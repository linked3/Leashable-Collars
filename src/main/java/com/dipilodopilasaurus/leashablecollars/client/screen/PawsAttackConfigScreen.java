package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.Text;

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
import com.dipilodopilasaurus.leashablecollars.network.Net;
import com.dipilodopilasaurus.leashablecollars.network.PacketPawsConfig;
import com.dipilodopilasaurus.leashablecollars.network.PacketSavePawsConfig;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;

import java.util.List;
import java.util.UUID;

/** Whether the paws let their wearer swing at mobs, at players, or at neither. */
public class PawsAttackConfigScreen extends Screen {
    private final UUID petId;
    private final Screen parent;
    private boolean attackMobs;
    private boolean attackPlayers;
    private Button mobsButton;
    private Button playersButton;
    private int top;

    public PawsAttackConfigScreen(PacketPawsConfig payload, String petName, Screen parent) {
        super(Text.translatable("gui.playercollars.paw_configurator.attack.title", petName));
        this.petId = payload.petId();
        this.parent = parent;
        this.attackMobs = payload.attackMobs();
        this.attackPlayers = payload.attackPlayers();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 90;
        top = this.height / 2 - 40;

        mobsButton = addRenderableWidget(GuiCompat.button(Text.empty(), button -> {
            attackMobs = !attackMobs;
            refresh();
        }, x, top, 180, 20));
        playersButton = addRenderableWidget(GuiCompat.button(Text.empty(), button -> {
            attackPlayers = !attackPlayers;
            refresh();
        }, x, top + 24, 180, 20));

        addRenderableWidget(GuiCompat.button(Text.translatable("gui.done"), button -> save(), x, top + 56, 88, 20));
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.cancel"), button -> onClose(), x + 92, top + 56, 88, 20));
        refresh();
    }

    private void refresh() {
        mobsButton.setMessage(Text.translatable("gui.playercollars.paw_configurator.attack.mobs", toggleText(attackMobs)));
        playersButton.setMessage(Text.translatable("gui.playercollars.paw_configurator.attack.players", toggleText(attackPlayers)));
    }

    private static Component toggleText(boolean enabled) {
        return Text.translatable(enabled ? "options.on" : "options.off");
    }

    private void save() {
        Net.sendToServer(new PacketSavePawsConfig(petId, PawsConfigHelper.ATTACK, false, List.of(), attackMobs, attackPlayers));
        onClose();
    }

    @Override
    public void onClose() {
        GuiCompat.setScreen(minecraft, parent);
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
        GuiCompat.centeredText(context, font, title, this.width / 2, top - 20, -1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
