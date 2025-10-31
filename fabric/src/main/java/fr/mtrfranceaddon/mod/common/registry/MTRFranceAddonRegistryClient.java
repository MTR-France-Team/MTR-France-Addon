package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.util.Constants;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mapping.registry.RegistryClient;
import org.mtr.mod.item.ItemBlockClickingBase;

public class MTRFranceAddonRegistryClient {

    public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(MTRFranceAddonRegistry.REGISTRY);

    public static void register() {
        setupPacketClient();
        ModBlockEntityRenderers.registerClient();
        ModEvents.registerClient();
        ModNetworking.registerClient();

        // Predicate "mtr:selected"
        for (ItemRegistryObject item : ModItems.RAIL_CONNECTORS.values()) {
            REGISTRY_CLIENT.registerItemModelPredicate(item, new Identifier(Constants.MTR_MOD_ID, "selected"), checkItemPredicateTag());
        }

        for (BlockRegistryObject block : ModBlocks.LOGO_BLOCKS.values()) {
            REGISTRY_CLIENT.registerBlockRenderType(RenderLayer.getCutout(), block);
        }

        // Initialize the client registry
        REGISTRY_CLIENT.init();
    }

    public static void setupPacketClient() {
        REGISTRY_CLIENT.setupPackets(Constants.id("packet"));
    }

    // Taken from MTR
    private static RegistryClient.ModelPredicateProvider checkItemPredicateTag() {
        return (itemStack, clientWorld, livingEntity) -> itemStack.getOrCreateTag().contains(ItemBlockClickingBase.TAG_POS) ? 1 : 0;
    }

}
