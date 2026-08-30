package fr.mtrfra.mod.block.sign;

import fr.mtrfra.mod.sign.PacketOpenSignConfigScreen;
import fr.mtrfra.mod.registry.MTRFRARegistry;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.holder.Box;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mod.Items;
import org.mtr.mod.block.IBlock;

public class RATPSignCollisionExtensionBlock extends BlockExtension {

    public RATPSignCollisionExtensionBlock() {
        super(org.mtr.mod.Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque().dropsNothing());
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getInvisibleMapped();
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

    private static final int[][] NEIGHBOR_OFFSETS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1},
            {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0},
            {0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1}
    };

    private static VoxelShape computeShape(BlockView world, BlockPos pos) {
        VoxelShape shape = VoxelShapes.empty();
        for (final int[] offset : NEIGHBOR_OFFSETS) {
            final int dx = offset[0], dy = offset[1], dz = offset[2];
            final BlockPos anchorPos = pos.add(-dx, -dy, -dz);
            final BlockState anchorState = world.getBlockState(anchorPos);
            final Object anchorBlock = anchorState.getBlock().data;
            if (!(anchorBlock instanceof HasBoundingBox boundedAnchor)) {
                continue;
            }
            final VoxelShape anchorShape = anchorVoxelShape(anchorState, boundedAnchor);
            if (overlapsNeighborCell(anchorShape.getBoundingBox(), dx, dy, dz)) {
                shape = VoxelShapes.union(shape, anchorShape.offset(-dx, -dy, -dz));
            }
        }
        return shape;
    }

    private static VoxelShape anchorVoxelShape(BlockState anchorState, HasBoundingBox boundedAnchor) {
        final Direction facing = IBlock.getStatePropertySafe(anchorState, DirectionHelper.FACING);
        final double[] box = boundedAnchor.boundingBox();
        return IBlock.getVoxelShapeByDirection(box[0], box[1], box[2], box[3], box[4], box[5], facing);
    }

    private static boolean overlapsNeighborCell(Box bounds, int dx, int dy, int dz) {
        return bounds.getMinXMapped() - dx < 1 && bounds.getMaxXMapped() - dx > 0 &&
                bounds.getMinYMapped() - dy < 1 && bounds.getMaxYMapped() - dy > 0 &&
                bounds.getMinZMapped() - dz < 1 && bounds.getMaxZMapped() - dz > 0;
    }

    private static int[] neededOffsets(double min, double max) {
        if (min < 0 && max > 1) {
            return new int[]{-1, 0, 1};
        }
        if (min < 0) {
            return new int[]{-1, 0};
        }
        if (max > 1) {
            return new int[]{0, 1};
        }
        return new int[]{0};
    }

    public static boolean canPlaceAround(ItemPlacementContext context, BlockPos anchorPos, Direction facing, double[] boundingBox) {
        final World world = context.getWorld();
        final boolean[] canPlace = {true};
        forEachExtensionOffset(facing, boundingBox, (dx, dy, dz) -> {
            final BlockPos target = anchorPos.add(dx, dy, dz);
            final BlockState targetState = world.getBlockState(target);
            if (!targetState.isAir() && !targetState.canReplace(context)) {
                canPlace[0] = false;
            }
        });
        return canPlace[0];
    }

    public static void placeAround(World world, BlockPos anchorPos, Direction facing, double[] boundingBox) {
        forEachExtensionOffset(facing, boundingBox, (dx, dy, dz) -> placeAt(world, anchorPos.add(dx, dy, dz)));
    }

    private static void placeAt(World world, BlockPos target) {
        final BlockState existing = world.getBlockState(target);
        if (existing.isAir() || existing.getBlock().data instanceof RATPSignCollisionExtensionBlock) {
            world.setBlockState(target, fr.mtrfra.mod.registry.ModBlocks.RATP_SIGN_COLLISION_EXTENSION.get().getDefaultState(), 3);
        }
    }

    public static void removeAround(World world, BlockPos anchorPos, Direction facing, double[] boundingBox) {
        forEachExtensionOffset(facing, boundingBox, (dx, dy, dz) -> removeAt(world, anchorPos.add(dx, dy, dz)));
    }

    private interface OffsetConsumer {
        void accept(int dx, int dy, int dz);
    }

    private static void forEachExtensionOffset(Direction facing, double[] boundingBox, OffsetConsumer consumer) {
        final VoxelShape shape = IBlock.getVoxelShapeByDirection(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5], facing);
        final Box bounds = shape.getBoundingBox();
        final int[] xOffsets = neededOffsets(bounds.getMinXMapped(), bounds.getMaxXMapped());
        final int[] yOffsets = neededOffsets(bounds.getMinYMapped(), bounds.getMaxYMapped());
        final int[] zOffsets = neededOffsets(bounds.getMinZMapped(), bounds.getMaxZMapped());
        for (final int dx : xOffsets) {
            for (final int dy : yOffsets) {
                for (final int dz : zOffsets) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    consumer.accept(dx, dy, dz);
                }
            }
        }
    }

    private static void removeAt(World world, BlockPos target) {
        if (world.getBlockState(target).getBlock().data instanceof RATPSignCollisionExtensionBlock) {
            world.setBlockState(target, Blocks.getAirMapped().getDefaultState(), 35);
        }
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).getItem().equals(Items.BRUSH.get().asItem())) {
            return ActionResult.PASS;
        }
        final BlockPos anchorPos = findSignAnchorPos(BlockView.cast(world), pos);
        if (anchorPos == null) {
            return ActionResult.PASS;
        }
        final BlockState anchorState = world.getBlockState(anchorPos);
        if (!(anchorState.getBlock().data instanceof RATPSignBase sign)) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            final BlockEntity blockEntity = world.getBlockEntity(anchorPos);
            final RATPSignBase.BlockEntityBase signBlockEntity = blockEntity != null && blockEntity.data instanceof RATPSignBase.BlockEntityBase ? (RATPSignBase.BlockEntityBase) blockEntity.data : null;
            final String customTitle = signBlockEntity == null ? "" : signBlockEntity.customTitle;
            final String subtitle = signBlockEntity == null ? "" : signBlockEntity.subtitle;
            final int category = IBlock.getStatePropertySafe(anchorState, RATPSignBase.CATEGORY);
            MTRFRARegistry.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketOpenSignConfigScreen(anchorPos, customTitle, subtitle, category, sign.maxCategory(), sign.hasSubtitle()));
        }
        return ActionResult.SUCCESS;
    }

    public static BlockPos findSignAnchorPos(BlockView world, BlockPos pos) {
        for (final int[] offset : NEIGHBOR_OFFSETS) {
            final int dx = offset[0], dy = offset[1], dz = offset[2];
            final BlockPos anchorPos = pos.add(-dx, -dy, -dz);
            final BlockState anchorState = world.getBlockState(anchorPos);
            final Object anchorBlock = anchorState.getBlock().data;
            if (!(anchorBlock instanceof HasBoundingBox boundedAnchor)) {
                continue;
            }
            final VoxelShape anchorShape = anchorVoxelShape(anchorState, boundedAnchor);
            if (overlapsNeighborCell(anchorShape.getBoundingBox(), dx, dy, dz)) {
                return anchorPos;
            }
        }
        return null;
    }

    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        final BlockPos anchorPos = findSignAnchorPos(world, pos);
        return anchorPos == null ? ItemStack.getEmptyMapped() : world.getBlockState(anchorPos).getBlock().asItem().getDefaultStack();
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            final BlockPos anchorPos = findSignAnchorPos(BlockView.cast(world), pos);
            if (anchorPos != null) {
                world.breakBlock(anchorPos, false);
            }
        }
        super.onBreak2(world, pos, state, player);
    }

}
