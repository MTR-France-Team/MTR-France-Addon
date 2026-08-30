const I18N_TABLE = {
    fr: { departure: "Prochain départ", arrival: "Prochaine arrivée", departures: "Départs", arrivals: "Arrivées", onTime: "À l'heure", delayed: "Retardé", track: "Voie", noTrain: "Aucun train prévu.", noDepartures: "Aucun départs prévus.", noArrivals: "Aucune arrivées prévues.", ledNoDeparture: "Aucun départ prévu.", departurePlanned: "Départ prévu", delayedAbout: "Retard {0} environ" },
    en: { departure: "Next departure", arrival: "Next arrival", departures: "Departures", arrivals: "Arrivals", onTime: "On time", delayed: "Delayed", track: "Track", noTrain: "No train scheduled.", noDepartures: "No departures scheduled.", noArrivals: "No arrivals scheduled.", ledNoDeparture: "No departure scheduled.", departurePlanned: "Departure scheduled", delayedAbout: "Delay of about {0}" },
    es: { departure: "Próxima salida", arrival: "Próxima llegada", departures: "Salidas", arrivals: "Llegadas", onTime: "A tiempo", delayed: "Retrasado", track: "Vía", noTrain: "Ningún tren previsto.", noDepartures: "Ninguna salida prevista.", noArrivals: "Ninguna llegada prevista.", ledNoDeparture: "Ninguna salida prevista.", departurePlanned: "Salida prevista", delayedAbout: "Retraso de aproximadamente {0}" },
    de: { departure: "Nächste Abfahrt", arrival: "Nächste Ankunft", departures: "Abfahrten", arrivals: "Ankünfte", onTime: "Pünktlich", delayed: "Verspätet", track: "Gleis", noTrain: "Kein Zug geplant.", noDepartures: "Keine Abfahrt geplant.", noArrivals: "Keine Ankunft geplant.", ledNoDeparture: "Keine Abfahrt geplant.", departurePlanned: "Abfahrt geplant", delayedAbout: "Verspätung von etwa {0}" },
    it: { departure: "Prossima partenza", arrival: "Prossimo arrivo", departures: "Partenze", arrivals: "Arrivi", onTime: "In orario", delayed: "In ritardo", track: "Binario", noTrain: "Nessun treno previsto.", noDepartures: "Nessuna partenza prevista.", noArrivals: "Nessun arrivo previsto.", ledNoDeparture: "Nessuna partenza prevista.", departurePlanned: "Partenza prevista", delayedAbout: "Ritardo di circa {0}" },
    nl: { departure: "Volgend vertrek", arrival: "Volgende aankomst", departures: "Vertrek", arrivals: "Aankomst", onTime: "Op tijd", delayed: "Vertraagd", track: "Spoor", noTrain: "Geen trein gepland.", noDepartures: "Geen vertrek gepland.", noArrivals: "Geen aankomst gepland.", ledNoDeparture: "Geen vertrek gepland.", departurePlanned: "Vertrek gepland", delayedAbout: "Vertraging van ongeveer {0}" },
    ar: { departure: "المغادرة القادمة", arrival: "الوصول القادم", departures: "المغادرات", arrivals: "الوصول", onTime: "في الموعد", delayed: "متأخر", track: "الرصيف", noTrain: "لا يوجد قطار مقرر.", noDepartures: "لا توجد مغادرة مقررة.", noArrivals: "لا يوجد وصول مقرر.", ledNoDeparture: "لا توجد مغادرة مقررة.", departurePlanned: "المغادرة المقررة", delayedAbout: "تأخير حوالي {0}" },
    jp: { departure: "次の発車", arrival: "次の到着", departures: "発車", arrivals: "到着", onTime: "定刻", delayed: "遅延", track: "番線", noTrain: "予定された列車はありません。", noDepartures: "予定された発車はありません。", noArrivals: "予定された到着はありません。", ledNoDeparture: "予定された発車はありません。", departurePlanned: "発車予定", delayedAbout: "約{0}の遅延" },
    kr: { departure: "다음 출발", arrival: "다음 도착", departures: "출발", arrivals: "도착", onTime: "정시", delayed: "지연", track: "승강장", noTrain: "예정된 열차가 없습니다.", noDepartures: "예정된 출발이 없습니다.", noArrivals: "예정된 도착이 없습니다.", ledNoDeparture: "예정된 출발이 없습니다.", departurePlanned: "출발 예정", delayedAbout: "약 {0} 지연" },
    cn: { departure: "下一班发车", arrival: "下一班到达", departures: "出发", arrivals: "到达", onTime: "准点", delayed: "晚点", track: "站台", noTrain: "没有预定的列车。", noDepartures: "没有预定的发车。", noArrivals: "没有预定的到达。", ledNoDeparture: "没有预定的发车。", departurePlanned: "预定发车", delayedAbout: "延误约{0}" }
};

const I18nHandler = {
    t(state, key, params) {
        const langConf = ConfigHandler.get(state, "locale");
        const lang = I18N_TABLE[langConf] ? langConf : "fr";
        const text = I18N_TABLE[lang][key];
        if (!params) return text;
        var result = text;
        for (var i = 0; i < params.length; i++) {
            result = result.replace("{" + i + "}", params[i]);
        }
        return result;
    }
};

if (typeof module !== "undefined" && module.exports) {
    module.exports = I18nHandler;
}
