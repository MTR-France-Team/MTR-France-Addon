package fr.mtrfra.mod.block.sign;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.mtr.mapping.holder.BlockSettings;

public class LightFixtureBlock extends ConfigurableSignBlock {

    private static final int LIGHT_LEVEL = 15;

    public LightFixtureBlock(BlockSettings settings, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(applyLight(settings), x1, y1, z1, x2, y2, z2);
    }

    private static BlockSettings applyLight(BlockSettings settings) {
        ((BlockBehaviour.Properties) settings.data).lightLevel(state -> LIGHT_LEVEL);
        return settings;
    }

}
