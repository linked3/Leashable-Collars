package com.dipilodopilasaurus.leashablecollars.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.MapItemColor;
import com.dipilodopilasaurus.leashablecollars.Compat;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;

/** Slots come from data tags now, not the code-declared component Trinkets used, so subclasses pass no slot list. */
public class FootPawsItem extends WearableItem {
    public final int color, beansColor;

    public FootPawsItem(ResourceKey<Item> key, int color, int beansColor) {
        super(Registration.withId(new Item.Properties().stacksTo(1), key)
                .component(DataComponents.DYED_COLOR, Compat.dyedColor(color | 0xFF000000))
                .component(DataComponents.MAP_COLOR, new MapItemColor(beansColor))
        );
        this.color = color | 0xFF000000;
        this.beansColor = beansColor;
    }

    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, Ids.of(c.getName() + "_foot_paws"));
    }

}
