package com.dipilodopilasaurus.leashablecollars.registry.compat;

//? if <1.19.3 {
/*import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BuiltInRegistries {
    public static final Registry<? extends Registry<?>> REGISTRY = Registry.REGISTRY;
    public static final Registry<Block> BLOCK = Registry.BLOCK;
    public static final Registry<Item> ITEM = Registry.ITEM;
    public static final Registry<Enchantment> ENCHANTMENT = Registry.ENCHANTMENT;
    public static final Registry<Attribute> ATTRIBUTE = Registry.ATTRIBUTE;
    public static final Registry<SoundEvent> SOUND_EVENT = Registry.SOUND_EVENT;
    public static final Registry<MenuType<?>> MENU = Registry.MENU;
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = Registry.RECIPE_SERIALIZER;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = Registry.BLOCK_ENTITY_TYPE;

    private BuiltInRegistries() {
    }
}
*///?}
