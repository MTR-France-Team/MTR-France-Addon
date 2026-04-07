package fr.mtrfranceaddon.mod.common.item;

import org.mtr.mapping.holder.ItemSettings;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.data.RailType;
import org.mtr.mod.item.ItemRailModifier;

public class CustomItemRailModifier extends ItemRailModifier {

    private final int speed;
    private final boolean isOneWay;
    private final String translationKey;

    public CustomItemRailModifier(boolean isOneWay, RailType railType, int speed, ItemSettings itemSettings) {
        super(true, false, false, isOneWay, railType, itemSettings);
        this.speed = speed;
        this.isOneWay = isOneWay;
        this.translationKey = "item.mtrfranceaddon.rail_connector_" + (this.isOneWay ? "oneway" : "base");
    }

    @Override
    public Text getName2(ItemStack stack) {
        return Text.cast(TextHelper.translatable(this.translationKey, speed));
    }

}
