package fr.mtrfra.mod.block.sign;

import fr.mtrfra.mod.registry.ModBlockEntities;
import fr.mtrfra.mod.util.ClientRedrawQueue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.mtr.mapping.holder.ActionResult;
import org.mtr.mapping.holder.BlockHitResult;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockSettings;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockWithEntity;

public class MotteLightBlock extends LightFixtureBlock implements BlockWithEntity {

    public MotteLightBlock(BlockSettings settings, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(settings, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BlockEntity(blockPos, blockState);
    }

    @Override
    public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty() || !(stack.getItem().data instanceof DyeItem dyeItem)) {
            return ActionResult.PASS;
        }
        if (!world.isClient()) {
            final org.mtr.mapping.holder.BlockEntity entity = world.getBlockEntity(pos);
            if (entity != null && entity.data instanceof BlockEntity motteLightEntity) {
                motteLightEntity.setColor(dyeItem.getDyeColor().getFireworkColor());
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    public static class BlockEntity extends BlockEntityExtension {

        private static final String KEY_COLOR = "color";

        private int color = 0xFFFFFF;

        public BlockEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntities.MOTTE_LIGHT.get(), blockPos, blockState);
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
            redraw();
        }

        private void redraw() {
            markDirty2();
            final Level level = (Level) getWorld2().data;
            if (level instanceof ServerLevel serverLevel) {
                final net.minecraft.core.BlockPos blockPos = (net.minecraft.core.BlockPos) getPos2().data;
                final Packet<?> updatePacket = getUpdatePacket();
                if (updatePacket != null) {
                    for (final ServerPlayer serverPlayer : serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(blockPos), false)) {
                        serverPlayer.connection.send(updatePacket);
                    }
                }
            }
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            compoundTag.putInt(KEY_COLOR, color);
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            color = compoundTag.contains(KEY_COLOR) ? compoundTag.getInt(KEY_COLOR) : 0xFFFFFF;
            try {
                final World world = getWorld2();
                final Object worldData = world == null ? null : world.data;
                if (worldData instanceof Level level && level.isClientSide()) {
                    ClientRedrawQueue.queue((net.minecraft.core.BlockPos) getPos2().data, level);
                }
            } catch (final Exception ignored) {}
        }

    }

}
