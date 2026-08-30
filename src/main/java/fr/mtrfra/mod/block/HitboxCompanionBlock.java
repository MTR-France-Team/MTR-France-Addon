package fr.mtrfra.mod.block;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.DirectionProperty;
import org.mtr.mapping.holder.IntegerProperty;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.Blocks;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.block.IBlock.DoubleBlockHalf;

import java.util.List;

public class HitboxCompanionBlock extends BlockExtension {

    public static final IntegerProperty HEIGHT = IntegerProperty.of("height", 1, 16);
    public static final Property<Integer> HEIGHT_PROPERTY = Property.cast(HEIGHT);
    public static final DirectionProperty FACING = DirectionHelper.FACING;

    public HitboxCompanionBlock() {
        super(Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque().dropsNothing());
        setDefaultState2(getDefaultState2().with(new Property<>(FACING.data), Direction.NORTH.data));
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(HEIGHT);
        properties.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getInvisibleMapped();
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape2(0, 0, 0, 16, state.get(HEIGHT_PROPERTY), 16);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape2(state, world, pos, context);
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    private static BlockPos anchorPos(BlockPos pos, BlockState state) {
        return pos.offset(IBlock.getStatePropertySafe(state, FACING).rotateYClockwise());
    }

    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        final BlockPos anchorPos = anchorPos(pos, state);
        return world.getBlockState(anchorPos).getBlock().asItem().getDefaultStack();
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            final BlockPos anchorPos = anchorPos(pos, state);
            final DoubleBlockHalf anchorHalf = IBlock.getStatePropertySafe(world.getBlockState(anchorPos), IBlock.HALF);
            final BlockPos anchorTwinPos = anchorHalf == DoubleBlockHalf.UPPER ? anchorPos.down() : anchorPos.up();
            final BlockPos companionTwinPos = anchorHalf == DoubleBlockHalf.UPPER ? pos.down() : pos.up();
            final BlockState air = org.mtr.mapping.holder.Blocks.getAirMapped().getDefaultState();
            world.setBlockState(anchorPos, air, 35);
            world.setBlockState(anchorTwinPos, air, 35);
            world.setBlockState(companionTwinPos, air, 35);
        }
        super.onBreak2(world, pos, state, player);
    }

}
