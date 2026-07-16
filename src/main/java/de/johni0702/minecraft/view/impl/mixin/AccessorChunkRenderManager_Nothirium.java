package de.johni0702.minecraft.view.impl.mixin;

import meldexun.nothirium.api.renderer.chunk.IChunkRenderer;
import meldexun.nothirium.api.renderer.chunk.IRenderChunkDispatcher;
import meldexun.nothirium.api.renderer.chunk.IRenderChunkProvider;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChunkRenderManager.class, remap = false)
public interface AccessorChunkRenderManager_Nothirium {
    @Accessor("chunkRenderer")
    static IChunkRenderer<?> betterportals$getRenderer() {
        throw new AssertionError();
    }

    @Accessor("chunkRenderer")
    static void betterportals$setRenderer(IChunkRenderer<?> renderer) {
        throw new AssertionError();
    }

    @Accessor("renderChunkProvider")
    static IRenderChunkProvider<?> betterportals$getProvider() {
        throw new AssertionError();
    }

    @Accessor("renderChunkProvider")
    static void betterportals$setProvider(IRenderChunkProvider<?> provider) {
        throw new AssertionError();
    }

    @Accessor("taskDispatcher")
    static IRenderChunkDispatcher betterportals$getDispatcher() {
        throw new AssertionError();
    }

    @Accessor("taskDispatcher")
    static void betterportals$setDispatcher(IRenderChunkDispatcher dispatcher) {
        throw new AssertionError();
    }
}
