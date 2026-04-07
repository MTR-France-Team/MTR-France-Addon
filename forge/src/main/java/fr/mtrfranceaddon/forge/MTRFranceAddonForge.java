package fr.mtrfranceaddon.forge;

import fr.mtrfranceaddon.mod.common.util.Constants;
import fr.mtrfranceaddon.mod.common.Init;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Constants.MOD_ID)
public class MTRFranceAddonForge {

    private static final Logger LOGGER = LoggerFactory.getLogger("MTRFranceAddon");

    public MTRFranceAddonForge() {
        Init.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Init::initClient);
    }

}
