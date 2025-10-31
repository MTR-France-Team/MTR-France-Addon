package fr.mtrfranceaddon.mod.common;

import fr.mtrfranceaddon.mod.common.registry.MTRFranceAddonRegistry;
import fr.mtrfranceaddon.mod.common.registry.MTRFranceAddonRegistryClient;
import fr.mtrfranceaddon.mod.common.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Init {

    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    public static void init() {
        LOGGER.info("Initializing {} v{}", Constants.MOD_NAME, Constants.MOD_VERSION);
        MTRFranceAddonRegistry.register();
    }

    public static void initClient() {
        MTRFranceAddonRegistryClient.register();
    }

}
