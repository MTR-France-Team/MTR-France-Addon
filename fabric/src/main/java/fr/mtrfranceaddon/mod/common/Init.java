package fr.mtrfranceaddon.mod.common;

import fr.mtrfranceaddon.mod.common.registry.MTRFranceAddonRegistry;
import fr.mtrfranceaddon.mod.common.registry.MTRFranceAddonRegistryClient;
import fr.mtrfranceaddon.mod.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init {

    public static final Logger LOGGER = LogManager.getLogger("mtrfranceaddon");

    public static void init() {
        LOGGER.info("Initializing {} v{}", Constants.MOD_NAME, Constants.MOD_VERSION);
        MTRFranceAddonRegistry.register();
    }

    public static void initClient() {
        MTRFranceAddonRegistryClient.register();
    }

}
