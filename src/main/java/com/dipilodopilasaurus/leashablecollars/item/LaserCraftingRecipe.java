package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=1.19.3 {
import net.minecraft.world.item.crafting.CraftingBookCategory;
//?}
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
//? if >=1.21.2 {
import net.minecraft.world.item.crafting.PlacementInfo;
//?}
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

import java.util.List;
import java.util.function.IntFunction;

/** The stained-pane half of the laser recipe: same column, but the lens carries the pane's dye. */
public class LaserCraftingRecipe extends CustomRecipe {
    //? if >=1.21.2 {
    private PlacementInfo ingredientPlacement;
    //?}
    //? if >=1.19.3 {
    private final CraftingBookCategory category;
    //?}
    private final Ingredient shaft;

    //? if >=26.1 {
    /*public LaserCraftingRecipe(CraftingBookCategory category, Ingredient shaft) {
        super();
    *///?} elif >=1.20.5 {
    public LaserCraftingRecipe(CraftingBookCategory category, Ingredient shaft) {
        super(category);
    //?} elif >=1.19.3 {
    /*public LaserCraftingRecipe(ResourceLocation id, CraftingBookCategory category, Ingredient shaft) {
        super(id, category);
    *///?} else {
    /*public LaserCraftingRecipe(ResourceLocation id, Ingredient shaft) {
        super(id);
    *///?}
        //? if >=1.19.3 {
        this.category = category;
        //?}
        this.shaft = shaft;
    }

    //? if >=1.20.5 {
    public boolean matches(CraftingInput input, Level world) {
        return lens(input.width(), input.size(), input::getItem) != null;
    }
    //?} else {
    /*@Override
    public boolean matches(CraftingContainer input, Level world) {
        return lens(input.getWidth(), input.getContainerSize(), input::getItem) != null;
    }
    *///?}

    //? if >=26.1 {
    /*public ItemStack assemble(CraftingInput input) {
        DyeColor dye = lens(input.width(), input.size(), input::getItem);
    *///?} elif >=1.20.5 {
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        DyeColor dye = lens(input.width(), input.size(), input::getItem);
    //?} elif >=1.19.3 {
    /*public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        DyeColor dye = lens(input.getWidth(), input.getContainerSize(), input::getItem);
    *///?} else {
    /*public ItemStack assemble(CraftingContainer input) {
        DyeColor dye = lens(input.getWidth(), input.getContainerSize(), input::getItem);
    *///?}
        if (dye == null) return ItemStack.EMPTY;
        ItemStack output = new ItemStack(PlayerCollarsMod.LASER_POINTER_ITEM);
        Components.setDyeColor(output, Compat.dyeRgb(dye));
        return output;
    }

    // The pattern is one column, so the grid is walked for exactly three filled slots a row apart.
    private DyeColor lens(int width, int size, IntFunction<ItemStack> slot) {
        int first = -1;
        int found = 0;
        ItemStack[] column = new ItemStack[3];
        for (int i = 0; i < size; i++) {
            ItemStack stack = slot.apply(i);
            if (stack.isEmpty()) continue;
            if (found == 3) return null;
            if (first < 0) first = i;
            else if (i != first + found * width) return null;
            column[found++] = stack;
        }
        if (found != 3 || !column[1].is(Items.EMERALD) || !shaft.test(column[2])) return null;
        return Block.byItem(column[0].getItem()) instanceof StainedGlassPaneBlock pane ? pane.getColor() : null;
    }

    //? if >=1.19.3 {
    @Override
    public CraftingBookCategory category() {
        return category;
    }
    //?}

    private Ingredient getShaft() {
        return shaft;
    }

    //? if >=1.21.2 {
    @Override
    public PlacementInfo placementInfo() {
        if (ingredientPlacement == null) {
            ingredientPlacement = PlacementInfo.create(List.of(Ingredient.of(Items.EMERALD), shaft));
        }

        return ingredientPlacement;
    }
    //?} else {
    /*@Override
    public boolean canCraftInDimensions(int width, int height) {
        return height >= 3;
    }
    *///?}

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return com.dipilodopilasaurus.leashablecollars.item.LaserCraftingRecipe.Serializer.INSTANCE;
    }

    //? if >=1.20.5 {
    public static class Serializer {
        private static final MapCodec<LaserCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
            Ingredient.CODEC.fieldOf("base").forGetter(LaserCraftingRecipe::getShaft)
        ).apply(builder, LaserCraftingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, LaserCraftingRecipe> PACKET_CODEC = StreamCodec.composite(
                CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC, LaserCraftingRecipe::getShaft, LaserCraftingRecipe::new
        );
        //? if >=26.1 {
        /*public static final RecipeSerializer<LaserCraftingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, PACKET_CODEC);
        *///?} else {
        public static final RecipeSerializer<LaserCraftingRecipe> INSTANCE = new RecipeSerializer<>() {
            @Override
            public MapCodec<LaserCraftingRecipe> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, LaserCraftingRecipe> streamCodec() {
                return PACKET_CODEC;
            }
        };
        //?}
    }
    //?} elif >=1.19.3 {
    /*public static class Serializer implements RecipeSerializer<LaserCraftingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public LaserCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
                    json.has("category") ? json.get("category").getAsString() : "misc", CraftingBookCategory.MISC);
            return new LaserCraftingRecipe(id, category, Ingredient.fromJson(json.get("base")));
        }

        @Override
        public LaserCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new LaserCraftingRecipe(id, buffer.readEnum(CraftingBookCategory.class), Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LaserCraftingRecipe recipe) {
            buffer.writeEnum(recipe.category());
            recipe.shaft.toNetwork(buffer);
        }
    }
    *///?} else {
    /*//? if forge {
    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<LaserCraftingRecipe> {
    //?} else {
    /^public static class Serializer implements RecipeSerializer<LaserCraftingRecipe> {
    ^///?}
    //? if <1.19.3 {
    /^    public static final Serializer INSTANCE = new Serializer();

        @Override
        public LaserCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new LaserCraftingRecipe(id, Ingredient.fromJson(json.get("base")));
        }

        @Override
        public LaserCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new LaserCraftingRecipe(id, Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LaserCraftingRecipe recipe) {
            recipe.shaft.toNetwork(buffer);
        }
    }
    ^///?}
    *///?}
}
