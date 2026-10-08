package com.dipilodopilasaurus.leashablecollars.inventory;

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//? if >=26.1 {
/*import net.minecraft.world.inventory.ContainerInput;
*///?} else
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** The owner's view of a pet's 36 main slots; vanilla sync plus {@link ContainerData} covers it. */
public class InventoryEditorMenu extends AbstractContainerMenu {
    public static final int TARGET_SLOT_COUNT = InventoryLockManager.LOCKABLE_SLOT_COUNT;

    private final @Nullable Player target;
    private final ContainerData lockState;

    /** Client side: the target is unknown and unneeded. */
    public InventoryEditorMenu(int syncId, Inventory ownerInventory) {
        this(syncId, ownerInventory, null, new SimpleContainerData(TARGET_SLOT_COUNT));
    }

    public InventoryEditorMenu(int syncId, Inventory ownerInventory, Player target) {
        this(syncId, ownerInventory, target, lockState(target));
    }

    private InventoryEditorMenu(int syncId, Inventory ownerInventory, @Nullable Player target, ContainerData lockState) {
        super(PlayerCollarsMod.INVENTORY_EDITOR_MENU, syncId);
        this.target = target;
        this.lockState = lockState;
        Container targetInventory = target == null ? new SimpleContainer(TARGET_SLOT_COUNT) : target.getInventory();
        targetInventory.startOpen(ownerInventory.player);
        addDataSlots(lockState);

        addInventoryGrid(targetInventory, 18);
        addInventoryGrid(ownerInventory, 104);
    }

    /** Three rows then the hotbar, so screen slots 0-35 line up with container indices 9-35 then 0-8. */
    private void addInventoryGrid(Container inventory, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, y + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (!FeatureRules.CAN_ACCESS_OWNED_INVENTORY.enabled(Compat.level(player))) return false;
        if (target == null) return true;
        return target.isAlive()
                && player.distanceToSqr(target) <= 64.0D
                && EquippedAccessories.findOwned(target, stack -> stack.is(PlayerCollarsMod.COLLAR_TAG),
                        player.getUUID(), target.getUUID()) != null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (!stillValid(player)) return ItemStack.EMPTY;
        if (slotIndex < 0 || slotIndex >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        boolean moved = slotIndex < TARGET_SLOT_COUNT
                ? moveItemStackTo(original, TARGET_SLOT_COUNT, slots.size(), true)
                : moveItemStackTo(original, 0, TARGET_SLOT_COUNT, false);
        if (!moved) return ItemStack.EMPTY;

        if (original.isEmpty()) {
            //? if >=1.19.3 {
            slot.setByPlayer(ItemStack.EMPTY);
            //?} else {
            /*slot.set(ItemStack.EMPTY);
            *///?}
        } else slot.setChanged();
        return copy;
    }

    @Override
    //? if >=26.1 {
    /*public void clicked(int slotIndex, int button, ContainerInput actionType, Player player) {
    *///?} else
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (!stillValid(player)) return;
        if (!isLockToggle(slotIndex, button, actionType)) {
            super.clicked(slotIndex, button, actionType, player);
            return;
        }
        if (EquippedAccessories.findOwned(target, stack -> stack.is(PlayerCollarsMod.COLLAR_TAG),
                player.getUUID(), target.getUUID()) == null) {
            return;
        }

        boolean locked = InventoryLockManager.toggle(target, slots.get(slotIndex).getContainerSlot());
        Component message = Text.translatable(
                locked ? "item.playercollars.inventory_editor.locked" : "item.playercollars.inventory_editor.unlocked",
                slots.get(slotIndex).getItem().getHoverName());
        Compat.sendOverlayMessage(player, message);
        Compat.sendOverlayMessage(target, message);
        broadcastChanges();
    }

    //? if >=26.1 {
    /*private boolean isLockToggle(int slotIndex, int button, ContainerInput actionType) {
    *///?} else
    private boolean isLockToggle(int slotIndex, int button, ClickType actionType) {
        return target != null
                && slotIndex >= 0 && slotIndex < TARGET_SLOT_COUNT
                //? if >=26.1 {
                /*&& button == 1 && actionType == ContainerInput.PICKUP
                *///?} else
                && button == 1 && actionType == ClickType.PICKUP
                && getCarried().isEmpty()
                && slots.get(slotIndex).hasItem();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (target == null || Compat.level(player).isClientSide()) return;
        InventoryLockManager.clearEmptyLocks(target);
        target.getInventory().stopOpen(player);
    }

    public boolean isTargetSlotLocked(int screenSlotIndex) {
        if (screenSlotIndex < 0 || screenSlotIndex >= TARGET_SLOT_COUNT) return false;
        return lockState.get(slots.get(screenSlotIndex).getContainerSlot()) != 0;
    }

    private static ContainerData lockState(Player target) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return InventoryLockManager.isLocked(target, index) ? 1 : 0;
            }

            @Override
            public void set(int index, int value) {
                // The client never writes back; the owner toggles locks through clicked().
            }

            @Override
            public int getCount() {
                return TARGET_SLOT_COUNT;
            }
        };
    }
}
