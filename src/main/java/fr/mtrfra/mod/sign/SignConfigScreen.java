package fr.mtrfra.mod.sign;

import fr.mtrfra.mod.block.sign.RATPSignBase;
import fr.mtrfra.mod.block.sign.TextAlignment;
import fr.mtrfra.mod.registry.MTRFRARegistryClient;
import org.mtr.core.data.Station;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.TextFieldWidget;
import org.mtr.mapping.holder.TextFormatting;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.GuiDrawing;
import org.mtr.mapping.mapper.ScreenExtension;
import org.mtr.mapping.mapper.TextHelper;
import org.mtr.mod.InitClient;

public class SignConfigScreen extends ScreenExtension {

    private static final int FIELD_WIDTH = 200;
    private static final int FIELD_HEIGHT = 20;
    private static final int PANEL_PADDING = 10;
    private static final int ROW_GAP = 4;

    private final BlockPos pos;
    private final int maxCategory;
    private final boolean hasSubtitle;
    private final RATPSignBase signBlock;
    private final String initialTitle;
    private final String initialSubtitle;
    private int category;

    private TextFieldWidget titleField;
    private TextFieldWidget subtitleField;
    private int categoryButtonX;
    private int categoryButtonY;
    private int panelLeft;
    private int panelWidth;
    private int panelBottom;
    private int previewY;
    private int previewHeight;
    private int titleLabelY;
    private int subtitleLabelY;
    private int styleLabelY;

    public SignConfigScreen(BlockPos pos, String title, String subtitle, int category, int maxCategory, boolean hasSubtitle, RATPSignBase signBlock) {
        super(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.title"));
        this.pos = pos;
        this.category = category;
        this.maxCategory = maxCategory;
        this.hasSubtitle = hasSubtitle;
        this.signBlock = signBlock;
        this.initialTitle = title;
        this.initialSubtitle = subtitle;
    }

    @Override
    protected void init2() {
        super.init2();
        final int x = (width - FIELD_WIDTH) / 2;
        panelLeft = x - PANEL_PADDING;
        panelWidth = FIELD_WIDTH + PANEL_PADDING * 2;
        previewY = 32 + PANEL_PADDING;
        previewHeight = Math.max(20, Math.min(80, Math.round(FIELD_WIDTH / previewAspect())));

        int y = previewY + previewHeight + PANEL_PADDING;

        titleLabelY = y;
        y += 10;
        titleField = new TextFieldWidget(getTextRendererMapped(), x, y, FIELD_WIDTH, FIELD_HEIGHT, org.mtr.mapping.holder.Text.cast(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.title_field")));
        titleField.setMaxLength(64);
        titleField.setText(initialTitle);
        addChild(new ClickableWidget(titleField.data));
        y += FIELD_HEIGHT + ROW_GAP;

        if (hasSubtitle) {
            subtitleLabelY = y;
            y += 10;
            subtitleField = new TextFieldWidget(getTextRendererMapped(), x, y, FIELD_WIDTH, FIELD_HEIGHT, org.mtr.mapping.holder.Text.cast(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.subtitle_field")));
            subtitleField.setMaxLength(64);
            subtitleField.setText(initialSubtitle);
            addChild(new ClickableWidget(subtitleField.data));
            y += FIELD_HEIGHT + ROW_GAP;
        }

        if (maxCategory > 0) {
            styleLabelY = y;
            y += 10;
            categoryButtonX = x;
            categoryButtonY = y;
            addChild(new ClickableWidget(new ButtonWidgetExtension(
                    categoryButtonX, categoryButtonY, FIELD_WIDTH, FIELD_HEIGHT,
                    TextHelper.translatable("gui.mtrfranceaddon.panneau.config.category." + category),
                    pressed -> cycleCategoryForward()
            )));
            y += FIELD_HEIGHT + ROW_GAP;
        }

        y += 12;
        final int pairWidth = (FIELD_WIDTH - ROW_GAP) / 2;
        addChild(new ClickableWidget(new ButtonWidgetExtension(
                x, y, pairWidth, FIELD_HEIGHT, TextHelper.translatable("gui.mtrfranceaddon.panneau.config.save"),
                pressed -> save()
        )));
        addChild(new ClickableWidget(new ButtonWidgetExtension(
                x + pairWidth + ROW_GAP, y, pairWidth, FIELD_HEIGHT, TextHelper.translatable("gui.mtrfranceaddon.panneau.config.discard"),
                pressed -> MinecraftClient.getInstance().openScreen(null)
        )));

        panelBottom = y + FIELD_HEIGHT + PANEL_PADDING;
    }

    private void cycleCategoryForward() {
        category = category >= maxCategory ? 0 : category + 1;
        reopen();
    }

    private void cycleCategoryBackward() {
        category = category <= 0 ? maxCategory : category - 1;
        reopen();
    }

    private void reopen() {
        MinecraftClient.getInstance().openScreen(new org.mtr.mapping.holder.Screen(new SignConfigScreen(pos, titleField.getText(), subtitleText(), category, maxCategory, hasSubtitle, signBlock)));
    }

    private String subtitleText() {
        return subtitleField == null ? "" : subtitleField.getText();
    }

    @Override
    public boolean mouseClicked2(double mouseX, double mouseY, int button) {
        if (button == 1 && maxCategory > 0
                && mouseX >= categoryButtonX && mouseX < categoryButtonX + FIELD_WIDTH
                && mouseY >= categoryButtonY && mouseY < categoryButtonY + FIELD_HEIGHT) {
            cycleCategoryBackward();
            return true;
        }
        return super.mouseClicked2(mouseX, mouseY, button);
    }

    @Override
    public void onClose2() {
        save();
    }

    private void save() {
        MTRFRARegistryClient.REGISTRY_CLIENT.sendPacketToServer(new PacketSaveSignConfig(pos, titleField.getText(), subtitleText(), category));
        MinecraftClient.getInstance().openScreen(null);
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        renderBackground(graphicsHolder);

        graphicsHolder.push();
        graphicsHolder.translate(width / 2.0, 12, 0);
        graphicsHolder.scale(1.5F, 1.5F, 1.5F);
        graphicsHolder.drawCenteredText(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.title"), 0, 0, 0xFFFFFFFF);
        graphicsHolder.pop();

        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        guiDrawing.beginDrawingRectangle();
        guiDrawing.drawRectangle(panelLeft, 32, panelLeft + panelWidth, panelBottom, 0x90404040);
        guiDrawing.finishDrawingRectangle();

        drawPreview(graphicsHolder);

        graphicsHolder.drawText(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.title_field"), panelLeft + PANEL_PADDING, titleLabelY, 0xA0A0A0, false, GraphicsHolder.getDefaultLight());
        if (hasSubtitle) {
            graphicsHolder.drawText(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.subtitle_field"), panelLeft + PANEL_PADDING, subtitleLabelY, 0xA0A0A0, false, GraphicsHolder.getDefaultLight());
        }
        if (maxCategory > 0) {
            graphicsHolder.drawText(TextHelper.translatable("gui.mtrfranceaddon.panneau.config.style_field"), panelLeft + PANEL_PADDING, styleLabelY, 0xA0A0A0, false, GraphicsHolder.getDefaultLight());
        }

        super.render(graphicsHolder, mouseX, mouseY, delta);
    }

    private float previewAspect() {
        if (signBlock == null) {
            return 3F;
        }
        final float[] plate = signBlock.previewPlateBounds();
        return (plate[2] - plate[0]) / (plate[3] - plate[1]);
    }

    private void drawPreview(GraphicsHolder graphicsHolder) {
        final int previewLeft = panelLeft + PANEL_PADDING;
        final int previewRight = panelLeft + panelWidth - PANEL_PADDING;
        final Identifier previewTexture = signBlock == null ? null : signBlock.categoryTexture(category);

        final GuiDrawing guiDrawing = new GuiDrawing(graphicsHolder);
        if (previewTexture != null) {
            final float[] uv = signBlock.previewUv();
            guiDrawing.beginDrawingTexture(texturePath(previewTexture));
            guiDrawing.drawTexture(previewLeft, previewY, previewRight, previewY + previewHeight, uv[0], uv[1], uv[2], uv[3]);
            guiDrawing.finishDrawingTexture();
        } else {
            guiDrawing.beginDrawingRectangle();
            guiDrawing.drawRectangle(previewLeft, previewY, previewRight, previewY + previewHeight, 0xFF1A1A1A);
            guiDrawing.finishDrawingRectangle();
        }

        final Identifier logoOverlay = signBlock == null ? null : signBlock.categoryLogoOverlay(category);
        if (logoOverlay != null) {
            final float[] rect = signBlock.previewLogoOverlayRect();
            final int overlayLeft = previewLeft + Math.round(rect[0] * (previewRight - previewLeft));
            final int overlayRight = previewLeft + Math.round(rect[2] * (previewRight - previewLeft));
            final int overlayTop = previewY + Math.round(rect[1] * previewHeight);
            final int overlayBottom = previewY + Math.round(rect[3] * previewHeight);
            guiDrawing.beginDrawingTexture(texturePath(logoOverlay));
            guiDrawing.drawTexture(overlayLeft, overlayTop, overlayRight, overlayBottom, 0F, 0F, 1F, 1F);
            guiDrawing.finishDrawingTexture();
        }

        final String customTitle = titleField == null ? initialTitle : titleField.getText();
        final String subtitle = subtitleField == null ? initialSubtitle : subtitleField.getText();
        final String title = customTitle.trim().isEmpty() ? stationName() : customTitle;
        final boolean hasTitle = !title.trim().isEmpty();
        final boolean hasPreviewSubtitle = hasSubtitle && !subtitle.trim().isEmpty();
        final RATPSignBase.TextLayout layout = signBlock == null ? null : signBlock.textLayout(category);
        final boolean stacked = layout != null && hasTitle && hasPreviewSubtitle && !layout.hasSubtitlePosition;
        final float titleHeightRaw = 9F * (1F / 32F) * 16F;
        final float subtitleHeightRaw = 9F * (1F / 125F) * 16F;
        final float blockHalfHeightRaw = (titleHeightRaw + subtitleHeightRaw) / 2F;

        final int centerX = panelLeft + panelWidth / 2;
        final int titleScreenX;
        final int titleScreenY;
        if (layout != null) {
            final float[] plate = signBlock.previewPlateBounds();
            final float titleYRaw = stacked ? layout.titleY + blockHalfHeightRaw - titleHeightRaw / 2F + 0.2F : layout.titleY;
            titleScreenX = previewLeft + Math.round(mirroredFraction(layout.titleX, plate[0], plate[2]) * (previewRight - previewLeft));
            titleScreenY = previewY + Math.round((1F - fraction(titleYRaw, plate[1], plate[3])) * previewHeight) - 4;
        } else {
            titleScreenX = centerX;
            titleScreenY = previewY + previewHeight / 2 - 4;
        }

        final float titlePreviewScale = 1.0F;
        final TextAlignment alignment = signBlock == null ? TextAlignment.CENTER : signBlock.textAlignment();
        graphicsHolder.push();
        graphicsHolder.translate(titleScreenX, titleScreenY, 0);
        graphicsHolder.scale(titlePreviewScale, titlePreviewScale, titlePreviewScale);
        if (hasTitle) {
            if (alignment == TextAlignment.CENTER) {
                graphicsHolder.drawCenteredText(title, 0, 0, 0xFFFFFFFF);
            } else {
                graphicsHolder.drawText(title, (int) alignment.getX(GraphicsHolder.getTextWidth(title)), 0, 0xFFFFFFFF, false, GraphicsHolder.getDefaultLight());
            }
        } else {
            final MutableText placeholder = TextHelper.translatable("gui.mtrfranceaddon.panneau.config.preview_placeholder");
            if (alignment == TextAlignment.CENTER) {
                graphicsHolder.drawCenteredText(placeholder, 0, 0, 0xFF808080);
            } else {
                graphicsHolder.drawText(placeholder, (int) alignment.getX(GraphicsHolder.getTextWidth(placeholder)), 0, 0xFF808080, false, GraphicsHolder.getDefaultLight());
            }
        }
        graphicsHolder.pop();

        if (hasPreviewSubtitle) {
            final MutableText formattedSubtitle = TextHelper.literal(subtitle).formatted(TextFormatting.ITALIC);
            final int subtitleScreenX;
            final int subtitleScreenY;
            final float subtitleScale;
            if (layout != null && layout.hasSubtitlePosition) {
                final float[] plate = signBlock.previewPlateBounds();
                subtitleScreenX = previewLeft + Math.round(mirroredFraction(layout.subtitleX, plate[0], plate[2]) * (previewRight - previewLeft));
                subtitleScreenY = previewY + Math.round((1F - fraction(layout.subtitleY, plate[1], plate[3])) * previewHeight - 1.9F);
                subtitleScale = 0.5F;
            } else if (layout != null) {
                final float[] plate = signBlock.previewPlateBounds();
                final float subtitleYRaw = layout.titleY - blockHalfHeightRaw + subtitleHeightRaw / 2F - 0.2F;
                subtitleScreenX = titleScreenX;
                subtitleScreenY = previewY + Math.round((1F - fraction(subtitleYRaw, plate[1], plate[3])) * previewHeight) - 3;
                subtitleScale = 0.7F;
            } else {
                subtitleScreenX = titleScreenX;
                subtitleScreenY = titleScreenY + 10;
                subtitleScale = 0.7F;
            }
            graphicsHolder.push();
            graphicsHolder.translate(subtitleScreenX, subtitleScreenY, 0);
            graphicsHolder.scale(subtitleScale, subtitleScale, subtitleScale);
            graphicsHolder.drawCenteredText(formattedSubtitle, 0, 0, 0xFFFFFFFF);
            graphicsHolder.pop();
        }
    }

    private static float fraction(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    private static float mirroredFraction(float value, float min, float max) {
        return 1F - fraction(value, min, max);
    }

    private String stationName() {
        final Station station = InitClient.findStation(pos);
        return station == null ? "" : station.getName();
    }

    private static Identifier texturePath(Identifier modelTextureRef) {
        return new Identifier(modelTextureRef.getNamespace(), "textures/" + modelTextureRef.getPath() + ".png");
    }

}
