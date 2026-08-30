include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/animation_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:components/live_theme.js"));

const CFG = {
    z: { content: 3, text: 4 },
    voie: {
        margin: 1, width: 9, heightRatio: 0.8, capRatio: 30 / 705,
        labelScaleRatio: 0.28 / 10, valueScaleRatio: 0.6665 / 10, labelValueGapRatio: 0.6 / 10,
        expandedBoost: 1.1, compactReduction: 0.9, busIconRatio: 0.6
    },
    messageZoneHeightRatio: 1 / 10,
    marquee: { scale: 0.5, waitTime: 5, margin: 2, noScrollThreshold: 111.5 },
    trainNumberColor: 0x87CEFA,
    trainNumberArrivalColor: 0x00A651,
    row: {
        countDefault: 7, borderHeight: 0.5, textMarginLeft: 4, delayThresholdSeconds: 60,
        expandedHeightRatio: 1.5, scaleBaselineUnits: 7,
        expandedTopZoneRatio: 0.5, expandedZoneOuterMargin: 0.5
    },
    pill: {
        heightRatio: 0.8, heightExtra: 0.25, iconRatio: 0.65,
        labelScale: 0.2, labelYOffset: 0.2, innerGap: 0.25, trailingGap: -2.125,
        cycleSeconds: 5, delayAlternateSeconds: 2
    },
    peekBadgeSizeRatio: 1,
    trainInfo: { alternateSeconds: 2, gap: 7, numberScaleRatio: 0.55, numberWidthRef: "000000" },
    lineBadge: { sizeRatio: 1.6, gap: 0.5, textXOffset: 0.15 },
    trainType: { scaleRatio: 0.7, minScaleRatio: 0.7, maxWidthRef: "ABCDEF" },
    destination: { maxLen: 30, busIconGap: 1, busIconScale: 1.3 },
    stops: { scrollDurationSeconds: 0.4, rightArrowRatio: 30 / 46, scaleRatio: 0.75, gap: 1.5, rightMargin: 2, lineGap: 4, zOrder: 1 },
    comp: {
        pillHeight: 2, pillMargin: 1.1, pillMarginBottom: 0.5, trainMaxWidthRatio: 0.84,
        blockGap: 0.25, blockMargin: 0.5, blockHeightRatio: 0.45, widthMultiplier: 1.6,
        motriceNativeWidth: 450, motriceNativeHeight: 420
    },
    page: { maxCompactByExpanded: [7, 5, 4, 3, 2, 0], holdSeconds: 15, fadeSeconds: 0.5 },
    comprowTable: {
        1: { superRows: 1, classiqueRows: 3 },
        2: { superRows: 2, classiqueRows: 2 },
        3: { superRows: 3, classiqueRows: 0 }
    },
    pageDot: { size: 3.6, gap: 1, textScale: 0.28, textXOffset: 0.15 },
    noTrainMessageScale: 0.6
};
CFG.pill.timeScale = LIVE_CLOCK_SCALE * 1.125;
CFG.row.superHeightRatio = CFG.row.expandedHeightRatio * 2;
CFG.page.maxExpanded = CFG.page.maxCompactByExpanded.length - 1;
CFG.voie.zOrder = CFG.z.content + 1;
CFG.voie.textZOrder = CFG.voie.zOrder + 1;

function create(ctx, state, pids) {
    ConfigHandler.init(state, { departureFormat: "0-8" });
    SncfTheme.init(state);
}

function dispose(ctx, state, pids) {}

function parseDepartureFormat(state) {
    const dfConf = ConfigHandler.get(state, "departureFormat");
    if (dfConf && dfConf.indexOf("-") !== -1) {
        const dfParts = dfConf.split("-");
        const dfExpanded = Math.max(0, Math.min(20, parseInt(dfParts[0])));
        const dfCompact = Math.max(0, Math.min(20, parseInt(dfParts[1])));
        if (!isNaN(dfExpanded) && !isNaN(dfCompact) && (dfExpanded + dfCompact) > 0) {
            return { expandedCount: dfExpanded, compactCount: dfCompact };
        }
    }
    return { expandedCount: 0, compactCount: CFG.row.countDefault };
}

function computePagePlan(state) {
    const pageCount = Math.max(1, Math.min(9, ConfigHandler.get(state, "pageCount")));
    const requested = parseDepartureFormat(state);
    const compositionRowCount = ConfigHandler.get(state, "compositionRowCount");
    const pages = [];
    if (compositionRowCount > 0) {
        const comprowClamped = Math.min(Math.max(1, compositionRowCount), 3);
        const comprowFormat = CFG.comprowTable[comprowClamped];
        pages.push({ expandedCount: comprowFormat.superRows + comprowFormat.classiqueRows, compactCount: 0, superCount: comprowFormat.superRows });
        var remainingExpanded = Math.max(0, requested.expandedCount - comprowFormat.superRows);
        for (var p = 1; p < pageCount; p++) {
            var pageExpanded = Math.min(remainingExpanded, CFG.page.maxExpanded);
            var pageCompact = CFG.page.maxCompactByExpanded[pageExpanded];
            pages.push({ expandedCount: pageExpanded, compactCount: pageCompact, superCount: 0 });
            remainingExpanded -= pageExpanded;
        }
    } else if (pageCount <= 1) {
        pages.push({ expandedCount: requested.expandedCount, compactCount: requested.compactCount, superCount: 0 });
    } else {
        var remainingExpanded = requested.expandedCount;
        for (var p = 0; p < pageCount; p++) {
            var pageExpanded = Math.min(remainingExpanded, CFG.page.maxExpanded);
            var pageCompact = CFG.page.maxCompactByExpanded[pageExpanded];
            pages.push({ expandedCount: pageExpanded, compactCount: pageCompact, superCount: 0 });
            remainingExpanded -= pageExpanded;
        }
    }
    var startIndex = 0;
    pages.forEach(function (page) {
        page.rowCount = page.expandedCount + page.compactCount;
        page.startIndex = startIndex;
        startIndex += page.rowCount;
    });
    return pages;
}

function updatePageTransition(state, pageCount) {
    if (pageCount <= 1) return { activePageIndex: 0, fadeRatio: 0, phase: "hold", justChanged: false };
    if (!state.pageTransition) {
        state.pageTransition = { phase: "hold", timer: 0, activePageIndex: 0 };
    }
    const t = state.pageTransition;
    if (t.activePageIndex >= pageCount) t.activePageIndex = 0;
    t.timer += Timing.delta();
    var justChanged = false;
    if (t.phase === "hold") {
        if (t.timer >= CFG.page.holdSeconds) {
            t.phase = "fadeOut";
            t.timer = 0;
        }
    } else if (t.phase === "fadeOut") {
        if (t.timer >= CFG.page.fadeSeconds) {
            t.phase = "fadeIn";
            t.timer = 0;
            t.activePageIndex = (t.activePageIndex + 1) % pageCount;
            justChanged = true;
        }
    } else {
        if (t.timer >= CFG.page.fadeSeconds) {
            t.phase = "hold";
            t.timer = 0;
        }
    }
    var fadeRatio = 0;
    if (t.phase === "fadeOut") {
        fadeRatio = Math.min(1, t.timer / CFG.page.fadeSeconds);
    } else if (t.phase === "fadeIn") {
        fadeRatio = 1 - Math.min(1, t.timer / CFG.page.fadeSeconds);
    }
    return { activePageIndex: t.activePageIndex, fadeRatio: fadeRatio, phase: t.phase, justChanged: justChanged };
}

function computeRowLayout(pids, rowFormat, messageZoneHeight) {
    const contentTop = LIVE_HEADER_HEIGHT;
    const contentHeight = pids.height - contentTop - messageZoneHeight;
    const rowCount = rowFormat.rowCount;
    const plainExpandedCount = rowFormat.expandedCount - rowFormat.superCount;
    const totalUnits = (plainExpandedCount * CFG.row.expandedHeightRatio) + (rowFormat.superCount * CFG.row.superHeightRatio) + rowFormat.compactCount;
    const unitHeight = (contentHeight - ((rowCount - 1) * CFG.row.borderHeight)) / totalUnits;
    const rows = [];
    var y = contentTop;
    for (var i = 0; i < rowCount; i++) {
        var isSuperRow = i < rowFormat.superCount;
        var isExpandedRow = i < rowFormat.expandedCount;
        var height;
        if (isSuperRow) {
            height = unitHeight * CFG.row.superHeightRatio;
        } else if (isExpandedRow) {
            height = unitHeight * CFG.row.expandedHeightRatio;
        } else {
            height = unitHeight;
        }
        rows.push({ y: y, height: height, centerY: y + (height / 2), isExpandedRow: isExpandedRow, isSuperRow: isSuperRow });
        y += height + CFG.row.borderHeight;
    }
    const rowScale = totalUnits > 0 ? (CFG.row.scaleBaselineUnits / totalUnits) : 1;
    return { contentTop: contentTop, rows: rows, rowScale: rowScale, unitHeight: unitHeight };
}

function drawRowDividers(ctx, pids, lineColor, layout, bgColor, fadeRatio) {
    const fadedLineColor = fadeColor(lineColor, bgColor, fadeRatio);
    for (var i = 1; i < layout.rows.length; i++) {
        var gapTop = layout.rows[i - 1].y + layout.rows[i - 1].height;
        rect(ctx, 0, gapTop, pids.width, CFG.row.borderHeight, fadedLineColor, CFG.z.content);
    }
}

function drawRowTrainInfo(ctx, state, x, centerY, typeScale, typeMaxWidth, numberScale, numberCharHeight, columnWidth, arrival, lineColor, lowerCenterY, bgColor, fadeRatio) {
    const fade = function (c) { return fadeColor(c, bgColor, fadeRatio); };
    const routeInfo = TrainUtils.getRouteInfo(state, arrival);
    const trainTypeName = TrainUtils.getNameOfType(routeInfo.trainType) || ("" + routeInfo.trainType).toUpperCase();
    const trainNumber = routeInfo.trainNumber;
    const lineName = TrainUtils.resolveLineName(routeInfo);
    const isRer = TrainUtils.isMatchingOneAliasOfType(trainTypeName, TRAIN_TYPES.RER);
    const isTransilien = TrainUtils.isMatchingOneAliasOfType(trainTypeName, TRAIN_TYPES.TRANSILIEN);
    const isRerOrTransilien = (isRer || isTransilien) && lineName != null;
    const slotCenterX = x + (columnWidth / 2);

    if (isRerOrTransilien) {
        const badgeSize = numberCharHeight * CFG.lineBadge.sizeRatio;
        const iconSize = badgeSize;
        const lineTextHeight = RenderUtils.getCharHeight(numberScale);
        const badgeColor = fade(arrival.routeColor());
        const iconTexture = isRer ? "idf:images/rer_line_white.png" : "idf:images/transilien_line_white.png";
        const comboX = slotCenterX - ((iconSize + CFG.lineBadge.gap + badgeSize) / 2);

        function drawBadge(y) {
            img(ctx, iconTexture, comboX, y - (iconSize / 2), iconSize, iconSize, fade(SncfColors.OnTimeWhite), CFG.z.text);
            img(ctx, "sncf:images/platform_border.png", comboX + iconSize + CFG.lineBadge.gap, y - (badgeSize / 2), badgeSize, badgeSize, badgeColor, CFG.z.text);
            txt(ctx, lineName, comboX + iconSize + CFG.lineBadge.gap + (badgeSize / 2) + CFG.lineBadge.textXOffset, y - (lineTextHeight / 2), numberScale, badgeColor, "c", CFG.z.text + 1);
        }
        function drawRerNumber(y) {
            txt(ctx, trainNumber, slotCenterX, y - (numberCharHeight / 2), numberScale, fade(lineColor), "c", CFG.z.text);
        }

        if (lowerCenterY != null) {
            drawBadge(centerY);
            drawRerNumber(lowerCenterY);
        } else {
            const rerPhase = Math.floor(Timing.currentTimeMillis() / (CFG.trainInfo.alternateSeconds * 1000)) % 2;
            if (rerPhase === 0) {
                drawBadge(centerY);
            } else {
                drawRerNumber(centerY);
            }
        }
        return x + columnWidth;
    }

    const typeWidthAtScale = RenderUtils.getStringWidth(trainTypeName, typeScale);
    const typeShrinkRatio = typeWidthAtScale > typeMaxWidth ? Math.max(CFG.trainType.minScaleRatio, typeMaxWidth / typeWidthAtScale) : 1;
    const effectiveTypeScale = typeScale * typeShrinkRatio;
    const typeCharHeight = RenderUtils.getCharHeight(effectiveTypeScale);
    const typeOffsetX = (trainTypeName === "SNCF") ? 0.15 : 0;

    function drawType(y) {
        txt(ctx, trainTypeName, slotCenterX + typeOffsetX, y - (typeCharHeight / 2), effectiveTypeScale, fade(SncfColors.OnTimeWhite), "c", CFG.z.text);
    }
    function drawNumber(y) {
        txt(ctx, trainNumber, slotCenterX, y - (numberCharHeight / 2), numberScale, fade(lineColor), "c", CFG.z.text);
    }

    if (lowerCenterY != null) {
        drawType(centerY);
        drawNumber(lowerCenterY);
    } else {
        const phase = Math.floor(Timing.currentTimeMillis() / (CFG.trainInfo.alternateSeconds * 1000)) % 2;
        if (phase === 0) {
            drawType(centerY);
        } else {
            drawNumber(centerY);
        }
    }

    return x + columnWidth;
}

function drawRowDestination(ctx, state, x, centerY, scale, arrival, isArrivals, bgColor, fadeRatio) {
    const route = isArrivals ? arrival.route() : null;
    const originPlatforms = route ? route.platforms : null;
    const originName = originPlatforms && originPlatforms.length > 0 ? ("" + originPlatforms[0].stationName) : "";
    const rawText = isArrivals ? originName : ("" + arrival.destination());
    const rawDestination = rawText === "" ? "⚠ Undefined Name" : rawText;
    const fitted = SncfTheme.fitName(state, rawDestination, CFG.destination.maxLen).split("-Via-")[0];
    const destinationName = SncfTheme.displayStationName(fitted);
    const charHeight = RenderUtils.getCharHeight(scale);
    const isBusType = TrainUtils.isBusType(state, arrival);
    const whiteColor = fadeColor(SncfColors.OnTimeWhite, bgColor, fadeRatio);
    var textX = x;
    if (isBusType) {
        const iconSize = charHeight * CFG.destination.busIconScale;
        img(ctx, "sncf:images/pictograms/bus.png", x, centerY - (iconSize / 2), iconSize, iconSize, whiteColor, CFG.z.text);
        textX = x + iconSize + CFG.destination.busIconGap;
    }
    txt(ctx, destinationName, textX, centerY - (charHeight / 2), scale, whiteColor, "l", CFG.z.text, { bold: true });
}

function getRowStopsCache(state, arrival, rowIndex) {
    const route = arrival.route();
    const platforms = route ? route.platforms : null;
    if (platforms == null || platforms.length === 0) return null;
    const routeKey = "" + arrival.routeId() + "_" + arrival.departureIndex();
    const cacheSlot = "rowStops" + rowIndex;
    const existing = state[cacheSlot];
    if (existing && existing.key === routeKey) {
        return existing;
    }
    const stations = [];
    platforms.forEach(function (platform) {
        stations.push({
            name: platform.stationName === "" ? "⚠ Undefined Name" : "" + platform.stationName,
            id: "" + platform.platformId
        });
    });
    const currentStationId = "" + arrival.platformId();
    const currentIndex = stations.findIndex(function (s) { return s.id === currentStationId; });
    const remaining = currentIndex !== -1
        ? stations.slice(currentIndex + 1, stations.length - 1)
        : stations.slice(0, stations.length - 1);
    const names = remaining.map(function (s) {
        const shortened = SncfTheme.shortenNames(state, s.name).split("-Via-")[0];
        return SncfTheme.displayStationName(shortened);
    });
    const cacheEntry = { key: routeKey, names: names, pagesKey: null, pages: null };
    state[cacheSlot] = cacheEntry;
    return cacheEntry;
}

function buildRowStopsPages(names, scale, arrowHeight, maxWidth, gap) {
    const arrowWidth = arrowHeight * CFG.stops.rightArrowRatio;
    const viaWidth = RenderUtils.getStringWidth("via", scale);
    const pages = [];
    var currentPage = [];
    var currentWidth = 0;
    names.forEach(function (name, idx) {
        const isFirst = idx === 0;
        const prefixWidth = isFirst ? viaWidth : arrowWidth;
        const itemWidth = prefixWidth + gap + RenderUtils.getStringWidth(name, scale) + gap;
        if (currentPage.length > 0 && currentWidth + itemWidth > maxWidth) {
            pages.push(currentPage);
            currentPage = [];
            currentWidth = 0;
        }
        currentPage.push({ name: name, isFirst: isFirst });
        currentWidth += itemWidth;
    });
    if (currentPage.length > 0) pages.push(currentPage);
    return pages;
}

function fadeColor(color, bgColor, fadeRatio) {
    return RenderUtils.blendColor(color, bgColor, 1 - fadeRatio);
}

function drawRowStopsPage(ctx, x, centerY, scale, arrowHeight, arrowColor, items, gap, bgColor, fadeRatio) {
    const arrowWidth = arrowHeight * CFG.stops.rightArrowRatio;
    const charHeight = RenderUtils.getCharHeight(scale);
    const fadedArrowColor = fadeColor(arrowColor, bgColor, fadeRatio);
    const fadedTextColor = fadeColor(SncfColors.OnTimeWhite, bgColor, fadeRatio);
    var cursorX = x;
    items.forEach(function (item) {
        if (item.isFirst) {
            txt(ctx, "via", cursorX, centerY - (charHeight / 2), scale, fadedArrowColor, "l", CFG.stops.zOrder);
            cursorX += RenderUtils.getStringWidth("via", scale) + gap;
        } else {
            img(ctx, "sncf:images/right_arrow.png", cursorX, centerY - (arrowHeight / 2), arrowWidth, arrowHeight, fadedArrowColor, CFG.stops.zOrder);
            cursorX += arrowWidth + gap;
        }
        txt(ctx, item.name, cursorX, centerY - (charHeight / 2), scale, fadedTextColor, "l", CFG.stops.zOrder);
        cursorX += RenderUtils.getStringWidth(item.name, scale) + gap;
    });
}

function drawRowVoieBlock(ctx, state, pids, centerY, blockHeight, headerColor, arrival, unitHeight, bgColor, fadeRatio, isExpandedRow) {
    const blockWidth = CFG.voie.width;
    const blockMargin = CFG.voie.margin;
    const blockX = pids.width - blockMargin - blockWidth;
    const blockY = centerY - (blockHeight / 2);
    const capHeight = blockWidth * CFG.voie.capRatio;
    const whiteHeight = blockHeight - (capHeight * 2);
    const fadedWhite = fadeColor(0xFFFFFF, bgColor, fadeRatio);
    const fadedHeader = fadeColor(headerColor, bgColor, fadeRatio);
    img(ctx, "sncf:images/top_div.png", blockX, blockY, blockWidth, capHeight, fadedWhite, CFG.voie.zOrder);
    img(ctx, "aca:images/white.png", blockX, blockY + capHeight, blockWidth, whiteHeight, fadedWhite, CFG.voie.zOrder);
    img(ctx, "sncf:images/bot_div.png", blockX, blockY + capHeight + whiteHeight, blockWidth, capHeight, fadedWhite, CFG.voie.zOrder);
    const blockCenterX = blockX + (blockWidth / 2);
    const isBusType = TrainUtils.isBusType(state, arrival);
    if (isBusType) {
        const busIconSize = blockWidth * CFG.voie.busIconRatio;
        img(ctx, "sncf:images/pictograms/bus.png", blockCenterX - (busIconSize / 2), centerY - (busIconSize / 2), busIconSize, busIconSize, fadedHeader, CFG.voie.textZOrder);
        return;
    }
    const platformText = arrival != null ? ("" + arrival.platformName()).split("|")[0] : "";
    const textBaselineHeight = unitHeight * CFG.voie.heightRatio * (isExpandedRow ? CFG.voie.expandedBoost : CFG.voie.compactReduction);
    const labelScale = textBaselineHeight * CFG.voie.labelScaleRatio;
    const valueScale = textBaselineHeight * CFG.voie.valueScaleRatio;
    const labelValueGap = blockHeight * CFG.voie.labelValueGapRatio;
    const labelCharHeight = RenderUtils.getCharHeight(labelScale);
    const valueCharHeight = RenderUtils.getCharHeight(valueScale);
    const totalTextHeight = labelCharHeight + labelValueGap + valueCharHeight;
    const textStartY = blockY + ((blockHeight - totalTextHeight) / 2);
    txt(ctx, LiveTheme.t(state, "track"), blockCenterX, textStartY, labelScale, fadedHeader, "c", CFG.voie.textZOrder);
    txt(ctx, platformText, blockCenterX, textStartY + labelCharHeight + labelValueGap, valueScale, fadedHeader, "c", CFG.voie.textZOrder, { bold: true });
}

function drawRowTimeText(ctx, timeScale, timeCharHeight, centerY, scheduledTime, marginLeft, bgColor, fadeRatio) {
    const timeText = RenderUtils.formatTime(scheduledTime);
    txt(ctx, timeText, marginLeft, centerY - (timeCharHeight / 2), timeScale, fadeColor(SncfColors.DepartureYellow, bgColor, fadeRatio), "l", CFG.z.text, { bold: true });
    return marginLeft + RenderUtils.getBoldStringWidth(timeText, timeScale);
}

function computeRowPillHeight() {
    return (RenderUtils.getCharHeight(CFG.pill.timeScale) * CFG.pill.heightRatio) + (CFG.pill.heightExtra * 2);
}

function computeRowPillWidth(label) {
    const capWidth = computeRowPillHeight();
    const labelWidth = RenderUtils.getStringWidth(label, CFG.pill.labelScale);
    const middleWidth = CFG.pill.innerGap + labelWidth + CFG.pill.trailingGap;
    return capWidth + middleWidth + capWidth;
}

function drawRowStatusPill(ctx, state, centerX, centerY, isDelayed, delaySeconds, headerColor, bgColor, fadeRatio) {
    const labelScale = CFG.pill.labelScale;
    const pillHeight = computeRowPillHeight();
    const capWidth = pillHeight;
    const capRadius = capWidth / 2;
    const innerIconSize = pillHeight * CFG.pill.iconRatio;
    const pillColor = fadeColor(isDelayed ? SncfColors.LiveDelayPillBackground : headerColor, bgColor, fadeRatio);
    const iconColor = fadeColor(isDelayed ? SncfColors.LiveDelayPillIcon : SncfColors.OnTimeWhite, bgColor, fadeRatio);
    const iconTexture = isDelayed ? "sncf:images/delay.png" : "sncf:images/check.png";
    const delayedLabel = LiveTheme.t(state, "delayed");
    const onTimeLabel = LiveTheme.t(state, "onTime");
    const widthLabel = isDelayed ? delayedLabel : onTimeLabel;
    let displayText = widthLabel;
    const rightAlignLabel = true;
    if (isDelayed) {
        const subPhase = Math.floor(Timing.currentTimeMillis() / (CFG.pill.delayAlternateSeconds * 1000)) % 2;
        if (subPhase === 1) {
            displayText = RenderUtils.formatDelayAmount(delaySeconds);
        }
    }
    const delayedLabelWidth = RenderUtils.getStringWidth(delayedLabel, labelScale);
    const onTimeLabelWidth = RenderUtils.getStringWidth(onTimeLabel, labelScale);
    const displayWidth = RenderUtils.getStringWidth(displayText, labelScale);
    const textSlotWidth = Math.max(delayedLabelWidth, onTimeLabelWidth, displayWidth);
    const middleWidth = CFG.pill.innerGap + textSlotWidth + CFG.pill.trailingGap;
    const totalWidth = capWidth + middleWidth + capWidth;
    const x = centerX - (totalWidth / 2);
    const pillY = centerY - (pillHeight / 2);
    img(ctx, "aca:images/dot.png", x, pillY, capWidth, pillHeight, pillColor, CFG.z.content);
    img(ctx, "aca:images/white.png", x + capRadius, pillY, totalWidth - capWidth, pillHeight, pillColor, CFG.z.content);
    img(ctx, "aca:images/dot.png", x + totalWidth - capWidth, pillY, capWidth, pillHeight, pillColor, CFG.z.content);
    img(ctx, iconTexture, x + capRadius - (innerIconSize / 2), centerY - (innerIconSize / 2), innerIconSize, innerIconSize, iconColor, CFG.z.text);
    const labelCharHeight = RenderUtils.getCharHeight(labelScale);
    const labelYOffset = CFG.pill.labelYOffset;
    const labelX = rightAlignLabel ? (x + capWidth + CFG.pill.innerGap + textSlotWidth) : (x + capWidth + CFG.pill.innerGap);
    txt(ctx, displayText, labelX, centerY - (labelCharHeight / 2) + labelYOffset, labelScale, iconColor, rightAlignLabel ? "r" : "l", CFG.z.text);
    return x + totalWidth;
}

function drawRowPeekBadge(ctx, pids, centerY, badgeHeight, bgColor, fadeRatio) {
    const badgeSize = badgeHeight * CFG.peekBadgeSizeRatio;
    img(ctx, "aca:images/dot.png", 0 - (badgeSize / 2), centerY - (badgeSize / 2), badgeSize, badgeSize, fadeColor(SncfColors.LiveDelayPillBackground, bgColor, fadeRatio), CFG.z.content);
}

function drawMessageZone(ctx, state, pids, zoneTop, zoneHeight, backgroundColor) {
    rect(ctx, 0, zoneTop, pids.width, zoneHeight, backgroundColor, CFG.z.content);
    const formatted = SncfTheme.formatText(state, ConfigHandler.getTextSlots(state));
    if (formatted === "") return;
    const lineHeight = RenderUtils.getCharHeight(CFG.marquee.scale);
    const textY = zoneTop + ((zoneHeight - lineHeight) / 2);
    const textColor = SncfColors.OnTimeWhite;
    const targetX = pids.width;
    const marquee = AnimationManager.marqueeText(state, "live_departures_msg", formatted, {
        scale: CFG.marquee.scale,
        waitTime: CFG.marquee.waitTime,
        speed: ConfigHandler.get(state, "marqueeSpeed"),
        marginLeft: CFG.marquee.margin,
        maxWidth: targetX,
        noScrollThreshold: CFG.marquee.noScrollThreshold
    });
    if (!marquee.needsScroll) {
        txt(ctx, formatted, CFG.marquee.margin, textY, CFG.marquee.scale, textColor, "l", CFG.z.text, { italic: true });
        return;
    }
    for (var k = 0; k < 3; k++) {
        var copyBaseX = -marquee.offset + (k * marquee.loopWidth);
        if (copyBaseX > targetX) break;
        marquee.chars.forEach(function(ch) {
            var chX = copyBaseX + ch.x;
            if (chX + ch.width < 0 || chX > targetX) return;
            txt(ctx, ch.text, chX, textY, CFG.marquee.scale, textColor, "l", CFG.z.text, { italic: true });
        });
    }
}

function drawNoTrainMessage(ctx, state, pids, isArrivals, contentTop, contentBottom) {
    const lineHeight = RenderUtils.getCharHeight(CFG.noTrainMessageScale);
    const y = contentTop + ((contentBottom - contentTop - lineHeight) / 2);
    const color = isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor;
    const message = LiveTheme.t(state, isArrivals ? "noArrivals" : "noDepartures");
    txt(ctx, message, pids.width / 2, y, CFG.noTrainMessageScale, color, "c", CFG.z.text, { size: [pids.width, lineHeight] });
}

function drawRows(ctx, state, pids, colors, isArrivals, realDepartures, realCount, firstTime, lastTime, pages, pageTransition) {
    const rowFormat = pages[pageTransition.activePageIndex];
    const pageFadeRatio = pageTransition.fadeRatio;
    const showMessage = ConfigHandler.get(state, "showMessage");
    const messageZoneHeight = showMessage ? pids.height * CFG.messageZoneHeightRatio : 0;
    const layout = computeRowLayout(pids, rowFormat, messageZoneHeight);
    const lineColor = isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor;
    const fadedRailHeaderColor = fadeColor(colors.header, colors.background, pageFadeRatio);
    const fadedCarColor = fadeColor(lineColor, colors.background, pageFadeRatio);
    if (showMessage) {
        drawMessageZone(ctx, state, pids, pids.height - messageZoneHeight, messageZoneHeight, colors.background);
    }
    if (realCount < 2) {
        drawNoTrainMessage(ctx, state, pids, isArrivals, LIVE_HEADER_HEIGHT, pids.height - messageZoneHeight);
        return;
    }
    drawRowDividers(ctx, pids, lineColor, layout, colors.background, pageFadeRatio);
    const pageOffset = rowFormat.startIndex;
    const rowScale = layout.rowScale;
    const rowMarginLeft = CFG.row.textMarginLeft;
    const rowInfoGap = CFG.trainInfo.gap;
    const rowStopsGap = CFG.stops.gap;
    const rowStopsLineGap = CFG.stops.lineGap * rowScale;
    const delayedPillWidth = computeRowPillWidth(I18N_TABLE.fr.delayed);
    const onTimePillWidth = computeRowPillWidth(I18N_TABLE.fr.onTime);
    const maxPillWidth = Math.max(delayedPillWidth, onTimePillWidth);
    const timeRefWidthAtScale1 = RenderUtils.getBoldStringWidth("00:00", 1);
    const timeScale = maxPillWidth / timeRefWidthAtScale1;
    const timeCharHeight = RenderUtils.getCharHeight(timeScale);
    const pillCenterX = rowMarginLeft + (maxPillWidth / 2);
    const showPill = Math.floor(Timing.currentTimeMillis() / (CFG.pill.cycleSeconds * 1000)) % 2 === 1;
    const stopsScale = timeScale * CFG.stops.scaleRatio;
    const stopsArrowHeight = RenderUtils.getCharHeight(stopsScale);
    const stopsLineHeight = stopsArrowHeight + rowStopsLineGap;
    const typeScale = timeScale * CFG.trainType.scaleRatio;
    const typeMaxWidth = RenderUtils.getStringWidth(CFG.trainType.maxWidthRef, typeScale);
    const numberScale = timeScale * CFG.trainInfo.numberScaleRatio;
    const numberCharHeight = RenderUtils.getCharHeight(numberScale);
    const numberMaxWidth = RenderUtils.getStringWidth(CFG.trainInfo.numberWidthRef, numberScale);
    const badgeSize = numberCharHeight * CFG.lineBadge.sizeRatio;
    const comboWidth = badgeSize + CFG.lineBadge.gap + badgeSize;
    const trainInfoColumnWidth = Math.max(comboWidth, numberMaxWidth, typeMaxWidth);
    const timeColumnRightEdge = rowMarginLeft + maxPillWidth;
    for (var r = 0; r < rowFormat.rowCount; r++) {
        var arrival = TrainUtils.buildSimulatedDeparture(state, r + pageOffset, realDepartures, realCount, firstTime, lastTime);
        if (arrival == null) break;
        var row = layout.rows[r];
        if (!row.isExpandedRow) {
            rect(ctx, 0, row.y, pids.width, row.height, colors.background, CFG.stops.zOrder + 1);
        }
        var classiqueHeight = row.isSuperRow ? (row.height / 2) : row.height;
        var topZoneHeight = classiqueHeight * CFG.row.expandedTopZoneRatio;
        var bottomZoneHeight = classiqueHeight - topZoneHeight;
        var infoCenterY = row.isExpandedRow
            ? row.y + (CFG.row.expandedZoneOuterMargin / 2) + (topZoneHeight / 2)
            : row.centerY;
        var lowerZoneCenterY = row.isExpandedRow
            ? row.y + topZoneHeight + (bottomZoneHeight / 2) - (CFG.row.expandedZoneOuterMargin / 2)
            : null;
        var baseTime = isArrivals ? arrival.arrivalTime() : arrival.departureTime();
        var schedule = TrainUtils.getStableSchedule(state, arrival, baseTime);
        var delay = TrainUtils.applyForceDelay(state, schedule);
        var isDelayed = delay.time > CFG.row.delayThresholdSeconds;
        if (row.isExpandedRow) {
            drawRowTimeText(ctx, timeScale, timeCharHeight, infoCenterY, schedule.scheduledTime, rowMarginLeft, colors.background, pageFadeRatio);
            drawRowStatusPill(ctx, state, pillCenterX, lowerZoneCenterY, isDelayed, delay.time, colors.header, colors.background, pageFadeRatio);
        } else if (showPill) {
            drawRowStatusPill(ctx, state, pillCenterX, infoCenterY, isDelayed, delay.time, colors.header, colors.background, pageFadeRatio);
        } else {
            drawRowTimeText(ctx, timeScale, timeCharHeight, infoCenterY, schedule.scheduledTime, rowMarginLeft, colors.background, pageFadeRatio);
            if (isDelayed) {
                drawRowPeekBadge(ctx, pids, infoCenterY, timeCharHeight, colors.background, pageFadeRatio);
            }
        }
        var trainInfoRightEdgeX = drawRowTrainInfo(ctx, state, timeColumnRightEdge + rowInfoGap, infoCenterY, typeScale, typeMaxWidth, numberScale, numberCharHeight, trainInfoColumnWidth, arrival, lineColor, lowerZoneCenterY, colors.background, pageFadeRatio);
        var destinationX = trainInfoRightEdgeX + rowInfoGap;
        drawRowDestination(ctx, state, destinationX, infoCenterY, typeScale, arrival, isArrivals, colors.background, pageFadeRatio);
        if (row.isExpandedRow) {
            var stopsCache = getRowStopsCache(state, arrival, r);
            if (stopsCache != null && stopsCache.names.length > 0) {
                var stopsMaxWidth = (pids.width - CFG.voie.width - CFG.voie.margin - CFG.stops.rightMargin) - destinationX;
                var pagesFingerprint = stopsMaxWidth.toFixed(2);
                var stopsPages;
                if (stopsCache.pagesKey === pagesFingerprint) {
                    stopsPages = stopsCache.pages;
                } else {
                    stopsPages = buildRowStopsPages(stopsCache.names, stopsScale, stopsArrowHeight, stopsMaxWidth, rowStopsGap);
                    stopsCache.pagesKey = pagesFingerprint;
                    stopsCache.pages = stopsPages;
                }
                if (stopsPages.length > 0) {
                    var scrollSlot = "rowStopsScroll" + r;
                    var scroll = state[scrollSlot];
                    if (!scroll || pageTransition.justChanged) {
                        scroll = { phase: "hold", timer: 0, pageBase: 0 };
                        state[scrollSlot] = scroll;
                    }
                    if (scroll.pageBase >= stopsPages.length) scroll.pageBase = 0;
                    if (stopsPages.length > 1 && pageTransition.phase === "hold") {
                        var stopsHoldDuration = Math.max(2, CFG.page.holdSeconds / stopsPages.length);
                        scroll.timer += Timing.delta();
                        if (scroll.phase === "hold") {
                            if (scroll.timer >= stopsHoldDuration) {
                                scroll.phase = "scroll";
                                scroll.timer = 0;
                            }
                        } else {
                            if (scroll.timer >= CFG.stops.scrollDurationSeconds) {
                                scroll.phase = "hold";
                                scroll.timer = 0;
                                scroll.pageBase = (scroll.pageBase + 1) % stopsPages.length;
                            }
                        }
                    }
                    var stopsProgress = (stopsPages.length > 1 && scroll.phase === "scroll")
                        ? Math.min(1, scroll.timer / CFG.stops.scrollDurationSeconds)
                        : 0;
                    var stopsOffsetY = -stopsLineHeight * stopsProgress;
                    drawRowStopsPage(
                        ctx, destinationX, lowerZoneCenterY + stopsOffsetY,
                        stopsScale, stopsArrowHeight, lineColor,
                        stopsPages[scroll.pageBase], rowStopsGap, colors.background, pageFadeRatio
                    );
                    if (stopsPages.length > 1) {
                        var nextPageIndex = (scroll.pageBase + 1) % stopsPages.length;
                        drawRowStopsPage(
                            ctx, destinationX, lowerZoneCenterY + stopsLineHeight + stopsOffsetY,
                            stopsScale, stopsArrowHeight, lineColor,
                            stopsPages[nextPageIndex], rowStopsGap, colors.background, pageFadeRatio
                        );
                    }
                    rect(ctx, 0, row.y, pids.width, topZoneHeight, colors.background, CFG.z.content);
                }
            }
        }
        if (row.isSuperRow) {
            var compZoneTop = row.y + classiqueHeight;
            var compZoneHeight = row.height - classiqueHeight;
            rect(ctx, 0, compZoneTop, pids.width, compZoneHeight, colors.background, CFG.stops.zOrder + 1);
            var railCapWidth = CFG.comp.pillHeight;
            var railCapRadius = railCapWidth / 2;
            var railX = CFG.comp.pillMargin;
            var railWidth = pids.width - (CFG.comp.pillMargin * 2);
            var railY = compZoneTop + compZoneHeight - CFG.comp.pillMarginBottom - CFG.comp.pillHeight;
            img(ctx, "aca:images/dot.png", railX, railY, railCapWidth, CFG.comp.pillHeight, fadedRailHeaderColor, CFG.voie.zOrder);
            img(ctx, "aca:images/white.png", railX + railCapRadius, railY, railWidth - railCapWidth, CFG.comp.pillHeight, fadedRailHeaderColor, CFG.voie.zOrder);
            img(ctx, "aca:images/dot.png", railX + railWidth - railCapWidth, railY, railCapWidth, CFG.comp.pillHeight, fadedRailHeaderColor, CFG.voie.zOrder);
            var carCount = arrival.carCount ? arrival.carCount() : (arrival.cars() ? arrival.cars().length : 0);
            if (carCount > 0) {
                var blocksFullWidth = railWidth - railCapWidth;
                var carBlockHeight = compZoneHeight * CFG.comp.blockHeightRatio;
                var carBlockWidth = carBlockHeight * (CFG.comp.motriceNativeWidth / CFG.comp.motriceNativeHeight) * CFG.comp.widthMultiplier;
                var actualTrainWidth = (carBlockWidth * carCount) + (CFG.comp.blockGap * (carCount - 1));
                var maxTrainWidth = blocksFullWidth * CFG.comp.trainMaxWidthRatio;
                if (actualTrainWidth > maxTrainWidth) {
                    var compShrinkScale = maxTrainWidth / actualTrainWidth;
                    carBlockWidth *= compShrinkScale;
                    actualTrainWidth = maxTrainWidth;
                }
                var blocksInnerX = railX + railCapRadius + ((blocksFullWidth - actualTrainWidth) / 2);
                var carBlockY = railY - CFG.comp.blockMargin - carBlockHeight;
                var carRouteInfo = TrainUtils.getRouteInfo(state, arrival);
                var locoPositions = TrainUtils.resolveLocoPositions(state, carRouteInfo);
                var isTgvForCars = TrainUtils.isTgvFamily(carRouteInfo.trainType);
                var carLocoFlags = TrainUtils.resolveCarLocoFlags(locoPositions, carCount, isTgvForCars);
                for (var cb = 0; cb < carCount; cb++) {
                    var carBlockX = blocksInnerX + (cb * (carBlockWidth + CFG.comp.blockGap));
                    var isLocoCar = carLocoFlags[cb].isLocoCar;
                    var isFrontLoco = carLocoFlags[cb].isFrontLoco;
                    img(ctx, isLocoCar ? (isFrontLoco ? "sncf:images/car_front.png" : "sncf:images/car_back.png") : "aca:images/white.png", carBlockX, carBlockY, carBlockWidth, carBlockHeight, fadedCarColor, CFG.voie.zOrder);
                }
            }
        }
        var voieCenterY, voieHeight;
        if (row.isSuperRow) {
            voieCenterY = row.y + (classiqueHeight / 2);
            voieHeight = classiqueHeight - (CFG.voie.margin * 2);
        } else if (row.isExpandedRow) {
            voieCenterY = row.centerY;
            voieHeight = row.height - (CFG.voie.margin * 2);
        } else {
            voieCenterY = row.centerY;
            voieHeight = row.height * CFG.voie.heightRatio;
        }
        drawRowVoieBlock(ctx, state, pids, voieCenterY, voieHeight, colors.header, arrival, layout.unitHeight, colors.background, pageFadeRatio, row.isExpandedRow);
    }
}

function drawPageIndicator(ctx, state, pids, colors, lineColor, activePage) {
    const pageCount = Math.max(1, Math.min(9, ConfigHandler.get(state, "pageCount")));
    if (pageCount <= 1) return;
    const totalWidth = (pageCount * CFG.pageDot.size) + ((pageCount - 1) * CFG.pageDot.gap);
    const startX = (pids.width - totalWidth) / 2;
    const centerY = LIVE_HEADER_HEIGHT / 2;
    const textHeight = RenderUtils.getCharHeight(CFG.pageDot.textScale);
    for (var i = 0; i < pageCount; i++) {
        var dotX = startX + (i * (CFG.pageDot.size + CFG.pageDot.gap));
        var isActive = i === activePage;
        var dotColor = isActive ? lineColor : colors.background;
        var textColor = isActive ? colors.background : lineColor;
        img(ctx, "aca:images/dot.png", dotX, centerY - (CFG.pageDot.size / 2), CFG.pageDot.size, CFG.pageDot.size, dotColor, LIVE_ZORDERS.headerContent);
        txt(ctx, "" + (i + 1), dotX + (CFG.pageDot.size / 2) + CFG.pageDot.textXOffset, centerY - (textHeight / 2), CFG.pageDot.textScale, textColor, "c", LIVE_ZORDERS.headerContent + 1);
    }
}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);
    const isArrivals = ConfigHandler.get(state, "arrivals");
    const colors = LiveTheme.resolveColors(isArrivals);
    const labelText = isArrivals ? LiveTheme.t(state, "arrivals") : LiveTheme.t(state, "departures");
    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const realDepartures = TrainUtils.collectRealDepartures(state, arrivals, isArrivals, {
        maxScan: 20,
        filter: function(dep) { return isArrivals || !TrainUtils.isTerminatingHere(dep); }
    });
    const realCount = realDepartures.length;
    const firstTime = realCount >= 2 ? realDepartures[0].departureTime() : 0;
    const lastTime = realCount >= 2 ? realDepartures[realCount - 1].departureTime() : 0;
    const pages = computePagePlan(state);
    const pageTransition = updatePageTransition(state, pages.length);
    LiveTheme.drawBackground(ctx, pids, colors.background);
    LiveTheme.drawHeader(ctx, pids, colors.header, labelText, colors.background, false, isArrivals);
    drawPageIndicator(ctx, state, pids, colors, isArrivals ? CFG.trainNumberArrivalColor : CFG.trainNumberColor, pageTransition.activePageIndex);
    drawRows(ctx, state, pids, colors, isArrivals, realDepartures, realCount, firstTime, lastTime, pages, pageTransition);
    LiveTheme.drawFrame(ctx, state, pids);
}
