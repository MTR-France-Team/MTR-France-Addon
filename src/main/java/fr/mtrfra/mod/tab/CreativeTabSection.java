package fr.mtrfra.mod.tab;

import org.mtr.mapping.holder.MutableText;

public record CreativeTabSection(
        String id,
        MutableText title,
        String texturePath,
        int titleColor,
        int backgroundColor
) {

    public CreativeTabSection(String id, MutableText title, String texturePath, int titleColor) {
        this(id, title, texturePath, titleColor, 0xAA000000);
    }

}
