package com.dipilodopilasaurus.leashablecollars.enchant;

//? if <1.21 {
/*import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/^*
 * Below 1.21 enchantments are code, not data, so the ones this mod ships in
 * {@code data/playercollars/enchantment/} are declared here instead. Ids must match either way.
 ^/
public class CollarEnchantment extends Enchantment {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS,
            EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    private final int maxLevel;
    private final int minCost;
    private final int minStep;
    private final int maxCost;
    private final int maxStep;
    private final Predicate<ItemStack> supported;

    public CollarEnchantment(Rarity rarity, int maxLevel, int minCost, int minStep, int maxCost, int maxStep) {
        this(rarity, maxLevel, minCost, minStep, maxCost, maxStep, stack -> stack.is(PlayerCollarsMod.COLLAR_TAG));
    }

    // supported stands in for the JSON's supported_items and the cost pairs for its min_cost/max_cost;
    // vanilla's default window is five levels wide, which leaves most table rolls offering nothing.
    public CollarEnchantment(Rarity rarity, int maxLevel, int minCost, int minStep, int maxCost, int maxStep,
                             Predicate<ItemStack> supported) {
        super(rarity, EnchantmentCategory.WEARABLE, SLOTS);
        this.maxLevel = maxLevel;
        this.minCost = minCost;
        this.minStep = minStep;
        this.maxCost = maxCost;
        this.maxStep = maxStep;
        this.supported = supported;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getMinCost(int level) {
        return minCost + (level - 1) * minStep;
    }

    @Override
    public int getMaxCost(int level) {
        return maxCost + (level - 1) * maxStep;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return supported.test(stack);
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }
}
*///?}
