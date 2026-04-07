include(Resources.id("aca:config_handler.js"));
include(Resources.id("aca:train_utils.js"));
include(Resources.id("aca:sncf_utils.js"));
include(Resources.id("aca:animation_manager.js"));
include(Resources.id("aca:advertising_manager.js"));


function render(ctx, state, pids) {
    // 1. Initialisation des paramètres depuis le block entity (ConfigHandler)
    ConfigHandler.sync(state, pids);
    SncfUtils.init(state);

    // Dessiner le cadre de l'écran en 3D (Si activé via -frame dans MTR)
    if (ConfigHandler.get(state, "frame")) {
        SncfUtils.drawFrame(ctx, pids, ConfigHandler.get(state, "logo"));
    }

    // 2. Récupération de la liste des trains pour ce quai
    let departures = pids.arrivals();

    // 3. LA MAGIE DE LA PUB : Pas de trains ou train dans très longtemps ?
    if (AdvertisingManager.shouldShowAds(departures)) {
        // On affiche notre magnifique système de Pubs Vidéo/Texte
        AdvertisingManager.draw(ctx, state, pids, 0, 0, pids.width, pids.height);
        
        // On s'arrête là, pas besoin de dessiner le reste de l'écran SNCF
        return; 
    }

    // 4. AFFICHAGE SNCF NORMAL (S'il y a des trains)
    let now = typeof Timing !== "undefined" && Timing.currentTimeMillis ? Timing.currentTimeMillis() : Date.now();

    // -- En-tête de l'écran --
    SncfUtils.drawClock(ctx, pids, now);
    
    Text.create("Header")
        .text("Prochains départs")
        .color(0xAAAAAA) // Gris clair
        .pos(2, 2)
        .scale(0.4)
        .draw(ctx);

    // -- Liste des Trains --
    let maxLines = 5; // Nombre max de trains affichés
    let startY = 10;  // Position Y du premier train
    let lineHeight = 8; // Espace entre chaque ligne

    for (let i = 0; i < Math.min(departures.length, maxLines); i++) {
        let dep = departures.get(i);
        let y = startY + (i * lineHeight);

        // Décryptage des infos du train (Les fameux numéros + retards)
        let routeInfo = TrainUtils.getRouteInfo(state, dep);
        let platformInfo = TrainUtils.getPlatformInfo(dep);
        let delayInfo = TrainUtils.getTrainDelay(dep);
        
        let destName = SncfUtils.shortenNames(state, dep.destinationString());

        // A. Type de train (Texte simple pour ce test, remplaçable par ton logo plus tard)
        Text.create("type_" + i)
            .text(routeInfo.trainType.toUpperCase())
            .color(0x00A8E8) // Bleu SNCF
            .pos(2, y)
            .scale(0.5)
            .draw(ctx);

        // B. Numéro du train (Généré avec ton système Pair/Impair)
        Text.create("num_" + i)
            .text(routeInfo.trainNumber)
            .color(0xFFFFFF)
            .pos(20, y)
            .scale(0.5)
            .draw(ctx);

        // C. Destination (Défilement fluide anti-lag !)
        AnimationManager.drawSmoothHorizontalText(
            ctx, state, "dest_scroll_" + i, pids, destName, 45, y, 
            { maxDrawWidth: pids.width - 90, speed: 12, scale: 0.5, color: 0xFFFFFF }
        );

        // D. Heure ou Retard
        let depTimeMs = dep.time();
        let timeStr = SncfUtils.formatTime(depTimeMs);
        let timeColor = 0xFFFFFF;

        if (delayInfo.time > 0) {
            // S'il y a du retard
            timeStr = "Retard " + SncfUtils.formatTrainDelay(delayInfo.time, ConfigHandler.get(state, "roundDelay"), true);
            
            // Si le retard est en temps réel, on fait clignoter le texte en rouge
            if (delayInfo.realtime) {
                timeColor = AnimationManager.blinkColor(0xFF5555, 0xAA0000, { interval: 500 });
            } else {
                timeColor = 0xFF5555;
            }
        } else if (depTimeMs - now < 60000) {
            // Train à l'approche (moins d'une minute) -> Clignote en jaune
            timeColor = AnimationManager.blinkColor(0xFFDD00, 0xFFFFFF, { interval: 400 });
        }

        Text.create("time_" + i)
            .text(timeStr)
            .color(timeColor)
            .rightAlign()
            .pos(pids.width - 25, y)
            .scale(0.5)
            .draw(ctx);

        // E. Numéro de la Voie
        Text.create("plat_" + i)
            .text(platformInfo.name || "-")
            .color(0x55FF55) // Vert
            .rightAlign()
            .pos(pids.width - 2, y)
            .scale(0.5)
            .draw(ctx);
    }
}