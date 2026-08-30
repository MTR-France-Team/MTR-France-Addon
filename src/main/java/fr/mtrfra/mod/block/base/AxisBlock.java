package fr.mtrfra.mod.block.base;

import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.EnumProperty;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.StringIdentifiable;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public class AxisBlock extends BlockExtension {

    public static final EnumProperty<Axis> AXIS = EnumProperty.of("axis", Axis.class);

    public AxisBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(AXIS);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final float yaw = ((context.getPlayerYaw() % 180) + 180) % 180;
        final Axis axis = yaw >= 45 && yaw < 135 ? Axis.X : Axis.Z;
        return getDefaultState2().with(new Property<>(AXIS.data), axis);
    }

    public enum Axis implements StringIdentifiable {
        X("x"), Z("z");

        private final String name;

        Axis(String name) {
            this.name = name;
        }

        @Override
        public String asString2() {
            return name;
        }
    }

}
