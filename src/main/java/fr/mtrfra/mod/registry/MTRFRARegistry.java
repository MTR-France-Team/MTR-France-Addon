package fr.mtrfra.mod.registry;

import fr.mtrfra.mod.block.base.AxisSlabBlockExtension;
import fr.mtrfra.mod.tab.CreativeTabSection;
import fr.mtrfra.mod.tab.CreativeTabSections;
import fr.mtrfra.mod.util.Constants;
import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.Blocks;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.mapper.StairsBlockExtension;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.EntityTypeRegistryObject;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.registry.Registry;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MTRFRARegistry {

    public static final Registry REGISTRY = new Registry();

    private MTRFRARegistry() {}

    public static void register() {
        setupPackets();
        ModItemGroups.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModItems.register();
        ModEvents.register();
        ModNetworking.register();

        REGISTRY.init();
    }

    public static void setupPackets() {
        REGISTRY.setupPackets(Constants.id("packet"));
    }

    public static BlockRegistryObject registerBlockWithItem(String id, Supplier<Block> supplier, CreativeTabSection section) {
        final BlockRegistryObject registered = REGISTRY.registerBlockWithBlockItem(Constants.id(id), supplier, ModItemGroups.tabFor(section));
        CreativeTabSections.recordBlock(registered, section);
        return registered;
    }

    public static BlockRegistryObject registerBlockWithItem(String id, Supplier<Block> supplier, BiFunction<Block, ItemSettings, BlockItemExtension> itemFactory, CreativeTabSection section) {
        final BlockRegistryObject registered = REGISTRY.registerBlockWithBlockItem(Constants.id(id), supplier, itemFactory, ModItemGroups.tabFor(section));
        CreativeTabSections.recordBlock(registered, section);
        return registered;
    }

    public static BlockRegistryObject registerBlock(String id, Supplier<Block> supplier) {
        return REGISTRY.registerBlock(Constants.id(id), supplier);
    }

    public static ItemRegistryObject registerItem(String id, Function<ItemSettings, Item> callback, CreativeTabSection section) {
        final ItemRegistryObject registered = REGISTRY.registerItem(Constants.id(id), callback, ModItemGroups.tabFor(section));
        CreativeTabSections.recordItem(registered, section);
        return registered;
    }

    public static BlockRegistryObject registerBlockWithItemHidden(String id, Supplier<Block> supplier) {
        return REGISTRY.registerBlockWithBlockItem(Constants.id(id), supplier);
    }

    public static ItemRegistryObject registerItemHidden(String id, Function<ItemSettings, Item> callback) {
        return REGISTRY.registerItem(Constants.id(id), callback);
    }

    public static <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntity(String id, BiFunction<EntityType<?>, World, T> factory, float width, float height) {
        return (EntityTypeRegistryObject<T>) REGISTRY.registerEntityType(Constants.id(id), (type, world) -> factory.apply(new EntityType<>(type.data), world), width, height);
    }

    public record DecoBlockSet(BlockRegistryObject full, BlockRegistryObject stairs, BlockRegistryObject slab) {}

    public static DecoBlockSet registerDecoBlockSet(String id, Supplier<BlockSettings> settingsSupplier, CreativeTabSection section) {
        return registerDecoBlockSet(id, settingsSupplier, BlockExtension::new, section);
    }

    public static DecoBlockSet registerDecoBlockSet(String id, Supplier<BlockSettings> settingsSupplier, Function<BlockSettings, ? extends BlockExtension> fullBlockFactory, CreativeTabSection section) {
        final BlockRegistryObject full = registerBlockWithItem(id, () -> new Block(fullBlockFactory.apply(settingsSupplier.get())), section);
        final BlockRegistryObject stairs = registerBlockWithItem(id + "_stairs", () -> new Block(new StairsBlockExtension(Blocks.getStoneMapped().getDefaultState(), settingsSupplier.get())), section);
        final BlockRegistryObject slab = registerBlockWithItem(id + "_slab", () -> new Block(new AxisSlabBlockExtension(settingsSupplier.get())), section);
        return new DecoBlockSet(full, stairs, slab);
    }

}
