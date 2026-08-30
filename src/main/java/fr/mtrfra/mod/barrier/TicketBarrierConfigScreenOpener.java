package fr.mtrfra.mod.barrier;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Screen;

final class TicketBarrierConfigScreenOpener {

    private TicketBarrierConfigScreenOpener() {}

    static void open(BlockPos pos, TicketBarrierMode mode, String acceptedTicketIdsCsv) {
        MinecraftClient.getInstance().openScreen(new Screen(new TicketBarrierConfigScreen(pos, mode, acceptedTicketIdsCsv)));
    }

}
