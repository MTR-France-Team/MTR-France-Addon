const TRAIN_TYPES = {
    "SNCF": { name: "SNCF", texture: { id: "sncf_white", size: [8, 4.5] }, aliases: [] },
    "INOUI": { name: "TGV inOUI", texture: { id: "inoui_white", size: [7.05, 3.74] }, aliases: ["TGVINOUI", "TGV INOUI"] },
    "OUIGO": { name: "OUIGO", texture: { id: "ouigo_white", size: [6.39, 6.39] }, aliases: ["TGVOUIGO", "TGV OUIGO"] },
    "LYRIA": { name: "TGV Lyria", aliases: ["TGVLYRIA", "TGV LYRIA"] },
    "TGV": { name: "TGV", aliases: [] },
    "FRECCIAROSSA": { name: "Frecciarossa", no_texture: true, aliases: ["TGVFRECCIAROSSA", "TGV FRECCIAROSSA"] },
    "TRENITALIA": { name: "Trenitalia", no_texture: true, aliases: ["TGVTRENITALIA", "TGV TRENITALIA"] },
    "THALYS": { name: "THALYS", texture: { id: "thalys_white", size: [7.05, 3.74] }, aliases: ["TGVTHALYS", "TGV THALYS"] },
    "RENFE": { name: "RENFE", texture: { id: "renfe_white", size: [7.05, 3.74] }, aliases: ["TGVRENFE", "TGV RENFE"] },
    "EUROSTAR": { name: "EUROSTAR", texture: { id: "eurostar_white", size: [6.44, 6.39] }, aliases: ["TGVEUROSTAR", "TGV EUROSTAR"] },
    "INTERCITIES": { name: "INTERCITÉS", aliases: ["IC", "INTERCITY", "INTERCITÉS", "INTERCITÉ"] },
    "TER": { name: "TER", texture: { id: "ter_white", size: [7.05, 3.74] }, aliases: [] },
    "CAR": { name: "CAR SNCF", aliases: ["AUTOCAR", "BUS", "BUS SNCF", "CAR SNCF"], bus: true },
    "LER": { name: "LER", aliases: [], bus: true },
    "RER": { name: "RER", aliases: [] },
    "TRANSILIEN": { name: "Transilien", aliases: [] },
    "IDTGV": { name: " iDTGV", aliases: ["ID TGV"] },
    "ICE": { name: "ICE", aliases: ["DBICE", "DB ICE"] },
    "VELVET": { name: "Velvet", aliases: ["TGV VELVET", "TGVVELTET"] },
    "LETRAIN": { name: "LE TRAIN", aliases: ["LE TRAIN"] },
    "AVE": { name: "AVE", aliases: ["TGVAVE", "TGV AVE", "RENFE AVE"] },
    "NIGHTJET": { name: "Nightjet", aliases: [] },
    "EUROPEAN_SLEEPER": {  name: "European Sleeper", aliases: ["EUROPEANSLEEPER", "EUROSLEEPER", "EUSLEEPER"] },
    "NIGHT_INTERCITIES": {  name: "Intercités de Nuit", aliases: ["NIGHT_IC", "NIGHT_INTERCITY", "NIGHT_INTERCITÉS", "NIGHT_INTERCITÉ", "NIGHTINTERCITIES", "NIGHTIC", "NIGHTINTERCITY", "NIGHTINTERCITÉS", "NIGHTINTERCITÉ"] },
    "SPECIAL_TRAIN": { name: "Train spécial", aliases: ["SPECIALTRAIN"] }
};

const ALIAS_MAP = {};
for (let tKey in TRAIN_TYPES) {
    if (TRAIN_TYPES.hasOwnProperty(tKey)) {
        ALIAS_MAP[TRAIN_TYPES[tKey].name.toLowerCase()] = tKey;
        ALIAS_MAP[tKey.toLowerCase()] = tKey;
        let tAliases = TRAIN_TYPES[tKey].aliases;
        for (let i = 0; i < tAliases.length; i++) ALIAS_MAP[tAliases[i].toLowerCase()] = tKey;
    }
}

const TrainUtils = {

    getType: function (type) { return TRAIN_TYPES[type.toUpperCase()] || null; },
    getNameOfType: function (type) { let t = TRAIN_TYPES[type.toUpperCase()]; return t ? t.name : null; },
    getTextureOfType: function (type) { let t = TRAIN_TYPES[type.toUpperCase()]; return t ? t.texture : TRAIN_TYPES.SNCF.texture; },
    isShowingTexture: function (type) { let t = TRAIN_TYPES[type.toUpperCase()]; return t ? !t.no_texture : true; },
    
    findTypeFromAlias: function (alias) { 
        if (!alias) return null;
        return TRAIN_TYPES[ALIAS_MAP[("" + alias).toLowerCase()]] || null; 
    },
    findNameFromAlias: function (alias) { 
        if (!alias) return null;
        let t = ALIAS_MAP[("" + alias).toLowerCase()]; 
        return t ? TRAIN_TYPES[t].name : null; 
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

    getRouteInfo: function (state, departure) {
        if (!state.routeCache) state.routeCache = {};

        let rawRoute = "" + departure.routeNumber();
        if (rawRoute === "") rawRoute = "SNCF|";
        
        let depIndex = departure.departureIndex();
        let cacheKey = rawRoute + "_" + depIndex;

        if (state.routeCache[cacheKey]) return state.routeCache[cacheKey];

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
            let s = "" + num;
            while (s.length < length) s = "0" + s;
            return s;
        };

        if(pattern.indexOf["["] !== -1 && pattern.indexOf("]+") !== -1) {
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
            var base = parseInt(pattern.replace("+", ""), 10);
            if (!isNaN(base)) finalNumber = "" + (base + (depIndex * 2));
        }

        let result = {
            trainType: trainType,
            trainNumber: finalNumber,
            routeNumber: trainType + "|" + finalNumber,
            parts: parts
        };

        if (Object.keys(state.routeCache).length > 200) state.routeCache = {};
        state.routeCache[cacheKey] = result;

        return result;
    },
    
    getPlatformInfo: function (departure) {
        let rawPlatform = "" + departure.platformName();
        let parts = rawPlatform.split("|").map(p => p.trim());
        let platformName = parts[0].replace(/voie/i, "").trim(); 
        
        let section = "";
        let hall = "";

        for (let i = 1; i < parts.length; i++) {
            let p = parts[i];
            let pLower = p.toLowerCase();
            
            if (pLower.indexOf("hall") !== -1) {
                hall = p.replace(/hall/i, "").trim(); 
            } else if (!isNaN(parseInt(p))) {
                if (section === "") section = p;
                else hall = p;
            } else {
                hall = p;
            }
        }

        return {
            name: platformName,
            section: section,
            hall: hall,
            raw: rawPlatform
        };
    },

    getTrainDelay: function (departure) {
        if (typeof departure.deviation !== "function") {
            return { time: 0, undetermined: false, realtime: false };
        }

        let deviationMs = departure.deviation();
        
        if (deviationMs == null || isNaN(deviationMs)) {
            return { time: 0, undetermined: true, realtime: false };
        }

        let delaySec = Math.floor(deviationMs / 1000);
        if (delaySec < 0) delaySec = 0;

        let isRealtime = typeof departure.realtime === "function" ? departure.realtime() : false;

        return { time: delaySec, undetermined: false, realtime: isRealtime };
    }

}