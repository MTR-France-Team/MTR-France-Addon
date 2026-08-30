package fr.mtrfra.mod.block.copycat;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.IntegerProperty;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public class CopycatLayerBlock extends CopycatBlockBase {

    public static final IntegerProperty LAYERS = IntegerProperty.of("layers", 1, 8);
    private static final Property<Integer> LAYERS_PROPERTY = Property.cast(LAYERS);
    private static final int MAX_LAYERS = 8;

    public CopycatLayerBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(LAYERS);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final org.mtr.mapping.holder.BlockPos pos = context.getBlockPos();
        final BlockState existing = context.getWorld().getBlockState(pos);
        if (existing.isOf(asBlock2())) {
            final int layers = existing.get(LAYERS_PROPERTY);
            return existing.with(LAYERS_PROPERTY, Math.min(MAX_LAYERS, layers + 1));
        }
        return getDefaultState2();
    }

    @Override
    public boolean canReplace2(BlockState state, ItemPlacementContext context) {
        return state.isOf(asBlock2()) && state.get(LAYERS_PROPERTY) < MAX_LAYERS && context.getStack().getItem().equals(asItem2());
    }

    @Override
    protected int heightUnits(BlockState state) {
        return state.get(LAYERS_PROPERTY) * 2;
    }

}
