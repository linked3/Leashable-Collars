package com.dipilodopilasaurus.leashablecollars.client.screen;

//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.dipilodopilasaurus.leashablecollars.inventory.InventoryEditorMenu;

/**
 * Two inventory grids, the pet's above the owner's, gold outline on pinned slots. Filled rather than
 * blitted -- the container texture's blit signature changes in almost every version here.
 */
public class InventoryEditorScreen extends AbstractContainerScreen<InventoryEditorMenu> {
    private static final int BORDER = 0xFF1F1F26;
    private static final int PANEL = 0xFF2E2E38;
    private static final int SLOT = 0xFF14141A;
    private static final int LOCKED = 0xFFFFC300;

    public InventoryEditorScreen(InventoryEditorMenu menu, Inventory inventory, Component title) {
        // 26.1 made imageWidth/imageHeight final, so the size has to arrive through the constructor.
        //? if >=26.1 {
        /*super(menu, inventory, title, 176, 186);
        *///?} else
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        //? if <26.1 {
        this.imageWidth = 176;
        this.imageHeight = 186;
        //?}
        this.inventoryLabelY = 92;
        super.init();
    }

    //? if <1.20 {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }
    *///?}

    // Signature guarded, body shared -- see GuiCompat.
    //? if >=26.1 {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
    *///?} elif >=1.20 {
    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
    //?} else {
    /*@Override
    protected void renderBg(PoseStack context, float delta, int mouseX, int mouseY) {
    *///?}
        GuiCompat.fill(context, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BORDER);
        GuiCompat.fill(context, leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, PANEL);

        for (Slot slot : menu.slots) {
            int x = leftPos + slot.x;
            int y = topPos + slot.y;
            GuiCompat.fill(context, x - 1, y - 1, x + 17, y + 17, SLOT);
        }
        for (int index = 0; index < InventoryEditorMenu.TARGET_SLOT_COUNT; index++) {
            if (!menu.isTargetSlotLocked(index)) continue;
            Slot slot = menu.slots.get(index);
            outline(context, leftPos + slot.x, topPos + slot.y);
        }
    }

    //? if >=26.1 {
    /*private static void outline(GuiGraphicsExtractor context, int x, int y) {
    *///?} elif >=1.20 {
    private static void outline(GuiGraphics context, int x, int y) {
    //?} else {
    /*private static void outline(PoseStack context, int x, int y) {
    *///?}
        GuiCompat.fill(context, x - 1, y - 1, x + 17, y, LOCKED);
        GuiCompat.fill(context, x - 1, y + 16, x + 17, y + 17, LOCKED);
        GuiCompat.fill(context, x - 1, y, x, y + 16, LOCKED);
        GuiCompat.fill(context, x + 16, y, x + 17, y + 16, LOCKED);
    }
}
