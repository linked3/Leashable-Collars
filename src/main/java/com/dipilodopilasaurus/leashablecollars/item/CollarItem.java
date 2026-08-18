package com.dipilodopilasaurus.leashablecollars.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.MapItemColor;
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;
//?} else {
/*import java.util.List;
*///?}
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.ClientHooks;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import java.util.function.Consumer;

/** The necklace slot comes from a data tag now, not the code-declared component Trinkets used. See {@link WearableItem}. */
public class CollarItem extends WearableItem {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("collar"));
    public static final ResourceKey<Item> TAGLESS_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("tagless_collar"));
    public final boolean tagless;

    public CollarItem(boolean tagless) {
        super(Registration.withId(new Item.Properties().stacksTo(1), tagless ? TAGLESS_REGISTRY_KEY : REGISTRY_KEY)
                .component(DataComponents.ENCHANTABLE, new Enchantable(100))
                .component(DataComponents.DYED_COLOR, Compat.dyedColor(MapColor.COLOR_RED.col))
                .component(DataComponents.MAP_COLOR, new MapItemColor(MapColor.COLOR_BLUE.col)));
        this.tagless = tagless;
    }

    public static int getColor(ItemStack itemStack) {
        DyedItemColor $$1 = itemStack.get(DataComponents.DYED_COLOR);
        return $$1 != null ? $$1.rgb() : MapColor.COLOR_RED.col | 0xFF000000;
    }

    public static int getPawColor(ItemStack itemStack) {
        MapItemColor $$1 = itemStack.get(DataComponents.MAP_COLOR);
        return $$1 != null ? $$1.rgb() : MapColor.COLOR_BLUE.col;
    }

    @Override
    public InteractionResult use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack is = p_41433_.getItemInHand(p_41434_);
        if (p_41433_.isShiftKeyDown() && p_41432_.isClientSide()) {
            ClientHooks.openCollarDyeScreen(is, p_41433_.getUUID());
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null && owner.ownedName().isPresent())
            return Component.translatable("item.playercollars.collar.named", owner.ownedName().get());
        return super.getName(stack);
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
        if (type.isAdvanced() && !tagless) {
            tooltip.accept(Component.translatable("item.playercollars.collar.paw_color", Integer.toHexString(getPawColor(stack))).setStyle(Style.EMPTY.withColor(CommonColors.GRAY)));
        }
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null) {
            tooltip.accept(Component.translatable("item.playercollars.collar.owner", owner.name()).withStyle(ChatFormatting.GRAY));
        } else {
            // Nothing else in-game says leashing, locking and the clicker all need a deeded collar,
            // so an unowned one just looks broken.
            tooltip.accept(Component.translatable("item.playercollars.collar.unowned").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

}
