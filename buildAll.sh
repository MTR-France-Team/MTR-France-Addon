#!/bin/bash
# ─────────────────────────────────────────────────────────────
#  MTRFRA Build System
# ─────────────────────────────────────────────────────────────
set -e

ALL_VERSIONS=("1.20.4" "1.20.1" "1.19.4" "1.19.2" "1.18.2" "1.17.1" "1.16.5")
MOD_VERSION=$(grep "mod_version=" gradle.properties | cut -d'=' -f2)
RELEASE_DIR="releases"

# ── Colors ───────────────────────────────────────────────────
B='\033[1m'; D='\033[2m'; N='\033[0m'
R='\033[31m'; G='\033[32m'; Y='\033[33m'; C='\033[36m'; M='\033[35m'; W='\033[37m'

ok()   { echo -e "   ${G}✔${N}  $1"; }
warn() { echo -e "   ${Y}▸${N}  $1"; }
fail() { echo -e "   ${R}✖${N}  $1"; }
info() { echo -e "   ${C}◆${N}  $1"; }
step() { echo -e "\n ${M}${B}$1${N}\n"; }

# ── Time formatting ──────────────────────────────────────────
format_time() {
    local T=$1
    printf "%02dm %02ds\n" $((T / 60)) $((T % 60))
}

# ── Usage ────────────────────────────────────────────────────
usage() {
    echo -e "\n ${B}MTRFRA Build System${N}  ${D}v${MOD_VERSION}${N}\n"
    echo -e " ${B}Usage:${N}  ./buildAll.sh [options]\n"
    echo -e " ${B}Options:${N}"
    echo -e "   ${W}-l, --loader${N} ${D}<fabric|forge|both>${N}   Mod loader         ${D}(default: both)${N}"
    echo -e "   ${W}-v, --versions${N} ${D}<list>${N}              MC versions        ${D}(default: all)${N}"
    echo -e "                                       ${D}comma-separated (e.g. 1.20.4,1.19.4)${N}"
    echo -e "   ${W}-s, --suffix${N} ${D}<tag>${N}                 Filename suffix    ${D}(e.g. hotfix3, rc1)${N}"
    echo -e "   ${W}-y, --yes${N}                          Skip confirmation"
    echo -e "   ${W}-h, --help${N}                         Show this help\n"
    echo -e " ${B}Interactive:${N}"
    echo -e "   Run without arguments for guided prompts.\n"
    echo -e " ${B}Examples:${N}"
    echo -e "   ${D}./buildAll.sh${N}                                  ${D}# interactive${N}"
    echo -e "   ${D}./buildAll.sh -l fabric -v 1.20.4${N}              ${D}# fabric only, one version${N}"
    echo -e "   ${D}./buildAll.sh -l both -v 1.20.4,1.19.4 -s rc1${N}  ${D}# both loaders, 2 versions${N}"
    echo -e "   ${D}./buildAll.sh -y${N}                                ${D}# all defaults, no prompt${N}"
    echo ""
    exit 0
}

# ── Defaults ─────────────────────────────────────────────────
ARG_LOADER=""
ARG_VERSIONS=""
ARG_SUFFIX=""
ARG_YES=false
INTERACTIVE=true

# ── Parse args ───────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
    INTERACTIVE=false
    case $1 in
        -l|--loader)   ARG_LOADER="$2";   shift 2 ;;
        -v|--versions) ARG_VERSIONS="$2"; shift 2 ;;
        -s|--suffix)   ARG_SUFFIX="$2";   shift 2 ;;
        -y|--yes)      ARG_YES=true;      shift   ;;
        -h|--help)     usage ;;
        *)
            fail "Unknown option: $1"
            echo -e "   ${D}Run ./buildAll.sh --help for usage${N}"
            exit 1 ;;
    esac
done

# ── Banner ───────────────────────────────────────────────────
clear
echo ""
echo -e "   ${D}┌──────────────────────────────────────┐${N}"
echo -e "   ${D}│${N}  ${B}🚄 MTRFRA Build System${N}  ${D}v${MOD_VERSION}${N}      ${D}│${N}"
echo -e "   ${D}└──────────────────────────────────────┘${N}"

# ── 1. Loader ────────────────────────────────────────────────
step "1/4  Loader"

if $INTERACTIVE; then
    echo -e "   ${W}1${N} ${D})${N}  Fabric"
    echo -e "   ${W}2${N} ${D})${N}  Forge"
    echo -e "   ${W}3${N} ${D})${N}  Fabric + Forge"
    echo ""
    read -rp "   choix [1/2/3] (default: 3) > " LOADER_CHOICE
    LOADER_CHOICE=${LOADER_CHOICE:-3}

    case $LOADER_CHOICE in
        1) BUILD_FABRIC=true;  BUILD_FORGE=false; ok "Fabric" ;;
        2) BUILD_FABRIC=false; BUILD_FORGE=true;  ok "Forge"  ;;
        *) BUILD_FABRIC=true;  BUILD_FORGE=true;  ok "Fabric + Forge" ;;
    esac
else
    case ${ARG_LOADER:-both} in
        fabric) BUILD_FABRIC=true;  BUILD_FORGE=false; ok "Fabric" ;;
        forge)  BUILD_FABRIC=false; BUILD_FORGE=true;  ok "Forge"  ;;
        both|"") BUILD_FABRIC=true; BUILD_FORGE=true;  ok "Fabric + Forge" ;;
        *)
            fail "Invalid loader: ${ARG_LOADER}  (use: fabric, forge, both)"
            exit 1 ;;
    esac
fi

# ── 2. Versions ──────────────────────────────────────────────
step "2/4  Versions"

if $INTERACTIVE; then
    for i in "${!ALL_VERSIONS[@]}"; do
        echo -e "   ${W}$((i+1))${N} ${D})${N}  ${ALL_VERSIONS[$i]}"
    done
    echo ""
    echo -e "   ${D}Enter = all · space-separated numbers (e.g. 1 4 5)${N}"
    read -rp "   versions > " VERSION_INPUT

    SELECTED_VERSIONS=()
    if [ -z "$VERSION_INPUT" ]; then
        SELECTED_VERSIONS=("${ALL_VERSIONS[@]}")
        ok "All (${#ALL_VERSIONS[@]})"
    else
        for NUM in $VERSION_INPUT; do
            IDX=$((NUM - 1))
            if [ "$IDX" -ge 0 ] 2>/dev/null && [ "$IDX" -lt "${#ALL_VERSIONS[@]}" ]; then
                SELECTED_VERSIONS+=("${ALL_VERSIONS[$IDX]}")
            fi
        done
        if [ ${#SELECTED_VERSIONS[@]} -eq 0 ]; then
            fail "No valid version selected"; exit 1
        fi
        ok "${SELECTED_VERSIONS[*]}"
    fi
else
    SELECTED_VERSIONS=()
    if [ -z "$ARG_VERSIONS" ]; then
        SELECTED_VERSIONS=("${ALL_VERSIONS[@]}")
        ok "All (${#ALL_VERSIONS[@]})"
    else
        IFS=',' read -ra REQUESTED <<< "$ARG_VERSIONS"
        for V in "${REQUESTED[@]}"; do
            V=$(echo "$V" | xargs)  # trim whitespace
            FOUND=false
            for AV in "${ALL_VERSIONS[@]}"; do
                if [ "$V" == "$AV" ]; then
                    SELECTED_VERSIONS+=("$V")
                    FOUND=true
                    break
                fi
            done
            if ! $FOUND; then
                warn "Unknown version skipped: ${V}"
            fi
        done
        if [ ${#SELECTED_VERSIONS[@]} -eq 0 ]; then
            fail "No valid version selected"; exit 1
        fi
        ok "${SELECTED_VERSIONS[*]}"
    fi
fi

# ── 3. Suffix ────────────────────────────────────────────────
step "3/4  Suffix"

if $INTERACTIVE; then
    echo -e "   ${D}Appended to filename (e.g. hotfix3, rc1, beta1)${N}"
    read -rp "   suffix (empty = none) > " SUFFIX
else
    SUFFIX="$ARG_SUFFIX"
fi

if [ -n "$SUFFIX" ]; then
    SUFFIX_TAG="-${SUFFIX}"
    ok "Suffix: ${SUFFIX_TAG}"
else
    SUFFIX_TAG=""
    ok "No suffix"
fi

# ── 4. Summary ───────────────────────────────────────────────
LOADER_LABEL=""
LOADER_COUNT=0
if $BUILD_FABRIC; then LOADER_LABEL+="Fabric "; LOADER_COUNT=$((LOADER_COUNT+1)); fi
if $BUILD_FORGE;  then LOADER_LABEL+="Forge";   LOADER_COUNT=$((LOADER_COUNT+1)); fi

step "4/4  Summary"
info "Version   ${B}${MOD_VERSION}${SUFFIX_TAG}${N}"
info "Loaders   ${B}${LOADER_LABEL}${N}"
info "MC        ${B}${SELECTED_VERSIONS[*]}${N}"
info "Builds    ${B}$(( ${#SELECTED_VERSIONS[@]} * LOADER_COUNT ))${N} JAR(s)"
echo ""

if ! $ARG_YES; then
    read -rp "   Launch? [Y/n] > " CONFIRM
    if [[ "$CONFIRM" =~ ^[nN] ]]; then
        fail "Cancelled."; exit 0
    fi
fi

# ── Global timer ─────────────────────────────────────────────
GLOBAL_START=$(date +%s)

# ── Preparation ──────────────────────────────────────────────
step "Preparation"
warn "setupFiles..."
./gradlew setupFiles > /dev/null 2>&1
ok "Common code copied (Fabric -> Forge)"

warn "Cleaning..."
./gradlew clean > /dev/null 2>&1
ok "Workspace clean"

mkdir -p "$RELEASE_DIR"

# Backup original before loop
cp gradle.properties gradle.properties.orig

# ── Pack format ──────────────────────────────────────────────
get_pack_format() {
    case $1 in
        "1.16.5") echo 6  ;; "1.17.1") echo 7  ;;
        "1.18.2") echo 8  ;; "1.19.2") echo 9  ;;
        "1.19.4") echo 12 ;; "1.20.1") echo 15 ;;
        "1.20.4") echo 22 ;; *)        echo 15 ;;
    esac
}

# ── Find and copy JAR ───────────────────────────────────────
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
        fail "${LOADER^} — JAR not found for ${MC_VERSION}"
        return 1
    fi
}

# ── Build loop ───────────────────────────────────────────────
SUCCESS=0
FAIL=0
TOTAL=${#SELECTED_VERSIONS[@]}

for i in "${!SELECTED_VERSIONS[@]}"; do
    VERSION="${SELECTED_VERSIONS[$i]}"
    PACK_FORMAT=$(get_pack_format "$VERSION")

    STEP_START=$(date +%s)

    step "Build $((i+1))/${TOTAL}  ·  Minecraft ${VERSION}"

    # Patch pack.mcmeta + gradle.properties
    find . -type f -name "pack.mcmeta" -exec sed -i.bak "s/\"pack_format\": [0-9]*/\"pack_format\": $PACK_FORMAT/" {} +
    sed -i.bak "s/minecraft_version=.*/minecraft_version=$VERSION/" gradle.properties

    # Cleanup .bak files
    find . -type f -name "pack.mcmeta.bak" -delete
    rm -f gradle.properties.bak

    echo -e "   ${D}>> Launching Gradle...${N}"

    if ./gradlew build -Pmod_suffix="${SUFFIX}" --no-configuration-cache --console=rich; then
        STEP_END=$(date +%s)
        STEP_TIME=$((STEP_END - STEP_START))

        echo ""
        ok "Compilation finished in ${B}$(format_time $STEP_TIME)${N}"

        if $BUILD_FABRIC; then collect_jar "fabric" "$VERSION" || true; fi
        if $BUILD_FORGE;  then collect_jar "forge"  "$VERSION" || true; fi
        SUCCESS=$((SUCCESS+1))
    else
        STEP_END=$(date +%s)
        echo ""
        fail "Gradle build failed (after $(format_time $((STEP_END - STEP_START))))"
        FAIL=$((FAIL+1))
    fi
done

# ── Restore gradle.properties ────────────────────────────────
mv gradle.properties.orig gradle.properties

# ── Results ──────────────────────────────────────────────────
GLOBAL_END=$(date +%s)
TOTAL_TIME=$((GLOBAL_END - GLOBAL_START))

echo ""
echo -e "   ${D}┌──────────────────────────────────────┐${N}"
echo -e "   ${D}│${N}  ${B}Build finished in $(format_time $TOTAL_TIME)${N}            ${D}│${N}"
echo -e "   ${D}└──────────────────────────────────────┘${N}"
echo ""

if [ "$FAIL" -eq 0 ]; then
    ok "${G}${SUCCESS}/${TOTAL} builds succeeded${N}"
else
    ok "${SUCCESS} succeeded"
    fail "${FAIL} failed"
fi

echo ""
info "Contents of ${RELEASE_DIR}/ :"
echo ""
if ls "${RELEASE_DIR}"/*.jar 1>/dev/null 2>&1; then
    ls -1 "${RELEASE_DIR}"/*.jar | while read -r f; do
        SIZE=$(du -h "$f" | cut -f1)
        echo -e "      ${G}✔${N}  $(basename "$f")  ${D}${SIZE}${N}"
    done
else
    warn "No JARs in ${RELEASE_DIR}/"
fi
echo ""
