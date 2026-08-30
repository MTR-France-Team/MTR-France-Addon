const CHAR_WIDTH_TABLE = (function () {
    const t = {};
    function fill(str, w) {
        str.split("").forEach(function(ch) { t[ch.charCodeAt(0)] = w; });
    }
    fill("i.,;:!'|", 1);
    fill("l`•", 2);
    fill("tI\"()*¨{}[] ", 3);
    fill("fk<>°²", 4);
    fill("@~", 6);
    fill("¤", 7);
    fill("⚠", 9);
    return t;
})();
const CHAR_WIDTH_DEFAULT = 5;

function isCjkCode(code) {
    return (code >= 0x3040 && code <= 0x309F)
        || (code >= 0x30A0 && code <= 0x30FF)
        || (code >= 0x4E00 && code <= 0x9FFF);
}

function rect(ctx, x, y, w, h, color, z) {
    Texture.create().texture("aca:images/white.png").pos(x, y).size(w, h).color(color).zOrder(z).draw(ctx);
}

function img(ctx, tex, x, y, w, h, color, z, uv) {
    const b = Texture.create().texture(tex).pos(x, y).size(w, h).zOrder(z);
    if (color != null) b.color(color);
    if (uv) b.uv(uv[0], uv[1], uv[2], uv[3]);
    b.draw(ctx);
}

function txt(ctx, str, x, y, scale, color, align, z, opts) {
    const b = Text.create().text(str).pos(x, y).scale(scale).zOrder(z);
    if (color != null) b.color(color);
    if (align === "c") b.centerAlign();
    else if (align === "r") b.rightAlign();
    else b.leftAlign();
    if (opts) {
        if (opts.bold) b.bold();
        if (opts.italic) b.italic();
        if (opts.size) b.size(opts.size[0], opts.size[1]);
        if (opts.scaleXY) b.scaleXY();
        if (opts.wrapText) b.wrapText();
    }
    b.draw(ctx);
}

const RenderUtils = {

    getUV(x, y, w, h, total) {
        const t = total || 30;
        return [x / t, y / t, (x + w) / t, (y + h) / t];
    },

    getCharWidth(charOrCode, onePx) {
        if (onePx === undefined || onePx === null) onePx = 0.5;
        const code = typeof charOrCode === "number" ? charOrCode : charOrCode.charCodeAt(0);
        if (isCjkCode(code)) return 7.75 * onePx;
        const w = CHAR_WIDTH_TABLE[code];
        return (w !== undefined ? w : CHAR_WIDTH_DEFAULT) * onePx;
    },

    getCharHeight(onePx) {
        return 7 * (onePx || 0.5);
    },

    getStringWidth(str, onePx) {
        if (!str) return 0;
        if (onePx === undefined || onePx === null) onePx = 0.5;
        const len = str.length;
        if (len === 0) return 0;

        const table = CHAR_WIDTH_TABLE;
        const dflt = CHAR_WIDTH_DEFAULT;

        let total = 0;
        str.split("").forEach(function(ch, i) {
            const code = ch.charCodeAt(0);
            let w;
            if (isCjkCode(code)) {
                w = 7.75;
            } else {
                w = table[code];
                if (w === undefined) w = dflt;
            }
            total += w * onePx;
            if (i < len - 1) total += onePx;
        });
        return total;
    },

    getBoldStringWidth(str, onePx) {
        if (!str) return 0;
        if (onePx === undefined || onePx === null) onePx = 0.5;
        return this.getStringWidth(str, onePx) + (str.length * onePx);
    },

    getCachedStringWidth(cache, keyPrefix, str, onePx) {
        const keyField = "_" + keyPrefix + "WidthKey";
        const valueField = "_" + keyPrefix + "WidthValue";
        const key = str + "|" + onePx;
        if (cache[keyField] !== key) {
            cache[keyField] = key;
            cache[valueField] = this.getStringWidth(str, onePx);
        }
        return cache[valueField];
    },

    stripAccents(str) {
        const decomposed = ("" + str).normalize("NFD");
        var out = "";
        for (var i = 0; i < decomposed.length; i++) {
            var code = decomposed.charCodeAt(i);
            if (code < 0x0300 || code > 0x036F) out += decomposed.charAt(i);
        }
        return out;
    },

    getContrastColor(bgColor) {
        const color = typeof bgColor === "string" ? parseInt(bgColor, 16) : bgColor;
        const r = (color >> 16) & 0xFF;
        const g = (color >> 8) & 0xFF;
        const b = color & 0xFF;
        const luminance = ((r * 299) + (g * 587) + (b * 114)) / 1000;
        return (luminance >= 145) ? 0x000000 : 0xFFFFFF;
    },

    formatTime(timestamp, separator) {
        const sep = separator || ":";
        const date = new Date(timestamp);
        const h = date.getHours().toString().padStart(2, "0");
        const m = date.getMinutes().toString().padStart(2, "0");
        return `${h}${sep}${m}`;
    },

    getSeconds(timestamp) {
        return new Date(timestamp).getSeconds().toString().padStart(2, "0");
    },

    formatTrainDelay(delaySec, byFiveMinutes, shorted) {
        const minutes = byFiveMinutes
            ? Math.ceil((delaySec / 60) / 5) * 5
            : Math.ceil(delaySec / 60);
        if (minutes < 60) {
            return `${minutes.toString().padStart(2, "0")}${shorted ? "mn" : " min"}`;
        }
        const hours = Math.floor(minutes / 60);
        const rest = minutes % 60;
        return `${hours}h${rest.toString().padStart(2, "0")}`;
    },

    formatDelayAmount(delaySeconds) {
        const totalMinutes = Math.floor(delaySeconds / 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        if (hours > 0) {
            return "+" + hours + "h" + (minutes < 10 ? "0" + minutes : minutes);
        }
        return "+" + minutes + " min.";
    },

    blendColor(colorA, colorB, ratioA) {
        const ratioB = 1 - ratioA;
        const r = Math.round(((colorA >> 16) & 0xFF) * ratioA + ((colorB >> 16) & 0xFF) * ratioB);
        const g = Math.round(((colorA >> 8) & 0xFF) * ratioA + ((colorB >> 8) & 0xFF) * ratioB);
        const b = Math.round((colorA & 0xFF) * ratioA + (colorB & 0xFF) * ratioB);
        return (r << 16) | (g << 8) | b;
    }
};

const Viewport = {

    horizontal(pids, options) {
        const opts = options || {};
        const count = opts.count || 2;
        const gap = opts.gap || 0;
        const mx = opts.marginX || 0;
        const my = opts.marginY || 0;

        const usableW = pids.width - (mx * 2) - (gap * (count - 1));
        const panelW = usableW / count;
        const panelH = pids.height - (my * 2);

        const out = new Array(count);
        for (let i = 0; i < count; i++) {
            out[i] = {
                index: i,
                x: mx + i * (panelW + gap),
                y: my,
                width: panelW,
                height: panelH
            };
        }
        return out;
    },

    vertical(pids, options) {
        const opts = options || {};
        const count = opts.count || 2;
        const gap = opts.gap || 0;
        const mx = opts.marginX || 0;
        const my = opts.marginY || 0;

        const usableH = pids.height - (my * 2) - (gap * (count - 1));
        const panelH = usableH / count;
        const panelW = pids.width - (mx * 2);

        const out = new Array(count);
        for (let i = 0; i < count; i++) {
            out[i] = {
                index: i,
                x: mx,
                y: my + i * (panelH + gap),
                width: panelW,
                height: panelH
            };
        }
        return out;
    },

    drawBezelV(ctx, leftPanel, rightPanel, options) {
        const opts = options || {};
        const x = leftPanel.x + leftPanel.width;
        const y = leftPanel.y;
        const w = rightPanel.x - x;
        const h = leftPanel.height;
        if (w <= 0) return;

        const tex = Texture.create("Viewport-Bezel-V")
            .pos(x, y)
            .size(w, h);
        if (opts.texture) tex.texture(opts.texture);
        else tex.texture("aca:images/white.png");
        if (opts.color !== undefined) tex.color(opts.color);
        if (opts.uv) tex.uv(opts.uv[0], opts.uv[1], opts.uv[2], opts.uv[3]);
        tex.draw(ctx);
    },

    suggestHorizontalCount(pids, thresholds) {
        const ratio = pids.width / pids.height;
        for (let i = 0; i < thresholds.length; i++) {
            if (ratio < thresholds[i]) return i + 1;
        }
        return thresholds.length + 1;
    }
};
