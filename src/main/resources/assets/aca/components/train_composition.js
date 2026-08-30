const RESERVATION_TRAIN_TYPES = {
    "tgv": 1, "inoui": 1, "ouigo": 1, "lyria": 1, "eurostar": 1, "renfe": 1, "thalys": 1,
    "frecciarossa": 1, "trenitalia": 1, "idtgv": 1, "ice": 1, "velvet": 1, "letrain": 1,
    "ave": 1, "nightjet": 1, "european_sleeper": 1, "night_intercities": 1, "intercities": 1
};

const TrainCompositionWhiteSquare = RenderUtils.getUV(0, 0, 5, 5);

const TRACK_BOX_CENTER_X = 0.25 + (8.5 / 2);

const MAX_CAR_HEIGHT = 2.25;

const CAB_ICON_RATIO = 65 / 27;
const VEHICLE_ICON_RATIO = 54 / 27;

const LOCO_ICON_RATIOS = {
    "ic_cab": 42 / 27
};

const SYMMETRIC_LOCO_ICONS = {
    "ic_cab": 1
};
function matchesTarget(target, trainType, realTrainName, currentTrainNumber) {
    return trainType.indexOf(target) !== -1 || realTrainName.indexOf(target) !== -1 || currentTrainNumber.toLowerCase().indexOf(target) === 0;
}
function resolveTypeRule(rulesConf, trainType, realTrainName, currentTrainNumber) {
    if (!rulesConf || rulesConf.trim() === "") return null;
    if (rulesConf.indexOf("=") === -1) return rulesConf.trim();
    var rules = rulesConf.split(/[,;]/);
    var defaultValue = null;
    for (var r = 0; r < rules.length; r++) {
        var rule = rules[r].trim();
        if (rule === "") continue;
        var pair = rule.split("=");
        if (pair.length === 2) {
            var target = pair[0].trim().toLowerCase();
            var value = pair[1].trim();
            if (target === "") { defaultValue = value; continue; }
            if (matchesTarget(target, trainType, realTrainName, currentTrainNumber)) {
                return value;
            }
        } else {
            defaultValue = rule;
        }
    }
    return defaultValue;
}
const TrainComposition = {
    RESERVATION_TRAIN_TYPES: RESERVATION_TRAIN_TYPES,
    resolveTypeRule: resolveTypeRule,
    resolveHasComposition: function(compConfig, trainType) {
        if (compConfig === "false") return false;
        return compConfig === "true" || (compConfig === "auto" && RESERVATION_TRAIN_TYPES[trainType] === 1);
    },
    build(state, pidsLike, departure, routeInfo) {
        const trainType = routeInfo.trainType.toLowerCase();
        const isTgv = TrainUtils.isTgvFamily(trainType);
        const currentTrainNumber = String(routeInfo.trainNumber || "");
        const realTrainName = (TrainUtils.findNameFromAlias(trainType) || "sncf").toLowerCase();
        const platformInfo = TrainUtils.getPlatformInfo(departure);
        const trackNum = platformInfo.name;

        const trackNumScale = 0.55;
        const trackNumWidth = RenderUtils.getStringWidth(trackNum, trackNumScale);
        const trackNumX = TRACK_BOX_CENTER_X - (trackNumWidth / 2);
        const dotStartX = pidsLike.width * 0.025;
        const dotEndX = pidsLike.width * 0.975;
        const dotWidth = dotEndX - dotStartX;
        let markersRaw = ConfigHandler.get(state, "markers");

        const locoPosConf = ConfigHandler.get(state, "locoPos");
        const locoPosValue = resolveTypeRule(locoPosConf, trainType, realTrainName, currentTrainNumber);
        const customLocos = locoPosValue !== null
            ? locoPosValue.split("+").map(function(x) { return parseInt(x.trim()); })
            : null;
        const locoTypeConf = ConfigHandler.get(state, "locoType");
        const locoTypeMatch = resolveTypeRule(locoTypeConf, trainType, realTrainName, currentTrainNumber);
        const customLocoIcon = locoTypeMatch !== null ? locoTypeMatch : "cab_icon";

        const activeCabIconRatio = LOCO_ICON_RATIOS[customLocoIcon] || CAB_ICON_RATIO;
        const rawCars = departure.cars();
        let portions = [];
        let currentPortion = [];
        if (rawCars && rawCars.length > 0) {
            const carsArr = [];
            rawCars.forEach(function(car) { carsArr.push(car); });
            const totalCarsCount = carsArr.length;
            const carFlags = TrainUtils.resolveCarLocoFlags(customLocos, totalCarsCount, isTgv);
            carsArr.forEach(function(carObj, c) {
                const carId = carObj.getVehicleId().toLowerCase();
                const isLoco = carFlags[c].isLocoCar;
                if (isLoco && currentPortion.length > 0) {
                    if (isTgv && c === 10) {
                        portions.push(currentPortion);
                        currentPortion = [];
                    }
                }
                currentPortion.push({ loco: isLoco, id: carId, isFrontLoco: carFlags[c].isFrontLoco });
            });
            if (currentPortion.length > 0) portions.push(currentPortion);
        } else {
            const simulatedPortion = [];
            const simFlags = TrainUtils.resolveCarLocoFlags(null, 8, isTgv);
            for (let simC = 0; simC < 8; simC++) {
                simulatedPortion.push({ loco: simFlags[simC].isLocoCar, id: "simulate", isFrontLoco: simFlags[simC].isFrontLoco });
            }
            portions.push(simulatedPortion);
        }
        let trainStartX = 0;
        let trainWidth = 0;
        let hasTrainGrid = false;
        let carHeight = MAX_CAR_HEIGHT;
        const spaceBetweenCars = 0.5;
        const locoWidthFactor = activeCabIconRatio / VEHICLE_ICON_RATIO;
        const carEntries = [];
        const rawDestName = departure.destination() == "" ? "Unknown station" : "" + departure.destination();
        const destViaMatch = rawDestName.match(/[\s-]via[\s-]/i);
        const fullDestName = destViaMatch ? rawDestName.slice(0, destViaMatch.index).trim() : rawDestName;

        if (portions.length > 0) {
            let totalCars = 0;
            let locoCount = 0;
            portions.forEach(function(portion) {
                totalCars += portion.length;
                portion.forEach(function(car) { if (car.loco) locoCount++; });
            });
            const wagonCount = totalCars - locoCount;

            const platformSpanW = pidsLike.width * 0.95;
            const platformLengthConf = ConfigHandler.get(state, "platformLength");
            const platformLengthMeters = parseFloat(platformLengthConf);
            const effectivePlatformMeters = (!isNaN(platformLengthMeters) && platformLengthMeters > 0) ? platformLengthMeters : 100;
            const effectiveSpanW = platformSpanW;
            const totalLengthConf = ConfigHandler.get(state, "trainLength");
            const totalLengthMatch = resolveTypeRule(totalLengthConf, trainType, realTrainName, currentTrainNumber);
            const totalLengthMeters = (totalLengthMatch !== null && !isNaN(parseFloat(totalLengthMatch))) ? parseFloat(totalLengthMatch) : null;

            const gapsWidth = spaceBetweenCars * (totalCars - 1);
            if (totalLengthMeters !== null) {

                const fraction = totalLengthMeters / effectivePlatformMeters;
                const clampedFraction = Math.max(0.03, Math.min(1, fraction));
                trainWidth = effectiveSpanW * clampedFraction;
            } else {
                let legacyCarW = (pidsLike.width * 0.8 - gapsWidth) / totalCars;
                if (legacyCarW > 8.5) legacyCarW = 8.5;
                trainWidth = (legacyCarW * totalCars) + gapsWidth;
            }
            const carsOnlyWidth = Math.max(0, trainWidth - gapsWidth);
            const totalWidthUnits = (locoCount * locoWidthFactor) + wagonCount;
            const unitWidth = totalWidthUnits > 0 ? carsOnlyWidth / totalWidthUnits : 0;
            let wagonCarW = unitWidth;
            let locoCarW = unitWidth * locoWidthFactor;
            const naturalCarHeight = wagonCarW / VEHICLE_ICON_RATIO;
            carHeight = Math.min(MAX_CAR_HEIGHT, naturalCarHeight);
            if (totalLengthMeters === null && carHeight < naturalCarHeight) {
                wagonCarW = carHeight * VEHICLE_ICON_RATIO;
                locoCarW = carHeight * activeCabIconRatio;
                trainWidth = (locoCount * locoCarW) + (wagonCount * wagonCarW) + gapsWidth;
            }
            const trainPosConf = ConfigHandler.get(state, "trainOffset");
            let trainPercent = null;
            if (trainPosConf && trainPosConf.trim() !== "") {
                let matchedPercent = null;
                let defaultPercent = null;
                const trainPosRules = trainPosConf.split(/[,;]/);
                trainPosRules.some(function(rule) {
                    const trimmedRule = rule.trim();
                    if (trimmedRule === "") return false;
                    if (trimmedRule.indexOf("=") !== -1) {
                        const pair = trimmedRule.split("=");
                        if (pair.length === 2) {
                            const target = pair[0].trim().toLowerCase();
                            const value = pair[1].trim();
                            if (target === "") { defaultPercent = parseFloat(value); return false; }
                            let isMatch = false;
                            if (target.indexOf("-") !== -1) {
                                const lastDash = target.lastIndexOf("-");
                                const mainPart = target.substring(0, lastDash).trim();
                                const sizePart = parseInt(target.substring(lastDash + 1).trim());
                                if (!isNaN(sizePart) && totalCars === sizePart) {
                                    isMatch = matchesTarget(mainPart, trainType, realTrainName, currentTrainNumber);
                                }
                            } else {
                                isMatch = matchesTarget(target, trainType, realTrainName, currentTrainNumber);
                            }
                            if (isMatch) {
                                matchedPercent = parseFloat(value);
                                return true;
                            }
                        }
                    } else if (trimmedRule.indexOf("%") !== -1 || !isNaN(trimmedRule)) {
                        defaultPercent = parseFloat(trimmedRule);
                    }
                    return false;
                });
                trainPercent = matchedPercent !== null ? matchedPercent : defaultPercent;
            }
            if (trainPercent !== null && !isNaN(trainPercent)) {

                const travelRange = Math.max(0, effectiveSpanW - trainWidth);
                const clampedTrainPercent = Math.max(0, Math.min(100, trainPercent));
                trainStartX = dotStartX + travelRange * (clampedTrainPercent / 100);
            } else {
                trainStartX = dotStartX + (effectiveSpanW - trainWidth) / 2;
            }
            hasTrainGrid = true;
            const numPortions = portions.length;
            const directionMode = ConfigHandler.get(state, "carNumbering");
            const isReversed = (directionMode === "inverse" || directionMode === "reverse");
            const portionsToDraw = isReversed ? portions.slice().reverse() : portions;

            var cursorX = trainStartX;
            for (var p = 0; p < portionsToDraw.length; p++) {
                var portion = portionsToDraw[p];
                var passengerCarsInPortion = 0;
                for (var k = 0; k < portion.length; k++) {
                    if (!portion[k].loco) passengerCarsInPortion++;
                }
                var passengerCarIdx = 0;
                var originalP = isReversed ? (numPortions - 1 - p) : p;
                for (var c = 0; c < portion.length; c++) {
                    var isLoco = portion[c].loco;
                    var thisCarW = isLoco ? locoCarW : wagonCarW;
                    var carX = cursorX;
                    var carY = pidsLike.height - 14;
                    if (isLoco) {
                        var carIdLower = portion[c].id.toLowerCase();
                        var shouldFlip = !portion[c].isFrontLoco;

                        if (carIdLower.indexOf("backward") !== -1 || carIdLower.indexOf("(b)") !== -1) {
                            shouldFlip = true;
                        } else if (carIdLower.indexOf("forward") !== -1 || carIdLower.indexOf("(f)") !== -1) {
                            shouldFlip = false;
                        }
                        var suffix = (shouldFlip && !SYMMETRIC_LOCO_ICONS[customLocoIcon]) ? "_reversed" : "";
                        carEntries.push({
                            isLoco: true,
                            carX: carX, carY: carY, carW: thisCarW,
                            texture: "sncf:images/pictograms/" + customLocoIcon + suffix + ".png"
                        });
                    } else {
                        var displayCarNum = 1;
                        if (directionMode === "mid") {
                            if (numPortions === 1) {
                                displayCarNum = passengerCarsInPortion - passengerCarIdx;
                            } else if (originalP === 0) {
                                displayCarNum = passengerCarsInPortion - passengerCarIdx;
                            } else {
                                displayCarNum = 11 + passengerCarIdx;
                            }
                        } else if (directionMode === "mid-reverse") {
                            if (numPortions === 1) {
                                displayCarNum = passengerCarsInPortion - passengerCarIdx;
                            } else if (originalP === 0) {
                                displayCarNum = (10 + passengerCarsInPortion) - passengerCarIdx;
                            } else {
                                displayCarNum = 1 + passengerCarIdx;
                            }
                        } else if (directionMode === "reverse" || directionMode === "inverse") {
                            if (originalP === 0) {
                                displayCarNum = passengerCarsInPortion - passengerCarIdx;
                            } else {
                                displayCarNum = (10 + passengerCarsInPortion) - passengerCarIdx;
                            }
                        } else {
                            displayCarNum = (originalP === 0) ? (1 + passengerCarIdx) : (11 + passengerCarIdx);
                        }
                        passengerCarIdx++;
                        var formattedNum = displayCarNum < 10 ? "0" + displayCarNum : "" + displayCarNum;
                        carEntries.push({
                            isLoco: false,
                            carX: carX, carY: carY, carW: thisCarW,
                            numberText: formattedNum
                        });
                    }
                    cursorX += thisCarW + spaceBetweenCars;
                }
            }
        }
        const MARKER_BORDER_BOTTOM_OFFSET = { underTrain: 12.2, standalone: 11.4 };
        const MARKER_TEXT_BOTTOM_OFFSET = { underTrain: 11.2, standalone: 10.6 };
        const MARKER_Y_ADJUST = -0.5;
        const markerEntries = [];
        if (markersRaw && markersRaw.trim() !== "") {

            const rawGroups = markersRaw.split(/[,;]/).map(function(g) { return g.trim(); });
            let matchedBody = null;
            let defaultBody = null;
            rawGroups.some(function(group) {
                if (group === "") return false;
                const colonIdx = group.indexOf(":");
                if (colonIdx === -1) {
                    defaultBody = group;
                    return false;
                }
                const prefix = group.substring(0, colonIdx).trim().toLowerCase();
                if (matchesTarget(prefix, trainType, realTrainName, currentTrainNumber)) {
                    matchedBody = group.substring(colonIdx + 1).trim();
                    return true;
                }
                return false;
            });
            const finalBody = matchedBody !== null ? matchedBody : defaultBody;
            const markerParts = [];
            if (finalBody) {
                finalBody.split("+").forEach(function(entry) {
                    const trimmed = entry.trim();
                    if (trimmed !== "") markerParts.push(trimmed);
                });
            }
            const totalMarkers = markerParts.length;
            markerParts.forEach(function(part, i) {
                let letter = part;
                let letterX = 0;
                if (part.indexOf("=") !== -1) {
                    const kv = part.split("=");
                    letter = kv[0].trim();
                    const markerPercent = parseFloat(kv[1]);
                    letterX = dotStartX + dotWidth * (markerPercent / 100);
                } else {
                    const mStartX = pidsLike.width * 0.175;
                    const mUsableWidth = pidsLike.width * 0.65;
                    const markerSpacing = (totalMarkers > 1) ? mUsableWidth / (totalMarkers - 1) : 0;
                    letterX = (totalMarkers > 1) ? mStartX + markerSpacing * i : mStartX + mUsableWidth / 2;
                }
                const isUnderTrain = hasTrainGrid && letterX >= trainStartX && letterX <= (trainStartX + trainWidth);
                const borderSize = isUnderTrain ? 5.0 : 3.4;
                const textScale = isUnderTrain ? 0.4 : 0.25;

                const borderBottomOffset = isUnderTrain ? MARKER_BORDER_BOTTOM_OFFSET.underTrain : MARKER_BORDER_BOTTOM_OFFSET.standalone;
                const textBottomOffset = isUnderTrain ? MARKER_TEXT_BOTTOM_OFFSET.underTrain : MARKER_TEXT_BOTTOM_OFFSET.standalone;
                const borderY = pidsLike.height - borderBottomOffset + MARKER_Y_ADJUST;
                const textY = pidsLike.height - textBottomOffset + MARKER_Y_ADJUST;
                markerEntries.push({
                    letter: letter, letterX: letterX,
                    borderSize: borderSize, textScale: textScale,
                    borderY: borderY, textY: textY
                });
            });
        }
        const dottedDots = [];
        for (let dotX = dotStartX; dotX < dotEndX; dotX += 1.2) {
            dottedDots.push(dotX);
        }
        return {
            trackNum: trackNum,
            trackNumX: trackNumX,
            carEntries: carEntries,
            carHeight: carHeight,
            fullDestName: fullDestName,
            hasTrainGrid: hasTrainGrid,
            trainStartX: trainStartX,
            trainWidth: trainWidth,
            reperesEntries: markerEntries,
            dottedDots,
            dotStartX,
            dotWidth
        };
    },
    fingerprint(state, pidsLike, departure) {
        const routeKey = "" + departure.routeId() + "_" + departure.departureIndex();
        const markersConf = ConfigHandler.get(state, "markers");
        const locoPosConf = ConfigHandler.get(state, "locoPos");
        const locoTypeConf = ConfigHandler.get(state, "locoType");
        const trainPosConf = ConfigHandler.get(state, "trainOffset");
        const directionConf = ConfigHandler.get(state, "carNumbering");
        const platformLengthConf = ConfigHandler.get(state, "platformLength");
        const totalLengthConf = ConfigHandler.get(state, "trainLength");
        const rawPlatformName = departure.platformName();
        return [routeKey, markersConf, locoPosConf, locoTypeConf, trainPosConf, directionConf, platformLengthConf, totalLengthConf, rawPlatformName, pidsLike.width + "x" + pidsLike.height].join("|");
    },
    draw(ctx, state, pidsLike, departure, hasComposition, routeInfo, opts) {
        if (departure == null || !hasComposition) return;
        const offsetX = (opts && opts.offsetX != null) ? opts.offsetX : 0;
        const offsetY = (opts && opts.offsetY != null) ? opts.offsetY : 0;
        const bannerHeight = (opts && opts.bannerHeight != null) ? opts.bannerHeight : 23;
        const z = (opts && opts.zOrders) ? opts.zOrders : { bg: 7, mid: 8, marker: 9, letter: 10 };
        const colors = (opts && opts.colors) ? opts.colors : {};
        const backgroundColor = colors.background != null ? colors.background : 0x560e4c;
        const passengerCarColor = colors.passengerCar != null ? colors.passengerCar : 0xDCAFE8;
        const cabColor = colors.cab != null ? colors.cab : 0x854f91;
        const showTrackNumber = (opts && opts.showTrackNumber != null) ? opts.showTrackNumber : true;
        const showBackground = (opts && opts.showBackground != null) ? opts.showBackground : true;
        const fp = this.fingerprint(state, pidsLike, departure);
        if (state.scroll.compositionFingerprint !== fp) {
            state.scroll.compositionLayout = this.build(state, pidsLike, departure, routeInfo);
            state.scroll.compositionFingerprint = fp;
        }
        const layout = state.scroll.compositionLayout;
        if (showBackground) {
            Texture.create("Composition Background")
                .texture("idf:images/colors.png")
                .uv(TrainCompositionWhiteSquare[0], TrainCompositionWhiteSquare[1], TrainCompositionWhiteSquare[2], TrainCompositionWhiteSquare[3])
                .color(backgroundColor)
                .pos(offsetX, pidsLike.height - bannerHeight + offsetY)
                .size(pidsLike.width, bannerHeight)
                .zOrder(z.bg)
                .draw(ctx);
        }
        if (showTrackNumber) {
            Texture.create("Track Box")
                .texture("sncf:images/empty_track.png")
                .color(0xFFFFFF)
                .pos(offsetX + 0.25, pidsLike.height - 22 + offsetY)
                .size(8.5, 8.5)
                .zOrder(z.mid)
                .draw(ctx);
            Text.create("Track Number")
                .text(layout.trackNum)
                .color(0xFFFFFF)
                .scale(0.55)
                .leftAlign()
                .pos(offsetX + layout.trackNumX, pidsLike.height - 19.25 + offsetY)
                .zOrder(z.mid)
                .draw(ctx);
        }
        layout.dottedDots.forEach(function(dotX) {
            Texture.create("Dotted Line Dot")
                .texture("aca:images/dot.png")
                .color(0xFDFC31)
                .pos(offsetX + dotX, pidsLike.height - 9.7 + offsetY)
                .size(0.5, 0.5)
                .zOrder(z.mid)
                .draw(ctx);
        });
        if (layout.hasTrainGrid) {
            Text.create("Total Train Destination")
                .text(layout.fullDestName)
                .color(0xFDFC31)
                .scale(0.26)
                .centerAlign()
                .pos(offsetX + layout.trainStartX + layout.trainWidth / 2, pidsLike.height - 21.5 + offsetY)
                .zOrder(z.mid)
                .draw(ctx);
            layout.carEntries.forEach(function(car) {
                if (car.isLoco) {

                    Texture.create("Loco Graphic")
                        .texture(car.texture)
                        .color(cabColor)
                        .pos(offsetX + car.carX, car.carY + offsetY)
                        .size(car.carW, layout.carHeight)
                        .zOrder(z.mid)
                        .draw(ctx);
                } else {
                    Texture.create("Passenger Car")
                        .texture("sncf:images/pictograms/vehicle_icon.png")
                        .color(passengerCarColor)
                        .pos(offsetX + car.carX, car.carY + offsetY)
                        .size(car.carW, layout.carHeight)
                        .zOrder(z.mid)
                        .draw(ctx);
                    Text.create("Car Number")
                        .text(car.numberText)
                        .color(0xFFFFFF)
                        .scale(0.26)
                        .centerAlign()
                        .pos(offsetX + (car.carX + car.carW / 2) + 0.2, car.carY - 2.75 + offsetY)
                        .zOrder(z.mid)
                        .draw(ctx);
                }
            });
        }
        const passengerPos = ConfigHandler.get(state, "viewerPos");
        if (passengerPos && passengerPos.trim() !== "") {
            if (passengerPos.indexOf("%") !== -1 || !isNaN(passengerPos)) {
                const passengerPercent = parseFloat(passengerPos);
                const passengerX = layout.dotStartX + layout.dotWidth * (passengerPercent / 100);
                const iconSize = 5;
                const visibleDuration = 3;
                const hiddenDuration = 1;
                const cycleDuration = visibleDuration + hiddenDuration;

                if (Math.floor(Timing.currentTimeMillis() / 1000) % cycleDuration < visibleDuration) {
                    Texture.create("YouHereDot")
                        .texture("sncf:images/here.png")
                        .color(0xf2e9f2)
                        .size(iconSize, iconSize)
                        .pos(offsetX + passengerX - (iconSize / 2), (pidsLike.height - 9.5 + offsetY) - (iconSize / 2))
                        .zOrder(z.marker)
                        .draw(ctx);
                }
            }
        }
        layout.reperesEntries.forEach(function(marker) {
            Texture.create("Marker Background")
                .texture("sncf:images/marker_background.png")
                .pos(offsetX + marker.letterX - (marker.borderSize / 2), marker.borderY + offsetY)
                .size(marker.borderSize, marker.borderSize)
                .zOrder(z.marker)
                .draw(ctx);
            Text.create("Marker Letter")
                .text(marker.letter)
                .color(0xFFFFFF)
                .scale(marker.textScale)
                .centerAlign()
                .pos(offsetX + marker.letterX + 0.125, marker.textY + offsetY)
                .size(marker.borderSize, marker.borderSize)
                .zOrder(z.letter)
                .draw(ctx);
            if (passengerPos && passengerPos.toLowerCase() === marker.letter.toLowerCase()) {
                Text.create("You Are Here Dot Letter")
                    .text("●")
                    .color(0xFFFFFF)
                    .scale(0.35)
                    .centerAlign()
                    .pos(offsetX + marker.letterX, pidsLike.height - 16.5 + offsetY)
                    .zOrder(z.marker)
                    .draw(ctx);
            }
        });
    }
};