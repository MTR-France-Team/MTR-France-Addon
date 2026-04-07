const SncfUtils = {

    init(state) {
        if (!state.sncfCache) {
            state.sncfCache = {
                strings: {},
                lines: {},
                formatted: {}
            };
        }
    },

    // --- Graphismes & Textures ---

    getUV(x, y, w, h, total = 30) {
        let t = total || 30;
        return [x / t, y / t, (x + w) / t, (y + h) / t];
    },

    drawFrame(ctx, pids, showLogo) {
        let frameUV = this.getUV(10, 0, 5, 5); 
        let texPath = "jsblock:acat/sncf/images/colors.png";
        
        const size = 9.8;

        // Haut
        Texture.create("Frame-Top")
            .texture(texPath)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(pids.width + (size * 2), size)
            .pos(-size, -size)
            .draw(ctx);
    
        // Bas
        Texture.create("Frame-Bottom")
            .texture(texPath)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(pids.width + (size * 2), size)
            .pos(-size, pids.height)
            .draw(ctx);
    
        // Gauche
        Texture.create("Frame-Left")
            .texture(texPath)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(size, pids.height + (size * 2))
            .pos(-size, -size)
            .draw(ctx);
    
        // Droite
        Texture.create("Frame-Right")
            .texture(texPath)
            .uv(frameUV[0], frameUV[1], frameUV[2], frameUV[3])
            .size(size, pids.height + (size * 2))
            .pos(pids.width, -size)
            .draw(ctx);
    
        if(showLogo) {
            Texture.create("Sncf Logo")
                .texture("jsblock:sncf/images/labels/sncf.png")
                .size(6.5, 4)
                .pos(pids.width + (size * 0.2), pids.height + (size * 0.4))
                .draw(ctx);
        }
    },

    // --- Traitement de texte (Avec Cache) ---

    convertNameToLines(state, name) {
        if (!state.sncfCache) this.init(state);
        
        let cache = state.sncfCache.lines;
        if (cache[name]) return cache[name];

        let maxLength = 13;
        let lines = [];

        let specialCases = {
            "N-Dame": "N-Dame", "Part-Dieu": "Part-Dieu", "Charles de Gaulle": "CDG",
            "en-Bugey": "en-Bugey", "St-Charles": "St-Charles",
        };

        let segments = name.split('-Via-');
        let mainPart = segments[0] || '';
        let viaParts = segments.slice(1).map(part => 'Via-' + part);

        function processSegment(segment, isVia) {
            let parts = segment.split('-');
            let currentLine = '';
            let i = 0;

            while (i < parts.length) {
                let part = parts[i];
                let nextPart = i + 1 < parts.length ? parts[i + 1] : '';
                let combinedPart = '';

                if (nextPart && specialCases[part + '-' + nextPart]) {
                    combinedPart = part + '-' + nextPart;
                    i += 2;
                } else {
                    combinedPart = part;
                    i += 1;
                }

                let potential = currentLine ? currentLine + ' ' + combinedPart : combinedPart;

                if (potential.length <= maxLength) {
                    currentLine = potential;
                } else {
                    if (currentLine) {
                        lines.push({ text: currentLine, margin: isVia && currentLine.indexOf('Via') === 0 ? 0.7 : 0.5 });
                    }
                    currentLine = combinedPart;
                }
            }

            if (currentLine) {
                lines.push({ text: currentLine, margin: isVia && currentLine.indexOf('Via') === 0 ? 0.7 : 0.5 });
            }
        }

        processSegment(mainPart, false);
        for (let j = 0; j < viaParts.length; j++) {
            processSegment(viaParts[j], true);
        }

        cache[name] = lines;
        return lines;
    },

    shortenNames(state, name) {
        name = "" + name;
        if (!state.sncfCache) this.init(state);

        let cache = state.sncfCache.strings;
        if (cache[name]) return cache[name];

        let replacements = {
            "Saint": "St", "Sainte": "Ste", "Fort": "Ft", "Château": "Ch",
            "Grand": "Gd", "Petit": "Pt", "Notre-Dame": "N-Dame", "Ville": "",
            "Charles de Gaulle": "CDG", "CDG": "CDG", "via": "Via"
        };

        let specialCases = {
            "Aéroport Charles de Gaulle": "Aéroport-CDG"
        };

        let processed = name.replace(/\s+/g, ' ').trim();

        for (let key in replacements) {
            let regex = new RegExp('\\b' + key.replace(/[-]/g, '[-\\s]') + '\\b', 'gi');
            processed = processed.replace(regex, replacements[key]);
        }

        if (specialCases[processed]) {
            cache[name] = specialCases[processed];
            return specialCases[processed];
        }

        let parts = processed.split(/[-\s]/);
        let formattedParts = [];

        for (let i = 0; i < parts.length; i++) {
            let part = parts[i];
            let formattedPart = part;

            if (part.length > 0) {
                formattedPart = part.charAt(0).toUpperCase() + part.slice(1).toLowerCase();
            }

            for (let key in replacements) {
                if (formattedPart.toLowerCase() == key.toLowerCase()) {
                    formattedPart = replacements[key];
                    break;
                }
            }

            if (formattedPart.toLowerCase().indexOf("l'") == 0) {
                formattedPart = "L'" + formattedPart.slice(2);
            }
            let lower = formattedPart.toLowerCase();
            if (lower == "de" || lower == "en" || lower == "du" || lower == "des") {
                formattedPart = lower;
            }

            formattedParts.push(formattedPart);
        }

        let result = formattedParts.join('-').replace(/--+/g, '-').replace(/-$/, '');

        cache[name] = result;
        return result;
    },

    displayStationName(name) {
        return name.replace(/-/g, ' ');
    },

    formatText(state, sentences) {
        if (!sentences || sentences.length === 0) return "";
        
        if (!state.sncfCache) this.init(state);
        let key = sentences.join("||");
        let cache = state.sncfCache.formatted;
        
        if (cache[key]) return cache[key];

        function capitalizeFirst(str) {
            return str.length === 0 ? str : str.charAt(0).toUpperCase() + str.substring(1);
        }
        
        function processWord(word, isFirst) {
            let match = word.match(/^(.+?)([,.?!:;]*)$/);
            if (!match) return word;
            let core = match[1].toLowerCase();
            let punct = match[2];
            if (isFirst) core = capitalizeFirst(core);
            return core + punct;
        }

        let processedSentences = [];
        for (let j = 0; j < sentences.length; j++) {
            let s = sentences[j].trim();
            if (s === "") continue;
            
            let words = s.split(/\s+/);
            let processedWords = [];
            for (let k = 0; k < words.length; k++) {
                if (words[k]) processedWords.push(processWord(words[k], k === 0));
            }
            s = processedWords.join(" ");
            
            s = s.replace(/ ([?!:;])/g, "$1");
            
            if (!/[.!?]$/.test(s)) s += ".";
            processedSentences.push(s);
        }
        
        let res = processedSentences.join(" ");
        cache[key] = res;
        return res;
    },

    // --- Helpers de Temps ---

    drawClock(ctx, pids, timestamp) {
        // Horloge simple

        Text.create("Clock")
            .text(this.formatTime(timestamp))
            .color(0xFFFFFF)
            .pos(pids.width - 7.25, pids.height - 3.75)
            .scale(0.4)
            .rightAlign()
            .font("mtr:achemine_bold")
            .draw(ctx);
        
        Text.create("Clock Fixed Seconds")
            .text(this.getSeconds(timestamp))
            .color(0xFF9F17)
            .pos(pids.width - 3.75, pids.height - 3.1)
            .scale(0.28)
            .rightAlign()
            .font("mtr:achemine_bold")
            .draw(ctx);
    },

    formatTime(timestamp, separator) {
        separator = separator || ":";
        let date = new Date(timestamp);
        let h = date.getHours().toString().padStart(2, "0");
        let m = date.getMinutes().toString().padStart(2, "0");
        return `${h}${separator}${m}`;
    },

    getSeconds(timestamp) {
        let date = new Date(timestamp);
        return date.getSeconds().toString().padStart(2, "0");
    },

    formatTrainDelay(delay, byFiveMinutes, shorted) {
        let minutes;
        if (byFiveMinutes) {
            minutes = Math.ceil((delay / 60) / 5) * 5;
        } else {
            minutes = Math.ceil(delay / 60);
        }
        if (minutes < 60) {
            return `${minutes.toString().padStart(2, "0")}${shorted ? "mn" : " min"}`;
        } else {
            let hours = Math.floor(minutes / 60);
            let remainingMinutes = minutes % 60;
            return `${hours}h${remainingMinutes.toString().padStart(2, "0")}`;
        }
    },

    // --- Helpers de Fontes ---

    getCharWidth(char, onePx) {
        onePx = onePx || 0.5;
        const tiny = "i.,;:!'|"; // 1px
        const extraSmall = "l`•";  // 2px
        const small = "tI\"()*¨{}[] "; // 3px
        const medium = "fk<>°²";  // 4px
        const large = "@~";  // 6px
        const extraLarge = "¤"; // 7px
        const extraExtraLarge = ""; // 8px
        const extraExtraExtraLarge = "⚠"; // 9px
        const defaultWidth = 5;

        // CJK Checks
        const japaneseRegex = /[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF]/;
        if (japaneseRegex.test(char)) return 7.75 * onePx;
        
        if (tiny.indexOf(char) !== -1) return 1 * onePx;
        if (extraSmall.indexOf(char) !== -1) return 2 * onePx;
        if (small.indexOf(char) !== -1) return 3 * onePx;
        if (medium.indexOf(char) !== -1) return 4 * onePx;
        if (large.indexOf(char) !== -1) return 6 * onePx;
        if (extraLarge.indexOf(char) !== -1) return 7 * onePx;
        if (extraExtraLarge.indexOf(char) !== -1) return 8 * onePx;
        if (extraExtraExtraLarge.indexOf(char) !== -1) return 9 * onePx;
        
        return defaultWidth * onePx;
    },

    getCharHeight(onePx) {
        return 7 * (onePx || 0.5);
    },

    getStringWidth(str, onePx) {
        if (!str) return 0;
        onePx = onePx || 0.5;
        let totalWidth = 0;

        for (let i = 0; i < str.length; i++) {
            totalWidth += this.getCharWidth(str[i], onePx);
            
            if (i < str.length - 1) {
                totalWidth += (1 * onePx);
            }
        }

        return totalWidth;
    },

    getContrastColor(bgColor) {
        let color = typeof bgColor === 'string' ? parseInt(bgColor, 16) : bgColor;

        let r = (color >> 16) & 0xFF;
        let g = (color >> 8) & 0xFF;
        let b = color & 0xFF;

        let luminance = ((r * 299) + (g * 587) + (b * 114)) / 1000;
        
        return (luminance >= 145) ? 0x000000 : 0xFFFFFF;
    },

};