//? if forge && <1.19 {
/*package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod.EventBusSubscriber(modid = PlayerCollarsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LegacyCuriosSlots {
    private LegacyCuriosSlots() {
    }

    @SubscribeEvent
    public static void register(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.NECKLACE.getMessageBuilder().size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.HANDS.getMessageBuilder().size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("feet").size(1)
                        .icon(new ResourceLocation("minecraft", "item/empty_armor_slot_boots")).build());
    }
}
*///?}
