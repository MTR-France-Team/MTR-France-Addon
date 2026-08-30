package fr.mtrfra.mod.block.copycat;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.WorldAccess;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.PlatformHelper;

import java.util.List;

public class CopycatPlatformBlock extends CopycatBlockBase implements PlatformHelper {

    public CopycatPlatformBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(DOOR_TYPE);
        properties.add(SIDE);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().data);
    }

    @Override
    public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        final BlockState actualState = PlatformHelper.getActualState(BlockView.cast(world), pos, state);
        if (!world.isClient() && !actualState.equals(state)) {
            final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
            if (entity != null && entity.data instanceof CopycatBlockBase.BlockEntity) {
                ((CopycatBlockBase.BlockEntity) entity.data).redraw();
            }
        }
        return actualState;
    }

    @Override
    protected int heightUnits(BlockState state) {
        return 16;
    }

}
