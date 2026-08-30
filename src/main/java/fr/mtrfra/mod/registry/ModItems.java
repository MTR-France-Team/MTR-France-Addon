package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.data.CustomRailData;
import fr.mtrfra.mod.item.CushionItem;
import fr.mtrfra.mod.item.CustomItemRailModifier;
import fr.mtrfra.mod.item.EnchantedCushionItem;
import fr.mtrfra.mod.item.LogoPlacementItem;
import fr.mtrfra.mod.item.TicketMachineItem;
import fr.mtrfra.mod.item.VendingMachineItem;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mod.data.RailType;

import java.util.HashMap;
import java.util.Map;

public final class ModItems {

    public static final Map<String, ItemRegistryObject> RAIL_CONNECTORS = new HashMap<>();

    public static final ItemRegistryObject IDFM_SEAT_CUSHION = MTRFRARegistry.registerItem(
            "idfm_seat_cushion",
            itemSettings -> new Item(new CushionItem(itemSettings, ModEntities.CUSHION_IDFM_SEAT)),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final ItemRegistryObject IDFM_SEAT_WITH_POLE_CUSHION = MTRFRARegistry.registerItem(
            "idfm_seat_with_pole_cushion",
            itemSettings -> new Item(new CushionItem(itemSettings, ModEntities.CUSHION_IDFM_SEAT_WITH_POLE)),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final ItemRegistryObject IDFM_SEAT_CUSHION_STATION_COLOR = MTRFRARegistry.registerItem(
            "idfm_seat_cushion_station_color",
            itemSettings -> new Item(new EnchantedCushionItem(itemSettings, ModEntities.CUSHION_IDFM_SEAT_STATION_COLOR)),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final ItemRegistryObject IDFM_SEAT_WITH_POLE_CUSHION_STATION_COLOR = MTRFRARegistry.registerItem(
            "idfm_seat_with_pole_cushion_station_color",
            itemSettings -> new Item(new EnchantedCushionItem(itemSettings, ModEntities.CUSHION_IDFM_SEAT_WITH_POLE_STATION_COLOR)),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final ItemRegistryObject GREEN_VENDING_MACHINE = MTRFRARegistry.registerItem(
            "vending_machine_green",
            itemSettings -> new Item(new VendingMachineItem(itemSettings, ModEntities.VENDING_MACHINE_GREEN)),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final ItemRegistryObject RED_VENDING_MACHINE = MTRFRARegistry.registerItem(
            "vending_machine_red",
            itemSettings -> new Item(new VendingMachineItem(itemSettings, ModEntities.VENDING_MACHINE_RED)),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final ItemRegistryObject IDFM_TICKET_MACHINE = MTRFRARegistry.registerItem(
            "idfm_ticket_machine",
            itemSettings -> new Item(new TicketMachineItem(itemSettings, ModEntities.TICKET_MACHINE_IDFM)),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final ItemRegistryObject IDFM_TICKET_MACHINE_2 = MTRFRARegistry.registerItem(
            "idfm_ticket_machine_2",
            itemSettings -> new Item(new TicketMachineItem(itemSettings, ModEntities.TICKET_MACHINE_IDFM_WIDE)),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final ItemRegistryObject LOGO_MTRFRANCEADDON = MTRFRARegistry.registerItem(
            "mtrfranceaddon_logo_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_MTRFRANCEADDON)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_ACTUEL = MTRFRARegistry.registerItem(
            "sncf_logo_actuel_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_ACTUEL)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1992_2005 = MTRFRARegistry.registerItem(
            "sncf_logo_1992-2005_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1992_2005)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1985_1992_V1 = MTRFRARegistry.registerItem(
            "sncf_logo_1985-1992_block_v1",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1985_1992_V1)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1985_1992_V2 = MTRFRARegistry.registerItem(
            "sncf_logo_1985-1992_block_v2",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1985_1992_V2)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1967_1985_V1 = MTRFRARegistry.registerItem(
            "sncf_logo_1967-1985_block_v1",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1967_1985_V1)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1967_1985_V2 = MTRFRARegistry.registerItem(
            "sncf_logo_1967-1985_block_v2",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1967_1985_V2)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1947_1967 = MTRFRARegistry.registerItem(
            "sncf_logo_1947-1967_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1947_1967)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_1938_1947 = MTRFRARegistry.registerItem(
            "sncf_logo_1938-1947_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_1938_1947)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_RATP_1951_1960 = MTRFRARegistry.registerItem(
            "ratp_logo_1951-1960_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_RATP_1951_1960)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_RATP_1960_1976 = MTRFRARegistry.registerItem(
            "ratp_logo_1960-1976_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_RATP_1960_1976)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_RATP_1976_1992 = MTRFRARegistry.registerItem(
            "ratp_logo_1976-1992_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_RATP_1976_1992)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_RATP_ACTUEL = MTRFRARegistry.registerItem(
            "ratp_logo_actuel_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_RATP_ACTUEL)),
            ModItemGroups.LOGOS
    );
    public static final ItemRegistryObject LOGO_SNCF_SIGN = MTRFRARegistry.registerItem(
            "sncf_sign_block",
            itemSettings -> new Item(new LogoPlacementItem(itemSettings, ModEntities.LOGO_SNCF_SIGN)),
            ModItemGroups.LOGOS
    );

    private ModItems() {}

    public static void register() {
        for (CustomRailData.RailInfo info : CustomRailData.CUSTOM_RAILS) {
            registerRailConnector(info, false);
            registerRailConnector(info, true);
        }
    }

    private static void registerRailConnector(CustomRailData.RailInfo info, boolean isOneWay) {
        String itemId = info.name.toLowerCase() + (isOneWay ? "_oneway" : "");

        ItemRegistryObject registeredItem = MTRFRARegistry.registerItem(
                itemId,
                itemSettings -> new Item(new CustomItemRailModifier(
                        isOneWay,
                        RailType.valueOf(info.name),
                        info.speed,
                        itemSettings
                )),
                ModItemGroups.RAIL_CONNECTORS
        );

        RAIL_CONNECTORS.put(itemId, registeredItem);
    }

}
