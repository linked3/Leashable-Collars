package com.dipilodopilasaurus.leashablecollars.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
//? if <1.20 {
/*import net.minecraft.client.gui.GuiComponent;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
import net.minecraft.network.chat.Component;

/** GUI shims: 26.1 renamed the text calls, 26.2 moved the open screen off Minecraft onto Gui. */
public final class GuiCompat {
    private GuiCompat() {
    }

    /** Replaces the open screen, {@code null} closing it; not {@code setScreenAndShow}, which also forces a frame. */
    public static void setScreen(Minecraft client, Screen screen) {
        //? if >=26.2 {
        /*client.gui.setScreen(screen);
        *///?} else {
        client.setScreen(screen);
        //?}
    }

    public static Screen currentScreen(Minecraft client) {
        //? if >=26.2 {
        /*return client.gui.screen();
        *///?} else {
        return client.screen;
        //?}
    }

    //? if >=26.1 {
    /*public static void centeredText(GuiGraphicsExtractor context, Font font, Component text, int x, int y, int color) {
        context.centeredText(font, text, x, y, color);
    }

    public static void text(GuiGraphicsExtractor context, Font font, Component text, int x, int y, int color, boolean shadow) {
        context.text(font, text, x, y, color, shadow);
    }
    *///?} elif >=1.20 {
    public static void centeredText(GuiGraphics context, Font font, Component text, int x, int y, int color) {
        context.drawCenteredString(font, text, x, y, color);
    }

    public static void text(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean shadow) {
        context.drawString(font, text, x, y, color, shadow);
    }
    //?} else {
    /*public static void centeredText(PoseStack context, Font font, Component text, int x, int y, int color) {
        GuiComponent.drawCenteredString(context, font, text, x, y, color);
    }

    public static void text(PoseStack context, Font font, Component text, int x, int y, int color, boolean shadow) {
        if (shadow) font.drawShadow(context, text, x, y, color);
        else font.draw(context, text, x, y, color);
    }
    *///?}

    public static Button button(Component label, Button.OnPress action, int x, int y, int width, int height) {
        //? if >=1.19.3 {
        return Button.builder(label, action).bounds(x, y, width, height).build();
        //?} else {
        /*return new Button(x, y, width, height, label, action);
        *///?}
    }

    public static EditBox searchBox(Font font, int x, int y, int width, int height, Component hint) {
        //? if >=1.19.3 {
        EditBox box = new EditBox(font, x, y, width, height, hint);
        box.setHint(hint);
        return box;
        //?} else {
        /*return new EditBox(font, x, y, width, height, hint) {
            @Override
            public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
                super.renderButton(context, mouseX, mouseY, delta);
                if (isVisible() && !isFocused() && getValue().isEmpty()) {
                    font.drawShadow(context, font.plainSubstrByWidth(hint.getString(), getInnerWidth()),
                            x + 4, y + (height - 8) / 2, 0x707070);
                }
            }
        };
        *///?}
    }

    //? if >=26.1 {
    /*public static void fill(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y2, color);
    }
    *///?} elif >=1.20 {
    public static void fill(GuiGraphics context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y2, color);
    }
    //?} else {
    /*public static void fill(PoseStack context, int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(context, x1, y1, x2, y2, color);
    }
    *///?}

}
