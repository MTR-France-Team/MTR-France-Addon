package fr.mtrfra.mod.block.platform;

import fr.mtrfra.mod.registry.ModBlockEntities;
import fr.mtrfra.mod.registry.ModBlocks;
import fr.mtrfra.mod.util.Constants;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mapping.holder.SlabType;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.SlabBlockExtension;
import org.mtr.mod.InitClient;
import org.mtr.mod.block.BlockPlatformSlab;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.RenderRails;
import org.mtr.mod.render.StoredMatrixTransformations;

public class InvisibleSlabPlatform extends BlockPlatformSlab implements BlockWithEntity {

    public InvisibleSlabPlatform(BlockSettings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    public static class BlockEntity extends BlockEntityExtension {
        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.INVISIBLE_SLAB_PLATFORM.get(), blockPos, blockState);
        }
    }

    public static class Renderer extends BlockEntityRenderer<BlockEntity> {

        public Renderer(Argument dispatcher) {
            super(dispatcher);
        }

        @Override
        public void render(BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
            if (blockEntity.getWorld2() == null) {
                return;
            }
            final MinecraftClient minecraftClient = MinecraftClient.getInstance();
            final ClientPlayerEntity clientPlayerEntity = minecraftClient.getPlayerMapped();
            if (clientPlayerEntity == null) {
                return;
            }
            final StoredMatrixTransformations transformations = new StoredMatrixTransformations(
                    0.5 + blockEntity.getPos2().getX(), 0.5 + blockEntity.getPos2().getY(), 0.5 + blockEntity.getPos2().getZ()
            );
            final boolean shouldShowHint = (RenderRails.isHoldingRailRelated(clientPlayerEntity) || clientPlayerEntity.isHolding(ModBlocks.INVISIBLE_SLAB_PLATFORM.get().asItem()))
                    && minecraftClient.getCurrentScreenMapped() == null;
            if (!shouldShowHint) {
                return;
            }
            final SlabType slabType = SlabBlockExtension.getType(blockEntity.getCachedState2());
            final String texturePath = SlabType.getDoubleMapped().equals(slabType)
                    ? "textures/item/station_equipment/invisible_platform.png"
                    : "textures/item/station_equipment/invisible_slab_platform.png";
            MainRenderer.scheduleRender(
                    new Identifier(Constants.MOD_ID, texturePath),
                    false,
                    QueuedRenderLayer.LIGHT_TRANSLUCENT,
                    (graphicsHolderNew, offset) -> {
                        transformations.transform(graphicsHolderNew, offset);
                        InitClient.transformToFacePlayer(
                                graphicsHolderNew,
                                blockEntity.getPos2().getX() + 0.5,
                                blockEntity.getPos2().getY() + 0.5,
                                blockEntity.getPos2().getZ() + 0.5
                        );
                        graphicsHolderNew.rotateZDegrees(180);
                        IDrawing.drawTexture(graphicsHolderNew, -0.5F, -0.5F, 1, 1, Direction.UP, GraphicsHolder.getDefaultLight());
                        graphicsHolderNew.pop();
                    }
            );
        }

        @Override
        public boolean isInRenderDistance(BlockEntity blockEntity, Vector3d position) {
            return true;
        }
    }

}
