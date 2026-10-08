package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

import java.util.List;

import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CollarLockerItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("collar_locker"));

    /** Binding level 1 is this locker, 2 is the Diamond one; a level-2 lock refuses level-1 keys. */
    private final int tier;
    private final String translationPrefix;

    public CollarLockerItem() {
        this(REGISTRY_KEY, 1);
    }

    protected CollarLockerItem(ResourceKey<Item> registryKey, int tier) {
        super(Registration.withId(new Properties().stacksTo(1), registryKey));
        this.tier = tier;
        this.translationPrefix = "item.playercollars." + Ids.of(registryKey).getPath() + ".";
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof Player player) || Compat.level(user).isClientSide()) return InteractionResult.PASS;

        ItemStack collarStack = EquippedAccessories.findOwned(player, x -> x.is(PlayerCollarsMod.COLLAR_TAG), user.getUUID(), player.getUUID());
        if (collarStack == null) {
            Compat.sendOverlayMessage(user, refusal("no_set_non_owner"));
            return InteractionResult.FAIL;
        }
        if (Components.get(collarStack, PlayerCollarsMod.OWNER_COMPONENT_TYPE).owned().isEmpty()
                && !PlayerCollarsMod.LOCK_UNASSIGNED.get((ServerLevel) Compat.level(user))) {
            Compat.sendOverlayMessage(user, refusal("no_set_non_deed"));
            return InteractionResult.FAIL;
        }
        if (tier < 2 && PlayerCollarsMod.isDiamondLocked(collarStack)) {
            Compat.sendOverlayMessage(user, refusal("diamond_locked"));
            return InteractionResult.FAIL;
        }

        RegistryAccess registries = Compat.level(user).registryAccess();
        boolean shouldLock = tier >= 2 ? !PlayerCollarsMod.isDiamondLocked(collarStack) : !Enchants.preventsArmorChange(collarStack);
        List<EquippedAccessories.EquippedEntry> ls = EquippedAccessories.getEquippedStacks(player,
                (y) -> y.is(PlayerCollarsMod.COLLAR_TAG) ||
                        y.is(PlayerCollarsMod.PAWS_TAG) ||
                        y.is(PlayerCollarsMod.FOOT_PAWS_TAG)
        );

        for (EquippedAccessories.EquippedEntry p : ls) {
            if (tier >= 2) {
                if (shouldLock) Components.set(p.stack(), PlayerCollarsMod.DIAMOND_LOCKED_COMPONENT_TYPE, true);
                else Components.remove(p.stack(), PlayerCollarsMod.DIAMOND_LOCKED_COMPONENT_TYPE);
            }
            Enchants.setPreventArmorChange(p.stack(), registries, shouldLock, tier);
        }
        Component message = Text.translatable(translationPrefix + (shouldLock ? "locked" : "unlocked"));
        Compat.sendOverlayMessage(player, message);
        Compat.sendOverlayMessage(user, message);
        Compat.level(player).playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(),
                shouldLock ? Compat.armorEquipSound() : Compat.armorUnequipSound(), SoundSource.PLAYERS, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }

    private Component refusal(String key) {
        return Text.translatable(translationPrefix + key).withStyle(ChatFormatting.RED);
    }
}
