package fr.mtrfranceaddon.mod.common.block;

import fr.mtrfranceaddon.mod.common.registry.ModBlockEntities;
import fr.mtrfranceaddon.mod.common.registry.ModBlocks;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.Init;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockPlatform;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderRails;
import org.mtr.mod.render.StoredMatrixTransformations;

public class InvisiblePlatform extends BlockPlatform implements BlockWithEntity {

    public InvisiblePlatform(BlockSettings settings) {
        super(settings.nonOpaque(), false);
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new InvPlatBE(blockPos, blockState);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    public static class InvPlatBE extends BlockEntityExtension {

        public InvPlatBE(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.INVISIBLE_PLATFORM.get(), blockPos, blockState);
        }

    }

    public static class InvPlatRenderer extends BlockEntityRenderer<InvPlatBE> {

        public InvPlatRenderer(Argument dispatcher) {
            super(dispatcher);
        }

        @Override
        public void render(InvPlatBE blockEntity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
            if(blockEntity.getWorld2() == null) return;

            final MinecraftClient minecraftClient = MinecraftClient.getInstance();
            final ClientPlayerEntity clientPlayerEntity = minecraftClient.getPlayerMapped();

            if(clientPlayerEntity == null) return;
            if((RenderRails.isHoldingRailRelated(clientPlayerEntity) || clientPlayerEntity.isHolding(ModBlocks.INVISIBLE_PLATFORM.get().asItem())) && minecraftClient.getCurrentScreenMapped() == null) {
                final StoredMatrixTransformations storedMatrixTransformations = new StoredMatrixTransformations(0.5 + blockEntity.getPos2().getX(), 0.5 + blockEntity.getPos2().getY(), 0.5 + blockEntity.getPos2().getZ());

                MainRenderer.scheduleRender(new Identifier("mtrfranceaddon", "textures/item/invisible_platform.png"), false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (graphicsHolderNew, offset) -> {
                    storedMatrixTransformations.transform(graphicsHolderNew, offset);
                    InitClient.transformToFacePlayer(graphicsHolderNew, blockEntity.getPos2().getX() + 0.5, blockEntity.getPos2().getY() + 0.5, blockEntity.getPos2().getZ() + 0.5);
                    graphicsHolderNew.rotateZDegrees(180);
                    IDrawing.drawTexture(graphicsHolderNew, -0.5f, -0.5f, 1, 1, Direction.UP, GraphicsHolder.getDefaultLight());
                    graphicsHolderNew.pop();
                });
            }
        }

    }

}