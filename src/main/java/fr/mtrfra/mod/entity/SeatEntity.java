package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.block.base.Sittable;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.EntityExtension;
import net.minecraft.world.entity.Entity;

public class SeatEntity extends EntityExtension {

    private double savedX, savedY, savedZ;
    private double anchorX, anchorY, anchorZ;
    private boolean anchorSet = false;
    private BlockPos originBlockPos;
    private int lifetimeTicks = 0;
    private boolean mounted = false;

    public SeatEntity(EntityType<?> type, World world) {
        super(type, world);
        setInvisible(true);
        setNoGravity(true);
        setNoClipMapped(true);
    }

    public void setAnchor(double x, double y, double z) {
        anchorX = x;
        anchorY = y;
        anchorZ = z;
        anchorSet = true;
    }

    public void setOriginBlockPos(BlockPos pos) {
        originBlockPos = pos;
    }

    public void savePlayerPosition(PlayerEntity player) {
        final Vector3d pos = player.getPos();
        savedX = pos.getXMapped();
        savedY = pos.getYMapped();
        savedZ = pos.getZMapped();
    }

    public double getSavedX() {
        return savedX;
    }

    public double getSavedY() {
        return savedY;
    }

    public double getSavedZ() {
        return savedZ;
    }

    public void setMounted() {
        mounted = true;
    }

    @Override
    public void tick2() {
        lifetimeTicks++;

        if (!anchorSet) {
            anchorX = getX2();
            anchorY = getY2();
            anchorZ = getZ2();
            anchorSet = true;
        }
        setPosition2(anchorX, anchorY, anchorZ);

        if (lifetimeTicks <= 3) {
            return;
        }
        if (!mounted && lifetimeTicks > 20) {
            kill2();
            return;
        }
        if (!getEntityWorld2().isClient() && mounted && lifetimeTicks > 15 && getFirstPassenger() == null) {
            kill2();
            return;
        }
        if (mounted) {
            if (lifetimeTicks > 5 && originBlockPos != null) {
                final World world = getEntityWorld2();
                final BlockState stateHere = world.getBlockState(originBlockPos);
                final BlockPos above = new BlockPos(originBlockPos.getX(), originBlockPos.getY() + 1, originBlockPos.getZ());
                final BlockState stateAbove = world.getBlockState(above);
                if (!(stateHere.getBlock().data instanceof Sittable) && !(stateAbove.getBlock().data instanceof Sittable)) {
                    kill2();
                }
            }
        }
    }

    @Override
    public boolean isPushable2() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    protected void initDataTracker2() {}

}
