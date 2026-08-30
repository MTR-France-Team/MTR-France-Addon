package fr.mtrfra.mod.util;

import org.mtr.mapping.holder.Identifier;

public final class Constants {

    public static final String MOD_ID = "mtrfranceaddon";
    public static final String MOD_NAME = "MTR France Addon";

    public static final String MTR_MOD_ID = "mtr";

    private Constants() {}

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
