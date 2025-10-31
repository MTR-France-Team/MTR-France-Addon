package fr.mtrfranceaddon.mod.common.registry;

import fr.mtrfranceaddon.mod.common.Init;
import fr.mtrfranceaddon.mod.common.data.CustomRailData;
import fr.mtrfranceaddon.mod.common.item.*;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.registry.ItemRegistryObject;
import org.mtr.mod.data.RailType;

import java.util.HashMap;
import java.util.Map;

public class ModItems {

    public static Map<String, ItemRegistryObject> RAIL_CONNECTORS = new HashMap<>();

    public static void register() {
        Init.LOGGER.info("Registering Items");

        for (CustomRailData.RailInfo info : CustomRailData.CUSTOM_RAILS) {
            registerRailModifier(info, false);
            registerRailModifier(info, true);
        }
    }

    private static void registerRailModifier(CustomRailData.RailInfo info, boolean isOneWay) {
        String itemId = info.name.toLowerCase() + (isOneWay ? "_oneway" : "");

        ItemRegistryObject registeredItem = MTRFranceAddonRegistry.registerItem(
                itemId,
                itemSettings -> new Item(new CustomItemRailModifier(
                        isOneWay,
                        RailType.valueOf(info.name),
                        info.speed,
                        itemSettings
                )),
                ModItemGroups.MTRFranceAddonGroup
        );

        RAIL_CONNECTORS.put(itemId, registeredItem);
    }

}
