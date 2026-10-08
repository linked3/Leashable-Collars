//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/^* The Accessories view of {@link FootPawRenderer}. See {@link AccessoriesCollarRenderer}. ^/
public class AccessoriesFootPawRenderer implements AccessoryRenderer {

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;
        FootPawRenderer.drawLegs(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
}
*///?}
