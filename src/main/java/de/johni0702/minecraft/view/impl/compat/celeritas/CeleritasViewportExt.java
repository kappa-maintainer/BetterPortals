package de.johni0702.minecraft.view.impl.compat.celeritas;

import net.minecraft.util.math.BlockPos;

/** Exposes Celeritas' occlusion-graph origin independently from its camera transform. */
public interface CeleritasViewportExt {
    void betterportals$setVisibilityOrigin(BlockPos origin);
}
