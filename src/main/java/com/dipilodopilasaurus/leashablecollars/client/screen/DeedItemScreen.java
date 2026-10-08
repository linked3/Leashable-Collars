package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.network.PacketStampDeed;

public class DeedItemScreen extends Screen {
    private final OwnerComponent owner;
    private final Component name;
    // Null until the signer picks a side; Stamp stays disabled while it is, so consent is never implied.
    private Boolean canLeashForcibly;
    private Button stamp;

    public DeedItemScreen(ItemStack is, Entity plr) {
        super(is.getHoverName());
        this.owner = Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        this.name = plr.getName();
    }

    @Override
    protected void init() {
        this.addRenderableWidget(GuiCompat.button(forceOptionLabel(), this::cycleForceOption, this.width / 2 - 80, this.height / 2 + 49, 160, 20));
        stamp = this.addRenderableWidget(GuiCompat.button(Text.translatable("item.playercollars.deed_of_ownership.stamp"), this::stampDeed, this.width / 2 - 80, this.height / 2 + 72, 160, 20));
        stamp.active = canLeashForcibly != null;
        this.addRenderableWidget(GuiCompat.button(Text.translatable("gui.cancel"), x -> onClose(), this.width / 2 - 80, this.height / 2 + 95, 160, 20));
    }

    private Component forceOptionLabel() {
        if (canLeashForcibly == null) return Text.translatable("item.playercollars.deed_of_ownership.force_option.choose");
        return Text.translatable(canLeashForcibly
                ? "item.playercollars.deed_of_ownership.force_option.on"
                : "item.playercollars.deed_of_ownership.force_option.off");
    }

    private void cycleForceOption(Button button) {
        canLeashForcibly = canLeashForcibly == null || !canLeashForcibly;
        button.setMessage(forceOptionLabel());
        stamp.active = true;
    }

    // Signature guarded, body shared -- see GuiCompat.
    //? if >=26.1 {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        extractBlurredBackground(context);
        extractMenuBackground(context);
    *///?} elif >=1.21.6 {
    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBlurredBackground(context);
        renderMenuBackground(context);
    //?} elif >=1.21.2 {
    /*@Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBlurredBackground();
        renderMenuBackground(context);
    *///?} elif >=1.20.5 {
    /*@Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBlurredBackground(delta);
        renderMenuBackground(context);
    *///?} elif >=1.20 {
    /*// Neither blur nor the menu background is separable below 1.20.5; the base call draws both.
    @Override
    public void renderBackground(GuiGraphics context) {
        super.renderBackground(context);
    *///?} else {
    /*@Override
    public void renderBackground(PoseStack context) {
        super.renderBackground(context);
    *///?}
        // TODO draw background
    }

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
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership"), this.width / 2, this.height / 2 - 88, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line1", name, owner.name()), this.width / 2, this.height / 2 - 55, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line2"), this.width / 2, this.height / 2 - 38, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line3"), this.width / 2, this.height / 2 - 26, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line4"), this.width / 2, this.height / 2 - 14, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line5"), this.width / 2, this.height / 2 - 2, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line6"), this.width / 2, this.height / 2 + 10, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line7"), this.width / 2, this.height / 2 + 23, -1);
        GuiCompat.centeredText(context, font, Text.translatable("item.playercollars.deed_of_ownership.line8"), this.width / 2, this.height / 2 + 40, -1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void stampDeed(Button btn) {
        if (canLeashForcibly == null) return;
        Net.sendToServer(new PacketStampDeed(canLeashForcibly));
        onClose();
    }
}
