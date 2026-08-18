package com.dipilodopilasaurus.leashablecollars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? if fabric {
import net.fabricmc.api.ModInitializer;
//?} else {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.dipilodopilasaurus.leashablecollars.block.DogBedBlock;
import com.dipilodopilasaurus.leashablecollars.block.DogBowlBlock;
import com.dipilodopilasaurus.leashablecollars.block.InvisibleFenceBlock;
import com.dipilodopilasaurus.leashablecollars.item.*;
import com.dipilodopilasaurus.leashablecollars.leash.LeashImpl;
import com.dipilodopilasaurus.leashablecollars.leash.LeashProxyEntity;
import com.dipilodopilasaurus.leashablecollars.network.*;

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
    public static final CollarItem COLLAR_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarItem.REGISTRY_KEY, new CollarItem(false));
    public static final CollarItem TAGLESS_COLLAR_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarItem.TAGLESS_REGISTRY_KEY, new CollarItem(true));
    public static final ClickerItem CLICKER_ITEM = Registration.register(BuiltInRegistries.ITEM, ClickerItem.REGISTRY_KEY, new ClickerItem());
    public static final DeedItem DEED_OF_OWNERSHIP = Registration.register(BuiltInRegistries.ITEM, "deed_of_ownership", new DeedItem());
    public static final Item DEED_OF_OWNERSHIP_STAMPED = Registration.register(BuiltInRegistries.ITEM, "stamped_deed_of_ownership", new StampedDeedItem());
    public static final InvisibleFenceBlock INVISIBLE_FENCE_BLOCK = Registration.register(BuiltInRegistries.BLOCK, InvisibleFenceBlock.REGISTRY_KEY,
            new InvisibleFenceBlock(BlockBehaviour.Properties.of().instabreak().sound(SoundType.GLASS).noOcclusion().dynamicShape()));
    public static final BlockItem INVISIBLE_FENCE_BLOCK_ITEM = Registration.register(BuiltInRegistries.ITEM, InvisibleFenceBlock.ITEM_REGISTRY_KEY,
			new BlockItem(INVISIBLE_FENCE_BLOCK, Registration.withId(new Item.Properties(), InvisibleFenceBlock.ITEM_REGISTRY_KEY)));
    public static final PawSetupItem PAW_CONFIGURATION_ITEM = Registration.register(BuiltInRegistries.ITEM, PawSetupItem.REGISTRY_KEY, new PawSetupItem());
    public static final CollarLockerItem COLLAR_LOCKER_ITEM = Registration.register(BuiltInRegistries.ITEM, CollarLockerItem.REGISTRY_KEY, new CollarLockerItem());
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
			SoundEvent.createVariableRangeEvent(Ids.of("clicker_on")));
	public static final SoundEvent CLICKER_OFF = Registration.register(BuiltInRegistries.SOUND_EVENT, "clicker_off",
			SoundEvent.createVariableRangeEvent(Ids.of("clicker_off")));

	private static final Codec<OwnerComponent> OWNER_COMPONENT_CODEC = RecordCodecBuilder.create(builder -> builder.group(
			UUIDUtil.AUTHLIB_CODEC.fieldOf("uuid").forGetter(OwnerComponent::uuid),
            Codec.STRING.fieldOf("name").forGetter(OwnerComponent::name),
			ExtraCodecs.optionalEmptyMap(UUIDUtil.AUTHLIB_CODEC).fieldOf("owned").forGetter(OwnerComponent::owned),
			ExtraCodecs.optionalEmptyMap(Codec.STRING).fieldOf("owned_name").forGetter(OwnerComponent::ownedName)
    ).apply(builder, OwnerComponent::new));
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

	public static final DogBedBlock[] DOG_BEDS = new DogBedBlock[DyeColor.values().length];
	public static final BedItem[] DOG_BED_ITEMS = new BedItem[DyeColor.values().length];
	public static final TagKey<Item> COLLAR_TAG = TagKey.create(Registries.ITEM, Ids.of("c", "collars"));

	public static final DyeColor[] PAWS_DYE_COLORS = new DyeColor[]{DyeColor.WHITE, DyeColor.LIGHT_GRAY,
			DyeColor.GRAY, DyeColor.BLACK, DyeColor.BLUE, DyeColor.RED, DyeColor.PURPLE};
	public static final PawsItem[] PAWS_ITEMS = new PawsItem[PAWS_DYE_COLORS.length];
	public static final TagKey<Item> PAWS_TAG = TagKey.create(Registries.ITEM, Ids.of("paws"));
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
                    new DogBowlBlock(c, Registration.withId(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(0.6F).noOcclusion().pushReaction(PushReaction.DESTROY), blockKey)));
			ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Ids.of(blockKey));
            DOG_BOWL_ITEMS[c.ordinal()] = Registration.register(BuiltInRegistries.ITEM, itemKey,
                    new BlockItem(DOG_BOWLS[c.ordinal()], Registration.withId(new Item.Properties(), itemKey)));
        }
        DOG_BOWL_BLOCK_ENTITY = Registration.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE, "dog_bowl",
				Registration.blockEntityType(DogBowlBlock.DogBowlBlockEntity::new, DOG_BOWLS)
        );

        GROUP = Registration.register(BuiltInRegistries.CREATIVE_MODE_TAB, "group",
                Registration.creativeTab().title(Component.translatable("itemGroup.playercollars"))
                        .icon(COLLAR_ITEM::getDefaultInstance)
                        .displayItems(((displayContext, entries) -> {
                            entries.accept(COLLAR_ITEM);
                            entries.accept(TAGLESS_COLLAR_ITEM);
                            entries.accept(CLICKER_ITEM);
                            entries.accept(COLLAR_LOCKER_ITEM);
                            entries.accept(PAW_CONFIGURATION_ITEM);
                            entries.accept(LASER_POINTER_ITEM);
                            entries.accept(GROOMING_BRUSH_ITEM);
                            entries.accept(REWARD_TREAT_POUCH_ITEM);
                            for (PawsItem p : PAWS_ITEMS)
                                entries.accept(p);
                            for (FootPawsItem p : FOOT_PAWS_ITEMS)
                                entries.accept(p);
                            entries.accept(DEED_OF_OWNERSHIP);
                            entries.accept(SPATULA_ITEM);
                            for (BedItem bed : DOG_BED_ITEMS)
                                entries.accept(bed);
                            for (Item bowl : DOG_BOWL_ITEMS)
                                entries.accept(bowl);
                            entries.accept(INVISIBLE_FENCE_BLOCK_ITEM);
                        })).build());

	}

	public static ItemStack filterStacksByOwner(Iterable<EquippedAccessories.EquippedEntry> stacks, UUID plr, UUID entity) {
		for (EquippedAccessories.EquippedEntry p : stacks) {
			ItemStack is = p.stack();
			OwnerComponent owner = is.get(OWNER_COMPONENT_TYPE);
			if (owner != null && owner.uuid().equals(plr) &&
					(owner.owned().isEmpty() || owner.owned().get().equals(entity))) {
				return is;
			}
		}
		return null;
	}

	public static InteractionResult pullPlayerTowards(ServerPlayer plr, Vec3 towards, double minDist, double maxDist, UnaryOperator<Double> getFactor) {
		Vec3 vecTo = towards.subtract(plr.position());
		double distance = vecTo.length();
		if (distance < minDist) return InteractionResult.PASS;
		if (distance > maxDist) return InteractionResult.FAIL;

		plr.push(vecTo.scale(Math.abs(getFactor.apply(distance))));
		plr.connection.send(new ClientboundSetEntityMotionPacket(plr));
		// TODO(26.1.2): The old hasImpulse flag no longer exists; the motion packet above keeps the server/client sync.
		return InteractionResult.SUCCESS;
	}

	public static boolean blockLeashKnotBreak(ServerLevel world, Player player, LeashFenceKnotEntity entity) {
		if (entity.equals(((LeashImpl) player).leashplayers$getProxyLeashHolder())) {
			Compat.sendOverlayMessage(player, Component.translatable("message.playercollars.no_break_fence").withStyle(ChatFormatting.RED));
			return true;
		}
		if (!ALLOW_UNLEASH_OTHER.get(world)) {
			List<Leashable> list = Leashable.leashableLeashedTo(entity);
			for (Leashable l : list) {
				if (!(l instanceof LeashProxyEntity le)) continue;
				LivingEntity leashTarget = le.getLeashTarget();
				for (ItemStack stack : EquippedAccessories.getEquipped(leashTarget, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
					OwnerComponent oc = stack.get(OWNER_COMPONENT_TYPE);
					if (oc == null || !oc.owned().orElseGet(leashTarget::getUUID).equals(leashTarget.getUUID())) continue;
					if (!player.getUUID().equals(oc.uuid())) {
						Compat.sendOverlayMessage(player, Component.translatable("message.playercollars.no_break_fence_other", le.getLeashTarget().getName()).withStyle(ChatFormatting.RED));
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
			ItemStack collar = EquippedAccessories.findOwned(target, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG), player.getUUID(), target.getUUID());
			if (collar == null) continue;

			if (knot == null) {
				knot = LeashFenceKnotEntity.getOrCreateKnot(serverWorld, pos);
			}
			proxy.setLeashedTo(knot, true);
			attached = true;
		}
		return attached;
	}

	//? if !fabric {
	/*// NeoForge's entrypoint: mod-bus listeners here, then the loader-neutral common init below.
	public PlayerCollarsMod(IEventBus modBus) {
		modBus.addListener(Registration::flush);
		modBus.addListener(Net::flush);
		onInitialize();
	}
	*///?}

	//? if fabric {
	@Override
	//?}
	public void onInitialize() {
		Registration.register(BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE, "regeneration_effect", RegenerationEnchantmentEffect.CODEC);
		Registration.register(BuiltInRegistries.RECIPE_SERIALIZER, "owner_transfer", OwnershipCraftingRecipe.Serializer.INSTANCE);
		// Clientbound types are declared here because the server needs them to send; their handlers
		// attach in RegisterClient. See Net.
		Net.registerServerbound(PacketUpdateCollar.ID, PacketUpdateCollar.CODEC, PacketUpdateCollar::handle);
		Net.registerServerbound(PacketStampDeed.ID, PacketStampDeed.CODEC, PacketStampDeed::handle);
		Net.registerServerbound(PacketTogglePetControl.ID, PacketTogglePetControl.CODEC, PacketTogglePetControl::handle);

		Net.registerClientbound(PacketLookAtLerped.ID, PacketLookAtLerped.CODEC);
		Net.registerClientbound(PacketOpenPetControl.ID, PacketOpenPetControl.CODEC);

		Events.onServerTickEnd(server -> {
			if (server.getTickCount() % 10 != 0) return;
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (PetControlHelper.isVisionObscured(player)) {
					player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, false));
				}
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
			DOG_BED_ITEMS[c.ordinal()] = Registration.register(BuiltInRegistries.ITEM, itemKey,
					new BedItem(DOG_BEDS[c.ordinal()], Registration.withId((new Item.Properties()).stacksTo(1), itemKey)));
		}

		Events.onBlockBreakBefore((Level level, Player player, BlockPos blockPos) -> {
			if (level.isClientSide()) return true;
			if (player.isSpectator()) return true;
			Entity leashHolderEntity = ((LeashImpl) player).leashplayers$getProxyLeashHolder();
			if (leashHolderEntity instanceof LeashFenceKnotEntity knot && blockPos.equals(knot.getPos())) {
				Compat.sendOverlayMessage(player, Component.translatable("message.playercollars.no_break_fence").withStyle(ChatFormatting.RED));
				return false;
			}
			return true;
		});

		Events.onUseEntity((player, world, hand, entity) -> {
			if (!world.isClientSide() && entity instanceof Player pet) {
				ItemStack stack = player.getItemInHand(hand);

				if (stack.has(DataComponents.FOOD)) {
					// Magic Cooldown: Prevents spamming when holding right-click!
					if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.PASS;

					ItemStack collar = EquippedAccessories.findOwned(pet, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG), player.getUUID(), pet.getUUID());

					if (collar != null) {
						FoodProperties food = stack.get(DataComponents.FOOD);

						// Check if the pet is actually hungry (or if it's a special food that can always be eaten)
						if (pet.getFoodData().needsFood() || food.canAlwaysEat()) {
							pet.getFoodData().eat(food);

							// Set a 10-tick (half-second) cooldown for the owner
							player.getCooldowns().addCooldown(stack, 10);

							world.playSound(null, pet.blockPosition(), Compat.sound(SoundEvents.GENERIC_EAT), SoundSource.PLAYERS, 1.0f, 1.0f);
							((ServerLevel) world).sendParticles(ParticleTypes.HEART, pet.getX(), pet.getY() + 1.0, pet.getZ(), 3, 0.3, 0.3, 0.3, 0.0);

							if (!player.isCreative()) stack.shrink(1);
							return InteractionResult.SUCCESS;
						} else {
							Compat.sendOverlayMessage(player, Component.literal("Your pet's tummy is already full!").withStyle(ChatFormatting.GREEN));
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
			for (ItemStack collarStack : EquippedAccessories.getEquipped(player, (x) -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
				OwnerComponent owner = collarStack.get(OWNER_COMPONENT_TYPE);
				if (owner != null && owner.uuid().equals(entity.getUUID())) {
					// Collared players are allowed to attack owners, but have 75% damage returned to them
					Compat.sendOverlayMessage(player, Component.translatable("message.playercollars.no_attack_owner").withStyle(ChatFormatting.RED));

					if (!ALLOW_ATTACK_OWNER.get(sworld)) {
						return InteractionResult.FAIL;
					}

					double f = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
					f = (f - 1) * 0.75 + 1;
					player.hurtServer(sworld, player.damageSources().playerAttack(player), (float) Math.ceil(f));
					return InteractionResult.PASS;
				}
			}

			if (entity instanceof LeashFenceKnotEntity ke && blockLeashKnotBreak(sworld, player, ke)) return InteractionResult.FAIL;
			return InteractionResult.PASS;
		});

		Events.onUseItem((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (!stack.isEmpty() && PawsItem.hasPaws(player)) {
				if (!world.isClientSide() && stack.has(DataComponents.FOOD)) {
					Compat.sendOverlayMessage(player, Component.literal("Paws can't feed themselves by hand.").withStyle(ChatFormatting.RED));
				}
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});

		Events.onUseBlock((player, world, hand, hitResult) -> {
			if (player.isSpectator()) return InteractionResult.PASS;
			BlockState state = world.getBlockState(hitResult.getBlockPos());
			if (state.is(BlockTags.FENCES) && attachHeldPlayerLeashesToFence(player, world, hitResult.getBlockPos())) {
				return InteractionResult.SUCCESS;
			}
			if (PawsItem.shouldPreventBlockInteraction(player, state)) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
	}
}
