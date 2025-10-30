package fr.mtrfranceaddon.mod.common.block;

import fr.mtrfranceaddon.mod.common.block.base.DirectionalBlock;
import org.jetbrains.annotations.NotNull;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

public class LogoBlock extends DirectionalBlock {
    
    public LogoBlock(BlockSettings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public @NotNull VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(0, 0, 15, 16, 16, 16, facing);
    }
}
