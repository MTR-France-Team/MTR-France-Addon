package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.LogoEntity;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Axis;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class LogoPlacementItem extends ItemExtension {

    private final EntityTypeRegistryObject<LogoEntity> entityType;

    public LogoPlacementItem(ItemSettings settings, EntityTypeRegistryObject<LogoEntity> entityType) {
        super(settings);
        this.entityType = entityType;
    }

    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        final World world = context.getWorld();
        final BlockPos blockPos = context.getBlockPos();
        final BlockState state = world.getBlockState(blockPos);
        final VoxelShape shape = state.getOutlineShape(BlockView.cast(world), blockPos);
        if (shape.isEmpty()) {
            return ActionResult.FAIL;
        }

        final Direction side = context.getSide();
        if (!world.isClient()) {
            final EntityType<LogoEntity> type = entityType.get();
            final LogoEntity logo = new LogoEntity(type, world);
            final float yaw = yawFor(side, context.getPlayerYaw());
            final float pitch = pitchFor(side);
            final Vector3d hitPos = context.getHitPos();
            final Axis axis = side.getAxis();
            final double anchorX = axis == Axis.X ? hitPos.getXMapped() : blockPos.getX() + 0.5;
            final double anchorY = axis == Axis.Y ? hitPos.getYMapped() : blockPos.getY() + 0.5;
            final double anchorZ = axis == Axis.Z ? hitPos.getZMapped() : blockPos.getZ() + 0.5;
            logo.placeAt(anchorX, anchorY, anchorZ, blockPos, yaw, pitch, context.getStack().getItem());
            ServerWorld.cast(world).spawnEntity(new Entity(logo));
        }

        final PlayerEntity player = context.getPlayer();
        if (player != null && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    private static float yawFor(Direction side, float playerYaw) {
        if (side == Direction.NORTH) {
            return 0.0F;
        }
        if (side == Direction.EAST) {
            return 90.0F;
        }
        if (side == Direction.SOUTH) {
            return 180.0F;
        }
        if (side == Direction.WEST) {
            return 270.0F;
        }
        return Math.round(playerYaw / 90.0F) * 90.0F;
    }

    private static float pitchFor(Direction side) {
        if (side == Direction.UP) {
            return 90.0F;
        }
        if (side == Direction.DOWN) {
            return -90.0F;
        }
        return 0.0F;
    }

}
