// MP05 — ÉCRAN STATION V1
// Fond + cadre + panneau bleu + bandeau marron + prochaine station.


// ============================================================
// 1. CONFIGURATION — VALEURS À MODIFIER POUR TES ESSAIS
// ============================================================

var SCREEN_WIDTH = 6.0;
var SCREEN_HEIGHT = 5.25;

// Position du centre de l'écran par rapport à la voiture.
var SCREEN_X = 0.0;
var SCREEN_Y = 0.0;
var SCREEN_Z = 0.0;

// Orientation.
var SCREEN_ROTATION_Y = 0;

// Texture 2D : même ratio que 6 x 5,25 = 8:7.
var TEXTURE_WIDTH = 768;
var TEXTURE_HEIGHT = 672;


// ============================================================
// 2. COULEURS DU DESIGN
// ============================================================

var COLOR_BACKGROUND = 0xE7E7E7;
var COLOR_FRAME = 0xFFFFFF;
var COLOR_BLUE = 0x123B6D;
var COLOR_BROWN = 0x8B5A3C;
var COLOR_TEXT = 0xFFFFFF;


// ============================================================
// 3. RECTANGLE 3D
// ============================================================

var halfWidth = SCREEN_WIDTH / 2;
var halfHeight = SCREEN_HEIGHT / 2;

var meshBuilder = new RawMeshBuilder(
    4,
    "interior",
    Resources.id("aca:textures/white.png")
);

meshBuilder.vertex(-halfWidth, -halfHeight, 0)
    .normal(0, 0, 1).uv(0, 1).endVertex();

meshBuilder.vertex(halfWidth, -halfHeight, 0)
    .normal(0, 0, 1).uv(1, 1).endVertex();

meshBuilder.vertex(halfWidth, halfHeight, 0)
    .normal(0, 0, 1).uv(1, 0).endVertex();

meshBuilder.vertex(-halfWidth, halfHeight, 0)
    .normal(0, 0, 1).uv(0, 0).endVertex();

var rawModel = new RawModel();
rawModel.append(meshBuilder.getMesh());
rawModel.triangulate();

var BASE_MODEL = ModelManager.upload(rawModel);


// ============================================================
// 4. RÉCUPÉRATION DE LA PROCHAINE STATION
// ============================================================

function getNextStation(wrapper) {
    if (!wrapper) return "";

    try {
        var stops = wrapper.getThisRouteStops();
        if (!stops) return "";

        var count = stops.size ? stops.size() : stops.length;
        if (count <= 0) return "";

        var nextIndex = wrapper.getNextStopIndex(stops);
        if (nextIndex < 0 || nextIndex >= count) return "";

        var stop = stops.get ? stops.get(nextIndex) : stops[nextIndex];
        if (!stop) return "";

        if (stop.name) return ("" + stop.name).trim();

        if (typeof stop.getName === "function") {
            return ("" + stop.getName()).trim();
        }
    } catch (e) {
        console.log("[MP05] Erreur station : " + e);
    }

    return "";
}


// ============================================================
// 5. DESSIN DE L'ÉCRAN
// ============================================================

function drawMP05Screen(texture, stationName) {
    var g = texture.graphics.create();

    g.setRenderingHint(
        java.awt.RenderingHints.KEY_ANTIALIASING,
        java.awt.RenderingHints.VALUE_ANTIALIAS_ON
    );

    g.setRenderingHint(
        java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
        java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );

    // Fond gris clair.
    g.setColor(new java.awt.Color(COLOR_BACKGROUND));
    g.fillRect(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    // Cadre blanc extérieur.
    var margin = 24;

    g.setColor(new java.awt.Color(COLOR_FRAME));
    g.fillRoundRect(
        margin, margin,
        TEXTURE_WIDTH - margin * 2,
        TEXTURE_HEIGHT - margin * 2,
        28, 28
    );

    // Panneau bleu.
    var blueX = 96;
    var blueY = 100;
    var blueWidth = TEXTURE_WIDTH - 192;
    var blueHeight = 330;

    g.setColor(new java.awt.Color(COLOR_BLUE));
    g.fillRect(blueX, blueY, blueWidth, blueHeight);

    // Nom de station.
    var text = stationName;

    if (!text) text = "STATION";

    text = ("" + text).toUpperCase();

    if (text.length > 25) {
        text = text.substring(0, 22) + "...";
    }

    var font = new java.awt.Font(
        "SansSerif",
        java.awt.Font.BOLD,
        52
    );

    g.setFont(font);
    g.setColor(new java.awt.Color(COLOR_TEXT));

    var fm = g.getFontMetrics();
    var textWidth = fm.stringWidth(text);

    var textX = blueX + Math.floor((blueWidth - textWidth) / 2);
    var textY = blueY + Math.floor((blueHeight + fm.getAscent()) / 2);

    g.drawString(text, textX, textY);

    // Bandeau marron.
    var brownX = blueX;
    var brownY = 452;
    var brownWidth = blueWidth;
    var brownHeight = 105;

    g.setColor(new java.awt.Color(COLOR_BROWN));
    g.fillRect(brownX, brownY, brownWidth, brownHeight);

    var infoText = "PROCHAINE STATION";

    var infoFont = new java.awt.Font(
        "SansSerif",
        java.awt.Font.BOLD,
        28
    );

    g.setFont(infoFont);
    g.setColor(new java.awt.Color(COLOR_TEXT));

    var infoWidth = g.getFontMetrics().stringWidth(infoText);
    var infoX = brownX + Math.floor((brownWidth - infoWidth) / 2);
    var infoY = brownY +
        Math.floor((brownHeight + g.getFontMetrics().getAscent()) / 2);

    g.drawString(infoText, infoX, infoY);

    // Contour blanc autour du bloc bleu + marron.
    g.setColor(new java.awt.Color(COLOR_FRAME));
    g.setStroke(new java.awt.BasicStroke(12));

    g.drawRect(
        blueX - 6,
        blueY - 6,
        blueWidth + 12,
        brownY + brownHeight - blueY + 12
    );

    g.dispose();

    // Envoie la texture réellement à l'écran.
    texture.upload();
}


// ============================================================
// 6. CREATE
// ============================================================

function create(ctx, state, wrapper) {
    state.perCar = {};

    var cars = ctx.getMyCars();
    if (!cars) return;

    for (var i = 0; i < cars.length; i++) {
        var car = cars[i];

        var texture = new GraphicsTexture(
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT
        );

        var model = BASE_MODEL.copyForMaterialChanges();
        model.replaceAllTexture(texture.identifier);

        state.perCar[car] = {
            texture: texture,
            model: model,
            tracker: new StateTracker()
        };

        drawMP05Screen(
            texture,
            getNextStation(wrapper)
        );
    }
}


// ============================================================
// 7. RENDER
// ============================================================

function render(ctx, state, wrapper) {
    if (!state || !state.perCar) return;

    var cars = ctx.getMyCars();
    if (!cars) return;

    var station = getNextStation(wrapper);

    for (var i = 0; i < cars.length; i++) {
        var car = cars[i];
        var carData = state.perCar[car];

        if (!carData || !carData.model) continue;

        carData.tracker.setState(station);

        if (carData.tracker.stateNowFirst()) {
            drawMP05Screen(carData.texture, station);
        }

        // Côté droit.
        var matricesRight = new Matrices();

        matricesRight.translate(
            SCREEN_X,
            SCREEN_Y,
            SCREEN_Z
        );

        matricesRight.rotateYDegrees(SCREEN_ROTATION_Y);

        ctx.drawCarModel(
            carData.model,
            car,
            matricesRight
        );

        // Côté gauche.
        var matricesLeft = new Matrices();

        matricesLeft.translate(
            -SCREEN_X,
            SCREEN_Y,
            SCREEN_Z
        );

        matricesLeft.rotateYDegrees(-SCREEN_ROTATION_Y);

        ctx.drawCarModel(
            carData.model,
            car,
            matricesLeft
        );
    }
}


// ============================================================
// 8. DISPOSE
// ============================================================

function dispose(ctx, state, wrapper) {
    if (!state || !state.perCar) return;

    var cars = ctx.getMyCars();

    if (cars) {
        for (var i = 0; i < cars.length; i++) {
            var car = cars[i];

            if (
                state.perCar[car] &&
                state.perCar[car].texture
            ) {
                state.perCar[car].texture.close();
            }
        }
    }

    state.perCar = null;
}
