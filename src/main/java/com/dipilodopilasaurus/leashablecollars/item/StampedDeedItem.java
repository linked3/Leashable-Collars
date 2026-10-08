package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import java.util.function.Consumer;

public class StampedDeedItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("stamped_deed_of_ownership"));

    public StampedDeedItem() {
        super(Registration.withId(new Item.Properties().stacksTo(1), REGISTRY_KEY));
    }

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner == null || owner.ownedName().isEmpty()) return Text.translatable("item.playercollars.stamped_deed_of_ownership.invalid");
        return Text.translatable("item.playercollars.stamped_deed_of_ownership", owner.ownedName().get());
    }


    public ItemStack getRecipeRemainder(ItemStack stack) {
        return stack.copy();
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
        OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null) {
            tooltip.accept(Text.translatable("item.playercollars.collar.owner", owner.name()).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Text.translatable("item.playercollars.deed_of_ownership.can_leash_forcibly",
                    Text.translatable(owner.canLeashForcibly() ? "gui.yes" : "gui.no")).withStyle(ChatFormatting.GRAY));
        }
        // The least guessable step, and the deed survives the craft, so it is still here to say so.
        tooltip.accept(Text.translatable("item.playercollars.stamped_deed_of_ownership.tip").withStyle(ChatFormatting.DARK_GRAY));
    }
}
