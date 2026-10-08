package com.dipilodopilasaurus.leashablecollars.enchant;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
//? if >=1.21 {
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
//?}

/** Vanilla only iterates EquipmentSlots, so a collar in an accessory slot never sees a tick effect. */
public final class AccessoryEnchantments {
    // Accessories folds worn accessories into vanilla's own iteration; Curios carries no equivalent.
    //? if fabric {
    private static final boolean LIBRARY_FEEDS = true;
    //?} else {
    /*private static final boolean LIBRARY_FEEDS = false;
    *///?}

    private AccessoryEnchantments() {
    }

    /** Vanilla ticks armour enchantments every tick; the effects throttle themselves. */
    public static void tick(ServerLevel world, LivingEntity wearer) {
        //? if >=1.21 {
        if (LIBRARY_FEEDS) return;
        for (ItemStack stack : collars(wearer)) {
            EnchantedItemInUse context = context(stack, wearer);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : stack.getEnchantments().entrySet()) {
                entry.getKey().value().tick(world, entry.getIntValue(), context, wearer);
            }
        }
        //?}
    }

    /** Call from the victim's damage hook: the collar is the victim's item, so it runs as VICTIM. */
    public static void postAttack(ServerLevel world, LivingEntity victim, DamageSource source) {
        //? if >=1.21 {
        if (LIBRARY_FEEDS) return;
        for (ItemStack stack : collars(victim)) {
            EnchantedItemInUse context = context(stack, victim);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : stack.getEnchantments().entrySet()) {
                entry.getKey().value().doPostAttack(
                        world, entry.getIntValue(), context, EnchantmentTarget.VICTIM, victim, source);
            }
        }
        //?}
    }

    //? if >=1.21 {
    private static Iterable<ItemStack> collars(LivingEntity wearer) {
        return EquippedAccessories.getEquipped(wearer, x -> x.is(PlayerCollarsMod.COLLAR_TAG));
    }

    // An accessory sits in no EquipmentSlot, and a null slot would NPE the default break handler.
    private static EnchantedItemInUse context(ItemStack stack, LivingEntity wearer) {
        return new EnchantedItemInUse(stack, null, wearer, item -> { /* accessories do not break */ });
    }
    //?}
}
