package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.mojang.serialization.MapCodec;
//? if >=1.20.5 {
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
//?} else {
/*import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
*///?}
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.19.3 {
import net.minecraft.world.item.crafting.CraftingBookCategory;
//?}
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
//? if >=1.21.2 {
import net.minecraft.world.item.crafting.PlacementInfo;
//?}
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Re-dyeing in the grid, blending into the colour already on the item the way leather armour does.
 * Dyes left of the collar reach the band and dyes right of it the tag; one-colour items take every dye.
 */
public class DyeCraftingRecipe extends CustomRecipe {
    //? if >=1.19.3 {
    private final CraftingBookCategory category;
    //?}

    //? if >=26.1 {
    /*public DyeCraftingRecipe(CraftingBookCategory category) {
        super();
    *///?} elif >=1.20.5 {
    public DyeCraftingRecipe(CraftingBookCategory category) {
        super(category);
    //?} elif >=1.19.3 {
    /*public DyeCraftingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    *///?} else {
    /*public DyeCraftingRecipe(ResourceLocation id) {
        super(id);
    *///?}
        //? if >=1.19.3 {
        this.category = category;
        //?}
    }

    //? if >=1.20.5 {
    public boolean matches(CraftingInput input, Level world) {
        return dyeable(input.size(), input::getItem) >= 0;
    }
    //?} else {
    /*@Override
    public boolean matches(CraftingContainer input, Level world) {
        return dyeable(input.getContainerSize(), input::getItem) >= 0;
    }
    *///?}

    //? if >=26.1 {
    /*public ItemStack assemble(CraftingInput input) {
        return dye(input.width(), input.size(), input::getItem);
    *///?} elif >=1.20.5 {
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return dye(input.width(), input.size(), input::getItem);
    //?} elif >=1.19.3 {
    /*public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        return dye(input.getWidth(), input.getContainerSize(), input::getItem);
    *///?} else {
    /*public ItemStack assemble(CraftingContainer input) {
        return dye(input.getWidth(), input.getContainerSize(), input::getItem);
    *///?}
    }

    /** Slot of the one dyeable item, or -1 when the grid holds anything else, or no dye. */
    private static int dyeable(int size, IntFunction<ItemStack> slot) {
        int found = -1;
        boolean dyed = false;
        for (int i = 0; i < size; i++) {
            ItemStack stack = slot.apply(i);
            if (stack.isEmpty()) continue;
            if (isDyeable(stack)) {
                if (found >= 0) return -1;
                found = i;
            } else if (Compat.dyeOf(stack) != null) {
                dyed = true;
            } else {
                return -1;
            }
        }
        return dyed ? found : -1;
    }

    private static ItemStack dye(int width, int size, IntFunction<ItemStack> slot) {
        int target = dyeable(size, slot);
        if (target < 0) return ItemStack.EMPTY;

        int column = target % width;
        List<DyeColor> band = new ArrayList<>();
        List<DyeColor> tag = new ArrayList<>();
        List<DyeColor> all = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DyeColor color = Compat.dyeOf(slot.apply(i));
            if (color == null) continue;
            if (i % width <= column) band.add(color);
            if (i % width >= column) tag.add(color);
            all.add(color);
        }

        ItemStack output = Compat.copyWithCount(slot.apply(target), 1);
        applyColors(output, band, tag, all);
        return output;
    }

    private static boolean isDyeable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof CollarItem || item instanceof FootPawsItem
                || item instanceof ClickerItem || item instanceof LaserPointerItem;
    }

    private static void applyColors(ItemStack stack, List<DyeColor> band, List<DyeColor> tag, List<DyeColor> all) {
        Item item = stack.getItem();
        if (item instanceof CollarItem) {
            Components.setDyeColor(stack, blend(CollarItem.getColor(stack), CollarItem.DEFAULT_COLOR, band));
            Components.setPawColor(stack, blend(CollarItem.getPawColor(stack), CollarItem.DEFAULT_PAW_COLOR, tag));
        } else if (item instanceof FootPawsItem paws) {
            Components.setDyeColor(stack, blend(Components.dyeColor(stack, paws.color), paws.color, all));
        } else if (item instanceof ClickerItem) {
            int base = Components.dyeColor(stack, ClickerItem.DEFAULT_COLOR);
            Components.setDyeColor(stack, blend(base, ClickerItem.DEFAULT_COLOR, all));
        } else {
            int base = Components.dyeColor(stack, LaserPointerItem.DEFAULT_COLOR);
            Components.setDyeColor(stack, blend(base, LaserPointerItem.DEFAULT_COLOR, all));
        }
    }

    /** Vanilla's leather average, brightened back to the mean of the inputs' brightest channel. */
    private static int blend(int base, int fallback, List<DyeColor> dyes) {
        if (dyes.isEmpty()) return base & 0xFFFFFF;
        int[] rgb = new int[3];
        int sumMax = 0;
        int count = 0;
        if ((base & 0xFFFFFF) != (fallback & 0xFFFFFF)) {
            sumMax += accumulate(rgb, base);
            count++;
        }
        for (DyeColor dye : dyes) {
            sumMax += accumulate(rgb, Compat.dyeRgb(dye));
            count++;
        }
        int red = rgb[0] / count;
        int green = rgb[1] / count;
        int blue = rgb[2] / count;
        float maxAverage = (float) sumMax / count;
        float averageMax = Math.max(red, Math.max(green, blue));
        red = (int) (red * maxAverage / averageMax);
        green = (int) (green * maxAverage / averageMax);
        blue = (int) (blue * maxAverage / averageMax);
        return (red << 16) | (green << 8) | blue;
    }

    private static int accumulate(int[] rgb, int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        rgb[0] += red;
        rgb[1] += green;
        rgb[2] += blue;
        return Math.max(red, Math.max(green, blue));
    }

    //? if >=1.19.3 {
    @Override
    public CraftingBookCategory category() {
        return category;
    }
    //?}

    //? if >=1.21.2 {
    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }
    //?} else {
    /*@Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
    *///?}

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return com.dipilodopilasaurus.leashablecollars.item.DyeCraftingRecipe.Serializer.INSTANCE;
    }

    //? if >=1.20.5 {
    public static class Serializer {
        private static final MapCodec<DyeCraftingRecipe> CODEC =
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC)
                        .xmap(DyeCraftingRecipe::new, CraftingRecipe::category);
        public static final StreamCodec<RegistryFriendlyByteBuf, DyeCraftingRecipe> PACKET_CODEC = new StreamCodec<>() {
            @Override
            public DyeCraftingRecipe decode(RegistryFriendlyByteBuf buffer) {
                return new DyeCraftingRecipe(CraftingBookCategory.STREAM_CODEC.decode(buffer));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, DyeCraftingRecipe recipe) {
                CraftingBookCategory.STREAM_CODEC.encode(buffer, recipe.category());
            }
        };
        //? if >=26.1 {
        /*public static final RecipeSerializer<DyeCraftingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, PACKET_CODEC);
        *///?} else {
        public static final RecipeSerializer<DyeCraftingRecipe> INSTANCE = new RecipeSerializer<>() {
            @Override
            public MapCodec<DyeCraftingRecipe> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, DyeCraftingRecipe> streamCodec() {
                return PACKET_CODEC;
            }
        };
        //?}
    }
    //?} elif >=1.19.3 {
    /*public static class Serializer implements RecipeSerializer<DyeCraftingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public DyeCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new DyeCraftingRecipe(id, CraftingBookCategory.CODEC.byName(
                    json.has("category") ? json.get("category").getAsString() : "misc", CraftingBookCategory.MISC));
        }

        @Override
        public DyeCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new DyeCraftingRecipe(id, buffer.readEnum(CraftingBookCategory.class));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DyeCraftingRecipe recipe) {
            buffer.writeEnum(recipe.category());
        }
    }
    *///?} else {
    /*//? if forge {
    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<DyeCraftingRecipe> {
    //?} else {
    /^public static class Serializer implements RecipeSerializer<DyeCraftingRecipe> {
    ^///?}
    //? if <1.19.3 {
    /^    public static final Serializer INSTANCE = new Serializer();

        @Override
        public DyeCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new DyeCraftingRecipe(id);
        }

        @Override
        public DyeCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new DyeCraftingRecipe(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DyeCraftingRecipe recipe) {
            // This recipe has no configurable payload on 1.18.2.
        }
    }
    ^///?}
    *///?}
}
