package com.dipilodopilasaurus.leashablecollars;

//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.resources.ResourceKey;

/**
 * The one place that names Minecraft's namespaced-id type, renamed in 1.21.11. Elsewhere use var and
 * take the path as a String, so the rename stays two guards.
 */
public final class Ids {
    private Ids() {
    }

    //? if >=1.21.11 {
    /** An id in this mod's namespace. */
    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(PlayerCollarsMod.MOD_ID, path);
    }

    /** An id in some other namespace -- the conventional {@code c:} tag namespace, in practice. */
    public static Identifier of(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    /** The id a registry key was created from. */
    public static Identifier of(ResourceKey<?> key) {
        return key.identifier();
    }

    /** Round-trips {@link #of}'s {@code toString}; null when the text is not a well-formed id. */
    public static Identifier parse(String text) {
        return Identifier.tryParse(text);
    }
    //?} elif >=1.20.5 {
    /*public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(PlayerCollarsMod.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation of(ResourceKey<?> key) {
        return key.location();
    }

    public static ResourceLocation parse(String text) {
        return ResourceLocation.tryParse(text);
    }
    *///?} else {
    /*// The namespace/path factories arrived in 1.20.5; before it the constructor is the only way in.
    public static ResourceLocation of(String path) {
        return new ResourceLocation(PlayerCollarsMod.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation of(ResourceKey<?> key) {
        return key.location();
    }

    public static ResourceLocation parse(String text) {
        return ResourceLocation.tryParse(text);
    }
    *///?}
}
