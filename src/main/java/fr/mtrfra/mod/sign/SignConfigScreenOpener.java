package fr.mtrfra.mod.sign;

import fr.mtrfra.mod.block.sign.RATPSignBase;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Screen;

final class SignConfigScreenOpener {

    private SignConfigScreenOpener() {}

    static void open(BlockPos pos, String title, String subtitle, int category, int maxCategory, boolean hasSubtitle, RATPSignBase signBlock) {
        MinecraftClient.getInstance().openScreen(new Screen(new SignConfigScreen(pos, title, subtitle, category, maxCategory, hasSubtitle, signBlock)));
    }

}
