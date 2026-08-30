package fr.mtrfra.mod.barrier;

import fr.mtrfra.mod.registry.MTRFRARegistryClient;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.ClickableWidget;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.Screen;
import org.mtr.mapping.mapper.ButtonWidgetExtension;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.mapper.ScreenExtension;
import org.mtr.mapping.mapper.TextHelper;

public class TicketBarrierConfigScreen extends ScreenExtension {

    private static final int TITLE_SCALE = 2;
    private static final int TOP_OFFSET = 44;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 4;
    private static final int BOTTOM_ROW_GAP = 12;

    private final BlockPos pos;
    private TicketBarrierMode mode;

    public TicketBarrierConfigScreen(BlockPos pos, TicketBarrierMode mode, String acceptedTicketIdsCsv) {
        super(TextHelper.translatable("gui.mtrfranceaddon.ticket_barrier.config.title"));
        this.pos = pos;
        this.mode = mode;
    }

    private void switchMode(TicketBarrierMode newMode) {
        MinecraftClient.getInstance().openScreen(new Screen(new TicketBarrierConfigScreen(pos, newMode, "")));
    }

    @Override
    protected void init2() {
        super.init2();
        final int x = (width - BUTTON_WIDTH) / 2;

        addChild(new ClickableWidget(modeButton(x, TOP_OFFSET, TicketBarrierMode.MTR_BALANCE, "gui.mtrfranceaddon.ticket_barrier.config.mode_mtr_balance")));
        addChild(new ClickableWidget(modeButton(x, TOP_OFFSET + (BUTTON_HEIGHT + ROW_GAP), TicketBarrierMode.FREE_ENTRY, "gui.mtrfranceaddon.ticket_barrier.config.mode_free_entry")));

        final int y = TOP_OFFSET + 2 * (BUTTON_HEIGHT + ROW_GAP) + BOTTOM_ROW_GAP;
        final int pairWidth = (BUTTON_WIDTH - ROW_GAP) / 2;
        final ButtonWidgetExtension saveButton = new ButtonWidgetExtension(
                x, y, pairWidth, BUTTON_HEIGHT, TextHelper.translatable("gui.mtrfranceaddon.ticket_barrier.config.save"),
                pressed -> {
                    MTRFRARegistryClient.REGISTRY_CLIENT.sendPacketToServer(new PacketSaveTicketBarrierConfig(pos, mode, ""));
                    MinecraftClient.getInstance().openScreen(null);
                }
        );
        final ButtonWidgetExtension discardButton = new ButtonWidgetExtension(
                x + pairWidth + ROW_GAP, y, pairWidth, BUTTON_HEIGHT, TextHelper.translatable("gui.mtrfranceaddon.ticket_barrier.config.discard"),
                pressed -> MinecraftClient.getInstance().openScreen(null)
        );
        addChild(new ClickableWidget(saveButton));
        addChild(new ClickableWidget(discardButton));
    }

    private ButtonWidgetExtension modeButton(int x, int y, TicketBarrierMode target, String translationKey) {
        final MutableText label = mode == target
                ? TextHelper.append(TextHelper.literal("* "), TextHelper.translatable(translationKey))
                : TextHelper.translatable(translationKey);
        return new ButtonWidgetExtension(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, label, pressed -> {
            if (mode != target) {
                switchMode(target);
            }
        });
    }

    @Override
    public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
        renderBackground(graphicsHolder);

        final MutableText title = TextHelper.translatable("gui.mtrfranceaddon.ticket_barrier.config.title");
        graphicsHolder.push();
        graphicsHolder.translate(width / 2.0, 10, 0);
        graphicsHolder.scale(TITLE_SCALE, TITLE_SCALE, TITLE_SCALE);
        graphicsHolder.drawCenteredText(title, 0, 0, 0xFFFFFFFF);
        graphicsHolder.pop();

        super.render(graphicsHolder, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen2() {
        return false;
    }

}
