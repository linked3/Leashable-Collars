package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.network.Net;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.network.PacketLookAtLerped;

import java.util.List;

public class LaserPointerItem extends Item {
    public static final ResourceKey<Enchantment> LASER_REACH_KEY = ResourceKey.create(Registries.ENCHANTMENT, Ids.of("laser_reach"));
    public static final int DEFAULT_COLOR = 0x1EFF00;

    private static final int USE_TICKS = 72000;
    private static final int AIM_INTERVAL = 2;

    public LaserPointerItem(Properties settings) {
        super(Compat.enchantable(settings, 15));
    }


    //? if <1.21.2 {
    /*@Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }
    *///?}

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        return doUse(user, hand);
    }
    //?} else {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return Compat.useResult(doUse(user, hand), user.getItemInHand(hand));
    }
    *///?}

    //? if >=1.21 {
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return USE_TICKS;
    }
    //?} else {
    /*@Override
    public int getUseDuration(ItemStack stack) {
        return USE_TICKS;
    }
    *///?}

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remaining) {
        if (world.isClientSide() || remaining % AIM_INTERVAL != 0 || !(user instanceof Player player)) return;
        aim(world, player, stack);
    }

    // FAIL, not CONSUME: a success result makes the client run the reequip dip on every press.
    private InteractionResult doUse(Player user, InteractionHand hand) {
        user.startUsingItem(hand);
        return InteractionResult.FAIL;
    }

    // Collared players follow the dot for as long as the beam is held on it.
    private void aim(Level world, Player user, ItemStack stack) {
        double maxDistance = 32.0 * (1.0 + Enchants.level(world.registryAccess(), LASER_REACH_KEY, stack));
        HitResult hit = user.pick(maxDistance, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        Vec3 targetPos = hit.getLocation();
        // Same bound as the raycast, so Laser Reach extends who can be aimed as well as how far.
        double maxRangeSqr = maxDistance * maxDistance;
        List<ServerPlayer> plrs = ((ServerLevel) world)
                .getPlayers(p -> !p.is(user) && p.distanceToSqr(user) <= maxRangeSqr);
        PacketLookAtLerped packet = new PacketLookAtLerped(targetPos.x, targetPos.y, targetPos.z);

        for (ServerPlayer p : plrs) {
            ItemStack collar = EquippedAccessories.findOwned(p, x -> x.is(PlayerCollarsMod.COLLAR_TAG), user.getUUID(), p.getUUID());
            if (collar != null) {
                Net.sendToClient(p, packet);
            }
        }
    }
}
