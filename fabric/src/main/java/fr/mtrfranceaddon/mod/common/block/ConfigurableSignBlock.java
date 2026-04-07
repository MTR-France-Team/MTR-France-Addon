package fr.mtrfranceaddon.mod.common.block;

import fr.mtrfranceaddon.mod.common.block.base.DirectionalBlock;
import org.mtr.mapping.holder.*;
import org.mtr.mod.block.IBlock;

import javax.annotation.Nonnull;

public class ConfigurableSignBlock extends DirectionalBlock {

    private final double x1, y1, z1, x2, y2, z2;

    public ConfigurableSignBlock(BlockSettings settings, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(settings);
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.x2 = x2; this.y2 = y2; this.z2 = z2;
    }

    @Override
    public @Nonnull VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = IBlock.getStatePropertySafe(state, FACING);
        return IBlock.getVoxelShapeByDirection(x1, y1, z1, x2, y2, z2, facing);
    }
}
