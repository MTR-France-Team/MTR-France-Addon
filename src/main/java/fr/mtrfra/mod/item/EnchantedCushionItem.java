package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.CushionEntity;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class EnchantedCushionItem extends CushionItem {

    public EnchantedCushionItem(ItemSettings settings, EntityTypeRegistryObject<CushionEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public boolean hasGlint2(ItemStack stack) {
        return true;
    }

}
