const DebugUtils = {
    Color: {
        RESET: "§r",
        BLACK: "§0", DARK_BLUE: "§1", DARK_GREEN: "§2", DARK_AQUA: "§3",
        DARK_RED: "§4", PURPLE: "§5", GOLD: "§6", GRAY: "§7",
        DARK_GRAY: "§8", BLUE: "§9", GREEN: "§a", AQUA: "§b",
        RED: "§c", PINK: "§d", YELLOW: "§e", WHITE: "§f"
    },

    _autoColor(value) {
        if (value === true) return this.Color.GREEN;
        if (value === false) return this.Color.RED;
        if (value === null || value === undefined) return this.Color.GRAY;
        if (value === "") return this.Color.GRAY;
        if (typeof value === "number") return this.Color.AQUA;
        return this.Color.WHITE;
    },

    colorize(value, color) {
        const c = color || this._autoColor(value);
        return c + value + this.Color.RESET;
    },

    _parseCategories(state) {
        const raw = ConfigHandler.get(state, "debugCategories");
        if (state._debugCatCache && state._debugCatCache.raw === raw) {
            return state._debugCatCache;
        }
        let list;
        const values = {};
        const rawLower = raw ? raw.trim().toLowerCase() : "";
        if (rawLower === "" || rawLower === "true" || rawLower === "help") {
            list = [];
        } else if (rawLower === "all") {
            list = null;
        } else {
            list = raw.split(/[,;]/).map(function(tok) {
                const trimmed = tok.trim();
                const eq = trimmed.indexOf("=");
                const name = (eq !== -1 ? trimmed.substring(0, eq) : trimmed).trim().toLowerCase();
                values[name] = eq !== -1 ? trimmed.substring(eq + 1).trim() : "true";
                return name;
            });
        }
        const parsed = { raw: raw, list: list, values: values };
        state._debugCatCache = parsed;
        return parsed;
    },

    _enabledCategories(state) {
        return this._parseCategories(state).list;
    },

    getCategoryValue(state, category) {
        return this._parseCategories(state).values[category.toLowerCase()];
    },

    isEnabled(ctx, state, category) {
        if (!ctx.debugModeEnabled()) return false;
        const enabled = this._enabledCategories(state);
        if (enabled === null) return true;
        return enabled.indexOf(category.toLowerCase()) !== -1;
    },

    report(ctx, state, category, key, value, color) {
        if (!this.isEnabled(ctx, state, category)) return;
        ctx.setDebugInfo(category + ":" + key, this.colorize(value, color));
    },

    hint(ctx, state, categories) {
        if (!ctx.debugModeEnabled()) return;

        const raw = ConfigHandler.get(state, "debugCategories");
        const rawLower = raw ? raw.trim().toLowerCase() : "";
        if (rawLower === "help" || rawLower === "true") {
            ctx.setDebugInfo("debug", this.colorize(
                "categories: " + (categories || []).join(", ") + " (also: all, help)",
                this.Color.GOLD));
            return;
        }

        const enabled = this._enabledCategories(state);
        if (enabled === null || enabled.length !== 0) return;
        ctx.setDebugInfo("debug", this.colorize("set -dbg all (or -dbg <category>, or -dbg help) to see values", this.Color.GRAY));
    }
};

if (typeof module !== "undefined" && module.exports) {
    module.exports = DebugUtils;
}
