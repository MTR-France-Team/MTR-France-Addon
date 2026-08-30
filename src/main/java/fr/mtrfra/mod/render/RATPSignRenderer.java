package fr.mtrfra.mod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.mtrfra.mod.block.sign.RATPSignBase;
import fr.mtrfra.mod.mixin.GraphicsHolderAccessor;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.TextFormatting;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.block.IBlock;

public class RATPSignRenderer extends BlockEntityRenderer<RATPSignBase.BlockEntityBase> {

    private static final float TITLE_SCALE_DEFAULT = 1 / 50F;

    public RATPSignRenderer(Argument argument) {
        super(argument);
    }

    @Override
    public void render(RATPSignBase.BlockEntityBase entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
        final BlockState state = entity.getCachedState2();
        final Object rawBlock = state.getBlock().data;
        if (!(rawBlock instanceof RATPSignBase signBlock)) {
            return;
        }

        final String title = entity.getEffectiveTitle().trim();
        final String subtitle = signBlock.hasSubtitle() ? entity.subtitle.trim() : "";
        if (title.isEmpty() && subtitle.isEmpty()) {
            return;
        }

        final GraphicsHolderAccessor accessor = (GraphicsHolderAccessor) (Object) graphicsHolder;
        final PoseStack poseStack = accessor.getMatrixStack();
        final Direction facing = IBlock.getStatePropertySafe(state, DirectionHelper.FACING);
        final int category = IBlock.getStatePropertySafe(state, RATPSignBase.CATEGORY);
        final RATPSignBase.TextLayout layout = signBlock.textLayout(category);
        final float titleScale = title.isEmpty() ? TITLE_SCALE_DEFAULT : fitTitleScale(signBlock, layout, title);

        final float baseRotation = -facing.asRotation() + 180F;
        drawFace(poseStack, graphicsHolder, signBlock, layout, title, subtitle, baseRotation, titleScale);
        if (signBlock.doubleSided()) {
            drawFace(poseStack, graphicsHolder, signBlock, layout, title, subtitle, baseRotation + 180F, titleScale);
        }
    }

    private static float fitTitleScale(RATPSignBase signBlock, RATPSignBase.TextLayout layout, String title) {
        final float availableWidth;
        if (layout.titleMaxWidth > 0F) {
            availableWidth = layout.titleMaxWidth;
        } else {
            final double[] box = signBlock.boundingBox();
            final float margin = 1.5F + signBlock.extraTitleMargin();
            final float spaceLeft = layout.titleX - (float) box[0] - margin;
            final float spaceRight = (float) box[3] - layout.titleX - margin;
            switch (signBlock.textAlignment()) {
                case LEFT:
                    availableWidth = spaceLeft;
                    break;
                case RIGHT:
                    availableWidth = spaceRight;
                    break;
                default:
                    availableWidth = 2F * Math.min(spaceLeft, spaceRight);
                    break;
            }
        }
        final float widthAtDefaultScale = GraphicsHolder.getTextWidth(title) * TITLE_SCALE_DEFAULT * 16F;
        if (availableWidth <= 0F || widthAtDefaultScale <= availableWidth || widthAtDefaultScale <= 0F) {
            return TITLE_SCALE_DEFAULT;
        }
        return TITLE_SCALE_DEFAULT * (availableWidth / widthAtDefaultScale);
    }

    private void drawFace(PoseStack poseStack, GraphicsHolder graphicsHolder, RATPSignBase signBlock, RATPSignBase.TextLayout layout, String title, String subtitle, float rotationDegrees, float titleScale) {
        final boolean hasTitle = !title.isEmpty();
        final boolean hasSubtitle = !subtitle.isEmpty();
        if (!hasTitle && !hasSubtitle) {
            return;
        }

        final boolean stackedAsOneBlock = hasTitle && hasSubtitle && !layout.hasSubtitlePosition;
        final float titleHeight = 9F * titleScale * 16F;
        final float subtitleHeight = 9F * (1 / 125F) * 16F;
        final float blockHalfHeight = (titleHeight + subtitleHeight) / 2F;
        final float textZ = signBlock.textZ() - 0.001F;

        if (hasTitle) {
            final float titleY = stackedAsOneBlock ? layout.titleY + blockHalfHeight - titleHeight / 2F + 0.2F : layout.titleY;
            pushTextPose(poseStack, rotationDegrees, layout.titleX, titleY, textZ, titleScale);
            final int titleX = (int) signBlock.textAlignment().getX(GraphicsHolder.getTextWidth(title));
            graphicsHolder.drawText(title, titleX, -4, 0xFFFFFF, false, GraphicsHolder.getDefaultLight());
            poseStack.popPose();
        }

        if (hasSubtitle) {
            final MutableText formattedSubtitle = TextHelper.literal(subtitle).formatted(TextFormatting.ITALIC);
            final float subtitleY;
            if (layout.hasSubtitlePosition) {
                subtitleY = layout.subtitleY;
            } else if (hasTitle) {
                subtitleY = layout.titleY - blockHalfHeight + subtitleHeight / 2F - 0.2F;
            } else {
                subtitleY = layout.titleY;
            }
            pushTextPose(poseStack, rotationDegrees, layout.subtitleX, subtitleY, textZ, 1 / 125F);
            graphicsHolder.drawText(formattedSubtitle, -GraphicsHolder.getTextWidth(formattedSubtitle) / 2, -4, 0xFFFFFF, false, GraphicsHolder.getDefaultLight());
            poseStack.popPose();
        }
    }

    private void pushTextPose(PoseStack poseStack, float rotationDegrees, float x, float y, float z, float scale) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        //? if >=1.19.4 {
        poseStack.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(rotationDegrees)));
        //? } else {
        /*poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(rotationDegrees));
        *///? }
        poseStack.translate(x / 16.0 - 0.5, y / 16.0, z / 16.0 - 0.5);
        //? if >=1.19.4 {
        poseStack.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(180)));
        //? } else {
        /*poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(180));
        *///? }
        poseStack.scale(scale, -scale, scale);
    }

}
