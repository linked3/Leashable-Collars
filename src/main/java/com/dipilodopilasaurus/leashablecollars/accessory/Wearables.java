//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.accessory;

import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.item.FootPawsItem;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

/^* Every item that occupies an accessory slot, in the order the renderers are registered in. ^/
public final class Wearables {
    private Wearables() {
    }

    public static List<Item> all() {
        List<Item> items = new ArrayList<>();
        items.add(PlayerCollarsMod.COLLAR_ITEM);
        items.add(PlayerCollarsMod.TAGLESS_COLLAR_ITEM);
        for (PawsItem paws : PlayerCollarsMod.PAWS_ITEMS) items.add(paws);
        for (FootPawsItem paws : PlayerCollarsMod.FOOT_PAWS_ITEMS) items.add(paws);
        return items;
    }
}
*///?}
