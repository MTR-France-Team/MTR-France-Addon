package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.CushionEntity;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemUsageContext;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class CushionItem extends EntityPlacementItem<CushionEntity> {

    public CushionItem(ItemSettings settings, EntityTypeRegistryObject<CushionEntity> entityType) {
        super(settings, entityType, CushionEntity::new, 180.0F);
    }

    @Override
    protected void onPlaced(CushionEntity entity, ItemUsageContext context) {
        final CompoundTag tag = context.getStack().getTag();
        if (tag != null && tag.contains(CushionEntity.TAG_COLOR)) {
            entity.setColor(tag.getInt(CushionEntity.TAG_COLOR));
        }
    }

}
