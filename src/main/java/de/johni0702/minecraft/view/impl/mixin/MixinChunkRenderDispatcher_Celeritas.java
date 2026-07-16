package de.johni0702.minecraft.view.impl.mixin;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderDispatcher.class)
public abstract class MixinChunkRenderDispatcher_Celeritas {
    @Redirect(
            method = "<init>(I)V",
            at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;availableProcessors()I")
    )
    private int betterportals$disableVanillaTerrainWorkers(Runtime runtime) {
        // A processor count of one selects vanilla's synchronous fallback without starting worker threads.
        return 1;
    }
}
