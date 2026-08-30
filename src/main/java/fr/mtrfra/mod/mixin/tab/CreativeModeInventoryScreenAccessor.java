package fr.mtrfra.mod.mixin.tab;

//? if >=1.20.1 {
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface CreativeModeInventoryScreenAccessor {

    @Accessor
    int getLeftPos();

    @Accessor
    int getTopPos();

}
//? } else {
/*import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerScreen.class)
public interface CreativeModeInventoryScreenAccessor {

}
*///? }
