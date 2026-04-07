include(Resources.id("jsblock:acat/aca/config_handler.js"));
include(Resources.id("jsblock:acat/aca/train_utils.js"));
include(Resources.id("jsblock:acat/aca/sncf_utils.js"));

const Colors = {
    TextColor: "0x0a0086",
    DepartureColor: "0xfec107",
    DepartureBlinking: "0xb28704",
    DepartureBackground: "0x000000",
    Background: "0xbec5df"
}

function create(ctx, state, pids) {
    ConfigHandler.init(state);
    SncfUtils.init(state);
}

function dispose(ctx, state, pids) {}


/**
 * @param {PIDSScriptContext} ctx
 * @param {any} state
 * @param {PIDSWrapper} pids
 */
function render(ctx, state, pids) {
    if (pids.arrivals().get(0) == null) return;

    let filteredArrivals = [];
    for(let i = 0; i < 15; i++) {
        let arr = null;
        try { arr = pids.arrivals().get(i); } catch (e) { break; }
        if(arr == null) break;

        let rInfo = TrainUtils.getRouteInfo(state, arr);
        let type = TrainUtils.findTypeFromAlias(rInfo.trainType);

        if(type == TRAIN_TYPES.RER || type == TRAIN_TYPES.TRANSILIEN) {
            filteredArrivals.push(arr);
        }

        if(filteredArrivals.length >= 6) break;
    }

    if(filteredArrivals.length === 0) return;

    const uvs = {
        whiteSquare: SncfUtils.getUV(0, 0, 5, 5),
        trainNumberSquare: SncfUtils.getUV(0, 5, 5, 5),
        rerInfoSquare: SncfUtils.getUV(0, 25, 70, 95, 120),
        rerInfoLogoSquare: SncfUtils.getUV(65, 150, 435, 350, 500)
    };

    const firstArrival = filteredArrivals[0];
    const routeInfo = TrainUtils.getRouteInfo(state, firstArrival);
    const routeColor = firstArrival.routeColor() || "0x000000";
    const trainType = TrainUtils.findTypeFromAlias(routeInfo.trainType);

    Texture.create("Background")
        .texture("jsblock:acat/idf/images/colors.png").size(pids.width, pids.height).pos(0, 0)
        .uv(uvs.whiteSquare[0], uvs.whiteSquare[1], uvs.whiteSquare[2], uvs.whiteSquare[3])
        .color(Colors.Background).draw(ctx);

    drawHeader(ctx, pids, filteredArrivals, firstArrival, routeInfo, routeColor, trainType, uvs.whiteSquare);
    drawArrivalsList(ctx, state, pids, filteredArrivals, uvs);
    drawFooter(ctx, pids, trainType, uvs);
    drawClock(ctx, pids, uvs.whiteSquare);
}

function drawHeader(ctx, pids, filteredArrivals, firstArrival, routeInfo, routeColor, trainType, whiteSquareUV) {
    Texture.create("Header Background")
        .texture("jsblock:acat/idf/images/colors.png").size(pids.width - (2 * 1.35), 11.4).pos(1.35, 1.55)
        .uv(whiteSquareUV[0], whiteSquareUV[1], whiteSquareUV[2], whiteSquareUV[3]).draw(ctx);

    Texture.create("Line Color")
        .texture("jsblock:acat/idf/images/colors.png").size(pids.width - (2 * 1.35), 1.2).pos(1.35, 12.85)
        .uv(whiteSquareUV[0], whiteSquareUV[1], whiteSquareUV[2], whiteSquareUV[3])
        .color(routeColor).draw(ctx);

    const rerLogoSize = 8;
    const isTransilien = (trainType == TRAIN_TYPES.TRANSILIEN);
    
    Texture.create("RER Line Logo")
        .texture(`jsblock:acat/idf/images/${isTransilien ? "transilien" : "rer"}_line.png`)
        .size(rerLogoSize, rerLogoSize).pos(2.5, 1.5 + ((11.4 - rerLogoSize) / 2)).draw(ctx);

    const boxSize = rerLogoSize;
    const boxX = 3.25 + rerLogoSize;
    const boxY = 1.5 + ((11.4 - rerLogoSize) / 2);

    Texture.create("RER Line Color").texture("jsblock:acat/idf/images/rer_square.png")
        .size(boxSize, boxSize).pos(boxX, boxY).color(routeColor).draw(ctx);

    const lineName = routeInfo.parts.length > 2 ? routeInfo.parts[2].trim().toUpperCase() : "?";
    const textScale = 0.65;
    const textHeight = 7 * textScale;

    Text.create("RER Line Name").text(lineName)
        .pos(boxX + (boxSize / 2) + 0.3, boxY + (boxSize - textHeight) / 2)
        .scale(textScale).color(SncfUtils.getContrastColor(routeColor)).centerAlign().draw(ctx);

    const destsArray = getHeaderDestinationsArray(filteredArrivals, firstArrival);
    
    const boxWidth = pids.width - (boxX * 2) - 10;
    const finalScale = 0.55;
    const lineHeight = 7 * finalScale;
    const lineSpacing = 1.0;
    
    let lines = [];
    let currentLine = "";
    
    for (let i = 0; i < destsArray.length; i++) {
        let prefix = (currentLine === "") ? (i === 0 ? "" : "• ") : " • ";
        let proposed = currentLine + prefix + destsArray[i];
        if (SncfUtils.getStringWidth(proposed, finalScale) <= boxWidth) {
            currentLine = proposed;
        } else {
            if (currentLine !== "") lines.push(currentLine);
            currentLine = (i === 0 ? "" : "• ") + destsArray[i];
        }
    }

    if (currentLine !== "") lines.push(currentLine);

    let totalHeight = (lines.length * lineHeight) + ((lines.length - 1) * lineSpacing);
    let startY = 1.55 + (11.4 - totalHeight) / 2;

    for (let i = 0; i < lines.length; i++) {
        Text.create("RER Destination L" + i)
            .text(lines[i]).color(Colors.TextColor).scale(finalScale)
            .pos(boxX * 2, startY + (i * (lineHeight + lineSpacing))).leftAlign().draw(ctx);
    }
}

function drawArrivalsList(ctx, state, pids, filteredArrivals, uvs) {
    const startY = 14.6;
    const ARRIVAL_HEIGHT = 8.4;

    for (let i = 0; i < Math.min(5, filteredArrivals.length); i++) {
        let currentDeparture = filteredArrivals[i];
        if (currentDeparture == null) continue;

        let currentY = startY + ((ARRIVAL_HEIGHT + 0.47) * i);
        drawSingleArrival(ctx, state, pids, currentDeparture, i, currentY, ARRIVAL_HEIGHT, uvs);
    }
}

function drawSingleArrival(ctx, state, pids, arrival, index, yPos, height, uvs) {
    const rInfo = TrainUtils.getRouteInfo(state, arrival);
    const timeInfo = calculateTimeInfo(arrival);
    const isShortTrain = arrival.carCount() <= 5;
    
    const departureBoxSize = 2.5;
    let finalDepartureBoxSize = (timeInfo.isMinutes ? 5 * 0.5 : SncfUtils.getStringWidth(timeInfo.text, 0.55)) + (departureBoxSize * 2);
    if (timeInfo.text === "à l'approche") finalDepartureBoxSize -= departureBoxSize;

    Texture.create("Arrival Line Info Background")
        .texture("jsblock:acat/idf/images/colors.png").size(84, height).pos(1.35, yPos)
        .uv(uvs.whiteSquare[0], uvs.whiteSquare[1], uvs.whiteSquare[2], uvs.whiteSquare[3]).draw(ctx);

    Texture.create("Arrival Departure Info Background")
        .texture("jsblock:acat/idf/images/colors.png").size(49.3, height).pos(pids.width - 49.3 - 1.35, yPos)
        .color((isShortTrain || arrival.terminating()) && index === 0 ? Colors.DepartureColor : 0xDEE2EE)
        .uv(uvs.whiteSquare[0], uvs.whiteSquare[1], uvs.whiteSquare[2], uvs.whiteSquare[3]).draw(ctx);

    Texture.create("Train Number Background")
        .texture("jsblock:acat/idf/images/colors.png").size(10, 3.3).pos(1.35, yPos + (height / 2) - (3.3 / 2))
        .uv(uvs.trainNumberSquare[0], uvs.trainNumberSquare[1], uvs.trainNumberSquare[2], uvs.trainNumberSquare[3]).draw(ctx);

    Text.create("Train Number").text(rInfo.trainNumber)
        .scale(0.2).pos(1.35 + (10 / 2), yPos + (height / 2) - ((7 * 0.2) / 2))
        .centerAlign().color(0xFFFFFF).bold().size(45, 9).stretchXY().draw(ctx);

    Text.create("Train Destination").text(arrival.destination())
        .scale(0.6).pos((1.35 * 2) + 10 + 0.6, yPos + (height / 2) - ((7 * 0.6) / 2))
        .leftAlign().color(Colors.TextColor).size(117.5, 20).stretchXY().draw(ctx);

    Texture.create("Departure Background")
        .texture("jsblock:acat/idf/images/colors.png").size(finalDepartureBoxSize, height).pos(pids.width - 49.3 - 1.35, yPos)
        .uv(uvs.whiteSquare[0], uvs.whiteSquare[1], uvs.whiteSquare[2], uvs.whiteSquare[3])
        .color(0x000000).draw(ctx);

    let textColor = Colors.DepartureColor;
    if (timeInfo.shouldBlink) {
        textColor = (Math.floor(Date.now() / 750) % 2 === 0) ? Colors.DepartureColor : Colors.DepartureBlinking;
    }

    const textXPos = pids.width - 49.3 - 1.35 + departureBoxSize - ((timeInfo.text.length > 1) && timeInfo.isMinutes ? (SncfUtils.getStringWidth(timeInfo.text, 0.4) / 3) : 0) + (timeInfo.isUnderAnHour ? 0.3 : 1.6);
    const textYPos = timeInfo.isMinutes ? yPos + 1.5 + (timeInfo.isUnderAnHour ? 0 : 1.5) : yPos + (height / 2) - ((7 * 0.6) / 2) + 0.3;

    Text.create("Departure Text")
        .text(timeInfo.text).size(finalDepartureBoxSize * 3, height).scale(timeInfo.isMinutes ? (timeInfo.isUnderAnHour ? 0.4 : 0.25) : 0.45)
        .scaleXY().bold().pos(textXPos, textYPos)
        .leftAlign().color(textColor).draw(ctx);

    if (timeInfo.isMinutes) {
        if (timeInfo.isUnderAnHour) {
            Text.create("Minutes Text").text("min").size(finalDepartureBoxSize, height).scale(0.25)
                .pos(pids.width - 49.3 - 1.35 + (finalDepartureBoxSize / 2), yPos + height - 3)
                .centerAlign().color(Colors.DepartureBlinking).draw(ctx);
        }
        if (timeInfo.isDelayed) {
            Text.create("Train Delayed").text("retardé").bold().scale(0.45)
                .pos(pids.width - 49.3 + 0.25 + finalDepartureBoxSize, yPos + (height / 2) - ((7 * 0.45) / 2))
                .leftAlign().color(Colors.TextColor).size(117.5, 20).stretchXY().draw(ctx);
        }
    }

    if ((timeInfo.isMinutes || timeInfo.atPlatform) && !timeInfo.isDelayed) {
        let sizeText = arrival.terminating() ? "train terminus" : (isShortTrain ? "train court" : "");
        Text.create("Train Size").text(sizeText).bold()
            .scale(0.45).size(timeInfo.atPlatform ? 40 : 70, height).scaleXY()
            .pos(pids.width - 49.3 + 0.25 + finalDepartureBoxSize, yPos + (height / 2) - ((7 * 0.45) / 2)).leftAlign()
            .color((isShortTrain || arrival.terminating()) && index === 0 ? Colors.DepartureBackground : Colors.TextColor).draw(ctx);
    }

    const platformBoxSize = 6;
    const platformSize = 0.75;
    const platformXPos = pids.width - (1.35 * 2) - platformBoxSize;

    Texture.create()
        .texture("jsblock:acat/sncf/images/platform_border.png").color(Colors.TextColor)
        .pos(platformXPos, yPos + (height - platformBoxSize) / 2)
        .size(platformBoxSize, platformBoxSize).draw(ctx);

    Text.create("Platform Text")
        .centerAlign().text("quai").color(Colors.TextColor).scale(0.19).bold()
        .pos(platformXPos + (platformBoxSize / 2), yPos + (platformSize * 2) + 0.3).draw(ctx);
        
    Text.create("Platform Number")
        .centerAlign().text(arrival.platformName().slice(0).split("|")[0])
        .color(Colors.TextColor).scale(platformSize).size(platformBoxSize * platformSize, platformBoxSize * platformSize)
        .scaleXY().bold().pos(platformXPos + (platformBoxSize / 2), yPos + (height / 2) - (platformSize / 2)).draw(ctx);
}

function drawFooter(ctx, pids, trainType, uvs) {
    const isTransilien = (trainType == TRAIN_TYPES.TRANSILIEN);

    Texture.create("RER Box Background").texture("jsblock:acat/idf/images/rer_square.png").size(4.3, 5.5)
        .uv(uvs.rerInfoSquare[0], uvs.rerInfoSquare[1], uvs.rerInfoSquare[2], uvs.rerInfoSquare[3])
        .pos(1.35, pids.height - 15.7).draw(ctx);

    Texture.create("RER Box Logo Background")
        .texture(`jsblock:acat/idf/images/${isTransilien ? "transilien" : "rer"}_line_white.png`).size(3.5, 3.25)
        .uv(uvs.rerInfoLogoSquare[0], uvs.rerInfoLogoSquare[1], uvs.rerInfoLogoSquare[2], uvs.rerInfoLogoSquare[3])
        .color(0xc6c4e3).pos(1.35, pids.height - 15.7).draw(ctx);

    Texture.create("Footer Background")
        .texture("jsblock:acat/idf/images/colors.png").size(pids.width - 5.5, 15.7).pos(5.5, pids.height - 15.7)
        .uv(uvs.whiteSquare[0], uvs.whiteSquare[1], uvs.whiteSquare[2], uvs.whiteSquare[3]).draw(ctx);
}

function drawClock(ctx, pids, whiteSquareUV) {
    const date = new Date(typeof Timing !== "undefined" ? Timing.currentTimeMillis() : Date.now());
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    Texture.create("Clock Background")
        .texture("jsblock:acat/idf/images/colors.png").size(15, 6.4).pos(pids.width - 15 - 2.7, 0)
        .uv(whiteSquareUV[0], whiteSquareUV[1], whiteSquareUV[2], whiteSquareUV[3])
        .color(Colors.DepartureBackground).draw(ctx);

    Text.create("Hours").text(hours)
        .pos(pids.width - 10.5, 1.5).rightAlign().scale(0.45)
        .color(Colors.DepartureColor).draw(ctx);

    Text.create("Time Separator").text(":")
        .pos(pids.width - 10, 1.5).centerAlign().scale(0.4)
        .color((Math.floor(Date.now() / 1000) % 2 === 0) ? Colors.DepartureColor : Colors.DepartureBlinking).draw(ctx);

    Text.create("Minutes").text(minutes)
        .pos(pids.width - 9.5, 1.5).leftAlign().scale(0.45)
        .color(Colors.DepartureColor).draw(ctx);
}

function getHeaderDestinationsArray(filteredArrivals, firstArrival) {
    let uniqueDestinations = [];
    let lowerCasesList = [];

    for (let i = 0; i < Math.min(6, filteredArrivals.length); i++) {
        let arr = filteredArrivals[i];
        if (arr != null) {
            let name = arr.destination().trim();
            if (name !== "") {
                let lower = name.toLowerCase();
                if (!lowerCasesList.includes(lower)) {
                    lowerCasesList.push(lower);
                    uniqueDestinations.push(name);
                }
            }
        }
    }
    
    uniqueDestinations.sort((a, b) => a.localeCompare(b));

    if (uniqueDestinations.length === 0) {
        uniqueDestinations.push(firstArrival.destination().trim());
    }

    return uniqueDestinations;
}

function getHeaderDestinations(pids, firstArrival, trainType) {
    let checkList = [];
    let isRerOrTransilien = (trainType == TRAIN_TYPES.RER || trainType == TRAIN_TYPES.TRANSILIEN);

    if (isRerOrTransilien) {
        for (let i = 0; i < 6; i++) {
            let arr = pids.arrivals().get(i);
            if (arr != null) {
                let originalName = arr.destination().trim();
                let lowerName = originalName.toLowerCase();
                
                if (originalName !== "" && checkList.indexOf(lowerName) === -1) {
                    checkList.push(lowerName);
                }
            }
        }
    }

    let uniqueDestinations = isRerOrTransilien ? getOriginalCases(pids, checkList) : [];

    if (uniqueDestinations.length === 0) {
        uniqueDestinations.push(firstArrival.destination().trim());
    }

    return uniqueDestinations.join(" • ");
}

function getOriginalCases(pids, lowerCasesList) {
    let result = [];
    for (let i = 0; i < 6; i++) {
        let arr = pids.arrivals().get(i);
        if (arr != null) {
            let name = arr.destination().trim();
            if (lowerCasesList.includes(name.toLowerCase()) && !result.includes(name)) {
                result.push(name);
            }
        }
    }
    return result;
}

function calculateTimeInfo(arrival) {
    let info = {
        text: "",
        isMinutes: false,
        shouldBlink: false,
        isDelayed: false,
        isUnderAnHour: true,
        atPlatform: false,
        isComing: false
    };

    let dInfo = TrainUtils.getTrainDelay(arrival);
    info.isDelayed = dInfo != null && dInfo.time > 60;

    let now = typeof Timing !== "undefined" ? Timing.currentTimeMillis() : Date.now();
    let timeDiff = arrival.arrivalTime() - now;

    if (timeDiff >= 60 * 1000 && timeDiff < 60 * 60 * 1000) {
        info.text = Math.floor(timeDiff / (60 * 1000)).toString(); 
        info.isMinutes = true;
    } else if (timeDiff <= 10 * 1000 && timeDiff >= 0) {
        info.text = "à l'approche";
        info.shouldBlink = true;
    } else if (timeDiff < 0) {
        info.text = "à quai";
        info.atPlatform = true;
    } else if (timeDiff >= 60 * 60 * 1000) {
        info.text = SncfUtils.formatTrainDelay(timeDiff / 1000, false, false);
        info.isUnderAnHour = false;
        info.isMinutes = true;
    } else {
        info.text = "à l'approche";
        info.isComing = true;
    }

    return info;
}
