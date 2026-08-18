package com.dipilodopilasaurus.leashablecollars;

//? if fabric {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
*///?} else
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?} else {
/*import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The loader seam for registries. This tree registers from static field initialisers, which NeoForge
 * forbids outside {@code RegisterEvent}, so these construct eagerly and queue the insertion for
 * {@link #flush}. A {@link Holder} cannot be queued, hence {@link #registerForHolder}.
 */
public final class Registration {
    private Registration() {
    }

    //? if !fabric {
    /*private static final List<Consumer<RegisterEvent>> PENDING = new ArrayList<>();
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
        //? if fabric {
        return Registry.registerForHolder(registry, Ids.of(path), value);
        //?} else {
        /*defer(registry, path, value);
        return DeferredHolder.<T, T>create(registry.key(), Ids.of(path));
        *///?}
    }

    /**
     * Vanilla exposes no builder for these, so Fabric API adds one and NeoForge patches the
     * constructor public. The factory is our own interface because vanilla's nested one gets renamed.
     */
    public static <T extends BlockEntity> BlockEntityType<T> blockEntityType(
            BlockEntityFactory<T> factory, Block... blocks) {
        //? if fabric {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
        //?} else {
        /*return new BlockEntityType<>(factory::create, blocks);
        *///?}
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }

    /**
     * 1.21.2 made {@code Properties.setId} mandatory; before that the setter does not exist. Every
     * construction site routes through here so it stays one guard rather than seventeen.
     */
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

    /** Fabric API's builder defaults the tab to its own overflow row; NeoForge places modded tabs itself. */
    public static CreativeModeTab.Builder creativeTab() {
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

    //? if !fabric {
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
}
