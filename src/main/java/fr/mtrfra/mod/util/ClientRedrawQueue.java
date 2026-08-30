package fr.mtrfra.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientRedrawQueue {

    private static final Set<BlockPos> PENDING = ConcurrentHashMap.newKeySet();
    private static Level pendingLevel;

    private ClientRedrawQueue() {}

    public static void queue(BlockPos pos, Level level) {
        pendingLevel = level;
        PENDING.add(pos);
    }

    public static void tick() {
        if (PENDING.isEmpty()) {
            return;
        }
        final Level level = pendingLevel;
        final Set<BlockPos> positions = new HashSet<>(PENDING);
        PENDING.clear();
        if (level == null) {
            return;
        }
        for (final BlockPos pos : positions) {
            final BlockState state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

}
