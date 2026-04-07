package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.entity.SeatEntity;
import fr.mtrfranceaddon.mod.common.util.Constants;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.registry.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MTRFranceAddonRegistry {

    public static final Registry REGISTRY = new Registry();

    public static void register() {
        setupPacket();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModEvents.register();
        ModItems.register();
        ModNetworking.register();

        // Initialize the registry.
        REGISTRY.init();
    }

    public static void setupPacket() {
        REGISTRY.setupPackets(Constants.id("packet"));
    }

    /**
     * Register a simple block with an associated item.
     * @param id Identifier of the block (ex: "example_block")
     * @param supplier The constructor of the block (ex: () -> new Block(BlockSettings.of()))
     * @param itemGroup The Creative tab where the item will appear
     * @return The block's object registered.
     */
    public static BlockRegistryObject registerBlockWithItem(String id, Supplier<Block> supplier, CreativeModeTabHolder itemGroup) {
        return REGISTRY.registerBlockWithBlockItem(Constants.id(id), supplier, itemGroup);
    }

    /**
     * Register a simple item.
     * @param id Identifier of the item (ex: "example_item")
     * @param callback The constructor of the item
     * @param itemGroup The creative tab where the item will appear
     * @return The item's object registered.
     */
    public static ItemRegistryObject registerItem(String id, Function<ItemSettings, Item> callback, CreativeModeTabHolder itemGroup) {
        return REGISTRY.registerItem(Constants.id(id), callback, itemGroup);
    }

    public static <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntity(String id, BiFunction<EntityType<?>, World, T> factory, float width, float height) {
        return (EntityTypeRegistryObject<T>) REGISTRY.registerEntityType(
                Constants.id(id),
                (type, world) -> factory.apply(new EntityType<>(type.data), world),
                width, height
        );
    }

}
