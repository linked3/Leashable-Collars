package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.21 {
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.OwnerComponent;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record RegenerationEnchantmentEffect(LevelBasedValue level) implements EnchantmentEntityEffect {
    /** Long enough to span several of vanilla's heal ticks; see {@link #apply}. */
    private static final int DURATION = 100;

    public static final MapCodec<RegenerationEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                LevelBasedValue.CODEC.fieldOf("level").forGetter(RegenerationEnchantmentEffect::level)
            ).apply(instance, RegenerationEnchantmentEffect::new));

    /** Regeneration only heals on a duration multiple, so re-applying early never heals -- top up at expiry. */
    @Override
    public void apply(ServerLevel world, int enchantmentLevel, EnchantedItemInUse context, Entity user, Vec3 pos) {
        LivingEntity wearer = context.owner();
        if (wearer == null) return;
        MobEffectInstance active = wearer.getEffect(MobEffects.REGENERATION);
        if (active != null && active.getDuration() > 20) return;

        int amplifier = Math.max(0, (int) this.level.calculate(enchantmentLevel));
        for (ItemStack stack : EquippedAccessories.getEquipped(wearer, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
            OwnerComponent oc = Components.get(stack, PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            if (oc != null) {
                Player own = world.getPlayerByUUID(oc.uuid());
                if (own != null && own.distanceTo(wearer) < 16) {
                    wearer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, DURATION, amplifier, false, false, false));
                    return;
                }
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
//?}
