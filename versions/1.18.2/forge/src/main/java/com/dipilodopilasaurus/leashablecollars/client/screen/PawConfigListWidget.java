package com.dipilodopilasaurus.leashablecollars.client.screen;

import com.dipilodopilasaurus.leashablecollars.PawConfigEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.List;
import java.util.function.IntConsumer;

public class PawConfigListWidget extends AbstractSelectionList<PawConfigListWidget.Entry> {
    private final boolean heldItems;
    private final IntConsumer clickHandler;
    private int left;

    public PawConfigListWidget(int width, int height, int y, int itemHeight, boolean heldItems, IntConsumer clickHandler) {
        super(Minecraft.getInstance(), width, height, y, y + height, itemHeight);
        this.heldItems = heldItems;
        this.clickHandler = clickHandler;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    public void setLeftPos(int left) {
        this.left = left;
        this.x0 = left;
        this.x1 = left + this.width;
    }

    public void setEntries(List<PawConfigEntry> entries) {
        clearEntries();
        for (int i = 0; i < entries.size(); i++) {
            addEntry(new Entry(i, entries.get(i)));
        }
        setScrollAmount(0.0D);
    }

    @Override
    public int getRowWidth() {
        return this.width - 5;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.left + this.getRowWidth();
    }

    @Override
    public int getRowLeft() {
        return this.left + 2;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final int index;
        private final PawConfigEntry configEntry;

        public Entry(int index, PawConfigEntry configEntry) {
            this.index = index;
            this.configEntry = configEntry;
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            if (hovered) {
                fill(poseStack, left, top - 1, left + width, top + height - 1, 0x8F999999);
            }
            Minecraft.getInstance().font.draw(poseStack, configEntry.getDisplayName(heldItems), left, top, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            clickHandler.accept(index);
            return true;
        }
    }
}
