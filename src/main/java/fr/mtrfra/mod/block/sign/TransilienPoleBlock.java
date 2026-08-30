package fr.mtrfra.mod.block.sign;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mod.block.IBlock;

public class TransilienPoleBlock extends BlockExtension {

    public TransilienPoleBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    private VoxelShape computeShape() {
        return IBlock.getVoxelShapeByDirection(7.5, 0, 7.5, 8.5, 16, 8.5, org.mtr.mapping.holder.Direction.NORTH);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape();
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape();
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

}
