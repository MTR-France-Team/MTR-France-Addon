
include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:components/train_composition.js"));
include(Resources.id("aca:components/advertising_manager.js"));

const CFG = {
    leftPanelWidth: 383 * (136 / 1100),
    frameHeightReduction: 9.8,
    comp: {
        colors: { background: 0x0A2C6B, passengerCar: 0xAFC7EA, cab: 0x2B79C2 },
        naturalHeight: 23,
        verticalNudge: 2,
        zOrders: { bg: 3, mid: 4, marker: 5, letter: 6 }
    },
    leftPanel: { contentScale: 0.5, inactiveMultiplier: 1.5 },
    z: { bg: 0, left: 2, infoBandBg: 7, seamBorder: 8, infoBand: 9, clockBox: 10, clock: 11, frame: 12 },
    panelColor: { departure: 0x014494, arrival: 0x006E30 },
    watermark: {
        refWidth: 1100, refHeight: 616,
        states: {
            departure: { texture: "sncf:images/platform/departure.png", color: 0xD8DCE6, nativeWidth: 285, nativeHeight: 824, visibleHeight: 370, marginRight: 5, marginBottom: 5 },
            arrival: { texture: "sncf:images/platform/arrival.png", color: 0xD5E5C3, nativeWidth: 225, nativeHeight: 816, widthScale: 0.85, visibleHeight: 350, marginRight: 30, marginBottom: 5 }
        },
        extraMarginRight: 1, sizeMultiplier: 1.40625, shiftRight: 0.7
    },
    seamBorderHeight: 0.5,
    important: { textScale: 0.275, lineGap: 1.5, textMaxWidth: 158.675, marginX: 2, marginTop: 2.17 },
    hourScale: 0.85, statusScale: 0.42,
    layout: { contentTop: 2, iconHourGap: 3.5, hourStatusGap: 3, statusTrainInfoGap: 9, iconHeight: 6 }
};
CFG.frameOffsetY = CFG.frameHeightReduction / 2;
CFG.layout.iconY = CFG.layout.contentTop;
CFG.layout.hourY = CFG.layout.iconY + CFG.layout.iconHeight + CFG.layout.iconHourGap;
CFG.layout.statusY = CFG.layout.hourY + (7 * CFG.hourScale) + CFG.layout.hourStatusGap;
CFG.layout.trainInfoY = CFG.layout.statusY + (7 * CFG.statusScale) + CFG.layout.statusTrainInfoGap;

function drawStateBackground(ctx, x, y, width, height, isTerminating) {
    rect(ctx, x, y, CFG.leftPanelWidth, height, 0xFFFFFF, CFG.z.bg);
    rect(ctx, x + CFG.leftPanelWidth, y, width - CFG.leftPanelWidth, height, isTerminating ? CFG.panelColor.arrival : CFG.panelColor.departure, CFG.z.bg);

    const xScale = width / CFG.watermark.refWidth;
    const yScale = height / CFG.watermark.refHeight;
    const wmCfg = isTerminating ? CFG.watermark.states.arrival : CFG.watermark.states.departure;
    const wmWidthScale = wmCfg.widthScale != null ? wmCfg.widthScale : 1;
    const wmHeight = wmCfg.visibleHeight * yScale * CFG.watermark.sizeMultiplier;
    const wmWidth = wmHeight * (wmCfg.nativeWidth / wmCfg.nativeHeight) * wmWidthScale;
    const marginRight = (wmCfg.marginRight * xScale) + CFG.watermark.extraMarginRight;
    const marginBottom = wmCfg.marginBottom * yScale;

    img(ctx, wmCfg.texture, x + CFG.leftPanelWidth - marginRight - wmWidth + CFG.watermark.shiftRight, y + height - marginBottom - wmHeight, wmWidth, wmHeight, wmCfg.color, CFG.z.bg + 1);
}

function drawSeamBorder(ctx, x, y, width, isTerminating) {
    rect(ctx, x, y, CFG.leftPanelWidth, CFG.seamBorderHeight, isTerminating ? CFG.panelColor.arrival : CFG.panelColor.departure, CFG.z.seamBorder);
    rect(ctx, x + CFG.leftPanelWidth, y, width - CFG.leftPanelWidth, CFG.seamBorderHeight, 0xFFFFFF, CFG.z.seamBorder);
}

function create(ctx, state, pids) {
    ConfigHandler.init(state);
    SncfTheme.init(state);

    if (state.scroll != null) return;

    state.scroll = {};
}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);
    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const departure = arrivals.get(0);

    const framePids = { width: pids.width, height: pids.height - CFG.frameHeightReduction };

    const screenY = CFG.frameOffsetY;
    const screenWidth = framePids.width;
    const screenHeight = framePids.height;
    const screenEdge = { width: screenWidth, height: screenY + screenHeight };

    let routeInfo = null;
    let compConfig = null;
    let hasComposition = false;
    let showTrain = false;

    if (departure != null && state.scroll != null) {
        routeInfo = TrainUtils.getRouteInfo(state.scroll, departure);
        const trainType = routeInfo.trainType.toLowerCase();

        compConfig = ConfigHandler.get(state, "composition");
        hasComposition = TrainComposition.resolveHasComposition(compConfig, trainType);

        showTrain = TrainUtils.isTgvFamily(trainType) || (hasComposition && AdvertisingManager.isDepartureImminent(departure)) || ConfigHandler.get(state, "forceNext");
    }

    if (!showTrain) {
        AdvertisingManager.draw(ctx, state, pids, 0, screenY, screenWidth, screenHeight);
        SncfTheme.drawFrame(ctx, framePids, ConfigHandler.get(state, "logo"), { zOrder: CFG.z.frame, offsetY: CFG.frameOffsetY });
        return;
    }

    const platforms = departure.route().platforms;
    if (platforms == null || platforms.length == 0) return;

    const departureTime = departure.departureTime();
    const timestamp = Timing.currentTimeMillis();

    const lastPlatformId = "" + platforms[platforms.length - 1].platformId;
    const isTerminating = departure.terminating() && ("" + departure.platformId()) === lastPlatformId;

    if (isTerminating) {
        if (departureTime - timestamp <= 15000 && departureTime - timestamp >= -6000) {
            if ((Math.floor(timestamp / 1000) % 8) < 3) {
                img(ctx, "sncf:images/platform/terminating.png", 0, 0, pids.width, pids.height, null, CFG.z.bg);
                SncfTheme.drawFrame(ctx, framePids, ConfigHandler.get(state, "logo"), { zOrder: CFG.z.frame, offsetY: CFG.frameOffsetY });
                return;
            }
        }
    }

    const isImportant = ConfigHandler.get(state, "important");
    const topAreaHeight = isImportant ? screenHeight * 0.5 : screenHeight;

    drawStateBackground(ctx, 0, screenY, screenWidth, topAreaHeight, isTerminating);

    const routeKey = "" + departure.routeId() + "_" + departure.departureIndex();

    const topAreaPids = { width: screenWidth, height: topAreaHeight };

    const leftPanelScale = isImportant ? CFG.leftPanel.contentScale : CFG.leftPanel.contentScale * CFG.leftPanel.inactiveMultiplier;
    drawLeftPanel(ctx, state, state.scroll, topAreaPids, departure, isTerminating, routeInfo, screenY, leftPanelScale);

    const rightWidth = screenWidth - CFG.leftPanelWidth;
    const compositionOffsetY = screenY + (topAreaHeight - CFG.comp.naturalHeight) / 2 + CFG.comp.verticalNudge;
    const compositionPids = { width: rightWidth, height: CFG.comp.naturalHeight };
    TrainComposition.draw(ctx, state, compositionPids, departure, hasComposition, routeInfo, {
        offsetX: CFG.leftPanelWidth,
        offsetY: compositionOffsetY,
        bannerHeight: CFG.comp.naturalHeight,
        zOrders: CFG.comp.zOrders,
        colors: CFG.comp.colors,
        showTrackNumber: false,
        showBackground: false
    });

    if (isImportant) {
        drawPassengerInfoBand(ctx, state, 0, screenY + topAreaHeight, screenWidth, screenHeight - topAreaHeight);
        drawSeamBorder(ctx, 0, screenY + topAreaHeight, screenWidth, isTerminating);
    }

    SncfTheme.drawClock(ctx, screenEdge, timestamp, { zOrder: CFG.z.clock, drawBox: true, boxZOrder: CFG.z.clockBox });
    SncfTheme.drawFrame(ctx, framePids, ConfigHandler.get(state, "logo"), { zOrder: CFG.z.frame, offsetY: CFG.frameOffsetY });
}

function wrapTextToWidth(text, scale, maxWidth) {
    if (!text) return [];
    const words = text.split(" ");
    const lines = [];
    let currentLine = "";
    words.forEach(function(word) {
        const candidate = currentLine === "" ? word : currentLine + " " + word;
        if (currentLine === "" || RenderUtils.getStringWidth(candidate, scale) <= maxWidth) {
            currentLine = candidate;
        } else {
            lines.push(currentLine);
            currentLine = word;
        }
    });
    if (currentLine !== "") lines.push(currentLine);
    return lines;
}

function drawPassengerInfoBand(ctx, state, x, y, width, height) {
    rect(ctx, x, y, width, height, SncfColors.ImportantBand, CFG.z.infoBandBg);

    const combined = SncfTheme.formatText(state, ConfigHandler.getTextSlots(state));
    if (combined === "") return;

    const lines = wrapTextToWidth(combined, CFG.important.textScale, CFG.important.textMaxWidth);

    const textX = x + CFG.important.marginX;
    const textY = y + CFG.important.marginTop;
    const lineHeight = RenderUtils.getCharHeight(CFG.important.textScale);

    lines.forEach(function(line, i) {
        txt(ctx, line, textX, textY + (i * (lineHeight + CFG.important.lineGap)), CFG.important.textScale, SncfColors.ImportantText, "l", CFG.z.infoBand);
    });
}

function drawLeftPanel(ctx, state, scroll, pids, departure, isTerminating, routeInfo, offsetY, scale) {
    drawTrainTypeLogo(ctx, routeInfo, offsetY, scale);
    drawTrainDepartureHour(ctx, state, scroll, pids, departure, isTerminating, offsetY, scale);
    drawTrainInformation(ctx, scroll, pids, departure, (CFG.layout.trainInfoY * scale) + offsetY, routeInfo, offsetY, scale);
}

function drawTrainTypeLogo(ctx, routeInfo, offsetY, scale) {
    const trainType = routeInfo.trainType;
    const trainTexture = TrainUtils.getTextureOfType(trainType);
    if (!TrainUtils.isShowingTexture(trainType)) return;

    const iconSize = TrainUtils.getIconSize(trainTexture, { maxHeight: CFG.layout.iconHeight * scale });
    const trainTextureId = (trainTexture && trainTexture.id) ? trainTexture.id : "sncf";

    img(ctx, "sncf:images/labels/" + trainTextureId + ".png", 1.5 * scale, (CFG.layout.iconY * scale) + offsetY, iconSize[0], iconSize[1], null, CFG.z.left);
}

function drawTrainDepartureHour(ctx, state, scroll, pids, departure, isTerminating, offsetY, scale) {
    const baseTime = isTerminating ? departure.arrivalTime() : departure.departureTime();
    const schedule = TrainUtils.getStableSchedule(state, departure, baseTime);
    const delay = TrainUtils.applyForceDelay(state, schedule);
    const color = isTerminating ? CFG.panelColor.arrival : CFG.panelColor.departure;
    const timeLimitToShowDelay = 20;

    txt(ctx, RenderUtils.formatTime(schedule.scheduledTime, "h"), 2.25 * scale, (CFG.layout.hourY * scale) + offsetY, CFG.hourScale * scale, color, "l", CFG.z.left, { bold: true });

    let statusText;
    if (delay.time > timeLimitToShowDelay) {
        statusText = "retard " + RenderUtils.formatTrainDelay(delay.time, !ConfigHandler.get(state, "exactDelay"), false) + ".";
    } else {
        const onTimeMessages = ["À l'heure", "On time", "In orario"];
        statusText = onTimeMessages[Math.floor(Timing.currentTimeMillis() / 3000) % onTimeMessages.length].toLowerCase();
    }

    txt(ctx, statusText, 2.25 * scale, (CFG.layout.statusY * scale) + offsetY, CFG.statusScale * scale, color, "l", CFG.z.left);
}

function drawTrainInformation(ctx, scroll, pids, departure, startY, routeInfo, offsetY, scale) {
    const trainType = routeInfo.trainType;
    const trainNumber = routeInfo.trainNumber;

    let trainName = TrainUtils.findNameFromAlias(trainType) || trainType.toUpperCase();

    let startX = 1.9 * scale;

    if (TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.CAR)) {
        img(ctx, "sncf:images/pictograms/bus_black.png", 0.15 * scale, pids.height + offsetY - (1.95 * scale), 2.0 * scale, 2.0 * scale, null, CFG.z.left);
    }

    const trainNamePixelSize = 0.475 * scale;
    const transilienPixelSize = 0.42 * scale;
    let useTransilienPixelSize = false;

    const isItTransilien = TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.TRANSILIEN);
    const lineName = routeInfo.parts.length > 2 ? routeInfo.parts[2].trim().toUpperCase() : null;

    if (isItTransilien && lineName != null) {
        useTransilienPixelSize = true;

        img(ctx, "sncf:images/train.png", startX, startY, 4.5 * scale, 4.5 * scale, null, CFG.z.left);

        startX += 2.75 * scale;

        img(ctx, "sncf:images/platform_border.png", startX + 0.225 * scale, startY, 4 * scale, 4 * scale, departure.routeColor(), CFG.z.left);
        txt(ctx, lineName, startX + 1.3 * scale, startY + 0.5 * scale, transilienPixelSize / 2, departure.routeColor(), "c", CFG.z.left);

        startX += 3.125 * scale;
        trainName = trainName.toUpperCase();

        txt(ctx, trainName, startX, startY, transilienPixelSize - 0.0125 * scale, 0x00409D, "l", CFG.z.left);
    } else {
        txt(ctx, trainName, startX, startY, trainNamePixelSize, 0x00409D, "l", CFG.z.left);
    }

    const nameWidthStep = useTransilienPixelSize ? transilienPixelSize - 0.0125 * scale : trainNamePixelSize;
    scroll.trainNameWidth = RenderUtils.getCachedStringWidth(scroll, "trainName", trainName, nameWidthStep);

    const posX = startX + scroll.trainNameWidth + 1.375 * scale;
    txt(ctx, trainNumber, posX, startY + (useTransilienPixelSize ? 0.275 * scale : 0.25 * scale), 0.49 * scale, 0x7C86B5, "l", CFG.z.left);
}
