include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/animation_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:components/train_composition.js"));
include(Resources.id("aca:components/advertising_manager.js"));

const CFG = {
    totalHeight: 68.6,
    bg: { refWidth: 1100, refHeight: 616, leftPanelWidth: 383, topAreaHeight: 555 },
    lineColor: { station: 0x6969AF, terminus: 0x76A56B },
    z: { importantBand: 11, marquee: 12, marqueeMask: 13 },
    comp: { raiseRatio: 0.5, defaultBgColor: 0x560e4c, bannerHeight: 23, zOrders: { bg: 7, mid: 8, marker: 9, letter: 10 } },
    trainDestLineGap: 0.8,
    stationList: { maxVisible: 10, maxVisibleWithComp: 7, maxRowsScrollingWithComp: 9, stopHeight: 5 },
    departureMessageDuration: 10,
    timeBetweenStates: 5,
    nearbyTrainThreshold: 13,
    marquee: { marginLeft: 2, scale: 0.5, waitTime: 5, clockLeftMargin: 20.3, maskWidthRight: 3, noScrollThreshold: 111.5 }
};

const BG_STATES = {
    departure: {
        panelColor: 0x004494,
        watermarkColor: 0xD8DCE6,
        watermarkTexture: "sncf:images/platform/departure.png",
        watermarkNativeWidth: 285,
        watermarkNativeHeight: 824,
        watermarkVisibleHeight: 370,
        watermarkMarginRight: 5,
        watermarkMarginBottom: 5
    },
    arrival: {
        panelColor: 0x006E30,
        watermarkColor: 0xD5E5C3,
        watermarkTexture: "sncf:images/platform/arrival.png",
        watermarkNativeWidth: 225,
        watermarkNativeHeight: 816,
        watermarkVisibleHeight: 350,
        watermarkWidthScale: 0.85,
        watermarkMarginRight: 30,
        watermarkMarginBottom: 5
    }
};

function getImportantBandHeight(pids) {
    const yScale = pids.height / CFG.bg.refHeight;
    return pids.height - (CFG.bg.topAreaHeight * yScale);
}

function getCompositionRaise(pids) {
    return getImportantBandHeight(pids) * CFG.comp.raiseRatio;
}

function drawStateBackground(ctx, pids, stateKey, zOrder, hasComposition) {
    const cfg = BG_STATES[stateKey];
    const xScale = pids.width / CFG.bg.refWidth;
    const yScale = pids.height / CFG.bg.refHeight;
    const leftPanelWidth = CFG.bg.leftPanelWidth * xScale;
    const topAreaHeight = CFG.bg.topAreaHeight * yScale;

    rect(ctx, 0, 0, leftPanelWidth, topAreaHeight, 0xFFFFFF, zOrder);
    rect(ctx, leftPanelWidth, 0, pids.width - leftPanelWidth, topAreaHeight, cfg.panelColor, zOrder);
    rect(ctx, 0, topAreaHeight, pids.width, pids.height - topAreaHeight, cfg.panelColor, zOrder);

    const widthScale = cfg.watermarkWidthScale != null ? cfg.watermarkWidthScale : 1;
    const wmHeight = cfg.watermarkVisibleHeight * yScale;
    const wmWidth = wmHeight * (cfg.watermarkNativeWidth / cfg.watermarkNativeHeight) * widthScale;
    const marginRight = cfg.watermarkMarginRight * xScale;
    const marginBottom = cfg.watermarkMarginBottom * yScale;

    const compositionRaise = getCompositionRaise(pids);
    const watermarkFloor = hasComposition ? Math.min(topAreaHeight, pids.height - CFG.comp.bannerHeight - compositionRaise) : topAreaHeight;

    img(ctx, cfg.watermarkTexture, leftPanelWidth - marginRight - wmWidth, watermarkFloor - marginBottom - wmHeight, wmWidth, wmHeight, cfg.watermarkColor, zOrder + 1);
}

function drawImportantBand(ctx, pids) {
    const bandHeight = getImportantBandHeight(pids);
    rect(ctx, 0, pids.height - bandHeight, pids.width, bandHeight, SncfColors.ImportantBand, CFG.z.importantBand);
}

function calculateDynamicMargin(numStations, maxStations) {
    if (numStations <= 2) return 18;
    if (numStations >= maxStations) return 2;
    return Math.max(2, 18 * Math.pow(1 - (numStations - 2) / (maxStations - 2), 2));
}

function computeRowStep(hasComposition, stationCount) {
    const visibleStops = hasComposition ? CFG.stationList.maxVisibleWithComp : CFG.stationList.maxVisible;
    let dynamicMargin = calculateDynamicMargin(stationCount, visibleStops);
    if (hasComposition && stationCount === 5) dynamicMargin += 0.5;
    return { visibleStops: visibleStops, dynamicMargin: dynamicMargin, rowStep: CFG.stationList.stopHeight + dynamicMargin };
}

function isNearbyTrainDetected(pids) {
    const pidsPos = pids.blockPos();
    let nearestDist = null;

    try {
        MTRClientData.getInstance().vehicles.forEach(function(vehicle) {
            if (!vehicle.getIsOnRoute() || vehicle.closeToDepot()) return;

            const headPos = vehicle.getHeadPosition();
            const dx = pidsPos.x() - headPos.x();
            const dy = pidsPos.y() - headPos.y();
            const dz = pidsPos.z() - headPos.z();
            const dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (nearestDist === null || dist < nearestDist) nearestDist = dist;
        });
    } catch (e) {
        return false;
    }

    return nearestDist !== null && nearestDist < CFG.nearbyTrainThreshold;
}

function create(ctx, state, pids) {
    ConfigHandler.init(state, { logo: true });
    SncfTheme.init(state);

    if(state.scroll != null) return;

    state.scroll = {
        stations: [],
        fixedStations: [],
        isTerminating: false,
        animator: null
    };
}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);
    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const departure = arrivals.get(0);

    if (departure == null && isNearbyTrainDetected(pids)) {
        img(ctx, "sncf:images/platform/train_nearby.png", 0, 0, pids.width, pids.height, null, 0);
        SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 16 });
        return;
    }

    if (!ConfigHandler.get(state, "forceNext") && AdvertisingManager.maybeShow(ctx, state, pids)) {
        SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 16 });
        return;
    }

    if(departure != null && state.scroll != null) {
        let platforms = departure.route().platforms;
        if(platforms == null || platforms.length == 0) return;

        const routeInfo = TrainUtils.getRouteInfo(state.scroll, departure);
        const compConfig = ConfigHandler.get(state, "composition");
        const hasComposition = TrainComposition.resolveHasComposition(compConfig, routeInfo.trainType.toLowerCase());
        const isImportant = ConfigHandler.get(state, "important");

        const departureTime = departure.departureTime();
        const timestamp = Timing.currentTimeMillis();

        const lastPlatformId = "" + platforms[platforms.length - 1].platformId;
        let isTerminating = departure.terminating() && ("" + departure.platformId()) === lastPlatformId;

        if(isTerminating) {
            if (departureTime - timestamp <= 15000 && departureTime - timestamp >= -6000) {
                if ((Math.floor(timestamp / 1000) % 8) < 3) {
                    img(ctx, "sncf:images/platform/terminating.png", 0, 0, pids.width, pids.height, null, 0);
                    SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 16 });
                    return;
                }
            }

            drawStateBackground(ctx, pids, "arrival", 0, hasComposition);
        } else {
            if (departureTime - timestamp <= (CFG.departureMessageDuration * 1000) && departureTime - timestamp >= -6000) {
                img(ctx, "sncf:images/platform/train_departing.png", 0, 0, pids.width, pids.height, null, 0);

                if (Math.floor(timestamp / 1000) % 2 == 0) {
                    img(ctx, "sncf:images/pictograms/forbidden_boarding.png", 21, 7.4, 55, 55, null, 1);
                }

                SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 16 });
                return;
            }

            drawStateBackground(ctx, pids, "departure", 0, hasComposition);
        }

        const routeKey = "" + departure.routeId() + "_" + departure.departureIndex();

        if(state.scroll.stations.length == 0 || state.scroll.stationsRouteKey !== routeKey) {
            state.scroll.stationsRouteKey = routeKey;

            let stations = [];
            platforms.forEach(function(platform) {
                stations.push({
                    name: String(platform.stationName || ""),
                    id: "" + platform.platformId
                });
            });

            state.scroll.stations = stations;
            state.scroll.fixedStations = stations;
            state.scroll.isTerminating = false;
        }

        if(!isTerminating) {
            const currentStation = "" + departure.platformId();
            let currentIndex = state.scroll.fixedStations.findIndex(function(station) { return station.id === currentStation; });

            if (currentIndex !== -1) {
                state.scroll.stations = state.scroll.fixedStations.slice(currentIndex + 1);
            }
            state.scroll.isTerminating = false;
        } else {
            state.scroll.stations = state.scroll.fixedStations.slice(0, -1);
            state.scroll.isTerminating = true;
        }

        if (state.scroll.linearOffset == null) {
            state.scroll.linearOffset = 0;
            state.scroll.scrollTimer = 0;
        }

        const rowStepInfo = computeRowStep(hasComposition, state.scroll.stations.length);
        const rowStep = rowStepInfo.rowStep;

        const compositionRaise = getCompositionRaise(pids);
        let targetY = hasComposition ? (36.5 - compositionRaise) : 50.0;
        const maxScroll = Math.max(0, (state.scroll.stations.length - 1) * rowStep - targetY);

        const scrollSpeed = ConfigHandler.get(state, "scrollSpeed");

        if (maxScroll > 0) {
            state.scroll.scrollTimer += Timing.delta();

            if (state.scroll.scrollTimer > CFG.timeBetweenStates) {

                if (state.scroll.linearOffset < maxScroll) {
                    state.scroll.linearOffset += scrollSpeed * Timing.delta();
                    if (state.scroll.linearOffset > maxScroll) {
                        state.scroll.linearOffset = maxScroll;
                    }
                }

                const totalCycleDuration = CFG.timeBetweenStates + (maxScroll / scrollSpeed) + CFG.timeBetweenStates;
                if (state.scroll.scrollTimer > totalCycleDuration) {
                    state.scroll.linearOffset = 0;
                    state.scroll.scrollTimer = 0;
                }
            }
        } else {
            state.scroll.linearOffset = 0;
            state.scroll.scrollTimer = 0;
        }

        if(state.scroll.stations.length > 0) {
            drawStationList(ctx, state, state.scroll, pids, isTerminating, hasComposition);
        }

        drawLeftPanel(ctx, state, state.scroll, pids, departure, isTerminating, routeInfo);
        TrainComposition.draw(ctx, state, pids, departure, hasComposition, routeInfo, { offsetY: -compositionRaise, bannerHeight: CFG.comp.bannerHeight, zOrders: CFG.comp.zOrders });
        if (hasComposition) {
            rect(ctx, 0, pids.height - compositionRaise, pids.width, compositionRaise, CFG.comp.defaultBgColor, CFG.comp.zOrders.bg);
        }
        if (isImportant) drawImportantBand(ctx, pids);
        drawTextInformation(ctx, state, pids, isImportant, isTerminating);

        SncfTheme.drawClock(ctx, pids, timestamp, { zOrder: 15, drawBox: true });
    } else {
        if (!AdvertisingManager.maybeShow(ctx, state, pids)) {
            drawStateBackground(ctx, pids, "departure", 0, false);
        }
    }

    SncfTheme.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"), { zOrder: 16 });
}

function drawLeftPanel(ctx, state, scroll, pids, departure, isTerminating, routeInfo) {
    drawTrainDepartureHour(ctx, state, scroll, pids, departure, isTerminating);
    drawTrainDestination(ctx, state, scroll, pids, departure, isTerminating, routeInfo);
}

function drawTrainDepartureHour(ctx, state, scroll, pids, departure, isTerminating) {
    const baseTime = isTerminating ? departure.arrivalTime() : departure.departureTime();
    const schedule = TrainUtils.getStableSchedule(state, departure, baseTime);
    const delay = TrainUtils.applyForceDelay(state, schedule);
    const color = isTerminating ? 0x006E30 : 0x014494;
    let showDelay = false;

    const timeLimitToShowDelay = 20;

    if (delay.time > timeLimitToShowDelay) {
        showDelay = (Math.floor(Timing.currentTimeMillis() / 1000) % 5) < 2;
    }

    if(showDelay) {
        txt(ctx, "retard " + RenderUtils.formatTrainDelay(delay.time, !ConfigHandler.get(state, "exactDelay"), false) + ".", 2.25, 10.8, 0.4, color, "l", 5);
    } else {
        txt(ctx, RenderUtils.formatTime(schedule.scheduledTime, "h"), 2.25, 9.425, 0.6, color, "l", 5, { bold: true });

        if(delay.time <= timeLimitToShowDelay) {
            const onTimeMessages = ["À l'heure", "On time", "In orario"];
            txt(ctx, onTimeMessages[Math.floor(Timing.currentTimeMillis() / 3000) % onTimeMessages.length].toLowerCase(), 34.125, 11.875, 0.28, color, "c", 5);
        }
    }
}

function drawTrainDestination(ctx, state, scroll, pids, departure, isTerminating, routeInfo) {
    const color = isTerminating ? 0x006E30 : 0x014494;
    const destination = departure.destination() == "" ? "⚠ Undefined Name" : ""+departure.destination();

    let nameToShow = destination;
    if (isTerminating) {
        const originName = scroll.fixedStations[0].name;
        nameToShow = originName == "" ? "⚠ Undefined Name" : "" + originName;
    }
    let lines = SncfTheme.convertNameToLines(state, SncfTheme.shortenNames(state, nameToShow));
    let startY = 20;
    const LINE_HEIGHT = 4.5;

    const isUndefinedName = departure.destination() == "" || (isTerminating && scroll.fixedStations[0].name == "");
    const nameTextColor = isUndefinedName ? 0xE33B19 : color;
    let lineY = startY;
    for(let i = 0; i < lines.length; i++) {
        txt(ctx, lines[i].text, 2.25, lineY, 0.51, nameTextColor, "l", 5, { bold: true, size: [42.5, LINE_HEIGHT] });
        lineY += LINE_HEIGHT + lines[i].margin + CFG.trainDestLineGap;
    }

    let newY = lineY + 2.25;

    drawTrainInformation(ctx, scroll, pids, departure, newY, routeInfo);
}

function drawTrainInformation(ctx, scroll, pids, departure, startY, routeInfo) {
    const trainType = routeInfo.trainType;
    const trainNumber = routeInfo.trainNumber;

    let trainName = TrainUtils.findNameFromAlias(trainType) || trainType.toUpperCase();

    let trainTexture = TrainUtils.getTextureOfType(trainType);
    let iconSize = TrainUtils.getIconSize(trainTexture, { maxHeight: 4.75 });
    let iconWidth = iconSize[0];
    let iconHeight = iconSize[1];
    let trainTextureId = (trainTexture && trainTexture.id) ? trainTexture.id : "sncf";

    let showingTexture = TrainUtils.isShowingTexture(trainType);

    if(showingTexture) {
        img(ctx, "sncf:images/labels/" + trainTextureId + ".png", 1.5, 1.25, iconWidth, iconHeight, null, 5);
    }

    if (TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.CAR)) {
        img(ctx, "sncf:images/pictograms/bus_black.png", 0.15, pids.height - 1.95, 1.0, 1.0, null, 5);
    }

    const trainNamePixelSize = 0.37;
    const transilienPixelSize = 0.3;
    let useTransilienPixelSize = false;
    let startX = 1.9;

    const isItTransilien = TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.TRANSILIEN) || TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.RER);
    let lineName = TrainUtils.resolveLineName(routeInfo);

    if(isItTransilien && lineName != null) {
        useTransilienPixelSize = true;

        const lineIconSize = 2.25;
        const lineGroupOffsetY = -0.15;

        img(ctx, "sncf:images/train.png", startX, startY + lineGroupOffsetY, lineIconSize, lineIconSize, null, 5);

        startX += lineIconSize + 0.5;

        const lineBadgeSize = 2.25;
        const lineBadgeOffsetX = 0.225;
        const lineTextScale = 0.17;
        const lineTextHeight = RenderUtils.getCharHeight(lineTextScale);

        img(ctx, "sncf:images/platform_border.png", startX + lineBadgeOffsetX, startY + lineGroupOffsetY, lineBadgeSize, lineBadgeSize, departure.routeColor(), 5);
        txt(ctx, lineName, startX + lineBadgeOffsetX + lineBadgeSize / 2 + 0.13, startY + lineGroupOffsetY + (lineBadgeSize - lineTextHeight) / 2, lineTextScale, departure.routeColor(), "c", 5);

        startX += lineBadgeOffsetX + lineBadgeSize + 0.9;
        trainName = trainName.toUpperCase();

        txt(ctx, trainName, startX, startY, transilienPixelSize - 0.0125, 0x00409D, "l", 5);
    } else {
        txt(ctx, trainName, startX, startY, trainNamePixelSize, 0x00409D, "l", 5);
    }

    const nameWidthStep = useTransilienPixelSize ? transilienPixelSize - 0.0125 : trainNamePixelSize;
    scroll.trainNameWidth = RenderUtils.getCachedStringWidth(scroll, "trainName", trainName, nameWidthStep);

    const posX = startX + scroll.trainNameWidth + 1.375;
    txt(ctx, trainNumber, posX, startY + (useTransilienPixelSize ? 0.275 : 0.25), 0.35, 0x7C86B5, "l", 5);
}

function drawTextInformation(ctx, state, pids, isImportant, isTerminating) {
    const formatted = SncfTheme.formatText(state, ConfigHandler.getTextSlots(state));
    if (formatted === "") return;

    const TEXT_HEIGHT = 6.25;
    const textY = pids.height - TEXT_HEIGHT + 0.5;
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
        txt(ctx, formatted, CFG.marquee.marginLeft, textY, CFG.marquee.scale, textColor, "l", CFG.z.marquee, { italic: true });
        return;
    }

    for (var k = 0; k < 3; k++) {
        var copyBaseX = -marquee.offset + k * marquee.loopWidth;
        if (copyBaseX > targetX) break;
        marquee.chars.forEach(function(ch) {
            var chX = copyBaseX + ch.x;
            if (chX + ch.width < 0 || chX > targetX) return;
            txt(ctx, ch.text, chX, textY, CFG.marquee.scale, textColor, "l", CFG.z.marquee, { italic: true });
        });
    }

    const maskColor = isImportant ? SncfColors.ImportantBand : (isTerminating ? BG_STATES.arrival.panelColor : BG_STATES.departure.panelColor);
    const maskHeight = getImportantBandHeight(pids);
    const maskY = pids.height - maskHeight;

    rect(ctx, targetX, maskY, CFG.marquee.maskWidthRight, maskHeight, maskColor, CFG.z.marqueeMask);
}

function drawStationList(ctx, state, scroll, pids, isTerminating, hasComposition) {
    let stations = scroll.stations;
    const availableHeight = hasComposition ? (CFG.totalHeight - CFG.comp.bannerHeight) : CFG.totalHeight;

    const rowStepInfo = computeRowStep(hasComposition, stations.length);
    const visibleStops = rowStepInfo.visibleStops;
    const dynamicMargin = rowStepInfo.dynamicMargin;
    const rowStep = rowStepInfo.rowStep;

    const totalStopsHeight = stations.length * CFG.stationList.stopHeight;
    const totalMarginHeight = (stations.length - 1) * dynamicMargin;
    const totalContentHeight = totalStopsHeight + totalMarginHeight;

    let scrollIndex = 0;
    let scrollOffset = -1.5;

    if (stations.length > visibleStops) {
        scrollIndex = Math.floor(scroll.linearOffset / rowStep);
        scrollOffset = (scroll.linearOffset % rowStep) - 1.5;
    }

    let startY = -scrollOffset;
    let maxI = (hasComposition ? CFG.stationList.maxRowsScrollingWithComp : CFG.stationList.maxVisible) + 1;

    let lineHeightSize = availableHeight;

    if(stations.length <= visibleStops) {
        maxI = stations.length;
        if (hasComposition) {
            const TARGET_TOP_GAP = 15;
            const MIN_GAP = 3;
            const trueCenterStartY = (availableHeight - totalContentHeight) / 2;
            const sparseSingleStopBonus = stations.length === 1 ? 4 : 0;
            const anchor = Math.max(TARGET_TOP_GAP, trueCenterStartY + sparseSingleStopBonus);
            const maxStartYBeforeBanner = availableHeight - totalContentHeight;
            startY = Math.max(MIN_GAP, Math.min(anchor, maxStartYBeforeBanner));
        } else {
            startY = (availableHeight - totalContentHeight) / 2;
        }
        lineHeightSize = startY + (CFG.stationList.stopHeight * stations.length) + (dynamicMargin * (stations.length - 1));
    } else {
        let lineMaxY = hasComposition ? (pids.height - CFG.comp.bannerHeight) : pids.height;

        let terminusRowIndex = stations.length - 1 - scrollIndex;
        if (terminusRowIndex < maxI) {
            let lastStationY = startY + (terminusRowIndex * rowStep) + CFG.stationList.stopHeight;
            lineHeightSize = Math.min(lineMaxY, lastStationY);
        } else {
            lineHeightSize = lineMaxY;
        }
    }

    rect(ctx, 50.125, 0, 2, lineHeightSize, isTerminating ? CFG.lineColor.terminus : CFG.lineColor.station, 2);

    let loopStart = (stations.length > visibleStops && scrollOffset > rowStep) ? 1 : 0;

    const lastStationId = scroll.fixedStations[scroll.fixedStations.length - 1].id;

    for (var i = loopStart; i < maxI; i++) {
        var targetIdx = i + scrollIndex;
        if (targetIdx >= stations.length || targetIdx < 0) break;

        const pixelSize = 0.45;
        var station = stations[targetIdx];

        var stationName = station._displayName;
        if (stationName === undefined) {
            let rawStationName = "" + station.name == "" ? "⚠ Undefined Name" : "" + station.name;

            if (JAPANESE_REGEX.test(rawStationName) || KOREAN_REGEX.test(rawStationName)) {
                stationName = cropStationName(state, SncfTheme.displayStationName(SncfTheme.shortenNames(state, rawStationName))).split(" Via")[0];
            } else {
                let fitted = SncfTheme.fitName(state, rawStationName, 28);
                fitted = fitted.split("-Via-")[0];
                stationName = SncfTheme.displayStationName(fitted);
            }
            station._displayName = stationName;
        }

        if(station.id === lastStationId) {
            if (scroll._lastStationNameWidthFor !== stationName) {
                scroll.lastStationNameWidth = RenderUtils.getStringWidth(stationName, pixelSize);
                scroll._lastStationNameWidthFor = stationName;
            }

            rect(ctx, 50.125, startY + (rowStep * i) - (CFG.stationList.stopHeight / 3), 4.875 + scroll.lastStationNameWidth + 2.875, CFG.stationList.stopHeight + 2, CFG.lineColor.station, 2);
        }

        img(ctx, "aca:images/dot.png", 50.25, startY + (rowStep * i) + 0.75, 1.75, 1.75, 0xFCFF02, 3);
        txt(ctx, stationName, 55, startY + (rowStep * i), pixelSize, 0xFFFFFF, "l", 3, { size: [75, CFG.stationList.stopHeight] });
    }

    const footerHeight = getImportantBandHeight(pids);
    rect(ctx, 0, pids.height - footerHeight, pids.width, footerHeight, isTerminating ? BG_STATES.arrival.panelColor : BG_STATES.departure.panelColor, 4);
}

const LATIN_REGEX = /^[A-z-\s]+$/;
const JAPANESE_REGEX = /[぀-ゟ゠-ヿ一-鿿]/;
const CHINESE_REGEX = /[一-鿿]/;
const KOREAN_REGEX = /[가-힯]/;
const DIACRITICS_REGEX = /[̀-ͯ]/;

function cropStationName(state, input) {
    if (!state.cropCache) state.cropCache = {};
    let key = String(input);
    let cached = state.cropCache[key];
    if (cached) return cached;

    let trimmed = key.trim();
    let result;
    if (LATIN_REGEX.test(trimmed)) {
        trimmed = trimmed.normalize("NFD").replace(DIACRITICS_REGEX, "");
        result = trimmed.length > 28 ? trimmed.substring(0, 25) + "..." : trimmed;
    } else if (JAPANESE_REGEX.test(trimmed)) {
        result = trimmed.length > 19 ? trimmed.substring(0, 16) + "..." : trimmed;
    } else if (CHINESE_REGEX.test(trimmed)) {
        result = trimmed.length > 20 ? trimmed.substring(0, 17) + "..." : trimmed;
    } else if (KOREAN_REGEX.test(trimmed)) {
        result = trimmed.length > 22 ? trimmed.substring(0, 19) + "..." : trimmed;
    } else {
        result = trimmed.length > 28 ? trimmed.substring(0, 25) + "..." : trimmed;
    }

    state.cropCache[key] = result;
    return result;
}
