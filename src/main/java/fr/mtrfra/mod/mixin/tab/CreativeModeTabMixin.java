package fr.mtrfra.mod.mixin.tab;

//? if >=1.20.1 {
import fr.mtrfra.mod.tab.CreativeTabSections;
import fr.mtrfra.mod.registry.ModItemGroups;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow
    private Collection<ItemStack> displayItems;

    @Inject(method = "buildContents", at = @At("TAIL"))
    private void mtrfra$sectionBanners(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        if (ModItemGroups.MAIN.identifier.equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey((CreativeModeTab) (Object) this))) {
            this.displayItems = CreativeTabSections.reorderAndPad(this.displayItems);
        }
    }

}
//? }

//? if =1.19.4 {
/*import fr.mtrfra.mod.tab.CreativeTabSections;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow
    private Collection<ItemStack> displayItems;

    @Inject(method = "buildContents", at = @At("TAIL"))
    private void mtrfra$sortItems(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        this.displayItems = CreativeTabSections.sortByPriority(this.displayItems);
    }

}
*///? }

//? if <1.19.4 {
/*import fr.mtrfra.mod.tab.CreativeTabSections;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Inject(method = "fillItemList", at = @At("RETURN"))
    private void mtrfra$sortItems(NonNullList<ItemStack> items, CallbackInfo ci) {
        items.sort(Comparator.comparingInt(stack -> CreativeTabSections.itemPriority().getOrDefault(stack.getItem(), Integer.MAX_VALUE)));
    }

}
*///? }
