package fr.mtrfra.mod.block.sign;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.ItemPlacementContext;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ShapeContext;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.TextFormatting;
import org.mtr.mapping.holder.TooltipContext;
import org.mtr.mapping.holder.VoxelShape;
import org.mtr.mapping.holder.VoxelShapes;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mod.block.BlockPoleCheckBase;
import org.mtr.mod.block.IBlock;

import java.util.List;

public class SNCFPoleBlock extends BlockPoleCheckBase {

    private static final double[] BOX_LEFT = {15, 0, 7.5, 16, 16, 8.5};
    private static final double[] BOX_RIGHT = {0, 0, 7.5, 1, 16, 8.5};

    public SNCFPoleBlock(BlockSettings blockSettings) {
        super(blockSettings);
    }

    @Override
    public BlockState getPlacementState2(ItemPlacementContext context) {
        final World world = context.getWorld();
        final BlockPos abovePos = context.getBlockPos().up();
        final BlockState aboveState = world.getBlockState(abovePos);
        final Object aboveBlock = aboveState.getBlock().data;

        if (aboveBlock instanceof SNCFPoleBlock) {
            return getDefaultState2()
                    .with(new Property<>(FACING.data), IBlock.getStatePropertySafe(aboveState, FACING).data)
                    .with(new Property<>(IBlock.SIDE.data), IBlock.getStatePropertySafe(aboveState, IBlock.SIDE));
        }

        final BlockPos anchorPos;
        if (aboveBlock instanceof RATPSignBase) {
            anchorPos = abovePos;
        } else if (aboveBlock instanceof RATPSignCollisionExtensionBlock) {
            anchorPos = RATPSignCollisionExtensionBlock.findSignAnchorPos(BlockView.cast(world), abovePos);
        } else {
            anchorPos = null;
        }
        if (anchorPos == null) {
            return null;
        }

        final BlockState anchorState = world.getBlockState(anchorPos);
        if (!(anchorState.getBlock().data instanceof RATPSignBase)) {
            return null;
        }

        final Direction facing = IBlock.getStatePropertySafe(anchorState, FACING);
        final IBlock.EnumSide side;
        if (abovePos.equals(anchorPos.offset(facing.rotateYClockwise()))) {
            side = IBlock.EnumSide.RIGHT;
        } else {
            side = IBlock.EnumSide.LEFT;
        }

        return getDefaultState2()
                .with(new Property<>(FACING.data), facing.data)
                .with(new Property<>(IBlock.SIDE.data), side);
    }

    private VoxelShape computeShape(BlockState state) {
        final Direction facing = IBlock.getStatePropertySafe(state, FACING);
        final double[] box = IBlock.getStatePropertySafe(state, IBlock.SIDE) == IBlock.EnumSide.RIGHT ? BOX_LEFT : BOX_RIGHT;
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
    protected boolean isBlock(Block block) {
        return block.data instanceof RATPSignBase || block.data instanceof RATPSignCollisionExtensionBlock || block.data instanceof SNCFPoleBlock;
    }

    @Override
    protected Text getTooltipBlockText() {
        return Text.cast(TextHelper.translatable("block.mtrfranceaddon.panneau_sncf_double"));
    }

    @Override
    public void addTooltips(ItemStack stack, BlockView world, List<MutableText> tooltip, TooltipContext options) {
        final String[] lines = TextHelper.translatable("tooltip.mtrfranceaddon.pole_placement", getTooltipBlockText().data).getString().split("\n");
        for (final String line : lines) {
            tooltip.add(TextHelper.literal(line).formatted(TextFormatting.GRAY));
        }
    }

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(FACING);
        properties.add(IBlock.SIDE);
    }

}
