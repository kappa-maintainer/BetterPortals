package de.johni0702.minecraft.view.impl.mixin;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_Celeritas {
    @Redirect(method = "loadRenderers", at = @At(value = "NEW", target = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher"))
    private ChunkRenderDispatcher betterportals$createMinimalDispatcher() {
        // Celeritas owns terrain rendering, so vanilla only needs a lifecycle-compatible placeholder.
        return new ChunkRenderDispatcher(1);
    }
}
