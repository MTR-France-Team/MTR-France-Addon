const SncfUtils = {
    // Draw the border frame around the display
    drawFrame(ctx, pids, size, showLogo) {
        Texture.create("Frame-Top")
            .texture("jsblock:sncf/images/gray.png")
            .size(pids.width + (size * 2), size)
            .pos(-size, -size)
            .draw(ctx);
    
        Texture.create("Frame-Bottom")
            .texture("jsblock:sncf/images/gray.png")
            .size(pids.width + (size * 2), size)
            .pos(-size, pids.height)
            .draw(ctx);
    
        Texture.create("Frame-Left")
            .texture("jsblock:sncf/images/gray.png")
            .size(size, pids.height + (size * 2))
            .pos(-size, -size)
            .draw(ctx);
    
        Texture.create("Frame-Right")
            .texture("jsblock:sncf/images/gray.png")
            .size(size, pids.height + (size * 2))
            .pos(pids.width, -size)
            .draw(ctx);
    
        if(showLogo) Texture.create("Sncf Logo")
            .texture("jsblock:sncf/images/labels/sncf.png")
            .size(6.5, 4)
            .pos(pids.width + (size * 0.2), pids.height + (size * 0.4))
            .draw(ctx);
    },

    // Convert station name to lines for the Platform Display
    convertNameToLines(name) {
        let maxLength = 13;
        let lines = [];

        let specialCases = {
            "N-Dame": "N-Dame",
            "Part-Dieu": "Part-Dieu",
            "Charles de Gaulle": "CDG",
            "en-Bugey": "en-Bugey",
            "St-Charles": "St-Charles",
        };

        let segments = name.split('-Via-');
        let mainPart = segments[0] || '';
        let viaParts = segments.slice(1).map(function(part) { return 'Via-' + part; });

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

        return lines;
    },
    shortenNames(name) {
        name = ""+name;

        let replacements = {
            "Saint": "St",
            "Sainte": "Ste",
            "Fort": "Ft",
            "Château": "Ch",
            "Grand": "Gd",
            "Petit": "Pt",
            "Notre-Dame": "N-Dame",
            "Ville": "",
            "Charles de Gaulle": "CDG",
            "CDG": "CDG",
            "via": "Via"
        };

        let specialCases = {
            "Aéroport Charles de Gaulle": "Aéroport-CDG"
        };

        name = name.replace(/\s+/g, ' ').trim();

        // Preprocess replacements that contain hyphens or spaces (e.g., Notre-Dame, Charles de Gaulle)
        for (let key in replacements) {
            let regex = new RegExp('\\b' + key.replace(/[-]/g, '[-\\s]') + '\\b', 'gi');
            let replacement = replacements[key];
            name = name.replace(regex, replacement);
        }

        if (specialCases[name]) {
            return specialCases[name];
        }

        let parts = name.split(/[-\s]/);
        let formattedParts = [];

        for (let i = 0; i < parts.length; i++) {
            let part = parts[i];
            let formattedPart = part;

            if (part.length > 0) {
                formattedPart = part.charAt(0).toUpperCase() + part.slice(1).toLowerCase();
            }

            for (let key in replacements) {
                let keyLower = key.toLowerCase();
                let partLower = formattedPart.toLowerCase();
                if (partLower == keyLower) {
                    formattedPart = replacements[key];
                    break;
                }
            }

            if (formattedPart.toLowerCase().indexOf("l'") == 0) {
                formattedPart = "L'" + formattedPart.slice(2);
            }

            if (formattedPart.toLowerCase() == "de" || formattedPart.toLowerCase() == "en" || formattedPart.toLowerCase() == "du" || formattedPart.toLowerCase() == "des") {
                formattedPart = formattedPart.toLowerCase();
            }

            formattedParts.push(formattedPart);
        }

        let result = formattedParts.join('-').replace(/--+/g, '-');

        result = result.replace(/-$/, '');

        return result;
    },
    displayStationName(name) {
        let result = name.replace(/-/g, ' ');
        return result;
    },

    // Format Text for the Information Display
    formatText(sentences) {
        function capitalizeFirst(str) {
            if (str.length === 0) {
                return str;
            }
            return str.charAt(0).toUpperCase() + str.substring(1);
        }
        
        function processWord(word, isFirst) {
            let match = word.match(/^(.+?)([,.?!:;]*)$/);
            if (!match) {
                return word;
            }
            let core = match[1];
            let punct = match[2];
            
            core = core.toLowerCase();
            
            if (isFirst) {
                core = capitalizeFirst(core);
            }
            
            return core + punct;
        }
        
        function fixPunctuationSpacing(sentence) {
            let result = "";
            let punctuationMarks = { "?": true, "!": true, ":": true, ";": true };
            for (var i = 0; i < sentence.length; i++) {
                let ch = sentence.charAt(i);
                if (punctuationMarks[ch]) {
                    if (ch === ":" && i + 2 < sentence.length && sentence.charAt(i + 1) === "/" && sentence.charAt(i + 2) === "/") {
                        result += ch;
                    } else {
                        if (result.length > 0 && result.charAt(result.length - 1) !== " ") {
                            result += " ";
                        }
                        result += ch;
                    }
                } else {
                    result += ch;
                }
            }
            return result;
        }
        
        let processedSentences = [];
        for (var j = 0; j < sentences.length; j++) {
            let s = sentences[j];
            s = s.trim();
            if (s === "") {
                continue;
            }
            
            let words = s.split(/\s+/);
            let processedWords = [];
            for (var k = 0; k < words.length; k++) {
                if (words[k]) {
                    processedWords.push(processWord(words[k], k === 0));
                }
            }
            s = processedWords.join(" ");
            
            s = fixPunctuationSpacing(s);
            
            if (!/[.!?]$/.test(s)) {
                s += ".";
            }
            
            processedSentences.push(s);
        }
        
        return processedSentences.join(" ");
    },

    // Get character width and height for text rendering based on pixel size (scale parameter) (based on default minecraft font)
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

        const defaultWidth = 5; // 5px
    
        // Unicode for CJK
        const japaneseRegex = /[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF]/; // Hiragana, Katakana, Kanji
        const chineseRegex = /[\u4E00-\u9FFF]/; // Chinese characters
        const koreanRegex = /[\uAC00-\uD7AF]/; // Hangul
    
        // Check for CJK first
        if (japaneseRegex.test(char)) return 7.75 * onePx;
        if (chineseRegex.test(char)) return 7 * onePx;
        if (koreanRegex.test(char)) return 7 * onePx;
    
        // Existing Latin-based checks
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
        return 7 * onePx;
    },

    // All functions related to time
    drawClock(ctx, pids, timestamp) {
        Text.create("Clock")
            .text(this.formatTime(timestamp))
            .color(0xFFFFFF)
            .pos(pids.width - 7.25, pids.height - 3.75)
            .scale(0.4)
            .rightAlign()
            .draw(ctx);
    
        Text.create("Clock Fixed Seconds")
            .text(this.getSeconds(timestamp))
            .color(0xFF9F17)
            .pos(pids.width - 3.75, pids.height - 3.1)
            .scale(0.28)
            .rightAlign()
            .draw(ctx);
    },  
    formatTime(timestamp, separator) {
        separator = separator || ":";
    
        const date = new Date(timestamp);
        const hours = date.getHours().toString().padStart(2, "0");
        const minutes = date.getMinutes().toString().padStart(2, "0");
        return `${hours}${separator}${minutes}`;
    },
    getSeconds(timestamp) {
        const date = new Date(timestamp);
        return date.getSeconds().toString().padStart(2, "0");
    },

    formatTrainDelay(delay, byFiveMinutes, shorted) {
        let minutes;
        if (byFiveMinutes) {
            // 05, 10, 15, 20, etc.
            minutes = Math.ceil((delay / 60) / 5) * 5;
        } else {
            // 01, 02, 03, etc.
            minutes = Math.ceil(delay / 60);
        }
        if (minutes < 60) {
            return `${minutes.toString().padStart(2, "0")}${shorted ? "mn" : " min"}`;
        } else {
            let hours = Math.floor(minutes / 60);
            let remainingMinutes = minutes % 60;
            return `${hours}h${remainingMinutes.toString().padStart(2, "0")}`;
        }
    }
};