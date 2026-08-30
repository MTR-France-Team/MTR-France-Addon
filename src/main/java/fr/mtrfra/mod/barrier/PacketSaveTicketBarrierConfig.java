package fr.mtrfra.mod.barrier;

import fr.mtrfra.mod.block.barrier.RATPTicketBarrierBlock;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public final class PacketSaveTicketBarrierConfig extends PacketHandler {

    private final int x;
    private final int y;
    private final int z;
    private final int modeOrdinal;
    private final String acceptedTicketIdsCsv;

    public PacketSaveTicketBarrierConfig(BlockPos pos, TicketBarrierMode mode, String acceptedTicketIdsCsv) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.modeOrdinal = mode.ordinal();
        this.acceptedTicketIdsCsv = acceptedTicketIdsCsv;
    }

    public PacketSaveTicketBarrierConfig(PacketBufferReceiver packetBufferReceiver) {
        x = packetBufferReceiver.readInt();
        y = packetBufferReceiver.readInt();
        z = packetBufferReceiver.readInt();
        modeOrdinal = packetBufferReceiver.readInt();
        acceptedTicketIdsCsv = packetBufferReceiver.readString();
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeInt(x);
        packetBufferSender.writeInt(y);
        packetBufferSender.writeInt(z);
        packetBufferSender.writeInt(modeOrdinal);
        packetBufferSender.writeString(acceptedTicketIdsCsv);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        final World world = World.cast(serverPlayerEntity.getServerWorld());
        final BlockPos pos = new BlockPos(x, y, z);
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.data instanceof RATPTicketBarrierBlock.BarrierBlockEntity) {
            final RATPTicketBarrierBlock.BarrierBlockEntity barrierBlockEntity = (RATPTicketBarrierBlock.BarrierBlockEntity) blockEntity.data;
            barrierBlockEntity.mode = TicketBarrierMode.byOrdinalSafe(modeOrdinal);
            barrierBlockEntity.acceptedTicketIdsCsv = acceptedTicketIdsCsv;
            barrierBlockEntity.markDirty2();
        }
    }

}
