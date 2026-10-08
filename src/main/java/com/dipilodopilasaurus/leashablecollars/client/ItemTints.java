package com.dipilodopilasaurus.leashablecollars.client;

//? if fabric && <1.21.4 {
/*import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
*///?}
//? if neoforge && <1.21.4 {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
*///?}
//? if forge && <1.21.4 {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
*///?}
//? if forge && >=1.19 && <1.21.4 {
/*import net.minecraftforge.client.event.RegisterColorHandlersEvent;
*///?}
//? if forge && <1.19 {
/*import net.minecraftforge.client.event.ColorHandlerEvent;
*///?}
//? if <1.21.4 {
/*import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import com.dipilodopilasaurus.leashablecollars.component.Components;
import com.dipilodopilasaurus.leashablecollars.item.ClickerItem;
import com.dipilodopilasaurus.leashablecollars.item.CollarItem;
import com.dipilodopilasaurus.leashablecollars.item.FootPawsItem;
import com.dipilodopilasaurus.leashablecollars.item.LaserPointerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
*///?}

/** Item tints below 1.21.4, where a model cannot carry its own; values match the tints list in items/. */
public final class ItemTints {
    private ItemTints() {
    }

    //? if <1.21.4 {
    /*// Constant tints of the sixteen dog beds, in DyeColor order.
    private static final int[] DOG_BED = {
            -986896, -1341372, -3975987, -10057261, -2175190, -12464844, -2588264, -12369085,
            -5526613, -14125417, -8704066, -14339694, -11456486, -12889830, -5033684, -14804197};

    // 1.21 reads the tint as ARGB; a colour out of a data component has no alpha, so unforced it draws clear.
    private static int tint(ItemStack stack, int index) {
        return 0xFF000000 | rgb(stack, index);
    }

    private static int rgb(ItemStack stack, int index) {
        Item item = stack.getItem();
        if (item instanceof CollarItem) {
            return index == 0 ? CollarItem.getColor(stack) : CollarItem.getPawColor(stack);
        }
        if (item instanceof FootPawsItem paws) {
            return index == 0 ? Components.dyeColor(stack, paws.color) : paws.beansColor;
        }
        if (item instanceof BlockItem bed && bed.getBlock() instanceof DogBedBlock dogBed) {
            return DOG_BED[dogBed.getColor().ordinal()];
        }
        if (item instanceof LaserPointerItem) {
            return index == 0 ? -1 : Components.dyeColor(stack, LaserPointerItem.DEFAULT_COLOR);
        }
        if (item instanceof ClickerItem) {
            return Components.dyeColor(stack, ClickerItem.DEFAULT_COLOR);
        }
        return -1;
    }

    private static ItemLike[] tinted() {
        List<ItemLike> items = new ArrayList<>();
        items.add(PlayerCollarsMod.COLLAR_ITEM);
        items.add(PlayerCollarsMod.TAGLESS_COLLAR_ITEM);
        items.add(PlayerCollarsMod.LASER_POINTER_ITEM);
        items.add(PlayerCollarsMod.CLICKER_ITEM);
        items.addAll(List.of(PlayerCollarsMod.PAWS_ITEMS));
        items.addAll(List.of(PlayerCollarsMod.FOOT_PAWS_ITEMS));
        items.addAll(List.of(PlayerCollarsMod.DOG_BED_ITEMS));
        return items.toArray(new ItemLike[0]);
    }
    *///?}

    //? if fabric && <1.21.4 {
    /*public static void register() {
        ColorProviderRegistry.ITEM.register(ItemTints::tint, tinted());
    }
    *///?}

    //? if neoforge && <1.21.4 {
    /*@EventBusSubscriber(modid = PlayerCollarsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Registrar {
        private Registrar() {
        }

        @SubscribeEvent
        public static void onItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(ItemTints::tint, tinted());
        }
    }
    *///?}

    //? if forge && >=1.19 && <1.21.4 {
    /*@Mod.EventBusSubscriber(modid = PlayerCollarsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Registrar {
        private Registrar() {
        }

        @SubscribeEvent
        public static void onItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(ItemTints::tint, tinted());
        }
    }
    *///?}

    //? if forge && <1.19 {
    /*@Mod.EventBusSubscriber(modid = PlayerCollarsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Registrar {
        private Registrar() {
        }

        @SubscribeEvent
        public static void onItemColors(ColorHandlerEvent.Item event) {
            event.getItemColors().register(ItemTints::tint, tinted());
        }
    }
    *///?}
}
