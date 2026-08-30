package fr.mtrfra.mod.block.sign;

import fr.mtrfra.mod.block.base.DirectionalBlock;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.IBlock;

public class RATPSignMapBlock extends DirectionalBlock implements HasBoundingBox {

    private static final double[] BOUNDING_BOX = {-16, 0, 7, 32, 32, 9};

    public RATPSignMapBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public double[] boundingBox() {
        return BOUNDING_BOX;
    }

    private VoxelShape computeShape(BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(BOUNDING_BOX[0], BOUNDING_BOX[1], BOUNDING_BOX[2], BOUNDING_BOX[3], BOUNDING_BOX[4], BOUNDING_BOX[5], facing);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(state);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(state);
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final BlockState state = super.getPlacementState2(context);
        if (state == null) {
            return null;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (!RATPSignCollisionExtensionBlock.canPlaceAround(context, context.getBlockPos(), facing, BOUNDING_BOX)) {
            return null;
        }
        return state;
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced2(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            RATPSignCollisionExtensionBlock.placeAround(world, pos, IBlock.getStatePropertySafe(state, FACING), BOUNDING_BOX);
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            RATPSignCollisionExtensionBlock.removeAround(world, pos, IBlock.getStatePropertySafe(state, FACING), BOUNDING_BOX);
        }
        super.onBreak2(world, pos, state, player);
    }

}
