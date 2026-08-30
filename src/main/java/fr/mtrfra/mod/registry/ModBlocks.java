package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.block.base.DirectionalBlock;
import fr.mtrfra.mod.block.platform.BlockPlatformLayer;
import fr.mtrfra.mod.block.BufferBlock;
import fr.mtrfra.mod.block.sign.ConfigurableSignBlock;
import fr.mtrfra.mod.block.copycat.CopycatLayerBlock;
import fr.mtrfra.mod.block.copycat.CopycatLayerPlatformBlock;
import fr.mtrfra.mod.block.copycat.CopycatPlatformBlock;
import fr.mtrfra.mod.block.HitboxCompanionBlock;
import fr.mtrfra.mod.block.ticketmachine.IDFMTicketMachine2Block;
import fr.mtrfra.mod.block.ticketmachine.IDFMTicketMachineBlock;
import fr.mtrfra.mod.block.platform.InvisiblePlatform;
import fr.mtrfra.mod.block.platform.InvisibleSlabPlatform;
import fr.mtrfra.mod.block.sign.LightFixtureBlock;
import fr.mtrfra.mod.block.LogoBlock;
import fr.mtrfra.mod.block.sign.MotteLightBlock;
import fr.mtrfra.mod.block.sign.MotteLightStationColorBlock;
import fr.mtrfra.mod.block.platform.SittablePlatformSlab;
import fr.mtrfra.mod.block.barrier.RATPTicketBarrierBlock;
import fr.mtrfra.mod.block.barrier.RATPTicketBarrierCollisionExtensionBlock;
import fr.mtrfra.mod.block.barrier.RATPTicketBarrierSideCoverBlock;
import fr.mtrfra.mod.block.barrier.RATPTicketBarrierUpperBlock;
import fr.mtrfra.mod.block.base.AxisBlock;
import fr.mtrfra.mod.block.base.HangingSignBlock;
import fr.mtrfra.mod.block.sign.RATPSignBase;
import fr.mtrfra.mod.block.sign.RATPSignCollisionExtensionBlock;
import fr.mtrfra.mod.block.sign.RATPSignMapBlock;
import fr.mtrfra.mod.block.sign.TextAlignment;
import fr.mtrfra.mod.block.sign.RATPPillarPostBaseBlock;
import fr.mtrfra.mod.block.sign.RATPPillarPostBlock;
import fr.mtrfra.mod.block.sign.SNCFPoleBlock;
import fr.mtrfra.mod.block.sign.TransilienPoleBlock;
import fr.mtrfra.mod.item.CopycatLayerBlockItem;
import fr.mtrfra.mod.item.MotteLightStationColorBlockItem;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockHelper;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.Blocks;
import org.mtr.mod.block.BlockPlatform;

public final class ModBlocks {

    public static final BlockRegistryObject PLATFORM_LAYER_SNCF = MTRFRARegistry.registerBlockWithItem(
            "platform_layer_sncf",
            () -> new Block(new BlockPlatformLayer(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject PLATFORM_LAYER_SNCF2 = MTRFRARegistry.registerBlockWithItem(
            "platform_layer_sncf2",
            () -> new Block(new BlockPlatformLayer(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject PLATFORM_LAYER_RATP = MTRFRARegistry.registerBlockWithItem(
            "platform_layer_ratp",
            () -> new Block(new BlockPlatformLayer(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject COPYCAT_LAYER = MTRFRARegistry.registerBlockWithItem(
            "copycat_layer",
            () -> new Block(new CopycatLayerBlock(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            CopycatLayerBlockItem::new,
            ModItemGroups.BUILDING_MATERIALS
    );

    public static final BlockRegistryObject COPYCAT_LAYER_PLACEHOLDER = MTRFRARegistry.registerBlock(
            "copycat_layer_placeholder", () -> new Block(new BlockExtension(Blocks.createDefaultBlockSettings(true)))
    );

    public static final BlockRegistryObject COPYCAT_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "copycat_platform",
            () -> new Block(new CopycatPlatformBlock(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            CopycatLayerBlockItem::new,
            ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject COPYCAT_LAYER_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "copycat_layer_platform",
            () -> new Block(new CopycatLayerPlatformBlock(BlockHelper.createBlockSettings(false, false).strength(0.2f).nonOpaque().dynamicBounds())),
            CopycatLayerBlockItem::new,
            ModItemGroups.BUILDING_MATERIALS
    );

    public static final BlockRegistryObject SNCF_BUFFER = MTRFRARegistry.registerBlockWithItem(
            "sncf_buffer",
            () -> new Block(new BufferBlock(Blocks.createDefaultBlockSettings(false))),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject INVISIBLE_SLAB_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "invisible_slab_platform",
            () -> new Block(new InvisibleSlabPlatform(Blocks.createDefaultBlockSettings(false).strength(-1.0F, 3600000.0F).dropsNothing())),
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject INVISIBLE_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "invisible_platform",
            () -> new Block(new InvisiblePlatform(Blocks.createDefaultBlockSettings(false).strength(-1.0F, 3600000.0F).dropsNothing())),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject SNCF_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "sncf_platform", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), false)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF_PLATFORM_INDENTED = MTRFRARegistry.registerBlockWithItem(
            "sncf_platform_indented", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), true)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF_PLATFORM_SLAB = MTRFRARegistry.registerBlockWithItem(
            "sncf_platform_slab", () -> new Block(new SittablePlatformSlab(Blocks.createDefaultBlockSettings(false))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF2_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "sncf2_platform", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), false)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF2_PLATFORM_INDENTED = MTRFRARegistry.registerBlockWithItem(
            "sncf2_platform_indented", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), true)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF2_PLATFORM_SLAB = MTRFRARegistry.registerBlockWithItem(
            "sncf2_platform_slab", () -> new Block(new SittablePlatformSlab(Blocks.createDefaultBlockSettings(false))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_PLATFORM = MTRFRARegistry.registerBlockWithItem(
            "ratp_platform", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), false)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_PLATFORM_INDENTED = MTRFRARegistry.registerBlockWithItem(
            "ratp_platform_indented", () -> new Block(new BlockPlatform(Blocks.createDefaultBlockSettings(false), true)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_PLATFORM_SLAB = MTRFRARegistry.registerBlockWithItem(
            "ratp_platform_slab", () -> new Block(new SittablePlatformSlab(Blocks.createDefaultBlockSettings(false))), ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject HITBOX_COMPANION = MTRFRARegistry.registerBlock(
            "hitbox_companion", () -> new Block(new HitboxCompanionBlock())
    );

    public static final BlockRegistryObject IDFM_TICKET_MACHINE = MTRFRARegistry.registerBlock(
            "idfm_ticket_machine", () -> new Block(new IDFMTicketMachineBlock())
    );
    public static final BlockRegistryObject IDFM_TICKET_MACHINE_2 = MTRFRARegistry.registerBlock(
            "idfm_ticket_machine_2", () -> new Block(new IDFMTicketMachine2Block())
    );

    public static final BlockRegistryObject GREEN_VENDING_MACHINE = MTRFRARegistry.registerBlock(
            "vending_machine_green", () -> new Block(new BlockExtension(Blocks.createDefaultBlockSettings(true).nonOpaque()))
    );
    public static final BlockRegistryObject RED_VENDING_MACHINE = MTRFRARegistry.registerBlock(
            "vending_machine_red", () -> new Block(new BlockExtension(Blocks.createDefaultBlockSettings(true).nonOpaque()))
    );

    public static final BlockRegistryObject RATP_ALARM_SIGN = MTRFRARegistry.registerBlockWithItem(
            "ratp_alarm_sign", () -> new Block(new HangingSignBlock(Blocks.createDefaultBlockSettings(false))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_DOORCLOSE_SIGN = MTRFRARegistry.registerBlockWithItem(
            "ratp_doorclose_sign", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 3.25, 0, 14, 12.75, 25, 16)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_FIREEXIT_SIGN = MTRFRARegistry.registerBlockWithItem(
            "ratp_fireexit_sign", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 4, 2, 15, 9, 12, 16)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_CURVEDPLATFORM_SIGN = MTRFRARegistry.registerBlockWithItem(
            "ratp_curvedplatform_sign", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 3.25, 0, 14, 12.75, 25, 16)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_NOSMOKING_SIGN = MTRFRARegistry.registerBlockWithItem(
            "ratp_nosmoking_sign", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 4, 0, 15, 12, 16, 16)), ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject RATP_TICKET_BARRIER_SIDE_COVER = MTRFRARegistry.registerBlockWithItem(
            "ratp_ticket_barrier_side_cover", () -> new Block(new RATPTicketBarrierSideCoverBlock()), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_UPPER = MTRFRARegistry.registerBlock(
            "ratp_ticket_barrier_upper", () -> new Block(new RATPTicketBarrierUpperBlock())
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_COLLISION_EXTENSION = MTRFRARegistry.registerBlock(
            "ratp_ticket_barrier_collision_extension", () -> new Block(new RATPTicketBarrierCollisionExtensionBlock())
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_ENTRANCE = MTRFRARegistry.registerBlockWithItem(
            "ratp_ticket_barrier_entrance", () -> new Block(new RATPTicketBarrierBlock(true)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_EXIT = MTRFRARegistry.registerBlockWithItem(
            "ratp_ticket_barrier_exit", () -> new Block(new RATPTicketBarrierBlock(false)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_ENTRANCE_WITH_SIDE = MTRFRARegistry.registerBlockWithItem(
            "ratp_ticket_barrier_entrance_with_side", () -> new Block(new RATPTicketBarrierBlock(true, true)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_EXIT_WITH_SIDE = MTRFRARegistry.registerBlockWithItem(
            "ratp_ticket_barrier_exit_with_side", () -> new Block(new RATPTicketBarrierBlock(false, true)), ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject LOGO_MTRFRANCEADDON = MTRFRARegistry.registerBlock(
            "mtrfranceaddon_logo_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_ACTUEL = MTRFRARegistry.registerBlock(
            "sncf_logo_actuel_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1992_2005 = MTRFRARegistry.registerBlock(
            "sncf_logo_1992-2005_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1985_1992_V1 = MTRFRARegistry.registerBlock(
            "sncf_logo_1985-1992_block_v1", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1985_1992_V2 = MTRFRARegistry.registerBlock(
            "sncf_logo_1985-1992_block_v2", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1967_1985_V1 = MTRFRARegistry.registerBlock(
            "sncf_logo_1967-1985_block_v1", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1967_1985_V2 = MTRFRARegistry.registerBlock(
            "sncf_logo_1967-1985_block_v2", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1947_1967 = MTRFRARegistry.registerBlock(
            "sncf_logo_1947-1967_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_1938_1947 = MTRFRARegistry.registerBlock(
            "sncf_logo_1938-1947_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_RATP_1951_1960 = MTRFRARegistry.registerBlock(
            "ratp_logo_1951-1960_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_RATP_1960_1976 = MTRFRARegistry.registerBlock(
            "ratp_logo_1960-1976_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_RATP_1976_1992 = MTRFRARegistry.registerBlock(
            "ratp_logo_1976-1992_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_RATP_ACTUEL = MTRFRARegistry.registerBlock(
            "ratp_logo_actuel_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );
    public static final BlockRegistryObject LOGO_SNCF_SIGN = MTRFRARegistry.registerBlock(
            "sncf_sign_block", () -> new Block(new LogoBlock(Blocks.createDefaultBlockSettings(false).strength(0.2f)))
    );

    public static final BlockRegistryObject POLE = MTRFRARegistry.registerBlockWithItem(
            "pole", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 7, 0, 7, 9, 16, 9)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject ECLAIRAGE_STATION_PARIS = MTRFRARegistry.registerBlockWithItem(
            "eclairage_station_paris", () -> new Block(new LightFixtureBlock(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, 7, 4, 16, 16, 12)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject MOTTE_LIGHT = MTRFRARegistry.registerBlockWithItem(
            "motte_light", () -> new Block(new MotteLightBlock(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, 0, 4, 16, 15, 12)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject MOTTE_LIGHT_STATION_COLOR = MTRFRARegistry.registerBlockWithItem(
            "motte_light_station_color",
            () -> new Block(new MotteLightStationColorBlock(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, 0, 4, 16, 15, 12)),
            MotteLightStationColorBlockItem::new,
            ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SIGN_SPEED_20 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_20", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_40 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_40", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_60 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_60", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_80 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_80", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_120 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_120", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_160 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_160", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_200 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_200", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_300 = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_300", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_DEPOT = MTRFRARegistry.registerBlockWithItem(
            "sign_depot", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_ONE_WAY_CORRECT = MTRFRARegistry.registerBlockWithItem(
            "sign_one_way_correct", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_ONE_WAY_WRONG = MTRFRARegistry.registerBlockWithItem(
            "sign_one_way_wrong", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_END_OF_TRACK = MTRFRARegistry.registerBlockWithItem(
            "sign_end_of_track", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_LEVEL_CROSSING = MTRFRARegistry.registerBlockWithItem(
            "sign_level_crossing", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SQUARE = MTRFRARegistry.registerBlockWithItem(
            "sign_square", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_STATION = MTRFRARegistry.registerBlockWithItem(
            "sign_station", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_4 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_4", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_6 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_6", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_8 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_8", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_10 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_10", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_12 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_12", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_14 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_14", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_CARS_16 = MTRFRARegistry.registerBlockWithItem(
            "sign_cars_16", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_PLATFORM_LEFT = MTRFRARegistry.registerBlockWithItem(
            "sign_platform_left", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_PLATFORM_RIGHT = MTRFRARegistry.registerBlockWithItem(
            "sign_platform_right", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_NO_TUNNEL_VENT = MTRFRARegistry.registerBlockWithItem(
            "sign_no_tunnel_vent", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_S_CURVE_LEFT = MTRFRARegistry.registerBlockWithItem(
            "sign_s_curve_left", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_S_CURVE_RIGHT = MTRFRARegistry.registerBlockWithItem(
            "sign_s_curve_right", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_TUNNEL = MTRFRARegistry.registerBlockWithItem(
            "sign_tunnel", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_WHISTLE = MTRFRARegistry.registerBlockWithItem(
            "sign_whistle", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_100M = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_100m", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_200M = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_200m", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_300M = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_300m", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_ONE_WAY_CORRECT_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_one_way_correct_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_ONE_WAY_WRONG_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_one_way_wrong_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_20_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_20_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_40_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_40_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_60_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_60_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_80_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_80_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_120_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_120_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_160_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_160_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_200_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_200_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_300_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_300_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_DEPOT_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_depot_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_END_OF_TRACK_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_end_of_track_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_S_CURVE_LEFT_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_s_curve_left_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_S_CURVE_RIGHT_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_s_curve_right_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_LEVEL_CROSSING_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_level_crossing_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SQUARE_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_square_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_STATION_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_station_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_NO_TUNNEL_VENT_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_no_tunnel_vent_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_TUNNEL_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_tunnel_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_WHISTLE_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_whistle_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_100M_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_100m_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_200M_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_200m_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SWITCH_300M_GROUND = MTRFRARegistry.registerBlockWithItem(
            "sign_switch_300m_ground", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject POLE_CONNECTION_LEFT = MTRFRARegistry.registerBlockWithItem(
            "pole_connection_left", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject POLE_CONNECTION_RIGHT = MTRFRARegistry.registerBlockWithItem(
            "pole_connection_right", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject POLE_CONNECTION_HORIZONTAL = MTRFRARegistry.registerBlockWithItem(
            "pole_connection_horizontal", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject POLE_CONNECTION_MIDDLE = MTRFRARegistry.registerBlockWithItem(
            "pole_connection_middle", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_20_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_20_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_40_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_40_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_60_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_60_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_80_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_80_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_120_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_120_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_160_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_160_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_200_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_200_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SPEED_300_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_speed_300_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );
    public static final BlockRegistryObject SIGN_SQUARE_GALLOWS = MTRFRARegistry.registerBlockWithItem(
            "sign_square_gallows", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)), ModItemGroups.SIGNS
    );

    public static final BlockRegistryObject IDFM_SEAT_WITH_POLE = MTRFRARegistry.registerBlock(
            "idfm_seat_with_pole", () -> new Block(new BlockExtension(Blocks.createDefaultBlockSettings(false).nonOpaque()))
    );
    public static final BlockRegistryObject IDFM_SEAT = MTRFRARegistry.registerBlock(
            "idfm_seat", () -> new Block(new BlockExtension(Blocks.createDefaultBlockSettings(false).nonOpaque()))
    );

    private static final MTRFRARegistry.DecoBlockSet BEIGE_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "beige_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject BEIGE_LIMESTONE_PAVING = BEIGE_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject BEIGE_LIMESTONE_PAVING_STAIRS = BEIGE_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject BEIGE_LIMESTONE_PAVING_SLAB = BEIGE_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet GREIGE_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "greige_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject GREIGE_LIMESTONE_PAVING = GREIGE_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject GREIGE_LIMESTONE_PAVING_STAIRS = GREIGE_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject GREIGE_LIMESTONE_PAVING_SLAB = GREIGE_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet GREY_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "grey_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject GREY_LIMESTONE_PAVING = GREY_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject GREY_LIMESTONE_PAVING_STAIRS = GREY_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject GREY_LIMESTONE_PAVING_SLAB = GREY_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet HONEY_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "honey_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject HONEY_LIMESTONE_PAVING = HONEY_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject HONEY_LIMESTONE_PAVING_STAIRS = HONEY_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject HONEY_LIMESTONE_PAVING_SLAB = HONEY_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet IVORY_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "ivory_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject IVORY_LIMESTONE_PAVING = IVORY_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject IVORY_LIMESTONE_PAVING_STAIRS = IVORY_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject IVORY_LIMESTONE_PAVING_SLAB = IVORY_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet PINK_TERRACOTTA_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "pink_terracotta_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject PINK_TERRACOTTA_LIMESTONE_PAVING = PINK_TERRACOTTA_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject PINK_TERRACOTTA_LIMESTONE_PAVING_STAIRS = PINK_TERRACOTTA_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject PINK_TERRACOTTA_LIMESTONE_PAVING_SLAB = PINK_TERRACOTTA_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet SAND_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "sand_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject SAND_LIMESTONE_PAVING = SAND_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject SAND_LIMESTONE_PAVING_STAIRS = SAND_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject SAND_LIMESTONE_PAVING_SLAB = SAND_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet GREEN_TERRACOTTA_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "green_terracotta_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject GREEN_TERRACOTTA_LIMESTONE_PAVING = GREEN_TERRACOTTA_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject GREEN_TERRACOTTA_LIMESTONE_PAVING_STAIRS = GREEN_TERRACOTTA_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject GREEN_TERRACOTTA_LIMESTONE_PAVING_SLAB = GREEN_TERRACOTTA_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet ANTHRACITE_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "anthracite_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject ANTHRACITE_LIMESTONE_PAVING = ANTHRACITE_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject ANTHRACITE_LIMESTONE_PAVING_STAIRS = ANTHRACITE_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject ANTHRACITE_LIMESTONE_PAVING_SLAB = ANTHRACITE_LIMESTONE_PAVING_SET.slab();

    private static final MTRFRARegistry.DecoBlockSet GREY_ANTHRACITE_LIMESTONE_PAVING_SET = MTRFRARegistry.registerDecoBlockSet(
            "grey_anthracite_limestone_paving", () -> Blocks.createDefaultBlockSettings(false), AxisBlock::new, ModItemGroups.BUILDING_MATERIALS
    );
    public static final BlockRegistryObject GREY_ANTHRACITE_LIMESTONE_PAVING = GREY_ANTHRACITE_LIMESTONE_PAVING_SET.full();
    public static final BlockRegistryObject GREY_ANTHRACITE_LIMESTONE_PAVING_STAIRS = GREY_ANTHRACITE_LIMESTONE_PAVING_SET.stairs();
    public static final BlockRegistryObject GREY_ANTHRACITE_LIMESTONE_PAVING_SLAB = GREY_ANTHRACITE_LIMESTONE_PAVING_SET.slab();

    public static final BlockRegistryObject RATP_TUNNEL_ARROW = MTRFRARegistry.registerBlockWithItem(
            "ratp_tunnel_arrow",
            () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, 0, 0, 16, 16, 16)),
            ModItemGroups.STATION_EQUIPMENT
    );

    public static final BlockRegistryObject RATP_TUNNEL_LINE = MTRFRARegistry.registerBlockWithItem(
            "ratp_tunnel_line",
            () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, 0, 0, 16, 16, 16)),
            ModItemGroups.STATION_EQUIPMENT
    );

    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_DOUBLE = {
            new RATPSignBase.TextLayout(8F, 5.0F),
            new RATPSignBase.TextLayout(11.375F, 5.0F),
            new RATPSignBase.TextLayout(8F, 5.75F, 8F, 2.05F),
            new RATPSignBase.TextLayout(11.375F, 5.75F, 8F, 2.05F),
            new RATPSignBase.TextLayout(8F, 5.75F, 8F, 2.05F),
            new RATPSignBase.TextLayout(11.375F, 5.75F, 8F, 2.05F),
            new RATPSignBase.TextLayout(8F, 4.25F, 8F, 7.8F),
            new RATPSignBase.TextLayout(11.375F, 4.25F, 8F, 7.8F)
    };
    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_TOP = {
            new RATPSignBase.TextLayout(8F, 7.0F),
            new RATPSignBase.TextLayout(11.375F, 7.0F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.375F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.375F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 6.25F, 8F, 9.8F),
            new RATPSignBase.TextLayout(11.375F, 6.25F, 8F, 9.8F)
    };
    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_PILLAR = {
            new RATPSignBase.TextLayout(8F, 7.0F),
            new RATPSignBase.TextLayout(11.375F, 7.0F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.5F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.5F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 6.25F, 8F, 9.8F),
            new RATPSignBase.TextLayout(11.5F, 6.25F, 8F, 9.8F)
    };
    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_WALL_RER = {
            new RATPSignBase.TextLayout(8F, 7.0F),
            new RATPSignBase.TextLayout(11.375F, 7.0F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.375F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(11.375F, 7.75F, 8F, 4.05F),
            new RATPSignBase.TextLayout(8F, 6.25F, 8F, 9.8F),
            new RATPSignBase.TextLayout(11.375F, 6.25F, 8F, 9.8F)
    };
    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_WALL_METRO_LARGE = {
            new RATPSignBase.TextLayout(8F, 8.0F),
            new RATPSignBase.TextLayout(8F, 9.375F, 8F, 2.175F),
            new RATPSignBase.TextLayout(8F, 9.375F, 8F, 2.175F),
            new RATPSignBase.TextLayout(8F, 6.625F, 8F, 13.675F)
    };
    private static final RATPSignBase.TextLayout[] TRANSILIEN_SIGN_LAYOUT = {
            new RATPSignBase.TextLayout(8F, 3F)
    };
    private static final RATPSignBase.TextLayout[] RATP_SIGN_LAYOUT_WALL_METRO_SMALL = {
            new RATPSignBase.TextLayout(8F, 8.0F),
            new RATPSignBase.TextLayout(8F, 9.0F, 8F, 3.8F),
            new RATPSignBase.TextLayout(8F, 9.0F, 8F, 3.8F),
            new RATPSignBase.TextLayout(8F, 7.0F, 8F, 12.05F)
    };

    private static final RATPSignBase.TextLayout[] SNCF_SIGN_LAYOUT_DOUBLE = {
            new RATPSignBase.TextLayout(30.0F, 5.0F, 44.0F),
            new RATPSignBase.TextLayout(30.0F, 5.0F, 35.25F)
    };
    private static final RATPSignBase.TextLayout[] SNCF_SIGN_LAYOUT_WALL = {
            new RATPSignBase.TextLayout(29.0F, 5.0F, 42.0F),
            new RATPSignBase.TextLayout(29.0F, 5.0F, 34.0F)
    };

    private static Identifier ratpSignTexture(String path) {
        return new Identifier("mtrfranceaddon:block/panneau_ratp/" + path);
    }

    private static Identifier sncfSignTexture(String path) {
        return new Identifier("mtrfranceaddon:block/panneau_sncf/" + path);
    }

    private static final Identifier[] SNCF_SIGN_TEXTURES_DOUBLE = {
            sncfSignTexture("panneau_sncf"),
            sncfSignTexture("panneau_sncf")
    };
    private static final Identifier[] SNCF_SIGN_LOGO_OVERLAYS_DOUBLE = {
            null,
            sncfSignTexture("logo_sncf")
    };
    private static final float[] PREVIEW_LOGO_OVERLAY_RECT_SNCF = {0.784F, 0.25F, 0.95F, 0.75F};
    private static final Identifier[] SNCF_SIGN_TEXTURES_WALL = {
            sncfSignTexture("panneau_sncf"),
            sncfSignTexture("panneau_sncf")
    };

    private static final Identifier[] RATP_SIGN_TEXTURES_DOUBLE = {
            ratpSignTexture("panneau_ratp_double"),
            ratpSignTexture("panneau_ratp_double_logo"),
            ratpSignTexture("panneau_ratp_double_monument"),
            ratpSignTexture("panneau_ratp_double_logo_monument"),
            ratpSignTexture("panneau_ratp_double_point_interet"),
            ratpSignTexture("panneau_ratp_double_logo_point_interet"),
            ratpSignTexture("panneau_ratp_double_ville"),
            ratpSignTexture("panneau_ratp_double_logo_ville")
    };
    private static final Identifier[] RATP_SIGN_TEXTURES_PILLAR = {
            ratpSignTexture("panneau_ratp_pilier"),
            ratpSignTexture("panneau_ratp_pilier_logo"),
            ratpSignTexture("panneau_ratp_pilier_monument"),
            ratpSignTexture("panneau_ratp_pilier_logo_monument"),
            ratpSignTexture("panneau_ratp_pilier_point_interet"),
            ratpSignTexture("panneau_ratp_pilier_logo_point_interet"),
            ratpSignTexture("panneau_ratp_pilier_ville"),
            ratpSignTexture("panneau_ratp_pilier_logo_ville")
    };
    private static final Identifier[] RATP_SIGN_TEXTURES_WALL_RER = {
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_logo"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_monument"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_logo_monument"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_point_interet"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_logo_point_interet"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_ville"),
            ratpSignTexture("panneau_ratp_mur_rer_panneau_ratp_pilier_logo_ville")
    };
    private static final Identifier[] RATP_SIGN_TEXTURES_TOP = {
            ratpSignTexture("panneau_ratp_top"),
            ratpSignTexture("panneau_ratp_top_logo"),
            ratpSignTexture("panneau_ratp_top_monument"),
            ratpSignTexture("panneau_ratp_top_logo_monument"),
            ratpSignTexture("panneau_ratp_top_point_interet"),
            ratpSignTexture("panneau_ratp_top_logo_point_interet"),
            ratpSignTexture("panneau_ratp_top_ville"),
            ratpSignTexture("panneau_ratp_top_logo_ville")
    };
    private static final Identifier[] RATP_SIGN_TEXTURES_WALL_METRO_LARGE = {
            ratpSignTexture("panneau_ratp_mur_metro_grand"),
            ratpSignTexture("panneau_ratp_mur_metro_grand_point_interet"),
            ratpSignTexture("panneau_ratp_mur_metro_grand_monument"),
            ratpSignTexture("panneau_ratp_mur_metro_grand_ville")
    };
    private static final Identifier[] RATP_SIGN_TEXTURES_WALL_METRO_SMALL = {
            ratpSignTexture("panneau_ratp_mur_metro_petit"),
            ratpSignTexture("panneau_ratp_mur_metro_petit_point_interet"),
            ratpSignTexture("panneau_ratp_mur_metro_petit_monument"),
            ratpSignTexture("panneau_ratp_mur_metro_ville")
    };
    private static final Identifier[] TRANSILIEN_SIGN_TEXTURES = {
            new Identifier("mtrfranceaddon:block/panneau_transilien/panneau_transilien_body")
    };

    private static final float[] PREVIEW_UV_DOUBLE_PILLAR_WALL_RER_TOP = {0F, 0F, 11.5F / 16F, 2F / 16F};
    private static final float[] PREVIEW_UV_WALL_METRO_LARGE = {0F, 0F, 12F / 16F, 3.5F / 16F};
    private static final float[] PREVIEW_UV_WALL_METRO_SMALL = {0F, 0F, 1F, 5F / 16F};
    private static final float[] PREVIEW_UV_TRANSILIEN = {0F, 0F, 6F / 16F, 1.5F / 16F};

    private static final float[] PREVIEW_PLATE_BOUNDS_DOUBLE = {-15F, 1F, 31F, 9F};
    private static final float[] PREVIEW_PLATE_BOUNDS_PILLAR = {-15F, 3F, 31F, 11F};
    private static final float[] PREVIEW_PLATE_BOUNDS_WALL_RER = {-15F, 3F, 31F, 11F};
    private static final float[] PREVIEW_PLATE_BOUNDS_TOP = {-15F, 3F, 31F, 11F};
    private static final float[] PREVIEW_PLATE_BOUNDS_WALL_METRO_LARGE = {-16F, 1F, 32F, 15F};
    private static final float[] PREVIEW_PLATE_BOUNDS_WALL_METRO_SMALL = {-8F, 3F, 24F, 13F};
    private static final float[] PREVIEW_PLATE_BOUNDS_TRANSILIEN = {-4F, 0F, 20F, 6F};
    private static final float[] PREVIEW_UV_SNCF_DOUBLE = {0F, 0F, 1F, 0.5F};
    private static final float[] PREVIEW_UV_SNCF_WALL = {0F, 0F, 6F / 16F, 1.5F / 16F};
    private static final float[] PREVIEW_PLATE_BOUNDS_SNCF_WALL = {-4F, 0F, 20F, 16F};

    public static final BlockRegistryObject RATP_SIGN_DOUBLE = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_double", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 7, RATP_SIGN_LAYOUT_DOUBLE, 6.999F, new double[]{-16, 0, 7, 32, 9, 9}, true, true, 1F, RATP_SIGN_TEXTURES_DOUBLE, PREVIEW_UV_DOUBLE_PILLAR_WALL_RER_TOP, PREVIEW_PLATE_BOUNDS_DOUBLE)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_PILLAR = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_pilier", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 7, RATP_SIGN_LAYOUT_PILLAR, 11.95F, new double[]{-15, 0, 12, 31, 11, 14}, false, true, 0F, RATP_SIGN_TEXTURES_PILLAR, PREVIEW_UV_DOUBLE_PILLAR_WALL_RER_TOP, PREVIEW_PLATE_BOUNDS_PILLAR)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_WALL_RER = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_mur_rer", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 7, RATP_SIGN_LAYOUT_WALL_RER, 13.95F, new double[]{-15, 3, 14, 31, 11, 16}, false, true, 0F, RATP_SIGN_TEXTURES_WALL_RER, PREVIEW_UV_DOUBLE_PILLAR_WALL_RER_TOP, PREVIEW_PLATE_BOUNDS_WALL_RER)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_TOP = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_top", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 7, RATP_SIGN_LAYOUT_TOP, 6.999F, new double[]{-15, 3, 7, 31, 16, 9}, true, true, 0F, RATP_SIGN_TEXTURES_TOP, PREVIEW_UV_DOUBLE_PILLAR_WALL_RER_TOP, PREVIEW_PLATE_BOUNDS_TOP)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_WALL_METRO_LARGE = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_mur_metro_grand", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 3, RATP_SIGN_LAYOUT_WALL_METRO_LARGE, 15.45F, new double[]{-16, 1, 15.5, 32, 15, 16}, false, true, 0F, RATP_SIGN_TEXTURES_WALL_METRO_LARGE, PREVIEW_UV_WALL_METRO_LARGE, PREVIEW_PLATE_BOUNDS_WALL_METRO_LARGE)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_WALL_METRO_SMALL = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_mur_metro_petit", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 3, RATP_SIGN_LAYOUT_WALL_METRO_SMALL, 15.45F, new double[]{-8, 3, 15.5, 24, 13, 16}, false, true, 0F, RATP_SIGN_TEXTURES_WALL_METRO_SMALL, PREVIEW_UV_WALL_METRO_SMALL, PREVIEW_PLATE_BOUNDS_WALL_METRO_SMALL)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_MAP = MTRFRARegistry.registerBlockWithItem(
            "panneau_ratp_plan", () -> new Block(new RATPSignMapBlock(Blocks.createDefaultBlockSettings(false).nonOpaque())), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_PILLAR_POST = MTRFRARegistry.registerBlockWithItem(
            "pilier_ratp", () -> new Block(new RATPPillarPostBlock(Blocks.createDefaultBlockSettings(true))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_PILLAR_POST_BASE = MTRFRARegistry.registerBlockWithItem(
            "pilier_base_ratp", () -> new Block(new RATPPillarPostBaseBlock(Blocks.createDefaultBlockSettings(true))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject TRANSILIEN_SIGN = MTRFRARegistry.registerBlockWithItem(
            "panneau_transilien", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 0, TRANSILIEN_SIGN_LAYOUT, 6.999F, new double[]{-4, 0, 7, 20, 16, 9}, true, false, -0.75F, TRANSILIEN_SIGN_TEXTURES, PREVIEW_UV_TRANSILIEN, PREVIEW_PLATE_BOUNDS_TRANSILIEN)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject TRANSILIEN_POLE = MTRFRARegistry.registerBlockWithItem(
            "pilier_transilien", () -> new Block(new TransilienPoleBlock(Blocks.createDefaultBlockSettings(true))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF_SIGN_DOUBLE = MTRFRARegistry.registerBlockWithItem(
            "panneau_sncf_double", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 1, SNCF_SIGN_LAYOUT_DOUBLE, 7.999F, new double[]{-16, 0, 7, 32, 9, 9}, true, false, 0F, SNCF_SIGN_TEXTURES_DOUBLE, PREVIEW_UV_SNCF_DOUBLE, PREVIEW_PLATE_BOUNDS_DOUBLE, SNCF_SIGN_LOGO_OVERLAYS_DOUBLE, PREVIEW_LOGO_OVERLAY_RECT_SNCF, TextAlignment.LEFT)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF_SIGN_WALL = MTRFRARegistry.registerBlockWithItem(
            "panneau_sncf_mur", () -> new Block(new RATPSignBase(Blocks.createDefaultBlockSettings(false).nonOpaque(), 1, SNCF_SIGN_LAYOUT_WALL, 15.499F, new double[]{-15, 1, 15.5, 31, 9, 16}, false, false, 0F, SNCF_SIGN_TEXTURES_WALL, PREVIEW_UV_SNCF_DOUBLE, PREVIEW_PLATE_BOUNDS_DOUBLE, SNCF_SIGN_LOGO_OVERLAYS_DOUBLE, PREVIEW_LOGO_OVERLAY_RECT_SNCF, TextAlignment.LEFT)), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject SNCF_POLE = MTRFRARegistry.registerBlockWithItem(
            "pilier_sncf", () -> new Block(new SNCFPoleBlock(Blocks.createDefaultBlockSettings(true))), ModItemGroups.STATION_EQUIPMENT
    );
    public static final BlockRegistryObject RATP_SIGN_COLLISION_EXTENSION = MTRFRARegistry.registerBlock(
            "panneau_ratp_collision_extension", () -> new Block(new RATPSignCollisionExtensionBlock())
    );

    public static final BlockRegistryObject IDFM_METRO = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_1 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_1", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_2 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_2", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_3 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_3", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_3BIS = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_3bis", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_4 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_4", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_5 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_5", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_6 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_6", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_7 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_7", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_7BIS = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_7bis", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_8 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_8", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_9 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_9", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_10 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_10", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_11 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_11", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_12 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_12", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_13 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_13", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_14 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_14", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_15 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_15", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_16 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_16", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_17 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_17", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_METRO_18 = MTRFRARegistry.registerBlockWithItem(
            "idfm_metro_18", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER_A = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer_a", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER_B = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer_b", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER_C = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer_c", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER_D = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer_d", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_RER_E = MTRFRARegistry.registerBlockWithItem(
            "idfm_rer_e", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN = MTRFRARegistry.registerBlockWithItem(
            "idfm_train", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_H = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_h", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_J = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_j", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_K = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_k", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_L = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_l", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_N = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_n", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_P = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_p", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_R = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_r", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_U = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_u", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAIN_V = MTRFRARegistry.registerBlockWithItem(
            "idfm_train_v", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T1 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t1", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T2 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t2", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T3 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t3", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T3A = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t3a", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T3B = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t3b", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T4 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t4", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T5 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t5", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T6 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t6", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T7 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t7", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T8 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t8", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T9 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t9", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T10 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t10", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T11 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t11", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T12 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t12", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T13 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t13", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_TRAM_T14 = MTRFRARegistry.registerBlockWithItem(
            "idfm_tram_t14", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_CABLE = MTRFRARegistry.registerBlockWithItem(
            "idfm_cable", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_CABLE_1 = MTRFRARegistry.registerBlockWithItem(
            "idfm_cable_1", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );
    public static final BlockRegistryObject IDFM_BUS = MTRFRARegistry.registerBlockWithItem(
            "idfm_bus", () -> new Block(new ConfigurableSignBlock(Blocks.createDefaultBlockSettings(false, state -> 8), 2, 2, 14, 14, 14, 16)), ModItemGroups.LOGOS
    );

    private ModBlocks() {}

    public static void register() {}

}
