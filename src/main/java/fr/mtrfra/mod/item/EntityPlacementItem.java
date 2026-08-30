package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.SupportedPlacedEntity;
import org.mtr.mapping.holder.ActionResult;
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

public abstract class EntityPlacementItem<T extends SupportedPlacedEntity> extends ItemExtension {

    private final EntityTypeRegistryObject<T> entityType;
    private final EntityFactory<T> entityFactory;
    private final float yawOffset;

    protected EntityPlacementItem(ItemSettings settings, EntityTypeRegistryObject<T> entityType, EntityFactory<T> entityFactory, float yawOffset) {
        super(settings);
        this.entityType = entityType;
        this.entityFactory = entityFactory;
        this.yawOffset = yawOffset;
    }

    @FunctionalInterface
    public interface EntityFactory<T extends SupportedPlacedEntity> {
        T create(EntityType<?> type, World world);
    }

    @Override
    public ActionResult useOnBlock2(ItemUsageContext context) {
        if (context.getSide() != Direction.UP) {
            return ActionResult.FAIL;
        }

        final World world = context.getWorld();
        final BlockPos blockPos = context.getBlockPos();
        final BlockState state = world.getBlockState(blockPos);
        final VoxelShape shape = state.getOutlineShape(BlockView.cast(world), blockPos);
        if (shape.isEmpty()) {
            return ActionResult.FAIL;
        }

        final Vector3d hitPos = context.getHitPos();
        if (!world.isClient()) {
            final EntityType<T> type = entityType.get();
            final T entity = entityFactory.create(type, world);
            final float rawYaw = context.getPlayerYaw() + yawOffset;
            final float yaw = Math.round(rawYaw / 90.0F) * 90.0F;
            entity.placeAt(Math.floor(hitPos.getXMapped()) + 0.5, hitPos.getYMapped(), Math.floor(hitPos.getZMapped()) + 0.5, blockPos, yaw, context.getStack().getItem());
            onPlaced(entity, context);
            ServerWorld.cast(world).spawnEntity(new Entity(entity));
        }

        final PlayerEntity player = context.getPlayer();
        if (player != null && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    protected void onPlaced(T entity, ItemUsageContext context) {}

}
