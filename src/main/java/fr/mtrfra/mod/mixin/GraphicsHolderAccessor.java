package fr.mtrfra.mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GraphicsHolder.class, remap = false)
public interface GraphicsHolderAccessor {

    @Accessor("matrixStack")
    PoseStack getMatrixStack();

    @Accessor("vertexConsumerProvider")
    MultiBufferSource getVertexConsumerProvider();

}
