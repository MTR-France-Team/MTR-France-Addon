include(Resources.id("aca:modules/i18n_handler.js"));

const LIVE_BG_TEXTURE = "aca:images/white.png";
const LIVE_HEADER_HEIGHT = 7.84;
const LIVE_HEADER_MARGIN = 1.5;
const LIVE_HEADER_SHADOW_HEIGHT = 1.5;

const LIVE_EDGE_OVERDRAW = 0.75;

const LIVE_DEPART_ICON_SIZE = (LIVE_HEADER_HEIGHT - (LIVE_HEADER_MARGIN * 2)) * 1.15;

const LIVE_CLOCK_SCALE = 0.45;
const LIVE_CLOCK_GAP = LIVE_CLOCK_SCALE * 2.75;
const LIVE_CLOCK_HOUR_GAP = LIVE_CLOCK_SCALE * 4;

const LIVE_LABEL_GAP = 2.5;

const LIVE_CLOCK_MARGIN_RIGHT = 3;

const LIVE_CLOCK_COLON_FADE_PERIOD = 2;
const LIVE_CLOCK_WHITE_PEAK_FRACTION = 0.45;

const LIVE_ZORDERS = { background: 0, header: 10, headerContent: 11, frame: 12 };

function liveHeaderTextY(scale) {
    const availableHeight = LIVE_HEADER_HEIGHT - (LIVE_HEADER_MARGIN * 2);
    return LIVE_HEADER_MARGIN + (availableHeight - RenderUtils.getCharHeight(scale)) / 2;
}

function drawLiveHeaderClock(ctx, pids, timestamp, headerColor) {
    const hhmm = RenderUtils.formatTime(timestamp).split(":");
    const hh = hhmm[0];
    const mm = hhmm[1];

    const wHours = RenderUtils.getStringWidth(hh, LIVE_CLOCK_SCALE);
    const wColon = RenderUtils.getStringWidth(":", LIVE_CLOCK_SCALE);
    const gap = LIVE_CLOCK_GAP;
    const hourGap = LIVE_CLOCK_HOUR_GAP;

    const endX = pids.width - LIVE_CLOCK_MARGIN_RIGHT;
    const totalWidth = wHours + hourGap + wColon + gap + RenderUtils.getStringWidth(mm, LIVE_CLOCK_SCALE);
    const startX = endX - totalWidth;

    const y = liveHeaderTextY(LIVE_CLOCK_SCALE);

    const cyclePos = ((timestamp / 1000) % LIVE_CLOCK_COLON_FADE_PERIOD) / LIVE_CLOCK_COLON_FADE_PERIOD;
    const whiteAmount = cyclePos <= LIVE_CLOCK_WHITE_PEAK_FRACTION
        ? cyclePos / LIVE_CLOCK_WHITE_PEAK_FRACTION
        : 1 - (cyclePos - LIVE_CLOCK_WHITE_PEAK_FRACTION) / (1 - LIVE_CLOCK_WHITE_PEAK_FRACTION);
    const colonColor = RenderUtils.blendColor(SncfColors.OnTimeWhite, headerColor, whiteAmount);

    Text.create("Header Clock Hours")
        .text(hh)
        .color(SncfColors.OnTimeWhite)
        .bold()
        .leftAlign()
        .scale(LIVE_CLOCK_SCALE)
        .pos(startX, y)
        .zOrder(LIVE_ZORDERS.headerContent)
        .draw(ctx);

    Text.create("Header Clock Separator")
        .text(":")
        .color(colonColor)
        .bold()
        .leftAlign()
        .scale(LIVE_CLOCK_SCALE)
        .pos(startX + wHours + hourGap, y)
        .zOrder(LIVE_ZORDERS.headerContent)
        .draw(ctx);

    Text.create("Header Clock Minutes")
        .text(mm)
        .color(SncfColors.OnTimeWhite)
        .bold()
        .leftAlign()
        .scale(LIVE_CLOCK_SCALE)
        .pos(startX + wHours + hourGap + wColon + gap, y)
        .zOrder(LIVE_ZORDERS.headerContent)
        .draw(ctx);
}

const LiveTheme = {

    t(state, key) {
        return I18nHandler.t(state, key);
    },

    resolveColors(isArrivals) {
        return {
            background: isArrivals ? SncfColors.LiveArrivalBackground : SncfColors.LiveBackground,
            header: isArrivals ? SncfColors.LiveArrivalHeader : SncfColors.LiveHeader
        };
    },

    drawBackground(ctx, pids, backgroundColor) {
        Texture.create("Background")
            .texture(LIVE_BG_TEXTURE)
            .color(backgroundColor)
            .pos(-LIVE_EDGE_OVERDRAW, -LIVE_EDGE_OVERDRAW)
            .size(pids.width + (LIVE_EDGE_OVERDRAW * 2), pids.height + (LIVE_EDGE_OVERDRAW * 2))
            .zOrder(LIVE_ZORDERS.background)
            .draw(ctx);
    },

    drawHeader(ctx, pids, headerColor, labelText, backgroundColor, drawShadow, isArrivals) {
        Texture.create("Header")
            .texture(LIVE_BG_TEXTURE)
            .color(headerColor)
            .pos(-LIVE_EDGE_OVERDRAW, -LIVE_EDGE_OVERDRAW)
            .size(pids.width + (LIVE_EDGE_OVERDRAW * 2), LIVE_HEADER_HEIGHT + LIVE_EDGE_OVERDRAW)
            .zOrder(LIVE_ZORDERS.header)
            .draw(ctx);

        if (drawShadow !== false) {
            Texture.create("Header Shadow")
                .texture("aca:images/bot_grad.png")
                .color(backgroundColor)
                .pos(0, LIVE_HEADER_HEIGHT)
                .size(pids.width, LIVE_HEADER_SHADOW_HEIGHT)
                .zOrder(LIVE_ZORDERS.header)
                .draw(ctx);
        }

        Texture.create("Depart Icon")
            .texture(isArrivals ? "sncf:images/arrival_icon.png" : "sncf:images/depart_icon.png")
            .pos(LIVE_HEADER_MARGIN, (LIVE_HEADER_HEIGHT - LIVE_DEPART_ICON_SIZE) / 2)
            .size(LIVE_DEPART_ICON_SIZE, LIVE_DEPART_ICON_SIZE)
            .zOrder(LIVE_ZORDERS.headerContent)
            .draw(ctx);

        Text.create("Header Label")
            .text(labelText)
            .color(SncfColors.OnTimeWhite)
            .bold()
            .leftAlign()
            .scale(LIVE_CLOCK_SCALE)
            .pos(LIVE_HEADER_MARGIN + LIVE_DEPART_ICON_SIZE + LIVE_LABEL_GAP, liveHeaderTextY(LIVE_CLOCK_SCALE))
            .zOrder(LIVE_ZORDERS.headerContent)
            .draw(ctx);

        drawLiveHeaderClock(ctx, pids, Timing.currentTimeMillis(), headerColor);
    },

    drawFrame(ctx, state, pids) {
        if (!ConfigHandler.get(state, "hideFrame")) {
            SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: LIVE_ZORDERS.frame });
        }
    }
};
