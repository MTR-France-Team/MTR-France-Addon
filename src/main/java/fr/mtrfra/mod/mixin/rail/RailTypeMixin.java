package fr.mtrfra.mod.mixin.rail;

import fr.mtrfra.mod.Init;
import fr.mtrfra.mod.data.CustomRailData;
import org.mtr.core.data.Rail;
import org.mtr.mapping.holder.MapColor;
import org.mtr.mod.data.RailType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = RailType.class, remap = false)
public abstract class RailTypeMixin {

    @Shadow @Final @Mutable
    private static RailType[] $VALUES;

    @Invoker("<init>")
    private static RailType newRailType(String name, int ordinal, int speedLimit, MapColor mapColor, boolean isSavedRail, boolean canAccelerate, boolean hasSignal, Rail.Shape railShape) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomRailType(CallbackInfo cbI) {
        List<RailType> railTypes = new ArrayList<>(Arrays.asList($VALUES));
        int initialOrdinal = $VALUES.length;

        for (int i = 0; i < CustomRailData.CUSTOM_RAILS.size(); i++) {
            CustomRailData.RailInfo info = CustomRailData.CUSTOM_RAILS.get(i);

            for (RailType existing : railTypes) {
                if (existing.speedLimit == info.speed && existing != RailType.CABLE_CAR) {
                    Init.LOGGER.error(
                            "Rail speed collision: {} ({} km/h) matches the same speed as an existing rail type ({}). " +
                                    "RailType.getRailColor() will always return {}'s color for this speed, never {}'s — pick a different speed.",
                            info.name, info.speed, existing, existing, info.name
                    );
                }
            }

            railTypes.add(newRailType(
                    info.name,
                    initialOrdinal + i,
                    info.speed,
                    info.color,
                    false, true, true, Rail.Shape.QUADRATIC
            ));
        }

        $VALUES = railTypes.toArray(new RailType[0]);
    }

    @Inject(method = "getRailColor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fixCableCarColorCollision(Rail rail, CallbackInfoReturnable<Integer> cir) {
        if (rail.isPlatform() || rail.isSiding() || rail.canTurnBack() || rail.canConnectRemotely()) {
            return;
        }
        if (((RailSchemaAccessor) (Object) rail).mtrfra$getShape() == Rail.Shape.CABLE) {
            return;
        }

        final long speed = Math.max(rail.getSpeedLimitKilometersPerHour(false), rail.getSpeedLimitKilometersPerHour(true));
        if (speed != RailType.CABLE_CAR.speedLimit) {
            return;
        }

        for (RailType railType : RailType.values()) {
            if (railType != RailType.CABLE_CAR && railType.speedLimit == speed) {
                cir.setReturnValue(railType.color);
                return;
            }
        }
    }

}
