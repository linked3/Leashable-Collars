package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import com.dipilodopilasaurus.leashablecollars.component.Components;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow @Final Inventory inventory;

    // 26.3 replaced drop's throwerName flag with a Prediction; a stale @Shadow fails at apply time.
    //? if >=26.3 {
    /*@Shadow @Nullable public abstract ItemEntity drop(ItemStack stack, boolean retainOwnership, net.minecraft.util.Prediction prediction);
    *///?} else {
    @Shadow @Nullable public abstract ItemEntity drop(ItemStack stack, boolean retainOwnership);
    //?}

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void playercollars$modifyPawDestroySpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        if (!PawsItem.hasPaws(this)) return;

        cir.setReturnValue(Math.max(cir.getReturnValue() * 0.1f, 0.05f));
    }

    //? if >=1.21 {
    @Redirect(method="attack", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal=0), require=0)
    private double getAttributeValue(Player instance, Holder<Attribute> registryEntry) {
        double ret = instance.getAttributeValue(registryEntry);
    //?} else {
    /*@Redirect(method="attack", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", ordinal=0), require=0)
    private double getAttributeValue(Player instance, Attribute registryEntry) {
        double ret = instance.getAttributeValue(registryEntry);
    *///?}
        if (!PawsItem.hasPaws(this)) return ret;
        return (ret - 1) * 0.75f + 1;
    }

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void playercollars$addAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        Compat.addAttribute(Compat.addAttribute(cir.getReturnValue(), PlayerCollarsMod.ATTR_LEASH_DISTANCE),
                PlayerCollarsMod.ATTR_CLICKER_DISTANCE);
    }

    @Inject(method = "aiStep", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;tick()V", shift = At.Shift.AFTER))
    private void playercollars$dropPawItems(CallbackInfo ci) {
        if (!FeatureRules.CAN_USE_PAWS.enabled(Compat.level(this))) return;
        if (Compat.level(this).isClientSide()) return;
        for (ItemStack pawsStack : EquippedAccessories.getEquipped(this, x -> x.is(PlayerCollarsMod.PAWS_TAG))) {
            if (PawsItem.shouldDrop(pawsStack, Compat.selectedItem(inventory))) {
                playercollars$moveHeldItemAway(Compat.selectedSlot(inventory));
            }
            if (PawsItem.shouldDrop(pawsStack, inventory.getItem(Inventory.SLOT_OFFHAND))) {
                playercollars$moveHeldItemAway(Inventory.SLOT_OFFHAND);
            }
        }
    }

    @Unique
    private void playercollars$moveHeldItemAway(int slot) {
        ItemStack stack = inventory.removeItemNoUpdate(slot);
        if (stack.isEmpty()) return;

        int selected = Compat.selectedSlot(inventory);
        int inventorySize = Math.min(36, inventory.getContainerSize());
        for (int i = 0; i < inventorySize; i++) {
            if (i == selected || i == Inventory.SLOT_OFFHAND || i == slot) continue;
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack);
                return;
            }
        }

        //? if >=26.3 {
        /*drop(stack, true, net.minecraft.util.Prediction.SERVER_ONLY);
        *///?} else {
        drop(stack, true);
        //?}
    }

    @Inject(method="updatePlayerPose", at= @At("TAIL"))
    private void playercollars$forceCrawl(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        Pose pose = player.getPose();

        if (player.getAbilities().flying || (pose != Pose.CROUCHING && pose != Pose.STANDING)) return;

        if (FeatureRules.CAN_USE_FOOT_PAWS.enabled(Compat.level(this))
                && EquippedAccessories.hasEquipped(this, x -> x.is(PlayerCollarsMod.FOOT_PAWS_TAG))) {
            player.setPose(Pose.SWIMMING);
            return;
        }

        for (ItemStack collarStack : EquippedAccessories.getEquipped(this, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
            if (Components.getOrDefault(collarStack, PlayerCollarsMod.FORCED_CRAWL_COMPONENT_TYPE, false)) {
                player.setPose(Pose.SWIMMING);
                return;
            }
        }
    }
}
