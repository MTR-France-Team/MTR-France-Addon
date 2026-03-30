package fr.mtrfranceaddon.mod.common.block.sign;

import fr.mtrfranceaddon.mod.common.block.base.DirectionalBlock;
import javax.annotation.Nonnull;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

public class polerightconnexionBlock extends DirectionalBlock {
    public polerightconnexionBlock(BlockSettings settings) {
        super(settings);
    }

    @Override
    public @Nonnull org.mtr.mapping.holder.VoxelShape getOutlineShape2(org.mtr.mapping.holder.BlockState state, org.mtr.mapping.holder.BlockView world, org.mtr.mapping.holder.BlockPos pos, org.mtr.mapping.holder.ShapeContext context) {
        final org.mtr.mapping.holder.Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(1,1,1,15,15,15, facing);
    }
}
