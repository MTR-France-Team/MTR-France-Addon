package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.entity.SeatEntity;
import fr.mtrfranceaddon.mod.common.entity.SeatEntityRenderer;
import org.mtr.mapping.registry.EntityTypeRegistryObject;

public class ModEntities {

    public static EntityTypeRegistryObject<SeatEntity> SEAT;

    public static void register() {
        SEAT = MTRFranceAddonRegistry.registerEntity(
                "seat",
                SeatEntity::new,
                0.01f, 0.01f
        );
    }

    public static void registerClient() {
        MTRFranceAddonRegistryClient.REGISTRY_CLIENT.registerEntityRenderer(
                ModEntities.SEAT,
                SeatEntityRenderer::new
        );
    }

}
