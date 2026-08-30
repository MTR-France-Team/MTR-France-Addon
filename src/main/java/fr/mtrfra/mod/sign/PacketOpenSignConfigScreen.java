package fr.mtrfra.mod.sign;

import fr.mtrfra.mod.block.sign.RATPSignBase;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

public final class PacketOpenSignConfigScreen extends PacketHandler {

    private final int x;
    private final int y;
    private final int z;
    private final String title;
    private final String subtitle;
    private final int category;
    private final int maxCategory;
    private final boolean hasSubtitle;

    public PacketOpenSignConfigScreen(BlockPos pos, String title, String subtitle, int category, int maxCategory, boolean hasSubtitle) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.maxCategory = maxCategory;
        this.hasSubtitle = hasSubtitle;
    }

    public PacketOpenSignConfigScreen(PacketBufferReceiver packetBufferReceiver) {
        x = packetBufferReceiver.readInt();
        y = packetBufferReceiver.readInt();
        z = packetBufferReceiver.readInt();
        title = packetBufferReceiver.readString();
        subtitle = packetBufferReceiver.readString();
        category = packetBufferReceiver.readInt();
        maxCategory = packetBufferReceiver.readInt();
        hasSubtitle = packetBufferReceiver.readInt() != 0;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeInt(x);
        packetBufferSender.writeInt(y);
        packetBufferSender.writeInt(z);
        packetBufferSender.writeString(title);
        packetBufferSender.writeString(subtitle);
        packetBufferSender.writeInt(category);
        packetBufferSender.writeInt(maxCategory);
        packetBufferSender.writeInt(hasSubtitle ? 1 : 0);
    }

    @Override
    public void runClient() {
        final BlockPos pos = new BlockPos(x, y, z);
        final ClientWorld world = MinecraftClient.getInstance().getWorldMapped();
        final Object block = world == null ? null : world.getBlockState(pos).getBlock().data;
        final RATPSignBase signBlock = block instanceof RATPSignBase ? (RATPSignBase) block : null;
        SignConfigScreenOpener.open(pos, title, subtitle, category, maxCategory, hasSubtitle, signBlock);
    }

}
