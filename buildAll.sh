#!/bin/bash
# ─────────────────────────────────────────────────────────────
#  MTRFRA Build System
# ─────────────────────────────────────────────────────────────
set -e

ALL_VERSIONS=("1.20.4" "1.20.1" "1.19.4" "1.19.2" "1.18.2" "1.17.1" "1.16.5")
MOD_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
RELEASE_DIR="releases"

# ── Couleurs ──────────────────────────────────────────────────
B='\033[1m'; D='\033[2m'; N='\033[0m'
R='\033[31m'; G='\033[32m'; Y='\033[33m'; C='\033[36m'; M='\033[35m'; W='\033[37m'

ok()   { echo -e "   ${G}✔${N}  $1"; }
warn() { echo -e "   ${Y}▸${N}  $1"; }
fail() { echo -e "   ${R}✖${N}  $1"; }
info() { echo -e "   ${C}◆${N}  $1"; }
step() { echo -e "\n ${M}${B}$1${N}\n"; }

# ── Formatage du temps ────────────────────────────────────────
format_time() {
    local T=$1
    local MIN=$((T / 60))
    local SEC=$((T % 60))
    printf "%02dm %02ds\n" $MIN $SEC
}

# ── Banner ────────────────────────────────────────────────────
clear
echo ""
echo -e "   ${D}┌──────────────────────────────────────┐${N}"
echo -e "   ${D}│${N}  ${B}🚄 MTRFRA Build System${N}  ${D}v${MOD_VERSION}${N}      ${D}│${N}"
echo -e "   ${D}└──────────────────────────────────────┘${N}"

# ── 1. Loader ─────────────────────────────────────────────────
step "1/4  Loader"
echo -e "   ${W}1${N} ${D})${N}  Fabric"
echo -e "   ${W}2${N} ${D})${N}  Forge"
echo -e "   ${W}3${N} ${D})${N}  Fabric + Forge"
echo ""
read -rp "   choix [1/2/3] (défaut: 3) › " LOADER_CHOICE
LOADER_CHOICE=${LOADER_CHOICE:-3}

case $LOADER_CHOICE in
    1) BUILD_FABRIC=true;  BUILD_FORGE=false; ok "Fabric" ;;
    2) BUILD_FABRIC=false; BUILD_FORGE=true;  ok "Forge"  ;;
    *) BUILD_FABRIC=true;  BUILD_FORGE=true;  ok "Fabric + Forge" ;;
esac

# ── 2. Versions ───────────────────────────────────────────────
step "2/4  Versions"
for i in "${!ALL_VERSIONS[@]}"; do
    echo -e "   ${W}$((i+1))${N} ${D})${N}  ${ALL_VERSIONS[$i]}"
done
echo ""
echo -e "   ${D}Entrée = toutes · numéros séparés par espace (ex: 1 4 5)${N}"
read -rp "   versions › " VERSION_INPUT

SELECTED_VERSIONS=()
if [ -z "$VERSION_INPUT" ]; then
    SELECTED_VERSIONS=("${ALL_VERSIONS[@]}")
    ok "Toutes (${#ALL_VERSIONS[@]})"
else
    for NUM in $VERSION_INPUT; do
        IDX=$((NUM - 1))
        if [ "$IDX" -ge 0 ] 2>/dev/null && [ "$IDX" -lt "${#ALL_VERSIONS[@]}" ]; then
            SELECTED_VERSIONS+=("${ALL_VERSIONS[$IDX]}")
        fi
    done
    if [ ${#SELECTED_VERSIONS[@]} -eq 0 ]; then
        fail "Aucune version valide"; exit 1
    fi
    ok "${SELECTED_VERSIONS[*]}"
fi

# ── 3. Suffixe ────────────────────────────────────────────────
step "3/4  Suffixe"
echo -e "   ${D}Ajout au nom de fichier (ex: hotfix3, rc1, beta1)${N}"
read -rp "   suffixe (vide = aucun) › " SUFFIX

if [ -n "$SUFFIX" ]; then
    SUFFIX_TAG="-${SUFFIX}"
    ok "Suffixe : ${SUFFIX_TAG}"
else
    SUFFIX_TAG=""
    ok "Aucun suffixe"
fi

# ── 4. Récap ──────────────────────────────────────────────────
LOADER_LABEL=""
LOADER_COUNT=0
if $BUILD_FABRIC; then LOADER_LABEL+="Fabric "; LOADER_COUNT=$((LOADER_COUNT+1)); fi
if $BUILD_FORGE;  then LOADER_LABEL+="Forge";   LOADER_COUNT=$((LOADER_COUNT+1)); fi

step "4/4  Récapitulatif"
info "Version   ${B}${MOD_VERSION}${SUFFIX_TAG}${N}"
info "Loaders   ${B}${LOADER_LABEL}${N}"
info "MC        ${B}${SELECTED_VERSIONS[*]}${N}"
info "Builds    ${B}$(( ${#SELECTED_VERSIONS[@]} * LOADER_COUNT ))${N} JAR(s)"
echo ""
read -rp "   Lancer ? [O/n] › " CONFIRM
if [[ "$CONFIRM" =~ ^[nN] ]]; then
    fail "Annulé."; exit 0
fi

# ── Démarrage Chronomètre Global ──────────────────────────────
GLOBAL_START=$(date +%s)

# ── Préparation ───────────────────────────────────────────────
step "Préparation"
warn "setupFiles..."
./gradlew setupFiles > /dev/null 2>&1
ok "Code commun copié (Fabric → Forge)"

warn "Nettoyage..."
./gradlew clean > /dev/null 2>&1
ok "Workspace propre"

mkdir -p "$RELEASE_DIR"

# Backup du fichier original AVANT la boucle
cp gradle.properties gradle.properties.orig

# ── Pack format ───────────────────────────────────────────────
get_pack_format() {
    case $1 in
        "1.16.5") echo 6  ;; "1.17.1") echo 7  ;;
        "1.18.2") echo 8  ;; "1.19.2") echo 9  ;;
        "1.19.4") echo 12 ;; "1.20.1") echo 15 ;;
        "1.20.4") echo 22 ;; *)        echo 15 ;;
    esac
}

# ── Chercher et copier un JAR ─────────────────────────────────
collect_jar() {
    local LOADER=$1
    local MC_VERSION=$2
    local OUTPUT_NAME="MTRFRA-${LOADER}-${MOD_VERSION}+${MC_VERSION}${SUFFIX_TAG}.jar"

    local JAR
    JAR=$(find . -path "*/build/libs/*" \
        -name "*${LOADER}*${MC_VERSION}*.jar" \
        ! -name "*-sources.jar" \
        ! -name "*-javadoc.jar" \
        ! -name "*-dev.jar" \
        ! -name "*-shadow.jar" \
        2>/dev/null | head -1)

    if [ -n "$JAR" ] && [ -f "$JAR" ]; then
        cp "$JAR" "${RELEASE_DIR}/${OUTPUT_NAME}"
        local SIZE
        SIZE=$(du -h "${RELEASE_DIR}/${OUTPUT_NAME}" | cut -f1)
        ok "${OUTPUT_NAME}  ${D}(${SIZE})${N}"
        return 0
    else
        fail "${LOADER^} — JAR introuvable pour ${MC_VERSION}"
        return 1
    fi
}

# ── Build loop ────────────────────────────────────────────────
SUCCESS=0
FAIL=0
TOTAL=${#SELECTED_VERSIONS[@]}

for i in "${!SELECTED_VERSIONS[@]}"; do
    VERSION="${SELECTED_VERSIONS[$i]}"
    PACK_FORMAT=$(get_pack_format "$VERSION")

    # Chrono d'étape
    STEP_START=$(date +%s)

    step "Build $((i+1))/${TOTAL}  ·  Minecraft ${VERSION}"

    # Patcher pack.mcmeta + gradle.properties
    find . -type f -name "pack.mcmeta" -exec sed -i.bak "s/\"pack_format\": [0-9]*/\"pack_format\": $PACK_FORMAT/" {} +
    sed -i.bak "s/minecraft_version=.*/minecraft_version=$VERSION/" gradle.properties

    # Nettoyage direct des .bak
    find . -type f -name "pack.mcmeta.bak" -delete
    rm -f gradle.properties.bak

    echo -e "   ${D}>> Lancement de Gradle...${N}"

    # Exécution de gradle AVEC l'affichage de la console riche (barre de progression / étapes)
    if ./gradlew build -Pmod_suffix="${AFTER}" --no-configuration-cache --console=rich; then
        STEP_END=$(date +%s)
        STEP_TIME=$((STEP_END - STEP_START))

        echo ""
        ok "Compilation terminée en ${B}$(format_time $STEP_TIME)${N}"

        if $BUILD_FABRIC; then collect_jar "fabric" "$VERSION" || true; fi
        if $BUILD_FORGE;  then collect_jar "forge"  "$VERSION" || true; fi
        SUCCESS=$((SUCCESS+1))
    else
        STEP_END=$(date +%s)
        echo ""
        fail "Gradle build échoué (après $(format_time $((STEP_END - STEP_START))))"
        FAIL=$((FAIL+1))
    fi
done

# ── Restaurer gradle.properties ───────────────────────────────
mv gradle.properties.orig gradle.properties

# ── Résumé ────────────────────────────────────────────────────
GLOBAL_END=$(date +%s)
TOTAL_TIME=$((GLOBAL_END - GLOBAL_START))

echo ""
echo -e "   ${D}┌──────────────────────────────────────┐${N}"
echo -e "   ${D}│${N}  ${B}Build terminé en $(format_time $TOTAL_TIME)${N}            ${D}│${N}"
echo -e "   ${D}└──────────────────────────────────────┘${N}"
echo ""

if [ "$FAIL" -eq 0 ]; then
    ok "${G}${SUCCESS}/${TOTAL} builds réussis${N}"
else
    ok "${SUCCESS} réussi(s)"
    fail "${FAIL} échoué(s)"
fi

echo ""
info "Contenu de ${RELEASE_DIR}/ :"
echo ""
if ls "${RELEASE_DIR}"/*.jar 1>/dev/null 2>&1; then
    ls -1 "${RELEASE_DIR}"/*.jar | while read -r f; do
        SIZE=$(du -h "$f" | cut -f1)
        echo -e "      ${G}✔${N}  $(basename "$f")  ${D}${SIZE}${N}"
    done
else
    warn "Aucun JAR dans ${RELEASE_DIR}/"
fi
echo ""