package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Compat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
//? if <26.1 && >=1.21.5 {
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GroomingBrushItem extends Item {
    public GroomingBrushItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!Compat.level(user).isClientSide() && entity instanceof Player pet) {
            ((ServerLevel) Compat.level(user)).sendParticles(
                    ParticleTypes.HEART,
                    pet.getX(), pet.getY() + 1.0, pet.getZ(),
                    5, 0.3, 0.3, 0.3, 0.0
            );


            // 26.1 promoted the wolf pant to a plain SoundEvents field; on 1.21.x it is in WOLF_SOUNDS.
            //? if >=26.1 {
            /*SoundEvent pant = Compat.sound(SoundEvents.WOLF_PANT_BABY);
            *///?} elif >=1.21.5 {
            SoundEvent pant = SoundEvents.WOLF_SOUNDS.get(WolfSoundVariants.SoundSet.CLASSIC).pantSound().value();
            //?} else {
            /*SoundEvent pant = Compat.sound(SoundEvents.WOLF_PANT);
            *///?}
            Compat.level(user).playSound(null, pet.blockPosition(), pant, SoundSource.PLAYERS, 1.0f, 1.2f);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
