package com.dipilodopilasaurus.leashablecollars.item;

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
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.ItemStack;
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
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import java.util.function.IntFunction;

public class OwnershipCraftingRecipe extends CustomRecipe {
    //? if >=1.21.2 {
    private PlacementInfo ingredientPlacement;
    //?}
    //? if >=1.19.3 {
    private final CraftingBookCategory category;
    //?}
    private final Ingredient base;

    // 26.1 dropped the category from CustomRecipe's constructor; below 1.20.5 the recipe carries its own id.
    //? if >=26.1 {
    /*public OwnershipCraftingRecipe(CraftingBookCategory category, Ingredient base) {
        super();
    *///?} elif >=1.20.5 {
    public OwnershipCraftingRecipe(CraftingBookCategory category, Ingredient base) {
        super(category);
    //?} elif >=1.19.3 {
    /*public OwnershipCraftingRecipe(ResourceLocation id, CraftingBookCategory category, Ingredient base) {
        super(id, category);
    *///?} else {
    /*public OwnershipCraftingRecipe(ResourceLocation id, Ingredient base) {
        super(id);
    *///?}
        //? if >=1.19.3 {
        this.category = category;
        //?}
        this.base = base;
    }

    //? if >=1.20.5 {
    public boolean matches(CraftingInput input, Level world) {
        return matchesGrid(input.size(), input::getItem);
    }
    //?} else {
    /*@Override
    public boolean matches(CraftingContainer input, Level world) {
        return matchesGrid(input.getContainerSize(), input::getItem);
    }
    *///?}

    // Walked by hand: below 1.20.5 nothing checks it, and stacked-contents says nothing about pairing.
    private boolean matchesGrid(int size, IntFunction<ItemStack> slot) {
        ItemStack deed = ItemStack.EMPTY;
        ItemStack baseStack = ItemStack.EMPTY;

        for (int i = 0; i < size; i++) {
            ItemStack is = slot.apply(i);
            if (is.isEmpty()) continue;
            if (is.is(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)) {
                if (!deed.isEmpty()) return false;
                deed = is;
            } else if (base.test(is) && baseStack.isEmpty()) {
                baseStack = is;
            } else {
                return false;
            }
        }
        return !deed.isEmpty() && !baseStack.isEmpty()
                && fits(Components.get(deed, PlayerCollarsMod.OWNER_COMPONENT_TYPE), baseStack);
    }

    /** An already-bound base takes a second deed only to fill its empty owned half, and only its owner's. */
    private static boolean fits(OwnerComponent deed, ItemStack base) {
        if (deed == null || deed.owned().isEmpty()) return false;
        OwnerComponent bound = Components.get(base, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        return bound == null || (bound.owned().isEmpty() && bound.uuid().equals(deed.uuid()));
    }

    // 26.1 dropped the registry-lookup parameter from Recipe.assemble.
    //? if >=26.1 {
    /*public ItemStack assemble(CraftingInput input) {
        int size = input.size();
    *///?} elif >=1.20.5 {
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int size = input.size();
    //?} elif >=1.19.3 {
    /*public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        int size = input.getContainerSize();
    *///?} else {
    /*public ItemStack assemble(CraftingContainer input) {
        int size = input.getContainerSize();
    *///?}
        ItemStack output = ItemStack.EMPTY;
        OwnerComponent owner = null;

        for (int j = 0; j < size; j++) {
            ItemStack is = input.getItem(j);
            if (!is.isEmpty()) {
                if (is.is(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)) {
                    owner = Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                } else if (base.test(is)) {
                    output = is.copy();
                }
            }
        }

        if (owner == null || output.isEmpty()) return ItemStack.EMPTY;
        Components.set(output, PlayerCollarsMod.OWNER_COMPONENT_TYPE, owner);
        return output;
    }

    @Override
    //? if >=1.20.5 {
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        int size = input.size();
    //?} else {
    /*public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
        int size = input.getContainerSize();
    *///?}
        NonNullList<ItemStack> remaining = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)) {
                remaining.set(i, stack.copy());
            }
        }
        return remaining;
    }

    //? if >=1.19.3 {
    @Override
    public CraftingBookCategory category() {
        return category;
    }
    //?}

    private Ingredient getBase() {
        return base;
    }

    // 1.21.2 replaced canCraftInDimensions with PlacementInfo.
    //? if >=1.21.2 {
    @Override
    public PlacementInfo placementInfo() {
        if (ingredientPlacement == null) {
            ingredientPlacement = PlacementInfo.create(List.of(base, Ingredient.of(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)));
        }

        return ingredientPlacement;
    }
    //?} else {
    /*@Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }
    *///?}

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return com.dipilodopilasaurus.leashablecollars.item.OwnershipCraftingRecipe.Serializer.INSTANCE;
    }

    //? if >=1.20.5 {
    public static class Serializer {
        private static final MapCodec<OwnershipCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
            Ingredient.CODEC.fieldOf("base").forGetter(OwnershipCraftingRecipe::getBase)
        ).apply(builder, OwnershipCraftingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, OwnershipCraftingRecipe> PACKET_CODEC = StreamCodec.composite(
                CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC, OwnershipCraftingRecipe::getBase, OwnershipCraftingRecipe::new
        );
        // 26.1 turned RecipeSerializer into a concrete class taking both codecs.
        //? if >=26.1 {
        /*public static final RecipeSerializer<OwnershipCraftingRecipe> INSTANCE = new RecipeSerializer<>(CODEC, PACKET_CODEC);
        *///?} else {
        public static final RecipeSerializer<OwnershipCraftingRecipe> INSTANCE = new RecipeSerializer<>() {
            @Override
            public MapCodec<OwnershipCraftingRecipe> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, OwnershipCraftingRecipe> streamCodec() {
                return PACKET_CODEC;
            }
        };
        //?}
    }
    //?} elif >=1.19.3 {
    /*// Recipes are JSON below 1.20.5, so the serializer parses rather than decodes.
    public static class Serializer implements RecipeSerializer<OwnershipCraftingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public OwnershipCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
                    json.has("category") ? json.get("category").getAsString() : "misc", CraftingBookCategory.MISC);
            return new OwnershipCraftingRecipe(id, category, Ingredient.fromJson(json.get("base")));
        }

        @Override
        public OwnershipCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new OwnershipCraftingRecipe(id, buffer.readEnum(CraftingBookCategory.class), Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OwnershipCraftingRecipe recipe) {
            buffer.writeEnum(recipe.category());
            recipe.base.toNetwork(buffer);
        }
    }
    *///?} else {
    /*//? if forge {
    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>
            implements RecipeSerializer<OwnershipCraftingRecipe> {
    //?} else {
    /^public static class Serializer implements RecipeSerializer<OwnershipCraftingRecipe> {
    ^///?}
    //? if <1.19.3 {
    /^    public static final Serializer INSTANCE = new Serializer();

        @Override
        public OwnershipCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new OwnershipCraftingRecipe(id, Ingredient.fromJson(json.get("base")));
        }

        @Override
        public OwnershipCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new OwnershipCraftingRecipe(id, Ingredient.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OwnershipCraftingRecipe recipe) {
            recipe.base.toNetwork(buffer);
        }
    }
    ^///?}
    *///?}
}
