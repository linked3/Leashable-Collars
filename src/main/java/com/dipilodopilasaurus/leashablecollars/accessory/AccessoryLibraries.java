//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

/^*
 * Which equipment library is actually installed, on the nodes where Accessories publishes for this
 * loader too. Probed by class rather than by mod id so one form serves Forge and NeoForge.
 ^/
public final class AccessoryLibraries {
    private static final boolean CURIOS = present("top.theillusivec4.curios.api.CuriosApi");
    private static final boolean ACCESSORIES = present("io.wispforest.accessories.api.AccessoriesAPI");

    private AccessoryLibraries() {
    }

    public static boolean curios() {
        return CURIOS;
    }

    public static boolean accessories() {
        return ACCESSORIES;
    }

    private static boolean present(String className) {
        try {
            Class.forName(className, false, AccessoryLibraries.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError absent) {
            // The library is simply not installed; every call site already handles that.
            return false;
        }
    }
}
*///?}
