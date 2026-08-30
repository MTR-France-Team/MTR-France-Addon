package fr.mtrfra.mod.block.barrier;

import fr.mtrfra.mod.barrier.PacketOpenTicketBarrierConfigScreen;
import fr.mtrfra.mod.barrier.TicketBarrierMode;
import fr.mtrfra.mod.registry.MTRFRARegistry;
import fr.mtrfra.mod.registry.ModBlockEntities;
import fr.mtrfra.mod.registry.ModBlocks;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.Random;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.SoundCategory;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mod.Items;
import org.mtr.mod.SoundEvents;
import org.mtr.mod.block.BlockTicketBarrier;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.data.TicketSystem.EnumTicketBarrierOpen;

public class RATPTicketBarrierBlock extends BlockTicketBarrier implements BlockWithEntity, HasCompanionShape {

    private final boolean hasSideCover;

    public RATPTicketBarrierBlock(boolean isEntrance) {
        this(isEntrance, false);
    }

    public RATPTicketBarrierBlock(boolean isEntrance, boolean hasSideCover) {
        super(isEntrance);
        this.hasSideCover = hasSideCover;
    }

    public boolean hasSideCover() {
        return hasSideCover;
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
        if (!RATPTicketBarrierCollisionExtensionBlock.canPlaceSideCompanions(context, pos, computeShape(state, facing))) {
            return null;
        }
        if (!RATPTicketBarrierCollisionExtensionBlock.canPlaceSideCompanions(context, pos.up(), computeUpperShape(facing))) {
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
            world.setBlockState(upperPos, Blocks.getAirMapped().getDefaultState(), 35);
        }
        super.onBreak2(world, pos, state, player);
    }

    @Override
    public VoxelShape companionShape(BlockView world, BlockPos pos, BlockState state) {
        return computeShape(state, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BarrierBlockEntity(blockPos, blockState);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).getItem().equals(Items.BRUSH.get().asItem())) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            final BarrierBlockEntity barrierBlockEntity = getBarrierBlockEntity(world, pos);
            final TicketBarrierMode mode = barrierBlockEntity == null ? TicketBarrierMode.MTR_BALANCE : barrierBlockEntity.mode;
            final String acceptedTicketIdsCsv = barrierBlockEntity == null ? "" : barrierBlockEntity.acceptedTicketIdsCsv;
            MTRFRARegistry.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketOpenTicketBarrierConfigScreen(pos, mode.ordinal(), acceptedTicketIdsCsv));
        }
        return ActionResult.SUCCESS;
    }

    static BarrierBlockEntity getBarrierBlockEntity(World world, BlockPos pos) {
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.data instanceof BarrierBlockEntity ? (BarrierBlockEntity) blockEntity.data : null;
    }

    @Override
    public void onEntityCollision2(BlockState state, World world, BlockPos blockPos, Entity entity) {
        if (world.isClient() || !PlayerEntity.isInstance(entity)) {
            return;
        }
        final BarrierBlockEntity barrierBlockEntity = getBarrierBlockEntity(world, blockPos);
        final TicketBarrierMode mode = barrierBlockEntity == null ? TicketBarrierMode.MTR_BALANCE : barrierBlockEntity.mode;
        if (mode == TicketBarrierMode.MTR_BALANCE) {
            super.onEntityCollision2(state, world, blockPos, entity);
            return;
        }

        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final Vector3d playerPosRotated = entity.getPos()
                .subtract(blockPos.getX() + 0.5, 0, blockPos.getZ() + 0.5)
                .rotateY((float) Math.toRadians(facing.asRotation()));
        final EnumTicketBarrierOpen open = IBlock.getStatePropertySafe(state, OPEN);

        if (open == EnumTicketBarrierOpen.OPEN && playerPosRotated.getZMapped() > 0) {
            world.setBlockState(blockPos, state.with(new Property<>(OPEN.data), EnumTicketBarrierOpen.CLOSED));
        } else if (open == EnumTicketBarrierOpen.CLOSED && playerPosRotated.getZMapped() < 0) {
            final BlockPos blockPosCopy = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            world.setBlockState(blockPosCopy, state.with(new Property<>(OPEN.data), EnumTicketBarrierOpen.OPEN));
            world.playSound(null, blockPosCopy, SoundEvents.TICKET_BARRIER.get(), SoundCategory.getBlocksMapped(), 1, 1);
            if (!hasScheduledBlockTick(world, blockPosCopy, new Block(this))) {
                scheduleBlockTick(world, blockPosCopy, new Block(this), 40);
            }
        }
    }

    @Override
    public void scheduledTick2(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        final BlockState currentState = World.cast(world).getBlockState(pos);
        if (!(currentState.getBlock().data instanceof RATPTicketBarrierBlock)) {
            return;
        }
        final PlayerEntity nearby = World.cast(world).getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5, false);
        if (nearby != null) {
            scheduleBlockTick(World.cast(world), pos, new Block(this), 40);
            return;
        }
        World.cast(world).setBlockState(pos, currentState.with(new Property<>(OPEN.data), EnumTicketBarrierOpen.CLOSED));
    }

    private static final double[] POST_LEFT = {13, 0, -3.5, 16, 30, 19.5};
    private static final double[] POST_RIGHT = {-3, 0, -3.5, 0, 30, 19.5};
    private static final double[] DOOR_CLOSED = {-2, 0, 5.5, 16, 30, 6.5};

    static final double[] POST_LEFT_UPPER = {13, 0, -3.5, 16, 14, 19.5};
    static final double[] POST_RIGHT_UPPER = {-3, 0, -3.5, 0, 14, 19.5};
    static final double[] DOOR_CLOSED_UPPER = {-2, 0, 5.5, 16, 14, 6.5};

    private VoxelShape computeShape(BlockState state, Direction facing) {
        VoxelShape shape = box(POST_LEFT, facing);
        if (hasSideCover) {
            shape = VoxelShapes.union(shape, box(POST_RIGHT, facing));
        }
        return shape;
    }

    private VoxelShape computeUpperShape(Direction facing) {
        VoxelShape shape = box(POST_LEFT_UPPER, facing);
        if (hasSideCover) {
            shape = VoxelShapes.union(shape, box(POST_RIGHT_UPPER, facing));
        }
        return shape;
    }

    static VoxelShape box(double[] bounds, Direction facing) {
        return IBlock.getVoxelShapeByDirection(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5], facing);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(state, IBlock.getStatePropertySafe(state, FACING));
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos blockPos, ShapeContext context) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        VoxelShape shape = computeShape(state, facing);
        final EnumTicketBarrierOpen open = IBlock.getStatePropertySafe(state, OPEN);
        if (open != EnumTicketBarrierOpen.OPEN && open != EnumTicketBarrierOpen.OPEN_CONCESSIONARY) {
            shape = VoxelShapes.union(shape, box(DOOR_CLOSED, facing));
        }
        return shape;
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public static class BarrierBlockEntity extends BlockEntityExtension {

        private static final String KEY_MODE = "mode";
        private static final String KEY_ACCEPTED_TICKET_IDS = "accepted_ticket_ids";

        public TicketBarrierMode mode = TicketBarrierMode.MTR_BALANCE;
        public String acceptedTicketIdsCsv = "";

        public BarrierBlockEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.RATP_TICKET_BARRIER.get(), blockPos, blockState);
        }

        public boolean isTicketAccepted(String ticketId) {
            if (acceptedTicketIdsCsv.isEmpty()) {
                return true;
            }
            for (final String id : acceptedTicketIdsCsv.split(",")) {
                if (id.equals(ticketId)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            compoundTag.putInt(KEY_MODE, mode.ordinal());
            compoundTag.putString(KEY_ACCEPTED_TICKET_IDS, acceptedTicketIdsCsv);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            mode = compoundTag.contains(KEY_MODE) ? TicketBarrierMode.byOrdinalSafe(compoundTag.getInt(KEY_MODE)) : TicketBarrierMode.MTR_BALANCE;
            acceptedTicketIdsCsv = compoundTag.contains(KEY_ACCEPTED_TICKET_IDS) ? compoundTag.getString(KEY_ACCEPTED_TICKET_IDS) : "";
        }

    }

}
