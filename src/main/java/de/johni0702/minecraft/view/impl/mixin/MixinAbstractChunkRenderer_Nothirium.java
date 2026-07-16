package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.client.render.ChunkVisibilityDetail;
import de.johni0702.minecraft.view.client.render.RenderPass;
import de.johni0702.minecraft.view.impl.compat.nothirium.NothiriumPassClassifier;
import meldexun.nothirium.api.renderer.chunk.IRenderChunk;
import meldexun.nothirium.renderer.chunk.AbstractChunkRenderer;
import meldexun.nothirium.util.Direction;
import meldexun.nothirium.util.math.MathUtil;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AbstractChunkRenderer.class, remap = false)
public abstract class MixinAbstractChunkRenderer_Nothirium {
    @Shadow protected abstract boolean isSpectator();

    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lmeldexun/nothirium/util/math/MathUtil;floor(D)I", ordinal = 0),
            require = 1)
    private int betterportals$usePortalRootX(double cameraX) {
        BlockPos origin = betterportals$getVisibilityOrigin();
        return origin == null ? MathUtil.floor(cameraX) : origin.getX();
    }

    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lmeldexun/nothirium/util/math/MathUtil;floor(D)I", ordinal = 1),
            require = 1)
    private int betterportals$usePortalRootY(double cameraY) {
        BlockPos origin = betterportals$getVisibilityOrigin();
        return origin == null ? MathUtil.floor(cameraY) : origin.getY();
    }

    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lmeldexun/nothirium/util/math/MathUtil;floor(D)I", ordinal = 2),
            require = 1)
    private int betterportals$usePortalRootZ(double cameraZ) {
        BlockPos origin = betterportals$getVisibilityOrigin();
        return origin == null ? MathUtil.floor(cameraZ) : origin.getZ();
    }

    @Redirect(
            method = "setup",
            at = @At(value = "INVOKE", target = "Lmeldexun/nothirium/renderer/chunk/AbstractChunkRenderer;isSpectator()Z"),
            require = 1)
    private boolean betterportals$disableSyntheticSectionConnectivity(AbstractChunkRenderer<?> renderer) {
        return this.isSpectator() || NothiriumPassClassifier.currentSyntheticPass() != null;
    }

    @Redirect(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lmeldexun/nothirium/util/Direction;isFaceCulled(Lmeldexun/nothirium/api/renderer/chunk/IRenderChunk;DDD)Z"),
            require = 1)
    private boolean betterportals$useVisibilityOriginForFaceCulling(
            Direction face,
            IRenderChunk neighbor,
            double cameraX,
            double cameraY,
            double cameraZ) {
        BlockPos origin = betterportals$getVisibilityOrigin();
        if (origin == null) {
            return face.isFaceCulled(neighbor, cameraX, cameraY, cameraZ);
        }
        return face.isFaceCulled(
                neighbor,
                origin.getX() + 0.5,
                origin.getY() + 0.5,
                origin.getZ() + 0.5);
    }

    private static BlockPos betterportals$getVisibilityOrigin() {
        RenderPass current = NothiriumPassClassifier.currentSyntheticPass();
        return current == null ? null : current.get(ChunkVisibilityDetail.class).getOrigin();
    }
}
