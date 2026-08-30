#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [[ -f .env ]]; then
    set -a
    source .env
    set +a
fi

usage() {
    cat <<'EOF'
Usage:
  ./build.sh                          everything: every version
  ./build.sh -v 1.20.4                 one Minecraft version
  ./build.sh -v 1.20.1 -v 1.20.4        several versions
  ./build.sh -l fabric                 explicit loader filter
  ./build.sh -l forge                  legacy-forge only
  ./build.sh -u                        also upload every built jar to Modrinth

Jars land in releases/. Requires JDK 21+ on PATH (or JAVA_HOME) for the
Stonecutter tree, and a JDK legacy-forge's ForgeGradle accepts (17 works
across 1.18.2-1.20.4) for the forge loader. On Windows, run via Git Bash:
sh build.sh

Uploading (-u) requires MODRINTH_TOKEN in the environment (a Modrinth
personal access token, never committed) and uses the current content of
CHANGELOG.md as the changelog text for every version it creates. Each
built jar becomes its own Modrinth version entry (one game_version, one
loader), sharing that same changelog and mod.version from gradle.properties.
EOF
}

ALL_VERSIONS=(1.18.2 1.19.2 1.19.4 1.20.1 1.20.4)
RELEASE_DIR="releases"
MODRINTH_PROJECT_ID="YJct9p8I"
MODRINTH_MTR_PROJECT_ID="XKPAmI6u"
MODRINTH_JCM_PROJECT_ID="lQqKCDhg"

loaders_for() {
    echo "fabric forge"
}

gradle_property() {
    sed -n "s/^$1=//p" gradle.properties | head -1
}

modrinth_version_type() {
    case "$1" in
        *alpha*) echo "alpha" ;;
        *beta*)  echo "beta" ;;
        *)       echo "release" ;;
    esac
}

upload_to_modrinth() {
    local jar="$1" mc="$2" loader="$3" mod_version="$4" mod_name="$5" changelog="$6"
    local version_number="${mod_version}+${mc}-${loader}"
    local filename
    filename="$(basename "$jar")"

    echo ">> Uploading ${version_number} to Modrinth (${mc} ${loader})..."
    local data
    data=$(MODRINTH_NAME="${mod_name} ${mod_version} (${mc} ${loader})" \
        MODRINTH_VERSION_NUMBER="$version_number" \
        MODRINTH_CHANGELOG="$changelog" \
        MODRINTH_VERSION_TYPE="$(modrinth_version_type "$mod_version")" \
        MODRINTH_GAME_VERSION="$mc" \
        MODRINTH_LOADER="$loader" \
        MODRINTH_PROJECT_ID="$MODRINTH_PROJECT_ID" \
        MODRINTH_MTR_PROJECT_ID="$MODRINTH_MTR_PROJECT_ID" \
        MODRINTH_JCM_PROJECT_ID="$MODRINTH_JCM_PROJECT_ID" \
        MODRINTH_FILENAME="$filename" \
        python3 -c '
import json
import os

print(json.dumps({
    "name": os.environ["MODRINTH_NAME"],
    "version_number": os.environ["MODRINTH_VERSION_NUMBER"],
    "changelog": os.environ["MODRINTH_CHANGELOG"],
    "dependencies": [
        {"project_id": os.environ["MODRINTH_MTR_PROJECT_ID"], "dependency_type": "required"},
        {"project_id": os.environ["MODRINTH_JCM_PROJECT_ID"], "dependency_type": "optional"},
    ],
    "game_versions": [os.environ["MODRINTH_GAME_VERSION"]],
    "version_type": os.environ["MODRINTH_VERSION_TYPE"],
    "loaders": [os.environ["MODRINTH_LOADER"]],
    "featured": False,
    "project_id": os.environ["MODRINTH_PROJECT_ID"],
    "file_parts": [os.environ["MODRINTH_FILENAME"]],
    "primary_file": os.environ["MODRINTH_FILENAME"],
}))
')

    local response
    response=$(curl -sS -X POST "https://api.modrinth.com/v2/version" \
        -H "Authorization: ${MODRINTH_TOKEN}" \
        -F "data=${data};type=application/json" \
        -F "${filename}=@${jar}")

    if MODRINTH_RESPONSE="$response" python3 -c '
import json
import os
import sys

sys.exit(0 if "id" in json.loads(os.environ["MODRINTH_RESPONSE"]) else 1)
' 2>/dev/null; then
        local created_id
        created_id=$(MODRINTH_RESPONSE="$response" python3 -c '
import json
import os

print(json.loads(os.environ["MODRINTH_RESPONSE"])["id"])
')
        echo ">> OK: Modrinth version ${created_id} created"
    else
        echo ">> ERROR uploading ${version_number}: $response"
        return 1
    fi
}

VERSIONS=()
LOADER_FILTER=""
UPLOAD=0
while [[ $# -gt 0 ]]; do
    case "$1" in
        -v|--version) VERSIONS+=("$2"); shift 2 ;;
        -l|--loader)  LOADER_FILTER="$2"; shift 2 ;;
        -u|--upload)  UPLOAD=1; shift ;;
        -h|--help)    usage; exit 0 ;;
        *) echo "Unknown option: $1 (see -h)"; exit 1 ;;
    esac
done
[[ ${#VERSIONS[@]} -eq 0 ]] && VERSIONS=("${ALL_VERSIONS[@]}")

if [[ "$UPLOAD" -eq 1 ]]; then
    [[ -z "${MODRINTH_TOKEN:-}" ]] && { echo "ERROR: -u/--upload needs MODRINTH_TOKEN set in the environment"; exit 1; }
    command -v python3 >/dev/null || { echo "ERROR: -u/--upload needs python3 on PATH"; exit 1; }
    [[ -f CHANGELOG.md ]] || { echo "ERROR: CHANGELOG.md not found"; exit 1; }
    MOD_VERSION=$(gradle_property "mod.version")
    MOD_NAME=$(gradle_property "mod.name")
    CHANGELOG_TEXT=$(cat CHANGELOG.md)
fi

mkdir -p "$RELEASE_DIR"

for MC in "${VERSIONS[@]}"; do
    LOADERS=$(loaders_for "$MC")

    if [[ -n "$LOADER_FILTER" ]]; then
        if [[ " $LOADERS " != *" $LOADER_FILTER "* ]]; then
            echo ">> $MC: loader '$LOADER_FILTER' does not apply here (available: $LOADERS) - skipping"
            continue
        fi
        LOADERS="$LOADER_FILTER"
    fi

    for LOADER in $LOADERS; do
        echo "=================================================="
        echo ">> Minecraft $MC - $LOADER"
        echo "=================================================="

        if [[ "$LOADER" == "forge" ]]; then
            (cd legacy-forge && ./gradlew build -Pminecraft_version="$MC" --no-daemon)
            JAR=$(find "legacy-forge/build/libs" -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" ! -name "*-dev.jar" | head -1)
        else
            ./gradlew ":${MC}-${LOADER}:build" --no-daemon
            JAR=$(find "versions/${MC}-${LOADER}/build/libs" -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" ! -name "*-dev.jar" | head -1)
        fi

        if [[ -z "$JAR" ]]; then
            echo ">> ERROR: no jar found for ${MC}-${LOADER}"
            exit 1
        fi
        cp "$JAR" "$RELEASE_DIR/"
        echo ">> OK: ${RELEASE_DIR}/$(basename "$JAR")"

        if [[ "$UPLOAD" -eq 1 ]]; then
            upload_to_modrinth "$RELEASE_DIR/$(basename "$JAR")" "$MC" "$LOADER" "$MOD_VERSION" "$MOD_NAME" "$CHANGELOG_TEXT"
        fi
    done
done

echo "=================================================="
echo "Done! Jars available in ${RELEASE_DIR}/"
echo "=================================================="
