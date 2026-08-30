const CUSTOM_MESSAGE_SLOT_COUNT = 4;

const ConfigHandler = {

    _schema: {
        "hideFrame": { type: Boolean, default: false, alias: "hf" },
        "logo": { type: Boolean, default: false, alias: "showlogo" },
        "showMessage": { type: Boolean, default: false, alias: "sm" },
        "startIndex": { type: Number, default: 0, alias: "si" },
        "arrivals": { type: Boolean, default: false, alias: "as" },
        "pageCount": { type: Number, default: 1, alias: ["p", "page"] },
        "departureFormat": { type: String, default: "0-10", alias: "df" },
        "compositionRowCount": { type: Number, default: 0, alias: ["comprow", "cr"] },
        "exactDelay": { type: Boolean, default: false, alias: ["rd", "roundDelay", "ed"] },
        "composition": { type: String, default: "auto", alias: "comp" },
        "markers": { type: String, default: "", alias: ["reperes", "rep"] },
        "viewerPos": { type: String, default: null, alias: ["herePos", "vPos", "hPos", "here"] },
        "trainOffset": { type: String, default: null, alias: ["trainPos", "tPos", "tOffset"] },
        "locoPos": { type: String, default: null, alias: "lpos" },
        "locoType": { type: String, default: null, alias: "lt" },
        "platformLength": { type: String, default: null, alias: "plen" },
        "trainLength": { type: String, default: null, alias: "tLen" },
        "scrollSpeed": { type: Number, default: 12, alias: "sspd" },
        "marqueeSpeed": { type: Number, default: 8, alias: "mspd" },
        "carNumbering": { type: String, default: "normal", alias: ["cn", "carNumber"] },
        "debugCategories": { type: String, default: null, alias: "dbg" },
        "forceNext": { type: Boolean, default: false },
        "forceDelay": { type: Number, default: 0 },
        "important": { type: Boolean, default: false, alias: "imp" },
        "reservationOnly": { type: Boolean, default: false, alias: ["mainline", "gl", "grandelignes"] },
        "hideTitle": { type: Boolean, default: false, alias: "ht" },
        "hideColumnHeaders": { type: Boolean, default: false, alias: "hch" },
        "locale": { type: String, default: "fr", alias: ["lang", "language", "langage", "loc"] }
    },

    _lookupMap: null,

    _initLookup() {
        if (this._lookupMap) return;
        this._lookupMap = {};
        const map = this._lookupMap;
        const schema = this._schema;
        Object.keys(schema).forEach(function(key) {
            map[key] = key;
            const entry = schema[key];
            const alias = entry.alias;
            if (alias) {
                if (Array.isArray(alias)) {
                    alias.forEach(function(a) { map[a] = key; });
                } else {
                    map[alias] = key;
                }
            }
        });
    },

    init(state, defaults) {
        this._initLookup();
        state.config = {
            values: {},
            lastRaw: null,
            textSlots: [],
            defaults: defaults || {}
        };

        this._resetToDefaults(state);
    },

    _resetToDefaults(state) {
        const schema = this._schema;
        const values = state.config.values;
        const overrides = state.config.defaults;
        Object.keys(schema).forEach(function(key) {
            values[key] = overrides.hasOwnProperty(key) ? overrides[key] : schema[key].default;
        });
    },

    sync(state, pids) {
        if (!state.config) this.init(state);

        var rawSlots = [];
        for (var r = 0; r < CUSTOM_MESSAGE_SLOT_COUNT; r++) {
            var msg = pids.getCustomMessage(r);
            rawSlots.push(msg === null || msg === undefined ? "" : (msg + ""));
        }

        var signature = rawSlots.join("\u0001");
        if (signature === state.config.lastRaw) return;
        state.config.lastRaw = signature;

        this._resetToDefaults(state);
        const values = state.config.values;

        var textSlots = [];
        for (var s = 0; s < rawSlots.length; s++) {
            textSlots.push(this._parseSlot(values, rawSlots[s]));
        }
        state.config.textSlots = textSlots;
    },

    get(state, key) {
        return state.config && state.config.values ? state.config.values[key] : this._schema[key].default;
    },

    getTextSlots(state) {
        return (state.config && state.config.textSlots) ? state.config.textSlots : [];
    },

    _parseSlot(values, input) {
        if (!input || input.trim() === "") return "";

        var tokens = input.match(/(?:[^\s"]+|"[^"]*")+/g) || [];
        var leftover = [];

        for (var i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            if (token.indexOf("-") !== 0) { leftover.push(token); continue; }

            var rawKey = token.replace(/^-+/, "");
            var eqParts = rawKey.indexOf("=") !== -1 ? rawKey.split("=", 2) : null;
            if (eqParts) rawKey = eqParts[0];

            var canonicalKey = this._lookupMap[rawKey];
            if (!canonicalKey) { leftover.push(token); continue; }

            var schemaEntry = this._schema[canonicalKey];

            var val = "true";
            var consumedNext = false;
            if (eqParts) {
                val = eqParts[1].replace(/"/g, "");
            } else if (i + 1 < tokens.length && tokens[i + 1].indexOf("-") !== 0) {
                var nextRaw = tokens[i + 1].replace(/"/g, "");
                var isBoolLiteral = /^(true|false|0|1)$/i.test(nextRaw);
                if (schemaEntry.type !== Boolean || isBoolLiteral) {
                    val = nextRaw;
                    consumedNext = true;
                }
            }

            values[canonicalKey] = this._coerceType(val, schemaEntry.type);
            if (consumedNext) i++;
        }

        return leftover.join(" ");
    },

    _coerceType(val, type) {
        if (type === Boolean) return val !== "false" && val !== "0";
        if (type === Number) {
            let num = Number(val);
            return isNaN(num) ? 0 : num;
        }
        return String(val);
    }

};
