package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PetControlHelper;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.inventory.InventoryEditorMenu;
import net.minecraft.ChatFormatting;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Shift-right-click an owned player to open their main inventory and pin slots shut. */
public class InventoryEditorItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("inventory_editor"));

    public InventoryEditorItem() {
        super(Registration.withId(new Properties().stacksTo(1), REGISTRY_KEY));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (!FeatureRules.CAN_ACCESS_OWNED_INVENTORY.enabled(Compat.level(user))) return InteractionResult.PASS;
        if (!user.isShiftKeyDown() || !(entity instanceof Player)) return InteractionResult.PASS;
        if (Compat.level(user).isClientSide()) return InteractionResult.SUCCESS;
        if (!(user instanceof ServerPlayer owner) || !(entity instanceof ServerPlayer pet)) return InteractionResult.FAIL;

        PetControlHelper.ValidationResult result = PetControlHelper.validateOwnerControl(owner, pet);
        if (!result.successful()) {
            Compat.sendOverlayMessage(owner, Text.translatable("item.playercollars.inventory_editor.no_set_non_owner")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        owner.openMenu(new SimpleMenuProvider(
                (syncId, inventory, player) -> new InventoryEditorMenu(syncId, inventory, pet),
                Text.translatable("gui.playercollars.inventory_editor.title", pet.getName())));
        return InteractionResult.SUCCESS;
    }
}
