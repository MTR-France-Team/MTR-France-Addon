package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.registry.ModEntities;
import fr.mtrfra.mod.registry.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;

public class CushionEntity extends SupportedPlacedEntity {

    public static final float SEAT_HEIGHT = 0.5F;
    public static final String TAG_COLOR = "color";

    public CushionEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public ActionResult interact2(PlayerEntity player, Hand hand) {
        if (!isAlwaysStationColored()) {
            final ItemStack stack = player.getStackInHand(hand);
            if (!stack.isEmpty() && stack.getItem().data instanceof DyeItem dyeItem) {
                if (!getEntityWorld2().isClient()) {
                    setColor(dyeItem.getDyeColor().getFireworkColor());
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                }
                return ActionResult.SUCCESS;
            }
            if (player.isSneaking()) {
                return ActionResult.PASS;
            }
        } else if (player.isSneaking()) {
            return ActionResult.PASS;
        }

        if (!getEntityWorld2().isClient()) {
            player.startRiding(new Entity(this));
        }
        return ActionResult.SUCCESS;
    }

    public boolean isAlwaysStationColored() {
        final net.minecraft.world.entity.EntityType<?> myType = this.getType();
        return myType == ModEntities.CUSHION_IDFM_SEAT_STATION_COLOR.get().data
                || myType == ModEntities.CUSHION_IDFM_SEAT_WITH_POLE_STATION_COLOR.get().data;
    }

    public int getCustomColor() {
        final Text customName = getCustomName2();
        if (customName == null) {
            return -1;
        }
        final String value = customName.getString();
        if (value.length() != 7 || value.charAt(0) != '#') {
            return -1;
        }
        try {
            return Integer.parseInt(value.substring(1), 16);
        } catch (final NumberFormatException exception) {
            return -1;
        }
    }

    public void setColor(int color) {
        setCustomName2(Text.of(String.format("#%06X", color & 0xFFFFFF)));
    }

    @Override
    public net.minecraft.world.item.ItemStack getPickResult() {
        final net.minecraft.world.item.Item item = resolveDropItem();
        if (item == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        final net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
        if (Screen.hasControlDown()) {
            final int color = getCustomColor();
            if (color >= 0) {
                stack.getOrCreateTag().putInt(TAG_COLOR, color);
            }
        }
        return stack;
    }

    //? if >=1.20.4 {
    @Override
    public Vec3 getPassengerRidingPosition(net.minecraft.world.entity.Entity passenger) {
        return new Vec3(getX(), getY() + SEAT_HEIGHT, getZ());
    }
    //? } else {
    /*@Override
    public double getPassengersRidingOffset() {
        return SEAT_HEIGHT;
    }
    *///? }

    @Override
    public Vector3d updatePassengerForDismount2(LivingEntity passenger) {
        return new Vector3d(anchorX, anchorY, anchorZ);
    }

    @Override
    protected net.minecraft.world.item.Item resolveDropItem() {
        if (dropItem != null) {
            return (net.minecraft.world.item.Item) dropItem.data;
        }
        final net.minecraft.world.entity.EntityType<?> myType = this.getType();
        if (myType == ModEntities.CUSHION_IDFM_SEAT.get().data) {
            return (net.minecraft.world.item.Item) ModItems.IDFM_SEAT_CUSHION.get().data;
        }
        if (myType == ModEntities.CUSHION_IDFM_SEAT_WITH_POLE.get().data) {
            return (net.minecraft.world.item.Item) ModItems.IDFM_SEAT_WITH_POLE_CUSHION.get().data;
        }
        if (myType == ModEntities.CUSHION_IDFM_SEAT_STATION_COLOR.get().data) {
            return (net.minecraft.world.item.Item) ModItems.IDFM_SEAT_CUSHION_STATION_COLOR.get().data;
        }
        if (myType == ModEntities.CUSHION_IDFM_SEAT_WITH_POLE_STATION_COLOR.get().data) {
            return (net.minecraft.world.item.Item) ModItems.IDFM_SEAT_WITH_POLE_CUSHION_STATION_COLOR.get().data;
        }
        return null;
    }

}
