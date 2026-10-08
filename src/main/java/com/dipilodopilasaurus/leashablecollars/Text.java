package com.dipilodopilasaurus.leashablecollars;

//? if >=1.19 {
import net.minecraft.network.chat.Component;
//?}
import net.minecraft.network.chat.MutableComponent;

public final class Text {
    private Text() {
    }

    public static MutableComponent literal(String text) {
        //? if >=1.19 {
        return Component.literal(text);
        //?} else {
        /*return new net.minecraft.network.chat.TextComponent(text);
        *///?}
    }

    public static MutableComponent translatable(String key, Object... arguments) {
        //? if >=1.19 {
        return Component.translatable(key, arguments);
        //?} else {
        /*return new net.minecraft.network.chat.TranslatableComponent(key, arguments);
        *///?}
    }

    public static MutableComponent empty() {
        //? if >=1.19 {
        return Component.empty();
        //?} else {
        /*return new net.minecraft.network.chat.TextComponent("");
        *///?}
    }
}
