include(Resources.id("aca:components/deprecated_pids.js"));

function create(ctx, state, pids) { DeprecatedPids.create(state); }
function dispose(ctx, state, pids) {}
function render(ctx, state, pids) { DeprecatedPids.render(ctx, state, pids, "SNCF_STATION_ARRIVALS", "CATI Departure Board", "-as"); }
