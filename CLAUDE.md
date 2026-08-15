# CLAUDE.md — MTR France Addon

Guide de contexte pour Claude Code sur ce dépôt. Lire ce fichier avant toute
modification.

## Vue d'ensemble

Addon Fabric (Minecraft) pour **Minecraft Transit Railway (MTR)**, ajoutant du
contenu français : matériel roulant SNCF/RATP/IDFM, signalétique, plateformes,
bornes de tickets, logos historiques, etc.

- **Loader** : Fabric uniquement (pas de Forge/NeoForge dans ce repo)
- **Dépendance principale** : `maven.modrinth:minecraft-transit-railway`
  (voir `fabric/build.gradle` et racine `build.gradle` pour la version exacte
  via `rootProject.mtr_version` / `rootProject.minecraft_version`)
- **Langue du dépôt** : commentaires et échanges en français, code en anglais
  (conventions Java standard)

## Architecture

```
MTR-France-Addon/
├── build.gradle                # config racine, versions MC/MTR
├── buildAll.sh                 # build multi-version
├── crowdin.yml                 # config traductions
└── fabric/
    ├── build.gradle            # deps Fabric + MTR
    ├── gradle.properties       # versions par MC (yarn, fabric-api, modmenu)
    └── src/main/
        ├── java/fr/mtrfranceaddon/
        │   ├── fabric/                          # entrypoints Fabric purs
        │   │   ├── MTRFranceAddonFabric.java         (ModInitializer)
        │   │   └── MTRFranceAddonFabricClient.java   (ClientModInitializer)
        │   └── mod/common/                      # logique partagée
        │       ├── Init.java                    # point d'entrée logique (init/initClient)
        │       ├── block/                       # blocs (logos, plateformes, bornes...)
        │       │   ├── base/                    # classes de base réutilisables
        │       │   ├── MTRSign/                 # signalétique (panneaux, girouettes)
        │       │   └── sign/                    # poteaux, connecteurs de panneaux
        │       ├── item/                        # items custom (modificateur de rail...)
        │       ├── data/                        # data blocs (BlockProperties, CustomRailData)
        │       ├── registry/                    # tout l'enregistrement MTR-mapping
        │       ├── util/Constants.java           # MOD_ID, MOD_NAME, MOD_VERSION
        │       └── mixin/                        # mixins sur les classes MTR Core
        └── resources/assets/
            ├── mtrfranceaddon/     # assets propres à l'addon (blockstates, models, lang, sons)
            ├── mtr/                # modèles de trains custom (bbmodel/obj) posés dans le
            │                       #   namespace `mtr` — convention MTR pour le matériel roulant
            ├── idf/, jsblock/      # scripts JS pour les écrans d'affichage IDF (SIEL, panneaux)
            └── aca/                # utilitaires JS partagés (train_utils, sncf_utils)
```

## API MTR Core (comment ça marche)

MTR Core expose une **couche d'API stable pour les addons** sous
`org.mtr.mapping.*` (holders, registry, mapper) — **toujours privilégier
cette API** plutôt que les classes internes `org.mtr.mod.*` /
`org.mtr.core.*`.

Exemples vus dans ce repo :
- `org.mtr.mapping.holder.Block`, `BlockPos`, `BlockState`, `Identifier`...
- `org.mtr.mapping.registry.BlockRegistryObject`, `BlockEntityTypeRegistryObject`
- `org.mtr.mapping.mapper.BlockEntityExtension`, `BlockEntityRenderer`, `BlockWithEntity`

On descend dans `org.mtr.mod.*` (classes internes, non garanties stables)
quand on a besoin de :
- hériter d'un bloc concret déjà présent côté MTR (ex.
  `org.mtr.mod.block.BlockTicketMachine`, `BlockPlatform`) plutôt que de
  tout réécrire
- accéder à des données internes (ex. `org.mtr.mod.data.ArrivalsCacheServer`,
  utilisé via un **mixin** dans `mixin/ArrivalsCacheServerMixin.java` pour
  augmenter le nombre max d'arrivées renvoyées)
- toucher au modèle de données bas niveau `org.mtr.core.operation.*`
  (requêtes/réponses d'arrivées de trains, etc.)

⚠️ **Toute utilisation de `org.mtr.mod.*` ou `org.mtr.core.*` est fragile
aux changements de version de MTR** — documenter pourquoi dans un
commentaire à chaque fois, et vérifier après une montée de version.

## Pattern d'enregistrement

Tout passe par `MTRFranceAddonRegistry` (commun) et
`MTRFranceAddonRegistryClient` (client). Ne jamais enregistrer un bloc/item
ailleurs que dans `registry/`.

```java
public static final BlockRegistryObject MON_BLOC = MTRFranceAddonRegistry.registerBlockWithItem(
        "mon_bloc_id",                                   // = nom de fichier blockstate/model/texture
        () -> new Block(new MaClasseDeBloc(settings)),
        ModItemGroups.MTRFranceAddonGroup                 // ou MTRSignAddonGroup pour la signalétique
);
```

Puis, si besoin d'un enregistrement supplémentaire (log, entrée dans une map),
l'ajouter dans `ModBlocks.register()` — **ne pas oublier d'appeler
`register()` depuis `Init.java` si un nouveau registre est créé**, sinon rien
ne s'enregistre.

- `ModBlockEntities` : uniquement si le bloc a un état/comportement dynamique
  (voir `InvisiblePlatform.InvPlatBE`)
- `ModBlockEntityRenderers` : uniquement si rendu custom côté client (voir
  `InvisiblePlatform.InvPlatRenderer`)
- `ModNetworking` : pour tout packet serveur↔client custom (actuellement vide,
  juste le squelette)

## Conventions de nommage assets

Le nom passé à `registerBlockWithItem("nom", ...)` doit correspondre
**exactement** (en minuscules) à :
- `resources/assets/mtrfranceaddon/blockstates/nom.json`
- `resources/assets/mtrfranceaddon/models/block/nom.json`
- `resources/assets/mtrfranceaddon/models/item/nom.json`
- clé `block.mtrfranceaddon.nom` dans chaque `lang/*.json`

Ce repo maintient des traductions dans ~30 langues (`lang/*.json`, géré via
Crowdin — voir `crowdin.yml`). **Pour une nouvelle fonctionnalité, ajouter au
minimum `en_us.json` et `fr_fr.json`** ; les autres langues sont mises à jour
via Crowdin, pas manuellement par Claude sauf demande explicite.

## Build

```bash
cd fabric && ../gradlew build      # build Fabric seul
./buildAll.sh                      # build toutes les versions MC supportées
```

Le jar final est copié automatiquement vers `../build/` (task `moveBuild`,
voir `fabric/build.gradle`).

## Style de code observé

- Classes de bloc simples : constructeur qui appelle `super(settings)`,
  override `getOutlineShape2` / `getRenderType2` si forme/rendu custom
  (suffixe `2` = convention MTR-mapping pour éviter les collisions de nom
  avec les méthodes vanilla mappées)
- Pas de Javadoc systématique, mais noms de variables/méthodes explicites
- Blocs de signalétique (`MTRSign/`, `sign/`) suivent un pattern quasi
  identique par famille (ground/gallows/pole) — copier-coller assumé plutôt
  que factorisation excessive, à respecter pour la cohérence sauf refactor
  demandé explicitement

## Contexte projet en cours : carte tactile (tactile map)

Objectif : un bloc cliquable qui ouvre un navigateur vers une webapp locale
affichant horaires/types de train en temps réel pour tous les quais d'une
station, backée par l'API HTTP **native** de MTR Core (pas de mixin sur
`ArrivalsCacheServer` nécessaire pour ce besoin — voir ci-dessous).

⚠️ Nom : ne pas confondre avec `org.mtr.mod.block.BlockTactileMap`, un bloc
déjà présent nativement dans MTR Core. Ce dernier n'a **rien à voir** avec
une carte web : son `onUse` déclenche uniquement une annonce vocale du nom
de la station (accessibilité malvoyants), sans navigateur ni webserver.
Notre bloc est un objet entièrement différent, à nommer sans collision
(`TactileMapBlock` reste correct côté addon car dans notre propre package/
namespace, mais garder cette nuance en tête pour la doc/traductions afin de
ne pas induire les joueurs en erreur sur la fonction du bloc).

### API MTR Core réellement disponible (vérifié par décompilation du jar
`FABRIC-4.0.3+1.16.5`, cache Loom `fabric/build/loom-cache/remapped_working/`)

- **Ne pas confondre avec** l'ancienne API HTTP documentée sur le wiki MTR
  (`/mtr:development:api_reference`, endpoints POST `update_data`,
  `delete_data`, `get_data`, `set_time`, etc.) : celle-ci est **retirée
  depuis la release stable 4.0.0** et n'existe plus en 4.0.3.
- L'API qui existe toujours : `org.mtr.core.servlet.SystemMapServlet`,
  montée sur `/mtr/api/map/*` par `org.mtr.core.Main`, lui-même instancié
  côté serveur dédié par `org.mtr.mod.Init` (donc disponible en contexte
  mod Fabric, pas seulement en standalone).
- **Port** : `Config.getServer().getWebserverPort()`, valeur par défaut
  **`8888`** (`ServerSchema.webserverPort = 8888L`), configurable côté
  serveur MTR. **Ne pas supposer le port 80.**
- CORS ouvert en dur côté serveur (`Access-Control-Allow-Origin: *` dans
  `ServletBase.sendResponse`) → la webapp peut faire des `fetch()` directs
  vers `http://localhost:<port>/mtr/api/map/...` sans proxy Java.

Endpoints utiles (tous sous `/mtr/api/map/`, `?dimension=N` en query param,
`N`=index de dimension, 0=overworld par défaut) :

| Endpoint | Méthode | Usage pour la carte tactile |
|---|---|---|
| `stations-and-routes` | GET/POST | Données statiques : stations (id hex, nom, couleur, zones), routes (id hex, nom, couleur, numéro, **`type`** = `route.getRouteTypeKey()` → type de transport, état circulaire, liste des stations desservies). Cache serveur 30s. À fetch une fois au chargement pour construire une table `routeId → type/nom/couleur`. |
| `arrivals` | POST | Horaires réels. Body JSON accepté : `{"stationIdsHex": ["<hex station id>"], "maxCountPerPlatform": N, "maxCountTotal": N}` (existe aussi `stationIds`/`platformIds`/`platformIdsHex` en variante numérique/hex). **`stationIdsHex` résout automatiquement tous les quais de la station côté serveur** (`ArrivalsRequest.iteratePlatformIds`) → répond exactement au besoin "tous les quais d'une station", pas besoin de lister les platform IDs nous-mêmes. |
| `departures` | GET/POST | Vue agrégée départs par siding, cache 3s — alternative si `arrivals` ne suffit pas. |

Champs de chaque arrivée renvoyée par `arrivals` (`ArrivalResponseSchema`) :
`destination`, `arrival`, `departure` (ms), `deviation`, `realtime`,
`departureIndex`, `isTerminating`, `routeId`, `routeName`, `routeNumber`,
`routeColor`, `circularState`, `platformId`, `platformName`, `cars[]`
(chaque `CarDetails` = `vehicleId` + `occupancy`).

**Type de train** : `ArrivalResponse` ne contient **pas** directement le
type de transport (train/bus/bateau/etc.) — c'est un champ de `Route`
(`routeTypeKey`), disponible via `stations-and-routes`. Côté JS : croiser
`arrival.routeId` avec la table construite depuis `stations-and-routes` au
chargement pour afficher le type. Le modèle de rame précis (ex. MP14, TGV
Duplex) n'est **pas** exposé par cette API (seulement un `vehicleId` brut
dans `cars[]`, sans résolution vers un nom de modèle) — hors scope pour le
MVP sauf demande explicite d'aller chercher ça côté `org.mtr.core.data.Vehicle`
par un autre biais.

### Architecture retenue

Pas de proxy/API Java côté addon : le JS de la webapp interroge directement
l'API MTR (`fetch` cross-origin, CORS déjà ouvert). Le seul rôle du serveur
embarqué dans l'addon est de **servir les fichiers statiques** de la webapp
(HTML/CSS/JS) — `TactileMapApiHandler.java` n'est donc plus nécessaire et
est retiré du plan.

Fichiers prévus :
- `mod/common/block/TactileMapBlock.java` — bloc cliquable, `onUse2` ouvre
  le navigateur système vers `http://localhost:<portAddon>/?station=<hexId>`
- `mod/common/webserver/TactileMapServer.java` — sert uniquement les
  fichiers statiques (`com.sun.net.httpserver.HttpServer`, lit depuis les
  ressources du mod/classpath, pas depuis le disque, pour fonctionner en
  jar buildé) ; le port de ce serveur est indépendant de celui de MTR Core
  (8888) — à choisir/allouer séparément (port libre dynamique recommandé,
  cf. `Init.findFreePort` côté MTR pour l'inspiration)
- edits : `ModBlocks.java`, `ModBlockEntities.java`, `Init.java`
- assets : `blockstates/`, `models/block/`, `models/item/`, `lang/en_us.json`,
  `lang/fr_fr.json`
- webapp : `resources/assets/mtrfranceaddon/web/{index.html,app.js,style.css}`
  — `app.js` fetch directement `http://localhost:8888/mtr/api/map/stations-and-routes`
  et `.../arrivals` (POST) ; le port MTR (8888 par défaut) doit être
  paramétrable côté webapp au cas où le serveur cible l'a changé en config.

**Association bloc ↔ station : détection automatique au clic**, en
réutilisant le mécanisme déjà présent dans MTR Core plutôt qu'en écrivant
une résolution maison :

```java
Station station = InitClient.findStation(blockPos); // org.mtr.mod.InitClient, public static
```

Résout par zone de station (`station.inArea(position)` sur les données
client déjà chargées via `MinecraftClientData`), pas par simple distance —
c'est le même mécanisme utilisé en interne par `BlockTactileMap` et
`BlockStationNameBase`. Si `null` (bloc posé hors de toute zone de station),
`onUse2` doit annoncer/afficher un message d'erreur au joueur plutôt que
d'ouvrir le navigateur. Le `hexId` de la `Station` retournée
(`station.getHexId()`) est directement ce qu'il faut passer en paramètre
`stationIdsHex` à l'API `arrivals`, et dans l'URL ouverte côté navigateur
(`?station=<hexId>`).

## Ce que Claude ne doit PAS faire sans demande explicite

- Ne pas modifier les fichiers sous `fabric/build/` ou `fabric/.gradle/`
  (caches générés, jamais committés)
- Ne pas toucher aux traductions autres que `en_us`/`fr_fr` sans demande
- Ne pas refactoriser en masse les blocs de signalétique existants
- Ne pas changer la version de MTR Core (`mtr_version`) sans confirmation