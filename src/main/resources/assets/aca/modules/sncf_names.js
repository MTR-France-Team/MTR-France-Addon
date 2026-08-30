
const SncfNames = (function () {

    const KEEP_UPPER = {
        "TGV": 1, "CDG": 1, "RER": 1, "TER": 1, "SNCF": 1, "N-D": 1
    };

    const PARTICLES = {
        "de":   { lower: 1, hyphRate: 0.64 },
        "du":   { lower: 1, hyphRate: 0.75 },
        "des":  { lower: 1, hyphRate: 0.82 },
        "la":   { lower: 1, hyphRate: 0.96 },
        "le":   { lower: 1, hyphRate: 0.85 },
        "les":  { lower: 1, hyphRate: 0.91 },
        "en":   { lower: 1, hyphRate: 0.92 },
        "sur":  { lower: 1, hyphRate: 0.94 },
        "sous": { lower: 1, hyphRate: 0.80 },
        "lès":  { lower: 1, hyphRate: 0.95 },
        "aux":  { lower: 1, hyphRate: 1.00 },
        "près": { lower: 1, hyphRate: 1.00 },
        "chez": { lower: 1, hyphRate: 0.80 },
        "et":   { lower: 1, hyphRate: 0.50 },
        "à":    { lower: 1, hyphRate: 0.50 }
    };
    const HYPH_THRESHOLD = 0.6;

    const NO_TRAILING = {
        "de": 1, "du": 1, "des": 1, "la": 1, "le": 1, "les": 1,
        "en": 1, "et": 1, "au": 1, "aux": 1, "à": 1,
        "sur": 1, "sous": 1, "lès": 1, "chez": 1, "près": 1,
        "s/": 1, "ss/": 1, "st": 1, "ste": 1, "n-d": 1
    };

    const LEVEL_LEXICAL_WORDS = [
        ["Charles de Gaulle", "CDG"],
        ["Notre-Dame", "N-D"],
        ["Rive Gauche", "R.G."],
        ["Rive Droite", "R.D."],
        ["Château", "Chât."],
        ["Aéroport", "Aérop."],
        ["Hôpital", "Hôp."],
        ["Université", "Univ."],
        ["Boulevard", "Bd"],
        ["Avenue", "Av."],
        ["Faubourg", "Fbg"],
        ["Général", "Gal"],
        ["Maréchal", "Mal"],
        ["Président", "Pdt"],
        ["Grande", "Gde"],
        ["Grand", "Gd"],
        ["Petite", "Pte"],
        ["Petit", "Pt"],
        ["Vieux", "Vx"]
    ];
    const LEVEL_LEXICAL = LEVEL_LEXICAL_WORDS.map(function (pair) {
        const word = pair[0];
        const rep = pair[1];
        const core = word.replace(/[\s-]+/g, "[\\s-]");
        return {
            regex: new RegExp("(^|[\\s\\-'(])" + core + "(?=$|[\\s\\-')])", "gi"),
            rep2: "$1" + rep
        };
    });

    const LINK_SUR  = /([\s-])sur[\s-]/gi;
    const LINK_SOUS = /([\s-])sous[\s-]/gi;

    const SPECIAL_CASES = {
        "AEROPORTCHARLESDEGAULLE": "Aéroport CDG"
    };

    function getDB() {
        return (typeof SncfStationsDB !== "undefined") ? SncfStationsDB : null;
    }
    function getCustomDB() {
        return (typeof SncfCustomStations !== "undefined") ? SncfCustomStations : null;
    }
    function lookupKey(key) {
        const custom = getCustomDB();
        if (custom && custom[key]) return custom[key];
        const db = getDB();
        if (db && db[key]) return db[key];
        return null;
    }

    function searchKey(s) {
        return String(s)
            .toUpperCase()
            .replace(/[ÀÂÄ]/g, "A").replace(/[ÉÈÊË]/g, "E")
            .replace(/[ÎÏ]/g, "I").replace(/[ÔÖ]/g, "O")
            .replace(/[ÙÛÜ]/g, "U").replace(/Ç/g, "C")
            .replace(/[^A-Z0-9]/g, "");
    }

    function tokenize(segment) {
        const raw = segment.trim().split(/[\s\-_]+/).filter(function (t) { return t.length > 0; });
        return raw;
    }

    function isNumericToken(word) {
        return /^[A-Za-z0-9]{1,4}$/.test(word) && /\d/.test(word);
    }

    function recaseWord(word, isFirst) {
        if (word.length === 0) return word;

        const pm = word.match(/^([^A-Za-z0-9À-ſ]+)(.*)$/);
        if (pm && pm[2].length > 0) return pm[1] + recaseWord(pm[2], isFirst);

        const lowerAll = word.toLowerCase();
        if (lowerAll === "via") return "Via";

        const upper = word.toUpperCase();
        if (KEEP_UPPER[upper] === 1) return upper;
        if (isNumericToken(word)) return upper;

        const m = word.match(/^([LlDd])'(.*)$/);
        if (m) {
            const prefix = isFirst ? m[1].toUpperCase() : m[1].toLowerCase();
            return prefix + "'" + recaseWord(m[2], true);
        }

        const lower = word.toLowerCase();
        if (!isFirst && PARTICLES[lower]) return lower;

        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }

    const ALWAYS_HYPHEN_PREFIX = { "saint": 1, "sainte": 1, "st": 1, "ste": 1, "via": 1 };

    function buildFromTokens(tokens) {
        if (tokens.length === 0) return "";

        const words = [];
        for (var i0 = 0; i0 < tokens.length; i0++) {
            words.push(recaseWord(tokens[i0], i0 === 0));
        }

        let out = words[0];
        for (var i = 1; i < words.length; i++) {
            var prev = words[i - 1];
            var cur = words[i];
            var nextNext = (i + 1 < words.length) ? words[i + 1] : null;

            var sep;
            var prevLower = prev.toLowerCase();
            var curLower = cur.toLowerCase();

            if (ALWAYS_HYPHEN_PREFIX[prevLower] === 1 || curLower === "via") {
                sep = "-";
            } else if (PARTICLES[curLower] && nextNext &&
                       PARTICLES[curLower].hyphRate >= HYPH_THRESHOLD) {
                sep = "-";
            } else if (PARTICLES[prevLower] && PARTICLES[prevLower].hyphRate >= HYPH_THRESHOLD &&
                       i >= 2 && wasHyphenatedIn(words, i - 1)) {
                sep = "-";
            } else {
                sep = " ";
            }
            out += sep + cur;
        }
        return out;
    }

    function wasHyphenatedIn(words, idx) {
        const w = words[idx].toLowerCase();
        return !!(PARTICLES[w] && PARTICLES[w].hyphRate >= HYPH_THRESHOLD);
    }

    const VIA_SPLIT = /(^|[\s-])via(?=[\s-])/i;

    function canonicalize(name) {
        const raw = String(name);

        const direct = lookupWithRetry(raw);
        if (direct) return direct;

        const viaMatch = raw.match(VIA_SPLIT);
        if (viaMatch) {
            const before = raw.slice(0, viaMatch.index);
            const after = raw.slice(viaMatch.index + viaMatch[0].length);
            const beforeCanon = canonicalizeMain(before);
            const afterCanon = canonicalizeMain(after);
            if (beforeCanon && afterCanon) return beforeCanon + "-Via-" + afterCanon;
            if (beforeCanon) return beforeCanon;
            if (afterCanon) return "Via-" + afterCanon;
            return "";
        }

        return canonicalizeMain(raw);
    }

    function canonicalizeMain(name) {
        const direct = lookupWithRetry(String(name));
        if (direct) return direct;

        const rawSegments = String(name).split(/\s-\s/);
        if (rawSegments.length > 1) {
            const out = [];
            for (var s = 0; s < rawSegments.length; s++) {
                out.push(canonicalizeSegment(rawSegments[s]));
            }
            return out.join(" - ");
        }
        return canonicalizeSegment(name);
    }

    function lookupWithRetry(text) {
        const key = searchKey(text);
        const hit = lookupKey(key);
        if (hit) return hit;

        const tokens = tokenize(text);
        let hasAbbrev = false;
        const expanded = [];
        for (var i2 = 0; i2 < tokens.length; i2++) {
            var tl = tokens[i2].toLowerCase();
            if (tl === "st") { expanded.push("Saint"); hasAbbrev = true; }
            else if (tl === "ste") { expanded.push("Sainte"); hasAbbrev = true; }
            else expanded.push(tokens[i2]);
        }
        if (hasAbbrev) {
            const hit2 = lookupKey(searchKey(expanded.join("")));
            if (hit2) return hit2;
        }
        return null;
    }

    function canonicalizeSegment(segment) {
        const tokens = tokenize(segment);
        if (tokens.length === 0) return "";
        const direct = lookupWithRetry(tokens.join(" "));
        if (direct) return direct;
        return buildFromTokens(tokens);
    }


    function applyLexical(name) {
        let s = name;
        for (var i3 = 0; i3 < LEVEL_LEXICAL.length; i3++) {
            s = s.replace(LEVEL_LEXICAL[i3].regex, LEVEL_LEXICAL[i3].rep2);
        }
        return s;
    }
    function applySaintSte(name) {
        return name
            .replace(/(^|[\s\-'(])Sainte(?=$|[\s\-')])/gi, "$1Ste")
            .replace(/(^|[\s\-'(])Saint(?=$|[\s\-')])/gi, "$1St");
    }
    function applyLinks(name) {
        return name.replace(LINK_SUR, "$1s/").replace(LINK_SOUS, "$1ss/");
    }
    function stripParens(name) {
        return name.replace(/\s*\([^)]*\)\s*/g, " ").replace(/\s+/g, " ")
                   .replace(/^[\s-]+|[\s-]+$/g, "");
    }

    function truncate(name, maxLen, measure) {
        function withDot(s) { return /\.$/.test(s) ? s : s + "."; }
        if (measure(name) <= maxLen) return name;

        let s = name;
        while (s.length > 1 && measure(withDot(s)) > maxLen) s = s.slice(0, s.length - 1);
        s = s.replace(/[\s\-'.]+$/, "");

        function isDangling(tok) {
            const t = tok.toLowerCase().replace(/\.+$/, "");
            if (t === "") return true;
            if (NO_TRAILING[t] === 1) return true;
            if (t.length === 1) return true;
            const sl = t.match(/^(s?s\/)(.*)$/);
            if (sl) return sl[2] === "" || sl[2].length <= 2 || NO_TRAILING[sl[2]] === 1;
            if (/^[ld]'/.test(t)) return t.length <= 4;
            return false;
        }
        let changed = true;
        while (changed) {
            changed = false;
            const m = s.match(/^(.*?)[\s-]([^\s-]+)$/);
            if (m && isDangling(m[2])) { s = m[1].replace(/[\s\-'.]+$/, ""); changed = true; }
        }
        if (s.length === 0) s = name.slice(0, Math.max(1, maxLen - 1));
        return withDot(s);
    }

    function defaultMeasure(s) { return s.length; }

    function format(name) {
        const key = searchKey(name);
        if (SPECIAL_CASES[key]) return SPECIAL_CASES[key];
        return applySaintSte(canonicalize(name));
    }

    function toDisplay(name) {
        return String(name).replace(/-/g, " ");
    }

    function fit(name, maxLen, measure) {
        if (!measure) measure = defaultMeasure;

        const key = searchKey(name);
        if (SPECIAL_CASES[key]) return SPECIAL_CASES[key];

        const canonRaw = canonicalize(name);
        if (measure(canonRaw) <= maxLen) return canonRaw;

        const canon = applySaintSte(canonRaw);
        let s = canon;
        if (measure(s) <= maxLen) return s;

        s = applyLexical(s);
        if (measure(s) <= maxLen) return s;

        s = applyLinks(s);
        if (measure(s) <= maxLen) return s;

        s = stripParens(s);
        if (measure(s) <= maxLen) return s;

        const viaIdx = s.toLowerCase().indexOf("-via-");
        if (viaIdx > 0) {
            s = s.slice(0, viaIdx);
            if (measure(s) <= maxLen) return s;
        }

        const origSegments = canon.split(/\s-\s/);
        if (origSegments.length > 1) {
            for (var keep = origSegments.length - 1; keep >= 1; keep--) {
                var candidate = stripParens(applyLinks(applyLexical(
                    applySaintSte(origSegments.slice(0, keep).join(" - ")))));
                if (measure(candidate) <= maxLen) return candidate;
                if (keep === 1) s = candidate;
            }
        }

        return truncate(s, maxLen, measure);
    }

    return {
        canonicalize: canonicalize,
        format: format,
        fit: fit,
        toDisplay: toDisplay,
        searchKey: searchKey
    };
})();

if (typeof module !== "undefined" && module.exports) {
    module.exports = SncfNames;
}
