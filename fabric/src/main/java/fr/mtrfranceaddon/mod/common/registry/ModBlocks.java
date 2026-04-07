package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.Init;
import fr.mtrfranceaddon.mod.common.block.*;
import fr.mtrfranceaddon.mod.common.block.ConfigurableSignBlock;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockPlatform;

import java.util.HashMap;
import java.util.Map;

import static org.mtr.mod.Blocks.createDefaultBlockSettings;

public final class ModBlocks {

    public static Map<String, BlockRegistryObject> LOGO_BLOCKS = new HashMap<>();

    // Dim : 0, 0, 15, 16, 16, 16
    public static final BlockRegistryObject MTRFRANCEADDON_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "mtrfranceaddon_logo_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_ACTUAL_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_actuel_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1992TO2005_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1992-2005_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1985TO1992_LOGO_V1 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1985-1992_block_v1",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1985TO1992_LOGO_V2 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1985-1992_block_v2",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1967TO1985_LOGO_V1 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1967-1985_block_v1",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1967TO1985_LOGO_V2 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1967-1985_block_v2",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1947TO1967_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1947-1967_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1938TO1947_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1938-1947_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1951TO1960_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1951-1960_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1960TO1976_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1960-1976_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1976TO1992_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1976-1992_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_ACTUAL_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_actuel_block",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false).strength(0.2f), 0, 0, 15, 16, 16, 16)),
            ModItemGroups.MTRFranceAddonGroup
    );

    //platforms
    public static final BlockRegistryObject INVISIBLE_PLATFORM = MTRFranceAddonRegistry.registerBlockWithItem(
            "invisible_platform",
            () -> new Block(new InvisiblePlatform(createDefaultBlockSettings(false).strength(-1.0F, 3600000.0F).dropsNothing())), ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_PLATFORM = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_platform",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), false)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_PLATFORM = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_platform",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), false)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF2_PLATFORM = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf2_platform",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), false)),
            ModItemGroups.MTRFranceAddonGroup
    );
    //platforms Indented
    public static final BlockRegistryObject RATP_PLATFORM_INDENTED = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_platform_indented",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), true)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_PLATFORM_INDENTED = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_platform_indented",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), true)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF2_PLATFORM_INDENTED = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf2_platform_indented",
            () -> new Block(new BlockPlatform(createDefaultBlockSettings(false), true)),
            ModItemGroups.MTRFranceAddonGroup
    );

    //Ticket Barrier Block
    public static final BlockRegistryObject RATP_TICKET_BARRIER_SIDE_COVER = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_ticket_barrier_side_cover",
            () -> new Block(new RATPTicketBarrierSideCoverBlock()),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_ENTRANCE = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_ticket_barrier_entrance",
            () -> new Block(new RATPTicketBarrierBlock(true)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_EXIT = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_ticket_barrier_exit",
            () -> new Block(new RATPTicketBarrierBlock(false)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_ENTRANCE_WITH_SIDE = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_ticket_barrier_entrance_with_side",
            () -> new Block(new RATPTicketBarrierBlock(true)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_TICKET_BARRIER_EXIT_WITH_SIDE = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_ticket_barrier_exit_with_side",
            () -> new Block(new RATPTicketBarrierBlock(false)),
            ModItemGroups.MTRFranceAddonGroup
    );

    // MTR France Sign Addon
    // Dim : 7, 0, 7, 9, 16, 9
    public static final BlockRegistryObject POLE = MTRFranceAddonRegistry.registerBlockWithItem(
            "pole",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 7, 0, 7, 9, 16, 9)),
            ModItemGroups.MTRSignAddonGroup
    );

    // Dim  : 5, 0, 5, 11, 16, 11
    public static final BlockRegistryObject TWENTYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twentysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject FOURTYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "fourtysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SIXTYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "sixtysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HEITYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "heitysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDTWENTYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredtwentysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDSIXTYSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredsixtysignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWOHUNDREDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twohundredsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject THREEHUNDREDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "threehundredsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject DEPOTSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "depotsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject DOWNSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "downsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject UPSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "upsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject ENDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "endsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject LEVELCROSSINGSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "levelcrossingsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SQUARESIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "squaresignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject STATIONSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "stationsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject FOURCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "fourcarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SIXCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "sixcarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HEIGHTCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "heightcarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TENCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "tencarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWELVCARCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twelvcarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject FOURTEENCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "fourteencarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SIXTEENCARSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "sixteencarsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject PLATEFORMELSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "plateformelsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject PLATEFORMERSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "plateformersignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject NOVENTSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "noventsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject LEFTSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "leftssignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject RIGHTSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "rightssignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TUNNELSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "tunnelsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject WHEEZINGSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "wheezingsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHOHSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchohsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHTHSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchthsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHTHHSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchthhsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 5, 0, 5, 11, 16, 11)),
            ModItemGroups.MTRSignAddonGroup
    );

    // Dim : 1, 0, 1, 14, 7, 14
    public static final BlockRegistryObject DOUWNGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "downgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject UPGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "upgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWENTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twentygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject FOURTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "fourtygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SIXTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "sixtygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HEIGHTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "heightygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDTWENTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredtwentygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDSIXTYGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredsixtygroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWOHUNDREDGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twohundredgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject THREEHUNDREDGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "threehundredgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject DEPOTGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "depotgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject ENDGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "endgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject LEFTSGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "leftsgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject RIGHTSGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "rightsgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject LEVELCROSSINGGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "levelcrossinggroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SQUAREGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "squaregroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject STATIONGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "stationgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject NOVENTGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "noventgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TUNNELGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "tunnelgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject WHEEZINGGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "wheezinggroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHOHGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchohgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHTHGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchthgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SWITCHTHHGROUNDSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "switchthhgroundsignblock",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 0, 1, 14, 7, 14)),
            ModItemGroups.MTRSignAddonGroup
    );

    // Dim : 1, 1, 1, 15, 15, 15
    public static final BlockRegistryObject POLELEFTCONNEXIONSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "poleleftconnection",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject POLERIGHTCONNEXIONSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "polerightconnection",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject POLEHORIZONTALCONNEXIONSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "polehorizontalconnection",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject POLEMIDDLECONNEXIONSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "polemiddleconnection",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWENTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twentygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject FOURTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "fourtygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SIXTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "sixtygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HEIGHTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "heightygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDTWENTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredtwentygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject HUNDREDSIXTYGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "hundredsixtygallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject TWOHUNDREDGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "twohundredgallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject THREEHUNDREDGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "threehundredgallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );
    public static final BlockRegistryObject SQUAREGALLOWSSIGNBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "squaregallows",
            () -> new Block(new ConfigurableSignBlock(createDefaultBlockSettings(false), 1, 1, 1, 15, 15, 15)),
            ModItemGroups.MTRSignAddonGroup
    );

    // Seat
    // Dim : 0, 0, 0, 16, 17, 11
    public static final BlockRegistryObject IDFMSEATWITHPOLE = MTRFranceAddonRegistry.registerBlockWithItem(
            "idfm_seat_with_pole",
            () -> new Block(new SeatBlock(createDefaultBlockSettings(false).nonOpaque(), 0, 0, 0, 16, 17, 16, 0.0)),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject IDFMSEAT = MTRFranceAddonRegistry.registerBlockWithItem(
            "idfm_seat",
            () -> new Block(new SeatBlock(createDefaultBlockSettings(false).nonOpaque(), 0, 0, 0, 16, 17, 16, 0.0)),
            ModItemGroups.MTRFranceAddonGroup
    );

    // Ticket Machine
    public static final BlockRegistryObject IDFTICKETMACHINEBLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "idfm_ticket_machine",
            () -> new Block(new IDFMTicketMachineBlock()),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject IDFTICKETMACHINE2BLOCK = MTRFranceAddonRegistry.registerBlockWithItem(
            "idfm_ticket_machine_2",
            () -> new Block(new IDFMTicketMachine2Block()),
            ModItemGroups.MTRFranceAddonGroup
    );

    public static void register() {
        Init.LOGGER.info("Registering Blocks");

        LOGO_BLOCKS.put("mtrfranceaddon_logo_block", MTRFRANCEADDON_LOGO);
        LOGO_BLOCKS.put("sncf_logo_actuel_block", SNCF_ACTUAL_LOGO);
        LOGO_BLOCKS.put("sncf_logo_1992-2005_block", SNCF_1992TO2005_LOGO);
        LOGO_BLOCKS.put("sncf_logo_1985-1992_block_v1", SNCF_1985TO1992_LOGO_V1);
        LOGO_BLOCKS.put("sncf_logo_1985-1992_block_v2", SNCF_1985TO1992_LOGO_V2);
        LOGO_BLOCKS.put("sncf_logo_1967-1985_block_v1", SNCF_1967TO1985_LOGO_V1);
        LOGO_BLOCKS.put("sncf_logo_1967-1985_block_v2", SNCF_1967TO1985_LOGO_V2);
        LOGO_BLOCKS.put("sncf_logo_1947-1967_block", SNCF_1947TO1967_LOGO);
        LOGO_BLOCKS.put("sncf_logo_1938-1947_block", SNCF_1938TO1947_LOGO);
        LOGO_BLOCKS.put("ratp_logo_1951-1960_block", RATP_1951TO1960_LOGO);
        LOGO_BLOCKS.put("ratp_logo_1960-1976_block", RATP_1960TO1976_LOGO);
        LOGO_BLOCKS.put("ratp_logo_1976-1992_block", RATP_1976TO1992_LOGO);
        LOGO_BLOCKS.put("ratp_logo_actuel_block", RATP_ACTUAL_LOGO);
    }

    public static void registerClient() {}

}
