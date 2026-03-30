package fr.mtrfranceaddon.mod.common.block.base;

import javax.annotation.Nonnull;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

public class SeatBlock extends DirectionalBlock {

    public SeatBlock(BlockSettings settings) {
        super(settings);
    }

    @Override
    public @Nonnull VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(0, 0, 0, 16, 17, 11, facing);
    }
}
