package com.dipilodopilasaurus.leashablecollars.item;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.Ids;

/** Slots come from data tags now, not the code-declared component Trinkets used, so subclasses pass no slot list. */
public class FootPawsItem extends WearableItem {
    public final int color, beansColor;

    public FootPawsItem(ResourceKey<Item> key, int color, int beansColor) {
        super(Components.withDefaultColors(Registration.withId(new Item.Properties().stacksTo(1), key),
                color | 0xFF000000, beansColor));
        this.color = color | 0xFF000000;
        this.beansColor = beansColor;
    }

    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, Ids.of(c.getName() + "_foot_paws"));
    }

}
