package fr.mtrfra.mod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.mtrfra.mod.entity.TicketMachineEntity;
import fr.mtrfra.mod.mixin.GraphicsHolderAccessor;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.mapper.DirectionHelper;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.registry.BlockRegistryObject;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.state.BlockState;
//? if fabric {
import net.minecraft.resources.ResourceLocation;
//? }
import org.mtr.mod.block.IBlock;

import java.util.function.Supplier;

public class TicketMachineEntityRenderer extends EntityRenderer<TicketMachineEntity> {

    private final Supplier<BlockRegistryObject> blockSupplier;

    public TicketMachineEntityRenderer(Argument argument, Supplier<BlockRegistryObject> blockSupplier) {
        super(argument);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public void render(TicketMachineEntity entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light) {
        final GraphicsHolderAccessor accessor = (GraphicsHolderAccessor) (Object) graphicsHolder;
        final PoseStack poseStack = accessor.getMatrixStack();
        final MultiBufferSource bufferSource = accessor.getVertexConsumerProvider();
        final BlockState state = (BlockState) blockSupplier.get().get().getDefaultState()
                .with(new Property<>(DirectionHelper.FACING.data), Direction.SOUTH.data)
                .with(new Property<>(IBlock.HALF.data), IBlock.DoubleBlockHalf.LOWER)
                .data;

        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        //? if >=1.19.4 {
        poseStack.mulPose(new org.joml.Quaternionf().rotationY((float) Math.toRadians(-yaw)));
        //? } else {
        /*poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-yaw));
        *///? }
        poseStack.translate(-0.5, 0, -0.5);

        dispatcher.renderSingleBlock(state, poseStack, bufferSource, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public Identifier getTexture2(TicketMachineEntity entity) {
        return new Identifier(Constants.MOD_ID, "textures/entity/empty.png");
    }

    //? if fabric {
    @Override
    public ResourceLocation getTextureLocation(TicketMachineEntity entity) {
        return (ResourceLocation) getTexture2(entity).data;
    }
    //? }

}
