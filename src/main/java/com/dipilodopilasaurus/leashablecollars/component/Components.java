package com.dipilodopilasaurus.leashablecollars.component;

//? if >=1.20.5 && <26.3 {
import net.minecraft.world.item.component.MapItemColor;
//?}
//? if >=26.3 {
/*import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
*///?}
//? if >=1.20.5 {
import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.DyedItemColor;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.DataComponentType;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Era seam for component storage: components above 1.20.5, stack NBT below; the 1.20.1 tag paths must not move. */
public final class Components {
    //? if <1.20.5 {
    /*private static final DataComponentType<Integer> DYE_COLOR = DataComponentType.at(Codec.INT, "display", "color");
    private static final DataComponentType<Integer> PAW_COLOR = DataComponentType.at(Codec.INT, "display", "paw");
    private static final DataComponentType<Boolean> FORCE_TURN =
            DataComponentType.at(Codec.BOOL, "playercollars_clicker", "force_turn");
    *///?}

    private Components() {
    }

    public static <T> @Nullable T get(ItemStack stack, DataComponentType<T> type) {
        //? if >=1.20.5 {
        return stack.get(type);
        //?} else {
        /*Tag tag = read(stack.getTag(), type.path());
        if (tag == null) return null;
        return type.codec().parse(NbtOps.INSTANCE, tag).result().orElse(null);
        *///?}
    }

    public static <T> T getOrDefault(ItemStack stack, DataComponentType<T> type, T fallback) {
        //? if >=1.20.5 {
        return stack.getOrDefault(type, fallback);
        //?} else {
        /*T value = get(stack, type);
        return value == null ? fallback : value;
        *///?}
    }

    public static <T> boolean has(ItemStack stack, DataComponentType<T> type) {
        //? if >=1.20.5 {
        return stack.has(type);
        //?} else {
        /*return read(stack.getTag(), type.path()) != null;
        *///?}
    }

    public static <T> void set(ItemStack stack, DataComponentType<T> type, T value) {
        //? if >=1.20.5 {
        stack.set(type, value);
        //?} else {
        /*Codec<T> codec = type.codec();
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(encoded -> {
            String[] path = type.path();
            CompoundTag parent = stack.getOrCreateTag();
            for (int i = 0; i < path.length - 1; i++) {
                // getCompound hands back a detached empty tag when the key is absent, so put it back.
                CompoundTag child = parent.getCompound(path[i]);
                parent.put(path[i], child);
                parent = child;
            }
            parent.put(path[path.length - 1], encoded);
        });
        *///?}
    }

    public static <T> void remove(ItemStack stack, DataComponentType<T> type) {
        //? if >=1.20.5 {
        stack.remove(type);
        //?} else {
        /*String[] path = type.path();
        CompoundTag parent = stack.getTag();
        for (int i = 0; parent != null && i < path.length - 1; i++) {
            parent = parent.contains(path[i], Tag.TAG_COMPOUND) ? parent.getCompound(path[i]) : null;
        }
        if (parent != null) parent.remove(path[path.length - 1]);
        *///?}
    }

    /** Collar dye colour, as an rgb int on both sides of the boundary. */
    public static int dyeColor(ItemStack stack, int fallback) {
        //? if >=1.20.5 {
        DyedItemColor value = stack.get(DataComponents.DYED_COLOR);
        return value == null ? fallback : value.rgb();
        //?} else {
        /*Integer value = get(stack, DYE_COLOR);
        return value == null ? fallback : value;
        *///?}
    }

    /** See {@link #dyeColor}. */
    public static void setDyeColor(ItemStack stack, int rgb) {
        //? if >=1.20.5 {
        stack.set(DataComponents.DYED_COLOR, Compat.dyedColor(rgb));
        //?} else {
        /*set(stack, DYE_COLOR, rgb);
        *///?}
    }

    /** The collar's paw colour rides vanilla's map colour above 1.20.5 and its own tag below. */
    public static int pawColor(ItemStack stack, int fallback) {
        //? if >=26.3 {
        /*Integer rgb = stack.get(PlayerCollarsMod.PAW_COLOR_COMPONENT_TYPE);
        return rgb == null ? fallback : rgb;
        *///?} elif >=1.20.5 {
        MapItemColor value = stack.get(DataComponents.MAP_COLOR);
        return value == null ? fallback : value.rgb();
        //?} else {
        /*Integer value = get(stack, PAW_COLOR);
        return value == null ? fallback : value;
        *///?}
    }

    /** See {@link #pawColor}. */
    public static void setPawColor(ItemStack stack, int rgb) {
        //? if >=26.3 {
        /*stack.set(PlayerCollarsMod.PAW_COLOR_COMPONENT_TYPE, rgb);
        *///?} elif >=1.20.5 {
        stack.set(DataComponents.MAP_COLOR, new MapItemColor(rgb));
        //?} else {
        /*set(stack, PAW_COLOR, rgb);
        *///?}
    }

    /** Item defaults only exist as components; below 1.20.5 the readers' fallbacks carry them. */
    public static Item.Properties withDefaultColors(Item.Properties properties, int dye, int paw) {
        //? if >=26.3 {
        /*return properties.component(DataComponents.DYED_COLOR, Compat.dyedColor(dye))
                .component(PlayerCollarsMod.PAW_COLOR_COMPONENT_TYPE, paw);
        *///?} elif >=1.20.5 {
        return properties.component(DataComponents.DYED_COLOR, Compat.dyedColor(dye))
                .component(DataComponents.MAP_COLOR, new MapItemColor(paw));
        //?} else {
        /*return properties;
        *///?}
    }

    /** The clicker's head-turn flag is a marker component above 1.20.5 and a boolean tag below. */
    public static boolean forceTurn(ItemStack stack) {
        //? if >=1.20.5 {
        return stack.has(DataComponents.INTANGIBLE_PROJECTILE);
        //?} else {
        /*return Boolean.TRUE.equals(get(stack, FORCE_TURN));
        *///?}
    }

    /** See {@link #forceTurn}. */
    public static void setForceTurn(ItemStack stack, boolean enabled) {
        //? if >=1.20.5 {
        if (enabled) stack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        else stack.remove(DataComponents.INTANGIBLE_PROJECTILE);
        //?} else {
        /*set(stack, FORCE_TURN, enabled);
        *///?}
    }

    //? if <1.20.5 {
    /*private static @Nullable Tag read(@Nullable CompoundTag root, String[] path) {
        CompoundTag parent = root;
        for (int i = 0; parent != null && i < path.length - 1; i++) {
            parent = parent.contains(path[i], Tag.TAG_COMPOUND) ? parent.getCompound(path[i]) : null;
        }
        return parent == null ? null : parent.get(path[path.length - 1]);
    }
    *///?}
}
