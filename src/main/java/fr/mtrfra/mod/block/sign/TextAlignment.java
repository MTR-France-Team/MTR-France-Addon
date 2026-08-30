package fr.mtrfra.mod.block.sign;

public enum TextAlignment {

    LEFT,
    CENTER,
    RIGHT;

    public float getX(float width) {
        switch (this) {
            case LEFT:
                return 0F;
            case RIGHT:
                return -width;
            default:
                return -width / 2F;
        }
    }

}
