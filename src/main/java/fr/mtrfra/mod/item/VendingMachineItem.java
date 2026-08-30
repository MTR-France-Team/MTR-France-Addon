package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.VendingMachineEntity;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class VendingMachineItem extends EntityPlacementItem<VendingMachineEntity> {

    public VendingMachineItem(ItemSettings settings, EntityTypeRegistryObject<VendingMachineEntity> entityType) {
        super(settings, entityType, VendingMachineEntity::new, 0.0F);
    }

}
