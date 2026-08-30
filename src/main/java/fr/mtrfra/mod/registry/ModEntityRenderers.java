package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.render.CushionEntityRenderer;
import fr.mtrfra.mod.render.LogoEntityRenderer;
import fr.mtrfra.mod.render.SeatEntityRenderer;
import fr.mtrfra.mod.render.TicketMachineEntityRenderer;
import fr.mtrfra.mod.render.VendingMachineEntityRenderer;

public final class ModEntityRenderers {

    private ModEntityRenderers() {}

    public static void registerClient() {
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.SEAT, SeatEntityRenderer::new);
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.CUSHION_IDFM_SEAT, argument -> new CushionEntityRenderer(argument, () -> ModBlocks.IDFM_SEAT));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.CUSHION_IDFM_SEAT_WITH_POLE, argument -> new CushionEntityRenderer(argument, () -> ModBlocks.IDFM_SEAT_WITH_POLE));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.CUSHION_IDFM_SEAT_STATION_COLOR, argument -> new CushionEntityRenderer(argument, () -> ModBlocks.IDFM_SEAT));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.CUSHION_IDFM_SEAT_WITH_POLE_STATION_COLOR, argument -> new CushionEntityRenderer(argument, () -> ModBlocks.IDFM_SEAT_WITH_POLE));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.VENDING_MACHINE_GREEN, argument -> new VendingMachineEntityRenderer(argument, () -> ModBlocks.GREEN_VENDING_MACHINE));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.VENDING_MACHINE_RED, argument -> new VendingMachineEntityRenderer(argument, () -> ModBlocks.RED_VENDING_MACHINE));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.TICKET_MACHINE_IDFM, argument -> new TicketMachineEntityRenderer(argument, () -> ModBlocks.IDFM_TICKET_MACHINE));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.TICKET_MACHINE_IDFM_WIDE, argument -> new TicketMachineEntityRenderer(argument, () -> ModBlocks.IDFM_TICKET_MACHINE_2));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_MTRFRANCEADDON, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_MTRFRANCEADDON));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_ACTUEL, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_ACTUEL));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1992_2005, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1992_2005));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1985_1992_V1, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1985_1992_V1));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1985_1992_V2, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1985_1992_V2));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1967_1985_V1, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1967_1985_V1));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1967_1985_V2, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1967_1985_V2));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1947_1967, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1947_1967));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_1938_1947, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_1938_1947));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_RATP_1951_1960, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_RATP_1951_1960));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_RATP_1960_1976, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_RATP_1960_1976));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_RATP_1976_1992, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_RATP_1976_1992));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_RATP_ACTUEL, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_RATP_ACTUEL));
        MTRFRARegistryClient.REGISTRY_CLIENT.registerEntityRenderer(ModEntities.LOGO_SNCF_SIGN, argument -> new LogoEntityRenderer(argument, () -> ModBlocks.LOGO_SNCF_SIGN));
    }

}
