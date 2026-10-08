package com.dipilodopilasaurus.leashablecollars.mixin;

//? if >=1.19.3 && <1.21 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?} elif <1.19.3 {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21 {
/*import com.dipilodopilasaurus.leashablecollars.enchant.CollarEnchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
*///?}

/** No EnchantmentCategory accepts a collar, clicker or laser; from 1.21 supported_items says it instead. */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    //? if <1.21 {
    /*@Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void playercollars$offerCollarEnchantments(int cost, ItemStack stack, boolean treasure,
                                                              CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> results = cir.getReturnValue();
        if (results == null || stack.isEmpty()) return;

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if (!(enchantment instanceof CollarEnchantment collar) || !collar.canEnchant(stack)) continue;
            if (enchantment.isTreasureOnly() && !treasure) continue;
            for (int level = enchantment.getMaxLevel(); level >= enchantment.getMinLevel(); level--) {
                if (cost >= enchantment.getMinCost(level) && cost <= enchantment.getMaxCost(level)) {
                    results.add(new EnchantmentInstance(enchantment, level));
                    break;
                }
            }
        }
    }
    *///?}
}
