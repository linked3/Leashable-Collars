package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;
//?} else {
/*import java.util.List;
*///?}
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import com.dipilodopilasaurus.leashablecollars.ClientHooks;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

public class DeedItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("deed_of_ownership"));

    public DeedItem() {
        super(Registration.withId(new Properties().stacksTo(1), REGISTRY_KEY));
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        return doUse(p_41432_, p_41433_, p_41434_);
    }
    //?} else {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        return Compat.useResult(doUse(p_41432_, p_41433_, p_41434_), p_41433_.getItemInHand(p_41434_));
    }
    *///?}

    private InteractionResult doUse(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack is = p_41433_.getItemInHand(p_41434_);
        if (p_41432_.isClientSide()) {
            OwnerComponent owner = Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            if (owner != null && owner.owned().isEmpty()) {
                if (owner.uuid().equals(p_41433_.getUUID())) {
                    Compat.sendOverlayMessage(p_41433_, Text.translatable("item.playercollars.deed_of_ownership.no_self_own"));
                    return InteractionResult.PASS;
                }
                ClientHooks.openDeedScreen(is, p_41433_);
                return InteractionResult.CONSUME;
            }
        } else if (Components.get(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE) == null) {
            Components.set(is, PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(p_41433_.getUUID(), p_41433_.getName().getString()));
            Compat.sendOverlayMessage(p_41433_, Text.translatable("item.playercollars.deed_of_ownership.filled_out"));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE) != null)
            return Text.translatable("item.playercollars.deed_of_ownership.filled");
        return super.getName(stack);
    }

    /** The deed is the only way to bind a collar and nothing in-game said so, hence the tooltip. */
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
        boolean signed = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE) != null;
        tooltip.accept(Text.translatable(signed
                ? "item.playercollars.deed_of_ownership.tip_filled"
                : "item.playercollars.deed_of_ownership.tip_blank").withStyle(ChatFormatting.DARK_GRAY));
    }
}
