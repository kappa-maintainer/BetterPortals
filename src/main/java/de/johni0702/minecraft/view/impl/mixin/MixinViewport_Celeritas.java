package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.compat.celeritas.CeleritasViewportExt;
import net.minecraft.util.math.BlockPos;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.embeddedt.embeddium.impl.shadow.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Celeritas uses one position for both camera-relative culling and its occlusion graph. Portal passes need the
 * former to remain the physical camera while starting the graph at the remote portal.
 */
@Mixin(value = Viewport.class, remap = false)
public abstract class MixinViewport_Celeritas implements CeleritasViewportExt {
    @Mutable @Shadow @Final private Vector3i chunkCoords;

    @Override
    public void betterportals$setVisibilityOrigin(BlockPos origin) {
        this.chunkCoords = new Vector3i(
                Math.floorDiv(origin.getX(), 16),
                Math.floorDiv(origin.getY(), 16),
                Math.floorDiv(origin.getZ(), 16));
    }
}
