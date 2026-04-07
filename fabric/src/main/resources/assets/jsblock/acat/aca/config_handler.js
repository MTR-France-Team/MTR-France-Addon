const ConfigHandler = {

    _schema: {
        "frame": { type: Boolean, default: true },
        "logo": { type: Boolean, default: false },
        "page": { type: Number, default: 1, alias: "p" },
        "showDelay": { type: Boolean, default: true, alias: "sd" },
        "showMessage": { type: Boolean, default: false, alias: "sm" },
        "startIndex": { type: Number, default: 0, alias: "si" },
        "arrivals": { type: Boolean, default: false, alias: "as" },
        "color": { type: String, default: null, alias: "c" },

        "theme": { type: String, default: null, alias: "t" },

        // Retard
        "roundDelay": { type: Boolean, default: true, alias: "rd" },

        "arrows": { type: String, default: "both", alias: "ar"}
    },

    _lookupMap: null,

    _initLookup() {
        if (this._lookupMap) return;
        this._lookupMap = {};
        for (let key in this._schema) {
            if (this._schema.hasOwnProperty(key)) {
                this._lookupMap[key] = key;
                if (this._schema[key].alias) this._lookupMap[this._schema[key].alias] = key;
            }
        }
    },

    init(state) {
        this._initLookup();
        state.config = {
            values: {},
            lastRaw: null
        };

        for (let key in this._schema) {
            state.config.values[key] = this._schema[key].default;
        }
    },

    sync(state, pids) {
        if (!state.config) this.init(state);

        let raw = pids.getCustomMessage(0);
        if (raw === null || raw === undefined) raw = "";
        else raw = raw + "";

        if (raw === state.config.lastRaw) return;

        state.config.lastRaw = raw;
        this._parse(state, raw);
    },

    get(state, key) {
        return state.config && state.config.values ? state.config.values[key] : this._schema[key].default;
    },

    _parse(state, input) {
        for (let key in this._schema) {
            state.config.values[key] = this._schema[key].default;
        }

        if (!input || input.trim() === "") return;

        let tokens = input.match(/(?:[^\s"]+|"[^"]*")+/g) || [];
        for (let i = 0; i < tokens.length; i++) {
            let token = tokens[i];
            if (token.indexOf("-") !== 0) continue;

            let rawKey = token.replace(/^-+/, "");
            let val = "true";

            if (i + 1 < tokens.length && tokens[i + 1].indexOf("-") !== 0) {
                val = tokens[i + 1].replace(/"/g, "");
                i++;
            } else if (rawKey.includes("=")) {
                let parts = rawKey.split("=", 2);
                rawKey = parts[0];
                val = parts[1].replace(/"/g, "");
            }

            let canonicalKey = this._lookupMap[rawKey];
            if (canonicalKey) {
                state.config.values[canonicalKey] = this._coerceType(val, this._schema[canonicalKey].type);
            }
        }
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