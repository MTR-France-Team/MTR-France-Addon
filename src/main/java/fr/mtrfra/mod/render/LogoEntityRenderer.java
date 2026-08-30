package fr.mtrfra.mod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.mtrfra.mod.entity.LogoEntity;
import fr.mtrfra.mod.mixin.GraphicsHolderAccessor;
import fr.mtrfra.mod.registry.ModEntities;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
//? if fabric {
import net.minecraft.resources.ResourceLocation;
//? }
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.registry.BlockRegistryObject;

import java.util.function.Supplier;

public class LogoEntityRenderer extends EntityRenderer<LogoEntity> {

    private static final float DEFAULT_Z_OFFSET = -1.01F;
    private static final float SNCF_SIGN_Z_OFFSET = -0.5572F;

    private final Supplier<BlockRegistryObject> blockSupplier;

    public LogoEntityRenderer(Argument argument, Supplier<BlockRegistryObject> blockSupplier) {
        super(argument);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public void render(LogoEntity entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light) {
        final GraphicsHolderAccessor accessor = (GraphicsHolderAccessor) (Object) graphicsHolder;
        final PoseStack poseStack = accessor.getMatrixStack();
        final MultiBufferSource bufferSource = accessor.getVertexConsumerProvider();
        final BlockState state = (BlockState) blockSupplier.get().get().getDefaultState().data;
        final float pitch = entity.getPitch2(tickDelta);

        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        //? if >=1.19.4 {
        poseStack.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(-yaw)));
        poseStack.mulPose(new org.joml.Quaternionf().rotationX((float) Math.toRadians(pitch)));
        //? } else {
        /*poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-yaw));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitch));
        *///? }
        final boolean isSncfSign = entity.getType() == ModEntities.LOGO_SNCF_SIGN.get().data;
        poseStack.translate(-0.5, -0.5, isSncfSign ? SNCF_SIGN_Z_OFFSET : DEFAULT_Z_OFFSET);

        dispatcher.renderSingleBlock(state, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public Identifier getTexture2(LogoEntity entity) {
        return new Identifier(Constants.MOD_ID, "textures/entity/empty.png");
    }

    //? if fabric {
    @Override
    public ResourceLocation getTextureLocation(LogoEntity entity) {
        return (ResourceLocation) getTexture2(entity).data;
    }
    //? }

}
