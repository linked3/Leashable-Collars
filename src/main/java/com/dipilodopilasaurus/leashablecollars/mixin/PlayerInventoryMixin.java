package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.inventory.InventoryLockManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** A pinned slot cannot be dropped or swapped away, only unpinned by the owner. */
@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void playercollars$preventDroppingLockedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        if (InventoryLockManager.isLocked(player, Compat.selectedSlot((Inventory) (Object) this))) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "pickSlot", at = @At("HEAD"), cancellable = true)
    private void playercollars$preventSwappingLockedItem(int slot, CallbackInfo ci) {
        int selected = Compat.selectedSlot((Inventory) (Object) this);
        if (InventoryLockManager.isLocked(player, slot) || InventoryLockManager.isLocked(player, selected)) {
            ci.cancel();
        }
    }
}
