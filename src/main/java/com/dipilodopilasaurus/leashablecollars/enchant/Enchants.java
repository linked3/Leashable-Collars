package com.dipilodopilasaurus.leashablecollars.enchant;

//? if >=1.19.3 && <1.21 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?} elif <1.19.3 {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

//? if >=1.21 {
import net.minecraft.core.registries.Registries;
//?}

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.resources.ResourceKey;
//? if >=1.21 {
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
*///?}

/** Era seam for enchantments: below 1.21 code-declared {@link CollarEnchantment}s answer the same questions. */
public final class Enchants {
    private Enchants() {
    }

    /** Curse of Binding is what the locker applies and the golden spatula strips. */
    public static boolean preventsArmorChange(ItemStack stack) {
        //? if >=1.21 {
        return EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        //?} else {
        /*return EnchantmentHelper.hasBindingCurse(stack);
        *///?}
    }

    /** See {@link #preventsArmorChange}. Unlocking strips every enchantment carrying that effect. */
    public static void setPreventArmorChange(ItemStack stack, RegistryAccess registries, boolean locked) {
        setPreventArmorChange(stack, registries, locked, 1);
    }

    /** {@code tier} is the binding level the lock writes: 1 for the Lock-inator, 2 for the Diamond one. */
    public static void setPreventArmorChange(ItemStack stack, RegistryAccess registries, boolean locked, int tier) {
        //? if >=1.21 {
        Holder<Enchantment> binding = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
        if (!stack.isEnchanted()) {
            if (!locked) return;
            ItemEnchantments.Mutable added = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            added.upgrade(binding, tier);
            EnchantmentHelper.setEnchantments(stack, added.toImmutable());
            return;
        }
        EnchantmentHelper.updateEnchantments(stack, ench -> {
            if (locked) ench.upgrade(binding, tier);
            else ench.removeIf(e -> e.value().effects().has(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE));
        });
        //?} else {
        /*// enchant() appends rather than upgrading, so the old entry goes first either way.
        removeBinding(stack);
        if (locked) stack.enchant(Enchantments.BINDING_CURSE, tier);
        *///?}
    }

    //? if <1.21 {
    /*private static void removeBinding(ItemStack stack) {
        ResourceLocation binding = BuiltInRegistries.ENCHANTMENT.getKey(Enchantments.BINDING_CURSE);
        ListTag tags = stack.getEnchantmentTags();
        tags.removeIf(tag -> tag instanceof CompoundTag entry && binding.equals(EnchantmentHelper.getEnchantmentId(entry)));
    }
    *///?}

    /** Level of one of this mod's own enchantments, keyed by id so both eras find the same entry. */
    public static int level(RegistryAccess registries, ResourceKey<Enchantment> key, ItemStack stack) {
        //? if >=1.21 {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).get(key)
                .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack))
                .orElse(0);
        //?} else {
        /*Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(key.location());
        return enchantment == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
        *///?}
    }
}
