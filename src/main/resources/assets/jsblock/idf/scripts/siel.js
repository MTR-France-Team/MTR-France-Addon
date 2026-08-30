include(Resources.id("jsblock:scripts/pids_util.js"));
include(Resources.id("jsblock:sncf/scripts/utils/sncf_utils.js"));
include(Resources.id("jsblock:sncf/scripts/utils/train_utils.js"));

function create(ctx, state, pids) {}

function render(ctx, state, pids) {
    drawBackground(ctx, pids);
    drawTrainInformation(ctx, pids);
    drawArivalTimes(ctx, pids);
    drawClock(ctx, pids);
}

function drawBackground(ctx, pids) {
    Texture.create("Background")
        .texture("jsblock:idf/images/backgrounds/siel.png")
        .size(pids.width, pids.height)
        .draw(ctx);
}

function drawTrainInformation(ctx, pids) {
    const firstArrival = pids.arrivals().get(0);

    if (firstArrival != null) {
        drawMainTrainInfo(ctx, pids, firstArrival);
    }
}

function drawMainTrainInfo(ctx, pids, arrival) {
    const routeInfo = TrainUtils.getRouteInfo(arrival);

    // Draw destination
    const destination = arrival.destination() || "Destination inconnue";
    Text.create("Destination")
        .text(destination)
        .color(0x023E91)
        .pos(28, 5)
        .size(75, 9)
        .scaleXY()
        .draw(ctx);

    // Draw line icon - Use parts[2] for line if available, otherwise use trainType
    const line = (routeInfo.parts.length > 2 ? routeInfo.parts[2] : routeInfo.trainType).toLowerCase();
    Texture.create("Line Icon")
        .texture("jsblock:idf/images/line_icons/line_" + line + ".png")
        .pos(14, 2.5)
        .size(11, 11)
        .draw(ctx);

    // Draw colored bar
    Texture.create("Line-Colored Bar")
        .texture("jsblock:idf/images/backgrounds/siel_bar.png")
        .color(arrival.routeColor())
        .size(pids.width, pids.height)
        .draw(ctx);
}

function drawArivalTimes(ctx, pids) {
    const firstArrival = pids.arrivals().get(0);
    const secondArrival = pids.arrivals().get(1);

    drawFirstArrival(ctx, firstArrival);
    drawSecondArrival(ctx, secondArrival);
}

function drawFirstArrival(ctx, arrival) {
    let displayText = "...";

    if (arrival != null) {
        const eta = Math.round(((arrival.arrivalTime() - Date.now()) + 20000) / 60000);
        displayText = formatETA(eta);
    }

    Text.create("Arrival 1")
        .text(displayText)
        .color(0xECBB00)
        .pos(47, 35)
        .scale(4.5)
        .rightAlign()
        .draw(ctx);
}

function drawSecondArrival(ctx, arrival) {
    let displayText = "...";

    if (arrival != null) {
        const eta = Math.round(((arrival.arrivalTime() - Date.now()) + 20000) / 60000);
        displayText = formatETA(eta);
    }

    Text.create("Arrival 2")
        .text(displayText)
        .color(0xECBB00)
        .pos(117, 35)
        .scale(4.5)
        .rightAlign()
        .draw(ctx);
}

function formatETA(eta) {
    if (eta > 99) {
        return "++";
    } else if (eta <= 0) {
        return "0";
    } else {
        return eta.toString();
    }
}

function drawClock(ctx, pids) {
    const now = new Date();
    const timeString = now.getHours().toString().padStart(2, '0') +
        ":" +
        now.getMinutes().toString().padStart(2, '0');

    Text.create("Clock")
        .text(timeString)
        .color(0xECBB00)
        .pos(pids.width - 25, 3)
        .scale(1)
        .draw(ctx);
}

function dispose(ctx, state, pids) {}
