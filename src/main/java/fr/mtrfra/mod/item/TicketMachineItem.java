package fr.mtrfra.mod.item;

import fr.mtrfra.mod.entity.TicketMachineEntity;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class TicketMachineItem extends EntityPlacementItem<TicketMachineEntity> {

    public TicketMachineItem(ItemSettings settings, EntityTypeRegistryObject<TicketMachineEntity> entityType) {
        super(settings, entityType, TicketMachineEntity::new, 0.0F);
    }

}
