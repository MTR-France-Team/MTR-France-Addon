package fr.mtrfra.mod.block.barrier;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.holder.BooleanBiFunction;
import org.mtr.mapping.holder.Box;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockExtension;

public class RATPTicketBarrierCollisionExtensionBlock extends BlockExtension {

    public RATPTicketBarrierCollisionExtensionBlock() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque().dropsNothing());
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getInvisibleMapped();
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(world, pos);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(world, pos);
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    private static VoxelShape computeShape(BlockView world, BlockPos pos) {
        VoxelShape shape = VoxelShapes.empty();
        for (final Direction direction : Direction.values()) {
            final BlockPos anchorPos = pos.offset(direction);
            final BlockState anchorState = world.getBlockState(anchorPos);
            final Object anchorBlock = anchorState.getBlock().data;
            if (!(anchorBlock instanceof HasCompanionShape hasCompanionShape)) {
                continue;
            }
            final VoxelShape anchorShape = hasCompanionShape.companionShape(world, anchorPos, anchorState);
            if (anchorShape.isEmpty()) {
                continue;
            }
            final Box bounds = anchorShape.getBoundingBox();
            final int dx = pos.getX() - anchorPos.getX();
            final int dy = pos.getY() - anchorPos.getY();
            final int dz = pos.getZ() - anchorPos.getZ();
            final boolean overlapsHere =
                    (dx > 0 && bounds.getMaxXMapped() > 1) ||
                            (dx < 0 && bounds.getMinXMapped() < 0) ||
                            (dz > 0 && bounds.getMaxZMapped() > 1) ||
                            (dz < 0 && bounds.getMinZMapped() < 0) ||
                            (dy > 0 && bounds.getMaxYMapped() > 1) ||
                            (dy < 0 && bounds.getMinYMapped() < 0);
            if (overlapsHere) {
                final VoxelShape localShape = anchorShape.offset(-dx, -dy, -dz);
                final VoxelShape unitCube = VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);
                shape = VoxelShapes.union(shape, VoxelShapes.combineAndSimplify(localShape, unitCube, BooleanBiFunction.getAndMapped()));
            }
        }
        return shape;
    }

    private static BlockPos findAnchorPos(BlockView world, BlockPos pos) {
        for (final Direction direction : Direction.values()) {
            final BlockPos anchorPos = pos.offset(direction);
            if (world.getBlockState(anchorPos).getBlock().data instanceof HasCompanionShape) {
                return anchorPos;
            }
        }
        return null;
    }

    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        final BlockPos anchorPos = findAnchorPos(world, pos);
        return anchorPos == null ? ItemStack.getEmptyMapped() : world.getBlockState(anchorPos).getBlock().asItem().getDefaultStack();
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            final BlockPos anchorPos = findAnchorPos(BlockView.cast(world), pos);
            if (anchorPos != null) {
                final BlockState anchorState = world.getBlockState(anchorPos);
                if (anchorState.getBlock().data instanceof BlockExtension anchorBlock) {
                    anchorBlock.onBreak2(world, anchorPos, anchorState, player);
                }
                world.breakBlock(anchorPos, false);
            }
        }
        super.onBreak2(world, pos, state, player);
    }

    static void placeAt(World world, BlockPos target) {
        final BlockState existing = world.getBlockState(target);
        if (existing.isAir() || existing.getBlock().data instanceof RATPTicketBarrierCollisionExtensionBlock) {
            world.setBlockState(target, fr.mtrfra.mod.registry.ModBlocks.RATP_TICKET_BARRIER_COLLISION_EXTENSION.get().getDefaultState(), 3);
        }
    }

    static void removeAt(World world, BlockPos target) {
        if (world.getBlockState(target).getBlock().data instanceof RATPTicketBarrierCollisionExtensionBlock) {
            world.setBlockState(target, Blocks.getAirMapped().getDefaultState(), 35);
        }
    }

    private static boolean canReplaceAt(World world, BlockPos target, ItemPlacementContext context) {
        final BlockState state = world.getBlockState(target);
        return state.isAir() || state.canReplace(context);
    }

    public static boolean canPlaceSideCompanions(ItemPlacementContext context, BlockPos pos, VoxelShape shape) {
        if (shape.isEmpty()) {
            return true;
        }
        final World world = context.getWorld();
        final Box bounds = shape.getBoundingBox();
        boolean canPlace = true;
        if (bounds.getMinZMapped() < 0) {
            canPlace &= canReplaceAt(world, pos.offset(Direction.NORTH), context);
        }
        if (bounds.getMaxZMapped() > 1) {
            canPlace &= canReplaceAt(world, pos.offset(Direction.SOUTH), context);
        }
        if (bounds.getMinXMapped() < 0) {
            canPlace &= canReplaceAt(world, pos.offset(Direction.WEST), context);
        }
        if (bounds.getMaxXMapped() > 1) {
            canPlace &= canReplaceAt(world, pos.offset(Direction.EAST), context);
        }
        return canPlace;
    }

    public static void placeSideCompanions(World world, BlockPos pos, VoxelShape shape) {
        if (shape.isEmpty()) {
            return;
        }
        final Box bounds = shape.getBoundingBox();
        if (bounds.getMinZMapped() < 0) {
            placeAt(world, pos.offset(Direction.NORTH));
        }
        if (bounds.getMaxZMapped() > 1) {
            placeAt(world, pos.offset(Direction.SOUTH));
        }
        if (bounds.getMinXMapped() < 0) {
            placeAt(world, pos.offset(Direction.WEST));
        }
        if (bounds.getMaxXMapped() > 1) {
            placeAt(world, pos.offset(Direction.EAST));
        }
    }

    public static void removeSideCompanions(World world, BlockPos pos, VoxelShape shape) {
        if (shape.isEmpty()) {
            return;
        }
        final Box bounds = shape.getBoundingBox();
        if (bounds.getMinZMapped() < 0) {
            removeAt(world, pos.offset(Direction.NORTH));
        }
        if (bounds.getMaxZMapped() > 1) {
            removeAt(world, pos.offset(Direction.SOUTH));
        }
        if (bounds.getMinXMapped() < 0) {
            removeAt(world, pos.offset(Direction.WEST));
        }
        if (bounds.getMaxXMapped() > 1) {
            removeAt(world, pos.offset(Direction.EAST));
        }
    }

}
