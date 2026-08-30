package fr.mtrfra.mod.sign;

import fr.mtrfra.mod.block.sign.RATPSignBase;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public final class PacketSaveSignConfig extends PacketHandler {

    private final int x;
    private final int y;
    private final int z;
    private final String title;
    private final String subtitle;
    private final int category;

    public PacketSaveSignConfig(BlockPos pos, String title, String subtitle, int category) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
    }

    public PacketSaveSignConfig(PacketBufferReceiver packetBufferReceiver) {
        x = packetBufferReceiver.readInt();
        y = packetBufferReceiver.readInt();
        z = packetBufferReceiver.readInt();
        title = packetBufferReceiver.readString();
        subtitle = packetBufferReceiver.readString();
        category = packetBufferReceiver.readInt();
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeInt(x);
        packetBufferSender.writeInt(y);
        packetBufferSender.writeInt(z);
        packetBufferSender.writeString(title);
        packetBufferSender.writeString(subtitle);
        packetBufferSender.writeInt(category);
    }

    @Override
    public void runServer(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        final World world = World.cast(serverPlayerEntity.getServerWorld());
        final BlockPos pos = new BlockPos(x, y, z);
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity != null && blockEntity.data instanceof RATPSignBase.BlockEntityBase)) {
            return;
        }
        final RATPSignBase.BlockEntityBase signBlockEntity = (RATPSignBase.BlockEntityBase) blockEntity.data;
        signBlockEntity.customTitle = title;
        signBlockEntity.subtitle = subtitle;
        signBlockEntity.markDirty2();
        final BlockState state = world.getBlockState(pos);
        final Object block = state.getBlock().data;
        if (block instanceof RATPSignBase signBlock) {
            world.setBlockState(pos, state.with(new Property<>(RATPSignBase.CATEGORY.data), Math.max(0, Math.min(signBlock.maxCategory(), category))), 3);
        }
    }

}
