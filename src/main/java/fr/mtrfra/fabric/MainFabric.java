package fr.mtrfra.fabric;

//? if fabric {

import fr.mtrfra.mod.Init;
import net.fabricmc.api.ModInitializer;

public class MainFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Init.init();
    }

}

//? }
