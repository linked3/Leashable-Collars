package com.dipilodopilasaurus.leashablecollars.block;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.Text;
import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import com.dipilodopilasaurus.leashablecollars.Compat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if <1.21.2 && >=1.20.5 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >=1.21.2 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else {
/*import net.minecraft.world.level.LevelAccessor;
*///?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}
//? if <1.21.6 {
/*import net.minecraft.nbt.CompoundTag;
*///?}
//? if >=1.20.5 && <1.21.6 {
/*import net.minecraft.core.HolderLookup;
*///?}
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.dipilodopilasaurus.leashablecollars.EquippedAccessories;
import com.dipilodopilasaurus.leashablecollars.Ids;
import com.dipilodopilasaurus.leashablecollars.PlayerCollarsMod;


public class DogBowlBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE_BASE = Shapes.or(
            Block.box(2.0, 0.0, 1.0, 14.0, 5.0, 2.0),
            Block.box(2.0, 0.0, 14.0, 14.0, 5.0, 15.0),
            Block.box(1.0, 0.0, 1.0, 2.0, 5.0, 15.0),
            Block.box(14.0, 0.0, 1.0, 15.0, 5.0, 15.0)
            );
    private static final VoxelShape[] SHAPE = new VoxelShape[] {
            Shapes.or(SHAPE_BASE, Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0)),
            Shapes.or(SHAPE_BASE, Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0)),
            Shapes.or(SHAPE_BASE, Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0)),
            Shapes.or(SHAPE_BASE, Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0))
    };
    public static final IntegerProperty LEVEL = BlockStateProperties.AGE_3;
    public static final BooleanProperty MILK = BlockStateProperties.SNOWY;
    public final DyeColor color;

    public DogBowlBlock(DyeColor c, Properties settings) {
        super(settings);
        color = c;
        registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 0).setValue(MILK, false));
    }

    public static ResourceKey<Block> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.BLOCK, Ids.of(c.getName() + "_dog_bowl"));
    }

    //? if <1.20 {
    /*@Override
    public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
        return net.minecraft.world.level.material.PushReaction.DESTROY;
    }
    *///?}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DogBowlBlockEntity(pos, state);
    }

    @Override
    //? if >=1.21.2 {
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() :
                super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
    //?} else {
    /*public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() :
                super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
    *///?}

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.below();
        return canSupportRigidBlock(world, blockPos) || canSupportCenter(world, blockPos, Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int level = state.getValue(LEVEL);
        return (level < 0 || level > 3) ? SHAPE_BASE : SHAPE[level];
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = tryUseItem(stack, state, world, pos, player, hand);
        return result == InteractionResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : result;
    }
    //?}
    //? if >=1.20.5 && <1.21.2 {
    /*@Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = tryUseItem(stack, state, world, pos, player, hand);
        if (result == InteractionResult.PASS) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return result == InteractionResult.FAIL ? ItemInteractionResult.FAIL : ItemInteractionResult.SUCCESS;
    }
    *///?}
    //? if <1.20.5 {
    /*@Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = tryUseItem(player.getItemInHand(hand), state, world, pos, player, hand);
        return result == InteractionResult.PASS ? useWithoutItem(state, world, pos, player, hit) : result;
    }
    *///?}

    // PASS stands in for the era-specific "now try the empty-hand path" value, which each override maps.
    private InteractionResult tryUseItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand) {
        if (!FeatureRules.CAN_USE_DOG_BOWLS.enabled(world)) return InteractionResult.PASS;
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof DogBowlBlockEntity be)) return InteractionResult.FAIL;

        if (Compat.food(stack) != null && EquippedAccessories.hasEquipped(player, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
            if (!world.isClientSide()) {
                Compat.sendOverlayMessage(player, Text.literal("Naughty pet! Only your owner can feed you!").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        if (stack.is(Items.MILK_BUCKET) && be.getCount() == 0) {
            be.insert(stack);
            state = state.setValue(MILK, true);
            world.setBlock(pos, state, 2);
            if (!player.isCreative()) player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            Compat.makeSound(player, Compat.sound(SoundEvents.BUCKET_EMPTY));
            return InteractionResult.SUCCESS;
        }

        if (Compat.food(stack) == null) return InteractionResult.PASS;
        int decr = be.insert(stack);
        if (decr > 0) {
            stack.shrink(decr);
            state = state.setValue(LEVEL, Math.min((be.getCount() + 20) / 21, 3));
            world.setBlock(pos, state, 2);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    //? if >=1.20.5 {
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof DogBowlBlockEntity be) be.drop();
        return super.playerWillDestroy(world, pos, state, player);
    }
    //?}
    //? if <1.20.5 {
    /*@Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof DogBowlBlockEntity be) be.drop();
        super.playerWillDestroy(world, pos, state, player);
    }
    *///?}

    //? if >=1.20.5 {
    @Override
    //?}
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!FeatureRules.CAN_USE_DOG_BOWLS.enabled(world)) return InteractionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof DogBowlBlockEntity be)) return InteractionResult.PASS;
        if (EquippedAccessories.hasEquipped(player, x -> x.is(PlayerCollarsMod.COLLAR_TAG))) {
            return eatFromBowl(state, world, pos, player, be);
        }

        ItemStack is = be.take();
        if (is.isEmpty()) return InteractionResult.PASS;
        if (is.is(Items.MILK_BUCKET)) {
            state = state.setValue(MILK, false);
            world.setBlock(pos, state, 2);
            if (!world.isClientSide()) player.removeAllEffects();
            Compat.makeSound(player, Compat.sound(SoundEvents.GENERIC_DRINK));
            return InteractionResult.SUCCESS;
        }

        state = state.setValue(LEVEL, Math.min((be.getCount() + 20) / 21, 3));
        world.setBlock(pos, state, 2);

        FoodProperties food = Compat.food(is);
        if (food != null && player.canEat(food.canAlwaysEat())) {
            Compat.eat(player, world, is, food);
            return InteractionResult.SUCCESS;
        } else if (!player.addItem(is)) {
            //? if >=26.3 {
            /*player.drop(is, true, net.minecraft.util.Prediction.SERVER_ONLY);
            *///?} else {
            player.drop(is, true);
            //?}
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult eatFromBowl(BlockState state, Level world, BlockPos pos, Player player, DogBowlBlockEntity be) {
        ItemStack is = be.peekOne();
        if (is.isEmpty()) return InteractionResult.PASS;
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        if (is.is(Items.MILK_BUCKET)) {
            be.take();
            state = state.setValue(MILK, false);
            world.setBlock(pos, state, 2);
            player.removeAllEffects();
            Compat.makeSound(player, Compat.sound(SoundEvents.GENERIC_DRINK));
            return InteractionResult.SUCCESS;
        }

        FoodProperties food = Compat.food(is);
        if (food == null || !player.canEat(food.canAlwaysEat())) {
            return InteractionResult.FAIL;
        }

        Compat.eat(player, world, is, food);
        be.take();
        state = state.setValue(LEVEL, Math.min((be.getCount() + 20) / 21, 3));
        world.setBlock(pos, state, 2);
        Compat.makeSound(player, Compat.sound(SoundEvents.GENERIC_EAT));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, MILK);
    }

    public static class DogBowlBlockEntity extends BlockEntity {
        private ItemStack inBowl = ItemStack.EMPTY;

        public DogBowlBlockEntity(BlockPos pos, BlockState state) {
            super(PlayerCollarsMod.DOG_BOWL_BLOCK_ENTITY, pos, state);
        }

        // 1.21.6 swapped CompoundTag for ValueInput/ValueOutput; the older form also wants the lookup.
        @Override
        //? if >=1.21.6 {
        protected void loadAdditional(ValueInput input) {
            super.loadAdditional(input);
            inBowl = input.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
        //?}
        //? if >=1.21.5 && <1.21.6 {
        /*protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            inBowl = tag.getCompound("item")
                    .flatMap(item -> ItemStack.parse(registries, item))
                    .orElse(ItemStack.EMPTY);
        }
        *///?}
        //? if >=1.20.5 && <1.21.5 {
        /*protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            inBowl = tag.contains("item")
                    ? ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY)
                    : ItemStack.EMPTY;
        }
        *///?}
        //? if <1.20.5 {
        /*public void load(CompoundTag tag) {
            super.load(tag);
            inBowl = tag.contains("item") ? ItemStack.of(tag.getCompound("item")) : ItemStack.EMPTY;
        }
        *///?}

        @Override
        //? if >=1.21.6 {
        protected void saveAdditional(ValueOutput output) {
            super.saveAdditional(output);
            if (!inBowl.isEmpty())
                output.store("item", ItemStack.CODEC, inBowl);
        }
        //?}
        //? if >=1.20.5 && <1.21.6 {
        /*protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            if (!inBowl.isEmpty())
                tag.put("item", inBowl.save(registries));
        }
        *///?}
        //? if <1.20.5 {
        /*protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (!inBowl.isEmpty())
                tag.put("item", inBowl.save(new CompoundTag()));
        }
        *///?}

        protected int getCount() {
            return inBowl.getCount();
        }

        protected int insert(ItemStack is) {
            if (inBowl.isEmpty()) {
                inBowl = is.copy();
                setChanged();
                return is.getCount();
            }
            if (is.is(inBowl.getItem())) {
                int count = Math.min(is.getCount(), inBowl.getMaxStackSize() - inBowl.getCount());
                inBowl.grow(count);
                setChanged();
                return count;
            }
            return 0;
        }

        protected ItemStack take() {
            if (inBowl.isEmpty()) return ItemStack.EMPTY;
            ItemStack is = Compat.copyWithCount(inBowl, 1);
            inBowl.shrink(1);
            setChanged();
            return is;
        }

        protected ItemStack peekOne() {
            if (inBowl.isEmpty()) return ItemStack.EMPTY;
            return Compat.copyWithCount(inBowl, 1);
        }

        protected void drop() {
            if (inBowl.isEmpty() || inBowl.is(Items.MILK_BUCKET) || level == null) return;
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), inBowl));
            inBowl = ItemStack.EMPTY;
        }
    }
}
