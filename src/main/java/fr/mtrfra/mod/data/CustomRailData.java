package fr.mtrfra.mod.data;

import org.mtr.mapping.holder.MapColor;

import java.util.Arrays;
import java.util.List;

public final class CustomRailData {

    public static final class RailInfo {
        public final String name;
        public final int speed;
        public final MapColor color;

        public RailInfo(String name, int speed, MapColor color) {
            this.name = name;
            this.speed = speed;
            this.color = color;
        }
    }

    private CustomRailData() {}

    public static final List<RailInfo> CUSTOM_RAILS = Arrays.asList(
            new RailInfo("RAIL_30", 30, MapColor.getSpruceBrownMapped()),
            new RailInfo("RAIL_50", 50, MapColor.getDarkGreenMapped()),
            new RailInfo("RAIL_70", 70, MapColor.getDarkRedMapped()),
            new RailInfo("RAIL_90", 90, MapColor.getLightBlueGrayMapped()),
            new RailInfo("RAIL_110", 110, MapColor.getTerracottaOrangeMapped()),
            new RailInfo("RAIL_130", 130, MapColor.getLimeMapped()),
            new RailInfo("RAIL_150", 150, MapColor.getBlueMapped()),
            new RailInfo("RAIL_170", 170, MapColor.getYellowMapped()),
            new RailInfo("RAIL_220", 220, MapColor.getGoldMapped()),
            new RailInfo("RAIL_270", 270, MapColor.getOrangeMapped()),
            new RailInfo("RAIL_320", 320, MapColor.getPinkMapped()),
            new RailInfo("RAIL_350", 350, MapColor.getStoneGrayMapped()),
            new RailInfo("RAIL_999", 999, MapColor.getWhiteMapped())
    );

}
