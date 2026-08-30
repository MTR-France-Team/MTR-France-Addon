package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.barrier.PacketOpenTicketBarrierConfigScreen;
import fr.mtrfra.mod.barrier.PacketSaveTicketBarrierConfig;
import fr.mtrfra.mod.sign.PacketOpenSignConfigScreen;
import fr.mtrfra.mod.sign.PacketSaveSignConfig;

public final class ModNetworking {

    private ModNetworking() {}

    public static void register() {
        MTRFRARegistry.REGISTRY.registerPacket(PacketOpenTicketBarrierConfigScreen.class, PacketOpenTicketBarrierConfigScreen::new);
        MTRFRARegistry.REGISTRY.registerPacket(PacketSaveTicketBarrierConfig.class, PacketSaveTicketBarrierConfig::new);
        MTRFRARegistry.REGISTRY.registerPacket(PacketOpenSignConfigScreen.class, PacketOpenSignConfigScreen::new);
        MTRFRARegistry.REGISTRY.registerPacket(PacketSaveSignConfig.class, PacketSaveSignConfig::new);
    }

    public static void registerClient() {}

}
