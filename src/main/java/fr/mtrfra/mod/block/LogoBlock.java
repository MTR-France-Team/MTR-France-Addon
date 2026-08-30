package fr.mtrfra.mod.block;

import fr.mtrfra.mod.block.base.DirectionalBlock;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mod.block.IBlock;

public class LogoBlock extends DirectionalBlock {

    public LogoBlock(BlockSettings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(0, 0, 15, 16, 16, 16, facing);
    }

}
