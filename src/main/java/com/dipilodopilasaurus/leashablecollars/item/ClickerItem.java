package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.network.Net;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.enchantment.Enchantment;
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;
//?} else {
/*import java.util.List;
*///?}
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.network.PacketLookAtLerped;

import java.util.List;
import java.util.function.Consumer;

public class ClickerItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("clicker"));
    public static final int DEFAULT_COLOR = 0xFFFFFF;
    public static final ResourceKey<Enchantment> BECKON_KEY = ResourceKey.create(Registries.ENCHANTMENT, Ids.of("clicker_walk"));
    //? if <1.21 {
    /*private static final ResourceKey<Enchantment> AUDIBLE_KEY = ResourceKey.create(Registries.ENCHANTMENT, Ids.of("clicker"));
    // add_multiplied_base against these, per clicker.json. No attribute modifier below 1.21 carries an
    // enchantment's level, so the multiply happens at the one place that reads the attribute.
    private static final int[] AUDIBLE_MULTIPLIERS = {1, 3, 7};
    *///?}
    public ClickerItem() {
        super(Compat.enchantable(Registration.withId(new Item.Properties().stacksTo(1), REGISTRY_KEY), 45));
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
        if (!FeatureRules.CAN_USE_CLICKERS.enabled(p_41432_)) return InteractionResult.PASS;
        p_41433_.startUsingItem(p_41434_);
        if (!p_41432_.isClientSide()) {
            ItemStack is = p_41433_.getItemInHand(p_41434_);
            // With the toggle rule off the clicker has no off switch, so force-turn reads as always on.
            boolean toggleable = PlayerCollarsMod.CLICKER_TURN_TOGGLE.get((ServerLevel) p_41432_);
            if (p_41433_.isShiftKeyDown()) {
                if (!toggleable) return InteractionResult.CONSUME;
                if (Components.forceTurn(is)) {
                    Components.setForceTurn(is, false);
                    Compat.sendOverlayMessage(p_41433_, Text.translatable("item.playercollars.clicker.turn_disable"));
                } else {
                    Components.setForceTurn(is, true);
                    Compat.sendOverlayMessage(p_41433_, Text.translatable("item.playercollars.clicker.turn_enable"));
                }
                return InteractionResult.CONSUME;
            }

            double distance = Compat.attributeValue(p_41433_, PlayerCollarsMod.ATTR_CLICKER_DISTANCE) * audibleMultiplier(p_41432_, is);
            boolean turn = !toggleable || Components.forceTurn(is);
            int beckon = Enchants.level(p_41432_.registryAccess(), BECKON_KEY, is);
            if (distance > 0 && (turn || beckon > 0)) {
                List<ServerPlayer> plrs = ((ServerLevel) p_41432_).getPlayers((p) -> !p.is(p_41433_) && p.closerThan(p_41433_, distance));
                PacketLookAtLerped packet = new PacketLookAtLerped(p_41433_);
                for (ServerPlayer p : plrs) {
                    ItemStack collar = EquippedAccessories.findOwned(p, x -> x.is(PlayerCollarsMod.COLLAR_TAG), p_41433_.getUUID(), p.getUUID());
                    if (collar == null) continue;
                    if (turn) {
                        Net.sendToClient(p, packet);
                    }
                    if (beckon > 0) {
                        beckon(p, p_41433_, beckon, distance);
                    }
                }
            }
            p_41432_.playSound(null, p_41433_, PlayerCollarsMod.CLICKER_ON, SoundSource.PLAYERS, 1, 1);
        }
        return InteractionResult.FAIL;
    }


    /** Above 1.21 clicker.json's attribute modifier is already applied by the time this reads it. */
    private static double audibleMultiplier(Level level, ItemStack stack) {
        //? if >=1.21 {
        return 1;
        //?} else {
        /*int audible = Enchants.level(level.registryAccess(), AUDIBLE_KEY, stack);
        return audible <= 0 ? 1 : 1 + AUDIBLE_MULTIPLIERS[Math.min(audible, AUDIBLE_MULTIPLIERS.length) - 1];
        *///?}
    }

    // Flat, not Y-tracking: the leash tick avoids the vertical axis for the same reason.
    private static void beckon(ServerPlayer owned, Player owner, int level, double range) {
        double strength = 0.15 * level;
        PlayerCollarsMod.pullPlayerTowards(owned, new Vec3(owner.getX(), owned.getY(), owner.getZ()),
                2, range, x -> strength / x);
    }

    //? if <1.21.2 {
    /*@Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 45;
    }
    *///?}

    @Override
    //? if >=1.20.5 {
    public int getUseDuration(ItemStack p_41454_, LivingEntity user) {
    //?} else {
    /*public int getUseDuration(ItemStack p_41454_) {
    *///?}
        return Integer.MAX_VALUE;
    }

    //? if >=1.21.2 {
    @Override
    public boolean releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        doReleaseUsing(p_41413_, p_41414_);
        return false;
    }
    //?} else {
    /*@Override
    public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        doReleaseUsing(p_41413_, p_41414_);
    }
    *///?}

    private void doReleaseUsing(Level level, LivingEntity user) {
        if (!level.isClientSide()) {
            level.playSound(null, user, PlayerCollarsMod.CLICKER_OFF, SoundSource.PLAYERS, 1, 1);
        }
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
        if (Components.forceTurn(stack))
            tooltip.accept(Text.translatable("item.playercollars.clicker.turn"));
    }
}
