package fr.mtrfra.mod.block.sign;

import fr.mtrfra.mod.block.base.DirectionalBlock;
import fr.mtrfra.mod.sign.PacketOpenSignConfigScreen;
import fr.mtrfra.mod.registry.MTRFRARegistry;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.IntegerProperty;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.LivingEntity;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockWithEntity;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.InitClient;
import org.mtr.mod.Items;
import org.mtr.mod.block.IBlock;

import java.util.List;

public class RATPSignBase extends DirectionalBlock implements BlockWithEntity, HasBoundingBox {

    public static final IntegerProperty CATEGORY = IntegerProperty.of("category", 0, 7);

    private final int maxCategory;
    private final TextLayout[] textLayouts;
    private final float textZ;
    private final double[] boundingBox;
    private final boolean doubleSided;
    private final boolean hasSubtitle;
    private final float extraTitleMargin;
    private final Identifier[] categoryTextures;
    private final Identifier[] categoryLogoOverlays;
    private final float[] previewUv;
    private final float[] previewPlateBounds;
    private final float[] previewLogoOverlayRect;
    private final TextAlignment textAlignment;

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided) {
        this(blockSettings, maxCategory, textLayouts, textZ, boundingBox, doubleSided, true, 0F, new Identifier[0], new float[]{0F, 0F, 1F, 1F}, new float[]{0F, 0F, 16F, 16F});
    }

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided, boolean hasSubtitle) {
        this(blockSettings, maxCategory, textLayouts, textZ, boundingBox, doubleSided, hasSubtitle, 0F, new Identifier[0], new float[]{0F, 0F, 1F, 1F}, new float[]{0F, 0F, 16F, 16F});
    }

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided, boolean hasSubtitle, float extraTitleMargin) {
        this(blockSettings, maxCategory, textLayouts, textZ, boundingBox, doubleSided, hasSubtitle, extraTitleMargin, new Identifier[0], new float[]{0F, 0F, 1F, 1F}, new float[]{0F, 0F, 16F, 16F});
    }

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided, boolean hasSubtitle, float extraTitleMargin, Identifier[] categoryTextures, float[] previewUv, float[] previewPlateBounds) {
        this(blockSettings, maxCategory, textLayouts, textZ, boundingBox, doubleSided, hasSubtitle, extraTitleMargin, categoryTextures, previewUv, previewPlateBounds, new Identifier[0], new float[]{0F, 0F, 0F, 0F});
    }

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided, boolean hasSubtitle, float extraTitleMargin, Identifier[] categoryTextures, float[] previewUv, float[] previewPlateBounds, Identifier[] categoryLogoOverlays, float[] previewLogoOverlayRect) {
        this(blockSettings, maxCategory, textLayouts, textZ, boundingBox, doubleSided, hasSubtitle, extraTitleMargin, categoryTextures, previewUv, previewPlateBounds, categoryLogoOverlays, previewLogoOverlayRect, TextAlignment.CENTER);
    }

    public RATPSignBase(BlockSettings blockSettings, int maxCategory, TextLayout[] textLayouts, float textZ, double[] boundingBox, boolean doubleSided, boolean hasSubtitle, float extraTitleMargin, Identifier[] categoryTextures, float[] previewUv, float[] previewPlateBounds, Identifier[] categoryLogoOverlays, float[] previewLogoOverlayRect, TextAlignment textAlignment) {
        super(blockSettings);
        setDefaultState2(getDefaultState2().with(new Property<>(CATEGORY.data), 0));
        this.maxCategory = maxCategory;
        this.textLayouts = textLayouts;
        this.textZ = textZ;
        this.boundingBox = boundingBox;
        this.doubleSided = doubleSided;
        this.hasSubtitle = hasSubtitle;
        this.extraTitleMargin = extraTitleMargin;
        this.categoryTextures = categoryTextures;
        this.categoryLogoOverlays = categoryLogoOverlays;
        this.previewUv = previewUv;
        this.previewPlateBounds = previewPlateBounds;
        this.previewLogoOverlayRect = previewLogoOverlayRect;
        this.textAlignment = textAlignment;
    }

    public int maxCategory() {
        return maxCategory;
    }

    public boolean hasSubtitle() {
        return hasSubtitle;
    }

    public float extraTitleMargin() {
        return extraTitleMargin;
    }

    public Identifier categoryTexture(int category) {
        return category >= 0 && category < categoryTextures.length ? categoryTextures[category] : null;
    }

    public Identifier categoryLogoOverlay(int category) {
        return category >= 0 && category < categoryLogoOverlays.length ? categoryLogoOverlays[category] : null;
    }

    public float[] previewUv() {
        return previewUv;
    }

    public float[] previewPlateBounds() {
        return previewPlateBounds;
    }

    public float[] previewLogoOverlayRect() {
        return previewLogoOverlayRect;
    }

    public TextLayout textLayout(int category) {
        return textLayouts[category];
    }

    public float textZ() {
        return textZ;
    }

    @Override
    public double[] boundingBox() {
        return boundingBox;
    }

    public boolean doubleSided() {
        return doubleSided;
    }

    public TextAlignment textAlignment() {
        return textAlignment;
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntityBase(blockPos, blockState);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final BlockState state = super.getPlacementState2(context);
        if (state == null) {
            return null;
        }
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        if (!RATPSignCollisionExtensionBlock.canPlaceAround(context, context.getBlockPos(), facing, boundingBox())) {
            return null;
        }
        return state;
    }

    @Override
    public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced2(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            RATPSignCollisionExtensionBlock.placeAround(world, pos, IBlock.getStatePropertySafe(state, FACING), boundingBox());
        }
    }

    @Override
    public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            RATPSignCollisionExtensionBlock.removeAround(world, pos, IBlock.getStatePropertySafe(state, FACING), boundingBox());
        }
        super.onBreak2(world, pos, state, player);
    }

    private VoxelShape computeShape(BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final double[] box = boundingBox();
        return IBlock.getVoxelShapeByDirection(box[0], box[1], box[2], box[3], box[4], box[5], facing);
    }

    @Override
    public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(state);
    }

    @Override
    public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return computeShape(state);
    }

    @Override
    public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        super.addBlockProperties(properties);
        properties.add(CATEGORY);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).getItem().equals(Items.BRUSH.get().asItem())) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            final BlockEntityBase signBlockEntity = blockEntity != null && blockEntity.data instanceof BlockEntityBase ? (BlockEntityBase) blockEntity.data : null;
            final String customTitle = signBlockEntity == null ? "" : signBlockEntity.customTitle;
            final String subtitle = signBlockEntity == null ? "" : signBlockEntity.subtitle;
            final int category = org.mtr.mod.block.IBlock.getStatePropertySafe(state, CATEGORY);
            MTRFRARegistry.REGISTRY.sendPacketToClient(ServerPlayerEntity.cast(player), new PacketOpenSignConfigScreen(pos, customTitle, subtitle, category, maxCategory(), hasSubtitle));
        }
        return ActionResult.SUCCESS;
    }

    public static final class TextLayout {

        public final float titleX;
        public final float titleY;
        public final float subtitleX;
        public final float subtitleY;
        public final boolean hasSubtitlePosition;
        public final float titleMaxWidth;

        public TextLayout(float titleX, float titleY) {
            this(titleX, titleY, titleX, titleY, false, -1F);
        }

        public TextLayout(float titleX, float titleY, float titleMaxWidth) {
            this(titleX, titleY, titleX, titleY, false, titleMaxWidth);
        }

        public TextLayout(float titleX, float titleY, float subtitleX, float subtitleY) {
            this(titleX, titleY, subtitleX, subtitleY, true, -1F);
        }

        private TextLayout(float titleX, float titleY, float subtitleX, float subtitleY, boolean hasSubtitlePosition, float titleMaxWidth) {
            this.titleX = titleX;
            this.titleY = titleY;
            this.subtitleX = subtitleX;
            this.subtitleY = subtitleY;
            this.hasSubtitlePosition = hasSubtitlePosition;
            this.titleMaxWidth = titleMaxWidth;
        }

    }

    public static class BlockEntityBase extends BlockEntityExtension {

        private static final String KEY_CUSTOM_TITLE = "custom_title";
        private static final String KEY_SUBTITLE = "subtitle";

        public String customTitle = "";
        public String subtitle = "";

        public BlockEntityBase(BlockPos pos, BlockState state) {
            super(fr.mtrfra.mod.registry.ModBlockEntities.RATP_SIGN.get(), pos, state);
        }

        public String getEffectiveTitle() {
            if (!customTitle.isEmpty()) {
                return customTitle;
            }
            final org.mtr.core.data.Station station = InitClient.findStation(getPos2());
            return station != null ? station.getName() : "";
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            customTitle = compoundTag.getString(KEY_CUSTOM_TITLE);
            subtitle = compoundTag.getString(KEY_SUBTITLE);
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            compoundTag.putString(KEY_CUSTOM_TITLE, customTitle);
            compoundTag.putString(KEY_SUBTITLE, subtitle);
        }

    }

}
