include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/animation_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:components/live_theme.js"));
importPackage(java.awt);

const CFG = {
    contentMargin: LIVE_HEADER_MARGIN,
    contentLeftPanelRatio: 0.3695,
    z: { content: 3, text: 4 },
    footer: {
        heightRatio: 1.5,
        blockMargin: 1, blockBottomMargin: 0.25, blockWidth: 9, blockHeight: 12,
        blockLabelScale: 0.29, blockValueScale: 0.55, blockLabelValueGap: 0.95, blockBusIconRatio: 0.6,
        pillMarginLeft: 1.1, pillMarginBottom: 1, pillBlockGap: 0.25, pillBlockMargin: 0.5,
        pillTrainBlockHeightRatio: 0.45, motriceNativeWidth: 450, motriceNativeHeight: 420,
        pillTrainBlockWidthMultiplier: 1.6, pillTrainMaxWidthRatio: 0.84, pillHeight: 2
    },
    card: { margin: 2, blockGap: 0.5, botDivRatio: 30 / 705 },
    topBlock: { divHeight: 1.8, whiteHeight: 4.95, contentMargin: 2, timeScale: LIVE_CLOCK_SCALE * 1.125 },
    pill: {
        gap: 5, heightRatio: 0.8, heightExtra: 0.25, innerGap: 0.25, trailingGap: -2.125,
        iconRatio: 0.65, labelScale: 0.2, delayThresholdSeconds: 60, delayAlternateSeconds: 2
    },
    stationName: { scale: LIVE_CLOCK_SCALE * 0.9, lineGap: 0.8 },
    trainInfo: { scale: LIVE_CLOCK_SCALE * 0.5, marginTop: 1, gap: 2 },
    trainNumberColor: 0x87CEFA,
    trainNumberArrivalColor: 0x00A651,
    bottomIcon: { verticalOffset: 0, lineBadgeGap: 1, scale: 1.75, lineBadgeTextScale: 0.35 },
    stationList: {
        stopHeight: 5, maxVisibleStops: 10, rowMargin: 0.25, advanceExitDistanceMultiplier: 2.75,
        topMargin: 1.25, textScale: 0.375, nameMaxLen: 23, lineWidth: 1.25, lineTopOverdraw: 0.5,
        dotSize: 1, dotYOffset: 0.75, highlightDotScale: 1.25, highlightDotBackScale: 2,
        currentArrowRatio: 30 / 58,
        currentSemitpScale: 3, currentBackdot2Scale: 2.5,
        anim: { staticDuration: 2.5, arrowDropDuration: 0.6, backdot2GrowDuration: 0.3, backdot3GrowDuration: 0.3, holdDuration: 0.75, resetDuration: 0.5 },
        currentArrowDropRatio: 0.3,
        fadeDotRatios: [0.75, 0.5, 0.25], fadeDotGap: 1.5, fadeDotListGap: 2.85,
        fadeDotsRaiseScrolling: 1.8, fadeDotsRaiseStatic: 0.4, fadeDotSize: 1.25,
        textGap: 4, timeBetweenStates: 5,
        scrollFadeOutDuration: 0.5, scrollFadeHoldDuration: 0.2, scrollTargetRatio: 42.5 / 68.6
    },
    noTrainMessageScale: 0.6,
    shadowTex: { w: 2, h: 256 }
};
CFG.footer.height = LIVE_HEADER_HEIGHT * CFG.footer.heightRatio;
CFG.footer.zOrder = LIVE_ZORDERS.header;
CFG.footer.shadowZOrder = CFG.footer.zOrder;
CFG.footer.pillTrainBlockHeight = CFG.footer.height * CFG.footer.pillTrainBlockHeightRatio;
CFG.footer.pillTrainBlockWidth = CFG.footer.pillTrainBlockHeight * (CFG.footer.motriceNativeWidth / CFG.footer.motriceNativeHeight) * CFG.footer.pillTrainBlockWidthMultiplier;
CFG.topBlock.height = CFG.topBlock.divHeight + CFG.topBlock.whiteHeight;
CFG.pill.zOrder = CFG.z.text;
CFG.pill.contentZOrder = CFG.z.text + 1;
CFG.bottomIcon.maxHeight = (CFG.topBlock.height - (CFG.topBlock.contentMargin * 2)) * CFG.bottomIcon.scale;
CFG.bottomIcon.lineBadgeTextZOrder = CFG.z.text + 1;
CFG.stationList.nonBoldTextScale = CFG.stationList.textScale * 0.9;
CFG.stationList.dotOffset = (CFG.stationList.lineWidth - CFG.stationList.dotSize) / 2;
CFG.stationList.dotCenterOffset = CFG.stationList.dotYOffset + (CFG.stationList.dotSize / 2);
CFG.stationList.highlightDotZOrder = CFG.z.text + 1;
CFG.stationList.currentSemitpZOrder = CFG.z.text + 2;
CFG.stationList.currentBackdot2ZOrder = CFG.z.text + 3;
CFG.stationList.currentBackdotZOrder = CFG.z.text + 4;
CFG.stationList.currentArrowZOrder = CFG.z.text + 5;
CFG.stationList.anim.totalDuration = CFG.stationList.anim.staticDuration + CFG.stationList.anim.arrowDropDuration
    + CFG.stationList.anim.backdot2GrowDuration + CFG.stationList.anim.backdot3GrowDuration
    + CFG.stationList.anim.holdDuration + CFG.stationList.anim.resetDuration;
CFG.stationList.currentSemitpGrownScale = CFG.stationList.currentSemitpScale * 1.3;
CFG.stationList.currentBackdot2GrownScale = CFG.stationList.currentSemitpScale;
CFG.stationList.currentAnimEase = AnimationUtils.cubicBezier(0, 0, 0.58, 1.0);
CFG.stationList.fadeDotsHeadroom = CFG.stationList.fadeDotListGap + (CFG.stationList.fadeDotGap * (CFG.stationList.fadeDotRatios.length - 1));
CFG.stationList.scrollEase = AnimationUtils.cubicBezier(0.35, 0.05, 0.20, 1.0);
CFG.stationList.lineColor = CFG.trainNumberColor;

function create(ctx, state, pids) {
    ConfigHandler.init(state);
    SncfTheme.init(state);
    if (state.scroll != null) return;
    state.scroll = {
        stations: [],
        fixedStations: []
    };
}

function dispose(ctx, state, pids) {
    if (state.headerShadowTexture) {
        state.headerShadowTexture.close();
        state.headerShadowTexture = null;
    }
    if (state.footerShadowTexture) {
        state.footerShadowTexture.close();
        state.footerShadowTexture = null;
    }
}

function buildEdgeGradientImage(gt, pxWidth, pxHeight, startY, startColor, endY, endColor) {
    const g2 = gt.graphics;
    g2.setComposite(AlphaComposite.Src);
    g2.setPaint(new GradientPaint(0, startY, startColor, 0, endY, endColor));
    g2.fillRect(0, 0, pxWidth, pxHeight);
    gt.upload();
}

function opaqueColor(color) {
    return new Color((255 << 24) | (color & 0xFFFFFF), true);
}
function transparentColor(color) {
    return new Color(color & 0xFFFFFF, true);
}

function drawHeaderShadowAwt(ctx, state, pids, color) {
    if (!state.headerShadowTexture || state.headerShadowColor !== color) {
        state.headerShadowColor = color;
        if (!state.headerShadowTexture) {
            state.headerShadowTexture = new GraphicsTexture(CFG.shadowTex.w, CFG.shadowTex.h);
        }
        buildEdgeGradientImage(
            state.headerShadowTexture, CFG.shadowTex.w, CFG.shadowTex.h,
            0, opaqueColor(color),
            CFG.shadowTex.h * 0.85, transparentColor(color)
        );
    }
    img(ctx, state.headerShadowTexture.identifier, 0, LIVE_HEADER_HEIGHT, pids.width, LIVE_HEADER_SHADOW_HEIGHT, null, LIVE_ZORDERS.header);
}

function drawFooterShadowAwt(ctx, state, pids, color) {
    if (!state.footerShadowTexture || state.footerShadowColor !== color) {
        state.footerShadowColor = color;
        if (!state.footerShadowTexture) {
            state.footerShadowTexture = new GraphicsTexture(CFG.shadowTex.w, CFG.shadowTex.h);
        }
        buildEdgeGradientImage(
            state.footerShadowTexture, CFG.shadowTex.w, CFG.shadowTex.h,
            CFG.shadowTex.h * 0.15, transparentColor(color),
            CFG.shadowTex.h, opaqueColor(color)
        );
    }
    const footerTopY = pids.height - CFG.footer.height;
    img(ctx, state.footerShadowTexture.identifier, 0, footerTopY - LIVE_HEADER_SHADOW_HEIGHT, pids.width, LIVE_HEADER_SHADOW_HEIGHT, null, CFG.footer.shadowZOrder);
}

function resolveStaticPlatformText(pids) {
    try {
        const ids = pids.getTargetPlatformIds();
        if (ids == null || ids.size() === 0) return null;
        const platform = MTRClientData.getInstance().platformIdMap.get(ids.get(0));
        if (platform == null) return null;
        const name = ("" + platform.name).trim();
        return (name === "" || name === "null" || name === "undefined") ? null : name;
    } catch (e) {
        return null;
    }
}

function drawFooter(ctx, state, pids, backgroundColor, headerColor, departure) {
    rect(ctx, 0, pids.height - CFG.footer.height, pids.width, CFG.footer.height, backgroundColor, CFG.footer.zOrder);
    drawFooterShadowAwt(ctx, state, pids, backgroundColor);
    const blockX = pids.width - CFG.footer.blockMargin - CFG.footer.blockWidth;
    const blockY = pids.height - CFG.footer.blockBottomMargin - CFG.footer.blockHeight;
    const blockCapHeight = CFG.footer.blockWidth * CFG.card.botDivRatio;
    const blockWhiteHeight = CFG.footer.blockHeight - (blockCapHeight * 2);
    img(ctx, "sncf:images/top_div.png", blockX, blockY, CFG.footer.blockWidth, blockCapHeight, 0xFFFFFF, LIVE_ZORDERS.headerContent);
    img(ctx, "aca:images/white.png", blockX, blockY + blockCapHeight, CFG.footer.blockWidth, blockWhiteHeight, 0xFFFFFF, LIVE_ZORDERS.headerContent);
    img(ctx, "sncf:images/bot_div.png", blockX, blockY + blockCapHeight + blockWhiteHeight, CFG.footer.blockWidth, blockCapHeight, 0xFFFFFF, LIVE_ZORDERS.headerContent);
    const blockCenterX = blockX + (CFG.footer.blockWidth / 2);
    const isBusType = TrainUtils.isBusType(state, departure);
    if (isBusType) {
        const busIconSize = CFG.footer.blockWidth * CFG.footer.blockBusIconRatio;
        img(ctx, "sncf:images/pictograms/bus.png", blockCenterX - (busIconSize / 2), blockY + ((CFG.footer.blockHeight - busIconSize) / 2), busIconSize, busIconSize, headerColor, LIVE_ZORDERS.headerContent + 1);
    } else {
        if (departure != null) {
            state.scroll.lastPlatformText = ("" + departure.platformName()).split("|")[0];
        }
        if (!state.scroll.staticPlatformText) {
            state.scroll.staticPlatformText = resolveStaticPlatformText(pids);
        }
        const platformText = state.scroll.staticPlatformText || state.scroll.lastPlatformText || "";
        const labelCharHeight = RenderUtils.getCharHeight(CFG.footer.blockLabelScale);
        const valueCharHeight = RenderUtils.getCharHeight(CFG.footer.blockValueScale);
        const totalTextHeight = labelCharHeight + CFG.footer.blockLabelValueGap + valueCharHeight;
        const textStartY = blockY + ((CFG.footer.blockHeight - totalTextHeight) / 2);
        const voieLabelLang = ConfigHandler.get(state, "locale");
        let voieLabelOffsetX = 0.1;
        let voieLabelScale = CFG.footer.blockLabelScale;
        if (voieLabelLang === "en") {
            voieLabelOffsetX = 0;
            voieLabelScale = CFG.footer.blockLabelScale * 0.8;
        } else if (voieLabelLang === "jp" || voieLabelLang === "cn") {
            voieLabelOffsetX = 0.25;
        } else if (voieLabelLang === "kr") {
            voieLabelOffsetX = 0;
        }
        txt(ctx, LiveTheme.t(state, "track"), blockCenterX + voieLabelOffsetX, textStartY, voieLabelScale, headerColor, "c", LIVE_ZORDERS.headerContent + 1);
        txt(ctx, platformText, blockCenterX, textStartY + labelCharHeight + CFG.footer.blockLabelValueGap, CFG.footer.blockValueScale, headerColor, "c", LIVE_ZORDERS.headerContent + 1, { bold: true });
    }
    const pillCapWidth = CFG.footer.pillHeight;
    const pillCapRadius = pillCapWidth / 2;
    const pillX = CFG.footer.pillMarginLeft;
    const pillWidth = (blockX - CFG.footer.pillMarginLeft) - pillX;
    const pillY = pids.height - CFG.footer.pillMarginBottom - CFG.footer.pillHeight;
    img(ctx, "aca:images/dot.png", pillX, pillY, pillCapWidth, CFG.footer.pillHeight, headerColor, LIVE_ZORDERS.headerContent);
    img(ctx, "aca:images/white.png", pillX + pillCapRadius, pillY, pillWidth - pillCapWidth, CFG.footer.pillHeight, headerColor, LIVE_ZORDERS.headerContent);
    img(ctx, "aca:images/dot.png", pillX + pillWidth - pillCapWidth, pillY, pillCapWidth, CFG.footer.pillHeight, headerColor, LIVE_ZORDERS.headerContent);
    const carCount = departure != null ? (departure.carCount ? departure.carCount() : (departure.cars() ? departure.cars().length : 0)) : 0;
    if (carCount > 0) {
        const blocksFullWidth = pillWidth - pillCapWidth;
        let blockWidth = CFG.footer.pillTrainBlockWidth;
        let blockHeight = CFG.footer.pillTrainBlockHeight;
        let actualTrainWidth = (blockWidth * carCount) + (CFG.footer.pillBlockGap * (carCount - 1));
        const maxTrainWidth = blocksFullWidth * CFG.footer.pillTrainMaxWidthRatio;
        if (actualTrainWidth > maxTrainWidth) {
            const shrinkScale = maxTrainWidth / actualTrainWidth;
            blockWidth *= shrinkScale;
            actualTrainWidth = maxTrainWidth;
        }
        const blocksInnerX = pillX + pillCapRadius + ((blocksFullWidth - actualTrainWidth) / 2);
        const carBlockY = pillY - CFG.footer.pillBlockMargin - blockHeight;
        const isArrivals = ConfigHandler.get(state, "arrivals") || (departure != null && state.scroll.isTerminating);
        const vehicleColor = isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor;
        const carRouteInfo = departure != null ? TrainUtils.getRouteInfo(state, departure) : null;
        const locoPositions = carRouteInfo != null ? TrainUtils.resolveLocoPositions(state, carRouteInfo) : null;
        const isTgvForCars = carRouteInfo != null && TrainUtils.isTgvFamily(carRouteInfo.trainType);
        const carLocoFlags = TrainUtils.resolveCarLocoFlags(locoPositions, carCount, isTgvForCars);
        for (var i = 0; i < carCount; i++) {
            var carBlockX = blocksInnerX + (i * (blockWidth + CFG.footer.pillBlockGap));
            var isLocoCar = carLocoFlags[i].isLocoCar;
            var isFrontLoco = carLocoFlags[i].isFrontLoco;
            img(ctx, isLocoCar ? (isFrontLoco ? "sncf:images/car_front.png" : "sncf:images/car_back.png") : "aca:images/white.png", carBlockX, carBlockY, blockWidth, blockHeight, vehicleColor, LIVE_ZORDERS.headerContent + 1);
        }
    }
}

function getContentPanels(pids) {
    const top = LIVE_HEADER_HEIGHT + CFG.contentMargin;
    const left = CFG.contentMargin;
    const contentWidth = pids.width - (CFG.contentMargin * 2);
    const height = pids.height - top - CFG.footer.height;
    const leftWidth = contentWidth * CFG.contentLeftPanelRatio;
    const rightWidth = contentWidth - leftWidth;
    return {
        top: top,
        height: height,
        left: { x: left, y: top, width: leftWidth, height: height },
        right: { x: left + leftWidth, y: top, width: rightWidth, height: height }
    };
}

function drawRightPanelDebugFill(ctx, panel) {
    rect(ctx, panel.x, panel.y, panel.width, panel.height, 0xFFFFFF, CFG.z.content);
}

function drawNoTrainMessage(ctx, state, pids, isArrivals) {
    const lineHeight = RenderUtils.getCharHeight(CFG.noTrainMessageScale);
    const y = (pids.height - lineHeight) / 2;
    const color = isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor;
    txt(ctx, LiveTheme.t(state, "noTrain"), pids.width / 2, y, CFG.noTrainMessageScale, color, "c", CFG.z.text, { size: [pids.width, lineHeight] });
}

function drawStatusPill(ctx, pillX, centerY, pillHeight, pillColor, widthLabelText, displayText, rightAlignLabel, iconTexture, iconColor) {
    const capWidth = pillHeight;
    const capRadius = capWidth / 2;
    const innerIconSize = pillHeight * CFG.pill.iconRatio;
    const labelWidth = RenderUtils.getStringWidth(widthLabelText, CFG.pill.labelScale);
    const displayWidth = RenderUtils.getStringWidth(displayText, CFG.pill.labelScale);
    const textSlotWidth = Math.max(labelWidth, displayWidth);
    const middleWidth = CFG.pill.innerGap + textSlotWidth + CFG.pill.trailingGap;
    const totalWidth = capWidth + middleWidth + capWidth;
    const pillY = centerY - (pillHeight / 2);
    img(ctx, "aca:images/dot.png", pillX, pillY, capWidth, pillHeight, pillColor, CFG.pill.zOrder);
    img(ctx, "aca:images/white.png", pillX + capRadius, pillY, totalWidth - capWidth, pillHeight, pillColor, CFG.pill.zOrder);
    img(ctx, "aca:images/dot.png", pillX + totalWidth - capWidth, pillY, capWidth, pillHeight, pillColor, CFG.pill.zOrder);
    const innerIconX = pillX + capRadius - (innerIconSize / 2);
    const innerIconY = centerY - (innerIconSize / 2);
    img(ctx, iconTexture, innerIconX, innerIconY, innerIconSize, innerIconSize, iconColor, CFG.pill.contentZOrder);
    const labelCharHeight = RenderUtils.getCharHeight(CFG.pill.labelScale);
    const labelX = rightAlignLabel ? (pillX + capWidth + CFG.pill.innerGap + textSlotWidth) : (pillX + capWidth + CFG.pill.innerGap);
    txt(ctx, displayText, labelX, centerY - (labelCharHeight / 2), CFG.pill.labelScale, iconColor, rightAlignLabel ? "r" : "l", CFG.pill.contentZOrder);
    return pillX + totalWidth;
}

function drawTopBlock(ctx, state, panel, headerColor, backgroundColor, timestamp, delay) {
    const x = panel.x;
    const y = panel.y;
    const width = panel.width - CFG.card.margin;
    img(ctx, "sncf:images/top_div.png", x, y, width, CFG.topBlock.divHeight, headerColor, CFG.z.content);
    img(ctx, "aca:images/white.png", x, y + CFG.topBlock.divHeight, width, CFG.topBlock.whiteHeight, headerColor, CFG.z.content);
    const contentCenterY = y + (CFG.topBlock.height / 2);
    const timeCharHeight = RenderUtils.getCharHeight(CFG.topBlock.timeScale);
    const timeText = RenderUtils.formatTime(timestamp);
    const timeX = x + CFG.topBlock.contentMargin;
    txt(ctx, timeText, timeX, contentCenterY - (timeCharHeight / 2), CFG.topBlock.timeScale, SncfColors.DepartureYellow, "l", CFG.z.text, { bold: true });
    const timeWidth = RenderUtils.getStringWidth(timeText, CFG.topBlock.timeScale);
    const pillX = timeX + timeWidth + CFG.pill.gap;
    const pillHeight = (timeCharHeight * CFG.pill.heightRatio) + (CFG.pill.heightExtra * 2);
    const isDelayed = delay.time > CFG.pill.delayThresholdSeconds;
    const pillWidthLabel = isDelayed ? LiveTheme.t(state, "delayed") : LiveTheme.t(state, "onTime");
    let pillDisplayLabel = pillWidthLabel;
    let pillRightAlign = isDelayed;
    if (isDelayed) {
        const alternatePhase = Math.floor(Timing.currentTimeMillis() / (CFG.pill.delayAlternateSeconds * 1000)) % 2;
        if (alternatePhase === 1) {
            pillDisplayLabel = RenderUtils.formatDelayAmount(delay.time);
        }
    }
    const pillIcon = isDelayed ? "sncf:images/delay.png" : "sncf:images/check.png";
    const pillColor = isDelayed ? SncfColors.LiveDelayPillBackground : backgroundColor;
    const pillIconColor = isDelayed ? SncfColors.LiveDelayPillIcon : SncfColors.OnTimeWhite;
    drawStatusPill(ctx, pillX, contentCenterY, pillHeight, pillColor, pillWidthLabel, pillDisplayLabel, pillRightAlign, pillIcon, pillIconColor);
    return y + CFG.topBlock.height;
}

function drawMiddleBlock(ctx, state, panel, headerColor, startY, isLastBlock, destination, trainType, trainNumber, isArrivals) {
    const x = panel.x;
    const width = panel.width - CFG.card.margin;
    const stationLineHeight = RenderUtils.getCharHeight(CFG.stationName.scale);
    const rawDestination = ("" + destination) === "" ? "⚠ Undefined Name" : "" + destination;
    const stationName = SncfTheme.shortenNames(state, rawDestination);
    const stationLines = SncfTheme.convertNameToLines(state, stationName);
    let stationBlockHeight = 0;
    stationLines.forEach(function(line) {
        stationBlockHeight += stationLineHeight + line.margin + CFG.stationName.lineGap;
    });
    const trainInfoLineHeight = RenderUtils.getCharHeight(CFG.trainInfo.scale);
    const contentHeight = stationBlockHeight + CFG.trainInfo.marginTop + trainInfoLineHeight;
    const blockHeight = CFG.topBlock.contentMargin + contentHeight + CFG.topBlock.contentMargin;
    if (isLastBlock) {
        const capHeight = width * CFG.card.botDivRatio;
        const whiteHeight = blockHeight - capHeight;
        img(ctx, "aca:images/white.png", x, startY, width, whiteHeight, headerColor, CFG.z.content);
        img(ctx, "sncf:images/bot_div.png", x, startY + whiteHeight, width, capHeight, headerColor, CFG.z.content);
    } else {
        img(ctx, "aca:images/white.png", x, startY, width, blockHeight, headerColor, CFG.z.content);
    }
    const contentX = x + CFG.topBlock.contentMargin;
    const contentY = startY + CFG.topBlock.contentMargin;
    let lineY = contentY;
    stationLines.forEach(function(line) {
        txt(ctx, line.text, contentX, lineY, CFG.stationName.scale, SncfColors.OnTimeWhite, "l", CFG.z.text, { bold: true });
        lineY += stationLineHeight + line.margin + CFG.stationName.lineGap;
    });
    const trainTypeName = TrainUtils.getNameOfType(trainType) || ("" + trainType).toUpperCase();
    const trainInfoY = contentY + stationBlockHeight + CFG.trainInfo.marginTop;
    txt(ctx, trainTypeName, contentX, trainInfoY, CFG.trainInfo.scale, SncfColors.OnTimeWhite, "l", CFG.z.text, { bold: true });
    const typeWidth = RenderUtils.getBoldStringWidth(trainTypeName, CFG.trainInfo.scale);
    txt(ctx, trainNumber, contentX + typeWidth + CFG.trainInfo.gap, trainInfoY, CFG.trainInfo.scale, isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor, "l", CFG.z.text);
    return startY + blockHeight;
}

function drawBottomBlockLineBadge(ctx, panel, startY, isRer, lineName, routeColor) {
    const x = panel.x;
    const iconTexture = isRer ? "idf:images/rer_line_white.png" : "idf:images/transilien_line_white.png";
    const size = CFG.bottomIcon.maxHeight;
    const contentCenterY = startY + (CFG.topBlock.height / 2);
    const contentTop = contentCenterY - (size / 2);
    const iconX = x + CFG.topBlock.contentMargin;
    const badgeX = iconX + size + CFG.bottomIcon.lineBadgeGap;
    img(ctx, iconTexture, iconX, contentTop, size, size, null, CFG.z.text);
    img(ctx, "sncf:images/platform_border.png", badgeX, contentTop, size, size, routeColor, CFG.z.text);
    const textHeight = RenderUtils.getCharHeight(CFG.bottomIcon.lineBadgeTextScale);
    txt(ctx, lineName, badgeX + (size / 2) + 0.15, contentCenterY - (textHeight / 2), CFG.bottomIcon.lineBadgeTextScale, routeColor, "c", CFG.bottomIcon.lineBadgeTextZOrder);
}

function drawBottomBlock(ctx, panel, headerColor, startY, trainType, isLineBadge, isRer, lineName, departure) {
    const x = panel.x;
    const width = panel.width - CFG.card.margin;
    const capHeight = width * CFG.card.botDivRatio;
    const whiteHeight = CFG.topBlock.height - capHeight;
    img(ctx, "aca:images/white.png", x, startY, width, whiteHeight, headerColor, CFG.z.content);
    img(ctx, "sncf:images/bot_div.png", x, startY + whiteHeight, width, capHeight, headerColor, CFG.z.content);
    if (isLineBadge) {
        drawBottomBlockLineBadge(ctx, panel, startY, isRer, lineName, departure.routeColor());
        return startY + CFG.topBlock.height;
    }
    const trainTexture = TrainUtils.getTextureOfType(trainType);
    const iconSize = TrainUtils.getIconSize(trainTexture, { maxHeight: CFG.bottomIcon.maxHeight });
    const iconWidth = iconSize[0];
    const iconHeight = iconSize[1];
    let trainTextureId = (trainTexture && trainTexture.id) ? trainTexture.id : "sncf";
    if (trainTextureId === "eurostar") trainTextureId = "eurostar_white";
    img(ctx, "sncf:images/labels/" + trainTextureId + ".png", x + CFG.topBlock.contentMargin, startY + (CFG.topBlock.height - iconHeight) / 2 - CFG.bottomIcon.verticalOffset, iconWidth, iconHeight, null, CFG.z.text);
    return startY + CFG.topBlock.height;
}

function drawLeftPanelBlocks(ctx, state, panel, headerColor, backgroundColor, timestamp, delay, routeInfo, destination, departure, isArrivals) {
    const trainType = routeInfo.trainType;
    const trainNumber = routeInfo.trainNumber;
    const isRer = TrainUtils.isMatchingOneAliasOfType(trainType, TRAIN_TYPES.RER);
    const isTransilien = TrainUtils.isMatchingOneAliasOfType(trainType, TRAIN_TYPES.TRANSILIEN);
    const lineName = TrainUtils.resolveLineName(routeInfo);
    const isLineBadge = (isRer || isTransilien) && lineName != null;
    const showBottomBlock = (TrainUtils.isTgvBrand(trainType) && TrainUtils.isShowingTexture(trainType)) || isLineBadge;
    const topBlockBottomY = drawTopBlock(ctx, state, panel, headerColor, backgroundColor, timestamp, delay);
    const middleBlockBottomY = drawMiddleBlock(ctx, state, panel, headerColor, topBlockBottomY + CFG.card.blockGap, !showBottomBlock, destination, trainType, trainNumber, isArrivals);
    if (showBottomBlock) {
        drawBottomBlock(ctx, panel, headerColor, middleBlockBottomY + CFG.card.blockGap, trainType, isLineBadge, isRer, lineName, departure);
    }
}

function updateStationList(state, departure, panelHeight) {
    const route = departure.route();
    const platforms = route ? route.platforms : null;
    if (platforms == null || platforms.length == 0) {
        state.scroll.stations = [];
        state.scroll.isTerminating = false;
        return;
    }
    const routeKey = "" + departure.routeId() + "_" + departure.departureIndex();
    const isNewRoute = state.scroll.stations.length == 0 || state.scroll.stationsRouteKey !== routeKey;
    if (isNewRoute) {
        state.scroll.stationsRouteKey = routeKey;
        const stations = [];
        platforms.forEach(function(platform) {
            stations.push({ name: String(platform.stationName || ""), id: "" + platform.platformId });
        });
        state.scroll.stations = stations;
        state.scroll.fixedStations = stations;
    }
    const lastPlatformId = "" + platforms[platforms.length - 1].platformId;
    const isTerminating = departure.terminating() && ("" + departure.platformId()) === lastPlatformId;
    state.scroll.isTerminating = isTerminating;
    const currentStation = "" + departure.platformId();
    const previousCurrentStationId = state.scroll.currentStationId;
    const hasAdvanced = !isNewRoute && !isTerminating && previousCurrentStationId != null && previousCurrentStationId !== currentStation;
    const previousHasStationsBefore = state.scroll.hasStationsBefore;
    state.scroll.currentStationId = currentStation;
    var newStations, newHasStationsBefore;
    if (!isTerminating) {
        const currentIndex = state.scroll.fixedStations.findIndex(function(station) { return station.id === currentStation; });
        newStations = currentIndex !== -1 ? state.scroll.fixedStations.slice(currentIndex) : state.scroll.fixedStations;
        newHasStationsBefore = currentIndex > 0;
    } else {
        newStations = state.scroll.fixedStations.slice(-1);
        newHasStationsBefore = state.scroll.fixedStations.length > 1;
    }
    state.scroll.hasStationsBefore = newHasStationsBefore;
    if (hasAdvanced && state.scroll.stations.length > 0) {
        state.scroll.pendingStations = newStations;
        state.scroll.forcedAdvanceScroll = true;
        state.scroll.linearOffset = 0;
        state.scroll.advanceTimer = 0;
        state.scroll.exitingStation = state.scroll.stations[0];
        state.scroll.exitingHasStationsBefore = previousHasStationsBefore;
        state.scroll.exitOffset = 0;
    } else if (!state.scroll.forcedAdvanceScroll) {
        state.scroll.stations = newStations;
        state.scroll.forcedAdvanceScroll = false;
    } else {
        state.scroll.pendingStations = newStations;
    }
    const rowStep = CFG.stationList.stopHeight + CFG.stationList.rowMargin;
    const startYMargin = state.scroll.hasStationsBefore ? (CFG.stationList.fadeDotsHeadroom - CFG.stationList.fadeDotsRaiseScrolling) : CFG.stationList.topMargin;
    const targetY = (panelHeight * CFG.stationList.scrollTargetRatio) - (startYMargin - CFG.stationList.topMargin);
    const naturalMaxScroll = Math.max(0, (state.scroll.stations.length - 1) * rowStep - targetY);
    const scrollSpeed = ConfigHandler.get(state, "scrollSpeed");
    if (state.scroll.forcedAdvanceScroll || state.scroll.exitingStation != null) {
        const exitDistance = rowStep * CFG.stationList.advanceExitDistanceMultiplier;
        if (state.scroll.advanceTimer == null) state.scroll.advanceTimer = 0;
        state.scroll.advanceTimer += Timing.delta();
        const totalDuration = exitDistance / scrollSpeed;
        const rawProgress = Math.min(1, state.scroll.advanceTimer / totalDuration);
        const totalOffset = exitDistance * CFG.stationList.scrollEase(rawProgress);
        state.scroll.exitOffset = totalOffset;
        state.scroll.linearOffset = Math.min(totalOffset, rowStep);
        if (state.scroll.forcedAdvanceScroll && totalOffset >= rowStep) {
            state.scroll.stations = state.scroll.pendingStations;
            state.scroll.pendingStations = null;
            state.scroll.forcedAdvanceScroll = false;
        }
        if (totalOffset >= exitDistance) {
            state.scroll.exitingStation = null;
            state.scroll.exitingHasStationsBefore = false;
            state.scroll.exitOffset = 0;
            state.scroll.advanceTimer = 0;
        }
    } else if (state.scroll.stations.length >= CFG.stationList.maxVisibleStops) {
        if (state.scroll.naturalPhase == null) {
            state.scroll.naturalPhase = "pause_start";
            state.scroll.naturalTimer = 0;
            state.scroll.linearOffset = 0;
            state.scroll.naturalFadeProgress = 0;
        }
        state.scroll.naturalTimer += Timing.delta();
        if (state.scroll.naturalPhase === "pause_start") {
            if (state.scroll.naturalTimer > CFG.stationList.timeBetweenStates) {
                state.scroll.naturalPhase = "scrolling";
                state.scroll.naturalTimer = 0;
            }
        } else if (state.scroll.naturalPhase === "scrolling") {
            const scrollDuration = naturalMaxScroll / scrollSpeed;
            const naturalRawProgress = Math.min(1, state.scroll.naturalTimer / scrollDuration);
            state.scroll.linearOffset = naturalMaxScroll * CFG.stationList.scrollEase(naturalRawProgress);
            if (naturalRawProgress >= 1) {
                state.scroll.naturalPhase = "hold_end";
                state.scroll.naturalTimer = 0;
            }
        } else if (state.scroll.naturalPhase === "hold_end") {
            if (state.scroll.naturalTimer > CFG.stationList.timeBetweenStates) {
                state.scroll.naturalPhase = "fade_out";
                state.scroll.naturalTimer = 0;
            }
        } else if (state.scroll.naturalPhase === "fade_out") {
            state.scroll.naturalFadeProgress = Math.min(1, state.scroll.naturalTimer / CFG.stationList.scrollFadeOutDuration);
            if (state.scroll.naturalFadeProgress >= 1) {
                state.scroll.naturalPhase = "faded_hold";
                state.scroll.naturalTimer = 0;
            }
        } else if (state.scroll.naturalPhase === "faded_hold") {
            if (state.scroll.naturalTimer > CFG.stationList.scrollFadeHoldDuration) {
                state.scroll.naturalPhase = "pause_start";
                state.scroll.naturalTimer = 0;
                state.scroll.linearOffset = 0;
                state.scroll.naturalFadeProgress = 0;
            }
        }
    } else {
        state.scroll.linearOffset = 0;
        state.scroll.naturalPhase = null;
        state.scroll.naturalFadeProgress = 0;
    }
    if (state.scroll.currentDotAnimTimer == null) state.scroll.currentDotAnimTimer = 0;
    state.scroll.currentDotAnimTimer = (state.scroll.currentDotAnimTimer + Timing.delta()) % CFG.stationList.anim.totalDuration;
}

function currentDotAnimProgress(t, growStart, growEnd, resetStart, resetEnd) {
    if (t < growStart) return 0;
    if (t < growEnd) return CFG.stationList.currentAnimEase((t - growStart) / (growEnd - growStart));
    if (t < resetStart) return 1;
    if (t < resetEnd) return 1 - CFG.stationList.currentAnimEase((t - resetStart) / (resetEnd - resetStart));
    return 0;
}

function computeCurrentDotAnimation(t) {
    const t1 = CFG.stationList.anim.staticDuration;
    const t2 = t1 + CFG.stationList.anim.arrowDropDuration;
    const t3 = t2 + CFG.stationList.anim.backdot2GrowDuration;
    const t4 = t3 + CFG.stationList.anim.backdot3GrowDuration;
    const t5 = t4 + CFG.stationList.anim.holdDuration;
    const t6 = t5 + CFG.stationList.anim.resetDuration;
    return {
        arrowProgress: currentDotAnimProgress(t, t1, t2, t5, t6),
        backdot2Progress: currentDotAnimProgress(t, t2, t3, t5, t6),
        backdot3Progress: currentDotAnimProgress(t, t3, t4, t5, t6),
    };
}

function drawStationListFadeDots(ctx, centerX, baseY, lineColor, backgroundColor, colorFn) {
    for (var f = 0; f < CFG.stationList.fadeDotRatios.length; f++) {
        var fadeCenterY = baseY - CFG.stationList.fadeDotListGap - (CFG.stationList.fadeDotGap * f);
        var blended = RenderUtils.blendColor(lineColor, backgroundColor, CFG.stationList.fadeDotRatios[f]);
        img(ctx, "aca:images/dot.png", centerX - (CFG.stationList.fadeDotSize / 2), fadeCenterY - (CFG.stationList.fadeDotSize / 2), CFG.stationList.fadeDotSize, CFG.stationList.fadeDotSize, colorFn(blended), CFG.z.text);
    }
}

function drawStationList(ctx, state, scroll, panel, backgroundColor) {
    const stations = scroll.stations;
    const availableHeight = panel.height;
    const isArrivals = ConfigHandler.get(state, "arrivals") || scroll.isTerminating;
    const lineColor = isArrivals ? CFG.trainNumberArrivalColor : CFG.stationList.lineColor;
    const fadeProgress = scroll.naturalFadeProgress || 0;
    function applyFade(color) {
        return fadeProgress > 0 ? RenderUtils.blendColor(color, backgroundColor, 1 - fadeProgress) : color;
    }
    function headerBandFade(color, y) {
        const bandBottom = panel.y;
        if (y >= bandBottom) return color;
        const ratio = Math.min(1, (bandBottom - y) / LIVE_HEADER_SHADOW_HEIGHT);
        return RenderUtils.blendColor(color, backgroundColor, 1 - ratio);
    }
    const rowStep = CFG.stationList.stopHeight + CFG.stationList.rowMargin;
    const isScrolling = stations.length >= CFG.stationList.maxVisibleStops || scroll.forcedAdvanceScroll;
    let scrollIndex = 0;
    let scrollOffset = -1.5;
    if (isScrolling) {
        scrollIndex = Math.floor(scroll.linearOffset / rowStep);
        scrollOffset = (scroll.linearOffset % rowStep) - 1.5;
    }
    let startY = -scrollOffset;
    let maxI = CFG.stationList.maxVisibleStops + 1;
    if (!isScrolling) {
        maxI = stations.length;
        startY = 0;
    }
    const hasStationsBefore = !!scroll.hasStationsBefore;
    if (hasStationsBefore) {
        startY += CFG.stationList.fadeDotsHeadroom - (isScrolling ? CFG.stationList.fadeDotsRaiseScrolling : CFG.stationList.fadeDotsRaiseStatic);
    } else {
        startY += CFG.stationList.topMargin;
    }
    const loopStart = (isScrolling && scrollOffset > rowStep) ? 1 : 0;
    const minI = isScrolling ? (scrollIndex >= 2 ? -2 : (scrollIndex === 1 ? -1 : loopStart)) : 0;
    const lineX = panel.x + CFG.topBlock.contentMargin;
    const textX = lineX + CFG.stationList.textGap;
    const nameWidth = panel.x + panel.width - textX;
    const lineStartY = panel.y + startY + (rowStep * minI) + CFG.stationList.dotCenterOffset;
    let lineHeightSize = availableHeight - (lineStartY - panel.y);
    if (!isScrolling) {
        lineHeightSize = rowStep * (stations.length - 1 - loopStart);
    } else {
        const terminusRowIndex = stations.length - 1 - scrollIndex;
        if (terminusRowIndex < maxI) {
            lineHeightSize = Math.min(lineHeightSize, rowStep * (terminusRowIndex - minI));
        }
    }
    img(ctx, "aca:images/white.png", lineX, lineStartY - CFG.stationList.lineTopOverdraw, CFG.stationList.lineWidth, lineHeightSize + CFG.stationList.lineTopOverdraw, applyFade(lineColor), CFG.z.content);
    if (hasStationsBefore) {
        var fadeDotCenterX = lineX + (CFG.stationList.lineWidth / 2);
        drawStationListFadeDots(ctx, fadeDotCenterX, lineStartY, lineColor, backgroundColor, applyFade);
    }
    const currentStationId = scroll.currentStationId;
    const lastStationId = scroll.fixedStations[scroll.fixedStations.length - 1].id;
    for (var i = minI; i < maxI; i++) {
        var targetIdx = i + scrollIndex;
        if (targetIdx >= stations.length || targetIdx < 0) break;
        var station = stations[targetIdx];
        var stationName = station._displayName;
        if (stationName === undefined) {
            var rawStationName = ("" + station.name) === "" ? "⚠ Undefined Name" : "" + station.name;
            var fitted = SncfTheme.fitName(state, rawStationName, CFG.stationList.nameMaxLen);
            fitted = fitted.split("-Via-")[0];
            stationName = SncfTheme.displayStationName(fitted);
            station._displayName = stationName;
        }
        var rowY = panel.y + startY + (rowStep * i);
        var isTerminusRow = station.id === lastStationId;
        var isCurrentStationRow = station.id === currentStationId;
        var hasBigDot = isTerminusRow || isCurrentStationRow;
        if (hasBigDot) {
            var dotCenterX = lineX + CFG.stationList.dotOffset + (CFG.stationList.dotSize / 2);
            var dotCenterY = rowY + CFG.stationList.dotCenterOffset;
            var backSize = CFG.stationList.dotSize * CFG.stationList.highlightDotBackScale;
            if (isCurrentStationRow) {
                var anim = computeCurrentDotAnimation(scroll.currentDotAnimTimer || 0);
                var semitpSize = CFG.stationList.dotSize * (CFG.stationList.currentSemitpScale
                    + anim.backdot3Progress * (CFG.stationList.currentSemitpGrownScale - CFG.stationList.currentSemitpScale));
                img(ctx, "aca:images/dot_semitp.png", dotCenterX - (semitpSize / 2), dotCenterY - (semitpSize / 2), semitpSize, semitpSize, applyFade(0xFFFFFF), CFG.stationList.currentSemitpZOrder);
                var backdot2Size = CFG.stationList.dotSize * (CFG.stationList.currentBackdot2Scale
                    + anim.backdot2Progress * (CFG.stationList.currentSemitpScale - CFG.stationList.currentBackdot2Scale));
                img(ctx, "aca:images/dot.png", dotCenterX - (backdot2Size / 2), dotCenterY - (backdot2Size / 2), backdot2Size, backdot2Size, applyFade(0xFFFFFF), CFG.stationList.currentBackdot2ZOrder);
            }
            img(ctx, "aca:images/dot.png", dotCenterX - (backSize / 2), dotCenterY - (backSize / 2), backSize, backSize, applyFade(lineColor), isCurrentStationRow ? CFG.stationList.currentBackdotZOrder : CFG.z.text);
            var frontSize = CFG.stationList.dotSize * CFG.stationList.highlightDotScale;
            if (isCurrentStationRow) {
                var arrowCenterY = dotCenterY + (anim.arrowProgress * (backSize / 2) * CFG.stationList.currentArrowDropRatio);
                var arrowHeight = frontSize * CFG.stationList.currentArrowRatio;
                img(ctx, "aca:images/arrow_down.png", dotCenterX - (frontSize / 2), arrowCenterY - (arrowHeight / 2), frontSize, arrowHeight, backgroundColor, CFG.stationList.currentArrowZOrder);
            } else {
                img(ctx, "aca:images/dot.png", dotCenterX - (frontSize / 2), dotCenterY - (frontSize / 2), frontSize, frontSize, backgroundColor, CFG.stationList.highlightDotZOrder);
            }
        } else {
            img(ctx, "aca:images/dot.png", lineX + CFG.stationList.dotOffset, rowY + CFG.stationList.dotYOffset, CFG.stationList.dotSize, CFG.stationList.dotSize, backgroundColor, CFG.z.text);
        }
        txt(ctx, stationName, textX, rowY, hasBigDot ? CFG.stationList.textScale : CFG.stationList.nonBoldTextScale, headerBandFade(applyFade(SncfColors.OnTimeWhite), rowY), "l", CFG.z.text, { size: [nameWidth, CFG.stationList.stopHeight], bold: hasBigDot });
    }
    if (scroll.exitingStation != null && scroll.exitOffset > rowStep) {
        var exitBaseMargin = scroll.exitingHasStationsBefore ? CFG.stationList.fadeDotsHeadroom : CFG.stationList.topMargin;
        var exitRestY = panel.y + 1.5 + exitBaseMargin;
        var exitRowY = exitRestY - scroll.exitOffset;
        if (scroll.exitingHasStationsBefore) {
            var exitLineStartY = exitRowY + CFG.stationList.dotCenterOffset;
            var exitFadeDotCenterX = lineX + (CFG.stationList.lineWidth / 2);
            drawStationListFadeDots(ctx, exitFadeDotCenterX, exitLineStartY, lineColor, backgroundColor, function (c) { return c; });
        }
        var exitStationName = scroll.exitingStation._displayName;
        if (exitStationName === undefined) exitStationName = "" + scroll.exitingStation.name;
        img(ctx, "aca:images/dot.png", lineX + CFG.stationList.dotOffset, exitRowY + CFG.stationList.dotYOffset, CFG.stationList.dotSize, CFG.stationList.dotSize, backgroundColor, CFG.z.text);
        txt(ctx, exitStationName, textX, exitRowY, CFG.stationList.nonBoldTextScale, headerBandFade(SncfColors.OnTimeWhite, exitRowY), "l", CFG.z.text, { size: [nameWidth, CFG.stationList.stopHeight] });
    }
}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);
    const panels = getContentPanels(pids);
    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const departure = arrivals.get(0);
    if (departure != null) {
        updateStationList(state, departure, panels.right.height);
    }
    const isArrivals = ConfigHandler.get(state, "arrivals") || (departure != null && state.scroll.isTerminating);
    const colors = LiveTheme.resolveColors(isArrivals);
    const labelText = isArrivals ? LiveTheme.t(state, "arrival") : LiveTheme.t(state, "departure");
    LiveTheme.drawBackground(ctx, pids, colors.background);
    LiveTheme.drawHeader(ctx, pids, colors.header, labelText, colors.background, false, isArrivals);
    drawHeaderShadowAwt(ctx, state, pids, colors.background);
    drawFooter(ctx, state, pids, colors.background, colors.header, departure);
    if (departure != null) {
        const routeInfo = TrainUtils.getRouteInfo(state, departure);
        const baseTime = isArrivals ? departure.arrivalTime() : departure.departureTime();
        const schedule = TrainUtils.getStableSchedule(state, departure, baseTime);
        const delay = TrainUtils.applyForceDelay(state, schedule);
        const originStation = state.scroll.fixedStations && state.scroll.fixedStations.length > 0 ? state.scroll.fixedStations[0] : null;
        const destination = state.scroll.isTerminating
            ? (originStation == null || originStation.name === "" ? "⚠ Undefined Name" : "" + originStation.name)
            : (departure.destination() == "" ? "⚠ Undefined Name" : "" + departure.destination());
        drawLeftPanelBlocks(ctx, state, panels.left, colors.header, colors.background, schedule.scheduledTime, delay, routeInfo, destination, departure, isArrivals);
        if (state.scroll.stations.length > 0) {
            drawStationList(ctx, state, state.scroll, panels.right, colors.background);
        } else {
            drawRightPanelDebugFill(ctx, panels.right);
        }
    } else {
        drawNoTrainMessage(ctx, state, pids, isArrivals);
    }
    LiveTheme.drawFrame(ctx, state, pids);
}
