const TRAIN_TYPES = {
    "SNCF": {
        name: "SNCF",
        texture: { id: "sncf_white", size: [7.5, 4.8] },
        aliases: []
    },
    "INOUI": {
        name: "TGV inOUI",
        texture: { id: "inoui_white", size: [7.5, 4] },
        aliases: ["TGVINOUI", "TGV INOUI"]
    },
    "OUIGO": {
        name: "OUIGO",
        texture: { id: "ouigo_white", size: [6.8, 6.8] },
        aliases: ["TGVOUIGO", "TGV OUIGO"]
    },
    "LYRIA": {
        name: "TGV Lyria",
        aliases: ["TGVLYRIA", "TGV LYRIA"]
    },
    "TGV": {
        name: "TGV",
        aliases: []
    },
    "FRECCIAROSSA": {
        name: "Frecciarossa",
        no_texture: true,
        aliases: ["TGVFRECCIAROSSA", "TGV FRECCIAROSSA"]
    },
    "TRENITALIA": {
        name: "Trenitalia",
        no_texture: true,
        aliases: ["TGVTRENITALIA", "TGV TRENITALIA"]
    },
    "THALYS": {
        name: "THALYS",
        texture: { id: "thalys_white", size: [7.5, 4] },
        aliases: ["TGVTHALYS", "TGV THALYS"]
    },
    "RENFE": {
        name: "RENFE",
        texture: { id: "renfe_white", size: [7.5, 4] },
        aliases: ["TGVRENFE", "TGV RENFE"]
    },
    "EUROSTAR": {
        name: "EUROSTAR",
        texture: { id: "eurostar_white", size: [6.85, 6.8] },
        aliases: ["TGVEUROSTAR", "TGV EUROSTAR"]
    },
    "INTERCITIES": {
        name: "INTERCITÉS",
        aliases: ["IC", "INTERCITY", "INTERCITÉS", "INTERCITÉ"]
    },
    "TER": {
        name: "TER",
        texture: { id: "ter_white", size: [7.5, 4] },
        aliases: []
    },
    "CAR": {
        name: "CAR SNCF",
        aliases: ["AUTOCAR", "BUS", "BUS SNCF", "CAR SNCF"],
        bus: true
    },
    "LER": {
        name: "LER",
        aliases: [],
        bus: true
    },
    "RER": {
        name: "RER",
        aliases: []
    },
    "TRANSILIEN": {
        name: "Transilien",
        aliases: []
    },
    "METRO": {
        name: "Métro",
        aliases: []
    },
    "TRAM": {
        name: "Tramway",
        aliases: ["TRAMWAY"]
    },
    "IDTGV": {
        name: "iDTGV",
        aliases: ["ID TGV"],
    },
    "TGV/ICE": {
        name: "TGV/ICE",
        aliases: ["TGV+ICE", "TGV-ICE"]
    },
    "ICE": {
        name: "ICE",
        aliases: ["TGVICE", "TGV ICE"]
    }
};

const ALIAS_MAP = (function () {
    let map = {};
    for (let type in TRAIN_TYPES) {
        if (TRAIN_TYPES.hasOwnProperty(type)) {
            map[TRAIN_TYPES[type].name.toLowerCase()] = type;
            let aliases = TRAIN_TYPES[type].aliases;
            for (let i = 0; i < aliases.length; i++) {
                map[aliases[i].toLowerCase()] = type;
            }
            map[type.toLowerCase()] = type;
        }
    }
    return map;
})();

const TrainCache = TrainCache || {};
TrainCache.trainNumberCache = TrainCache.trainNumberCache || {};
TrainCache.trainNumberCounters = TrainCache.trainNumberCounters || {};
TrainCache.routeRandomCache = TrainCache.routeRandomCache || {};
TrainCache.cacheTimestamps = TrainCache.cacheTimestamps || {};
TrainCache.trainArrivalCache = TrainCache.trainArrivalCache || {};
TrainCache.lastRouteDelay = TrainCache.lastRouteDelay || {};

// Durée du timeout pour le nettoyage
const NUMBER_CACHE_TIMEOUT = 5 * 60 * 1000; // 5 min
const ARRIVAL_CACHE_TIMEOUT = 3.25 * 60 * 60 * 1000; // 3h15 (retard maximum pour un train)

const TrainUtils = {
    getType: function (type) {
        return TRAIN_TYPES[type.toUpperCase()] || null;
    },
    getNameOfType: function (type) {
        let trainType = TRAIN_TYPES[type.toUpperCase()];
        return trainType ? trainType.name : null;
    },
    getTextureOfType: function (type) {
        let trainType = TRAIN_TYPES[type.toUpperCase()];
        return trainType ? trainType.texture : TRAIN_TYPES.SNCF.texture;
    },
    isShowingTexture: function (type) {
        let trainType = TRAIN_TYPES[type.toUpperCase()];
        return trainType ? !trainType.no_texture : true;
    },
    findTypeFromAlias: function (alias) {
        return TRAIN_TYPES[ALIAS_MAP[alias.toLowerCase()]] || null;
    },
    findNameFromAlias: function (alias) {
        let type = ALIAS_MAP[alias.toLowerCase()];
        return type ? TRAIN_TYPES[type].name : null;
    },
    isMatchingOneAliasOfType: function (alias, trainType) {
        let aliases = trainType.aliases.slice();

        aliases.push(trainType.name);
        for (let i = 0; i < aliases.length; i++) {
            if (aliases[i].toLowerCase() === alias.toLowerCase()) return true;
        }
        return false;
    },
    checkMidnightReset: function () {
        const now = Date.now();
        
        if (!TrainCache.lastResetTimestamp) {
            TrainCache.lastResetTimestamp = now;
        }

        const lastResetDate = new Date(TrainCache.lastResetTimestamp).setHours(0, 0, 0, 0);
        const currentDate = new Date(now).setHours(0, 0, 0, 0);

        if (lastResetDate !== currentDate) {
            TrainCache.trainNumberCounters = {};
            TrainCache.lastResetTimestamp = now;
        }
    },
    cleanCache: function () {
        let currentTime = Date.now();
        let routeIdsInUse = {};

        // Nettoyage de trainNumberCache
        for (let key in TrainCache.trainNumberCache) {
            if (TrainCache.trainNumberCache.hasOwnProperty(key)) {
                let routeId = key.split("-")[0];
                routeIdsInUse[routeId] = true;

                let departure = TrainCache.trainNumberCache[key].departure;
                if (departure && departure.terminating()) {
                    let departureTime = departure.departureTime();
                    if (departureTime && departureTime < currentTime) {
                        if (!TrainCache.cacheTimestamps[key]) {
                            TrainCache.cacheTimestamps[key] = currentTime;
                        }
                    }
                }
            }
        }

        // Supprimer les entrées expirées de trainNumberCache
        for (let key in TrainCache.cacheTimestamps) {
            if (TrainCache.cacheTimestamps.hasOwnProperty(key)) {
                if (currentTime - TrainCache.cacheTimestamps[key] > NUMBER_CACHE_TIMEOUT) {
                    delete TrainCache.trainNumberCache[key];
                    delete TrainCache.cacheTimestamps[key];
                }
            }
        }

        // Nettoyage de trainArrivalCache et lastRouteDelay
        for (let key in TrainCache.trainArrivalCache) {
            if (TrainCache.trainArrivalCache.hasOwnProperty(key)) {
                let departure = TrainCache.trainArrivalCache[key].departure;
                let cleanupTime = TrainCache.trainArrivalCache[key].cleanupTime;
                let routeId = departure.routeId();

                if (departure && departure.terminating()) {
                    let departureTime = departure.departureTime();
                    if (departureTime && departureTime < currentTime) {
                        delete TrainCache.trainArrivalCache[key];
                        delete TrainCache.lastRouteDelay[routeId];
                    }
                } else if (cleanupTime && currentTime > cleanupTime) {
                    delete TrainCache.trainArrivalCache[key];
                    delete TrainCache.lastRouteDelay[routeId];
                }
            }
        }

        // Nettoyer routeRandomCache
        for (let routeId in TrainCache.routeRandomCache) {
            if (TrainCache.routeRandomCache.hasOwnProperty(routeId) && !routeIdsInUse[routeId]) {
                delete TrainCache.routeRandomCache[routeId];
            }
        }
    },
    getRouteInfo: function (departure) {
        this.cleanCache();

        const routeId = departure.routeId();
        const departureIndex = departure.departureIndex();
        const key = routeId + "-" + departureIndex;

        // Le cache par départ est toujours utile pour éviter de recalculer à chaque rafraîchissement de l'écran
        if (TrainCache.trainNumberCache[key]) {
            return TrainCache.trainNumberCache[key];
        }

        const routeNumberStr = ("" + departure.routeNumber()).length === 0 ? "SNCF|" : "" + departure.routeNumber();
        const routeNumberParts = routeNumberStr.split("|");
        const trainType = routeNumberParts[0].trim().toLowerCase();
        const trainNumberPattern = routeNumberParts.length > 1 ? routeNumberParts[1] : "";

        let finalTrainNumber = trainNumberPattern;

        // Si le pattern contient '%', on calcule le numéro de manière déterministe
        if (trainNumberPattern.includes("%")) {
            // Étape 1: Déterminer le numéro de base en remplaçant les '%' par '0'
            const baseNumberStr = trainNumberPattern.replace(/%/g, "0");
            const baseNumber = parseInt(baseNumberStr, 10);

            if (!isNaN(baseNumber)) {
                // Étape 2: Le numéro final est la base + l'index du départ. Simple et efficace.
                finalTrainNumber = String(baseNumber + departureIndex);
            }
        }

        // Le cache est rempli avec la valeur calculée, mais il n'y a plus de compteur à gérer
        const generatedRouteNumber = trainType + "|" + finalTrainNumber;
        TrainCache.trainNumberCache[key] = {
            trainType: trainType,
            trainNumber: finalTrainNumber,
            routeNumber: generatedRouteNumber,
            departure: departure,
            parts: routeNumberParts
        };

        return TrainCache.trainNumberCache[key];
    },
    getTrainDelay: function (departure) {
        let routeId = departure.routeId();
        let departureIndex = departure.departureIndex();
        let key = routeId + "-" + departureIndex;

        let currentArrivalTime = departure.arrivalTime();

        if (currentArrivalTime == null || isNaN(currentArrivalTime) || currentArrivalTime < 0) {
            return { time: 0, undetermined: true };
        }

        if (TrainCache.trainArrivalCache[key]) {
            let initialArrivalTime = TrainCache.trainArrivalCache[key].initialArrivalTime;
            let delayMs = currentArrivalTime - initialArrivalTime;
            let delaySec = Math.floor(delayMs / 1000);
            let finalDelay = delaySec >= 0 ? delaySec : 0;

            // Mettre à jour le dernier retard connu pour la route
            TrainCache.lastRouteDelay[routeId] = {
                delaySec: finalDelay,
                departureIndex: departureIndex,
                cleanupTime: TrainCache.trainArrivalCache[key].cleanupTime
            };

            return { time: finalDelay, undetermined: false };
        } else {
            // Vérifier s'il existe un retard résiduel pour cette route
            let residualDelay = 0;
            if (TrainCache.lastRouteDelay[routeId]) {
                let lastDelay = TrainCache.lastRouteDelay[routeId].delaySec;
                let lastIndex = TrainCache.lastRouteDelay[routeId].departureIndex;
                let indexDiff = departureIndex - lastIndex;
                if (indexDiff > 0) {
                    // Facteur de décroissance : 20% par arrêt
                    let decayFactor = 0.8;
                    residualDelay = Math.floor(lastDelay * Math.pow(decayFactor, indexDiff));
                }
            }

            TrainCache.trainArrivalCache[key] = {
                initialArrivalTime: currentArrivalTime,
                departure: departure,
                cleanupTime: Date.now() + ARRIVAL_CACHE_TIMEOUT
            };

            return { time: residualDelay, undetermined: false };
        }
    }
};