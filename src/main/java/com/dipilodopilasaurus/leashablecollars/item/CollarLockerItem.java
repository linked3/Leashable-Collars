package com.dipilodopilasaurus.leashablecollars.item;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class CollarLockerItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("collar_locker"));
    public CollarLockerItem() {
        super(Registration.withId(new Properties().stacksTo(1), REGISTRY_KEY));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof Player player) || user.level().isClientSide()) return InteractionResult.PASS;

        ItemStack collarStack = EquippedAccessories.findOwned(player, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG), user.getUUID(), player.getUUID());
        if (collarStack == null) {
            Compat.sendOverlayMessage(user, Component.translatable("item.playercollars.collar_locker.no_set_non_owner").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (collarStack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE).owned().isEmpty()) {
            Compat.sendOverlayMessage(user, Component.translatable("item.playercollars.collar_locker.no_set_non_deed").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        Holder<Enchantment> binding = ((ServerPlayer) user).level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
        boolean shouldLock = !EnchantmentHelper.has(collarStack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        List<EquippedAccessories.EquippedEntry> ls = EquippedAccessories.getEquippedStacks(player,
                (y) -> y.is(PlayerCollarsMod.COLLAR_TAG) ||
                        y.is(PlayerCollarsMod.PAWS_TAG) ||
                        y.is(PlayerCollarsMod.FOOT_PAWS_TAG)
        );

        for (EquippedAccessories.EquippedEntry p : ls) {
            ItemStack is = p.stack();
            if (!is.isEnchanted()) {
                if (shouldLock) {
                    ItemEnchantments.Mutable ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    ench.upgrade(binding, 1);
                    EnchantmentHelper.setEnchantments(is, ench.toImmutable());
                }
                continue;
            }
            EnchantmentHelper.updateEnchantments(is, (ench) -> {
                if (shouldLock) ench.upgrade(binding, 1);
                else ench.removeIf((e) -> e.value().effects().has(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE));
            });
        }
        Compat.sendOverlayMessage(player, Component.translatable(shouldLock ? "item.playercollars.collar_locker.locked" : "item.playercollars.collar_locker.unlocked"));
        Compat.sendOverlayMessage(user, Component.translatable(shouldLock ? "item.playercollars.collar_locker.locked" : "item.playercollars.collar_locker.unlocked"));
        player.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), shouldLock ? Compat.sound(SoundEvents.ARMOR_EQUIP_WOLF) : SoundEvents.ARMOR_UNEQUIP_WOLF, SoundSource.PLAYERS);

        return InteractionResult.SUCCESS;
    }
}
