package fr.mtrfra.mod.block.sign;

import fr.mtrfra.mod.registry.ModBlockEntities;
import fr.mtrfra.mod.util.ClientRedrawQueue;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mod.InitClient;

public class MotteLightStationColorBlock extends LightFixtureBlock implements BlockWithEntity {

    public MotteLightStationColorBlock(BlockSettings settings, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(settings, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    public static class BlockEntity extends BlockEntityExtension {

        private static final int NO_STATION_COLOR = 0x7F7F7F;

        private static final int RETRY_WINDOW_TICKS = 200;
        private static final int RETRY_INTERVAL_TICKS = 10;

        private int ticksAlive = 0;
        private boolean resolved = false;

        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.MOTTE_LIGHT_STATION_COLOR.get(), blockPos, blockState);
        }

        @Override
        public void blockEntityTick() {
            super.blockEntityTick();
            if (resolved || ticksAlive > RETRY_WINDOW_TICKS) {
                return;
            }
            ticksAlive++;
            if (ticksAlive % RETRY_INTERVAL_TICKS != 0) {
                return;
            }
            try {
                final World world = getWorld2();
                final Object worldData = world == null ? null : world.data;
                if (worldData instanceof Level level && level.isClientSide()) {
                    final BlockPos pos = getPos2();
                    resolved = InitClient.getStationColor(pos) != NO_STATION_COLOR;
                    ClientRedrawQueue.queue((net.minecraft.core.BlockPos) pos.data, level);
                }
            } catch (final Exception ignored) {}
        }

    }

}
