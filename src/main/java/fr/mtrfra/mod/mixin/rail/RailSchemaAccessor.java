package fr.mtrfra.mod.mixin.rail;

import org.mtr.core.data.Rail;
import org.mtr.core.generated.data.RailSchema;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RailSchema.class, remap = false)
public interface RailSchemaAccessor {

    @Accessor("shape")
    Rail.Shape mtrfra$getShape();

}
