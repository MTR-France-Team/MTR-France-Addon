package fr.mtrfra.mod;

import fr.mtrfra.mod.util.Constants;
import fr.mtrfra.mod.registry.MTRFRARegistry;
import fr.mtrfra.mod.registry.MTRFRARegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Init {

    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    private Init() {}

    public static void init() {
        LOGGER.info("Initializing {} (common)", Constants.MOD_NAME);
        MTRFRARegistry.register();
    }

    public static void initClient() {
        LOGGER.info("Initializing {} (client)", Constants.MOD_NAME);
        MTRFRARegistryClient.register();
    }

}
