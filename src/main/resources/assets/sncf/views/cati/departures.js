include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/animation_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:modules/i18n_handler.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:components/train_composition.js"));

const CFG = {
    marquee: { marginLeft: 2, scale: 0.5, waitTime: 5, clockLeftMargin: 20.3, maskWidthRight: 3, noScrollThreshold: 111.5 },
    header: { frameBorderSize: 9.8, titleMarginTop: 1.6, titleScale: 0.45, scale: 0.32, marginTop: 0.75 },
    column: { train: 0.75, number: 12, time: 30.575, destination: 48.75, voie: 0 },
    subtitle: { scale: 0.28, marginBelow: 1.2, dotSize: 1.5, dotGap: 1.5 }
};
CFG.subtitle.startX = CFG.column.time;
CFG.lemanExpressWidth = RenderUtils.getStringWidth("Léman Express", 0.28);


function create(ctx, state, pids) {
    ConfigHandler.init(state);
    SncfTheme.init(state);
}

function dispose(ctx, state, pids) {}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);

    let showArrivals = ConfigHandler.get(state, "arrivals");

    const darkerBackground = showArrivals ? RenderUtils.getUV(0, 5, 5, 5) : RenderUtils.getUV(0, 0, 5, 5);

    let isImportant = ConfigHandler.get(state, "important");

    img(ctx, "sncf:images/colors.png", 0, 0, pids.width, pids.height, null, 0, darkerBackground);

    let showMessage = ConfigHandler.get(state, "showMessage");
    let maxDepartures = showMessage ? 9 : 10;
    let Height = pids.height / 10;

    let expandedCount = 0;
    let compactCount = maxDepartures;
    const dfConf = ConfigHandler.get(state, "departureFormat");
    if (dfConf && dfConf.indexOf("-") !== -1) {
        const dfParts = dfConf.split("-");
        const dfExpanded = Math.max(0, Math.min(20, parseInt(dfParts[0])));
        const dfCompact = Math.max(0, Math.min(20, parseInt(dfParts[1])));
        if (!isNaN(dfExpanded) && !isNaN(dfCompact) && (dfExpanded + dfCompact) > 0) {
            expandedCount = dfExpanded;
            compactCount = dfCompact;
        }
    }
    const totalDepartures = expandedCount + compactCount;
    const totalUnits = (expandedCount * 2) + compactCount;
    const usableHeight = pids.height - (showMessage ? Height : 0);
    const unitHeight = usableHeight / totalUnits;

    const rowLayout = [];
    var layoutY = 0;
    for (var row = 0; row < totalDepartures; row++) {
        var isExpandedRow = row < expandedCount;
        var rowHeight = isExpandedRow ? unitHeight * 2 : unitHeight;
        rowLayout.push({ row: row, y: layoutY, height: rowHeight, isExpandedRow: isExpandedRow });
        layoutY += rowHeight;
    }

    const lighterBackground = showArrivals ? RenderUtils.getUV(5, 5, 5, 5) : RenderUtils.getUV(5, 0, 5, 5);

    const reservationOnly = ConfigHandler.get(state, "reservationOnly");

    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const realDepartures = TrainUtils.collectRealDepartures(state, arrivals, showArrivals, {
        maxScan: 10,
        filter: function(dep) {
            if (!showArrivals && TrainUtils.isTerminatingHere(dep)) return false;
            if (reservationOnly) {
                const trainType = TrainUtils.getRouteInfo(state, dep).trainType.toLowerCase();
                if (TrainComposition.RESERVATION_TRAIN_TYPES[trainType] !== 1) return false;
            }
            return true;
        }
    });
    const realCount = realDepartures.length;
    const hasTrains = realCount >= 2;

    if (hasTrains) {
        rowLayout.forEach(function(r) {
            if (r.row % 2 === 0) {
                img(ctx, "sncf:images/colors.png", 0, r.y, pids.width, r.height, null, 1, lighterBackground);
            }
        });
    }

    img(ctx, `sncf:images/${showArrivals ? "arrival" : "departure"}_text.png`, 0, showMessage ? (showArrivals ? -1 : 0) : (showArrivals ? Height - 1 : Height), pids.width, pids.height, null, 4);

    const firstTime = hasTrains ? realDepartures[0].departureTime() : 0;
    const lastTime = hasTrains ? realDepartures[realCount - 1].departureTime() : 0;
    const startIndexConf = ConfigHandler.get(state, "startIndex");

    if (hasTrains) {
        rowLayout.forEach(function(r) {
            let realI = r.row + startIndexConf;
            let departure = TrainUtils.buildSimulatedDeparture(state, realI, realDepartures, realCount, firstTime, lastTime);

            if(departure !== null && departure !== undefined) {
                let info = TrainUtils.getRouteInfo(state, departure);
                let baseTime = showArrivals ? departure.arrivalTime() : departure.departureTime();
                let schedule = TrainUtils.getStableSchedule(state, departure, baseTime);
                schedule = { scheduledTime: schedule.scheduledTime, delay: TrainUtils.applyForceDelay(state, schedule) };

                if (r.isExpandedRow) {
                    const rowBackgroundUV = (r.row % 2 === 0) ? lighterBackground : darkerBackground;
                    drawStationList(ctx, state, pids, r.y, r.height, departure, r.row, rowBackgroundUV);
                }
                drawTrainInformation(ctx, state, pids, r.y, r.height, departure, info, schedule, !r.isExpandedRow);
            }
        });
    } else {
        drawNoTrainMessage(ctx, state, pids, showArrivals, 0, usableHeight);
    }

    if(showMessage) {
        drawMessageZone(ctx, state, pids, pids.height - Height, Height, isImportant, darkerBackground);

        SncfTheme.drawClock(ctx, pids, Timing.currentTimeMillis(), { zOrder: 10, drawBox: true });
    }

    if (!ConfigHandler.get(state, "hideFrame")) {
        SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 11 });
    }

    drawDeparturesHeader(ctx, pids, showArrivals, reservationOnly, ConfigHandler.get(state, "hideTitle"), ConfigHandler.get(state, "hideColumnHeaders"));
}

function drawNoTrainMessage(ctx, state, pids, showArrivals, contentTop, contentBottom) {
    const scale = 0.55;
    const lineHeight = RenderUtils.getCharHeight(scale);
    const y = contentTop + ((contentBottom - contentTop - lineHeight) / 2);
    const message = I18nHandler.t(state, showArrivals ? "noArrivals" : "noDepartures");
    txt(ctx, message, pids.width / 2, y, scale, 0xFFFFFF, "c", 2, { size: [pids.width, lineHeight] });
}

function drawDeparturesHeader(ctx, pids, showArrivals, isReservationOnly, hideTitle, hideColumnHeaders) {
    const HEADER_ZORDER = 12;
    const effectiveHideTitle = isReservationOnly ? false : hideTitle;

    if (!effectiveHideTitle) {
        const baseTitle = showArrivals ? "Arrivées" : "Départs";
        const titleText = isReservationOnly ? (baseTitle + " Grandes Lignes") : baseTitle;
        const titleY = -CFG.header.frameBorderSize + CFG.header.titleMarginTop;
        txt(ctx, titleText, CFG.column.train, titleY, CFG.header.titleScale, 0xFFFFFF, "l", HEADER_ZORDER, { bold: true });

        if (isReservationOnly) {
            const subtitleText = showArrivals
                ? "Mainline arrivals - Llegadas Grandes Líneas"
                : "Mainline departures - Salidas Grandes Líneas";
            txt(ctx, subtitleText, CFG.column.train, titleY + (7 * CFG.header.titleScale) + CFG.subtitle.marginBelow, CFG.subtitle.scale, 0xFFFFFF, "l", HEADER_ZORDER, { italic: true });
        } else {
            const enText = showArrivals ? "Arrivals" : "Departures";
            const esText = showArrivals ? "Llegadas" : "Salidas";
            const enWidth = RenderUtils.getStringWidth(enText, CFG.subtitle.scale);
            const enX = CFG.subtitle.startX;
            const dotX = enX + enWidth + CFG.subtitle.dotGap;
            const esX = dotX + CFG.subtitle.dotSize + CFG.subtitle.dotGap;
            const subtitleY = titleY + ((7 * CFG.header.titleScale) - (7 * CFG.subtitle.scale)) / 2 + 0.5;
            const dotY = subtitleY + ((7 * CFG.subtitle.scale) - CFG.subtitle.dotSize) / 2;

            txt(ctx, enText, enX, subtitleY, CFG.subtitle.scale, 0xFFFFFF, "l", HEADER_ZORDER, { italic: true });
            img(ctx, "aca:images/dot.png", dotX, dotY, CFG.subtitle.dotSize, CFG.subtitle.dotSize, 0xFFFFFF, HEADER_ZORDER);
            txt(ctx, esText, esX, subtitleY, CFG.subtitle.scale, 0xFFFFFF, "l", HEADER_ZORDER, { italic: true });
        }
    }

    if (effectiveHideTitle || hideColumnHeaders) return;

    const headerY = pids.height + CFG.header.marginTop;
    const voieX = pids.width - (6.9 * 0.25);

    txt(ctx, "train", CFG.column.train, headerY, CFG.header.scale, 0xFFFFFF, "l", HEADER_ZORDER);
    txt(ctx, "n°", CFG.column.number, headerY, CFG.header.scale, 0xFFFFFF, "l", HEADER_ZORDER);
    txt(ctx, "heure", CFG.column.time, headerY, CFG.header.scale, 0xFFFFFF, "l", HEADER_ZORDER);
    txt(ctx, showArrivals ? "provenance" : "destination", CFG.column.destination, headerY, CFG.header.scale, 0xFFFFFF, "l", HEADER_ZORDER);
    txt(ctx, "voie", voieX, headerY, CFG.header.scale, 0xFFFFFF, "r", HEADER_ZORDER);
}

function drawMessageZone(ctx, state, pids, zoneTop, zoneHeight, isImportant, darkerBackground) {
    if (isImportant) {
        rect(ctx, 0, zoneTop, pids.width, zoneHeight, SncfColors.ImportantBand, 6);
    }

    const formatted = SncfTheme.formatText(state, ConfigHandler.getTextSlots(state));
    if (formatted === "") return;

    const lineHeight = RenderUtils.getCharHeight(CFG.marquee.scale);
    const textY = zoneTop + (zoneHeight - lineHeight) / 2 - 0.3;
    const textColor = isImportant ? SncfColors.ImportantText : SncfColors.DepartureWhite;
    const targetX = pids.width - CFG.marquee.clockLeftMargin - CFG.marquee.maskWidthRight;

    const marquee = AnimationManager.marqueeText(state, "info_msg", formatted, {
        scale: CFG.marquee.scale,
        waitTime: CFG.marquee.waitTime,
        speed: ConfigHandler.get(state, "marqueeSpeed"),
        marginLeft: CFG.marquee.marginLeft,
        maxWidth: targetX,
        noScrollThreshold: CFG.marquee.noScrollThreshold
    });

    if (!marquee.needsScroll) {
        txt(ctx, formatted, CFG.marquee.marginLeft, textY, CFG.marquee.scale, textColor, "l", 7, { italic: true });
        return;
    }

    for (var k = 0; k < 3; k++) {
        var copyBaseX = -marquee.offset + k * marquee.loopWidth;
        if (copyBaseX > targetX) break;
        marquee.chars.forEach(function(ch) {
            var chX = copyBaseX + ch.x;
            if (chX + ch.width < 0 || chX > targetX) return;
            txt(ctx, ch.text, chX, textY, CFG.marquee.scale, textColor, "l", 7, { italic: true });
        });
    }

    if (isImportant) {
        rect(ctx, targetX, zoneTop, CFG.marquee.maskWidthRight, zoneHeight, SncfColors.ImportantBand, 8);
    } else {
        img(ctx, "sncf:images/colors.png", targetX, zoneTop, CFG.marquee.maskWidthRight, zoneHeight, null, 8, darkerBackground);
    }
}

function drawStationList(ctx, state, pids, startY, rowHeight, departure, rowIndex, backgroundUV) {
    const route = departure.route();
    const platforms = route ? route.platforms : null;
    if (platforms == null || platforms.length === 0) return;

    const scale = 0.5;
    const dotSize = 1.5;
    const dotGap = 1.5;
    const startX = 12;
    const rightMargin = 12;
    const drawWidth = pids.width - startX - rightMargin;
    const lineY = startY + rowHeight - 6;

    const routeKey = "" + departure.routeId() + "_" + departure.departureIndex();
    const cacheSlot = "stopSeg" + rowIndex;
    if (!state[cacheSlot] || state[cacheSlot].key !== routeKey) {
        const stops = [];
        platforms.forEach(function(platform) {
            stops.push({
                name: platform.stationName === "" ? "⚠ Undefined Name" : "" + platform.stationName,
                id: "" + platform.platformId
            });
        });

        const currentStation = "" + departure.platformId();
        const currentIdx = stops.findIndex(function(s) { return s.id === currentStation; });
        const ahead = currentIdx !== -1 ? stops.slice(currentIdx + 1) : stops;
        const names = ahead.map(function(s) { return SncfTheme.displayStationName(SncfTheme.fitName(state, s.name, 28)); });

        const segments = [];
        let cursor = 0;
        names.forEach(function(name, idx) {
            const runStartX = cursor;
            const chars = [];
            name.split("").forEach(function(ch) {
                const w = RenderUtils.getCharWidth(ch, scale) + scale;
                chars.push({ text: ch, x: cursor, width: w });
                cursor += w;
            });
            segments.push({ type: "run", text: name, x: runStartX, width: cursor - runStartX, chars: chars });
            if (idx < names.length - 1) {
                cursor += dotGap;
                segments.push({ type: "dot", x: cursor, width: dotSize });
                cursor += dotSize + dotGap;
            }
        });

        state[cacheSlot] = { key: routeKey, segments: segments, totalWidth: cursor };
    }

    const cache = state[cacheSlot];
    if (cache.segments.length === 0) return;

    if (!state.stopScrollAnims) state.stopScrollAnims = {};
    if (!state.stopScrollAnims[rowIndex] || state.stopScrollAnims[rowIndex].key !== cache.key) {
        state.stopScrollAnims[rowIndex] = { key: cache.key, offset: 0, phase: "wait", timeInState: 0 };
    }
    const anim = state.stopScrollAnims[rowIndex];

    const targetX = drawWidth * 0.8;
    const maxOffset = Math.max(0, cache.totalWidth - targetX);

    if (maxOffset > 0) {
        const waitTime = 3;
        const speed = ConfigHandler.get(state, "scrollSpeed");
        const delta = Timing.delta();
        anim.timeInState += delta;

        if (anim.phase === "wait") {
            if (anim.timeInState > waitTime) { anim.phase = "scroll"; anim.timeInState = 0; }
        } else if (anim.phase === "scroll") {
            anim.offset += speed * delta;
            if (anim.offset >= maxOffset) {
                anim.offset = maxOffset;
                anim.phase = "end";
                anim.timeInState = 0;
            }
        } else if (anim.phase === "end") {
            if (anim.timeInState > waitTime) {
                anim.phase = "wait";
                anim.timeInState = 0;
                anim.offset = 0;
            }
        }
    } else {
        anim.offset = 0;
    }

    const offset = anim.offset;

    cache.segments.forEach(function(seg) {
        const segX = startX + seg.x - offset;
        const segEndX = segX + seg.width;
        if (segEndX < startX || segX > startX + drawWidth) return;

        if (seg.type === "dot") {
            img(ctx, "aca:images/dot.png", segX, lineY + 1, seg.width, seg.width, SncfColors.DepartureYellow, 2);
            return;
        }

        if (segX >= startX && segEndX <= startX + drawWidth) {
            txt(ctx, seg.text, segX, lineY, scale, 0xFFFFFF, "l", 2);
        } else {
            seg.chars.forEach(function(ch) {
                const chX = startX + ch.x - offset;
                if (chX + ch.width < startX || chX > startX + drawWidth) return;
                txt(ctx, ch.text, chX, lineY, scale, 0xFFFFFF, "l", 2);
            });
        }
    });

    img(ctx, "sncf:images/colors.png", 0, startY, startX, rowHeight, null, 3, backgroundUV);
    img(ctx, "sncf:images/colors.png", pids.width - rightMargin, startY, rightMargin, rowHeight, null, 3, backgroundUV);
}

function platformBadgeTopY(startY, ROW_HEIGHT, size, isArrivalsHidden) {
    return startY + (isArrivalsHidden ? (ROW_HEIGHT - size) / 2 : size * 0.1);
}

function drawTrainInformation(ctx, state, pids, startY, ROW_HEIGHT, departure, trainInfo, schedule, isArrivalsHidden) {
    const routeInfo = trainInfo;
    const trainType = routeInfo.trainType;
    const trainNumber = routeInfo.trainNumber;

    let trainName = TrainUtils.findNameFromAlias(trainType) || trainType.toUpperCase();
    let isBusType = TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.CAR);

    let remainingHeight = isArrivalsHidden ? ROW_HEIGHT : ROW_HEIGHT - (8 * 0.5) - 1;

    let trainNamePixelSize = 0.28;
    let trainNumberPixelSize = 0.4;
    let trainTypeHeight = 10 * trainNamePixelSize + 7 * trainNumberPixelSize;
    let trainTypeStartY = startY + (remainingHeight - trainTypeHeight) / 2;

    let startX = 12;

    let lineName = TrainUtils.resolveLineName(routeInfo);
    let isRer = TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.RER);
    let isRerOrTransilien = (TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.TRANSILIEN) || isRer) && lineName != null;

    if(!isBusType) {
        if (isRerOrTransilien) {
            const iconTexture = isRer ? "idf:images/rer_line_white.png" : "idf:images/transilien_line_white.png";
            const iconW = 3.5, iconH = 3.5, badgeSize = 3.5, gap = 0.5;
            const comboWidth = iconW + gap + badgeSize;
            const comboHeight = Math.max(iconH, badgeSize);
            const comboX = (startX - comboWidth) / 2;
            const comboTopY = startY + (remainingHeight - comboHeight) / 2;
            const iconY = comboTopY + (comboHeight - iconH) / 2;
            const badgeY = comboTopY + (comboHeight - badgeSize) / 2;
            const textScale = 0.23;
            const textHeight = RenderUtils.getCharHeight(textScale);
            const textY = badgeY + (badgeSize - textHeight) / 2;

            img(ctx, iconTexture, comboX, iconY, iconW, iconH, null, 5);
            img(ctx, "sncf:images/platform_border.png", comboX + iconW + gap, badgeY, badgeSize, badgeSize, departure.routeColor(), 5);
            txt(ctx, lineName, comboX + iconW + gap + badgeSize / 2 + 0.15, textY, textScale, departure.routeColor(), "c", 5);
        } else {
            let trainIdentifier = TrainUtils.findTypeFromAlias(trainName) || TrainUtils.getType("SNCF");

            let trainTexture = trainIdentifier.texture || TrainUtils.getTextureOfType("SNCF");

            let iconSize = TrainUtils.getIconSize(trainTexture, { maxWidth: 5.5 });
            let iconWidth = iconSize[0];
            let iconHeight = iconSize[1];
            let trainTextureId = trainTexture.id || "blank";

            let showingTexture = TrainUtils.isShowingTexture(trainName);

            if(showingTexture) {
                img(ctx, "sncf:images/labels/" + trainTextureId + "_white.png", (startX - iconWidth) / 2, startY + (remainingHeight - iconHeight) / 2, iconWidth, iconHeight, null, 5);
            }
        }
    }

    const delay = schedule.delay;
    const timeLimitToShowDelay = 20;
    let showDelay = false;

    const onTimeMessages = ["à l'heure", "on time", "in orario"];
    let currentTime = Math.floor(Timing.currentTimeMillis() / 3000);
    let isOnTimeMessage = currentTime % 3 === 0;

    showDelay = delay.time > timeLimitToShowDelay;

    startX += 1;

    let departureTimePixelSize = 0.5;

    function getRemainingHeight(pxSize) {
        return startY + (remainingHeight - (8 * pxSize)) / 2;
    }

    txt(ctx, RenderUtils.formatTime(schedule.scheduledTime, "h"), 30.575, getRemainingHeight(departureTimePixelSize), departureTimePixelSize, 0xFEF103, "l", 5, { size: [55, 9.5], scaleXY: true });

    let showArrivals = ConfigHandler.get(state, "arrivals");

    let rawDestName = "" + (showArrivals ? departure.route().platforms[0].stationName : departure.destination());
    if (rawDestName === "") rawDestName = "⚠ Undefined Name";

    let destNameFitted = SncfTheme.fitName(state, rawDestName, 25).split("-Via-")[0];
    let destName = SncfTheme.displayStationName(destNameFitted);

    txt(ctx, destName, 48.75, getRemainingHeight(0.55), 0.65, 0xFFFFFF, "l", 5, { size: [100, 8.75], scaleXY: true });

    let size = isArrivalsHidden ? 6.9 : 8.1;
    const badgeTopY = platformBadgeTopY(startY, ROW_HEIGHT, size, isArrivalsHidden);

    if((departure.departureTime() - Timing.currentTimeMillis()) <= 20 * 60 * 1000) {
        txt(ctx, departure.platformName().split("|")[0], pids.width - (size * 0.725), badgeTopY + size * 0.275, 0.75, 0xFFFFFF, "c", 5, { size: [size * 0.75, size * 0.75], scaleXY: true });
        img(ctx, "sncf:images/platform_border.png", pids.width - (size * 1.25), badgeTopY, size, size, null, 5);
    } else if((departure.departureTime() - Timing.currentTimeMillis()) <= 2 * 60 * 60 * 1000) {
        let platformInfo = TrainUtils.getPlatformInfo(departure);

        if(platformInfo.hall != null && platformInfo.hall !== "") {
            img(ctx, "idf:images/rer_square.png", pids.width - (size * 1.25), badgeTopY, size, size, null, 5);
            txt(ctx, "Hall", pids.width - (size * 0.725), startY + 0.1, 0.75, 0x0c0c0c, "c", 6, { size: [size * 0.75, size * 0.75], scaleXY: true });

            let hallSize = 0.8;
            txt(ctx, platformInfo.hall, pids.width - (size * 0.725), startY + (4 * 0.75) + 0.2, hallSize, 0x0c0c0c, "c", 6, { size: [size * hallSize, size * hallSize], scaleXY: true, bold: true });
        }
    }

    if(isOnTimeMessage) {
        if(!isBusType) {
            if(showDelay) {
                txt(ctx, "retard", startX, trainTypeStartY, 0.32, 0xFEF103, "l", 5);
                txt(ctx, RenderUtils.formatTrainDelay(delay.time, !ConfigHandler.get(state, "exactDelay"), false) + ".", startX, trainTypeStartY + 10 * trainNamePixelSize, 0.32, 0xFEF103, "l", 5);
            } else {
                txt(ctx, onTimeMessages[currentTime % onTimeMessages.length], 12 + CFG.lemanExpressWidth * 0.35, startY + remainingHeight * 0.34, 0.3, 0xFFFFFF, "c", 5);
            }
        } else {
            Text.create("Train number")
            .text(trainNumber)
            .color(0xFFFFFF)
            .scale(trainNumberPixelSize)
                        .pos(12, trainTypeStartY + 10 * trainNamePixelSize)
            .zOrder(5)
            .draw(ctx);
        }
    } else {
        if(!isBusType && !isRerOrTransilien) {
            Text.create("Train name")
            .text(trainName)
            .size(62.5, 10)
            .scaleXY()
            .color(0xFDFCFC)
                        .scale(trainNamePixelSize)
            .pos(12, trainTypeStartY)
            .zOrder(5)
            .draw(ctx);
        }

        Text.create("Train number")
        .text(trainNumber)
        .color(0xFFFFFF)
        .scale(trainNumberPixelSize)
                .pos(12, trainTypeStartY + 10 * trainNamePixelSize)
        .zOrder(5)
        .draw(ctx);
    }

}
