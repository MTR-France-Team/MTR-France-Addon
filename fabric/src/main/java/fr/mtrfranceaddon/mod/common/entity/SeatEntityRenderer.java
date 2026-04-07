package fr.mtrfranceaddon.mod.common.entity;

import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {

    public SeatEntityRenderer(Argument argument) {
        super(argument);
    }

    @Override
    public void render(SeatEntity entity, float yaw, float tickDelta, GraphicsHolder gh, int light) {
    }

    @Override
    public Identifier getTexture2(SeatEntity entity) {
        return new Identifier("mtrfranceaddon", "textures/entity/empty.png");
    }
}