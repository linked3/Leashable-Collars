package com.dipilodopilasaurus.leashablecollars;

//? if >=1.21.9 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.resources.ResourceKey;

/**
 * The one place in this tree that names Minecraft's namespaced-id type, renamed in 1.21.9. Elsewhere use
 * {@code var} for locals and take the path as a {@code String}, so the rename stays two guards not sixty.
 */
public final class Ids {
    private Ids() {
    }

    //? if >=1.21.9 {
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
    //?} else {
    /*public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(PlayerCollarsMod.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation of(ResourceKey<?> key) {
        return key.location();
    }
    *///?}
}
