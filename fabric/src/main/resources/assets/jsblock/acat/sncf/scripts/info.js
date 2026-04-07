include(Resources.id("jsblock:scripts/pids_util.js"));

include(Resources.id("aca:sncf_utils.js"));
include(Resources.id("aca:trains_types.js"));
include(Resources.id("aca:config_handler.js"));
include(Resources.id("aca:animation_manager.js")); 

const totalHeight = 68.6;

const MAX_VISIBLE_LINES = 12;
const LINE_HEIGHT = 5;

function create(ctx, state, pids) {
    // Plus besoin d'initialiser state.scroll ici, AnimationManager gère son propre state !
    if(pids.type == "pids_1a") return;
}

function render(ctx, state, pids) {
    if(pids.type == "pids_1a") {
        Text.create()
        .text("PIDS non supportée.")
        .color(0xFFFFFF)
        .leftAlign()
        .scale(1)
        .pos(5, 5)
        .draw(ctx);
        return;
    }

    // Dessin du fond bleu de l'écran d'information
    Texture.create()
    .texture("sncf:images/info/information_background.png")
    .size(pids.width, pids.height)
    .draw(ctx);

    // Récupération des messages personnalisés (lignes 1 à 4)
    let text = [];
    for(let i = 0; i < 4; i++) {
        let msg = ""+pids.getCustomMessage(i);
        text.push(msg != null ? msg : "");
    }

    drawTextInformation(ctx, state, pids, text);
    drawHeaderAndFooter(ctx, pids);

    // Utilisation de Timing.currentTimeMillis() pour être 100% synchro avec le serveur
    SncfUtils.drawClock(ctx, pids, typeof Timing !== "undefined" ? Timing.currentTimeMillis() : Date.now());
    SncfUtils.drawFrame(ctx, pids, 9.8, true);
}

function dispose(ctx, state, pids) {}

function drawHeaderAndFooter(ctx, pids) {
    // Le bandeau supérieur (qui vient cacher le texte qui défile vers le haut)
    Texture.create()
    .texture("sncf:images/info/information_header.png")
    .size(pids.width, pids.height)
    .draw(ctx);

    // Le bandeau inférieur
    Texture.create()
    .texture("sncf:images/info/information_bottom_2.png")
    .size(pids.width, pids.height)
    .draw(ctx);
}

function drawTextInformation(ctx, state, pids, text) {
    let lines = splitSentences(text);
    let startY = 7.5;

    // 1. Appel du gestionnaire d'animation vertical
    const anim = AnimationManager.scrollVertical(state, "info_panel", lines.length, {
        maxVisible: MAX_VISIBLE_LINES,
        waitTime: 7,               // Temps de pause avant de scroller (TIME_BETWEEN_STATES)
        speed: 5,                // Vitesse de défilement
        itemHeight: LINE_HEIGHT + 0.5 // Hauteur d'une ligne + marge
    });

    // On affiche une ligne supplémentaire (+1) pour que la ligne qui sort/entre
    // de l'écran soit dessinée pendant sa transition fluide.
    let maxI = lines.length > MAX_VISIBLE_LINES ? MAX_VISIBLE_LINES + 1 : lines.length;

    // 2. Boucle d'affichage
    for (let i = 0; i < maxI; i++) {
        // Sécurité pour ne pas chercher une ligne qui n'existe pas dans le tableau
        if (i + anim.currentIndex >= lines.length) break;

        Text.create()
        .text(lines[i + anim.currentIndex])
        .color(0x00357A)
        .leftAlign()
        .scale(0.45)
        .font("mtr:achemine")
        .size(200, LINE_HEIGHT)
        // Application magique du décalage (anim.offset) pour la fluidité !
        .pos(59, startY + (i * (LINE_HEIGHT + 0.5)) - anim.offset)
        .draw(ctx);
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
                
                // --- LA SÉCURITÉ ANTI-DÉBORDEMENT ---
                // Si un mot est plus long que la ligne entière, on le coupe de force
                while (word.length > maxLength) {
                    // Si on avait déjà des mots sur la ligne actuelle, on la sauvegarde
                    if (currentLine) {
                        result.push(currentLine);
                        currentLine = "";
                    }
                    // On pousse un bout du mot géant
                    result.push(word.substring(0, maxLength));
                    // On garde ce qu'il reste du mot pour la suite
                    word = word.substring(maxLength);
                }
                // ------------------------------------

                if ((currentLine + (currentLine ? " " : "") + word).length <= maxLength) {
                    currentLine += (currentLine ? " " : "") + word;
                } else {
                    if (currentLine) result.push(currentLine);
                    currentLine = word;
                }
            }
            if (currentLine) result.push(currentLine);
        }
        
        // Ajoute un espace entre les différents messages s'ils existent
        if (index < textArray.length - 1 && textArray.slice(index + 1).some(s => s !== "")) {
            result.push(" ");
        }
    });

    // Lignes vides à la fin pour que le texte disparaisse complètement avant de boucler
    result.push(" ", " ", " ");
    
    return result;
}