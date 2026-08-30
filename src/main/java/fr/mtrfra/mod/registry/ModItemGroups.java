package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.tab.CreativeTabSection;
import fr.mtrfra.mod.tab.CreativeTabSections;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.world.item.Item;
import org.mtr.mapping.holder.ItemConvertible;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.registry.CreativeModeTabHolder;

//? if <1.20.1 {
/* import org.mtr.mapping.tool.HolderBase;
import java.util.function.Supplier;
*///? }


import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

public final class ModItemGroups {

    //? if >=1.20.1 {
    public static final CreativeModeTabHolder MAIN = MTRFRARegistry.REGISTRY.createCreativeModeTabHolder(
            Constants.id("main"),
            () -> new ItemStack(new ItemConvertible(ModItems.LOGO_MTRFRANCEADDON.get().data))
    );
    //? }

    public static final CreativeTabSection LOGOS = new CreativeTabSection(
            "logos", TextHelper.translatable("itemGroup.mtrfranceaddon.logos"), "tab_banner/logos", 0xB85FC7
    );
    public static final CreativeTabSection RAIL_CONNECTORS = new CreativeTabSection(
            "rail_connectors", TextHelper.translatable("itemGroup.mtrfranceaddon.rail_connectors"), "tab_banner/rail_connectors", 0x4A90D9
    );
    public static final CreativeTabSection STATION_EQUIPMENT = new CreativeTabSection(
            "station_equipment", TextHelper.translatable("itemGroup.mtrfranceaddon.station_equipment"), "tab_banner/station_equipment", 0xE0B84D
    );
    public static final CreativeTabSection BUILDING_MATERIALS = new CreativeTabSection(
            "building_materials", TextHelper.translatable("itemGroup.mtrfranceaddon.building_materials"), "tab_banner/building_materials", 0x93C976
    );
    public static final CreativeTabSection SIGNS = new CreativeTabSection(
            "sign_group", TextHelper.translatable("itemGroup.mtrfranceaddon.sign_group"), "tab_banner/sign_group", 0xCCCCCC
    );


    public static final List<CreativeTabSection> SECTION_ORDER = List.of(LOGOS, RAIL_CONNECTORS, STATION_EQUIPMENT, BUILDING_MATERIALS, SIGNS);

    static {
        CreativeTabSections.pinFirst(List.of(
                () -> (Item) ModItems.LOGO_MTRFRANCEADDON.get().data,
                () -> (Item) ModItems.LOGO_SNCF_ACTUEL.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1992_2005.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1985_1992_V1.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1985_1992_V2.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1967_1985_V1.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1967_1985_V2.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1947_1967.get().data,
                () -> (Item) ModItems.LOGO_SNCF_1938_1947.get().data,
                () -> (Item) ModItems.LOGO_RATP_1951_1960.get().data,
                () -> (Item) ModItems.LOGO_RATP_1960_1976.get().data,
                () -> (Item) ModItems.LOGO_RATP_1976_1992.get().data,
                () -> (Item) ModItems.LOGO_RATP_ACTUEL.get().data,
                () -> (Item) ModItems.LOGO_SNCF_SIGN.get().data,
                () -> (Item) ModBlocks.IDFM_METRO.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_1.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_2.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_3.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_3BIS.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_4.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_5.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_6.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_7.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_7BIS.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_8.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_9.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_10.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_11.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_12.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_13.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_14.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_15.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_16.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_17.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_METRO_18.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER_A.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER_B.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER_C.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER_D.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_RER_E.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_H.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_J.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_K.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_L.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_N.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_P.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_R.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_U.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAIN_V.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T1.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T2.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T3.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T3A.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T3B.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T4.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T5.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T6.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T7.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T8.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T9.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T10.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T11.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T12.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T13.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_TRAM_T14.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_CABLE.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_CABLE_1.get().asItem().data,
                () -> (Item) ModBlocks.IDFM_BUS.get().asItem().data
        ));

        CreativeTabSections.pinFirst(List.of(
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_30").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_30_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_50").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_50_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_70").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_70_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_90").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_90_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_110").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_110_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_130").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_130_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_150").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_150_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_170").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_170_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_220").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_220_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_270").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_270_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_320").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_320_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_350").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_350_oneway").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_999").get().data,
                () -> (Item) ModItems.RAIL_CONNECTORS.get("rail_999_oneway").get().data
        ));

        CreativeTabSections.pinFirst(List.of(
                () -> (Item) ModBlocks.PLATFORM_LAYER_SNCF.get().asItem().data,
                () -> (Item) ModBlocks.PLATFORM_LAYER_SNCF2.get().asItem().data,
                () -> (Item) ModBlocks.PLATFORM_LAYER_RATP.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_BUFFER.get().asItem().data,
                () -> (Item) ModBlocks.INVISIBLE_SLAB_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.INVISIBLE_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_PLATFORM_INDENTED.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_PLATFORM_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.SNCF2_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.SNCF2_PLATFORM_INDENTED.get().asItem().data,
                () -> (Item) ModBlocks.SNCF2_PLATFORM_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.RATP_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.RATP_PLATFORM_INDENTED.get().asItem().data,
                () -> (Item) ModBlocks.RATP_PLATFORM_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.RATP_ALARM_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.RATP_DOORCLOSE_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.RATP_FIREEXIT_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.RATP_CURVEDPLATFORM_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.RATP_NOSMOKING_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TICKET_BARRIER_SIDE_COVER.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TICKET_BARRIER_ENTRANCE.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TICKET_BARRIER_EXIT.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TICKET_BARRIER_ENTRANCE_WITH_SIDE.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TICKET_BARRIER_EXIT_WITH_SIDE.get().asItem().data,
                () -> (Item) ModBlocks.ECLAIRAGE_STATION_PARIS.get().asItem().data,
                () -> (Item) ModBlocks.MOTTE_LIGHT.get().asItem().data,
                () -> (Item) ModBlocks.MOTTE_LIGHT_STATION_COLOR.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TUNNEL_ARROW.get().asItem().data,
                () -> (Item) ModBlocks.RATP_TUNNEL_LINE.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_DOUBLE.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_PILLAR.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_WALL_RER.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_TOP.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_WALL_METRO_LARGE.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_WALL_METRO_SMALL.get().asItem().data,
                () -> (Item) ModBlocks.RATP_SIGN_MAP.get().asItem().data,
                () -> (Item) ModBlocks.RATP_PILLAR_POST.get().asItem().data,
                () -> (Item) ModBlocks.RATP_PILLAR_POST_BASE.get().asItem().data,
                () -> (Item) ModBlocks.TRANSILIEN_SIGN.get().asItem().data,
                () -> (Item) ModBlocks.TRANSILIEN_POLE.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_SIGN_DOUBLE.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_SIGN_WALL.get().asItem().data,
                () -> (Item) ModBlocks.SNCF_POLE.get().asItem().data,
                () -> (Item) ModItems.IDFM_SEAT_CUSHION.get().data,
                () -> (Item) ModItems.IDFM_SEAT_WITH_POLE_CUSHION.get().data,
                () -> (Item) ModItems.IDFM_SEAT_CUSHION_STATION_COLOR.get().data,
                () -> (Item) ModItems.IDFM_SEAT_WITH_POLE_CUSHION_STATION_COLOR.get().data,
                () -> (Item) ModItems.GREEN_VENDING_MACHINE.get().data,
                () -> (Item) ModItems.RED_VENDING_MACHINE.get().data,
                () -> (Item) ModItems.IDFM_TICKET_MACHINE.get().data,
                () -> (Item) ModItems.IDFM_TICKET_MACHINE_2.get().data
        ));

        CreativeTabSections.pinFirst(List.of(
                () -> (Item) ModBlocks.COPYCAT_LAYER.get().asItem().data,
                () -> (Item) ModBlocks.COPYCAT_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.COPYCAT_LAYER_PLATFORM.get().asItem().data,
                () -> (Item) ModBlocks.BEIGE_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.BEIGE_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.BEIGE_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.GREIGE_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.GREIGE_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.GREIGE_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.GREY_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.GREY_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.GREY_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.HONEY_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.HONEY_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.HONEY_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.IVORY_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.IVORY_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.IVORY_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.PINK_TERRACOTTA_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.PINK_TERRACOTTA_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.PINK_TERRACOTTA_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.SAND_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.SAND_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.SAND_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.GREEN_TERRACOTTA_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.GREEN_TERRACOTTA_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.GREEN_TERRACOTTA_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.ANTHRACITE_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.ANTHRACITE_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.ANTHRACITE_LIMESTONE_PAVING_SLAB.get().asItem().data,
                () -> (Item) ModBlocks.GREY_ANTHRACITE_LIMESTONE_PAVING.get().asItem().data,
                () -> (Item) ModBlocks.GREY_ANTHRACITE_LIMESTONE_PAVING_STAIRS.get().asItem().data,
                () -> (Item) ModBlocks.GREY_ANTHRACITE_LIMESTONE_PAVING_SLAB.get().asItem().data
        ));

        CreativeTabSections.pinFirst(List.of(
                () -> (Item) ModBlocks.POLE.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_20.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_40.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_60.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_80.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_120.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_160.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_200.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_300.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_DEPOT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_ONE_WAY_CORRECT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_ONE_WAY_WRONG.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_END_OF_TRACK.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_LEVEL_CROSSING.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SQUARE.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_STATION.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_4.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_6.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_8.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_10.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_12.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_14.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_CARS_16.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_PLATFORM_LEFT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_PLATFORM_RIGHT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_NO_TUNNEL_VENT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_S_CURVE_LEFT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_S_CURVE_RIGHT.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_TUNNEL.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_WHISTLE.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_100M.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_200M.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_300M.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_ONE_WAY_CORRECT_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_ONE_WAY_WRONG_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_20_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_40_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_60_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_80_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_120_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_160_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_200_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_300_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_DEPOT_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_END_OF_TRACK_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_S_CURVE_LEFT_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_S_CURVE_RIGHT_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_LEVEL_CROSSING_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SQUARE_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_STATION_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_NO_TUNNEL_VENT_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_TUNNEL_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_WHISTLE_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_100M_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_200M_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SWITCH_300M_GROUND.get().asItem().data,
                () -> (Item) ModBlocks.POLE_CONNECTION_LEFT.get().asItem().data,
                () -> (Item) ModBlocks.POLE_CONNECTION_RIGHT.get().asItem().data,
                () -> (Item) ModBlocks.POLE_CONNECTION_HORIZONTAL.get().asItem().data,
                () -> (Item) ModBlocks.POLE_CONNECTION_MIDDLE.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_20_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_40_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_60_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_80_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_120_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_160_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_200_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SPEED_300_GALLOWS.get().asItem().data,
                () -> (Item) ModBlocks.SIGN_SQUARE_GALLOWS.get().asItem().data
        ));
    }

    //? if >=1.20.1 {
    public static CreativeModeTabHolder tabFor(CreativeTabSection section) {
        return MAIN;
    }
    //? } else {
    /*private static final Map<CreativeTabSection, CreativeModeTabHolder> SECTION_TABS = new LinkedHashMap<>();

    private static CreativeModeTabHolder registerSectionTab(CreativeTabSection section, Supplier<? extends HolderBase<?>> itemSupplier) {
        final CreativeModeTabHolder holder = MTRFRARegistry.REGISTRY.createCreativeModeTabHolder(
                Constants.id(section.id()),
                () -> new ItemStack(ItemConvertible.cast(itemSupplier.get()))
        );
        SECTION_TABS.put(section, holder);
        return holder;
    }

    public static final CreativeModeTabHolder LOGOS_TAB = registerSectionTab(LOGOS, () -> ModItems.LOGO_MTRFRANCEADDON.get());
    public static final CreativeModeTabHolder RAIL_CONNECTORS_TAB = registerSectionTab(RAIL_CONNECTORS, () -> ModItems.RAIL_CONNECTORS.get("rail_30").get());
    public static final CreativeModeTabHolder STATION_EQUIPMENT_TAB = registerSectionTab(STATION_EQUIPMENT, () -> ModBlocks.SNCF_PLATFORM.get());
    public static final CreativeModeTabHolder BUILDING_MATERIALS_TAB = registerSectionTab(BUILDING_MATERIALS, () -> ModBlocks.BEIGE_LIMESTONE_PAVING.get());
    public static final CreativeModeTabHolder SIGNS_TAB = registerSectionTab(SIGNS, () -> ModBlocks.SIGN_SPEED_20.get());

    public static CreativeModeTabHolder tabFor(CreativeTabSection section) {
        return SECTION_TABS.get(section);
    }
    *///? }

    private ModItemGroups() {}

    public static void register() {}

}
