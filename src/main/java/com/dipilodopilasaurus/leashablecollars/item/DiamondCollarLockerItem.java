package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Ids;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/** Tier 2 of the lock ladder: only this can undo its own lock, and the golden spatula cannot. */
public class DiamondCollarLockerItem extends CollarLockerItem {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, Ids.of("diamond_collar_locker"));

    public DiamondCollarLockerItem() {
        super(REGISTRY_KEY, 2);
    }
}
