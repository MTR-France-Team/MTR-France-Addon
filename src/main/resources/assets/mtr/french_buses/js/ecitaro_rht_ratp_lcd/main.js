function create(ctx, state, vehicle) {
    VehicleScriptContext.setDataFetchMode("all")
}
function render(ctx, state, vehicle) {
    let current_route_stops = Vehicle.getThisRouteStops();
    current_route_stops.map(s => console.log(s.station.getName()))
}
function dispose(ctx, state, vehicle) {}