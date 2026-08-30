include(Resources.id("aca:modules/render_utils.js"));
include(Resources.id("aca:components/sncf_theme.js"));

const MODRINTH_PROJECT_URL = "https://api.modrinth.com/v2/project/sncf-pids";

const LANGUAGES = [
    {
        code: "fr", flag: "aca:images/flags/fr.png",
        title: "Mise à jour dispo",
        body1: "Ce PIDS a été remplacé par une nouvelle version.",
        body2: "Merci de sélectionner un nouveau preset dans le menu MTR."
    },
    {
        code: "en", flag: "aca:images/flags/en.png",
        title: "Update available",
        body1: "This PIDS has been replaced by a new version.",
        body2: "Please select a new preset from the MTR menu."
    },
    {
        code: "zh", flag: "aca:images/flags/zh.png",
        title: "有可用更新",
        body1: "此 PIDS 已被新版本取代。",
        body2: "请在 MTR 菜单中选择新的预设。"
    },
    {
        code: "ko", flag: "aca:images/flags/ko.png",
        title: "업데이트 사용 가능",
        body1: "이 PIDS는 새 버전으로 교체되었습니다.",
        body2: "MTR 메뉴에서 새 프리셋을 선택해 주세요."
    },
    {
        code: "ja", flag: "aca:images/flags/ja.png",
        title: "アップデートがあります",
        body1: "この PIDS は新しいバージョンに置き換えられました。",
        body2: "MTR メニューで新しいプリセットを選択してください。"
    }
];

const LANG_CYCLE_SECONDS = 4;
const TITLE_GREEN = 0x2ECC71;

const DeprecatedPids = {

    create(state) {
        state.modrinthInfo = null;
        state.modrinthFetchStarted = false;
        state.langTimer = 0;
        state.langIndex = 0;
    },

    render(ctx, state, pids, presetId, newPresetName, newPresetFlag) {
        if (!state.modrinthFetchStarted) {
            state.modrinthFetchStarted = true;
            BackgroundWorker.submit(function () {
                try {
                    const response = Networking.fetchString(MODRINTH_PROJECT_URL);
                    if (response.ok()) {
                        const project = JSON.parse(response.getData());
                        state.modrinthInfo = { updated: String(project.updated) };
                    } else {
                        state.modrinthInfo = { error: true };
                    }
                } catch (e) {
                    state.modrinthInfo = { error: true };
                }
            });
        }

        state.langTimer += Timing.delta();
        if (state.langTimer >= LANG_CYCLE_SECONDS) {
            state.langTimer -= LANG_CYCLE_SECONDS;
            state.langIndex = (state.langIndex + 1) % LANGUAGES.length;
        }
        const lang = LANGUAGES[state.langIndex];

        Texture.create("Background")
            .texture("aca:images/white.png")
            .color(0x1B1B1B)
            .pos(0, 0)
            .size(pids.width, pids.height)
            .zOrder(0)
            .draw(ctx);

        const flagSize = 8;
        Texture.create("Flag")
            .texture(lang.flag)
            .pos((pids.width / 2) - (flagSize / 2), pids.height * 0.06)
            .size(flagSize, flagSize)
            .zOrder(1)
            .draw(ctx);

        Text.create("Title")
            .text(lang.title)
            .color(TITLE_GREEN)
            .bold()
            .centerAlign()
            .scale(0.75)
            .pos(pids.width / 2, pids.height * 0.32)
            .zOrder(1)
            .draw(ctx);

        Text.create("Body 1")
            .text(lang.body1)
            .color(0xFFFFFF)
            .centerAlign()
            .scale(0.42)
            .pos(pids.width / 2, pids.height * 0.52)
            .zOrder(1)
            .draw(ctx);

        Text.create("Body 2")
            .text(lang.body2)
            .color(0xFFFFFF)
            .centerAlign()
            .scale(0.37)
            .pos(pids.width / 2, pids.height * 0.65)
            .zOrder(1)
            .draw(ctx);

        Text.create("Old Preset Info")
            .text("Old Preset: " + presetId)
            .color(0xAAAAAA)
            .centerAlign()
            .scale(0.24)
            .pos(pids.width / 2, pids.height * 0.74)
            .zOrder(1)
            .draw(ctx);

        Text.create("New Preset Info")
            .text("New Preset To Use: " + newPresetName + (newPresetFlag ? " (add " + newPresetFlag + ")" : ""))
            .color(0xAAAAAA)
            .centerAlign()
            .scale(0.24)
            .pos(pids.width / 2, pids.height * 0.81)
            .zOrder(1)
            .draw(ctx);

        if (state.modrinthInfo && state.modrinthInfo.updated) {
            Text.create("Modrinth Info")
                .text("Modrinth: " + state.modrinthInfo.updated.split("T")[0])
                .color(0xAAAAAA)
                .centerAlign()
                .scale(0.24)
                .pos(pids.width / 2, pids.height * 0.9)
                .zOrder(1)
                .draw(ctx);
        }

        SncfTheme.drawFrame(ctx, pids, false);
    }
};
