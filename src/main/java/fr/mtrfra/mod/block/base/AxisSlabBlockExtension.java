package fr.mtrfra.mod.block.base;

import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.mapper.SlabBlockExtension;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public class AxisSlabBlockExtension extends SlabBlockExtension {

    public AxisSlabBlockExtension(BlockSettings settings) {
        super(settings);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(AxisBlock.AXIS);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final float yaw = ((context.getPlayerYaw() % 180) + 180) % 180;
        final AxisBlock.Axis axis = yaw >= 45 && yaw < 135 ? AxisBlock.Axis.X : AxisBlock.Axis.Z;
        return super.getPlacementState2(context).with(new Property<>(AxisBlock.AXIS.data), axis);
    }

}
