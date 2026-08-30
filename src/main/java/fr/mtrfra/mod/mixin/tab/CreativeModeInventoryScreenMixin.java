package fr.mtrfra.mod.mixin.tab;

//? if >=1.20.1 {
import fr.mtrfra.mod.tab.CreativeTabSections;
import fr.mtrfra.mod.registry.ModItemGroups;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

    @Shadow
    private static CreativeModeTab selectedTab;

    @Inject(method = "render", at = @At("TAIL"))
    private void mtrfra$renderSectionBanners(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (ModItemGroups.MAIN.identifier.equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab))) {
            final CreativeModeInventoryScreenAccessor accessor = (CreativeModeInventoryScreenAccessor) this;
            CreativeTabSections.renderBanners(guiGraphics, accessor.getLeftPos() + 8, accessor.getTopPos() + 17, mouseX, mouseY);
        }
    }

}
//? } else {
/*import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

}
*///? }
