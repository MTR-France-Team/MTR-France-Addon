package fr.mtrfra.mod.entity;

import fr.mtrfra.mod.registry.ModEntities;
import fr.mtrfra.mod.registry.ModItems;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mod.Init;
import org.mtr.mod.data.TicketSystem;
import org.mtr.mod.packet.PacketOpenTicketMachineScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class TicketMachineEntity extends SupportedPlacedEntity {

    private final double localMinX, localMaxX, localMinZ, localMaxZ;

    public TicketMachineEntity(EntityType<?> type, World world) {
        super(type, world);
        final boolean wide = type.data == ModEntities.TICKET_MACHINE_IDFM_WIDE.get().data;
        if (wide) {
            localMinX = 1.5 / 16.0 - 0.5;
            localMaxX = 30.5 / 16.0 - 0.5;
            localMinZ = -0.5 / 16.0 - 0.5;
            localMaxZ = 16.0 / 16.0 - 0.5;
        } else {
            localMinX = -0.5;
            localMaxX = 0.5;
            localMinZ = -0.5;
            localMaxZ = 0.5;
        }
    }

    @Override
    public ActionResult interact2(PlayerEntity player, Hand hand) {
        final World world = getEntityWorld2();
        if (!world.isClient()) {
            Init.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketOpenTicketMachineScreen(TicketSystem.getBalance(world, player)));
        }
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
        final Item item = resolveDropItem();
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected AABB makeBoundingBox() {
        return makeRotatedBoundingBox(localMinX, localMaxX, localMinZ, localMaxZ, 1.875);
    }

    @Override
    protected Item resolveDropItem() {
        if (dropItem != null) {
            return (Item) dropItem.data;
        }
        final net.minecraft.world.entity.EntityType<?> myType = this.getType();
        if (myType == ModEntities.TICKET_MACHINE_IDFM.get().data) {
            return (Item) ModItems.IDFM_TICKET_MACHINE.get().data;
        }
        if (myType == ModEntities.TICKET_MACHINE_IDFM_WIDE.get().data) {
            return (Item) ModItems.IDFM_TICKET_MACHINE_2.get().data;
        }
        return null;
    }

}
