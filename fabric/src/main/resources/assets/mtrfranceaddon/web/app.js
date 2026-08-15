document.addEventListener('DOMContentLoaded', () => {
    const stationTitle = document.getElementById('stationTitle');
    const resultsList = document.getElementById('resultsList');
    const loadingState = document.getElementById('loadingState');
    const mtrPortInput = document.getElementById('mtrPortInput');

    // 1. Récupération des paramètres URL (?station=<stationIdHex>&dimension=0)
    const urlParams = new URLSearchParams(window.location.search);
    const stationHexId = urlParams.get('station');
    const dimension = urlParams.get('dimension') || '0';

    let mtrPort = localStorage.getItem('mtr_port') || '8888';
    mtrPortInput.value = mtrPort;

    // Dictionnaire de cache : routeId -> Route Data (Type, Nom, Couleur)
    let routesCache = {};
    let currentStationName = 'Station inconnue';

    // Si pas de station fournie
    if (!stationHexId) {
        loadingState.className = 'error-state';
        loadingState.textContent = 'Aucune station spécifiée dans l\'URL (ex: ?station=HEX_ID).';
        stationTitle.textContent = 'Erreur Station';
        return;
    }

    // Écouter le changement de port MTR
    mtrPortInput.addEventListener('change', (e) => {
        mtrPort = e.target.value;
        localStorage.setItem('mtr_port', mtrPort);
        init();
    });

    init();

    async function init() {
        loadingState.style.display = 'block';
        loadingState.className = 'loading-state';
        loadingState.textContent = 'Chargement du réseau MTR...';
        resultsList.innerHTML = '';

        try {
            // Étape A: Charger les données statiques (Stations & Routes) pour mapper les types de lignes
            await fetchStationsAndRoutes();

            // Étape B: Charger les arrivées réelles pour la station spécifiée
            await fetchArrivals();

            // Étape C: Rafraîchissement automatique toutes les 10 secondes
            setInterval(fetchArrivals, 10000);

        } catch (err) {
            loadingState.className = 'error-state';
            loadingState.textContent = `Erreur de connexion à MTR Core (port ${mtrPort}). Vérifiez que le serveur Minecraft est démarré.`;
            console.error(err);
        }
    }

    // Fetch GET /mtr/api/map/stations-and-routes
    async function fetchStationsAndRoutes() {
        const response = await fetch(`http://localhost:${mtrPort}/mtr/api/map/stations-and-routes?dimension=${dimension}`);
        if (!response.ok) throw new Error('Impossible de charger les routes MTR');

        const data = await response.json();

        // Résolution du nom de la station actuelle
        if (data.stations) {
            const station = data.stations.find(s => s.id === stationHexId || s.hexId === stationHexId);
            if (station) {
                currentStationName = station.name;
                stationTitle.textContent = currentStationName;
            }
        }

        // Indexation des routes pour la résolution rapide du type (train, bus, etc.)
        if (data.routes) {
            data.routes.forEach(route => {
                routesCache[route.id] = {
                    name: route.name,
                    color: route.color ? `#${Number(route.color).toString(16).padStart(6, '0')}` : '#0073AE',
                    type: route.type || 'train'
                };
            });
        }
    }

    // Fetch POST /mtr/api/map/arrivals
    async function fetchArrivals() {
        const response = await fetch(`http://localhost:${mtrPort}/mtr/api/map/arrivals?dimension=${dimension}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                stationIdsHex: [stationHexId],
                maxCountPerPlatform: 5,
                maxCountTotal: 20
            })
        });

        if (!response.ok) throw new Error('Erreur récupération des arrivées');

        const arrivals = await response.json();
        displayArrivals(arrivals);
    }

    // Affichage dynamique des arrivées
    function displayArrivals(arrivals) {
        loadingState.style.display = 'none';
        resultsList.innerHTML = '';

        if (!arrivals || arrivals.length === 0) {
            resultsList.innerHTML = '<li class="loading-state">Aucun train/transport prévu actuellement.</li>';
            return;
        }

        arrivals.forEach(item => {
            const routeInfo = routesCache[item.routeId] || {
                type: 'TER',
                color: item.routeColor ? `#${Number(item.routeColor).toString(16).padStart(6, '0')}` : '#0073AE'
            };

            // Calcul de l'heure ou temps d'attente
            const now = Date.now();
            const arrivalMs = item.arrival || item.departure;
            const diffMinutes = Math.round((arrivalMs - now) / 60000);

            let timeText = '';
            if (diffMinutes <= 0) {
                timeText = 'À quai';
            } else if (diffMinutes < 60) {
                timeText = `${diffMinutes} min`;
            } else {
                const date = new Date(arrivalMs);
                timeText = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
            }

            const li = document.createElement('li');
            li.className = 'trip-card';
            li.innerHTML = `
        <span class="trip-type" style="background-color: ${routeInfo.color}">
          ${routeInfo.type}
        </span>
        <div class="trip-destination">
          ${item.destination || 'Terminus'}
        </div>
        <div class="trip-platform">
          ${item.platformName ? 'Quai ' + item.platformName : ''}
        </div>
        <div class="trip-time">
          ${timeText}
        </div>
      `;
            resultsList.appendChild(li);
        });
    }
});