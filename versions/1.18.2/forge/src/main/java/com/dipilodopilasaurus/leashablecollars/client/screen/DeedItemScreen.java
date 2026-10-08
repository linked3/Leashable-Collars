package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.OwnerData;
import com.dipilodopilasaurus.leashablecollars.PacketStampDeed;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import static com.dipilodopilasaurus.leashablecollars.LeashableCollars.NETWORK;

public class DeedItemScreen extends Screen {
    private final OwnerData owner;
    private final Component name;

    public DeedItemScreen(ItemStack stack, Entity player) {
        super(stack.getHoverName());
        this.owner = com.dipilodopilasaurus.leashablecollars.item.CollarItem.getOwnerData(stack);
        this.name = player.getName();
    }

    @Override
    protected void init() {
        addRenderableWidget(new Button(this.width / 2 - 80, this.height / 2 + 72, 160, 20,
                new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.stamp"), button -> stampDeed()));
        addRenderableWidget(new Button(this.width / 2 - 80, this.height / 2 + 95, 160, 20,
                new net.minecraft.network.chat.TranslatableComponent("gui.cancel"), button -> onClose()));
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        fill(poseStack, 0, 0, this.width, this.height, 0xA0101010);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership"), this.width / 2, this.height / 2 - 88, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line1", name, owner.name()), this.width / 2, this.height / 2 - 55, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line2"), this.width / 2, this.height / 2 - 38, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line3"), this.width / 2, this.height / 2 - 26, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line4"), this.width / 2, this.height / 2 - 14, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line5"), this.width / 2, this.height / 2 - 2, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line6"), this.width / 2, this.height / 2 + 10, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line7"), this.width / 2, this.height / 2 + 23, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, new net.minecraft.network.chat.TranslatableComponent("item.playercollars.deed_of_ownership.line8"), this.width / 2, this.height / 2 + 40, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void stampDeed() {
        NETWORK.sendToServer(new PacketStampDeed());
        onClose();
    }
}
