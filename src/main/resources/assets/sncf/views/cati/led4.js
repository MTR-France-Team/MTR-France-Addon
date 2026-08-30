include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/debug_utils.js"));
include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:modules/train_utils.js"));
include(Resources.id("aca:modules/fake_route.js"));
include(Resources.id("aca:modules/i18n_handler.js"));
include(Resources.id("aca:components/sncf_theme.js"));

const CFG = {
    textColor: 0xEFA112,
    nameMaxLen: 29,
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
    if (pids.type == "pids_1a") {
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
    SncfTheme.drawFrame(ctx, pids, false);

    const arrivals = FakeRoute.resolveArrivals(ctx, state, pids);
    const departure = arrivals.get(0);

    if (departure == null) {
        img(ctx, "sncf:images/backgrounds/platform/led/background_4.png", -9.8, -5, pids.width + (9.8 * 2), pids.height + (5 * 2), null, 0);
        txt(ctx, ledText(I18nHandler.t(state, "ledNoDeparture")), 7, 24.75, 0.65, CFG.textColor, "l", 1);
        return;
    }

    const route = departure.route();
    const platforms = route ? route.platforms : null;
    if (platforms == null || platforms.length === 0) return;

    img(ctx, "sncf:images/backgrounds/platform/led/background_4.png", -9.8, -5, pids.width + (9.8 * 2), pids.height + (5 * 2), null, 0);

    const remainingStations = TrainUtils.buildRemainingStations(departure);

    const rawDestination = departure.destination() === "" ? "⚠ Undefined Name" : "" + departure.destination();
    const destinationText = ledText(SncfTheme.displayStationName(SncfTheme.fitName(state, rawDestination, CFG.nameMaxLen)));

    txt(ctx, destinationText, 7, 24.75, 0.65, CFG.textColor, "l", 1);
    txt(ctx, ledText(I18nHandler.t(state, "departurePlanned")), 7, 35.75, 0.65, CFG.textColor, "l", 1);

    const schedule = TrainUtils.getStableSchedule(state, departure, departure.departureTime());
    const delay = TrainUtils.applyForceDelay(state, schedule);
    const isDelayed = delay.time > CFG.delayThresholdSeconds;

    txt(ctx, RenderUtils.formatTime(schedule.scheduledTime), pids.width - 6, 35.75, 0.65, CFG.textColor, "r", 1);

    if (isDelayed) {
        const delayText = RenderUtils.formatTrainDelay(delay.time, !ConfigHandler.get(state, "exactDelay"), true);
        txt(ctx, ledText(I18nHandler.t(state, "delayedAbout", [delayText])), 7, 46.75, 0.65, CFG.textColor, "l", 1);
    }

    TrainUtils.advanceStationScroll(state, remainingStations.length, CFG.scrollSeconds);

    const nextStationText = ledText(SncfTheme.displayStationName(SncfTheme.fitName(state, remainingStations[state.stationIndex].name, CFG.nameMaxLen)));

    txt(ctx, nextStationText, 7, 57.75, 0.65, CFG.textColor, "l", 1);
}
