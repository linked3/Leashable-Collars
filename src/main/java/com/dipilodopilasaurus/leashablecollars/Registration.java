package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.Consumer;

//? if >=1.19.3 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

//? if fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
*///?} elif >=1.19.3 {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?} else {
/*import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
*///?}
//?}
//? if neoforge {
/*import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
*///?}
//? if forge && >=1.19 {
/*import net.minecraftforge.registries.RegisterEvent;
*///?}
//? if !fabric {
/*import java.util.ArrayList;
import java.util.List;
*///?}
//? if !fabric && <1.21.2 {
/*import java.util.Set;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
//? if !fabric {
/*import net.minecraft.core.MappedRegistry;
*///?}
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Loader seam for registries. NeoForge forbids registering from a static initialiser, so these
 * construct eagerly and queue the insertion for {@link #flush}; a {@link Holder} cannot be queued.
 */
public final class Registration {
    private Registration() {
    }

    //? if !fabric && >=1.19 {
    /*private static final List<Consumer<RegisterEvent>> PENDING = new ArrayList<>();
    *///?} elif forge {
    /*private static final List<net.minecraftforge.registries.DeferredRegister<?>> PENDING = new ArrayList<>();
    *///?}

    // Intrusive holders, and the loader freezes the registries before a @Mod class initialises.
    //? if !fabric && >=1.21.2 {
    /*public static void unfreezeForConstruction() {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            if (registry instanceof MappedRegistry<?> mapped) mapped.unfreeze(true);
        }
    }
    *///?}
    //? if !fabric && <1.21.2 {
    /*public static void unfreezeForConstruction() {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            if (registry instanceof MappedRegistry<?> mapped) mapped.unfreeze();
        }
    }
    *///?}

    /** Takes a path, not a built id, so no signature here names the type 1.21.9 renamed. See {@link Ids}. */
    public static <T, V extends T> V register(Registry<T> registry, String path, V value) {
        //? if fabric {
        return Registry.register(registry, Ids.of(path), value);
        //?} else {
        /*defer(registry, path, value);
        return value;
        *///?}
    }

    public static <T, V extends T> V register(Registry<T> registry, ResourceKey<T> key, V value) {
        return register(registry, Ids.of(key).getPath(), value);
    }

    /** Lazily bound on NeoForge, so the holder must not be dereferenced before registration is over. */
    public static <T, V extends T> Holder<T> registerForHolder(Registry<T> registry, String path, V value) {
        //? if fabric && >=1.19.3 {
        return Registry.registerForHolder(registry, Ids.of(path), value);
        //?} elif fabric {
        /*Registry.register(registry, Ids.of(path), value);
        return registry.getHolder(ResourceKey.create(registry.key(), Ids.of(path))).orElseThrow();
        *///?}
        //? if neoforge {
        /*defer(registry, path, value);
        return DeferredHolder.<T, T>create(registry.key(), Ids.of(path));
        *///?}
        //? if forge {
        /*// Forge has no DeferredHolder, and every call site unwraps to the value, so a direct holder
        // over the same instance is enough -- it just never reports a registry key.
        defer(registry, path, value);
        return Holder.direct(value);
        *///?}
    }

    /** Vanilla exposes no builder; Fabric API adds one, NeoForge patches the constructor public. */
    public static <T extends BlockEntity> BlockEntityType<T> blockEntityType(
            BlockEntityFactory<T> factory, Block... blocks) {
        //? if fabric {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
        //?} elif >=1.21.2 {
        /*return new BlockEntityType<>(factory::create, blocks);
        *///?} else {
        /*return new BlockEntityType<>(factory::create, Set.of(blocks), null);
        *///?}
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    /** 1.21.2 made Properties.setId mandatory, so every construction site routes through here. */
    public static Item.Properties withId(Item.Properties props, ResourceKey<Item> key) {
        //? if >=1.21.2 {
        return props.setId(key);
        //?} else {
        /*return props;
        *///?}
    }

    public static BlockBehaviour.Properties withId(BlockBehaviour.Properties props, ResourceKey<Block> key) {
        //? if >=1.21.2 {
        return props.setId(key);
        //?} else {
        /*return props;
        *///?}
    }

    //? if >=1.19.3 {
    /** Fabric API defaults to an overflow row; NeoForge places modded tabs itself. */
    private static CreativeModeTab.Builder creativeTab() {
        //? if fabric {
        // 26.1 renamed Fabric's item-group builder to match the vanilla CreativeModeTab name.
        //? if >=26.1 {
        /*return FabricCreativeModeTab.builder();
        *///?} else
        return FabricItemGroup.builder();
        //?} else {
        /*return CreativeModeTab.builder();
        *///?}
    }

    //?}

    //? if !fabric && >=1.19 {
    /*private static <T, V extends T> void defer(Registry<T> registry, String path, V value) {
        var id = Ids.of(path);
        PENDING.add(event -> event.register(registry.key(), id, () -> value));
    }

    // RegisterEvent fires once per registry and a queued call no-ops unless the keys match, so
    // replaying the whole queue every firing is correct -- and why it must not be cleared.
    public static void flush(RegisterEvent event) {
        for (Consumer<RegisterEvent> pending : PENDING) pending.accept(event);
    }
    *///?}

    //? if forge && <1.19 {
    /*private static <T, V extends T> void defer(Registry<T> registry, String path, V value) {
        var deferred = net.minecraftforge.registries.DeferredRegister.create(registry.key(), PlayerCollarsMod.MOD_ID);
        deferred.register(path, () -> value);
        PENDING.add(deferred);
    }

    public static void bindLegacy(net.minecraftforge.eventbus.api.IEventBus bus) {
        for (var deferred : PENDING) deferred.register(bus);
    }
    *///?}

    public static SoundEvent sound(String path) {
        //? if >=1.19.3 {
        return SoundEvent.createVariableRangeEvent(Ids.of(path));
        //?} else {
        /*return new SoundEvent(Ids.of(path));
        *///?}
    }

    public static <T extends AbstractContainerMenu> MenuType<T> menu(
            String path, BiFunction<Integer, Inventory, T> factory) {
        //? if >=1.19.3 {
        return register(BuiltInRegistries.MENU, path, new MenuType<>(
                factory::apply, net.minecraft.world.flag.FeatureFlags.VANILLA_SET));
        //?} elif fabric {
        /*return net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.registerSimple(Ids.of(path), factory::apply);
        *///?} else {
        /*return register(BuiltInRegistries.MENU, path, new MenuType<>(factory::apply));
        *///?}
    }

    public static CreativeModeTab creativeTab(String path, Component title,
            Supplier<ItemStack> icon,
            Consumer<Consumer<ItemLike>> items) {
        //? if >=1.19.3 {
        return register(BuiltInRegistries.CREATIVE_MODE_TAB, path, creativeTab().title(title).icon(icon)
                .displayItems((context, entries) -> items.accept(entries::accept)).build());
        //?} elif fabric {
        /*return FabricItemGroupBuilder.create(Ids.of(path)).icon(icon)
                .appendItems(stacks -> items.accept(item -> stacks.add(new ItemStack(item)))).build();
        *///?} else {
        /*return new CreativeModeTab(PlayerCollarsMod.MOD_ID) {
            @Override
            public Component getDisplayName() {
                return title;
            }

            @Override
            public ItemStack makeIcon() {
                return icon.get();
            }

            @Override
            public void fillItemList(net.minecraft.core.NonNullList<ItemStack> stacks) {
                items.accept(item -> stacks.add(new ItemStack(item)));
            }
        };
        *///?}
    }

}
