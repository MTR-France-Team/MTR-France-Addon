include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:components/sncf_theme.js"));
include(Resources.id("aca:modules/config_handler.js"));
include(Resources.id("aca:modules/animation_utils.js"));

const CFG = {
    scroll: { maxVisibleLines: 12, lineHeight: 5, speed: 4, waitTime: 3, targetY: 4, baseStartY: 7.5 }
};

function create(ctx, state, pids) {}

function render(ctx, state, pids) {
    Texture.create()
    .texture("sncf:images/info/information_background.png")
    .size(pids.width, pids.height)
    .draw(ctx);

    let text = [];
    for (let i = 0; i < 4; i++) {
        let msg = pids.getCustomMessage(i);
        if (msg && msg !== "") text.push(String(msg));
    }
    if (text.length > 0) drawTextInformation(ctx, state, pids, text);
    drawHeaderAndFooter(ctx, pids);

    SncfTheme.drawClock(ctx, pids, Timing.currentTimeMillis());
    SncfTheme.drawFrame(ctx, pids, true);
}

function dispose(ctx, state, pids) {}

function drawHeaderAndFooter(ctx, pids) {
    Texture.create()
    .texture("sncf:images/info/information_header.png")
    .size(pids.width, pids.height)
    .draw(ctx);

    Texture.create()
    .texture("sncf:images/info/information_bottom_2.png")
    .size(pids.width, pids.height)
    .draw(ctx);
}

function drawTextInformation(ctx, state, pids, text) {
    let hash = text.join("||");
    if (!state.infoCache || state.infoCache.hash !== hash) {
        state.infoCache = {
            hash: hash,
            lines: splitSentences(text)
        };
    }
    let lines = state.infoCache.lines;

    const itemHeight = CFG.scroll.lineHeight + 0.5;
    const anim = AnimationManager.scrollVertical(state, "info_panel", lines.length, {
        maxVisible: CFG.scroll.maxVisibleLines,
        waitTime: CFG.scroll.waitTime,
        speed: CFG.scroll.speed,
        itemHeight: itemHeight
    });
    const currentIndex = anim.currentIndex;
    const offset = anim.offset;

    const maxIndex = Math.max(1, lines.length - CFG.scroll.maxVisibleLines);
    const scrollProgress = Math.min(1, (currentIndex + offset / itemHeight) / maxIndex);
    const startY = AnimationUtils.lerp(CFG.scroll.baseStartY, CFG.scroll.targetY, scrollProgress);

    let maxI = lines.length > CFG.scroll.maxVisibleLines ? CFG.scroll.maxVisibleLines + 1 : lines.length;

    let rowY = startY - offset;
    for (var i = 0; i < maxI; i++) {
        if (i + currentIndex >= lines.length) break;

        Text.create()
        .text(lines[i + currentIndex])
        .color(0x00357A)
        .leftAlign()
        .scale(0.45)
        .size(200, CFG.scroll.lineHeight)
        .pos(59, rowY)
        .draw(ctx);

        rowY += itemHeight;
    }
}

function splitSentences(textArray, maxLength) {
    maxLength = maxLength || 27;
    const result = [];

    textArray.forEach((sentence, index) => {
        if (sentence === "") return;

        if (sentence.length <= maxLength) {
            result.push(sentence);
        } else {
            const words = sentence.split(" ");
            let currentLine = "";

            for (let word of words) {
                while (word.length > maxLength) {
                    if (currentLine) {
                        result.push(currentLine);
                        currentLine = "";
                    }
                    result.push(word.substring(0, maxLength));
                    word = word.substring(maxLength);
                }

                if ((currentLine + (currentLine ? " " : "") + word).length <= maxLength) {
                    currentLine += (currentLine ? " " : "") + word;
                } else {
                    if (currentLine) result.push(currentLine);
                    currentLine = word;
                }
            }
            if (currentLine) result.push(currentLine);
        }

        if (index < textArray.length - 1 && textArray.slice(index + 1).some(s => s !== "")) {
            result.push(" ");
        }
    });

    result.push(" ", " ", " ");

    return result;
}
