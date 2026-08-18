package com.dipilodopilasaurus.leashablecollars.client;

//? if fabric {
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.ICurioRenderer;
*///?}
import com.dipilodopilasaurus.leashablecollars.network.Net;
import net.minecraft.client.Minecraft;
import com.dipilodopilasaurus.leashablecollars.ClientHooks;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;
import com.dipilodopilasaurus.leashablecollars.client.screen.CollarDyeScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.DeedItemScreen;
import com.dipilodopilasaurus.leashablecollars.client.screen.PetControlScreen;
import com.dipilodopilasaurus.leashablecollars.item.FootPawsItem;
import com.dipilodopilasaurus.leashablecollars.item.PawsItem;
import com.dipilodopilasaurus.leashablecollars.network.PacketLookAtLerped;
import com.dipilodopilasaurus.leashablecollars.network.PacketOpenPetControl;

//? if fabric {
@Environment(EnvType.CLIENT)
public class RegisterClient implements ClientModInitializer {
//?} else {
/*// A second @Mod class for the same id, restricted to one side: NeoForge's client entrypoint.
@Mod(value = PlayerCollarsMod.MOD_ID, dist = Dist.CLIENT)
public class RegisterClient {
    public RegisterClient(IEventBus modBus) {
        // Not from the constructor: NeoForge fixes no order between two @Mod classes of one mod, and
        // this body needs PlayerCollarsMod's item arrays filled. Client setup is the first hook that is.
        modBus.addListener(FMLClientSetupEvent.class, event -> onInitializeClient());
    }
*///?}

    //? if fabric {
    @Override
    //?}
    public void onInitializeClient() {
        ClientHooks.setCollarDyeScreenOpener((stack, playerId) ->
                Minecraft.getInstance().setScreen(new CollarDyeScreen(stack, playerId)));
        ClientHooks.setDeedScreenOpener((stack, player) ->
                Minecraft.getInstance().setScreen(new DeedItemScreen(stack, player)));
        ClientHooks.setInvisibleFenceParticleFilter(() -> {
            Minecraft client = Minecraft.getInstance();
            return client.player != null && EquippedAccessories.hasEquipped(client.player, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG));
        });

        // Accessories binds items to a registered renderer id, Curios takes a supplier per item. Either
        // way each item gets its own renderer instance, which is all the renderers assume.
        //? if fabric {
        var collarRenderer = Ids.of("collar_renderer");
        AccessoriesRendererRegistry.registerRenderer(collarRenderer, CollarRenderer::new);
        AccessoriesRendererRegistry.bindItemToRenderer(PlayerCollarsMod.COLLAR_ITEM, collarRenderer);
        AccessoriesRendererRegistry.bindItemToRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, collarRenderer);

        var pawRenderer = Ids.of("paw_renderer");
        AccessoriesRendererRegistry.registerRenderer(pawRenderer, PawRenderer::new);
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            AccessoriesRendererRegistry.bindItemToRenderer(p, pawRenderer);

        var footPawRenderer = Ids.of("foot_paw_renderer");
        AccessoriesRendererRegistry.registerRenderer(footPawRenderer, FootPawRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            AccessoriesRendererRegistry.bindItemToRenderer(p, footPawRenderer);
        //?} else {
        /*ICurioRenderer.register(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
        ICurioRenderer.register(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
        for (PawsItem p : PlayerCollarsMod.PAWS_ITEMS)
            ICurioRenderer.register(p, PawRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS)
            ICurioRenderer.register(p, FootPawRenderer::new);
        *///?}
        // Both loaders' context objects came down to Minecraft.getInstance(), so the hop onto the render
        // thread is spelled out here and Net.onClientbound only delivers the payload.
        Net.onClientbound(PacketLookAtLerped.ID, payload ->
                Minecraft.getInstance().execute(() -> RotationLerpHandler.beginClickTurn(payload.vec())));
        Net.onClientbound(PacketOpenPetControl.ID, payload ->
                Minecraft.getInstance().execute(() -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.screen instanceof PetControlScreen screen && screen.isFor(payload.petId())) {
                        screen.update(payload);
                    } else {
                        client.setScreen(new PetControlScreen(payload));
                    }
                }));
        ClientEvents.onLevelRenderEnd((poses, buffers) -> RotationLerpHandler.turnTowardsClick());
        LaserRenderer.register();
    }
}
