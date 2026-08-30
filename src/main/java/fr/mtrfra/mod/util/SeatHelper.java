package fr.mtrfra.mod.util;

import fr.mtrfra.mod.entity.SeatEntity;
import fr.mtrfra.mod.registry.ModEntities;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.World;

public final class SeatHelper {

    private SeatHelper() {}

    public static ActionResult trySit(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (player.isSneaking() || player.getVehicle() != null) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            final VoxelShape shape = state.getOutlineShape(BlockView.cast(world), pos);
            final double height = shape.isEmpty() ? 1 : shape.getBoundingBox().getMaxYMapped();

            final double seatX = pos.getX() + 0.5;
            final double seatY = pos.getY() + height;
            final double seatZ = pos.getZ() + 0.5;

            final SeatEntity seat = new SeatEntity(ModEntities.SEAT.get(), world);
            seat.setPosition2(seatX, seatY, seatZ);
            seat.setAnchor(seatX, seatY, seatZ);
            seat.setOriginBlockPos(pos);
            seat.setBodyYaw2(player.getHorizontalFacing().asRotation());
            seat.savePlayerPosition(player);

            ServerWorld.cast(world).spawnEntity(new Entity(seat));
            player.startRiding(new Entity(seat));
            seat.setMounted();
        }
        return ActionResult.SUCCESS;
    }

}
