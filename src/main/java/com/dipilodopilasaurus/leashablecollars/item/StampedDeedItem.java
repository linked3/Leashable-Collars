package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
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
import java.util.function.Consumer;

public class StampedDeedItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("stamped_deed_of_ownership"));

    public StampedDeedItem() {
        super(Registration.withId(new Item.Properties().stacksTo(1), REGISTRY_KEY));
    }

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner == null || owner.ownedName().isEmpty()) return Component.translatable("item.playercollars.stamped_deed_of_ownership.invalid");
        return Component.translatable("item.playercollars.stamped_deed_of_ownership", owner.ownedName().get());
    }


    public ItemStack getRecipeRemainder(ItemStack stack) {
        return stack.copy();
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
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null) {
            tooltip.accept(Component.translatable("item.playercollars.collar.owner", owner.name()).withStyle(ChatFormatting.GRAY));
        }
        // The least guessable step, and the deed survives the craft, so it is still here to say so.
        tooltip.accept(Component.translatable("item.playercollars.stamped_deed_of_ownership.tip").withStyle(ChatFormatting.DARK_GRAY));
    }
}
