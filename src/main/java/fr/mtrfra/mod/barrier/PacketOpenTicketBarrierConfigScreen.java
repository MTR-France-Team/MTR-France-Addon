package fr.mtrfra.mod.barrier;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public final class PacketOpenTicketBarrierConfigScreen extends PacketHandler {

    private final int x;
    private final int y;
    private final int z;
    private final int modeOrdinal;
    private final String acceptedTicketIdsCsv;

    public PacketOpenTicketBarrierConfigScreen(BlockPos pos, int modeOrdinal, String acceptedTicketIdsCsv) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.modeOrdinal = modeOrdinal;
        this.acceptedTicketIdsCsv = acceptedTicketIdsCsv;
    }

    public PacketOpenTicketBarrierConfigScreen(PacketBufferReceiver packetBufferReceiver) {
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
    public void runClient() {
        TicketBarrierConfigScreenOpener.open(new BlockPos(x, y, z), TicketBarrierMode.byOrdinalSafe(modeOrdinal), acceptedTicketIdsCsv);
    }

}
