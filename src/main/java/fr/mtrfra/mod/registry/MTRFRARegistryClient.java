package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.util.Constants;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mapping.registry.RegistryClient;
import org.mtr.mod.InitClient;
import org.mtr.mod.item.ItemBlockClickingBase;
import fr.mtrfra.mod.block.copycat.CopycatBlockBase;
import fr.mtrfra.mod.block.sign.MotteLightBlock;
import fr.mtrfra.mod.render.CushionEntityRenderer;
import fr.mtrfra.mod.item.CopycatLayerBlockItem;
//? if fabric {
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//? }
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public final class MTRFRARegistryClient {

    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(MTRFRARegistry.REGISTRY);

    private MTRFRARegistryClient() {}

    public static void register() {
        setupPacketsClient();
        ModBlockEntityRenderers.registerClient();
        ModEntityRenderers.registerClient();
        ModEvents.registerClient();
        ModNetworking.registerClient();

        for (ItemRegistryObject item : ModItems.RAIL_CONNECTORS.values()) {
            REGISTRY_CLIENT.registerItemModelPredicate(item, new Identifier(Constants.MTR_MOD_ID, "selected"), checkItemPredicateTag());
        }

        REGISTRY_CLIENT.registerBlockColors((blockState, world, pos, tintIndex) ->
                        tintIndex == 0 ? CushionEntityRenderer.currentTintColor : 0xFFFFFF, ModBlocks.IDFM_SEAT, ModBlocks.IDFM_SEAT_WITH_POLE);

        REGISTRY_CLIENT.registerBlockColors((blockState, world, pos, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFF;
                    }

                    final BlockEntity entity = world.getBlockEntity(pos);
                    return entity != null && entity.data instanceof MotteLightBlock.BlockEntity motteLightEntity ? motteLightEntity.getColor() : 0xFFFFFF;
                },
                ModBlocks.MOTTE_LIGHT);

        REGISTRY_CLIENT.registerBlockColors((blockState, world, pos, tintIndex) ->
                        tintIndex == 0 ? InitClient.getStationColor(pos) : 0xFFFFFF, ModBlocks.MOTTE_LIGHT_STATION_COLOR);

        for (BlockRegistryObject block : new BlockRegistryObject[]{
                ModBlocks.LOGO_MTRFRANCEADDON, ModBlocks.LOGO_SNCF_ACTUEL, ModBlocks.LOGO_SNCF_1992_2005,
                ModBlocks.LOGO_SNCF_1985_1992_V1, ModBlocks.LOGO_SNCF_1985_1992_V2, ModBlocks.LOGO_SNCF_1967_1985_V1,
                ModBlocks.LOGO_SNCF_1967_1985_V2, ModBlocks.LOGO_SNCF_1947_1967, ModBlocks.LOGO_SNCF_1938_1947,
                ModBlocks.LOGO_RATP_1951_1960, ModBlocks.LOGO_RATP_1960_1976, ModBlocks.LOGO_RATP_1976_1992, ModBlocks.LOGO_RATP_ACTUEL,
                ModBlocks.LOGO_SNCF_SIGN,
                ModBlocks.SNCF_BUFFER,
                ModBlocks.RATP_ALARM_SIGN, ModBlocks.RATP_DOORCLOSE_SIGN, ModBlocks.RATP_FIREEXIT_SIGN,
                ModBlocks.RATP_CURVEDPLATFORM_SIGN, ModBlocks.RATP_NOSMOKING_SIGN,
                ModBlocks.POLE, ModBlocks.SIGN_SPEED_20, ModBlocks.SIGN_SPEED_40, ModBlocks.SIGN_SPEED_60,
                ModBlocks.SIGN_SPEED_80, ModBlocks.SIGN_SPEED_120, ModBlocks.SIGN_SPEED_160,
                ModBlocks.SIGN_SPEED_200, ModBlocks.SIGN_SPEED_300, ModBlocks.SIGN_DEPOT,
                ModBlocks.SIGN_ONE_WAY_CORRECT, ModBlocks.SIGN_ONE_WAY_WRONG, ModBlocks.SIGN_END_OF_TRACK, ModBlocks.SIGN_LEVEL_CROSSING,
                ModBlocks.SIGN_SQUARE, ModBlocks.SIGN_STATION, ModBlocks.SIGN_CARS_4, ModBlocks.SIGN_CARS_6,
                ModBlocks.SIGN_CARS_8, ModBlocks.SIGN_CARS_10, ModBlocks.SIGN_CARS_12, ModBlocks.SIGN_CARS_14,
                ModBlocks.SIGN_CARS_16, ModBlocks.SIGN_PLATFORM_LEFT, ModBlocks.SIGN_PLATFORM_RIGHT, ModBlocks.SIGN_NO_TUNNEL_VENT,
                ModBlocks.SIGN_S_CURVE_LEFT, ModBlocks.SIGN_S_CURVE_RIGHT, ModBlocks.SIGN_TUNNEL, ModBlocks.SIGN_WHISTLE,
                ModBlocks.SIGN_SWITCH_100M, ModBlocks.SIGN_SWITCH_200M, ModBlocks.SIGN_SWITCH_300M,
                ModBlocks.SIGN_ONE_WAY_CORRECT_GROUND, ModBlocks.SIGN_ONE_WAY_WRONG_GROUND, ModBlocks.SIGN_SPEED_20_GROUND,
                ModBlocks.SIGN_SPEED_40_GROUND, ModBlocks.SIGN_SPEED_60_GROUND, ModBlocks.SIGN_SPEED_80_GROUND,
                ModBlocks.SIGN_SPEED_120_GROUND, ModBlocks.SIGN_SPEED_160_GROUND, ModBlocks.SIGN_SPEED_200_GROUND,
                ModBlocks.SIGN_SPEED_300_GROUND, ModBlocks.SIGN_DEPOT_GROUND, ModBlocks.SIGN_END_OF_TRACK_GROUND,
                ModBlocks.SIGN_S_CURVE_LEFT_GROUND, ModBlocks.SIGN_S_CURVE_RIGHT_GROUND, ModBlocks.SIGN_LEVEL_CROSSING_GROUND,
                ModBlocks.SIGN_SQUARE_GROUND, ModBlocks.SIGN_STATION_GROUND, ModBlocks.SIGN_NO_TUNNEL_VENT_GROUND,
                ModBlocks.SIGN_TUNNEL_GROUND, ModBlocks.SIGN_WHISTLE_GROUND, ModBlocks.SIGN_SWITCH_100M_GROUND,
                ModBlocks.SIGN_SWITCH_200M_GROUND, ModBlocks.SIGN_SWITCH_300M_GROUND,
                ModBlocks.POLE_CONNECTION_LEFT, ModBlocks.POLE_CONNECTION_RIGHT, ModBlocks.POLE_CONNECTION_HORIZONTAL, ModBlocks.POLE_CONNECTION_MIDDLE,
                ModBlocks.SIGN_SPEED_20_GALLOWS, ModBlocks.SIGN_SPEED_40_GALLOWS, ModBlocks.SIGN_SPEED_60_GALLOWS, ModBlocks.SIGN_SPEED_80_GALLOWS,
                ModBlocks.SIGN_SPEED_120_GALLOWS, ModBlocks.SIGN_SPEED_160_GALLOWS, ModBlocks.SIGN_SPEED_200_GALLOWS,
                ModBlocks.SIGN_SPEED_300_GALLOWS, ModBlocks.SIGN_SQUARE_GALLOWS,
                ModBlocks.GREEN_VENDING_MACHINE, ModBlocks.RED_VENDING_MACHINE,
                ModBlocks.RATP_SIGN_DOUBLE, ModBlocks.RATP_SIGN_PILLAR, ModBlocks.RATP_SIGN_WALL_RER, ModBlocks.RATP_SIGN_TOP,
                ModBlocks.RATP_SIGN_WALL_METRO_LARGE, ModBlocks.RATP_SIGN_WALL_METRO_SMALL, ModBlocks.RATP_SIGN_MAP, ModBlocks.SNCF_SIGN_DOUBLE, ModBlocks.SNCF_SIGN_WALL,
                ModBlocks.RATP_PILLAR_POST, ModBlocks.RATP_PILLAR_POST_BASE,
                ModBlocks.IDFM_METRO, ModBlocks.IDFM_METRO_1, ModBlocks.IDFM_METRO_2, ModBlocks.IDFM_METRO_3,
                ModBlocks.IDFM_METRO_3BIS, ModBlocks.IDFM_METRO_4, ModBlocks.IDFM_METRO_5, ModBlocks.IDFM_METRO_6,
                ModBlocks.IDFM_METRO_7, ModBlocks.IDFM_METRO_7BIS, ModBlocks.IDFM_METRO_8, ModBlocks.IDFM_METRO_9,
                ModBlocks.IDFM_METRO_10, ModBlocks.IDFM_METRO_11, ModBlocks.IDFM_METRO_12, ModBlocks.IDFM_METRO_13,
                ModBlocks.IDFM_METRO_14, ModBlocks.IDFM_METRO_15, ModBlocks.IDFM_METRO_16, ModBlocks.IDFM_METRO_17,
                ModBlocks.IDFM_METRO_18,
                ModBlocks.IDFM_RER, ModBlocks.IDFM_RER_A, ModBlocks.IDFM_RER_B, ModBlocks.IDFM_RER_C,
                ModBlocks.IDFM_RER_D, ModBlocks.IDFM_RER_E,
                ModBlocks.IDFM_TRAIN, ModBlocks.IDFM_TRAIN_H, ModBlocks.IDFM_TRAIN_J, ModBlocks.IDFM_TRAIN_K,
                ModBlocks.IDFM_TRAIN_L, ModBlocks.IDFM_TRAIN_N, ModBlocks.IDFM_TRAIN_P, ModBlocks.IDFM_TRAIN_R,
                ModBlocks.IDFM_TRAIN_U, ModBlocks.IDFM_TRAIN_V,
                ModBlocks.IDFM_TRAM, ModBlocks.IDFM_TRAM_T1, ModBlocks.IDFM_TRAM_T2, ModBlocks.IDFM_TRAM_T3,
                ModBlocks.IDFM_TRAM_T3A, ModBlocks.IDFM_TRAM_T3B, ModBlocks.IDFM_TRAM_T4, ModBlocks.IDFM_TRAM_T5,
                ModBlocks.IDFM_TRAM_T6, ModBlocks.IDFM_TRAM_T7, ModBlocks.IDFM_TRAM_T8, ModBlocks.IDFM_TRAM_T9,
                ModBlocks.IDFM_TRAM_T10, ModBlocks.IDFM_TRAM_T11, ModBlocks.IDFM_TRAM_T12, ModBlocks.IDFM_TRAM_T13,
                ModBlocks.IDFM_TRAM_T14,
                ModBlocks.IDFM_CABLE, ModBlocks.IDFM_CABLE_1, ModBlocks.IDFM_BUS
        }) {
            REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), block);
        }

        registerCopycatItemPreview(ModBlocks.COPYCAT_LAYER);
        registerCopycatItemPreview(ModBlocks.COPYCAT_PLATFORM);
        registerCopycatItemPreview(ModBlocks.COPYCAT_LAYER_PLATFORM);

        REGISTRY_CLIENT.init();
    }

    private static void registerCopycatItemPreview(BlockRegistryObject copycatBlock) {
        //? if fabric {
        final Item item = (Item) copycatBlock.get().asItem().data;
        BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, poseStack, buffers, light, overlay) -> {
            final Minecraft minecraft = Minecraft.getInstance();
            final CompoundTag tag = stack.getTag();
            final String mimicId = tag != null && tag.contains(CopycatLayerBlockItem.KEY_MIMIC) ? tag.getString(CopycatLayerBlockItem.KEY_MIMIC) : null;
            final BlockState mimicState = mimicId == null ? null : CopycatBlockBase.BlockEntity.idToMimic(mimicId);
            final BlockState previewState = mimicState != null ? mimicState : (BlockState) ModBlocks.COPYCAT_LAYER_PLACEHOLDER.get().getDefaultState().data;
            final BakedModel bakedModel = minecraft.getBlockRenderer().getBlockModel(previewState);

            minecraft.getItemRenderer().render(stack, mode, false, poseStack, buffers, light, overlay, bakedModel);
        });
        //? }
    }

    public static void setupPacketsClient() {
        REGISTRY_CLIENT.setupPackets(Constants.id("packet"));
    }

    private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
        return (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0;
    }

}
