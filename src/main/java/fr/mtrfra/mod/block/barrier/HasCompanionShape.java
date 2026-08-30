package fr.mtrfra.mod.block.barrier;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.VoxelShape;

public interface HasCompanionShape {

    VoxelShape companionShape(BlockView world, BlockPos pos, BlockState state);

}
