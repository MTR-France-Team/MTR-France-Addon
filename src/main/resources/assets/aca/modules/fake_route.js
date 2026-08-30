const FAKE_ROUTE_NUMBER = "886277";
const FAKE_ROUTE_TYPE = "TER";
const FAKE_ROUTE_DESTINATION = "Lyon Perrache";
const FAKE_ROUTE_PLATFORM_NAME = "A";
const FAKE_ROUTE_COLOR = 0x0088CE;
const FAKE_ROUTE_START_BUFFER_MIN = 2;
const FAKE_ROUTE_STOPS = [
    { offsetMin: 0, name: "Firminy" },
    { offsetMin: 3, name: "Le Chambon-Feugerolles" },
    { offsetMin: 7, name: "La Ricamarie" },
    { offsetMin: 11, name: "Saint-Étienne Bellevue" },
    { offsetMin: 15, name: "Saint-Étienne Le Clapier" },
    { offsetMin: 18, name: "Saint-Étienne Carnot" },
    { offsetMin: 22, name: "Saint-Étienne Châteaucreux" },
    { offsetMin: 34, name: "Saint-Chamond" },
    { offsetMin: 41, name: "Rive-de-Gier" },
    { offsetMin: 53, name: "Givors" },
    { offsetMin: 56, name: "Givors Canal" },
    { offsetMin: 59, name: "Grigny-le-Sablon" },
    { offsetMin: 64, name: "Vernaison" },
    { offsetMin: 69, name: "Irigny Yvours" },
    { offsetMin: 72, name: "Pierre-Bénite" },
    { offsetMin: 75, name: "Oullins" },
    { offsetMin: 81, name: "Lyon Perrache" }
];

const FAKE_ROUTE_TYPE_DESTINATIONS = [
    "Lyon Perrache", "Marseille St-Charles", "Bordeaux St-Jean", "Nantes",
    "Strasbourg", "Nice Ville", "Toulouse Matabiau", "Lille Flandres",
    "Rennes", "Grenoble", "Dijon", "Montpellier St-Roch", "Metz",
    "Clermont-Ferrand", "Annecy", "Perpignan", "Tours", "Le Mans",
    "Angers St-Laud", "Besançon Viotte", "Reims", "Amiens", "Caen",
    "Brest", "Limoges Bénédictins", "Poitiers", "Avignon Centre"
];
const FAKE_ROUTE_RER_LINES = ["A", "B", "C", "D", "E"];
const FAKE_ROUTE_TRANSILIEN_LINES = ["H", "J", "K", "L", "N", "P", "U"];
const FAKE_ROUTE_TYPE_PLATFORM_LETTERS = ["A", "B", "C", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
const FAKE_ROUTE_TYPE_COLORS = [0x0088CE, 0xE2231A, 0x82C41A, 0xFFB612, 0x6E3892, 0x00A88F];

const FakeRoute = {
    _seededFraction: function(seed) {
        var str = "" + seed;
        var h = 0;
        for (var i = 0; i < str.length; i++) {
            h = ((h * 31) + str.charCodeAt(i)) >>> 0;
        }
        return (h % 1000) / 1000;
    },

    buildFakeCars: function(typeKey, seed) {
        var key = (typeKey || "").toUpperCase();
        var rand = this._seededFraction(seed != null ? seed : key);
        var count;
        if (key.indexOf("TGV") !== -1 || key === "INOUI" || key === "OUIGO" || key === "LYRIA" || key === "EUROSTAR" || key === "ICE") {
            count = rand < 0.5 ? 10 : 20;
        } else if (key === "RER") {
            count = rand < 0.5 ? 5 : 10;
        } else if (key === "TRANSILIEN") {
            count = Math.floor(rand * 7) + 6;
        } else if (key === "INTERCITIES" || key === "NIGHTJET") {
            count = Math.floor(rand * 8) + 7;
        } else if (key === "TER") {
            count = Math.floor(rand * 6) + 3;
        } else {
            count = Math.floor(rand * 18) + 3;
        }
        count = Math.max(3, Math.min(20, count));

        var cars = [];
        for (var i = 0; i < count; i++) {
            (function(index) {
                var isLoco = (index === 0 || index === count - 1);
                if (count === 20 && (index === 9 || index === 10)) isLoco = true;
                var id = isLoco ? "motrice_" + index : "wagon_" + index;
                cars.push({ getVehicleId: function() { return id; } });
            })(i);
        }
        return { count: count, cars: cars };
    },

    buildPlatforms: function() {
        var platforms = [];
        for (var i = 0; i < FAKE_ROUTE_STOPS.length; i++) {
            platforms.push({
                stationName: FAKE_ROUTE_STOPS[i].name,
                platformId: 900000 + i,
                stationId: 800000 + i
            });
        }
        return platforms;
    },

    buildRoute: function() {
        return {
            name: FAKE_ROUTE_TYPE + " " + FAKE_ROUTE_NUMBER + "||" + FAKE_ROUTE_DESTINATION,
            id: -1,
            color: FAKE_ROUTE_COLOR,
            platforms: this.buildPlatforms()
        };
    },

    buildArrival: function(index, nowMs) {
        const stop = FAKE_ROUTE_STOPS[index];
        const timeMs = nowMs + ((FAKE_ROUTE_START_BUFFER_MIN + stop.offsetMin) * 60000);
        const isTerminating = index === FAKE_ROUTE_STOPS.length - 1;
        const route = this.buildRoute();
        const fakeCars = this.buildFakeCars(FAKE_ROUTE_TYPE, FAKE_ROUTE_TYPE + "_" + FAKE_ROUTE_NUMBER);
        return {
            destination: function() { return FAKE_ROUTE_DESTINATION; },
            departureTime: function() { return timeMs; },
            arrivalTime: function() { return timeMs; },
            arrived: function() { return false; },
            departed: function() { return false; },
            deviation: function() { return 0; },
            realtime: function() { return true; },
            departureIndex: function() { return index; },
            terminating: function() { return isTerminating; },
            route: function() { return route; },
            routeId: function() { return -1; },
            routeName: function() { return route.name; },
            routeNumber: function() { return FAKE_ROUTE_TYPE + "|" + FAKE_ROUTE_NUMBER; },
            routeColor: function() { return FAKE_ROUTE_COLOR; },
            circularState: function() { return null; },
            platform: function() { return { stationName: stop.name, platformId: 900000 + index, stationId: 800000 + index }; },
            platformId: function() { return 900000 + index; },
            platformName: function() { return FAKE_ROUTE_PLATFORM_NAME; },
            carCount: function() { return fakeCars.count; },
            cars: function() { return fakeCars.cars; },
            toString: function() { return "FakeRouteArrival: " + FAKE_ROUTE_DESTINATION + " @ " + index; }
        };
    },

    buildArrivals: function(startIndex) {
        const start = (typeof startIndex === "number" && startIndex >= 0 && startIndex < FAKE_ROUTE_STOPS.length) ? startIndex : 0;
        const nowMs = Timing.currentTimeMillis();
        const self = this;
        var arrivals = [];
        for (var i = start; i < FAKE_ROUTE_STOPS.length; i++) {
            arrivals.push(self.buildArrival(i, nowMs));
        }
        return {
            get: function(i) { return (i >= 0 && i < arrivals.length) ? arrivals[i] : null; },
            forEach: function(fn) { arrivals.forEach(fn); },
            mixedCarLength: function() { return false; },
            platforms: function() { return self.buildPlatforms(); }
        };
    },

    buildTypeArrival: function(index, typeKey, nowMs) {
        const typeInfo = TRAIN_TYPES[typeKey];
        const destination = FAKE_ROUTE_TYPE_DESTINATIONS[index % FAKE_ROUTE_TYPE_DESTINATIONS.length];
        const timeMs = nowMs + ((FAKE_ROUTE_START_BUFFER_MIN + (index * 6)) * 60000);

        const isRer = typeKey === "RER";
        const isTransilien = typeKey === "TRANSILIEN";
        const number = "" + (10000 + index);
        let routeNumber = typeKey + "|" + number;
        if (isRer || isTransilien) {
            const lines = isRer ? FAKE_ROUTE_RER_LINES : FAKE_ROUTE_TRANSILIEN_LINES;
            routeNumber += "|" + lines[index % lines.length];
        }

        const stopCount = Math.min(10, FAKE_ROUTE_STOPS.length);
        const stopStart = (index * 3) % FAKE_ROUTE_STOPS.length;
        var platforms = [];
        for (var s = 0; s < stopCount; s++) {
            var stopName = FAKE_ROUTE_STOPS[(stopStart + s) % FAKE_ROUTE_STOPS.length].name;
            platforms.push({
                stationName: stopName,
                platformId: 700000 + (index * 100) + s,
                stationId: 600000 + (index * 100) + s
            });
        }

        const route = {
            name: typeKey + " " + number + "||" + destination,
            id: -(2000 + index),
            color: FAKE_ROUTE_TYPE_COLORS[index % FAKE_ROUTE_TYPE_COLORS.length],
            platforms: platforms
        };

        const isDelayed = (index % 3) === 0;
        const deviationMs = isDelayed ? (2 + (index % 5)) * 60000 : 0;

        const isBus = typeInfo.bus === true;
        const platformText = isBus ? "" : FAKE_ROUTE_TYPE_PLATFORM_LETTERS[index % FAKE_ROUTE_TYPE_PLATFORM_LETTERS.length];

        const fakeCars = this.buildFakeCars(typeKey, typeKey + "_" + index);

        return {
            destination: function() { return destination; },
            departureTime: function() { return timeMs; },
            arrivalTime: function() { return timeMs; },
            arrived: function() { return false; },
            departed: function() { return false; },
            deviation: function() { return deviationMs; },
            realtime: function() { return true; },
            departureIndex: function() { return index; },
            terminating: function() { return false; },
            route: function() { return route; },
            routeId: function() { return route.id; },
            routeName: function() { return route.name; },
            routeNumber: function() { return routeNumber; },
            routeColor: function() { return route.color; },
            circularState: function() { return null; },
            platform: function() { return platforms[0]; },
            platformId: function() { return platforms[0].platformId; },
            platformName: function() { return platformText; },
            carCount: function() { return fakeCars.count; },
            cars: function() { return fakeCars.cars; },
            toString: function() { return "FakeTypeArrival: " + typeKey + " -> " + destination + " @ " + index; }
        };
    },

    buildAllTypesArrivals: function(startIndex) {
        const typeKeys = Object.keys(TRAIN_TYPES);
        const start = (typeof startIndex === "number" && startIndex >= 0 && startIndex < typeKeys.length) ? startIndex : 0;
        const nowMs = Timing.currentTimeMillis();
        const self = this;
        var arrivals = [];
        for (var i = start; i < typeKeys.length; i++) {
            arrivals.push(self.buildTypeArrival(i, typeKeys[i], nowMs));
        }
        return {
            get: function(i) { return (i >= 0 && i < arrivals.length) ? arrivals[i] : null; },
            forEach: function(fn) { arrivals.forEach(fn); },
            mixedCarLength: function() { return false; },
            platforms: function() { return []; }
        };
    },

    resolveArrivals: function(ctx, state, pids) {
        const debugOn = ctx.debugModeEnabled();
        const fakeTypesVal = debugOn ? DebugUtils.getCategoryValue(state, "faketypes") : undefined;
        const fakeRouteVal = debugOn ? DebugUtils.getCategoryValue(state, "fakeroute") : undefined;
        const arrivals = fakeTypesVal !== undefined ? this.buildAllTypesArrivals(parseInt(fakeTypesVal) || 0)
            : fakeRouteVal !== undefined ? this.buildArrivals(parseInt(fakeRouteVal) || 0)
            : pids.arrivals();
        TrainUtils.learnRepeatIntervals(state, arrivals);
        return arrivals;
    }
};

if (typeof module !== "undefined" && module.exports) {
    module.exports = FakeRoute;
}
