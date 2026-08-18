package com.dipilodopilasaurus.leashablecollars.item;

import org.jetbrains.annotations.NotNull;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import com.dipilodopilasaurus.leashablecollars.block.DogBowlBlock;

import net.minecraft.core.registries.Registries;
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
        return EquippedAccessories.hasEquipped(entity, (x) -> x.is(PlayerCollarsMod.PAWS_TAG));
    }

    public static boolean shouldPreventBlockInteraction(ItemStack stack, @NotNull BlockState block) {
        return !isHardAllowedBlockInteraction(block);
    }

    public static boolean shouldPreventBlockInteraction(LivingEntity entity, @NotNull BlockState block) {
        return hasPaws(entity) && !isHardAllowedBlockInteraction(block);
    }

    public static boolean isHardAllowedBlockInteraction(@NotNull BlockState block) {
        Block b = block.getBlock();
        return b instanceof DoorBlock
                || b instanceof TrapDoorBlock
                || b instanceof ButtonBlock
                || b instanceof LeverBlock
                || b instanceof DogBedBlock
                || b instanceof DogBowlBlock;
    }

    public static boolean shouldDrop(ItemStack pawsStack, ItemStack thing) {
        return !thing.isEmpty();
    }

    @Override
    //? if >=1.21.5 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag type) {
        super.appendHoverText(stack, context, lines, type);
        Consumer<Component> tooltip = lines::add;
    *///?}
        tooltip.accept(Component.translatable("item.playercollars.paws.slippery"));
        tooltip.accept(Component.translatable("item.playercollars.paws.interaction"));
    }

    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, Ids.of(c.getName() + "_paws"));
    }
}
