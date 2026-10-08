package com.dipilodopilasaurus.leashablecollars.client;

import com.dipilodopilasaurus.leashablecollars.network.PacketGameRules;

import com.dipilodopilasaurus.leashablecollars.FeatureRules;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?}
//? if fabric && <1.19 {
/*import dev.emi.trinkets.api.client.TrinketRendererRegistry;
*///?} elif fabric {
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
//?}
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
*///?}
//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///?}
//? if dual {
/*import com.dipilodopilasaurus.leashablecollars.accessory.AccessoryLibraries;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
*///?}
//? if !fabric && >=26.3 {
/*import top.theillusivec4.curios.api.internal.CuriosClientServices;
*///?}
//? if !fabric && <26.3 {
/*import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
*///?}
//? if !neoforge {
import net.minecraft.client.gui.screens.MenuScreens;
//?}
//? if <1.21.4 {
/*import com.dipilodopilasaurus.leashablecollars.Ids;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
*///?}
import com.dipilodopilasaurus.leashablecollars.network.Net;
import net.minecraft.client.Minecraft;
import com.dipilodopilasaurus.leashablecollars.ClientHooks;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import net.minecraft.client.gui.screens.Screen;
import com.dipilodopilasaurus.leashablecollars.client.screen.CollarDyeScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.DeedItemScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.GuiCompat;
import com.dipilodopilasaurus.leashablecollars.client.screen.InventoryEditorScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.PawsAttackConfigScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.PawsConfigScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.PawsSelectScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.PetControlScreen;
import com.dipilodopilasaurus.leashablecollars.item.FootPawsItem;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import com.dipilodopilasaurus.leashablecollars.network.PacketLookAtLerped;
import com.dipilodopilasaurus.leashablecollars.network.PacketOpenPetControl;
import com.dipilodopilasaurus.leashablecollars.network.PacketPawsConfig;
import com.dipilodopilasaurus.leashablecollars.paws.PawsConfigHelper;

//? if fabric {
@Environment(EnvType.CLIENT)
public class RegisterClient implements ClientModInitializer {
//?}
//? if neoforge {
/*// A second @Mod class for the same id, restricted to one side: NeoForge's client entrypoint.
@Mod(value = PlayerCollarsMod.MOD_ID, dist = Dist.CLIENT)
public class RegisterClient {
    public RegisterClient(IEventBus modBus) {
        // Not the constructor: NeoForge fixes no order between two @Mod classes, and the item arrays
        // have to be filled first.
        modBus.addListener(FMLClientSetupEvent.class, event -> onInitializeClient());
        // MenuScreens is frozen by the time client setup runs on NeoForge; this event is the only window.
        modBus.addListener(RegisterMenuScreensEvent.class, event ->
                event.register(PlayerCollarsMod.INVENTORY_EDITOR_MENU, InventoryEditorScreen::new));
    }
*///?}
//? if forge {
/*// Forge's @Mod carries no dist, so the side restriction lives on an EventBusSubscriber instead.
@Mod.EventBusSubscriber(modid = PlayerCollarsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegisterClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        new RegisterClient().onInitializeClient();
    }
*///?}

    //? if fabric {
    @Override
    //?}
    public void onInitializeClient() {
        ClientHooks.setCollarDyeScreenOpener((stack, playerId) ->
                GuiCompat.setScreen(Minecraft.getInstance(), new CollarDyeScreen(stack, playerId)));
        ClientHooks.setDeedScreenOpener((stack, player) ->
                GuiCompat.setScreen(Minecraft.getInstance(), new DeedItemScreen(stack, player)));
        //? if <1.21.4 {
        /*// No items/ here, so the using_item swap is a model override keyed on this predicate. Queued
        // because ItemProperties' map is a plain HashMap and Forge runs client setup off-thread.
        Minecraft.getInstance().execute(() -> {
            ItemProperties.register(PlayerCollarsMod.LASER_POINTER_ITEM, Ids.of("using_item"), RegisterClient::usingItem);
            ItemProperties.register(PlayerCollarsMod.CLICKER_ITEM, Ids.of("using_item"), RegisterClient::usingItem);
        });
        *///?}
        ClientHooks.setInvisibleFenceParticleFilter(() -> {
            Minecraft client = Minecraft.getInstance();
            return client.player != null && EquippedAccessories.hasEquipped(client.player, x -> x.is(PlayerCollarsMod.COLLAR_TAG));
        });

        //? if fabric && <1.19 {
        /*TrinketRendererRegistry.registerRenderer(PlayerCollarsMod.COLLAR_ITEM, new CollarRenderer());
        TrinketRendererRegistry.registerRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, new CollarRenderer());
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            TrinketRendererRegistry.registerRenderer(p, new PawRenderer());
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            TrinketRendererRegistry.registerRenderer(p, new FootPawRenderer());
        *///?} elif fabric {
        AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            AccessoriesRendererRegistry.registerRenderer(p, PawRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            AccessoriesRendererRegistry.registerRenderer(p, FootPawRenderer::new);
        //?} elif dual {
        /*if (AccessoryLibraries.curios()) {
            CuriosRendererRegistry.register(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
            CuriosRendererRegistry.register(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
            for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
                CuriosRendererRegistry.register(p, PawRenderer::new);
            for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
                CuriosRendererRegistry.register(p, FootPawRenderer::new);
        }
        if (AccessoryLibraries.accessories()) {
            AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.COLLAR_ITEM, AccessoriesCollarRenderer::new);
            AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, AccessoriesCollarRenderer::new);
            for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
                AccessoriesRendererRegistry.registerRenderer(p, AccessoriesPawRenderer::new);
            for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
                AccessoriesRendererRegistry.registerRenderer(p, AccessoriesFootPawRenderer::new);
        }
        *///?} elif >=26.3 {
        /*CuriosClientServices.EXTENSIONS.registerCurioRenderer(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
        CuriosClientServices.EXTENSIONS.registerCurioRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            CuriosClientServices.EXTENSIONS.registerCurioRenderer(p, PawRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            CuriosClientServices.EXTENSIONS.registerCurioRenderer(p, FootPawRenderer::new);
        *///?} else {
        /*CuriosRendererRegistry.register(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
        CuriosRendererRegistry.register(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            CuriosRendererRegistry.register(p, PawRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            CuriosRendererRegistry.register(p, FootPawRenderer::new);
        *///?}
        // Both loaders' contexts came down to Minecraft.getInstance(), so the render-thread hop lives here.
        Net.onClientbound(PacketGameRules.ID, payload ->
                Minecraft.getInstance().execute(() -> FeatureRules.receive(Minecraft.getInstance().level, payload.mask())));
        Net.onClientbound(PacketLookAtLerped.ID, payload ->
                Minecraft.getInstance().execute(() -> RotationLerpHandler.beginClickTurn(payload.vec())));
        Net.onClientbound(PacketOpenPetControl.ID, payload ->
                Minecraft.getInstance().execute(() -> {
                    Minecraft client = Minecraft.getInstance();
                    if (GuiCompat.currentScreen(client) instanceof PetControlScreen screen && screen.isFor(payload.petId())) {
                        screen.update(payload);
                    } else {
                        GuiCompat.setScreen(client, new PetControlScreen(payload));
                    }
                }));
        Net.onClientbound(PacketPawsConfig.ID, payload ->
                Minecraft.getInstance().execute(() -> {
                    Minecraft client = Minecraft.getInstance();
                    // The reply lands on the select screen that asked for it, which is where the name is.
                    Screen open = GuiCompat.currentScreen(client);
                    String petName = open instanceof PawsSelectScreen select ? select.petName() : "";
                    GuiCompat.setScreen(client, payload.section() == PawsConfigHelper.ATTACK
                            ? new PawsAttackConfigScreen(payload, petName, open)
                            : new PawsConfigScreen(payload, petName, open));
                }));
        // Only Fabric registers here; the bus loaders subscribe from ItemTints itself.
        //? if fabric && <1.21.4 {
        /*ItemTints.register();
        *///?}
        //? if !neoforge {
        MenuScreens.register(PlayerCollarsMod.INVENTORY_EDITOR_MENU, InventoryEditorScreen::new);
        //?}
        ClientEvents.onLevelRenderEnd(frame -> RotationLerpHandler.turnTowardsClick());
        LaserRenderer.register();
    }

    //? if <1.21.4 {
    /*private static float usingItem(ItemStack stack, ClientLevel level, LivingEntity holder, int seed) {
        return holder != null && holder.isUsingItem() && holder.getUseItem() == stack ? 1.0F : 0.0F;
    }
    *///?}
}
