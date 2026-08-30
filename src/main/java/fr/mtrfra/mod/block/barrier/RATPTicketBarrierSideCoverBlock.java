package fr.mtrfra.mod.block.barrier;

import fr.mtrfra.mod.block.base.DirectionalBlock;
import fr.mtrfra.mod.registry.ModBlocks;
import org.mtr.mapping.holder.BlockPos;
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
import org.mtr.mod.Blocks;
import org.mtr.mod.block.IBlock;

public class RATPTicketBarrierSideCoverBlock extends DirectionalBlock implements HasCompanionShape {

    static final double[] POST = {13, 0, -3.5, 16, 30, 19.5};
    static final double[] POST_UPPER = {13, 0, -3.5, 16, 14, 19.5};

    public RATPTicketBarrierSideCoverBlock() {
        super(Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque());
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final BlockState state = super.getPlacementState2(context);
        if (state == null) {
            return null;
        }
        final BlockPos pos = context.getBlockPos();
        if (!context.getWorld().getBlockState(pos.up()).canReplace(context)) {
            return null;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final VoxelShape groundShape = IBlock.getVoxelShapeByDirection(POST[0], POST[1], POST[2], POST[3], POST[4], POST[5], facing);
        if (!RATPTicketBarrierCollisionExtensionBlock.canPlaceSideCompanions(context, pos, groundShape)) {
            return null;
        }
        final VoxelShape upperShape = IBlock.getVoxelShapeByDirection(POST_UPPER[0], POST_UPPER[1], POST_UPPER[2], POST_UPPER[3], POST_UPPER[4], POST_UPPER[5], facing);
        if (!RATPTicketBarrierCollisionExtensionBlock.canPlaceSideCompanions(context, pos.up(), upperShape)) {
            return null;
        }
        return state;
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced2(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            final BlockPos upperPos = pos.up();
            world.setBlockState(upperPos, ModBlocks.RATP_TICKET_BARRIER_UPPER.get().getDefaultState(), 3);
            RATPTicketBarrierCollisionExtensionBlock.placeSideCompanions(world, pos, companionShape(BlockView.cast(world), pos, state));
            RATPTicketBarrierCollisionExtensionBlock.placeSideCompanions(world, upperPos, RATPTicketBarrierUpperBlock.computeShape(BlockView.cast(world), upperPos));
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            final BlockPos upperPos = pos.up();
            RATPTicketBarrierCollisionExtensionBlock.removeSideCompanions(world, pos, companionShape(BlockView.cast(world), pos, state));
            RATPTicketBarrierCollisionExtensionBlock.removeSideCompanions(world, upperPos, RATPTicketBarrierUpperBlock.computeShape(BlockView.cast(world), upperPos));
            world.setBlockState(upperPos, org.mtr.mapping.holder.Blocks.getAirMapped().getDefaultState(), 35);
        }
        super.onBreak2(world, pos, state, player);
    }

    @Override
    public VoxelShape companionShape(BlockView world, BlockPos pos, BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(POST[0], POST[1], POST[2], POST[3], POST[4], POST[5], facing);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(POST[0], POST[1], POST[2], POST[3], POST[4], POST[5], facing);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos blockPos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(POST[0], POST[1], POST[2], POST[3], POST[4], POST[5], facing);
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

}
