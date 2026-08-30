package fr.mtrfra.mod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.mtrfra.mod.entity.CushionEntity;
import fr.mtrfra.mod.mixin.GraphicsHolderAccessor;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.registry.BlockRegistryObject;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mod.InitClient;
//? if fabric {
import net.minecraft.resources.ResourceLocation;
//? }

import java.util.function.Supplier;

public class CushionEntityRenderer extends EntityRenderer<CushionEntity> {

    private final Supplier<BlockRegistryObject> blockSupplier;

    public CushionEntityRenderer(Argument argument, Supplier<BlockRegistryObject> blockSupplier) {
        super(argument);
        this.blockSupplier = blockSupplier;
    }

    public static int currentTintColor = 0xFFFFFF;

    @Override
    public void render(CushionEntity entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light) {
        final GraphicsHolderAccessor accessor = (GraphicsHolderAccessor) (Object) graphicsHolder;
        final PoseStack poseStack = accessor.getMatrixStack();
        final MultiBufferSource bufferSource = accessor.getVertexConsumerProvider();
        final BlockState blockState = blockSupplier.get().get().getDefaultState();
        final net.minecraft.world.level.block.state.BlockState rawState = (net.minecraft.world.level.block.state.BlockState) blockState.data;

        poseStack.pushPose();
        //? if >=1.19.4 {
        poseStack.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(180.0F - yaw)));
        //? } else {
        /*poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(180.0F - yaw));
        *///? }
        poseStack.translate(-0.5, 0, -0.5);

        currentTintColor = resolveColor(entity);
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.renderSingleBlock(rawState, poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static int resolveColor(CushionEntity entity) {
        if (entity.isAlwaysStationColored()) {
            return InitClient.getStationColor(entity.getBlockPos2());
        }
        final int customColor = entity.getCustomColor();
        return customColor == -1 ? 0xFFFFFF : customColor;
    }

    @Override
    public Identifier getTexture2(CushionEntity entity) {
        return new Identifier(Constants.MOD_ID, "textures/entity/empty.png");
    }

    //? if fabric {
    @Override
    public ResourceLocation getTextureLocation(CushionEntity entity) {
        return (ResourceLocation) getTexture2(entity).data;
    }
    //? }

}
