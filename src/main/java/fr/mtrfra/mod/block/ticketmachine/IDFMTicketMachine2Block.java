package fr.mtrfra.mod.block.ticketmachine;

import org.mtr.mod.Blocks;
import org.mtr.mod.block.BlockTicketMachine;

public class IDFMTicketMachine2Block extends BlockTicketMachine {

    public IDFMTicketMachine2Block() {
        super(Blocks.createDefaultBlockSettings(true, state -> 5).nonOpaque());
    }

}
