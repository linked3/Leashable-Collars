package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;


import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;

public class SpatulaItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("golden_spatula"));
    public SpatulaItem() {
        super(Registration.withId(new Item.Properties().stacksTo(1).durability(8), REGISTRY_KEY));
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        return doUse(world, user, hand);
    }
    //?} else {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return Compat.useResult(doUse(world, user, hand), user.getItemInHand(hand));
    }
    *///?}

    private InteractionResult doUse(Level world, Player user, InteractionHand hand) {
        if (user.isShiftKeyDown()) {
            InteractionResult res = interactLivingEntity(user.getItemInHand(hand), user, user, hand);
            if (res.consumesAction()) return InteractionResult.SUCCESS;
        }
        // Not consumable, so Item.use is a bare PASS on every version in the matrix.
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (Compat.level(entity).isClientSide()) return InteractionResult.SUCCESS;

        ServerLevel world = (ServerLevel) Compat.level(entity);

        int count = 0;
        int diamondLocked = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            //? if >=1.20 {
            if (!slot.isArmor()) continue;
            //?} else {
            /*if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
            *///?}
            ItemStack is = entity.getItemBySlot(slot);
            if (!Enchants.preventsArmorChange(is)) continue;
            if (PlayerCollarsMod.isDiamondLocked(is)) {
                diamondLocked++;
                continue;
            }
            entity.setItemSlot(slot, ItemStack.EMPTY);
            Compat.spawnAtLocation(entity, world, is);
            count++;
        }

        for (EquippedAccessories.EquippedEntry p : EquippedAccessories.getAllEquippedStacks(entity)) {
            if (!Enchants.preventsArmorChange(p.stack())) continue;
            if (PlayerCollarsMod.isDiamondLocked(p.stack())) {
                diamondLocked++;
                continue;
            }
            ItemStack trinketStack = p.stack();
            if (p.setStack(ItemStack.EMPTY)) {
                Compat.spawnAtLocation(entity, world, trinketStack);
                count++;
            }
        }

        if (diamondLocked > 0) {
            Compat.sendOverlayMessage(user, Text.translatable("item.playercollars.golden_spatula.diamond_locked")
                    .withStyle(ChatFormatting.AQUA));
        }
        if (count == 0) return diamondLocked > 0 ? InteractionResult.FAIL : InteractionResult.PASS;
        Compat.hurtAndBreak(stack, count, user, hand);
        Compat.makeSound(entity, Compat.armorBreakSound());
        return InteractionResult.SUCCESS;
    }
}
