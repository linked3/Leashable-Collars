package com.dipilodopilasaurus.leashablecollars.client.screen;

import net.minecraft.client.gui.Font;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Text-drawing shim for our screens. 26.1 split drawing into an extraction phase and renamed the calls
 * ({@code drawCenteredString} -> {@code centeredText}, {@code drawString} -> {@code text}). Screens still
 * guard their own override signature; only the bodies are shared.
 */
final class GuiCompat {
    private GuiCompat() {
    }

    //? if >=26.1 {
    /*static void centeredText(GuiGraphicsExtractor context, Font font, Component text, int x, int y, int color) {
        context.centeredText(font, text, x, y, color);
    }

    static void text(GuiGraphicsExtractor context, Font font, Component text, int x, int y, int color, boolean shadow) {
        context.text(font, text, x, y, color, shadow);
    }
    *///?} else
    static void centeredText(GuiGraphics context, Font font, Component text, int x, int y, int color) {
        context.drawCenteredString(font, text, x, y, color);
    }

    static void text(GuiGraphics context, Font font, Component text, int x, int y, int color, boolean shadow) {
        context.drawString(font, text, x, y, color, shadow);
    }
}
