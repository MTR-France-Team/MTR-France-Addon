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
        .texture("jsblock:idf/images/backgrounds/iena2.png")
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

    if (!hasActiveArrivals) {
        Texture.create("No Service Background")
            .texture("jsblock:idf/images/backgrounds/iena2_quai_non_desservi.png")
            .size(pids.width, pids.height)
            .draw(ctx);
    }
}

function drawArrivalRow(ctx, pids, arrival, rowIndex) {
    const rowY = (ctx.canvas, 16 + rowIndex * 16);
    const routeInfo = TrainUtils.getRouteInfo(arrival);

    drawLineIcon(ctx, routeInfo, rowY, rowIndex);
    drawMissionNumber(ctx, routeInfo, rowY, rowIndex);
    drawDestination(ctx, arrival, rowY, rowIndex);
    drawPlatform(ctx, pids, arrival, rowY, rowIndex);

    if (typeof drawArrivalTime !== 'undefined') drawArrivalTime(ctx, arrival, rowY, rowIndex);
}

function drawLineIcon(ctx, routeInfo, rowY, rowIndex) {
    const line = (routeInfo.parts.length > 2 ? routeInfo.parts[2] : routeInfo.trainType).toLowerCase();
    Texture.create("Line Icon " + rowIndex)
        .texture("jsblock:idf/images/line_icons/line_" + line + ".png")
        .pos(2, rowY - 0.5)
        .size(9, 9)
        .draw(ctx);
}

function drawMissionNumber(ctx, routeInfo, rowY, rowIndex) {
    Text.create("Mission " + rowIndex)
        .text(routeInfo.trainNumber)
        .color(0xFFFFFF)
        .scale(0.6)
        .pos(13, rowY + 1.5)
        .size(18, 15)
        .stretchXY()
        .draw(ctx);
}

function drawDestination(ctx, arrival, rowY, rowIndex) {
    const destination = arrival.destination() || "Destination inconnue";
    Text.create("Arrival Destination " + rowIndex)
        .text(destination)
        .color(0xFFFFFF)
        .pos(27, rowY)
        .size(73, 15)
        .stretchXY()
        .draw(ctx);
}

function drawPlatform(ctx, pids, arrival, rowY, rowIndex) {
    const platform = arrival.platformName() || "?";
    Text.create("Platform Number " + rowIndex)
        .text(platform)
        .pos(pids.width - 8.5, rowY + 1)
        .centerAlign()
        .color(0x0C153E)
        .scale(0.75)
        .draw(ctx);
}

// === Pour iena2.js ===
function drawArrivalTime(ctx, arrival, rowY, rowIndex) {
    const arrivalTime = new Date(arrival.arrivalTime());
    const timeString = arrivalTime.getHours().toString().padStart(2, '0') + ":" + arrivalTime.getMinutes().toString().padStart(2, '0');

    Text.create("Arrival Time " + rowIndex)
        .text(timeString)
        .color(0xFFFFFF)
        .scale(0.75)
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
        .color(0xFFFFFF)
        .pos(3, 3)
        .scale(0.75)
        .draw(ctx);
}

function dispose(ctx, state, pids) {}
