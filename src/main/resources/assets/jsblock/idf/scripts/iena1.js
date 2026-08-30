include(Resources.id("jsblock:scripts/pids_util.js"));
include(Resources.id("jsblock:sncf/scripts/utils/sncf_utils.js"));
include(Resources.id("jsblock:sncf/scripts/utils/train_utils.js"));

function create(ctx, state, pids) {}

function render(ctx, state, pids) {
    drawBackground(ctx, pids);
    drawArrivals(ctx, pids);
    drawClock(ctx, pids);
}

function drawBackground(ctx, pids) {
    Texture.create("Background")
        .texture("jsblock:idf/images/backgrounds/iena1.png")
        .size(pids.width, pids.height)
        .draw(ctx);
}

function drawArrivals(ctx, pids) {
    const arrivals = pids.arrivals();
    let hasActiveArrivals = false;

    for (let i = 0; i < 4; i++) {
        const arrival = arrivals.get(i);

        if (arrival != null) {
            hasActiveArrivals = true;
            drawArrivalRow(ctx, pids, arrival, i);
        }
    }

    // If no arrivals, show "quai non desservi" background
    if (!hasActiveArrivals) {
        Texture.create("No Service Background")
            .texture("jsblock:idf/images/backgrounds/iena1_quai_non_desservi.png")
            .size(pids.width, pids.height)
            .draw(ctx);
    }
}

function drawArrivalRow(ctx, pids, arrival, rowIndex) {
    const rowY = rowIndex * 16 + 3.5;
    const routeInfo = TrainUtils.getRouteInfo(arrival);

    // Draw line icon
    drawLineIcon(ctx, routeInfo, rowY);

    // Draw mission number
    drawMissionNumber(ctx, routeInfo, rowY);

    // Draw destination
    drawDestination(ctx, arrival, rowY);

    // Draw platform
    drawPlatform(ctx, pids, arrival, rowY);

    // Draw ETA
    drawETA(ctx, pids, arrival, rowY);
}

function drawLineIcon(ctx, routeInfo, rowY) {
    // Use parts[2] for line if available, otherwise use trainType
    const line = (routeInfo.parts.length > 2 ? routeInfo.parts[2] : routeInfo.trainType).toLowerCase();

    Texture.create("Line Icon")
        .texture("jsblock:idf/images/line_icons/line_" + line + ".png")
        .pos(2, rowY - 0.5)
        .size(9, 9)
        .draw(ctx);
}

function drawMissionNumber(ctx, routeInfo, rowY) {
    Text.create("Mission")
        .text(routeInfo.trainNumber)
        .color(0xFFFFFF)
        .scale(0.6)
        .pos(13, rowY + 1.5)
        .size(18, 15)
        .stretchXY()
        .draw(ctx);
}

function drawDestination(ctx, arrival, rowY) {
    const destination = arrival.destination() || "Destination inconnue";

    Text.create("Arrival Destination")
        .text(destination)
        .color(0xFFFFFF)
        .pos(27, rowY)
        .size(70, 15)
        .stretchXY()
        .draw(ctx);
}

function drawPlatform(ctx, pids, arrival, rowY) {
    const platform = arrival.platformName() || "?";

    Text.create("Platform Number")
        .text(platform)
        .pos(pids.width - 8.5, rowY + 1)
        .centerAlign()
        .color(0x0C153E)
        .scale(0.75)
        .draw(ctx);
}

function drawETA(ctx, pids, arrival, rowY) {
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
        .size(25, 9)
        .scale(0.75)
        .stretchXY()
        .rightAlign()
        .pos(pids.width - 16, rowY + 1)
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
        .pos(pids.width - 21, pids.height - 8)
        .scale(0.75)
        .draw(ctx);
}

function dispose(ctx, state, pids) {}
