include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:modules/i18n_handler.js"));
include(Resources.id("aca:components/sncf_theme.js"));

const CFG = {
    textColor: 0xEFA112,
    nameMaxLen: 28,
    delayThresholdSeconds: 20,
    scrollSeconds: 5
};

function ledText(str) {
    return RenderUtils.stripAccents(("" + str).toUpperCase());
}

function create(ctx, state, pids) {
    state.stationIndex = 0;
    state.stationTimer = 0;
}

function dispose(ctx, state, pids) {}

function render(ctx, state, pids) {
    if (pids.type != "pids_1a") {
        Text.create("Unsupported")
            .text("PIDS non supportée.")
            .color(0xFFFFFF)
            .leftAlign()
            .scale(1)
            .pos(5, 5)
            .draw(ctx);
        return;
    }

    ConfigHandler.sync(state, pids);

    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const departure = arrivals.get(0);

    if (departure == null) {
        img(ctx, "sncf:images/backgrounds/platform/led/background_3.png", -9.8, -9.8, pids.width + (9.8 * 2), pids.height + (9.8 * 2), null, 0);
        txt(ctx, ledText(I18nHandler.t(state, "ledNoDeparture")), 2, 21, 1, CFG.textColor, "l", 1);
        return;
    }

    const route = departure.route();
    const platforms = route ? route.platforms : null;
    if (platforms == null || platforms.length === 0) return;

    img(ctx, "sncf:images/backgrounds/platform/led/background_3.png", -9.8, -9.8, pids.width + (9.8 * 2), pids.height + (9.8 * 2), null, 0);

    const remainingStations = TrainUtils.buildRemainingStations(departure);

    const rawDestination = departure.destination() === "" ? "⚠ Undefined Name" : "" + departure.destination();
    const destinationText = ledText(SncfTheme.displayStationName(SncfTheme.fitName(state, rawDestination, CFG.nameMaxLen)));

    txt(ctx, destinationText, 2, 21, 1, CFG.textColor, "l", 1);
    txt(ctx, ledText(I18nHandler.t(state, "departurePlanned")), 2, 35.5, 1, CFG.textColor, "l", 1);

    const schedule = TrainUtils.getStableSchedule(state, departure, departure.departureTime());
    const delay = TrainUtils.applyForceDelay(state, schedule);
    const isDelayed = delay.time > CFG.delayThresholdSeconds;

    txt(ctx, RenderUtils.formatTime(schedule.scheduledTime), pids.width - 1, 35.5, 1, CFG.textColor, "r", 1);

    let thirdRowText;
    if (isDelayed) {
        const delayText = RenderUtils.formatTrainDelay(delay.time, !ConfigHandler.get(state, "exactDelay"), true);
        thirdRowText = ledText(I18nHandler.t(state, "delayedAbout", [delayText]));
    } else {
        TrainUtils.advanceStationScroll(state, remainingStations.length, CFG.scrollSeconds);

        thirdRowText = ledText(SncfTheme.displayStationName(SncfTheme.fitName(state, remainingStations[state.stationIndex].name, CFG.nameMaxLen)));
    }

    txt(ctx, thirdRowText, 2, 50.5, 1, CFG.textColor, "l", 1);
}
