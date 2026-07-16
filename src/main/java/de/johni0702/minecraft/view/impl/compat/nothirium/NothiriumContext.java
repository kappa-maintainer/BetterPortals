package de.johni0702.minecraft.view.impl.compat.nothirium;

import meldexun.nothirium.api.renderer.chunk.IChunkRenderer;
import meldexun.nothirium.api.renderer.chunk.IRenderChunkDispatcher;
import meldexun.nothirium.api.renderer.chunk.IRenderChunkProvider;

public final class NothiriumContext {
    public IChunkRenderer<?> renderer;
    public IRenderChunkProvider<?> provider;
    public IRenderChunkDispatcher dispatcher;
}
