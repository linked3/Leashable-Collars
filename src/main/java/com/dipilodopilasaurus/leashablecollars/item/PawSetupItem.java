package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.component.Components;

import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.network.Net;
import net.minecraft.ChatFormatting;

import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.Level;
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.network.PacketOpenPetControl;

public class PawSetupItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("paw_configurator"));

    public PawSetupItem() {
        super(Registration.withId(new Properties().stacksTo(1), REGISTRY_KEY));
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
        ItemStack is = user.getItemInHand(hand);
        if (!user.isShiftKeyDown()) return InteractionResult.PASS;
        return interactLivingEntity(is, user, user, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!(entity instanceof Player player)) return InteractionResult.PASS;

        if (Compat.level(user).isClientSide()) return InteractionResult.SUCCESS;
        if (!(user instanceof ServerPlayer owner) || !(player instanceof ServerPlayer pet)) return InteractionResult.FAIL;

        PetControlHelper.ValidationResult result = PetControlHelper.validateOwnerControl(owner, pet);
        if (!result.successful()) {
            if (result.failure() != null) Compat.sendOverlayMessage(owner, result.failure().message());
            return InteractionResult.FAIL;
        }

        if (user.isShiftKeyDown()) {
            ItemStack collarStack = result.activeCollar().collar();
            boolean isCrawling = Components.getOrDefault(collarStack, PlayerCollarsMod.FORCED_CRAWL_COMPONENT_TYPE, false);
            Components.set(collarStack, PlayerCollarsMod.FORCED_CRAWL_COMPONENT_TYPE, !isCrawling);
            EquippedAccessories.markChanged(pet, collarStack);
            Compat.sendOverlayMessage(user, Text.translatable(
                    !isCrawling
                            ? "message.playercollars.pet_control.forced_crawl.enabled"
                            : "message.playercollars.pet_control.forced_crawl.disabled"
            ).withStyle(ChatFormatting.LIGHT_PURPLE));
            return InteractionResult.SUCCESS;
        } else {
            Net.sendToClient(owner, PacketOpenPetControl.from(pet, result.activeCollar().collar()));
            return InteractionResult.SUCCESS;
        }
    }
}
