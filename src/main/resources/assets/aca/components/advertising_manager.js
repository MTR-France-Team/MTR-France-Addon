const AdvertisingManager = {
    adsByType: {
        pids_1a: [
            { src: "sncf:images/vigipirate_baggage.png", duration: 15000 },
            { src: "sncf:images/vigipirate_alert_3117.png", duration: 15000 }
        ],
        default: [
            { src: "sncf:images/ads/regarde.png", duration: 15000 }
        ]
    },

    maybeShow: function(ctx, state, pids, opts) {
        const departures = pids.arrivals();
        if (!this.shouldShowAds(departures)) return false;

        const adsList = this.adsByType[pids.type] || this.adsByType.default;
        if (!adsList || adsList.length === 0) return false;

        const o = opts || {};
        const x = o.x != null ? o.x : 0;
        const y = o.y != null ? o.y : 0;
        const width = o.width != null ? o.width : pids.width;
        const height = o.height != null ? o.height : pids.height;

        this.draw(ctx, state, pids, x, y, width, height, adsList);
        return true;
    },
    shouldShowAds: function(departures) {
        if (!departures || departures.length === 0) return true;

        let firstDep = departures.get(0);
        if (firstDep == null) return true;

        return !this.isDepartureImminent(firstDep);
    },

    isDepartureImminent: function(departure) {
        if (departure == null) return false;

        let deviationMs = 0;
        if (typeof departure.deviation === "function") {
            const d = departure.deviation();
            if (d != null && !isNaN(d) && d > 0) deviationMs = d;
        }

        const scheduledDepTime = departure.departureTime() - deviationMs;
        const now = Timing.currentTimeMillis();

        return (scheduledDepTime - now) <= 1200000;
    },

    draw: function(ctx, state, pids, x, y, width, height, adsList) {
        const list = adsList || this.adsByType[pids.type] || this.adsByType.default;
        if (!list || list.length === 0) return;

        if (!state.adState) {
            state.adState = {
                currentIndex: 0,
                lastChangeTime: Timing.currentTimeMillis()
            };
        }

        let now = Timing.currentTimeMillis();
        let currentAd = list[state.adState.currentIndex % list.length];

        if (now - state.adState.lastChangeTime > currentAd.duration) {
            state.adState.currentIndex = (state.adState.currentIndex + 1) % list.length;
            state.adState.lastChangeTime = now;
            currentAd = list[state.adState.currentIndex];
        }

        Texture.create("ad_image_" + state.adState.currentIndex)
            .texture(currentAd.src)
            .pos(x, y)
            .size(width, height)
            .draw(ctx);
    }
};
