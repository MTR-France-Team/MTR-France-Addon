package fr.mtrfra.mod.block.platform;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.IntegerProperty;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.WorldAccess;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.PlatformHelper;

import java.util.List;

public class BlockPlatformLayer extends BlockExtension implements PlatformHelper {

    public static final IntegerProperty LAYERS = IntegerProperty.of("layers", 1, 8);
    private static final Property<Integer> LAYERS_PROPERTY = Property.cast(LAYERS);
    private static final int MAX_LAYERS = 8;

    public BlockPlatformLayer(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(LAYERS);
        properties.add(FACING);
        properties.add(DOOR_TYPE);
        properties.add(SIDE);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final BlockPos pos = context.getBlockPos();
        final BlockState existing = context.getWorld().getBlockState(pos);
        if (existing.isOf(asBlock2())) {
            final int layers = existing.get(LAYERS_PROPERTY);
            return existing.with(LAYERS_PROPERTY, Math.min(MAX_LAYERS, layers + 1));
        }
        return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
    }

    @Override
    public boolean canReplace2(BlockState state, ItemPlacementContext context) {
        return state.isOf(asBlock2()) && state.get(LAYERS_PROPERTY) < MAX_LAYERS && context.getStack().getItem().equals(asItem2());
    }

    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return PlatformHelper.getActualState(BlockView.cast(world), pos, state);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shapeForLayers(state.get(LAYERS_PROPERTY));
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shapeForLayers(state.get(LAYERS_PROPERTY));
    }

    private static VoxelShape shapeForLayers(int layers) {
        return createCuboidShape2(0, 0, 0, 16, layers * 2, 16);
    }

}
