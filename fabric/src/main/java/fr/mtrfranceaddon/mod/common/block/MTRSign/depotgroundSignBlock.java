package fr.mtrfranceaddon.mod.common.block.MTRSign;

import fr.mtrfranceaddon.mod.common.block.base.DirectionalBlock;
import javax.annotation.Nonnull;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

public class depotgroundSignBlock extends DirectionalBlock {

    public depotgroundSignBlock(BlockSettings settings) {
        super(settings);
    }

    @Override
    public @Nonnull VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(1, 0, 1, 14, 7, 14, facing);
    }
}
