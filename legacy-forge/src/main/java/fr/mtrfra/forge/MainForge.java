package fr.mtrfra.forge;

import fr.mtrfra.mod.Init;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.function.Consumer;

@Mod(Constants.MOD_ID)
public class MainForge {

    public MainForge() {
        Init.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Init::initClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MainForge::addPackFinders);
    }

    private static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        final Path modFile = ModList.get().getModFileById(Constants.MOD_ID).getFile().getFilePath();

        final RepositorySource repositorySource = (RepositorySource) Proxy.newProxyInstance(
                RepositorySource.class.getClassLoader(),
                new Class<?>[]{RepositorySource.class},
                (proxy, method, args) -> {

                    if (!method.getName().equals("loadPacks")) {
                        switch (method.getName()) {
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return proxy == args[0];
                            case "toString":
                                return "MTRFRARepositorySource";
                            default:
                                return null;
                        }
                    }

                    @SuppressWarnings("unchecked")
                    final Consumer<Pack> consumer = (Consumer<Pack>) args[0];

                    final Object packConstructor = args.length > 1 ? args[1] : null;

                    try {
                        consumer.accept(createPack(modFile, packConstructor));
                    } catch (ReflectiveOperationException exception) {
                        throw new IllegalStateException(
                                "Failed to register the MTR France Addon built-in resource pack",
                                exception
                        );
                    }

                    return null;
                }
        );

        event.addRepositorySource(repositorySource);
    }

    private static Pack createPack(Path modFile, Object packConstructor)
            throws ReflectiveOperationException {

        final String id = Constants.MOD_ID + "_resources";
        final Component title = literalComponent(
                Constants.MOD_NAME + " built-in resources"
        );

        for (Method method : Pack.class.getMethods()) {
            if (method.getName().equals("readMetaAndCreate")
                    && method.getParameterCount() == 7) {

                final Object resourcesSupplier =
                        createResourcesSupplier(
                                method.getParameterTypes()[3],
                                modFile,
                                id
                        );

                return (Pack) method.invoke(
                        null,
                        id,
                        title,
                        true,
                        resourcesSupplier,
                        PackType.CLIENT_RESOURCES,
                        Pack.Position.BOTTOM,
                        PackSource.BUILT_IN
                );
            }
        }

        for (Method method : Pack.class.getMethods()) {
            if (method.getName().equals("create")
                    && method.getParameterCount() == 6) {

                final Object resourcesSupplier =
                        createResourcesSupplier(
                                method.getParameterTypes()[2],
                                modFile,
                                id
                        );

                return (Pack) method.invoke(
                        null,
                        id,
                        true,
                        resourcesSupplier,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.BUILT_IN
                );
            }
        }

        throw new IllegalStateException(
                "Unsupported Forge version: no matching Pack#readMetaAndCreate or Pack#create overload found"
        );
    }

    private static Component literalComponent(String text)
            throws ReflectiveOperationException {

        try {
            return (Component) Component.class
                    .getMethod("literal", String.class)
                    .invoke(null, text);

        } catch (NoSuchMethodException noLiteralFactory) {

            final Class<?> textComponentClass =
                    Class.forName(
                            "net.minecraft.network.chat.TextComponent"
                    );

            return (Component) textComponentClass
                    .getConstructor(String.class)
                    .newInstance(text);
        }
    }

    private static Object createResourcesSupplier(
            Class<?> supplierType,
            Path modFile,
            String id
    ) throws ReflectiveOperationException {

        try {
            final Class<?> pathSupplierClass =
                    Class.forName(
                            "net.minecraft.server.packs.PathPackResources$PathResourcesSupplier"
                    );

            if (supplierType.isAssignableFrom(pathSupplierClass)) {
                return pathSupplierClass
                        .getConstructor(Path.class, boolean.class)
                        .newInstance(modFile, true);
            }

        } catch (ClassNotFoundException noPathResourcesSupplier) {}

        final Object packResources;

        try {
            final Class<?> pathPackClass =
                    Class.forName(
                            "net.minecraft.server.packs.PathPackResources"
                    );

            packResources = pathPackClass
                    .getConstructor(
                            String.class,
                            Path.class,
                            boolean.class
                    )
                    .newInstance(id, modFile, true);

        } catch (ClassNotFoundException noPathPackResources) {

            final Class<?> folderPackClass =
                    Class.forName(
                            "net.minecraft.server.packs.FolderPackResources"
                    );

            final Object folderPackResources =
                    folderPackClass
                            .getConstructor(File.class)
                            .newInstance(modFile.toFile());

            return wrapAsProxy(
                    supplierType,
                    folderPackResources
            );
        }

        return wrapAsProxy(
                supplierType,
                packResources
        );
    }

    private static Object wrapAsProxy(
            Class<?> supplierType,
            Object packResources
    ) {

        if (!supplierType.isInterface()) {
            return null;
        }

        return Proxy.newProxyInstance(
                supplierType.getClassLoader(),
                new Class<?>[]{supplierType},
                (proxy, method, args) -> {

                    switch (method.getName()) {
                        case "hashCode":
                            return System.identityHashCode(proxy);

                        case "equals":
                            return proxy == args[0];

                        case "toString":
                            return "MTRFRAResourcesSupplier";

                        default:
                            return packResources;
                    }
                }
        );
    }
}
