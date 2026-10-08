package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
//? if >=1.20.5 {
import net.minecraft.world.entity.Leashable;
//?}
import net.minecraft.world.entity.LivingEntity;
//? if >=1.20.5 && <1.21.6 {
/*import net.minecraft.world.item.LeadItem;
*///?}
import net.minecraft.world.level.Level;

import java.util.List;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
//? if <26.1 {
import net.minecraft.world.item.DyeItem;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.world.item.component.DyedItemColor;
//?}
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
*///?}
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
//?}
//? if >=1.21.2 {
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.enchantment.Enchantable;
//?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.phys.Vec3;
import com.dipilodopilasaurus.leashablecollars.leash.LeashProxyEntity;
import org.jetbrains.annotations.Nullable;

/** Shims for vanilla APIs whose shape differs across the versions this tree compiles for. */
public final class Compat {

    /** 1.21.9 replaced the integer permission level with a PermissionSet; both forms are a Predicate. */
    public static java.util.function.Predicate<net.minecraft.commands.CommandSourceStack> gameMasterOnly() {
        //? if >=1.21.6 {
        return net.minecraft.commands.Commands.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS);
        //?} else {
        /*return source -> source.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS);
        *///?}
    }

    public static void commandSuccess(net.minecraft.commands.CommandSourceStack source,
            java.util.function.Supplier<Component> message, boolean broadcast) {
        //? if >=1.20 {
        source.sendSuccess(message, broadcast);
        //?} else {
        /*source.sendSuccess(message.get(), broadcast);
        *///?}
    }

    public static Level level(Entity entity) {
        //? if >=1.20 {
        return entity.level();
        //?} else {
        /*return entity.level;
        *///?}
    }

    public static ServerLevel serverLevel(net.minecraft.server.level.ServerPlayer player) {
        //? if >=1.21.6 {
        return player.level();
        //?} elif >=1.19.3 {
        /*return player.serverLevel();
        *///?} else {
        /*return player.getLevel();
        *///?}
    }

    public static DamageSource thornsDamage(LivingEntity entity) {
        //? if >=1.19.4 {
        return entity.damageSources().thorns(entity);
        //?} else {
        /*return DamageSource.thorns(entity);
        *///?}
    }

    public static DamageSource playerAttackDamage(Player player) {
        //? if >=1.19.4 {
        return player.damageSources().playerAttack(player);
        //?} else {
        /*return DamageSource.playerAttack(player);
        *///?}
    }

    public static boolean isThornsDamage(DamageSource source) {
        //? if >=1.19.4 {
        return source.is(net.minecraft.world.damagesource.DamageTypes.THORNS);
        //?} else {
        /*return source instanceof net.minecraft.world.damagesource.EntityDamageSource entitySource
                && entitySource.isThorns();
        *///?}
    }

    public static ItemStack copyWithCount(ItemStack stack, int count) {
        //? if >=1.20 {
        return stack.copyWithCount(count);
        //?} else {
        /*if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
        *///?}
    }

    public static double attributeValue(LivingEntity entity, Holder<Attribute> attribute) {
        //? if >=1.21 {
        return entity.getAttributeValue(attribute);
        //?} else {
        /*return entity.getAttributeValue(attribute.value());
        *///?}
    }

    private Compat() {
    }

    /** 26.1 added {@code sendOverlayMessage}; before that, {@code displayClientMessage} with the flag. */
    public static void sendOverlayMessage(Player player, Component message) {
        //? if >=26.1 {
        /*player.sendOverlayMessage(message);
        *///?} else
        player.displayClientMessage(message, true);
    }

    /** Two overloads let javac settle the SoundEvents -> Holder conversion per call site. */
    public static SoundEvent sound(Holder<SoundEvent> holder) {
        return holder.value();
    }

    public static SoundEvent sound(SoundEvent event) {
        return event;
    }

    /** 1.21.5 replaced {@code Inventory.selected} with {@code getSelectedSlot}/{@code getSelectedItem}. */
    public static ItemStack selectedItem(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedItem();
        //?} else {
        /*return inventory.getSelected();
        *///?}
    }

    /** See {@link #selectedItem}. */
    public static int selectedSlot(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else {
        /*return inventory.selected;
        *///?}
    }

    /** 1.21.5 folded the tooltip flag into tooltip_display; everything here wants the old default, on. */
    //? if >=1.21.5 {
    public static DyedItemColor dyedColor(int rgb) {
        return new DyedItemColor(rgb);
    }
    //?}
    //? if >=1.20.5 && <1.21.5 {
    /*public static DyedItemColor dyedColor(int rgb) {
        return new DyedItemColor(rgb, true);
    }
    *///?}

    /** 1.21.2 gave {@code spawnAtLocation} an explicit level; before that it read the entity's own. */
    public static void spawnAtLocation(Entity entity, ServerLevel level, ItemStack stack) {
        //? if >=1.21.2 {
        entity.spawnAtLocation(level, stack);
        //?} else {
        /*entity.spawnAtLocation(stack);
        *///?}
    }

    /** 1.21.2 moved enchantability onto a component; below it items still need the override. */
    public static Item.Properties enchantable(Item.Properties properties, int value) {
        //? if >=1.21.2 {
        return properties.component(DataComponents.ENCHANTABLE, new Enchantable(value));
        //?} else {
        /*return properties;
        *///?}
    }

    //? if <1.21.2 {
    /*/^* 1.21.2 dropped the stack from {@code Item.use}'s return; below it, the caller must put it back. ^/
    public static InteractionResultHolder<ItemStack> useResult(InteractionResult result, ItemStack stack) {
        return new InteractionResultHolder<>(result, stack);
    }
    *///?}

    /** 1.21.5 routed server-side damage through its own method; before that {@code hurt} was it. */
    public static void hurt(LivingEntity entity, ServerLevel level, DamageSource source, float amount) {
        //? if >=1.21.5 {
        entity.hurtServer(level, source, amount);
        //?} else {
        /*entity.hurt(source, amount);
        *///?}
    }

    /** 1.20.5 gave FoodData a whole-FoodProperties overload; below it the two numbers go separately. */
    public static void feed(FoodData data, FoodProperties food) {
        //? if >=1.20.5 {
        data.eat(food);
        //?} else {
        /*data.eat(food.getNutrition(), food.getSaturationModifier());
        *///?}
    }

    /** {@code push} took three doubles until 1.20.5 added the vector overload. */
    public static void push(Entity entity, Vec3 delta) {
        //? if >=1.20.5 {
        entity.push(delta);
        //?} else {
        /*entity.push(delta.x, delta.y, delta.z);
        *///?}
    }

    /** 1.21 replaced the break-callback with the slot that holds the tool. */
    public static void hurtAndBreak(ItemStack stack, int amount, Player player, InteractionHand hand) {
        //? if >=1.21 {
        stack.hurtAndBreak(amount, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        //?} else {
        /*stack.hurtAndBreak(amount, player, broken -> broken.broadcastBreakEvent(hand));
        *///?}
    }

    /** Wolf armour, and its break sound, arrived in 1.20.5. */
    public static SoundEvent armorBreakSound() {
        //? if >=1.20.5 {
        return sound(SoundEvents.WOLF_ARMOR_BREAK);
        //?} else {
        /*return SoundEvents.ITEM_BREAK;
        *///?}
    }

    /** 1.21.2 keyed item cooldowns by stack rather than by item. */
    public static boolean isOnCooldown(Player player, ItemStack stack) {
        //? if >=1.21.2 {
        return player.getCooldowns().isOnCooldown(stack);
        //?} else {
        /*return player.getCooldowns().isOnCooldown(stack.getItem());
        *///?}
    }

    /** See {@link #isOnCooldown}. */
    public static void addCooldown(Player player, ItemStack stack, int ticks) {
        //? if >=1.21.2 {
        player.getCooldowns().addCooldown(stack, ticks);
        //?} else {
        /*player.getCooldowns().addCooldown(stack.getItem(), ticks);
        *///?}
    }

    /** 1.20.5 moved food onto a component; below it, it hangs off the item. */
    @Nullable
    public static FoodProperties food(ItemStack stack) {
        //? if >=1.20.5 {
        return stack.get(DataComponents.FOOD);
        //?} else {
        /*return stack.getItem().getFoodProperties();
        *///?}
    }

    /** 1.21.2 routed eating through the consumable component, which is where the effects moved. */
    public static void eat(Player player, Level level, ItemStack stack, FoodProperties food) {
        //? if >=1.21.2 {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) player.getFoodData().eat(food);
        else consumable.onConsume(level, player, stack);
        //?}
        //? if >=1.20.5 && <1.21.2 {
        /*player.eat(level, stack, food);
        *///?}
        //? if <1.20.5 {
        /*player.eat(level, stack);
        *///?}
    }

    /** The wolf-armour sounds the locker uses arrived with wolf armour in 1.20.5. */
    public static SoundEvent armorEquipSound() {
        //? if >=1.20.5 {
        return sound(SoundEvents.ARMOR_EQUIP_WOLF);
        //?} else {
        /*return SoundEvents.ARMOR_EQUIP_LEATHER;
        *///?}
    }

    /** See {@link #armorEquipSound}. */
    public static SoundEvent armorUnequipSound() {
        //? if >=1.20.5 {
        return sound(SoundEvents.ARMOR_UNEQUIP_WOLF);
        //?} else {
        /*return SoundEvents.ARMOR_EQUIP_GENERIC;
        *///?}
    }

    /** {@code makeSound} postdates 1.20.1; its volume and pitch getters are protected, hence the 1s. */
    public static void makeSound(LivingEntity entity, SoundEvent event) {
        //? if >=1.20.5 {
        entity.makeSound(event);
        //?} else {
        /*entity.playSound(event, 1.0f, 1.0f);
        *///?}
    }

    /** 1.21 moved attributes behind holders; the builder below it still takes the value. */
    public static AttributeSupplier.Builder addAttribute(AttributeSupplier.Builder builder, Holder<Attribute> attribute) {
        //? if >=1.21 {
        return builder.add(attribute);
        //?} else {
        /*return builder.add(attribute.value());
        *///?}
    }

    /** 26.1 moved a dye's colour off {@code DyeItem} and onto a data component. */
    public static @Nullable DyeColor dyeOf(ItemStack stack) {
        //? if >=26.1 {
        /*return stack.get(DataComponents.DYE);
        *///?} else {
        return stack.getItem() instanceof DyeItem dye ? dye.getDyeColor() : null;
        //?}
    }

    /** 1.21 folded the three float channels a dye tints a texture with into one packed int. */
    public static int dyeRgb(DyeColor color) {
        //? if >=1.21 {
        return color.getTextureDiffuseColor() & 0xFFFFFF;
        //?} else {
        /*float[] channels = color.getTextureDiffuseColors();
        return ((int) (channels[0] * 255.0F) << 16) | ((int) (channels[1] * 255.0F) << 8) | (int) (channels[2] * 255.0F);
        *///?}
    }

    /** 1.21.6 moved the leash-area query onto Leashable; below 1.20.5 the search is by concrete type. */
    public static List<LeashProxyEntity> leashProxiesHeldBy(Level level, Entity holder) {
        //? if >=1.21.6 {
        List<Leashable> held = Leashable.leashableLeashedTo(holder);
        //?} elif >=1.20.5 {
        /*List<Leashable> held = LeadItem.leashableInArea(level, holder.blockPosition(), e -> holder.equals(e.getLeashHolder()));
        *///?} else {
        /*return level.getEntitiesOfClass(LeashProxyEntity.class, holder.getBoundingBox().inflate(7.0D),
                e -> holder.equals(e.getLeashHolder()));
        *///?}
        //? if >=1.20.5 {
        return held.stream().filter(LeashProxyEntity.class::isInstance).map(LeashProxyEntity.class::cast).toList();
        //?}
    }
}
