include(Resources.id("aca:modules/sncf_stations_db.js"));
include(Resources.id("aca:modules/sncf_custom_stations.js"));
include(Resources.id("aca:modules/sncf_names.js"));

const SncfColors = {
    OrangeFrame: 0xFF9F17,
    DepartureYellow: 0xFEF103,
    DepartureWhite: 0xFFFFFF,
    Background: 0x000000,
    OnTimeWhite: 0xFFFFFF,
    DelayYellow: 0xFEF103,
    ClockColonDim: 0x003A79,
    ImportantBand: 0xFFC052,
    ImportantText: 0x003A79,

    LiveBackground: 0x0D2F5F,
    LiveHeader: 0x044179,

    LiveArrivalBackground: 0x0A4727,
    LiveArrivalHeader: 0x035A24,

    LiveDelayPillBackground: 0xFF9F17,
    LiveDelayPillIcon: 0x21212C
};

const SncfTheme = {

    init(state) {
        if (!state.sncfCache) {
            state.sncfCache = {
                strings: {},
                lines: {},
                formatted: {}
            };
        }
    },

    drawFrame(ctx, pids, showLogo, opts) {
        const frameUV = RenderUtils.getUV(10, 0, 5, 5);
        const tex = "sncf:images/colors.png";
        const size = 9.8;
        const z = (opts && opts.zOrder != null) ? opts.zOrder : null;
        const oy = (opts && opts.offsetY != null) ? opts.offsetY : 0;
        function withZ(builder) { return z !== null ? builder.zOrder(z) : builder; }
        function withZLogo(builder) { return z !== null ? builder.zOrder(z + 1) : builder; }

        withZ(Texture.create("Frame-Top")
            .texture(tex)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(pids.width + (size * 2), size)
            .pos(-size, -size + oy))
            .draw(ctx);

        withZ(Texture.create("Frame-Bottom")
            .texture(tex)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(pids.width + (size * 2), size)
            .pos(-size, pids.height + oy))
            .draw(ctx);

        withZ(Texture.create("Frame-Left")
            .texture(tex)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(size, pids.height + (size * 2))
            .pos(-size, -size + oy))
            .draw(ctx);

        withZ(Texture.create("Frame-Right")
            .texture(tex)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(size, pids.height + (size * 2))
            .pos(pids.width, -size + oy))
            .draw(ctx);

        if (showLogo) {
            withZLogo(Texture.create("Sncf Logo")
                .texture("sncf:images/labels/sncf.png")
                .size(6.5, 4)
                .pos(pids.width + (size * 0.2), pids.height + (size * 0.4) + oy))
                .draw(ctx);
        }
    },

    drawClock(ctx, pids, timestamp, opts) {
        const z = (opts && opts.zOrder != null) ? opts.zOrder : null;
        function withZ(builder) { return z !== null ? builder.zOrder(z) : builder; }

        if (opts && opts.drawBox) {
            const boxZ = opts.boxZOrder != null ? opts.boxZOrder : (z !== null ? z - 1 : null);
            const boxBuilder = Texture.create("Hour Box")
                .texture("sncf:images/hour_box.png")
                .pos(pids.width - 20.3, pids.height - 5.6)
                .size(18.7, 5.6);
            if (boxZ !== null) boxBuilder.zOrder(boxZ);
            boxBuilder.draw(ctx);
        }

        const scale = 0.4;
        const hhmm = RenderUtils.formatTime(timestamp).split(":");
        const hh = hhmm[0];
        const mm = hhmm[1];
        const y = pids.height - 3.75;
        const endX = pids.width - 8.25;
        const startX = endX - RenderUtils.getStringWidth(hh + ":" + mm, scale);
        const wH = RenderUtils.getStringWidth(hh, scale);
        const wSep = RenderUtils.getStringWidth(":", scale);
        const gap = scale;

        withZ(Text.create("Clock Hours")
            .text(hh)
            .color(SncfColors.OnTimeWhite)
            .pos(startX, y)
            .scale(scale)
            .leftAlign())
            .draw(ctx);

        withZ(Text.create("Clock Separator")
            .text(":")
            .color((Math.floor(timestamp / 1000) % 2 === 0) ? SncfColors.OnTimeWhite : SncfColors.ClockColonDim)
            .pos(startX + wH + gap + 0.1, y)
            .scale(scale)
            .leftAlign())
            .draw(ctx);

        withZ(Text.create("Clock Minutes")
            .text(mm)
            .color(SncfColors.OnTimeWhite)
            .pos(startX + wH + gap + wSep + gap, y)
            .scale(scale)
            .leftAlign())
            .draw(ctx);

        withZ(Text.create("Clock Fixed Seconds")
            .text(RenderUtils.getSeconds(timestamp))
            .color(SncfColors.OrangeFrame)
            .pos(pids.width - 3.75, pids.height - 3.1)
            .scale(0.28)
            .rightAlign())
            .draw(ctx);
    },

    shortenNames(state, name) {
        if (name === undefined) {
            name = state;
            state = null;
        }
        name = String(name);

        let cache = null;
        if (state) {
            if (!state.sncfCache) this.init(state);
            cache = state.sncfCache.strings;
            if (cache[name]) return cache[name];
        }

        const result = SncfNames.format(name);
        if (cache) cache[name] = result;
        return result;
    },

    fitName(state, name, maxLen, measure) {
        name = String(name);
        let cache = null;
        if (state && !measure) {
            if (!state.sncfCache) this.init(state);
            if (!state.sncfCache.fitted) state.sncfCache.fitted = {};
            cache = state.sncfCache.fitted;
            const key = name + "\u0000" + maxLen;
            if (cache[key]) return cache[key];
            const result = SncfNames.fit(name, maxLen);
            cache[key] = result;
            return result;
        }
        return SncfNames.fit(name, maxLen, measure);
    },

    convertNameToLines(state, name) {
        if (name === undefined) {
            name = state;
            state = null;
        }
        name = String(name);

        var cache = null;
        if (state) {
            if (!state.sncfCache) this.init(state);
            cache = state.sncfCache.lines;
            if (cache[name]) return cache[name];
        }

        var maxLength = 12;
        var lines = [];

        var segments = name.split("-Via-");
        var mainPart = segments[0] || "";
        var viaParts = [];
        for (var i = 1; i < segments.length; i++) {
            viaParts.push("Via " + segments[i]);
        }

        function processSegment(segment, isVia) {
            var segStr = String(segment).replace(/\s+/g, " ").trim();
            if (!segStr) return;

            var words = [];
            var separators = [];
            var currentWord = "";

            for (var k = 0; k < segStr.length; k++) {
                var ch = segStr.charAt(k);
                if (ch === " " || ch === "-") {
                    words.push(currentWord);
                    separators.push(ch);
                    currentWord = "";
                } else {
                    currentWord += ch;
                }
            }
            words.push(currentWord);

            var firstWord = words[0] || "";
            var currentLine = firstWord.length > maxLength ? firstWord.substring(0, maxLength - 3) + "..." : firstWord;

            for (var j = 1; j < words.length; j++) {
                var nextWord = words[j];
                var sep = separators[j - 1] || " ";
                var potential = currentLine + sep + nextWord;

                if (potential.length <= maxLength) {
                    currentLine = potential;
                } else {
                    if (currentLine) {
                        lines.push({
                            text: currentLine,
                            margin: isVia && currentLine.indexOf("Via") === 0 ? 0.7 : 0.5
                        });
                    }
                    currentLine = nextWord.length > maxLength ? nextWord.substring(0, maxLength - 3) + "..." : nextWord;
                }
            }

            if (currentLine) {
                lines.push({
                    text: currentLine,
                    margin: isVia && currentLine.indexOf("Via") === 0 ? 0.7 : 0.5
                });
            }
        }

        processSegment(mainPart, false);
        for (var j = 0; j < viaParts.length; j++) {
            processSegment(viaParts[j], true);
        }

        if (cache) cache[name] = lines;
        return lines;
    },

    formatText(state, sentences) {
        if (!sentences || sentences.length === 0) return "";

        if (!state.sncfCache) this.init(state);
        const key = sentences.join("||");
        const cache = state.sncfCache.formatted;
        if (cache[key]) return cache[key];

        function capitalizeFirst(s) {
            return s.length === 0 ? s : s.charAt(0).toUpperCase() + s.substring(1);
        }
        function processWord(word, isFirst) {
            const m = word.match(/^(.+?)([,.?!:;]*)$/);
            if (!m) return word;
            let core = m[1].toLowerCase();
            if (isFirst) core = capitalizeFirst(core);
            return core + m[2];
        }

        const out = [];
        sentences.forEach(function(raw) {
            let s = raw.trim();
            if (s === "") return;

            const words = s.split(/\s+/);
            const processed = [];
            words.forEach(function(word, k) {
                if (word) processed.push(processWord(word, k === 0));
            });
            s = processed.join(" ").replace(/ ([?!:;])/g, "$1");
            if (!/[.!?]$/.test(s)) s += ".";
            out.push(s);
        });

        const res = out.join(" ");
        cache[key] = res;
        return res;
    },

    displayStationName(name) {
        return ("" + name).replace(/-Via-/gi, " Via ").trim();
    }

};
