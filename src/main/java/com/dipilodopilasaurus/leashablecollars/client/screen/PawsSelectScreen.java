package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.Text;

//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
import net.minecraft.client.gui.screens.Screen;
import com.dipilodopilasaurus.leashablecollars.network.Net;
import com.dipilodopilasaurus.leashablecollars.network.PacketOpenPawsConfig;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;

import java.util.UUID;

/** Section picker for the paws configurator. Each button asks the server to open one section. */
public class PawsSelectScreen extends Screen {
    private final UUID petId;
    private final String petName;
    private final Screen parent;

    public PawsSelectScreen(UUID petId, String petName, Screen parent) {
        super(Text.translatable("gui.playercollars.paw_configurator.title", petName));
        this.petId = petId;
        this.petName = petName;
        this.parent = parent;
    }

    public String petName() {
        return petName;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 80;
        int y = this.height / 2 - 40;

        addSection("gui.playercollars.paw_configurator.block.open", x, y, PawsConfigHelper.BLOCKS);
        addSection("gui.playercollars.paw_configurator.item.open", x, y + 24, PawsConfigHelper.ITEMS);
        addSection("gui.playercollars.paw_configurator.attack.open", x, y + 48, PawsConfigHelper.ATTACK);
        addRenderableWidget(GuiCompat.button(Text.translatable("gui.back"), button -> onClose(), x, y + 80, 160, 20));
    }

    private void addSection(String key, int x, int y, int section) {
        addRenderableWidget(GuiCompat.button(Text.translatable(key), button ->
                Net.sendToServer(new PacketOpenPawsConfig(petId, section)),
                x, y, 160, 20));
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
        GuiCompat.centeredText(context, font, title, this.width / 2, this.height / 2 - 60, -1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
