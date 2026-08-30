package fr.mtrfra.mod.tab;

import fr.mtrfra.mod.Init;
import fr.mtrfra.mod.registry.ModItemGroups;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.mtr.mapping.registry.BlockRegistryObject;
import org.mtr.mapping.registry.ItemRegistryObject;
//? if >=1.20.1 {
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.mtrfra.mod.util.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
//? }

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class CreativeTabSections {

    private static final List<Runnable> PENDING = new ArrayList<>();
    private static final Map<Item, CreativeTabSection> ITEM_SECTIONS = new LinkedHashMap<>();

    private static final List<Runnable> PRIORITY_PENDING = new ArrayList<>();
    private static final Map<Item, Integer> ITEM_PRIORITY = new LinkedHashMap<>();

    private CreativeTabSections() {}

    public static void recordBlock(BlockRegistryObject blockRegistryObject, CreativeTabSection section) {
        PENDING.add(() -> ITEM_SECTIONS.put((Item) blockRegistryObject.get().asItem().data, section));
    }

    public static void recordItem(ItemRegistryObject itemRegistryObject, CreativeTabSection section) {
        PENDING.add(() -> ITEM_SECTIONS.put((Item) itemRegistryObject.get().data, section));
    }

    private static Map<Item, CreativeTabSection> itemSections() {
        runAllUntilResolved(PENDING);
        return ITEM_SECTIONS;
    }

    public static void pinFirst(List<Supplier<Item>> orderedItems) {
        for (int i = 0; i < orderedItems.size(); i++) {
            final int priority = i;
            final Supplier<Item> supplier = orderedItems.get(i);
            PRIORITY_PENDING.add(() -> ITEM_PRIORITY.put(supplier.get(), priority));
        }
    }

    public static Map<Item, Integer> itemPriority() {
        runAllUntilResolved(PRIORITY_PENDING);
        return ITEM_PRIORITY;
    }

    public static List<ItemStack> sortByPriority(java.util.Collection<ItemStack> original) {
        final Map<Item, Integer> priority = itemPriority();
        final List<ItemStack> result = new ArrayList<>(original);
        result.sort(Comparator.comparingInt(stack -> priority.getOrDefault(stack.getItem(), Integer.MAX_VALUE)));
        return result;
    }

    private static void runAllUntilResolved(List<Runnable> pending) {
        final java.util.Iterator<Runnable> iterator = pending.iterator();
        while (iterator.hasNext()) {
            final Runnable runnable = iterator.next();
            try {
                runnable.run();
                iterator.remove();
            } catch (final Exception exception) {
                Init.LOGGER.warn("CreativeTabSections: registry object not ready yet, will retry", exception);
            }
        }
    }

    //? if >=1.20.1 {
    private static final int ROW_WIDTH = 9;
    private static final int ITEM_SIZE = 18;
    private static final int VISIBLE_ROWS = 5;

    private static final Map<CreativeTabSection, Integer> SECTION_ROWS = new LinkedHashMap<>();

    public static int currentRow = 0;

    public static List<ItemStack> reorderAndPad(java.util.Collection<ItemStack> original) {
        final Map<Item, CreativeTabSection> itemSections = itemSections();
        final Map<CreativeTabSection, List<ItemStack>> bySection = new LinkedHashMap<>();
        final List<ItemStack> unsectioned = new ArrayList<>();

        for (final ItemStack stack : original) {
            final CreativeTabSection section = itemSections.get(stack.getItem());

            if (section == null) {
                unsectioned.add(stack);
            } else {
                bySection.computeIfAbsent(section, ignored -> new ArrayList<>()).add(stack);
            }
        }

        final Map<Item, Integer> priority = itemPriority();
        for (final List<ItemStack> items : bySection.values()) {
            items.sort(Comparator.comparingInt(stack -> priority.getOrDefault(stack.getItem(), Integer.MAX_VALUE)));
        }

        SECTION_ROWS.clear();
        final List<ItemStack> result = new ArrayList<>();
        int row = 0;
        for (final CreativeTabSection section : ModItemGroups.SECTION_ORDER) {
            final List<ItemStack> items = bySection.get(section);

            if (items == null || items.isEmpty()) {
                continue;
            }

            SECTION_ROWS.put(section, row);
            for (int i = 0; i < ROW_WIDTH; i++) {
                result.add(ItemStack.EMPTY);
            }
            row++;

            result.addAll(items);

            final int rowCount = (items.size() + ROW_WIDTH - 1) / ROW_WIDTH;
            row += rowCount;

            final int padding = (ROW_WIDTH - items.size() % ROW_WIDTH) % ROW_WIDTH;
            for (int i = 0; i < padding; i++) {
                result.add(ItemStack.EMPTY);
            }
        }
        result.addAll(unsectioned);
        return result;
    }

    private static final Map<String, ResourceLocation> BANNER_TEXTURES = new LinkedHashMap<>();

    private static ResourceLocation bannerTexture(String texturePath) {
        return BANNER_TEXTURES.computeIfAbsent(texturePath, path -> new ResourceLocation(Constants.MOD_ID, "textures/gui/sprites/" + path + ".png"));
    }

    public static void renderBanners(GuiGraphics graphics, int left, int top, int mouseX, int mouseY) {
        for (final Map.Entry<CreativeTabSection, Integer> entry : SECTION_ROWS.entrySet()) {
            final int sectionRow = entry.getValue() - currentRow;

            if (sectionRow < 0 || sectionRow >= VISIBLE_ROWS) {
                continue;
            }

            final CreativeTabSection section = entry.getKey();
            final int x = left;
            final int y = top + sectionRow * ITEM_SIZE;
            final int width = ROW_WIDTH * ITEM_SIZE;
            final Component text = (Component) section.title().data;
            final int textWidth = Minecraft.getInstance().font.width(text);

            graphics.blit(bannerTexture(section.texturePath()), x, y, 0.0f, 0.0f, width, ITEM_SIZE, width, ITEM_SIZE);
            graphics.fill(x + 2, y + 2, x + textWidth + 8, y + ITEM_SIZE - 2, section.backgroundColor());
            drawAuraText(graphics, text, x + 5, y + 5, section.titleColor(), darken(section.titleColor(), 0.2f));
        }
    }

    private static int darken(int argb, float factor) {
        final int a = argb >>> 24;
        final int r = (int) (((argb >> 16) & 0xFF) * (1 - factor));
        final int g = (int) (((argb >> 8) & 0xFF) * (1 - factor));
        final int b = (int) ((argb & 0xFF) * (1 - factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void drawAuraText(GuiGraphics graphics, Component text, int x, int y, int color, int auraColor) {
        final Font font = Minecraft.getInstance().font;
        final Window window = Minecraft.getInstance().getWindow();
        final float guiScale = (float) window.getGuiScale();

        graphics.drawString(font, text, x, y, auraColor, true);

        final PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 1);

        final Matrix4f matrix = pose.last().pose();
        final Vector3f topLeft = matrix.transformPosition(new Vector3f(x, y, 0));
        final Vector3f bottomRight = matrix.transformPosition(new Vector3f(x + font.width(text), y + 9.0f / 1.8f, 0));
        topLeft.mul(guiScale);
        bottomRight.mul(guiScale);

        final int scissorHeight = (int) (bottomRight.y - topLeft.y);
        final int scissorWidth = (int) (bottomRight.x - topLeft.x);

        RenderSystem.enableScissor((int) topLeft.x, window.getHeight() - (int) topLeft.y - scissorHeight, scissorWidth, scissorHeight);
        graphics.drawString(font, text, x, y, color, false);
        RenderSystem.disableScissor();
        pose.popPose();
    }
    //? }

}