package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.registry.ModEntities;
import fr.mtrfra.mod.registry.ModItems;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.TextHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class VendingMachineEntity extends SupportedPlacedEntity {

    private static final double LOCAL_MIN_X = 0.0 - 0.5 + 0.125;
    private static final double LOCAL_MAX_X = 1.75 - 0.5 + 0.125;
    private static final double LOCAL_MIN_Z = 0.0 - 0.5;
    private static final double LOCAL_MAX_Z = 1.0 - 0.5;

    public VendingMachineEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public ActionResult interact2(PlayerEntity player, Hand hand) {
        player.sendMessage(Text.cast(TextHelper.translatable("message.mtrfranceaddon.vending_machine_coming_soon")), true);
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onClientTick() {
        if (lifetimeTicks <= 5) {
            setPosition2(getX2(), getY2(), getZ2());
        }
    }

    @Override
    public ItemStack getPickResult() {
        final net.minecraft.world.item.Item item = resolveDropItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected AABB makeBoundingBox() {
        return makeRotatedBoundingBox(LOCAL_MIN_X, LOCAL_MAX_X, LOCAL_MIN_Z, LOCAL_MAX_Z, 2.0);
    }

    @Override
    protected net.minecraft.world.item.Item resolveDropItem() {
        if (dropItem != null) {
            return (net.minecraft.world.item.Item) dropItem.data;
        }
        final net.minecraft.world.entity.EntityType<?> myType = this.getType();
        if (myType == ModEntities.VENDING_MACHINE_GREEN.get().data) {
            return (net.minecraft.world.item.Item) ModItems.GREEN_VENDING_MACHINE.get().data;
        }
        if (myType == ModEntities.VENDING_MACHINE_RED.get().data) {
            return (net.minecraft.world.item.Item) ModItems.RED_VENDING_MACHINE.get().data;
        }
        return null;
    }

}
