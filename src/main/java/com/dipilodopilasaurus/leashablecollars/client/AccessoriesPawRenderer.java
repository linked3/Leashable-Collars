//? if dual {
/*package com.dipilodopilasaurus.leashablecollars.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/^*
 * The Accessories view of {@link PawRenderer}. This is the reason the node carries both libraries:
 * Curios has no first-person hook until 1.21.2, and Accessories has had one all along.
 ^/
public class AccessoriesPawRenderer implements AccessoryRenderer {

    // Accessories skips first person unless the renderer asks for it.
    @Override
    public boolean shouldRenderInFirstPerson(HumanoidArm arm, ItemStack stack, SlotReference reference) {
        return true;
    }

    @Override
    public <M extends LivingEntity> void renderOnFirstPerson(HumanoidArm arm, ItemStack stack, SlotReference reference,
            PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light) {
        if (!(model instanceof PlayerModel playerModel)) return;
        PawRenderer.drawArm(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel,
                arm == HumanoidArm.LEFT, true);
    }

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices,
            EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(model instanceof PlayerModel playerModel)) return;
        PawRenderer.drawArms(AccessoryItemRenderUtil.stripGlint(stack), matrices, multiBufferSource, light, playerModel);
    }
}
*///?}
