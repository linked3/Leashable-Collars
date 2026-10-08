package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.PacketOpenPawsConfig;
import com.dipilodopilasaurus.leashablecollars.LeashableCollars;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class PawsSelectScreen extends Screen {
    private final UUID targetUuid;

    public PawsSelectScreen(UUID targetUuid, String targetName) {
        super(new net.minecraft.network.chat.TranslatableComponent("gui.playercollars.paw_configurator.title", targetName));
        this.targetUuid = targetUuid;
    }

    @Override
    protected void init() {
        int x = this.width / 2;
        int y = this.height / 2;
        addRenderableWidget(new Button(x - 80, y, 160, 20, new net.minecraft.network.chat.TranslatableComponent("gui.playercollars.paw_configurator.block.open"), button -> {
            LeashableCollars.NETWORK.sendToServer(new PacketOpenPawsConfig(targetUuid, false));
            onClose();
        }));
        addRenderableWidget(new Button(x - 80, y + 22, 160, 20, new net.minecraft.network.chat.TranslatableComponent("gui.playercollars.paw_configurator.item.open"), button -> {
            LeashableCollars.NETWORK.sendToServer(new PacketOpenPawsConfig(targetUuid, true));
            onClose();
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
