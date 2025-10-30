package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.Init;
import fr.mtrfranceaddon.mod.common.block.InvisiblePlatform;
import fr.mtrfranceaddon.mod.common.block.LogoBlock;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mod.block.BlockPlatform;

import java.util.HashMap;
import java.util.Map;

import static org.mtr.mod.Blocks.createDefaultBlockSettings;

public final class ModBlocks {

    public static Map<String, BlockRegistryObject> LOGO_BLOCKS = new HashMap<>();

    public static final BlockRegistryObject MTRFRANCEADDON_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "mtrfranceaddon_logo_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_ACTUAL_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_actuel_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1992TO2005_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1992-2005_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1985TO1992_LOGO_V1 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1985-1992_block_v1",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1985TO1992_LOGO_V2 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1985-1992_block_v2",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1967TO1985_LOGO_V1 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1967-1985_block_v1",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1967TO1985_LOGO_V2 = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1967-1985_block_v2",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1947TO1967_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1947-1967_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject SNCF_1938TO1947_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "sncf_logo_1938-1947_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1951TO1960_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1951-1960_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1960TO1976_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1960-1976_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_1976TO1992_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_1976-1992_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
            ModItemGroups.MTRFranceAddonGroup
    );
    public static final BlockRegistryObject RATP_ACTUAL_LOGO = MTRFranceAddonRegistry.registerBlockWithItem(
            "ratp_logo_actuel_block",
            () -> new Block(new LogoBlock(createDefaultBlockSettings(false).strength(0.2f))),
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
