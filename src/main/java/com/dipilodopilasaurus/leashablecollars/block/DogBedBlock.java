package com.dipilodopilasaurus.leashablecollars.block;

//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.registry.compat.Registries;
*///?}

import com.dipilodopilasaurus.leashablecollars.FeatureRules;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
//? if <1.20.5 {
/*import net.minecraft.world.InteractionHand;
*///?}
import net.minecraft.core.BlockPos;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
//? if <26.2 {
import net.minecraft.world.level.block.entity.BlockEntity;
//?}
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.dipilodopilasaurus.leashablecollars.Registration;
import com.dipilodopilasaurus.leashablecollars.Ids;

public class DogBedBlock extends BedBlock {
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 6, 16);

    public DogBedBlock(DyeColor color, ResourceKey<Block> key) {
        super(color, Registration.withId(bedProperties(), key));
    }

    private static BlockBehaviour.Properties bedProperties() {
        //? if >=26.3 {
        /*return BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.2F).noOcclusion()
                .ignitedByLava().pushReaction(PushReaction.POPPED);
        *///?} elif >=1.20 {
        return BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.2F).noOcclusion()
                .ignitedByLava().pushReaction(PushReaction.DESTROY);
        //?} else {
        /*return BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.WOOL)
                .sound(SoundType.WOOL).strength(0.2F).noOcclusion();
        *///?}
    }

    @Override
    //? if >=1.20.5 {
    protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (!FeatureRules.CAN_USE_DOG_BEDS.enabled(level)) return InteractionResult.PASS;
        return super.useWithoutItem(state, level, pos, player, hit);
    }
    //?} else {
    /*public InteractionResult use(BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (!FeatureRules.CAN_USE_DOG_BEDS.enabled(level)) return InteractionResult.PASS;
        return super.use(state, level, pos, player, hand, hit);
    }
    *///?}

    //? if <1.20 {
    /*@Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
    *///?}

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // BedBlock stopped being an EntityBlock at 26.2, so there is no bed block entity left to suppress.
    //? if <26.2 {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
    //?}

    public static ResourceKey<Block> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.BLOCK, Ids.of(c.getName() + "_dog_bed"));
    }
}
