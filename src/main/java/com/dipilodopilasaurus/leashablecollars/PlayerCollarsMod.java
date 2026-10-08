package com.dipilodopilasaurus.leashablecollars;

//? if >=1.19.3 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.BuiltInRegistries;
*///?}

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if fabric {
import net.fabricmc.api.ModInitializer;
//?}
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*///?}
//? if forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
//? if >=1.19 {
import net.minecraft.core.UUIDUtil;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.UUIDUtil;
*///?}
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponentType;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.DataComponentType;
*///?}
import net.minecraft.core.particles.ParticleTypes;


import net.minecraft.world.item.*;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
//?}
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.*;
//? if <1.20.5 {
/*import com.dipilodopilasaurus.leashablecollars.component.compat.ExtraCodecs;
*///?}
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
//? if <26.3 {
import net.minecraft.world.item.BedItem;
//?}
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
//? if dual {
/*import com.dipilodopilasaurus.leashablecollars.accessory.AccessoriesBinding;
import com.dipilodopilasaurus.leashablecollars.accessory.AccessoryLibraries;
import com.dipilodopilasaurus.leashablecollars.accessory.CuriosBinding;
*///?}
import com.dipilodopilasaurus.leashablecollars.component.Components;
//? if <1.21 {
/*import com.dipilodopilasaurus.leashablecollars.enchant.CollarEnchantment;
import com.dipilodopilasaurus.leashablecollars.enchant.Enchants;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
*///?}
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import com.dipilodopilasaurus.leashablecollars.command.CollarCommand;
import com.dipilodopilasaurus.leashablecollars.block.DogBowlBlock;
import com.dipilodopilasaurus.leashablecollars.block.InvisibleFenceBlock;
import com.dipilodopilasaurus.leashablecollars.inventory.InventoryEditorMenu;
import com.dipilodopilasaurus.leashablecollars.item.*;
import com.dipilodopilasaurus.leashablecollars.leash.LeashImpl;
import com.dipilodopilasaurus.leashablecollars.leash.LeashProxyEntity;
import com.dipilodopilasaurus.leashablecollars.network.*;
import com.dipilodopilasaurus.leashablecollars.paws.PawsFilter;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;


//? if fabric {
public class PlayerCollarsMod implements ModInitializer {
//?} else {
/*@Mod(PlayerCollarsMod.MOD_ID)
public class PlayerCollarsMod {
*///?}
	public static final String MOD_ID = "playercollars";
	// Runs before every field below it, which is the whole point. See Registration.
	//? if !fabric {
	/*static {
		Registration.unfreezeForConstruction();
	}
	*///?}
	// 26.3 deleted map_color, which the paw colour rode, and datafixes it off every stack. Declared
	// above the items because CollarItem's constructor reads it through Components.withDefaultColors.
	//? if >=26.3 {
	/*public static final DataComponentType<Integer> PAW_COLOR_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"paw_color",
			DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());
	*///?}
    public static final CollarItem COLLAR_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarItem.REGISTRY_KEY, new CollarItem(false));
    public static final CollarItem TAGLESS_COLLAR_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarItem.TAGLESS_REGISTRY_KEY, new CollarItem(true));
    public static final ClickerItem CLICKER_ITEM = Registration.register(BuiltInRegistries.ITEM, ClickerItem.REGISTRY_KEY, new ClickerItem());
    public static final DeedItem DEED_OF_OWNERSHIP = Registration.register(BuiltInRegistries.ITEM, "deed_of_ownership", new DeedItem());
    public static final Item DEED_OF_OWNERSHIP_STAMPED = Registration.register(BuiltInRegistries.ITEM, "stamped_deed_of_ownership", new StampedDeedItem());
    public static final InvisibleFenceBlock INVISIBLE_FENCE_BLOCK = Registration.register(BuiltInRegistries.BLOCK, InvisibleFenceBlock.REGISTRY_KEY,
            new InvisibleFenceBlock(invisibleFenceProperties()));
    public static final BlockItem INVISIBLE_FENCE_BLOCK_ITEM = Registration.register(BuiltInRegistries.ITEM, InvisibleFenceBlock.ITEM_REGISTRY_KEY,
			new BlockItem(INVISIBLE_FENCE_BLOCK, Registration.withId(new Item.Properties(), InvisibleFenceBlock.ITEM_REGISTRY_KEY)));
    public static final PawSetupItem PAW_CONFIGURATION_ITEM = Registration.register(BuiltInRegistries.ITEM, PawSetupItem.REGISTRY_KEY, new PawSetupItem());
    public static final CollarLockerItem COLLAR_LOCKER_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarLockerItem.REGISTRY_KEY, new CollarLockerItem());
    public static final DiamondCollarLockerItem DIAMOND_COLLAR_LOCKER_ITEM = Registration.register(BuiltInRegistries.ITEM, DiamondCollarLockerItem.REGISTRY_KEY, new DiamondCollarLockerItem());
    public static final InventoryEditorItem INVENTORY_EDITOR_ITEM = Registration.register(BuiltInRegistries.ITEM, InventoryEditorItem.REGISTRY_KEY, new InventoryEditorItem());
    public static final MenuType<InventoryEditorMenu> INVENTORY_EDITOR_MENU = Registration.menu("inventory_editor", InventoryEditorMenu::new);
	public static final SpatulaItem SPATULA_ITEM = Registration.register(BuiltInRegistries.ITEM, "golden_spatula", new SpatulaItem());
	public static final GroomingBrushItem GROOMING_BRUSH_ITEM = Registration.register(
			BuiltInRegistries.ITEM,
			"grooming_brush",
			new GroomingBrushItem(Registration.withId(new Item.Properties(), ResourceKey.create(Registries.ITEM, Ids.of("grooming_brush"))))
	);
	public static final LaserPointerItem LASER_POINTER_ITEM = Registration.register(
			BuiltInRegistries.ITEM,
			"laser_pointer",
			new LaserPointerItem(Registration.withId(new Item.Properties().stacksTo(1), ResourceKey.create(Registries.ITEM, Ids.of("laser_pointer"))))
	);

	public static final SoundEvent CLICKER_ON = Registration.register(BuiltInRegistries.SOUND_EVENT, "clicker_on",
			Registration.sound("clicker_on"));
	public static final SoundEvent CLICKER_OFF = Registration.register(BuiltInRegistries.SOUND_EVENT, "clicker_off",
			Registration.sound("clicker_off"));

    private static BlockBehaviour.Properties invisibleFenceProperties() {
        //? if >=1.20 {
        return BlockBehaviour.Properties.of().instabreak().sound(SoundType.GLASS).noOcclusion().dynamicShape();
        //?} else {
        /*return BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.GLASS)
                .instabreak().sound(SoundType.GLASS).noOcclusion().dynamicShape();
        *///?}
    }

    private static BlockBehaviour.Properties dogBowlProperties() {
        //? if >=26.3 {
        /*return BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(0.6F).noOcclusion()
                .pushReaction(PushReaction.POPPED);
        *///?} elif >=1.20 {
        return BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(0.6F).noOcclusion()
                .pushReaction(PushReaction.DESTROY);
        //?} else {
        /*return BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE)
                .sound(SoundType.STONE).strength(0.6F).noOcclusion();
        *///?}
    }

	private static final Codec<OwnerComponent> OWNER_COMPONENT_CODEC = RecordCodecBuilder.create(builder -> builder.group(
			UUIDUtil.AUTHLIB_CODEC.fieldOf("uuid").forGetter(OwnerComponent::uuid),
            Codec.STRING.fieldOf("name").forGetter(OwnerComponent::name),
			ExtraCodecs.optionalEmptyMap(UUIDUtil.AUTHLIB_CODEC).fieldOf("owned").forGetter(OwnerComponent::owned),
			ExtraCodecs.optionalEmptyMap(Codec.STRING).fieldOf("owned_name").forGetter(OwnerComponent::ownedName),
			Codec.BOOL.optionalFieldOf("can_leash_forcibly", false).forGetter(OwnerComponent::canLeashForcibly)
    ).apply(builder, OwnerComponent::new));
	private static final Codec<List<Either<TagKey<Block>, ResourceKey<Block>>>> CAN_INTERACT_CODEC = PawsFilter.codec(Registries.BLOCK);
	private static final Codec<List<Either<TagKey<Item>, ResourceKey<Item>>>> HELD_ITEMS_CODEC = PawsFilter.codec(Registries.ITEM);
	// Below 1.20.5 these are stack-tag paths; "owner" is what the shipped 1.20.1 jar wrote, so neither moves.
	//? if >=1.20.5 {
	public static final DataComponentType<OwnerComponent> OWNER_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"owner_component",
			DataComponentType.<OwnerComponent>builder().persistent(OWNER_COMPONENT_CODEC).build());
	public static final DataComponentType<Boolean> FORCED_CRAWL_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"forced_crawl_component",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());
	public static final DataComponentType<SpeechMode> SPEECH_MODE_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"speech_mode_component",
			DataComponentType.<SpeechMode>builder()
					.persistent(SpeechMode.CODEC)
					.networkSynchronized(ByteBufCodecs.idMapper(SpeechMode::byId, SpeechMode::ordinal))
					.build());
	public static final DataComponentType<Boolean> COMMANDS_BLOCKED_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"commands_blocked_component",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<Boolean> VISION_OBSCURED_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"vision_obscured_component",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<Boolean> MOVEMENT_RESTRAINED_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"movement_restrained_component",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<List<Either<TagKey<Block>, ResourceKey<Block>>>> CAN_INTERACT_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"can_interact_component",
			DataComponentType.<List<Either<TagKey<Block>, ResourceKey<Block>>>>builder().persistent(CAN_INTERACT_CODEC).build());
	public static final DataComponentType<List<Either<TagKey<Item>, ResourceKey<Item>>>> HELD_ITEMS_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"held_items_component",
			DataComponentType.<List<Either<TagKey<Item>, ResourceKey<Item>>>>builder().persistent(HELD_ITEMS_CODEC).build());
	public static final DataComponentType<Boolean> CAN_ATTACK_MOBS_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"can_attack_mobs",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<Boolean> CAN_ATTACK_PLAYERS_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"can_attack_players",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	public static final DataComponentType<Boolean> DIAMOND_LOCKED_COMPONENT_TYPE = Registration.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			"diamond_locked",
			DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
	//?} else {
	/*public static final DataComponentType<OwnerComponent> OWNER_COMPONENT_TYPE =
			DataComponentType.at(OWNER_COMPONENT_CODEC, "owner");
	public static final DataComponentType<Boolean> FORCED_CRAWL_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "forced_crawl");
	public static final DataComponentType<SpeechMode> SPEECH_MODE_COMPONENT_TYPE =
			DataComponentType.at(SpeechMode.CODEC, "playercollars", "speech_mode");
	public static final DataComponentType<Boolean> COMMANDS_BLOCKED_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "commands_blocked");
	public static final DataComponentType<Boolean> VISION_OBSCURED_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "vision_obscured");
	public static final DataComponentType<Boolean> MOVEMENT_RESTRAINED_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "movement_restrained");
	public static final DataComponentType<List<Either<TagKey<Block>, ResourceKey<Block>>>> CAN_INTERACT_COMPONENT_TYPE =
			DataComponentType.at(CAN_INTERACT_CODEC, "playercollars", "can_interact");
	public static final DataComponentType<List<Either<TagKey<Item>, ResourceKey<Item>>>> HELD_ITEMS_COMPONENT_TYPE =
			DataComponentType.at(HELD_ITEMS_CODEC, "playercollars", "held_items");
	public static final DataComponentType<Boolean> CAN_ATTACK_MOBS_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "can_attack_mobs");
	public static final DataComponentType<Boolean> CAN_ATTACK_PLAYERS_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "can_attack_players");
	public static final DataComponentType<Boolean> DIAMOND_LOCKED_COMPONENT_TYPE =
			DataComponentType.at(Codec.BOOL, "playercollars", "diamond_locked");
	*///?}
	public static final RewardTreatPouchItem REWARD_TREAT_POUCH_ITEM = Registration.register(
			BuiltInRegistries.ITEM,
			"reward_treat_pouch",
			new RewardTreatPouchItem(Registration.withId(new Item.Properties().durability(64), ResourceKey.create(Registries.ITEM, Ids.of("reward_treat_pouch"))))
	);

	public static final Holder<Attribute> ATTR_CLICKER_DISTANCE = Registration.registerForHolder(
			BuiltInRegistries.ATTRIBUTE, "clicker_distance",
			new RangedAttribute("attribute.playercollars.clicker_distance", 4, 0, 32));
	public static final Holder<Attribute> ATTR_LEASH_DISTANCE = Registration.registerForHolder(
			BuiltInRegistries.ATTRIBUTE, "leash_distance",
			new RangedAttribute("attribute.playercollars.leash_distance", 4, 2, 16));

	public static final BoolGameRule PLAYER_LEASHES_BREAK_RULE = BoolGameRule.register(
			"player_leashes_break", "playerLeashesBreak", true);
    public static final BoolGameRule LEASHED_PLAYERS_RIDE_ENTITIES = BoolGameRule.register(
			"leashed_players_ride_entities", "leashedPlayersRideEntities", false);
	public static final BoolGameRule ALLOW_ATTACK_OWNER = BoolGameRule.register(
			"player_allow_attack_owner", "playerAllowAttackOwner", false);
	public static final BoolGameRule ALLOW_UNLEASH_OTHER = BoolGameRule.register(
			"allow_unleash_unowned_player", "allowUnleashUnownedPlayer", true);
	public static final BoolGameRule CLICKER_TURN_TOGGLE = BoolGameRule.register(
			"clicker_turn_toggle", "clickerTurnToggle", false);
	public static final BoolGameRule FORCE_EQUIP_UNOWNED = BoolGameRule.register(
			"collar_force_equip_unowned", "collarForceEquipUnowned", false);
	public static final BoolGameRule FORCE_EQUIP_CLAIMS_COLLAR = BoolGameRule.register(
			"collar_force_equip_claims_collar", "collarForceEquipClaimsCollar", true);
	public static final BoolGameRule FORCE_EQUIP_ASSIGNS_OWNED = BoolGameRule.register(
			"collar_force_equip_assigns_owned", "collarForceEquipAssignsOwned", false);
	public static final BoolGameRule FORCE_EQUIP_UNASSIGNED = BoolGameRule.register(
			"collar_force_equip_unassigned", "collarForceEquipUnassigned", true);
	// Off: consent is only what the deed crafted into this collar recorded; on: the newest deed signed.
	public static final BoolGameRule DEEDS_GLOBAL = BoolGameRule.register(
			"collar_deeds_global", "collarDeedsGlobal", true);
	public static final BoolGameRule LOCK_UNASSIGNED = BoolGameRule.register(
			"collar_lock_unassigned", "collarLockUnassigned", true);

	public static final DogBedBlock[] DOG_BEDS = new DogBedBlock[DyeColor.values().length];
	public static final BlockItem[] DOG_BED_ITEMS = new BlockItem[DyeColor.values().length];
	public static final TagKey<Item> COLLAR_TAG = TagKey.create(Registries.ITEM, Ids.of("c", "collars"));

	public static final DyeColor[] PAWS_DYE_COLORS = DyeColor.values();
	public static final PawsItem[] PAWS_ITEMS = new PawsItem[PAWS_DYE_COLORS.length];
	public static final TagKey<Item> PAWS_TAG = TagKey.create(Registries.ITEM, Ids.of("paws"));
	/** Blocks paws may always use, whatever the allow-list says. */
	public static final TagKey<Block> PAWS_ALLOW_INTERACT = TagKey.create(Registries.BLOCK, Ids.of("paws_allow_interact"));
	// Never datapack-declared: these ride inside the allow-list components as the "no restriction" entry.
	public static final TagKey<Block> PAWS_ALL_BLOCKS_MARKER = TagKey.create(Registries.BLOCK, Ids.of("__all_blocks__"));
	public static final TagKey<Item> PAWS_ALL_ITEMS_MARKER = TagKey.create(Registries.ITEM, Ids.of("__all_items__"));
	public static final FootPawsItem[] FOOT_PAWS_ITEMS = new FootPawsItem[PAWS_DYE_COLORS.length];
	public static final TagKey<Item> FOOT_PAWS_TAG = TagKey.create(Registries.ITEM, Ids.of("foot_paws"));

	public static final DogBowlBlock[] DOG_BOWLS = new DogBowlBlock[DyeColor.values().length];
	public static final Item[] DOG_BOWL_ITEMS = new Item[DyeColor.values().length];
	public static final BlockEntityType<DogBowlBlock.DogBowlBlockEntity> DOG_BOWL_BLOCK_ENTITY;
	public static final CreativeModeTab GROUP;

    static {
        for (DyeColor c : DyeColor.values()) {
			ResourceKey<Block> blockKey = DogBowlBlock.getRegistryKey(c);
            DOG_BOWLS[c.ordinal()] = Registration.register(BuiltInRegistries.BLOCK, blockKey,
                    new DogBowlBlock(c, Registration.withId(dogBowlProperties(), blockKey)));
			ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Ids.of(blockKey));
            DOG_BOWL_ITEMS[c.ordinal()] = Registration.register(BuiltInRegistries.ITEM, itemKey,
                    new BlockItem(DOG_BOWLS[c.ordinal()], Registration.withId(new Item.Properties(), itemKey)));
        }
        DOG_BOWL_BLOCK_ENTITY = Registration.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE, "dog_bowl",
				Registration.blockEntityType(DogBowlBlock.DogBowlBlockEntity::new, DOG_BOWLS)
        );

        GROUP = Registration.creativeTab("group", Text.translatable("itemGroup.playercollars"),
                COLLAR_ITEM::getDefaultInstance, entries -> {
                            entries.accept(COLLAR_ITEM);
                            entries.accept(TAGLESS_COLLAR_ITEM);
                            entries.accept(CLICKER_ITEM);
                            entries.accept(COLLAR_LOCKER_ITEM);
                            entries.accept(DIAMOND_COLLAR_LOCKER_ITEM);
                            entries.accept(PAW_CONFIGURATION_ITEM);
                            entries.accept(INVENTORY_EDITOR_ITEM);
                            entries.accept(LASER_POINTER_ITEM);
                            entries.accept(GROOMING_BRUSH_ITEM);
                            entries.accept(REWARD_TREAT_POUCH_ITEM);
                            for (PawsItem p : PAWS_ITEMS)
                                entries.accept(p);
                            for (FootPawsItem p : FOOT_PAWS_ITEMS)
                                entries.accept(p);
                            entries.accept(DEED_OF_OWNERSHIP);
                            entries.accept(SPATULA_ITEM);
                            for (BlockItem bed : DOG_BED_ITEMS)
                                entries.accept(bed);
                            for (Item bowl : DOG_BOWL_ITEMS)
                                entries.accept(bowl);
                            entries.accept(INVISIBLE_FENCE_BLOCK_ITEM);
                        });

	}

	/** The tier-2 lock: only a Diamond Lock-inator undoes it, and the golden spatula will not. */
	public static boolean isDiamondLocked(ItemStack stack) {
		return Components.getOrDefault(stack, DIAMOND_LOCKED_COMPONENT_TYPE, false);
	}

	public static ItemStack filterStacksByOwner(Iterable<EquippedAccessories.EquippedEntry> stacks, UUID plr, UUID entity) {
		for (EquippedAccessories.EquippedEntry p : stacks) {
			ItemStack is = p.stack();
			OwnerComponent owner = Components.get(is, OWNER_COMPONENT_TYPE);
			if (owner != null && owner.uuid().equals(plr) &&
					(owner.owned().isEmpty() || owner.owned().get().equals(entity))) {
				return is;
			}
		}
		return null;
	}

	//? if <1.21 {
	/*private static final ResourceKey<Enchantment> REGENERATION_KEY =
			ResourceKey.create(Registries.ENCHANTMENT, Ids.of("regeneration"));
	private static final ResourceKey<Enchantment> SPIKED_KEY =
			ResourceKey.create(Registries.ENCHANTMENT, Ids.of("thorns"));

	// The 1.21 JSON hangs this off minecraft:post_attack; below it LivingEntityMixin calls in after a
	// successful hit. Constants match thorns.json: 0.15 x level chance, 1-5 thorns damage.
	public static void applySpikedEnchantment(ServerLevel world, LivingEntity victim, DamageSource source) {
		if (Compat.isThornsDamage(source)) return;
		if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == victim) return;

		int level = 0;
		for (ItemStack stack : EquippedAccessories.getEquipped(victim, x -> x.is(COLLAR_TAG))) {
			level = Math.max(level, Enchants.level(world.registryAccess(), SPIKED_KEY, stack));
		}
		if (level == 0 || world.getRandom().nextFloat() >= Math.min(1.0F, 0.15F * level)) return;

		Compat.hurt(attacker, world, Compat.thornsDamage(victim), 1 + world.getRandom().nextInt(5));
	}

	// Above 1.21 this is an EnchantmentEntityEffect the enchantment's JSON hangs off; see
	// RegenerationEnchantmentEffect. Below it there is no such hook, so the tick applies it.
	private static void applyRegeneration(ServerPlayer player) {
		// Regeneration heals on `remaining duration % (50 >> amplifier) == 0`, which a short instance
		// refreshed every ten ticks never reaches. Top it up only once it has nearly run out.
		MobEffectInstance active = player.getEffect(MobEffects.REGENERATION);
		if (active != null && active.getDuration() > 20) return;

		for (ItemStack stack : EquippedAccessories.getEquipped(player, x -> x.is(COLLAR_TAG))) {
			int level = Enchants.level(Compat.level(player).registryAccess(), REGENERATION_KEY, stack);
			if (level == 0) continue;
			OwnerComponent oc = Components.get(stack, OWNER_COMPONENT_TYPE);
			if (oc == null) continue;
			Player owner = Compat.level(player).getPlayerByUUID(oc.uuid());
			if (owner != null && owner.distanceTo(player) < 16) {
				player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, level - 1, false, false, false));
				return;
			}
		}
	}
	*///?}

	public static InteractionResult pullPlayerTowards(ServerPlayer plr, Vec3 towards, double minDist, double maxDist, UnaryOperator<Double> getFactor) {
		Vec3 vecTo = towards.subtract(plr.position());
		double distance = vecTo.length();
		if (distance < minDist) return InteractionResult.PASS;
		if (distance > maxDist) return InteractionResult.FAIL;

		Compat.push(plr, vecTo.scale(Math.abs(getFactor.apply(distance))));
		plr.connection.send(new ClientboundSetEntityMotionPacket(plr));
		// TODO(26.1.2): hasImpulse is gone; the motion packet above keeps server and client in sync.
		return InteractionResult.SUCCESS;
	}

	/** Paws may be told not to attack mobs, players, or either; the owner sets this from the configurator. */
	public static boolean pawsBlockAttack(Player attacker, Entity target) {
        if (!FeatureRules.CAN_USE_PAWS.enabled(Compat.level(attacker))) return false;
		DataComponentType<Boolean> permission = target instanceof Player
				? CAN_ATTACK_PLAYERS_COMPONENT_TYPE : CAN_ATTACK_MOBS_COMPONENT_TYPE;
		for (ItemStack paws : EquippedAccessories.getEquipped(attacker, x -> x.is(PAWS_TAG))) {
			if (Components.getOrDefault(paws, permission, true)) continue;
			Compat.sendOverlayMessage(attacker, Text.translatable(target instanceof Player
					? "message.playercollars.no_attack_players"
					: "message.playercollars.no_attack_mobs").withStyle(ChatFormatting.RED));
			return true;
		}
		return false;
	}

	public static boolean blockLeashKnotBreak(ServerLevel world, Player player, LeashFenceKnotEntity entity) {
		if (entity.equals(((LeashImpl) player).leashplayers$getProxyLeashHolder())) {
			Compat.sendOverlayMessage(player, Text.translatable("message.playercollars.no_break_fence").withStyle(ChatFormatting.RED));
			return true;
		}
		if (!ALLOW_UNLEASH_OTHER.get(world)) {
			for (LeashProxyEntity le : Compat.leashProxiesHeldBy(world, entity)) {
				LivingEntity leashTarget = le.getLeashTarget();
				for (ItemStack stack : EquippedAccessories.getEquipped(leashTarget, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
					OwnerComponent oc = Components.get(stack, OWNER_COMPONENT_TYPE);
					if (oc == null || !oc.owned().orElseGet(leashTarget::getUUID).equals(leashTarget.getUUID())) continue;
					if (!player.getUUID().equals(oc.uuid())) {
						Compat.sendOverlayMessage(player, Text.translatable("message.playercollars.no_break_fence_other", le.getLeashTarget().getName()).withStyle(ChatFormatting.RED));
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean attachHeldPlayerLeashesToFence(Player player, Level world, BlockPos pos) {
		if (!(world instanceof ServerLevel serverWorld)) return false;

		LeashFenceKnotEntity knot = null;
		boolean attached = false;
		for (LeashProxyEntity proxy : serverWorld.getEntitiesOfClass(
				LeashProxyEntity.class,
				player.getBoundingBox().inflate(32.0D),
				proxy -> proxy.isAlive() && proxy.getLeashHolder() == player
		)) {
			LivingEntity target = proxy.getLeashTarget();
			ItemStack collar = EquippedAccessories.findOwned(target, x -> x.is(PlayerCollarsMod.COLLAR_TAG), player.getUUID(), target.getUUID());
			if (collar == null) continue;

			if (knot == null) {
				knot = LeashFenceKnotEntity.getOrCreateKnot(serverWorld, pos);
			}
			proxy.setLeashedTo(knot, true);
			attached = true;
		}
		return attached;
	}

	//? if neoforge {
	/*// NeoForge's entrypoint: mod-bus listeners here, then the loader-neutral common init below.
	public PlayerCollarsMod(IEventBus modBus) {
		modBus.addListener(Registration::flush);
		modBus.addListener(Net::flush);
		onInitialize();
	}
	*///?}
	//? if forge {
	/*// Forge injects nothing into the @Mod constructor, and its Net opens the channel from a static
	// initialiser, so only the registry queue needs a mod-bus listener here.
	public PlayerCollarsMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        //? if >=1.19 {
        modBus.addListener(Registration::flush);
        //?}
        onInitialize();
        //? if <1.19 {
        /^Registration.bindLegacy(modBus);
        ^///?}
	}
	*///?}

	//? if fabric {
	@Override
	//?}
	public void onInitialize() {
		//? if >=1.21 {
		Registration.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, "regeneration_effect", RegenerationEnchantmentEffect.CODEC);
		//?} else {
		/*// No data-driven enchantments below 1.21, so the ones shipped as JSON are declared in code.
		// Rarity stands in for the JSON's weight -- COMMON 10, UNCOMMON 5, RARE 2, VERY_RARE 1.
		Registration.register(BuiltInRegistries.ENCHANTMENT, "regeneration", new CollarEnchantment(Enchantment.Rarity.RARE, 1, 20, 25, 50, 25));
		Registration.register(BuiltInRegistries.ENCHANTMENT, "laser_reach",
				new CollarEnchantment(Enchantment.Rarity.UNCOMMON, 3, 10, 10, 50, 10, stack -> stack.is(LASER_POINTER_ITEM)));
		Registration.register(BuiltInRegistries.ENCHANTMENT, "short_leash", new CollarEnchantment(Enchantment.Rarity.UNCOMMON, 2, 12, 7, 25, 0));
		Registration.register(BuiltInRegistries.ENCHANTMENT, "clicker_walk",
				new CollarEnchantment(Enchantment.Rarity.RARE, 3, 8, 9, 65, 9, stack -> stack.is(CLICKER_ITEM)));
		Registration.register(BuiltInRegistries.ENCHANTMENT, "thorns", new CollarEnchantment(Enchantment.Rarity.VERY_RARE, 3, 10, 20, 60, 20));
		Registration.register(BuiltInRegistries.ENCHANTMENT, "clicker",
				new CollarEnchantment(Enchantment.Rarity.RARE, 3, 8, 9, 65, 9, stack -> stack.is(CLICKER_ITEM)));
		*///?}
		Registration.register(BuiltInRegistries.RECIPE_SERIALIZER, "owner_transfer", OwnershipCraftingRecipe.Serializer.INSTANCE);
		Registration.register(BuiltInRegistries.RECIPE_SERIALIZER, "laser_dye", LaserCraftingRecipe.Serializer.INSTANCE);
		Registration.register(BuiltInRegistries.RECIPE_SERIALIZER, "dye", DyeCraftingRecipe.Serializer.INSTANCE);
		// Declared here because the server needs them to send; handlers attach in RegisterClient.
		Net.registerServerbound(PacketUpdateCollar.ID, PacketUpdateCollar.CODEC, PacketUpdateCollar::handle);
		Net.registerServerbound(PacketStampDeed.ID, PacketStampDeed.CODEC, PacketStampDeed::handle);
		Net.registerServerbound(PacketTogglePetControl.ID, PacketTogglePetControl.CODEC, PacketTogglePetControl::handle);
		Net.registerServerbound(PacketOpenPawsConfig.ID, PacketOpenPawsConfig.CODEC, PacketOpenPawsConfig::handle);
		Net.registerServerbound(PacketSavePawsConfig.ID, PacketSavePawsConfig.CODEC, PacketSavePawsConfig::handle);

		Events.onRegisterCommands(CollarCommand::register);
        FeatureRules.initialize();

		Net.registerClientbound(PacketLookAtLerped.ID, PacketLookAtLerped.CODEC);
		Net.registerClientbound(PacketOpenPetControl.ID, PacketOpenPetControl.CODEC);
		Net.registerClientbound(PacketPawsConfig.ID, PacketPawsConfig.CODEC);

		Events.onServerTickEnd(server -> {
			if (server.getTickCount() % 10 != 0) return;
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (PetControlHelper.isVisionObscured(player)) {
					player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, false));
				}
				//? if <1.21 {
				/*applyRegeneration(player);
				*///?}
			}
		});

		for (int i = 0; i < PAWS_DYE_COLORS.length; i++) {
			DyeColor c = PAWS_DYE_COLORS[i];
			ResourceKey<Item> itemKey = PawsItem.getRegistryKey(c);
			PAWS_ITEMS[i] = Registration.register(BuiltInRegistries.ITEM, itemKey,
					new PawsItem(itemKey, c.getFireworkColor(), 0xF196CF));
            itemKey = FootPawsItem.getRegistryKey(c);
			FOOT_PAWS_ITEMS[i] = Registration.register(BuiltInRegistries.ITEM, itemKey,
					new FootPawsItem(itemKey, c.getFireworkColor(), 0xF196CF));
		}

		for (DyeColor c : DyeColor.values()) {
			ResourceKey<Block> blockKey = DogBedBlock.getRegistryKey(c);
			DOG_BEDS[c.ordinal()] = Registration.register(BuiltInRegistries.BLOCK, blockKey, new DogBedBlock(c, blockKey));
			ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Ids.of(blockKey));
			// 26.3 deleted BedItem; beds are plain BlockItems now, and nothing here used the subclass.
			//? if >=26.3 {
			/*DOG_BED_ITEMS[c.ordinal()] = Registration.register(BuiltInRegistries.ITEM, itemKey,
					new BlockItem(DOG_BEDS[c.ordinal()], Registration.withId((new Item.Properties()).stacksTo(1), itemKey)));
			*///?} else {
			DOG_BED_ITEMS[c.ordinal()] = Registration.register(BuiltInRegistries.ITEM, itemKey,
					new BedItem(DOG_BEDS[c.ordinal()], Registration.withId((new Item.Properties()).stacksTo(1), itemKey)));
			//?}
		}

		Events.onBlockBreakBefore((Level level, Player player, BlockPos blockPos) -> {
			if (level.isClientSide()) return true;
			if (player.isSpectator()) return true;
			// Through the knot guard, so the block under a knot is as protected as the knot itself.
			ServerLevel serverLevel = (ServerLevel) level;
			for (LeashFenceKnotEntity knot : serverLevel.getEntitiesOfClass(
					LeashFenceKnotEntity.class, new AABB(blockPos).inflate(0.5D))) {
				if (blockPos.equals(knot.getPos()) && blockLeashKnotBreak(serverLevel, player, knot)) return false;
			}
			return true;
		});

		Events.onUseEntity((player, world, hand, entity) -> {
			if (!world.isClientSide() && entity instanceof Player pet) {
				ItemStack stack = player.getItemInHand(hand);

				if (Compat.food(stack) != null) {
					// Magic Cooldown: Prevents spamming when holding right-click!
					if (Compat.isOnCooldown(player, stack)) return InteractionResult.PASS;

					ItemStack collar = EquippedAccessories.findOwned(pet, x -> x.is(PlayerCollarsMod.COLLAR_TAG), player.getUUID(), pet.getUUID());

					if (collar != null) {
						FoodProperties food = Compat.food(stack);

						// Check if the pet is actually hungry (or if it's a special food that can always be eaten)
						if (pet.getFoodData().needsFood() || food.canAlwaysEat()) {
							Compat.feed(pet.getFoodData(), food);

							// Set a 10-tick (half-second) cooldown for the owner
							Compat.addCooldown(player, stack, 10);

							world.playSound(null, pet.blockPosition(), Compat.sound(SoundEvents.GENERIC_EAT), SoundSource.PLAYERS, 1.0f, 1.0f);
							((ServerLevel) world).sendParticles(ParticleTypes.HEART, pet.getX(), pet.getY() + 1.0, pet.getZ(), 3, 0.3, 0.3, 0.3, 0.0);

							if (!player.isCreative()) stack.shrink(1);
							return InteractionResult.SUCCESS;
						} else {
							Compat.sendOverlayMessage(player, Text.literal("Your pet's tummy is already full!").withStyle(ChatFormatting.GREEN));
							return InteractionResult.FAIL;
						}
					}
				}
			}
			return InteractionResult.PASS;
		});

		Events.onAttackEntity((Player player, Level world, Entity entity) -> {
			if (world.isClientSide()) return InteractionResult.PASS;
			if (player.isSpectator()) return InteractionResult.PASS;

			ServerLevel sworld = (ServerLevel) world;
			for (ItemStack collarStack : EquippedAccessories.getEquipped(player, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
				OwnerComponent owner = Components.get(collarStack, OWNER_COMPONENT_TYPE);
				if (owner != null && owner.uuid().equals(entity.getUUID())) {
					// Collared players are allowed to attack owners, but have 75% damage returned to them
					Compat.sendOverlayMessage(player, Text.translatable("message.playercollars.no_attack_owner").withStyle(ChatFormatting.RED));

					if (!ALLOW_ATTACK_OWNER.get(sworld)) {
						return InteractionResult.FAIL;
					}

					double f = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
					f = (f - 1) * 0.75 + 1;
					Compat.hurt(player, sworld, Compat.playerAttackDamage(player), (float) Math.ceil(f));
					return InteractionResult.PASS;
				}
			}

			if (entity instanceof LeashFenceKnotEntity ke && blockLeashKnotBreak(sworld, player, ke)) return InteractionResult.FAIL;
			if (pawsBlockAttack(player, entity)) return InteractionResult.FAIL;
			return InteractionResult.PASS;
		});

		Events.onUseItem((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.isEmpty() || !PawsItem.hasPaws(player)) return InteractionResult.PASS;
			// Feeding yourself stays barred whatever the allow-list says.
			if (Compat.food(stack) != null) {
				if (!world.isClientSide()) {
					Compat.sendOverlayMessage(player, Text.literal("Paws can't feed themselves by hand.").withStyle(ChatFormatting.RED));
				}
				return InteractionResult.FAIL;
			}
			return PawsItem.shouldPreventItemUse(player, stack) ? InteractionResult.FAIL : InteractionResult.PASS;
		});

		Events.onUseBlock((player, world, hand, hitResult) -> {
			if (player.isSpectator()) return InteractionResult.PASS;
			BlockState state = world.getBlockState(hitResult.getBlockPos());
			if (state.is(BlockTags.FENCES) && attachHeldPlayerLeashesToFence(player, world, hitResult.getBlockPos())) {
				return InteractionResult.SUCCESS;
			}
			if (PawsItem.shouldPreventBlockInteraction(player, state, player.getItemInHand(hand), player.isShiftKeyDown())) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		//? if dual {
		/*// Last, because both binders walk the paw arrays the loop above fills.
		if (AccessoryLibraries.curios()) CuriosBinding.register();
		if (AccessoryLibraries.accessories()) AccessoriesBinding.register();
		*///?}
	}
}
