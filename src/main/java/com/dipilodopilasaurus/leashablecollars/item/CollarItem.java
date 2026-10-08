package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
import net.minecraft.world.entity.LivingEntity;
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
//? if >=1.19 {
import net.minecraft.world.level.material.MapColor;
//?} else {
/*import net.minecraft.world.level.material.MaterialColor;
*///?}
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.ClientHooks;
import com.dipilodopilasaurus.leashablecollars.DeedHolder;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.SignedDeeds;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Necklace slot eligibility comes from the equipment library's item tags. */
public class CollarItem extends WearableItem {
    //? if >=1.19 {
    public static final int DEFAULT_COLOR = MapColor.COLOR_RED.col;
    public static final int DEFAULT_PAW_COLOR = MapColor.COLOR_BLUE.col;
    //?} else {
    /*public static final int DEFAULT_COLOR = MaterialColor.COLOR_RED.col;
    public static final int DEFAULT_PAW_COLOR = MaterialColor.COLOR_BLUE.col;
    *///?}

    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("collar"));
    public static final ResourceKey<Item> TAGLESS_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("tagless_collar"));
    public final boolean tagless;

    public CollarItem(boolean tagless) {
        super(Components.withDefaultColors(
                Compat.enchantable(Registration.withId(new Item.Properties().stacksTo(1), tagless ? TAGLESS_REGISTRY_KEY : REGISTRY_KEY), 100),
                DEFAULT_COLOR, DEFAULT_PAW_COLOR));
        this.tagless = tagless;
    }

    public static int getColor(ItemStack itemStack) {
        return Components.dyeColor(itemStack, DEFAULT_COLOR | 0xFF000000);
    }

    public static int getPawColor(ItemStack itemStack) {
        return Components.pawColor(itemStack, DEFAULT_PAW_COLOR);
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        return doUse(p_41432_, p_41433_, p_41434_);
    }
    //?} else {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        //? if fabric && <1.19 {
        /^if (!p_41433_.isShiftKeyDown()) return super.use(p_41432_, p_41433_, p_41434_);
        ^///?}
        return Compat.useResult(doUse(p_41432_, p_41433_, p_41434_), p_41433_.getItemInHand(p_41434_));
    }
    *///?}

    private InteractionResult doUse(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack is = p_41433_.getItemInHand(p_41434_);
        if (p_41433_.isShiftKeyDown() && p_41432_.isClientSide()) {
            ClientHooks.openCollarDyeScreen(is, p_41433_.getUUID());
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }


    /** Shift-right-click a player to collar them; every refusal is spoken, since a silent no-op reads as a bug. */
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!user.isShiftKeyDown() || !(entity instanceof Player target) || target == user) return InteractionResult.PASS;
        if (Compat.level(user).isClientSide()) return InteractionResult.CONSUME;

        ServerLevel level = (ServerLevel) Compat.level(user);
        OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        Component refusal = checkForceEquip(level, owner, user, target);
        if (refusal != null) return refuse(user, refusal);

        ItemStack collar = Compat.copyWithCount(stack, 1);
        OwnerComponent rebound = rebind(level, owner, user, target);
        if (rebound != null) Components.set(collar, PlayerCollarsMod.OWNER_COMPONENT_TYPE, rebound);

        if (!EquippedAccessories.attemptToEquip(target, collar, "necklace")) {
            return refuse(user, Text.translatable("item.playercollars.collar.force_equip.no_slot", target.getName()));
        }

        if (!user.getAbilities().instabuild) stack.shrink(1);
        Compat.sendOverlayMessage(user, Text.translatable("item.playercollars.collar.force_equip.success_owner", target.getName()));
        Compat.sendOverlayMessage(target, Text.translatable("item.playercollars.collar.force_equip.success_owned", user.getName()));
        return InteractionResult.CONSUME;
    }

    /**
     * The collar decides who may be forced into it; the deed decides whether forcing is allowed at all.
     * @return the message to refuse with, or null to allow.
     */
    private static Component checkForceEquip(ServerLevel level, OwnerComponent owner, Player user, Player target) {
        if (owner != null && !owner.uuid().equals(user.getUUID())) {
            return Text.translatable("item.playercollars.collar.force_equip.not_owner");
        }
        if (owner != null && owner.owned().isPresent() && !owner.owned().get().equals(target.getUUID())) {
            return Text.translatable("item.playercollars.collar.force_equip.wrong_target");
        }
        // Claiming or assigning is itself a way in: the rule that allows it allows the equip.
        if (owner == null && !PlayerCollarsMod.FORCE_EQUIP_UNOWNED.get(level)
                && !PlayerCollarsMod.FORCE_EQUIP_CLAIMS_COLLAR.get(level)) {
            return Text.translatable("item.playercollars.collar.unowned");
        }
        if (owner != null && owner.owned().isEmpty() && !PlayerCollarsMod.FORCE_EQUIP_UNASSIGNED.get(level)
                && !PlayerCollarsMod.FORCE_EQUIP_ASSIGNS_OWNED.get(level)) {
            return Text.translatable("item.playercollars.collar.force_equip.unassigned");
        }
        return consented(level, owner, user, target)
                ? null : Text.translatable("item.playercollars.collar.force_equip.no_consent");
    }

    /** Whether {@code target} has agreed to be forcibly collared by {@code user}. See SignedDeeds. */
    private static boolean consented(ServerLevel level, OwnerComponent owner, Player user, Player target) {
        if (!PlayerCollarsMod.DEEDS_GLOBAL.get(level)) return boundConsent(owner, target);
        SignedDeeds.Entry deed = DeedHolder.between(user.getUUID(), target);
        // Deeds signed before this world recorded them globally live only on their collar; a later signing wins.
        return deed == null ? boundConsent(owner, target) : deed.canLeashForcibly();
    }

    private static boolean boundConsent(OwnerComponent owner, Player target) {
        return owner != null && owner.canLeashForcibly()
                && owner.owned().filter(target.getUUID()::equals).isPresent();
    }

    /** The owner tag the equipped copy should carry, or null; checkForceEquip already established consent. */
    private static OwnerComponent rebind(ServerLevel level, OwnerComponent owner, Player user, Player target) {
        boolean assign = PlayerCollarsMod.FORCE_EQUIP_ASSIGNS_OWNED.get(level);
        if (owner == null) {
            if (!PlayerCollarsMod.FORCE_EQUIP_CLAIMS_COLLAR.get(level)) return null;
            return assign
                    ? new OwnerComponent(user.getUUID(), user.getName().getString(),
                            Optional.of(target.getUUID()), Optional.of(target.getName().getString()), true)
                    : new OwnerComponent(user.getUUID(), user.getName().getString());
        }
        if (!assign || owner.owned().isPresent()) return null;
        return new OwnerComponent(owner.uuid(), owner.name(),
                Optional.of(target.getUUID()), Optional.of(target.getName().getString()), true);
    }

    private static InteractionResult refuse(Player user, Component message) {
        Compat.sendOverlayMessage(user, message.copy().withStyle(ChatFormatting.RED));
        return InteractionResult.CONSUME;
    }

    //? if <1.21.2 {
    /*@Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 100;
    }
    *///?}

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null && owner.ownedName().isPresent())
            return Text.translatable("item.playercollars.collar.named", owner.ownedName().get());
        return super.getName(stack);
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
        if (type.isAdvanced() && !tagless) {
            tooltip.accept(Text.translatable("item.playercollars.collar.paw_color", Integer.toHexString(getPawColor(stack))).setStyle(Style.EMPTY.withColor(0x808080)));
        }
        OwnerComponent owner = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null) {
            tooltip.accept(Text.translatable("item.playercollars.collar.owner", owner.name()).withStyle(ChatFormatting.GRAY));
        } else {
            // Nothing else in-game says leashing, locking and the clicker need a deeded collar.
            tooltip.accept(Text.translatable("item.playercollars.collar.unowned").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

}
