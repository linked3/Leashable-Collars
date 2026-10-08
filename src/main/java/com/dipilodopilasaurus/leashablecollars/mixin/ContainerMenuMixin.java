package com.dipilodopilasaurus.leashablecollars.mixin;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.inventory.InventoryLockManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//? if >=26.1 {
/*import net.minecraft.world.inventory.ContainerInput;
*///?} else
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Blocks every route a pinned slot could leave by: dragging it, or hotbar-swapping into it. */
@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    //? if >=26.1 {
    /*private void playercollars$preventLockedInventoryActions(int slotIndex, int button, ContainerInput actionType, Player player, CallbackInfo ci) {
    *///?} else
    private void playercollars$preventLockedInventoryActions(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (slotIndex >= 0 && slotIndex < menu.slots.size()) {
            Slot slot = menu.getSlot(slotIndex);
            if (slot.container instanceof Inventory inventory && inventory.player == player
                    && InventoryLockManager.isLocked(player, slot.getContainerSlot())) {
                if (!Compat.level(player).isClientSide()) menu.broadcastChanges();
                ci.cancel();
                return;
            }
        }

        //? if >=26.1 {
        /*if (actionType == ContainerInput.SWAP && button >= 0 && button < 9 && InventoryLockManager.isLocked(player, button)) {
        *///?} else
        if (actionType == ClickType.SWAP && button >= 0 && button < 9 && InventoryLockManager.isLocked(player, button)) {
            if (!Compat.level(player).isClientSide()) menu.broadcastChanges();
            ci.cancel();
        }
    }
}
