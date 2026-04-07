include(Resources.id("aca:config_handler.js"));
include(Resources.id("aca:train_utils.js"));
include(Resources.id("aca:sncf_utils.js"));
include(Resources.id("aca:animation_manager.js"));

function create(ctx, state, pids) {
    ConfigHandler.init(state);
    SncfUtils.init(state);
}

function dispose(ctx, state, pids) {}

function render(ctx, state, pids) {
    ConfigHandler.sync(state, pids);

    let showArrivals = ConfigHandler.get(state, "arrivals");
    let theme = ConfigHandler.get(state, "theme");
    let isGrayedThemed = theme == null ? false : (ConfigHandler.get(state, "theme") == "gd" || ConfigHandler.get(state, "theme") == "gray");

    const darkerBackground = showArrivals ? SncfUtils.getUV(0, 5, 5, 5) : SncfUtils.getUV(0, isGrayedThemed ? 10 : 0, 5, 5); 
    
    Texture.create()
        .texture("sncf:images/colors.png") // "sncf/dpg.png"
        .uv(darkerBackground[0], darkerBackground[1], darkerBackground[2], darkerBackground[3])
        .pos(0, 0)
        .size(pids.width, pids.height)
        .draw(ctx);


    let pageNumber = ConfigHandler.get(state, "page");
    if (!pageNumber || pageNumber < 1) pageNumber = 1;

    let showMessage = ConfigHandler.get(state, "showMessage");
    let maxDepartures = showMessage ? 9 : 10;
    let Height = pids.height / 10;

    const lighterBackground = showArrivals ? SncfUtils.getUV(5, 5, 5, 5) : SncfUtils.getUV(5, isGrayedThemed ? 10 : 0, 5, 5);
    for(let i = 0 ; i < 10; i+=2) {
        Texture.create().texture("sncf:images/colors.png")
            .uv(lighterBackground[0], lighterBackground[1], lighterBackground[2], lighterBackground[3])
            .pos(0, i*Height).size(pids.width, Height).draw(ctx);
    }

    Texture.create()
        .texture(`sncf:images/${showArrivals ? "arrival" : "departure"}_text.png`)
        .pos(0, showMessage ? showArrivals ? -1 : 0
            : showArrivals ? Height-1 : Height)
        .size(pids.width, pids.height)
        .draw(ctx);

    let currentPage = (maxDepartures * (pageNumber-1));

    for(let i = 0; i < 10; i++) {
        let realI = i + ConfigHandler.get(state, "startIndex");
        let departure = i < maxDepartures ? getSimulatedDeparture(state, realI + currentPage, pids.arrivals()) : null;

        if(departure === null || departure === undefined) continue;

        let info = TrainUtils.getRouteInfo(state, departure);
        let delay = TrainUtils.getTrainDelay(departure);

        drawTrainInformation(ctx, state, pids, i*Height, realI + currentPage, Height, departure, info, delay, true);
    }

    if(true) {

        /*let stops = "Gare de Lyon - Paris Austerlitz - Paris Bercy - Paris Gare de l'Est - Paris Gare du Nord - Paris Saint-Lazare - Paris Montparnasse - Paris Saint-Michel Notre-Dame - Paris Gare de Lyon".toUpperCase();
        // 1. Appel du gestionnaire d'animation
        const anim = AnimationManager.scrollHorizontal(state, "row_" + 0, stops, {
            visibleWidth: 75,     // MAX_VISIBLE_WIDTH
            waitTime: 3,        // TIME_BETWEEN_STATES
            speed: 14.33,       // Ton ancienne vitesse
            scale: 0.5          // Le scale du texte
        });

        AnimationManager.drawSmoothHorizontalText(ctx, state, "row_" + 1, pids, stops, 20, pids.height + 15, {
            maxDrawWidth: pids.width - (20 * 1.65) - 20, // Zone visible entre tes deux bords
            speed: 14.33,
            scale: 0.5,
            color: 0xFFFFFF
        });

        // 2. Dessin
        Text.create()
        .text(anim.text) // Le texte est déjà découpé !
        .color(0xFFFFFF)
        .leftAlign()
        .scale(0.5)
        .size(1.5, 5.5)
        .font("mtr:achemine_bold")
        .pos(20 - anim.offset, pids.height + 20) // Offset appliqué ici
        .draw(ctx);*/
    }

    if(showMessage) {
        Texture.create()
        .texture("sncf:images/hour_box.png")
        .pos(0, 0)
        .size(pids.width, pids.height)
        .draw(ctx);

        SncfUtils.drawClock(ctx, pids, Timing.currentTimeMillis());
    }

    if (ConfigHandler.get(state, "frame")) {
        SncfUtils.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"));
    }

}

function drawTrainInformation(ctx, state, pids, startY, index, ROW_HEIGHT, departure, trainInfo, delayInfo, isArrivalsHidden) {
    const routeInfo = trainInfo;
    const trainType = routeInfo.trainType;
    const trainNumber = routeInfo.trainNumber;

    let trainName = TrainUtils.findNameFromAlias(trainType) || "SNCF";
    let isAnAutocar = TrainUtils.isMatchingOneAliasOfType(trainName, TRAIN_TYPES.CAR);

    let remainingHeight = isArrivalsHidden ? ROW_HEIGHT : ROW_HEIGHT - (8 * 0.5) - 1;

    let trainNamePixelSize = 0.28;
    let trainNumberPixelSize = 0.4;
    let trainTypeHeight = 10 * trainNamePixelSize + 7 * trainNumberPixelSize;
    let trainTypeStartY = startY + (remainingHeight - trainTypeHeight) / 1.125;

    let startX = 12;

    if(!isAnAutocar) {
        let trainIdentifier = TrainUtils.findTypeFromAlias(trainName) || TrainUtils.getType("SNCF");

        let trainTexture = trainIdentifier.texture || TrainUtils.getTextureOfType("SNCF");

        let iconWidth = trainTexture.size[0];
        let iconHeight = trainTexture.size[1];
        let trainTextureId = trainTexture.id || "blank";

        let showingTexture = TrainUtils.isShowingTexture(trainName);

        if(showingTexture) {
            Texture.create()
            .texture("jsblock:sncf/images/labels/" + trainTextureId + ".png")
            .pos((startX - iconWidth)/2, startY + (remainingHeight - iconHeight) / 3)
            .size(iconWidth, iconHeight)
            .draw(ctx);
        }
    }
    
    const delay = delayInfo;
    const timeLimitToShowDelay = 20; // seconds
    let showDelay = false;

    const onTimeMessages = ["à l'heure", "on time", "in orario"];
    let currentTime = Math.floor(Timing.currentTimeMillis() / 3000);
    let isOnTimeMessage = currentTime % 3 === 0;

    if (delay != null) {
        showDelay = delay.time > timeLimitToShowDelay;
    }

    startX += 1;

    let departureTimePixelSize = 0.555;

    function getRemainingHeight(pxSize, height) {
        height = height || 8;
        return startY + (remainingHeight - (8 * pxSize)) / 1.6;
    }

    Text.create()
    .text(SncfUtils.formatTime(departure.departureTime() - (delay.time * 1000), "h"))
    .color(0xFEF103)
    .leftAlign()
    .font("mtr:achemine_bold")
    .size(55, 9.5)
    .scaleXY()
    .scale(departureTimePixelSize)
    .pos(30.375, getRemainingHeight(departureTimePixelSize, 6))
    .draw(ctx);

    let showArrivals = ConfigHandler.get(state, "arrivals");

    Text.create()
    .text(showArrivals ? departure.route().platforms[0].stationName : departure.destination()) //SncfUtils.displayStationName(SncfUtils.shortenNames(departure.destination() == "" ? "⚠ Undefined Name" : ""+departure.destination())))
    .color(0xFFFFFF)
    .scale(0.65)
    .size(100, 8.75)
    .font("mtr:achemine")
    .scaleXY()
    .leftAlign()
    .pos(48.75, getRemainingHeight(0.55))
    .draw(ctx);

    let size = isArrivalsHidden ? 6.5 : 5.5;

    // Si heure de départ inférieur à ou égale à 20mn après l'heure actuelle, on affiche le quai
    if((departure.departureTime() - Timing.currentTimeMillis()) <= 20 * 60 * 1000) {
        Text.create()
        .centerAlign()
        .text(departure.platformName().slice(0).split("|")[0])
        .color(0xFFFFFF)
        .scale(0.75)
        .font("mtr:achemine_bold")
        .size(size*0.75, size*0.75)
        .scaleXY()
        .pos(pids.width - (size * 0.725), startY + (isArrivalsHidden ? (ROW_HEIGHT - size) / 2 : size * 0.1) + size * 0.275)
        .draw(ctx);

        Texture.create()
        .texture("jsblock:sncf/images/pictograms/platform_border.png")
        .pos(pids.width - (size * 1.25), startY + (isArrivalsHidden ? (ROW_HEIGHT - size) / 2 : size * 0.1))
        .size(size, size)
        .draw(ctx);
    } else if((departure.departureTime() - Timing.currentTimeMillis()) <= 2 * 60 * 60 * 1000) { // On affiche le Hall si heure de départ inférieur à 2h
        let platformInfo = TrainUtils.getPlatformInfo(departure);

        if(platformInfo.hall != null && platformInfo.hall !== "") {
            Texture.create()
            .texture("idf:images/rer_square.png")
            .pos(pids.width - (size * 1.25), startY + (isArrivalsHidden ? (ROW_HEIGHT - size) / 2 : size * 0.1))
            .size(size, size)
            .draw(ctx);

            Text.create()
            .centerAlign()
            .text("Hall")
            .color(0x0c0c0c)
            .scale(0.75)
            .font("mtr:achemine_bold")
            .size(size*0.75, size*0.75)
            .scaleXY()
            .pos(pids.width - (size * 0.725), startY + 0.1)
            .draw(ctx);

            let hallSize = 0.8;

            Text.create()
            .centerAlign()
            .text(platformInfo.hall)
            .color(0x0c0c0c)
            .scale(hallSize)
            .font("mtr:achemine_bold")
            .size(size*hallSize, size*hallSize)
            .scaleXY()
            .bold()
            .pos(pids.width - (size * 0.725), startY + (4 * 0.75) + 0.2)
            .draw(ctx);
        }
    }

    if(isOnTimeMessage) {
        if(!isAnAutocar) {
            if(showDelay) {
                Text.create("Delay Title")
                .text("retard")
                .color(0xFEF103)
                .scale(0.32)
                .leftAlign()
                .font("mtr:achemine_bold")
                .pos(startX, startY + (7 * 0.32 / 2.4))
                .draw(ctx);

                Text.create("Delay Time")
                .text(SncfUtils.formatTrainDelay(delay.time+600, ConfigHandler.get(state, "roundDelay"), false) + ".")
                .color(0xFEF103)
                .scale(0.32)
                .leftAlign()
                .font("mtr:achemine_bold")
                .pos(startX, (startY + ROW_HEIGHT) - (7 * 0.32 * 1.7))
                .draw(ctx);
            } else {
                Text.create("On Time Message")
                .text(onTimeMessages[currentTime % onTimeMessages.length])
                .color(0xFFFFFF)
                .scale(0.49)
                .leftAlign()
                .scaleXY()
                .font("mtr:achemine_bold")
                .size(25, 10)
                .pos(startX, startY + (remainingHeight - 7*0.375) / 3)
                .draw(ctx);
            }
        } else {
            Text.create("Train number")
            .text(trainNumber)
            .color(0xFFFFFF)
            .scale(trainNumberPixelSize)
            .font("mtr:achemine_bold")
            .pos(startX, trainTypeStartY + 10 * trainNamePixelSize)
            .draw(ctx);
        }
    } else {
        if(!isAnAutocar) {
            Text.create("Train name")
            .text(trainName)
            .size(62.5, 10)
            .scaleXY()
            .color(0xFDFCFC)
            .font("mtr:achemine")
            .scale(trainNamePixelSize)
            .pos(startX, startY + (7 * 0.32 / 2.4))
            .draw(ctx);
        }

        Text.create("Train number")
        .text(trainNumber)
        .color(0xFFFFFF)
        .scale(trainNumberPixelSize)
        .font("mtr:achemine_bold")
        .pos(startX, (startY + ROW_HEIGHT) - (7 * 0.32 * 1.7))
        .draw(ctx);
    }

}

/**
 * Crée un train simulé avec stabilisation temporelle.
 * Utilise une moyenne lissée pour éviter les sauts d'horaires.
 */
function getSimulatedDeparture(state, targetIndex, realArrivals) {
    // 1. Compter les trains réels (Max 10)
    let realCount = 0;
    for(let i = 0; i < 10; i++) {
        if(realArrivals.get(i) == null) break;
        realCount++;
    }

    if (realCount < 2) return null;

    // 2. Si c'est un vrai train, on le retourne directement
    if (targetIndex < realCount) {
        return realArrivals.get(targetIndex);
    }

    // --- LOGIQUE DE STABILISATION ---

    // Initialisation du cache de fréquence si vide
    if (!state.sim) {
        state.sim = {
            avgInterval: 0,
            initialized: false
        };
    }

    const firstTime = realArrivals.get(0).departureTime();
    const lastTime = realArrivals.get(realCount - 1).departureTime();
    
    // Calcul de l'intervalle instantané actuel
    const totalDuration = lastTime - firstTime;
    const currentInterval = totalDuration / (realCount - 1);

    // LISSAGE : On mélange l'ancienne moyenne avec la nouvelle (95% ancienne, 5% nouvelle)
    // Cela empêche les horaires de sauter quand un train quitte la liste
    if (!state.sim.initialized) {
        state.sim.avgInterval = currentInterval;
        state.sim.initialized = true;
    } else {
        // Facteur de lissage (0.95 = très stable, 0.5 = très réactif)
        state.sim.avgInterval = (state.sim.avgInterval * 0.98) + (currentInterval * 0.02);
    }

    const stableInterval = state.sim.avgInterval;

    // --- CONSTRUCTION DU FAUX TRAIN ---

    // 3. Modèle Visuel : On copie les infos (Destination, N°...) du train correspondant dans le cycle
    // Ex: Le train 10 (Page 2, Ligne 1) copie le visuel du train 0
    const templateIndex = targetIndex % realCount;
    const templateDeparture = realArrivals.get(templateIndex);

    // 4. Projection Temporelle : On part du DERNIER train réel et on ajoute X fois l'intervalle
    // C'est beaucoup plus stable que de se baser sur le premier train.
    const trainsBeyondReal = targetIndex - (realCount - 1);
    const timeShift = Math.floor(trainsBeyondReal * stableInterval);

    // Capture des données (Force String pour éviter les crashs)
    const _dest = "" + templateDeparture.destination();
    const _platform = "" + templateDeparture.platformName();
    const _routeNum = "" + templateDeparture.routeNumber();
    const _routeColor = templateDeparture.routeColor();
    const _route = templateDeparture.route();
    const _platId = templateDeparture.platformId();
    const _isTerminating = templateDeparture.terminating();
    const _routeId = templateDeparture.routeId();
    const _realIndex = templateDeparture.departureIndex();
    const _carCount = templateDeparture.carCount(); // Si dispo

    // Calcul des temps basés sur le dernier train réel + intervalle stable
    // Note: On utilise lastTime (le dernier train de la liste) comme ancre
    const _newDepTime = lastTime + timeShift;
    const _newArrTime = (lastTime + timeShift) + (templateDeparture.arrivalTime() - templateDeparture.departureTime());

    // Faux index pour le cache
    const _fakeIndex = _realIndex + (targetIndex * 1000); 

    return {
        // API Standard
        destination: function() { return _dest; },
        departureTime: function() { return _newDepTime; },
        arrivalTime: function() { return _newArrTime; },
        platformName: function() { return _platform; },
        routeNumber: function() { return _routeNum; },
        routeColor: function() { return _routeColor; },
        route: function() { return _route; },
        terminating: function() { return _isTerminating; },
        platformId: function() { return _platId; },
        carCount: function() { return _carCount; },
        
        // API Cache/Interne
        routeId: function() { return _routeId; },
        departureIndex: function() { return _fakeIndex; },
        toString: function() { return "SimulatedDeparture: " + _dest; }
    };
}