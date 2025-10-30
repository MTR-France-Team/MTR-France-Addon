package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.Init;
import fr.mtrfranceaddon.mod.common.block.InvisiblePlatform;

public class ModBlockEntityRenderers {

    public static void registerClient() {
        Init.LOGGER.info("Registering Block Entity Renderers");

        MTRFranceAddonRegistryClient.REGISTRY_CLIENT.registerBlockEntityRenderer(ModBlockEntities.INVISIBLE_PLATFORM, InvisiblePlatform.InvPlatRenderer::new);
    }

}
