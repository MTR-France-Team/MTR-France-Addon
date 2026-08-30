package fr.mtrfra.mod.item;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.mapper.BlockItemExtension;

public class CopycatLayerBlockItem extends BlockItemExtension {

    public static final String KEY_MIMIC = "mimic_block";

    public CopycatLayerBlockItem(Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Override
    public boolean hasGlint2(ItemStack stack) {
        return stack.getOrCreateTag().contains(KEY_MIMIC);
    }

}
