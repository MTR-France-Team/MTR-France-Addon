package fr.mtrfranceaddon.mod.common.entity;

import fr.mtrfranceaddon.mod.common.block.SeatBlock;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.EntityExtension;

public class SeatEntity extends EntityExtension {

    private double savedX, savedY, savedZ;
    private int lifetimeTicks = 0;
    private boolean mounted = false;

    public SeatEntity(EntityType<?> type, World world) {
        super(type, world);
        super.setInvisible(true);
        super.setNoGravity(true);
        setNoClipMapped(true);
    }

    public void savePlayerPosition(PlayerEntity player) {
        Vector3d pos = player.getPos();
        this.savedX = pos.getXMapped();
        this.savedY = pos.getYMapped();
        this.savedZ = pos.getZMapped();
    }

    public double getSavedX() { return savedX; }
    public double getSavedY() { return savedY; }
    public double getSavedZ() { return savedZ; }

    public void setMounted() {
        this.mounted = true;
    }

    @Override
    public void tick2() {
        lifetimeTicks++;

        if (lifetimeTicks <= 3) return;

        if (!mounted && lifetimeTicks > 20) {
            kill2();
            return;
        }

        if (mounted && lifetimeTicks > 5) {
            BlockPos blockPos = getBlockPos2();
            World w = getEntityWorld2();
            BlockState stateHere = w.getBlockState(blockPos);
            BlockPos above = new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
            BlockState stateAbove = w.getBlockState(above);

            if (!(stateHere.getBlock().data instanceof SeatBlock)
                    && !(stateAbove.getBlock().data instanceof SeatBlock)) {
                kill2();
            }
        }
    }

    @Override
    protected void initDataTracker2() {
    }

}
