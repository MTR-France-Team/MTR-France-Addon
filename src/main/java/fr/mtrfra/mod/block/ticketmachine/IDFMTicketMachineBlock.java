package fr.mtrfra.mod.block.ticketmachine;

import org.mtr.mod.Blocks;
import org.mtr.mod.block.BlockTicketMachine;

public class IDFMTicketMachineBlock extends BlockTicketMachine {

    public IDFMTicketMachineBlock() {
        super(Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque());
    }

}
