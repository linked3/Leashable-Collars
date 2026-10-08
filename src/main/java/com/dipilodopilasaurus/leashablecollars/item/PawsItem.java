package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Compat;
//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import org.jetbrains.annotations.NotNull;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.paws.PawsFilter;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import com.dipilodopilasaurus.leashablecollars.block.DogBowlBlock;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;
//?} else {
/*import java.util.List;
*///?}
//? if <1.20.5 {
/*import net.minecraft.world.level.Level;
*///?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.Consumer;

public class PawsItem extends FootPawsItem {
    public PawsItem(ResourceKey<Item> key, int color, int pawColor) {
        // Hand slots come from the library's item tag, not an argument. See FootPawsItem.
        super(key, color, pawColor);
    }

    public static boolean hasPaws(LivingEntity entity) {
        return FeatureRules.CAN_USE_PAWS.enabled(Compat.level(entity)) && EquippedAccessories.hasEquipped(entity, x -> x.is(PlayerCollarsMod.PAWS_TAG));
    }

    public static boolean shouldPreventBlockInteraction(ItemStack stack, @NotNull BlockState block) {
        if (isHardAllowedBlockInteraction(block)) return false;
        return !PawsFilter.blocks().allows(Components.get(stack, PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE), block.getBlock());
    }

    public static boolean shouldPreventBlockInteraction(LivingEntity entity, @NotNull BlockState block) {
        if (!FeatureRules.CAN_USE_PAWS.enabled(Compat.level(entity))) return false;
        for (ItemStack paws : EquippedAccessories.getEquipped(entity, x -> x.is(PlayerCollarsMod.PAWS_TAG))) {
            if (shouldPreventBlockInteraction(paws, block)) return true;
        }
        return false;
    }

    /** Sneaking with an item skips the block's own use in vanilla, so the block list is not what gates it. */
    public static boolean shouldPreventBlockInteraction(LivingEntity entity, @NotNull BlockState block,
                                                        ItemStack held, boolean sneaking) {
        if (sneaking && !held.isEmpty() && !shouldPreventItemUse(entity, held)) return false;
        return shouldPreventBlockInteraction(entity, block);
    }

    /** The holdable allow-list gates using an item as well as keeping hold of it. */
    public static boolean shouldPreventItemUse(LivingEntity entity, ItemStack stack) {
        if (!FeatureRules.CAN_USE_PAWS.enabled(Compat.level(entity))) return false;
        if (stack.isEmpty()) return false;
        for (ItemStack paws : EquippedAccessories.getEquipped(entity, x -> x.is(PlayerCollarsMod.PAWS_TAG))) {
            if (shouldDrop(paws, stack)) return true;
        }
        return false;
    }

    public static boolean isHardAllowedBlockInteraction(@NotNull BlockState block) {
        Block b = block.getBlock();
        return b instanceof DoorBlock
                || b instanceof TrapDoorBlock
                || b instanceof ButtonBlock
                || b instanceof LeverBlock
                || b instanceof DogBedBlock
                || b instanceof DogBowlBlock
                || block.is(PlayerCollarsMod.PAWS_ALLOW_INTERACT);
    }

    public static boolean shouldDrop(ItemStack pawsStack, ItemStack thing) {
        if (thing.isEmpty()) return false;
        // Runs every tick, and below 1.20.5 a get() parses the whole list out of NBT; has() is a key probe.
        if (!Components.has(pawsStack, PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE)) return true;
        return !PawsFilter.items().allows(Components.get(pawsStack, PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE), thing.getItem());
    }

    @Override
    //? if >=1.21.5 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
    //?} elif >=1.20.5 {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag type) {
        super.appendHoverText(stack, context, lines, type);
        Consumer<Component> tooltip = lines::add;
    *///?} else {
    /*public void appendHoverText(ItemStack stack, Level context, List<Component> lines, TooltipFlag type) {
        super.appendHoverText(stack, context, lines, type);
        Consumer<Component> tooltip = lines::add;
    *///?}
        if (!PawsFilter.items().allowsEverything(Components.get(stack, PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE))) {
            tooltip.accept(Text.translatable("item.playercollars.paws.slippery"));
        }
        if (!PawsFilter.blocks().allowsEverything(Components.get(stack, PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE))) {
            tooltip.accept(Text.translatable("item.playercollars.paws.interaction"));
        }
    }

    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, Ids.of(c.getName() + "_paws"));
    }
}
