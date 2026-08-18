package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Right-clicking a bone puts it on your head, the way a helmet does; {@code BoneEquippableMixin} is what
 * makes the slot accept it. Vanilla's {@code Equipable.swapWithEquipmentSlot} is unreachable for a bone,
 * so the swap below mirrors it, binding curse and all.
 */
@Mod.EventBusSubscriber(modid = LeashableCollars.MOD_ID)
public final class BoneEquipHandler {
    private BoneEquipHandler() {
    }

    @SubscribeEvent
    public static void equipBoneOnUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (!held.is(Items.BONE)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
        if (EnchantmentHelper.hasBindingCurse(worn) || ItemStack.matches(held, worn)) {
            return;
        }

        if (!event.getLevel().isClientSide()) {
            player.awardStat(Stats.ITEM_USED.get(Items.BONE));
        }

        // Both sides, as vanilla's swap does, so the head redraws without waiting for the server.
        ItemStack returned = worn.isEmpty() ? ItemStack.EMPTY : worn.copyAndClear();
        player.setItemSlot(EquipmentSlot.HEAD, held.copyAndClear());
        if (!returned.isEmpty()) {
            player.setItemInHand(event.getHand(), returned);
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        event.setCanceled(true);
    }
}
