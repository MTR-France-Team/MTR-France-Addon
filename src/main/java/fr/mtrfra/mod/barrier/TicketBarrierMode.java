package fr.mtrfra.mod.barrier;

public enum TicketBarrierMode {

    MTR_BALANCE,
    FREE_ENTRY;

    public static TicketBarrierMode byOrdinalSafe(int ordinal) {
        final TicketBarrierMode[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : MTR_BALANCE;
    }

}
