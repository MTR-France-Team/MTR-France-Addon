package fr.mtrfra.mod.mixin.tab;

//? if >=1.20.1 {
import fr.mtrfra.mod.tab.CreativeTabSections;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMenuMixin {

    @Shadow
    protected abstract int getRowIndexForScroll(float scroll);

    @Inject(method = "scrollTo", at = @At("HEAD"))
    private void mtrfra$trackScrollRow(float scroll, CallbackInfo ci) {
        CreativeTabSections.currentRow = getRowIndexForScroll(scroll);
    }

}
//? } else {
/*import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class ItemPickerMenuMixin {

}
*///? }
