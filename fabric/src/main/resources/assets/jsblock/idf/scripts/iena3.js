include(Resources.id("jsblock:scripts/pids_util.js"));
include(Resources.id("jsblock:sncf/scripts/utils/sncf_utils.js"));
include(Resources.id("jsblock:sncf/scripts/utils/train_utils.js"));

// Configuration constants
const LONG_TRAIN_MINIMUM_CARS = 7;

function create(ctx, state, pids) {}

function render(ctx, state, pids) {
    const arrival = pids.arrivals().get(0);

    if (arrival != null) {
        drawMainContent(ctx, pids, arrival);
    } else {
        drawNoServiceBackground(ctx, pids);
    }

    drawClock(ctx, pids);
}

function drawMainContent(ctx, pids, arrival) {
    drawBackground(ctx, pids);

    const routeStations = getRouteStations(arrival, pids);

    if (routeStations.length === 0) {
        drawTerminusBackground(ctx, pids);
    } else {
        drawStationList(ctx, pids, routeStations);
    }

    drawTrainInfo(ctx, pids, arrival);
    drawPlatform(ctx, pids, arrival);
    drawTrainLength(ctx, pids, arrival);
    drawETA(ctx, pids, arrival);
}

function drawBackground(ctx, pids) {
    Texture.create("Background")
        .texture("jsblock:idf/images/backgrounds/iena3.png")
        .size(pids.width, pids.height)
        .draw(ctx);
}

function drawNoServiceBackground(ctx, pids) {
    Texture.create("No Service Background")
        .texture("jsblock:idf/images/backgrounds/iena3_quai_non_desservi.png")
        .size(pids.width, pids.height)
        .draw(ctx);
}

function drawTerminusBackground(ctx, pids) {
    Texture.create("Terminus Background")
        .texture("jsblock:idf/images/backgrounds/iena3_terminus.png")
        .size(pids.width, pids.height)
        .draw(ctx);
}

function getRouteStations(arrival, pids) {
    let routeStations = [];

    // Get all stations from the route
    const platforms = arrival.route().getPlatforms();
    for (let i = 0; i < platforms.length; i++) {
        routeStations.push(platforms[i].stationName);
    }

    // Normalize station names for comparison
    const currentStationName = pids.station().name.normalize('NFC');
    routeStations = routeStations.map(station => station.normalize('NFC'));

    // Find current station and get remaining stations
    const currentStationIndex = routeStations.indexOf(currentStationName);

    if (currentStationIndex !== -1) {
        return routeStations.slice(currentStationIndex + 1);
    }

    return routeStations;
}

function drawStationList(ctx, pids, stations) {
    if (stations.length < 13) {
        drawSimpleStationList(ctx, stations);
    } else if (stations.length < 25) {
        drawPaginatedStationList(ctx, stations);
    }
}

function drawSimpleStationList(ctx, stations) {
    // First column (up to 6 stations)
    for (let i = 0; i < Math.min(stations.length, 6); i++) {
        Text.create("Station Col1 " + i)
            .text(stations[i])
            .color(0xFFFFFF)
            .scale(0.6)
            .size(60, 8)
            .stretchXY()
            .pos(51, 32 + 7.4 * i)
            .draw(ctx);
    }

    // Second column (stations 7-12)
    for (let i = 0; i < Math.min(stations.length - 6, 6); i++) {
        if (i + 6 < stations.length) {
            Text.create("Station Col2 " + i)
                .text(stations[i + 6])
                .color(0xFFFFFF)
                .scale(0.6)
                .size(60, 8)
                .stretchXY()
                .pos(95, 32 + 7.4 * i)
                .draw(ctx);
        }
    }
}

function drawPaginatedStationList(ctx, stations) {
    const time = Date.now();
    const pageIndex = Math.floor(time / 5000) % 2;

    Text.create("Page Label")
        .text("Page")
        .color(0xFFFFFF)
        .scale(0.4)
        .pos(34, 32)
        .draw(ctx);

    Text.create("Page Number")
        .text((pageIndex + 1) + "/2")
        .color(0xFFFFFF)
        .scale(0.4)
        .pos(34, 37)
        .draw(ctx);

    const startIndex = pageIndex * 12;

    for (let i = 0; i < 6; i++) {
        const stationIndex = startIndex + i;
        if (stationIndex < stations.length) {
            Text.create("Station Page Col1 " + i)
                .text(stations[stationIndex])
                .color(0xFFFFFF)
                .scale(0.6)
                .size(60, 8)
                .stretchXY()
                .pos(51, 32 + 7.4 * i)
                .draw(ctx);
        }
    }

    for (let i = 0; i < 6; i++) {
        const stationIndex = startIndex + 6 + i;
        if (stationIndex < stations.length) {
            Text.create("Station Page Col2 " + i)
                .text(stations[stationIndex])
                .color(0xFFFFFF)
                .scale(0.6)
                .size(60, 8)
                .stretchXY()
                .pos(95, 32 + 7.4 * i)
                .draw(ctx);
        }
    }
}

function drawTrainInfo(ctx, pids, arrival) {
    const routeInfo = TrainUtils.getRouteInfo(arrival);
    // Use parts[2] for line if available, otherwise use trainType
    const line = (routeInfo.parts.length > 2 ? routeInfo.parts[2] : routeInfo.trainType).toLowerCase();

    // Draw line icon
    Texture.create("Line Icon")
        .texture("jsblock:idf/images/line_icons/line_" + line + ".png")
        .pos(34, 19)
        .size(10, 10)
        .draw(ctx);

    // Draw destination
    const destination = arrival.destination() || "Destination inconnue";
    Text.create("Arrival Destination")
        .text(destination)
        .color(0xFFFFFF)
        .scale(1)
        .size(55, 6)
        .stretchXY()
        .pos(46, 18)
        .draw(ctx);

    // Draw mission number
    Text.create("Mission")
        .text(routeInfo.trainNumber)
        .color(0xFFFFFF)
        .scale(0.5)
        .pos(46, 25)
        .draw(ctx);
}

function drawPlatform(ctx, pids, arrival) {
    const platform = arrival.platformName() || "?";

    Text.create("Platform Number")
        .text(platform)
        .pos(pids.width - 5.5, 2.5)
        .size(8, 8)
        .scaleXY()
        .scale(1)
        .centerAlign()
        .color(0x0C153E)
        .draw(ctx);
}

function drawTrainLength(ctx, pids, arrival) {
    const isLongTrain = arrival.carCount() >= LONG_TRAIN_MINIMUM_CARS;
    const lengthText = isLongTrain ? "Train long" : "Train court";

    Text.create("Train Length")
        .text(lengthText)
        .color(0xFFFFFF)
        .scale(0.4)
        .centerAlign()
        .pos(pids.width - 14, 12.5)
        .draw(ctx);
}

function drawETA(ctx, pids, arrival) {
    const eta = Math.ceil((arrival.arrivalTime() - Date.now()) / 60000);
    let etaText;

    if (eta <= 0) {
        etaText = "À quai";
    } else if (eta <= 1) {
        etaText = "À l'approche";
    } else {
        etaText = eta + " min";
    }

    Text.create("ETA Text")
        .text(etaText)
        .color(0xFFFFFF)
        .size(30, 7)
        .scaleXY()
        .rightAlign()
        .pos(pids.width - 2, 20)
        .draw(ctx);
}

function drawClock(ctx, pids) {
    const now = new Date();
    const timeString = now.getHours().toString().padStart(2, '0') +
        ":" +
        now.getMinutes().toString().padStart(2, '0');

    Text.create("Clock")
        .text(timeString)
        .color(0x0C153E)
        .pos(3, 3)
        .scale(0.75)
        .draw(ctx);
}

function dispose(ctx, state, pids) {}
