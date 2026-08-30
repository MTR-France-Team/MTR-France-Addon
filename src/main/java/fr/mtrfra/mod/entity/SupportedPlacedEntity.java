package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.Init;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.EntityExtension;

public abstract class SupportedPlacedEntity extends EntityExtension {

    private static final int UNSUPPORTED_GRACE_TICKS = 30;

    protected double anchorX, anchorY, anchorZ;
    private boolean anchorInitialized = false;
    protected BlockPos supportPos;
    protected Item dropItem;
    protected int lifetimeTicks = 0;
    private int unsupportedTicks = 0;

    protected SupportedPlacedEntity(EntityType<?> type, World world) {
        super(type, world);
        setNoGravity(true);
    }

    public void placeAt(double x, double y, double z, BlockPos supportPos, float yaw, Item dropItem) {
        placeAt(x, y, z, supportPos, yaw, 0, dropItem);
    }

    public void placeAt(double x, double y, double z, BlockPos supportPos, float yaw, float pitch, Item dropItem) {
        anchorX = x;
        anchorY = y;
        anchorZ = z;
        anchorInitialized = true;
        this.supportPos = supportPos;
        this.dropItem = dropItem;
        setPosition2(anchorX, anchorY, anchorZ);
        setBodyYaw2(yaw);
        updatePositionAndAngles2(anchorX, anchorY, anchorZ, yaw, pitch);
    }

    public BlockPos getSupportPos() {
        return supportPos;
    }

    @Override
    public void tick2() {
        lifetimeTicks++;
        if (getEntityWorld2().isClient()) {
            onClientTick();
            return;
        }

        if (!anchorInitialized) {
            anchorX = getX2();
            anchorY = getY2();
            anchorZ = getZ2();
            anchorInitialized = true;
            if (supportPos == null) {
                supportPos = findSupportPos(anchorX, anchorY, anchorZ);
            }
        }

        setPosition2(anchorX, anchorY, anchorZ);

        if (!requiresSupport() || lifetimeTicks < 20 || supportPos == null) {
            return;
        }

        if (hasSupport()) {
            unsupportedTicks = 0;
            return;
        }
        unsupportedTicks++;
        if (unsupportedTicks >= UNSUPPORTED_GRACE_TICKS) {
            Init.LOGGER.warn("{} at {} lost its support at {} (lifetime {} ticks) - self-destructing", getClass().getSimpleName(), getPos2(), supportPos, lifetimeTicks);
            kill2();
        }
    }

    protected void onClientTick() {}

    protected boolean requiresSupport() {
        return true;
    }

    protected AABB makeRotatedBoundingBox(double localMinX, double localMaxX, double localMinZ, double localMaxZ, double height) {
        final double theta = Math.toRadians(-getYRot());
        final double cosTheta = Math.cos(theta);
        final double sinTheta = Math.sin(theta);
        final double[] cornerX = {localMinX, localMaxX, localMinX, localMaxX};
        final double[] cornerZ = {localMinZ, localMinZ, localMaxZ, localMaxZ};
        double minOffsetX = Double.POSITIVE_INFINITY, maxOffsetX = Double.NEGATIVE_INFINITY;
        double minOffsetZ = Double.POSITIVE_INFINITY, maxOffsetZ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            final double worldOffsetX = cornerX[i] * cosTheta + cornerZ[i] * sinTheta;
            final double worldOffsetZ = -cornerX[i] * sinTheta + cornerZ[i] * cosTheta;
            minOffsetX = Math.min(minOffsetX, worldOffsetX);
            maxOffsetX = Math.max(maxOffsetX, worldOffsetX);
            minOffsetZ = Math.min(minOffsetZ, worldOffsetZ);
            maxOffsetZ = Math.max(maxOffsetZ, worldOffsetZ);
        }
        final double x = getX();
        final double y = getY();
        final double z = getZ();
        return new AABB(x + minOffsetX, y, z + minOffsetZ, x + maxOffsetX, y + height, z + maxOffsetZ);
    }

    private BlockPos findSupportPos(double x, double y, double z) {
        final BlockPos center = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
        final World world = getEntityWorld2();
        for (final Direction direction : Direction.values()) {
            final BlockPos candidate = center.offset(direction);
            try {
                if (!world.isChunkLoaded(candidate.getX() >> 4, candidate.getZ() >> 4)) {
                    continue;
                }
                final BlockState state = world.getBlockState(candidate);
                if (!state.getOutlineShape(BlockView.cast(world), candidate).isEmpty()) {
                    return candidate;
                }
            } catch (final Exception exception) {
                Init.LOGGER.warn("{} support candidate check failed for {}", getClass().getSimpleName(), candidate, exception);
            }
        }
        return new BlockPos((int) Math.floor(x), (int) Math.floor(y - 0.01), (int) Math.floor(z));
    }

    private boolean hasSupport() {
        try {
            final World world = getEntityWorld2();
            if (!world.isChunkLoaded(supportPos.getX() >> 4, supportPos.getZ() >> 4)) {
                return true;
            }
            final BlockState state = world.getBlockState(supportPos);
            final VoxelShape shape = state.getOutlineShape(BlockView.cast(world), supportPos);
            return !shape.isEmpty();
        } catch (final Exception exception) {
            Init.LOGGER.warn("{} support check failed for {}", getClass().getSimpleName(), supportPos, exception);
            return true;
        }
    }

    @Override
    public boolean isPushable2() {
        return false;
    }

    @Override
    public boolean isAttackable2() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    protected abstract net.minecraft.world.item.Item resolveDropItem();

    private void dropAndKill() {
        final net.minecraft.world.item.Item item = resolveDropItem();
        if (item != null) {
            spawnAtLocation(item);
        }
        kill2();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!getEntityWorld2().isClient() && isAlive2()) {
            if (isBrokenByCreativePlayer(source)) {
                kill2();
            } else {
                dropAndKill();
            }
        }
        return true;
    }

    private static boolean isBrokenByCreativePlayer(DamageSource source) {
        final Entity attacker = source.getEntity();
        return attacker instanceof Player player && player.isCreative();
    }

    @Override
    protected void initDataTracker2() {}

}
