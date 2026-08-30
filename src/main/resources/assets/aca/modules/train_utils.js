const TRAIN_TYPES = {
    "SNCF": { name: "SNCF", texture: { id: "sncf", ratio: iconRatio(199, 104) }, aliases: [] },
    "INOUI": { name: "TGV inOUI", texture: { id: "inoui", ratio: iconRatio(700, 314) }, aliases: ["TGVINOUI", "TGV INOUI"], carLength: 18.7, locoLength: 22 },
    "OUIGO": { name: "OUIGO", texture: { id: "ouigo", ratio: iconRatio(1200, 1200) }, aliases: ["TGVOUIGO", "TGV OUIGO"], carLength: 18.7, locoLength: 22 },
    "LYRIA": { name: "TGV Lyria", aliases: ["TGVLYRIA", "TGV LYRIA"], carLength: 18.7, locoLength: 22 },
    "TGV": { name: "TGV", aliases: [], carLength: 18.7, locoLength: 22 },
    "FRECCIAROSSA": { name: "Frecciarossa", texture: { id: "frec", ratio: iconRatio(3807, 500) }, aliases: ["TGVFRECCIAROSSA", "TGV FRECCIAROSSA"], carLength: 25 },
    "TRENITALIA": { name: "Trenitalia", no_texture: true, aliases: ["TGVTRENITALIA", "TGV TRENITALIA"], carLength: 25 },
    "THALYS": { name: "THALYS", texture: { id: "thalys", ratio: iconRatio(513, 216) }, aliases: ["TGVTHALYS", "TGV THALYS"], carLength: 18.7, locoLength: 22 },
    "RENFE": { name: "RENFE", texture: { id: "renfe", ratio: iconRatio(904, 346) }, aliases: ["TGVRENFE", "TGV RENFE"], carLength: 18.7, locoLength: 22 },
    "EUROSTAR": { name: "EUROSTAR", texture: { id: "eurostar", ratio: iconRatio(990, 1010) }, aliases: ["TGVEUROSTAR", "TGV EUROSTAR"], carLength: 18.7, locoLength: 22 },
    "INTERCITIES": { name: "INTERCITÉS", aliases: ["IC", "INTERCITY", "INTERCITÉS", "INTERCITÉ"], carLength: 26.4, locoLength: 17 },
    "TER": { name: "TER", texture: { id: "ter", ratio: iconRatio(900, 436) }, aliases: [], carLength: 26.4 },
    "CAR": { name: "CAR SNCF", aliases: ["AUTOCAR", "BUS", "BUS SNCF", "CAR SNCF"], bus: true },
    "LER": { name: "LER", aliases: [], bus: true },
    "RER": { name: "RER", aliases: [], carLength: 24.5 },
    "TRANSILIEN": { name: "Transilien", aliases: [], carLength: 24 },
    "LEX": { name: "Léman Express", texture: { id: "lex", ratio: iconRatio(204, 152) }, aliases: [], carLength: 26.4 },
    "IDTGV": { name: " iDTGV", aliases: ["ID TGV"], carLength: 18.7, locoLength: 22 },
    "ICE": { name: "ICE", aliases: ["DBICE", "DB ICE"], carLength: 26.4, locoLength: 20.6 },
    "VELVET": { name: "Velvet", aliases: ["TGV VELVET", "TGVVELTET"], carLength: 26.4, locoLength: 19 },
    "LETRAIN": { name: "LE TRAIN", aliases: ["LE TRAIN"], carLength: 24 },
    "AVE": { name: "AVE", aliases: ["TGVAVE", "TGV AVE", "RENFE AVE"], carLength: 18.7, locoLength: 22 },
    "NIGHTJET": { name: "Nightjet", aliases: [], carLength: 26.4, locoLength: 19 },
    "EUROPEAN_SLEEPER": { name: "European Sleeper", aliases: ["EUROPEANSLEEPER", "EUROSLEEPER", "EUSLEEPER"], carLength: 26.4, locoLength: 19 },
    "NIGHT_INTERCITIES": { name: "Intercités de Nuit", aliases: ["NIGHT_IC", "NIGHT_INTERCITY", "NIGHT_INTERCITÉS", "NIGHT_INTERCITÉ", "NIGHTINTERCITIES", "NIGHTIC", "NIGHTINTERCITY", "NIGHTINTERCITÉS", "NIGHTINTERCITÉ"], carLength: 26.4, locoLength: 17 },
    "SPECIAL_TRAIN": { name: "Train spécial", aliases: ["SPECIALTRAIN"] },
    "EUROPE_EXPRESS": { name: "EUROPE EXPRESS", aliases: ["EUEXPRESS", "EUEX", "EEX", "EUROPEX"] }
};

const ALIAS_MAP = {};

Object.keys(TRAIN_TYPES).forEach(function(tKey) {
    const entry = TRAIN_TYPES[tKey];
    ALIAS_MAP[entry.name.toLowerCase()] = tKey;
    ALIAS_MAP[tKey.toLowerCase()] = tKey;
    entry.aliases.forEach(function(alias) { ALIAS_MAP[alias.toLowerCase()] = tKey; });
});

const TrainUtils = {

    getType: function (type) { return this.findTypeFromAlias(type); },
    getNameOfType: function (type) { let t = this.findTypeFromAlias(type); return t ? t.name : null; },
    getTextureOfType: function (type) { let t = this.findTypeFromAlias(type); return t ? t.texture : TRAIN_TYPES.SNCF.texture; },
    getIconSize: function (texture, opts) {
        const ratio = (texture && texture.ratio) || TRAIN_TYPES.SNCF.texture.ratio;
        const width = opts && opts.maxWidth;
        const height = opts && opts.maxHeight;

        if (width != null && height != null) return (width / ratio > height) ? [height * ratio, height] : [width, width / ratio];
        if (width != null) return [width, width / ratio];
        if (height != null) return [height * ratio, height];
        return [4.5 * ratio, 4.5];
    },
    getCarLength: function (type, isLoco) {
        let t = this.findTypeFromAlias(type) || {};
        return isLoco ? (t.locoLength || 18) : (t.carLength || 24);
    },
    getRouteInfo: function (state, departure) {
        if (departure === undefined) {
            departure = state;
            state = null;
        }

        let rawRoute = "" + departure.routeNumber();
        if (rawRoute === "") rawRoute = "SNCF|";

        let depIndex = departure.departureIndex();
        let cacheKey = rawRoute + "_" + depIndex;

        if (state) {
            if (!state.routeCache) {
                state.routeCache = {};
                state.routeCacheSize = 0;
            }
            if (state.routeCache[cacheKey]) return state.routeCache[cacheKey];
        }

        let parts = rawRoute.split("|");
        let trainType = parts[0].trim().toLowerCase();
        let pattern = parts.length > 1 ? parts[1].trim() : "";
        let finalNumber = pattern;

        let toAlpha = function(num, length) {
            let s = "";
            let n = num;
            while (n >= 0) {
                s = String.fromCharCode(65 + (n % 26)) + s;
                n = Math.floor(n / 26) - 1;
            }
            while (s.length < length) s = "A" + s;
            return s;
        };

        let toNum = function(num, length) {
            let wrapped = num % Math.pow(10, length);
            let s = "" + wrapped;
            while (s.length < length) s = "0" + s;
            return s;
        };

        if (pattern.indexOf("[") !== -1 && pattern.indexOf("]") !== -1) {
            let content = pattern.substring(pattern.indexOf("[") + 1, pattern.indexOf("]"));
            let variants = content.split(",");
            finalNumber = variants[depIndex % variants.length].trim();
        } else if (pattern.indexOf("+") !== -1 && !/^\d+\+$/.test(pattern)) {
            let directions = pattern.split("+");
            if (directions.length === 2) {
                finalNumber = (depIndex % 2 === 0) ? directions[0].trim() : directions[1].trim();
            }
        } else if (pattern.indexOf("%") !== -1) {
            finalNumber = pattern.replace(/(.)?(%+)/g, function(match, prevChar, dots) {
                let len = dots.length;
                let isAlpha = (prevChar && /[a-zA-Z]/.test(prevChar)) || (!prevChar && /[a-zA-Z]/.test(pattern));

                let replacement = isAlpha ? toAlpha(depIndex, len) : toNum(depIndex, len);
                return (prevChar || "") + replacement;
            });
        } else if (pattern.indexOf("+") !== -1) {
            const base = parseInt(pattern.replace("+", ""), 10);
            if (!isNaN(base)) finalNumber = "" + (base + (depIndex * 2));
        } else if (parts.length === 2 && pattern.length > 0 && pattern.length <= 2 && /^[A-Za-z0-9]+$/.test(pattern)) {
            const resolvedType = this.findTypeFromAlias(trainType);
            if (resolvedType === TRAIN_TYPES.RER || resolvedType === TRAIN_TYPES.TRANSILIEN) {
                const dest = typeof departure.destination === "function" ? ("" + departure.destination()) : "";
                const isTerm = typeof departure.terminating === "function" ? departure.terminating() : false;
                finalNumber = generateMissionCode(pattern, dest, isTerm);
            }
        }

        if (finalNumber === "") finalNumber = "---";

        let result = {
            trainType: trainType,
            trainNumber: finalNumber,
            routeNumber: trainType + "|" + finalNumber,
            parts: parts
        };

        if (state) {
            if (state.routeCacheSize > 200) {
                state.routeCache = {};
                state.routeCacheSize = 0;
            }
            state.routeCache[cacheKey] = result;
            state.routeCacheSize++;
        }

        return result;
    },

    isShowingTexture: function (type) { let t = this.findTypeFromAlias(type); return t ? !t.no_texture : false; },
    isTgvFamily: function (type) { return /(tgv|inoui|ouigo)/.test(type); },
    isTgvBrand: function (type) {
        let t = this.findTypeFromAlias(type);
        return !!t && (t === TRAIN_TYPES.TGV || t.aliases.some(function (alias) { return alias.toUpperCase().indexOf("TGV") !== -1; }));
    },
    isMatchingOneAliasOfType: function (alias, trainType) {
        if (!trainType || !alias) return false;
        let aliases = trainType.aliases.slice();
        aliases.push(trainType.name);

        let search = ("" + alias).toLowerCase();

        for (let i = 0; i < aliases.length; i++) {
            if (aliases[i].toLowerCase() === search) return true;
        }
        return false;
    },
    isBusType: function (state, arrival) {
        let t = arrival != null && this.findTypeFromAlias(this.getRouteInfo(state, arrival).trainType);
        return !!(t && t.bus);
    },
    isTerminatingHere: function (departure) {
        if (!departure.terminating()) return false;
        const route = departure.route();
        const platforms = route ? route.platforms : null;
        if (!platforms || platforms.length === 0) return false;
        const lastPlatformId = "" + platforms[platforms.length - 1].platformId;
        return ("" + departure.platformId()) === lastPlatformId;
    },

    findTypeFromAlias: function (alias) { return alias !== null ? TRAIN_TYPES[ALIAS_MAP[("" + alias).toLowerCase()]] : null; },
    findNameFromAlias: function (alias) { return alias !== null ? TRAIN_TYPES[ALIAS_MAP[("" + alias).toLowerCase()]].name : null; },

    resolveLineName: function (routeInfo) {
        if (routeInfo.parts.length > 2) return routeInfo.parts[2].trim().toUpperCase();
        if (routeInfo.parts.length === 2 && routeInfo.parts[1].trim().length <= 2) return routeInfo.parts[1].trim().toUpperCase();
        return null;
    },

    resolveLocoPositions: function (state, routeInfo) {
        const locoPosConf = ConfigHandler.get(state, "locoPos");
        if (!locoPosConf || locoPosConf.trim() === "") return null;
        if (locoPosConf.indexOf("=") === -1) {
            return locoPosConf.trim().split("+").map(function (x) { return parseInt(x.trim()); });
        }

        const trainType = ("" + routeInfo.trainType).toLowerCase();
        const realTrainName = (this.findNameFromAlias(trainType) || "sncf").toLowerCase();
        const currentTrainNumber = ("" + (routeInfo.trainNumber || "")).toLowerCase();

        let matchedValue = null;
        let defaultValue = null;
        const rules = locoPosConf.split(/[,;]/);
        rules.some(function (rule) {
            const trimmedRule = rule.trim();
            if (trimmedRule === "") return false;
            const pair = trimmedRule.split("=");
            if (pair.length === 2) {
                const target = pair[0].trim().toLowerCase();
                const value = pair[1].trim();
                if (target === "") { defaultValue = value; return false; }
                if (trainType.indexOf(target) !== -1 || realTrainName.indexOf(target) !== -1 || currentTrainNumber.indexOf(target) === 0) {
                    matchedValue = value;
                    return true;
                }
            } else {
                defaultValue = trimmedRule;
            }
            return false;
        });
        const finalValue = matchedValue !== null ? matchedValue : defaultValue;
        return finalValue !== null ? finalValue.split("+").map(function (x) { return parseInt(x.trim()); }) : null;
    },

    resolveCarLocoFlags: function (locoPositions, carCount, isTgv) {
        const hasCustom = locoPositions && locoPositions.length > 0;
        var locoFlags = [];
        var locoCount = 0;
        for (var i = 0; i < carCount; i++) {
            var carPosition = i + 1;
            var isLoco;
            if (hasCustom) {
                isLoco = locoPositions.indexOf(carPosition) !== -1;
            } else if (isTgv && carCount === 20) {
                isLoco = (i === 0 || i === 9 || i === 10 || i === 19);
            } else if (isTgv && carCount === 10) {
                isLoco = (i === 0 || i === 9);
            } else {
                isLoco = carCount >= 2 && (i === 0 || i === carCount - 1);
            }
            locoFlags.push(isLoco);
            if (isLoco) locoCount++;
        }
        var hasWagon = locoCount < carCount;
        var firstLocoIdx = -1;
        for (var fi = 0; fi < carCount; fi++) {
            if (locoFlags[fi]) { firstLocoIdx = fi; break; }
        }
        var flags = [];
        for (var j = 0; j < carCount; j++) {
            var isLocoCar = locoFlags[j];
            var isFrontLoco = false;
            if (isLocoCar) {
                if (hasWagon) {
                    var nextIsLoco = (j + 1 < carCount) && locoFlags[j + 1];
                    var shouldFlip = (j === carCount - 1) || nextIsLoco;
                    isFrontLoco = !shouldFlip;
                } else {
                    isFrontLoco = (j === firstLocoIdx);
                }
            }
            flags.push({ isLocoCar: isLocoCar, isFrontLoco: isFrontLoco });
        }
        return flags;
    },

    buildRemainingStations: function (departure) {
        const route = departure.route();
        const platforms = route ? route.platforms : null;
        if (platforms == null || platforms.length === 0) return null;

        const stations = [];
        platforms.forEach(function (platform) {
            stations.push({
                name: platform.stationName === "" ? "⚠ Undefined Name" : "" + platform.stationName,
                id: "" + platform.platformId
            });
        });

        const currentStationId = "" + departure.platformId();
        const currentIndex = stations.findIndex(function (station) { return station.id === currentStationId; });
        const remainingStations = stations.slice(currentIndex + 1);
        remainingStations.push({ name: "", id: "NULL-blanked-1" }, { name: "", id: "NULL-blanked-2" });
        return remainingStations;
    },

    collectRealDepartures: function (state, arrivals, isArrivals, opts) {
        const maxScan = (opts && opts.maxScan) || 20;
        const filterFn = opts && opts.filter;
        const candidates = [];
        const seenRouteKeys = {};
        for (var i = 0; i < maxScan; i++) {
            var dep = arrivals.get(i);
            if (dep == null) break;
            if (filterFn && !filterFn(dep)) continue;
            var candidateRouteKey = "" + dep.routeId() + "_" + dep.departureIndex();
            if (!seenRouteKeys[candidateRouteKey]) {
                seenRouteKeys[candidateRouteKey] = true;
                var candidateBaseTime = isArrivals ? dep.arrivalTime() : dep.departureTime();
                var candidateSchedule = this.getStableSchedule(state, dep, candidateBaseTime);
                candidates.push({ dep: dep, scheduledTime: candidateSchedule.scheduledTime });
            }
        }
        candidates.sort(function (a, b) { return a.scheduledTime - b.scheduledTime; });
        const result = [];
        candidates.forEach(function (c) { result.push(c.dep); });
        return result;
    },

    advanceStationScroll: function (state, remainingCount, intervalSeconds) {
        state.stationTimer += Timing.delta();
        if (state.stationTimer >= intervalSeconds) {
            state.stationTimer -= intervalSeconds;
            state.stationIndex = (state.stationIndex + 1) % remainingCount;
        }
        if (state.stationIndex >= remainingCount) state.stationIndex = 0;
        return state.stationIndex;
    },

    _platformCache: {},
    _platformCacheSize: 0,

    getPlatformInfo: function (departure) {
        let rawPlatform = "" + departure.platformName();
        let cached = this._platformCache[rawPlatform];
        if (cached) return cached;

        let parts = rawPlatform.split("|").map(function(p) { return p.trim(); });
        let platformName = parts[0].replace(/voie/i, "").trim();

        let section = "";
        let hall = "";

        for (let i = 1; i < parts.length; i++) {
            let p = parts[i];
            let pLower = p.toLowerCase();

            if (pLower.indexOf("repere") === 0) {
                continue;
            }

            if (pLower.indexOf("hall") !== -1) {
                hall = p.replace(/hall/i, "").trim();
            } else if (section === "") {
                section = p;
            } else {
                hall = p;
            }
        }

        let result = {
            name: platformName,
            section: section,
            hall: hall,
            raw: rawPlatform
        };

        if (this._platformCacheSize > 500) {
            this._platformCache = {};
            this._platformCacheSize = 0;
        }
        this._platformCache[rawPlatform] = result;
        this._platformCacheSize++;

        return result;
    },

    learnRepeatIntervals: function (cache, arrivals) {
        const now = Timing.currentTimeMillis();
        if (cache._riNextScan && now < cache._riNextScan) return;
        cache._riNextScan = now + REPEAT_INTERVAL_RESCAN_MS;

        if (!cache._repeatIntervals) cache._repeatIntervals = {};
        const intervals = cache._repeatIntervals;
        const firstSeen = {};
        arrivals.forEach(function (arrival) {
            const key = "" + arrival.routeId() + "_" + arrival.departureIndex();
            const time = arrival.arrivalTime();
            if (firstSeen.hasOwnProperty(key)) {
                const diff = Math.abs(time - firstSeen[key]);
                if (diff >= REPEAT_INTERVAL_MIN_MS) {
                    const routeIdKey = "" + arrival.routeId();
                    const existing = intervals[routeIdKey];
                    if (!existing || diff < existing) intervals[routeIdKey] = diff;
                }
            } else {
                firstSeen[key] = time;
            }
        });
    },

    _growthDelay: function (cache, departure, deviationMs) {
        if (!cache._heldTrains) {
            cache._heldTrains = {};
            cache._heldTrainsSize = 0;
        }
        const key = "" + departure.routeId() + "_" + departure.departureIndex();
        let entry = cache._heldTrains[key];
        if (!entry || deviationMs < entry.baseDeviation - 5000) {
            if (!entry && cache._heldTrainsSize > 200) {
                cache._heldTrains = {};
                cache._heldTrainsSize = 0;
            }
            if (!entry) cache._heldTrainsSize++;
            entry = { baseDeviation: deviationMs };
            cache._heldTrains[key] = entry;
        }
        const growth = deviationMs - entry.baseDeviation;
        if (growth >= HELD_CONFIRM_GROWTH_MS) {
            return { time: Math.floor(growth / 1000), undetermined: false, realtime: true, lowerBound: true };
        }
        return { time: 0, undetermined: false, realtime: true };
    },

    getTrainDelay: function (cache, departure) {
        if (departure === undefined) {
            departure = cache;
            cache = null;
        }
        if (typeof departure.deviation !== "function") {
            return { time: 0, undetermined: false, realtime: false };
        }

        let deviationMs = departure.deviation();

        if (deviationMs == null || isNaN(deviationMs)) {
            return { time: 0, undetermined: true, realtime: false };
        }

        const isRealtime = typeof departure.realtime === "function" ? departure.realtime() : false;
        if (!isRealtime) {
            return { time: 0, undetermined: false, realtime: false };
        }

        if (deviationMs < -DEVIATION_EARLY_LIMIT_MS) {
            const r = (cache && cache._repeatIntervals) ? cache._repeatIntervals["" + departure.routeId()] : undefined;
            if (r) {
                deviationMs += r;
            } else if (cache) {
                return this._growthDelay(cache, departure, deviationMs);
            }
        }

        let delaySec = Math.floor(deviationMs / 1000);
        if (delaySec < 0) delaySec = 0;

        return { time: delaySec, undetermined: false, realtime: isRealtime };
    },

    getStableSchedule: function (cache, departure, baseTimeMs) {
        const rawDelay = this.getTrainDelay(cache, departure);
        return { scheduledTime: baseTimeMs - (rawDelay.time * 1000), delay: rawDelay };
    },

    applyForceDelay: function (state, schedule) {
        const forceDelayMin = ConfigHandler.get(state, "forceDelay");
        return forceDelayMin > 0 ? { time: forceDelayMin * 60, undetermined: false, realtime: true } : schedule.delay;
    },

    buildSimulatedDeparture: function (state, targetIndex, realDepartures, realCount, firstTime, lastTime) {
        if (realCount < 2) return null;
        if (targetIndex < realCount) return realDepartures[targetIndex];

        if (!state.sim) {
            state.sim = { avgInterval: 0, lastSignature: "", departureCache: {} };
        }

        const sig = realCount + "_" + firstTime + "_" + lastTime;
        if (state.sim.lastSignature !== sig) {
            state.sim.lastSignature = sig;
            state.sim.departureCache = {};

            let intervals = [];
            for (let gi = 1; gi < realCount; gi++) {
                let t1 = realDepartures[gi - 1].departureTime();
                let t2 = realDepartures[gi].departureTime();
                if (t2 > t1) {
                    intervals.push(t2 - t1);
                }
            }

            let computedInterval = state.sim.avgInterval || 0;
            if (intervals.length > 0) {
                intervals.sort(function (a, b) { return a - b; });
                let median = intervals[Math.floor(intervals.length / 2)];
                median = Math.round(median / 1000) * 1000;

                if (computedInterval === 0) {
                    computedInterval = median;
                } else {
                    computedInterval = (computedInterval * 0.95) + (median * 0.05);
                }
            } else {
                computedInterval = realCount === 2 ? (lastTime - firstTime) : 300000;
            }

            if (computedInterval < 60000) computedInterval = 60000;
            state.sim.avgInterval = computedInterval;
        }

        if (state.sim.departureCache[targetIndex]) {
            return state.sim.departureCache[targetIndex];
        }

        const stableInterval = state.sim.avgInterval;

        const templateIndex = targetIndex % realCount;
        const templateDeparture = realDepartures[templateIndex];

        const trainsBeyondReal = targetIndex - (realCount - 1);
        const timeShift = Math.floor(trainsBeyondReal * stableInterval);

        const _dest = "" + templateDeparture.destination();
        const _platform = "" + templateDeparture.platformName();
        const _routeNum = "" + templateDeparture.routeNumber();
        const _routeColor = templateDeparture.routeColor();
        const _route = templateDeparture.route();
        const _platId = templateDeparture.platformId();
        const _isTerminating = templateDeparture.terminating();
        const _routeId = templateDeparture.routeId();
        const _realIndex = templateDeparture.departureIndex();
        const _carCount = templateDeparture.carCount();

        const _newDepTime = lastTime + timeShift;
        const _newArrTime = (lastTime + timeShift) + (templateDeparture.arrivalTime() - templateDeparture.departureTime());
        const _fakeIndex = _realIndex + (targetIndex * 1000);

        const fakeDeparture = {
            destination: function () { return _dest; },
            departureTime: function () { return _newDepTime; },
            arrivalTime: function () { return _newArrTime; },
            platformName: function () { return _platform; },
            routeNumber: function () { return _routeNum; },
            routeColor: function () { return _routeColor; },
            route: function () { return _route; },
            terminating: function () { return _isTerminating; },
            platformId: function () { return _platId; },
            carCount: function () { return _carCount; },
            routeId: function () { return _routeId; },
            departureIndex: function () { return _fakeIndex; },
            toString: function () { return "SimulatedDeparture: " + _dest; }
        };

        state.sim.departureCache[targetIndex] = fakeDeparture;
        return fakeDeparture;
    }

}

function iconRatio(width, height) { return width / height; }

function generateMissionCode(line, destination, isTerminating) {
    const seed = `${line || ""}|${destination || ""}|${isTerminating ? "T" : "D"}`;
    let hash = 0;
    let code = "";

    for (let i = 0; i < seed.length; i++) hash = ((hash * 31) + seed.charCodeAt(i)) >>> 0;

    for (let i = 0; i < 4; i++) {
        code += String.fromCharCode(65 + (hash % 26));
        hash = Math.floor(hash / 26);
    }

    return code;
}

const DEVIATION_EARLY_LIMIT_MS = 120000;
const HELD_CONFIRM_GROWTH_MS = 30000;
const REPEAT_INTERVAL_RESCAN_MS = 5000;
const REPEAT_INTERVAL_MIN_MS = 60000;

