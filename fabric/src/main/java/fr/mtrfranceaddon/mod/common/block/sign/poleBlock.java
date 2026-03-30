package fr.mtrfranceaddon.mod.common.block.sign;

import fr.mtrfranceaddon.mod.common.block.base.DirectionalBlock;
import javax.annotation.Nonnull;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

public class poleBlock extends DirectionalBlock {

    public poleBlock(BlockSettings settings) {
        super(settings);
    }

    @Override
    public @Nonnull org.mtr.mapping.holder.VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(7, 0, 7, 9, 16, 9, facing);
    }
}
