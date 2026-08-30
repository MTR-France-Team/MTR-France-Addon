package fr.mtrfra.mod.block.copycat;

import fr.mtrfra.mod.item.CopycatLayerBlockItem;
import fr.mtrfra.mod.registry.ModBlockEntities;
import fr.mtrfra.mod.registry.ModBlocks;
import fr.mtrfra.mod.mixin.GraphicsHolderAccessor;
import fr.mtrfra.mod.util.ClientRedrawQueue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
//? if >=1.19.4 {
import net.minecraft.core.registries.BuiltInRegistries;
//? }
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//? }
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockRenderType;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.Items;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.mod.render.StoredMatrixTransformations;

import java.util.List;

public abstract class CopycatBlockBase extends BlockExtension implements BlockWithEntity {

    protected CopycatBlockBase(BlockSettings blockSettings) {
        super(blockSettings);
    }

    protected abstract int heightUnits(BlockState state);

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape2(0, 0, 0, 16, heightUnits(state), 16);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape2(0, 0, 0, 16, heightUnits(state), 16);
    }

    @Override
    public BlockRenderType getRenderType2(BlockState state) {
        return BlockRenderType.getEntityblockAnimatedMapped();
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState, this);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return ActionResult.PASS;
        }

        if (stack.getItem().equals(Items.BRUSH.get().asItem())) {
            if (!world.isClient()) {
                final BlockEntity copycatEntity = getCopycatEntity(world, pos);
                if (copycatEntity != null) {
                    copycatEntity.setLocked(!copycatEntity.isLocked());
                    player.sendMessage(Text.cast(TextHelper.translatable(copycatEntity.isLocked()
                            ? "block.mtrfranceaddon.copycat.now_locked"
                            : "block.mtrfranceaddon.copycat.now_unlocked")), true);
                }
            }
            return ActionResult.SUCCESS;
        }

        final Object rawItem = stack.getItem().data;
        if (!(rawItem instanceof BlockItem)) {
            return ActionResult.PASS;
        }
        final Block clickedBlock = ((BlockItem) rawItem).getBlock();
        if (clickedBlock == asBlock2().data) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            final BlockEntity copycatEntity = getCopycatEntity(world, pos);
            if (copycatEntity == null) {
                return ActionResult.PASS;
            }
            if (copycatEntity.isLocked()) {
                player.sendMessage(Text.cast(TextHelper.translatable("block.mtrfranceaddon.copycat.locked")), true);
                return ActionResult.SUCCESS;
            }
            final net.minecraft.world.level.block.state.BlockState clickedDefaultState = clickedBlock.defaultBlockState();
            if (!Block.isShapeFullBlock(clickedDefaultState.getShape(EmptyBlockGetter.INSTANCE, net.minecraft.core.BlockPos.ZERO))) {
                player.sendMessage(Text.cast(TextHelper.translatable("block.mtrfranceaddon.copycat.not_full_block")), true);
                return ActionResult.SUCCESS;
            }
            copycatEntity.setMimic(clickedDefaultState);
        }
        return ActionResult.SUCCESS;
    }

    private static BlockEntity getCopycatEntity(World world, BlockPos pos) {
        final Object blockEntityRaw = ((Level) world.data).getBlockEntity((net.minecraft.core.BlockPos) pos.data);
        return blockEntityRaw instanceof BlockEntity ? (BlockEntity) blockEntityRaw : null;
    }

    @Override
    public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
        final ItemStack stack = asItem2().getDefaultStack();
        final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity.data instanceof BlockEntity) {
            final String mimicId = ((BlockEntity) entity.data).getMimicId();
            if (mimicId != null) {
                stack.getOrCreateTag().putString(CopycatLayerBlockItem.KEY_MIMIC, mimicId);
            }
        }
        return stack;
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced2(world, pos, state, placer, itemStack);
        if (world.isClient() || !itemStack.getOrCreateTag().contains(CopycatLayerBlockItem.KEY_MIMIC)) {
            return;
        }
        final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null && entity.data instanceof BlockEntity) {
            ((BlockEntity) entity.data).setMimic(BlockEntity.idToMimic(itemStack.getOrCreateTag().getString(CopycatLayerBlockItem.KEY_MIMIC)));
        }
    }

    public static class BlockEntity extends BlockEntityExtension {

        private static final String KEY_MIMIC = CopycatLayerBlockItem.KEY_MIMIC;
        private static final String KEY_LOCKED = "locked";

        private net.minecraft.world.level.block.state.BlockState mimic;
        private boolean locked;
        private CopycatBlockBase owner;

        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.COPYCAT.get(), blockPos, blockState);
        }

        public BlockEntity(BlockPos blockPos, BlockState blockState, CopycatBlockBase owner) {
            this(blockPos, blockState);
            this.owner = owner;
        }

        public int heightUnits() {
            if (owner != null) {
                return owner.heightUnits(getCachedState2());
            }
            final Object rawBlock = getCachedState2().getBlock().data;
            return rawBlock instanceof CopycatBlockBase ? ((CopycatBlockBase) rawBlock).heightUnits(getCachedState2()) : 16;
        }

        public net.minecraft.world.level.block.state.BlockState getMimic() {
            return mimic;
        }

        public String getMimicId() {
            return mimic == null ? null : mimicToId(mimic);
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
            redraw();
        }

        public void setMimic(net.minecraft.world.level.block.state.BlockState mimic) {
            this.mimic = mimic;
            redraw();
        }

        public void redraw() {
            markDirty2();
            final Level level = (Level) getWorld2().data;
            if (level instanceof ServerLevel serverLevel) {
                final net.minecraft.core.BlockPos blockPos = (net.minecraft.core.BlockPos) getPos2().data;
                final Packet<?> updatePacket = getUpdatePacket();
                if (updatePacket != null) {
                    for (final ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(blockPos), false)) {
                        player.connection.send(updatePacket);
                    }
                }
            }
        }

        private static String mimicToId(net.minecraft.world.level.block.state.BlockState mimic) {
            //? if >=1.19.4 {
            return BuiltInRegistries.BLOCK.getKey(mimic.getBlock()).toString();
            //? } else {
            /*return net.minecraft.core.Registry.BLOCK.getKey(mimic.getBlock()).toString();
            *///? }
        }

        public static net.minecraft.world.level.block.state.BlockState idToMimic(String idString) {
            final ResourceLocation id = ResourceLocation.tryParse(idString);
            //? if >=1.19.4 {
            final Block block = id == null ? null : BuiltInRegistries.BLOCK.get(id);
            //? } else {
            /*final net.minecraft.world.level.block.Block block = id == null ? null : net.minecraft.core.Registry.BLOCK.get(id);
            *///? }
            return block == null ? null : block.defaultBlockState();
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            if (mimic != null) {
                compoundTag.putString(KEY_MIMIC, mimicToId(mimic));
            }
            compoundTag.putBoolean(KEY_LOCKED, locked);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            mimic = compoundTag.contains(KEY_MIMIC) ? idToMimic(compoundTag.getString(KEY_MIMIC)) : null;
            locked = compoundTag.getBoolean(KEY_LOCKED);
            try {
                final World world = getWorld2();
                final Object worldData = world == null ? null : world.data;
                if (worldData instanceof Level level && level.isClientSide()) {
                    ClientRedrawQueue.queue((net.minecraft.core.BlockPos) getPos2().data, level);
                }
            } catch (final Exception ignored) {}
        }

    }

    public static class Renderer extends BlockEntityRenderer<BlockEntity> {

        public Renderer(Argument dispatcher) {
            super(dispatcher);
        }

        @Override
        public void render(BlockEntity blockEntity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay) {
            final net.minecraft.world.level.block.state.BlockState mimic = blockEntity.getMimic() != null
                    ? blockEntity.getMimic()
                    : (net.minecraft.world.level.block.state.BlockState) ModBlocks.COPYCAT_LAYER_PLACEHOLDER.get().getDefaultState().data;

            final float totalHeight = blockEntity.heightUnits() / 16.0F;

            final GraphicsHolderAccessor accessor = (GraphicsHolderAccessor) (Object) graphicsHolder;
            final PoseStack poseStack = accessor.getMatrixStack();
            final MultiBufferSource bufferSource = accessor.getVertexConsumerProvider();

            final Level level = (Level) blockEntity.getWorld2().data;
            final net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) blockEntity.getPos2().data;
            final RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(mimic);
            final VertexConsumer consumer = bufferSource.getBuffer(renderType);

            final BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic);
            //? if >=1.19 {
            final RandomSource random = RandomSource.create(42L);
            //? } else {
            /*final java.util.Random random = new java.util.Random(42L);
            *///? }
            final BlockColors blockColors = Minecraft.getInstance().getBlockColors();

            for (final net.minecraft.core.Direction direction : new net.minecraft.core.Direction[]{
                    net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH,
                    net.minecraft.core.Direction.EAST, net.minecraft.core.Direction.WEST
            }) {
                final List<BakedQuad> quads = bakedModel.getQuads(mimic, direction, random);
                if (quads.isEmpty()) {
                    continue;
                }
                final BakedQuad referenceQuad = quads.get(0);
                float red = 1.0F, green = 1.0F, blue = 1.0F;
                if (referenceQuad.isTinted()) {
                    final int color = blockColors.getColor(mimic, level, pos, referenceQuad.getTintIndex());
                    red = ((color >> 16) & 0xFF) / 255.0F;
                    green = ((color >> 8) & 0xFF) / 255.0F;
                    blue = (color & 0xFF) / 255.0F;
                }
                emitSideQuad(consumer, poseStack.last(), referenceQuad.getSprite(), direction, 0.0F, totalHeight, red, green, blue, light);
            }

            poseStack.pushPose();
            poseStack.translate(0.0, totalHeight - 1.0, 0.0);
            drawQuads(consumer, poseStack, bakedModel.getQuads(mimic, net.minecraft.core.Direction.UP, random), mimic, level, pos, light, overlay);
            poseStack.popPose();

            drawQuads(consumer, poseStack, bakedModel.getQuads(mimic, net.minecraft.core.Direction.DOWN, random), mimic, level, pos, light, overlay);
            drawQuads(consumer, poseStack, bakedModel.getQuads(mimic, null, random), mimic, level, pos, light, overlay);

            if (blockEntity.isLocked()) {
                drawLockedOutline(blockEntity.getPos2(), totalHeight);
            }
        }

        private static boolean isHoldingBrush() {
            final ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().getPlayerMapped();
            return clientPlayer != null && clientPlayer.isHolding(Items.BRUSH.get().asItem());
        }

        private static void drawLockedOutline(BlockPos pos, float totalHeight) {
            final StoredMatrixTransformations transformations = new StoredMatrixTransformations(0.5 + pos.getX(), pos.getY(), 0.5 + pos.getZ());
            final boolean holdingBrush = isHoldingBrush();
            MainRenderer.scheduleRender(
                    new Identifier("mtr", "textures/block/white.png"), false, QueuedRenderLayer.LIGHT,
                    (graphicsHolderNew, offset) -> {
                        transformations.transform(graphicsHolderNew, offset);
                        final float y = totalHeight + 0.01F;
                        IDrawing.drawTexture(graphicsHolderNew, -0.4375F, y, -0.4375F, -0.3125F, y, 0.3125F, org.mtr.mapping.holder.Direction.UP, 0xFF00E5FF, GraphicsHolder.getDefaultLight());
                        IDrawing.drawTexture(graphicsHolderNew, 0.3125F, y, -0.4375F, 0.4375F, y, 0.3125F, org.mtr.mapping.holder.Direction.UP, 0xFF00E5FF, GraphicsHolder.getDefaultLight());
                        IDrawing.drawTexture(graphicsHolderNew, -0.4375F, y, -0.4375F, 0.3125F, y, -0.3125F, org.mtr.mapping.holder.Direction.UP, 0xFF00E5FF, GraphicsHolder.getDefaultLight());
                        IDrawing.drawTexture(graphicsHolderNew, -0.4375F, y, 0.3125F, 0.3125F, y, 0.4375F, org.mtr.mapping.holder.Direction.UP, 0xFF00E5FF, GraphicsHolder.getDefaultLight());

                        if (holdingBrush) {
                            drawCornerEdge(graphicsHolderNew, -0.4375F, -0.4375F, org.mtr.mapping.holder.Direction.NORTH, org.mtr.mapping.holder.Direction.WEST, totalHeight);
                            drawCornerEdge(graphicsHolderNew, 0.4375F, -0.4375F, org.mtr.mapping.holder.Direction.NORTH, org.mtr.mapping.holder.Direction.EAST, totalHeight);
                            drawCornerEdge(graphicsHolderNew, 0.4375F, 0.4375F, org.mtr.mapping.holder.Direction.SOUTH, org.mtr.mapping.holder.Direction.EAST, totalHeight);
                            drawCornerEdge(graphicsHolderNew, -0.4375F, 0.4375F, org.mtr.mapping.holder.Direction.SOUTH, org.mtr.mapping.holder.Direction.WEST, totalHeight);
                        }

                        graphicsHolderNew.pop();
                    }
            );
        }

        private static void drawCornerEdge(GraphicsHolder graphicsHolderNew, float cornerX, float cornerZ, org.mtr.mapping.holder.Direction zFace, org.mtr.mapping.holder.Direction xFace, float totalHeight) {
            final float zFaceZ = zFace == org.mtr.mapping.holder.Direction.NORTH ? -0.4375F : 0.4375F;
            final float zStripStart = cornerX < 0 ? -0.4375F : 0.3125F;
            final float zStripEnd = cornerX < 0 ? -0.3125F : 0.4375F;
            IDrawing.drawTexture(graphicsHolderNew, zStripStart, 0.0F, zFaceZ, zStripEnd, totalHeight, zFaceZ, zFace, 0xFF00E5FF, GraphicsHolder.getDefaultLight());

            final float xFaceX = xFace == org.mtr.mapping.holder.Direction.WEST ? -0.4375F : 0.4375F;
            final float xStripStart = cornerZ < 0 ? -0.4375F : 0.3125F;
            final float xStripEnd = cornerZ < 0 ? -0.3125F : 0.4375F;
            IDrawing.drawTexture(graphicsHolderNew, xFaceX, 0.0F, xStripStart, xFaceX, totalHeight, xStripEnd, xFace, 0xFF00E5FF, GraphicsHolder.getDefaultLight());
        }

        private static final float SHADE_UP = 1.0F;
        private static final float SHADE_NORTH_SOUTH = 0.8F;
        private static final float SHADE_EAST_WEST = 0.6F;
        private static final float SHADE_DOWN = 0.5F;

        private static void emitSideQuad(
                VertexConsumer consumer, PoseStack.Pose pose,
                TextureAtlasSprite sprite, net.minecraft.core.Direction direction, float yMin, float yMax,
                float red, float green, float blue, int light
        ) {
            final float u0 = sprite.getU0();
            final float u1 = sprite.getU1();
            //? if >=1.20.4 {
            final float vTop = sprite.getV(1.0F - yMax);
            final float vBottom = sprite.getV(1.0F - yMin);
            //? } else {
            /*final float vTop = sprite.getV((1.0F - yMax) * 16.0F);
            final float vBottom = sprite.getV((1.0F - yMin) * 16.0F);
            *///? }

            final float[][] corners;
            final float[] normal;
            final float shade;
            switch (direction) {
                case NORTH:
                    corners = new float[][]{{0, yMax, 0}, {1, yMax, 0}, {1, yMin, 0}, {0, yMin, 0}};
                    normal = new float[]{0, 0, -1};
                    shade = SHADE_NORTH_SOUTH;
                    break;
                case SOUTH:
                    corners = new float[][]{{1, yMax, 1}, {0, yMax, 1}, {0, yMin, 1}, {1, yMin, 1}};
                    normal = new float[]{0, 0, 1};
                    shade = SHADE_NORTH_SOUTH;
                    break;
                case EAST:
                    corners = new float[][]{{1, yMax, 0}, {1, yMax, 1}, {1, yMin, 1}, {1, yMin, 0}};
                    normal = new float[]{1, 0, 0};
                    shade = SHADE_EAST_WEST;
                    break;
                case WEST:
                    corners = new float[][]{{0, yMax, 1}, {0, yMax, 0}, {0, yMin, 0}, {0, yMin, 1}};
                    normal = new float[]{-1, 0, 0};
                    shade = SHADE_EAST_WEST;
                    break;
                default:
                    return;
            }
            final float[] u = {u1, u0, u0, u1};
            final float[] v = {vTop, vTop, vBottom, vBottom};
            final float shadedRed = red * shade;
            final float shadedGreen = green * shade;
            final float shadedBlue = blue * shade;

            for (int i = 0; i < 4; i++) {
                final float[] corner = corners[i];
                consumer.vertex(pose.pose(), corner[0], corner[1], corner[2])
                        .color(shadedRed, shadedGreen, shadedBlue, 1.0F)
                        .uv(u[i], v[i])
                        .uv2(light)
                        .normal(pose.normal(), normal[0], normal[1], normal[2])
                        .endVertex();
            }
        }

        private static void drawQuads(
                VertexConsumer consumer, PoseStack poseStack,
                List<BakedQuad> quads,
                net.minecraft.world.level.block.state.BlockState mimic, Level level, net.minecraft.core.BlockPos pos,
                int light, int overlay
        ) {
            final BlockColors blockColors = Minecraft.getInstance().getBlockColors();
            for (final BakedQuad quad : quads) {
                float red = 1.0F, green = 1.0F, blue = 1.0F;
                if (quad.isTinted()) {
                    final int color = blockColors.getColor(mimic, level, pos, quad.getTintIndex());
                    red = ((color >> 16) & 0xFF) / 255.0F;
                    green = ((color >> 8) & 0xFF) / 255.0F;
                    blue = (color & 0xFF) / 255.0F;
                }
                final float shade = shadeFor(quad.getDirection());
                consumer.putBulkData(poseStack.last(), quad, red * shade, green * shade, blue * shade, light, overlay);
            }
        }

        private static float shadeFor(net.minecraft.core.Direction direction) {
            if (direction == null) {
                return 1.0F;
            }
            switch (direction) {
                case UP:
                    return SHADE_UP;
                case DOWN:
                    return SHADE_DOWN;
                case NORTH:
                case SOUTH:
                    return SHADE_NORTH_SOUTH;
                case EAST:
                case WEST:
                    return SHADE_EAST_WEST;
                default:
                    return 1.0F;
            }
        }

        @Override
        public boolean isInRenderDistance(BlockEntity blockEntity, Vector3d position) {
            return true;
        }

    }

}
