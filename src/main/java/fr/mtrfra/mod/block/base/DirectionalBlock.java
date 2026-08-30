package fr.mtrfra.mod.block.base;

import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.DirectionProperty;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public abstract class DirectionalBlock extends BlockExtension {

    public static final DirectionProperty FACING = DirectionHelper.FACING;

    public DirectionalBlock(BlockSettings settings) {
        super(settings);
        setDefaultState2(getDefaultState2().with(new Property<>(FACING.data), Direction.NORTH.data));
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        return getDefaultState2().with(new Property<>(FACING.data), context.getPlayerFacing().getOpposite().data);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(FACING);
    }

}
