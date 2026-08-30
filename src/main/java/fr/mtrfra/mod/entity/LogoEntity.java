package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.registry.ModEntities;
import fr.mtrfra.mod.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.World;

public class LogoEntity extends SupportedPlacedEntity {

    private static final double HALF_WIDTH = 0.45;
    private static final double HALF_WIDTH_SNCF_SIGN = 0.95;
    private static final double HALF_HEIGHT_SNCF_SIGN = 0.47;
    private static final double HALF_DEPTH = 0.02;
    private static final double HALF_DEPTH_SNCF_SIGN = 0.05;

    public LogoEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void onClientTick() {
        if (lifetimeTicks <= 5) {
            setPosition2(getX2(), getY2(), getZ2());
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected boolean requiresSupport() {
        return this.getType() != ModEntities.LOGO_SNCF_SIGN.get().data;
    }

    @Override
    protected AABB makeBoundingBox() {
        final double x = getX();
        final double y = getY();
        final double z = getZ();
        final float pitch = getXRot();
        final boolean isSncfSign = this.getType() == ModEntities.LOGO_SNCF_SIGN.get().data;
        final double halfDepth = isSncfSign ? HALF_DEPTH_SNCF_SIGN : HALF_DEPTH;
        final double halfWidth = isSncfSign ? HALF_WIDTH_SNCF_SIGN : HALF_WIDTH;
        final double halfHeight = isSncfSign ? HALF_HEIGHT_SNCF_SIGN : HALF_WIDTH;

        if (pitch > 45.0F || pitch < -45.0F) {
            return new AABB(x - halfWidth, y - halfDepth, z - halfWidth, x + halfWidth, y + halfDepth, z + halfWidth);
        }

        final float normalizedYaw = ((getYRot() % 360.0F) + 360.0F) % 360.0F;
        if (normalizedYaw < 45.0F || normalizedYaw >= 315.0F || (normalizedYaw >= 135.0F && normalizedYaw < 225.0F)) {
            return new AABB(x - halfWidth, y - halfHeight, z - halfDepth, x + halfWidth, y + halfHeight, z + halfDepth);
        }
        return new AABB(x - halfDepth, y - halfHeight, z - halfWidth, x + halfDepth, y + halfHeight, z + halfWidth);
    }

    @Override
    public ItemStack getPickResult() {
        final Item item = resolveDropItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected Item resolveDropItem() {
        if (dropItem != null) {
            return (Item) dropItem.data;
        }
        final net.minecraft.world.entity.EntityType<?> myType = this.getType();
        if (myType == ModEntities.LOGO_MTRFRANCEADDON.get().data) {
            return (Item) ModItems.LOGO_MTRFRANCEADDON.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_ACTUEL.get().data) {
            return (Item) ModItems.LOGO_SNCF_ACTUEL.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1992_2005.get().data) {
            return (Item) ModItems.LOGO_SNCF_1992_2005.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1985_1992_V1.get().data) {
            return (Item) ModItems.LOGO_SNCF_1985_1992_V1.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1985_1992_V2.get().data) {
            return (Item) ModItems.LOGO_SNCF_1985_1992_V2.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1967_1985_V1.get().data) {
            return (Item) ModItems.LOGO_SNCF_1967_1985_V1.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1967_1985_V2.get().data) {
            return (Item) ModItems.LOGO_SNCF_1967_1985_V2.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1947_1967.get().data) {
            return (Item) ModItems.LOGO_SNCF_1947_1967.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_1938_1947.get().data) {
            return (Item) ModItems.LOGO_SNCF_1938_1947.get().data;
        }
        if (myType == ModEntities.LOGO_RATP_1951_1960.get().data) {
            return (Item) ModItems.LOGO_RATP_1951_1960.get().data;
        }
        if (myType == ModEntities.LOGO_RATP_1960_1976.get().data) {
            return (Item) ModItems.LOGO_RATP_1960_1976.get().data;
        }
        if (myType == ModEntities.LOGO_RATP_1976_1992.get().data) {
            return (Item) ModItems.LOGO_RATP_1976_1992.get().data;
        }
        if (myType == ModEntities.LOGO_RATP_ACTUEL.get().data) {
            return (Item) ModItems.LOGO_RATP_ACTUEL.get().data;
        }
        if (myType == ModEntities.LOGO_SNCF_SIGN.get().data) {
            return (Item) ModItems.LOGO_SNCF_SIGN.get().data;
        }
        return null;
    }

}
