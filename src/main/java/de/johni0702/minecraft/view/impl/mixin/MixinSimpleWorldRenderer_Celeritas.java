package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.client.render.ChunkVisibilityDetail;
import de.johni0702.minecraft.view.client.render.RenderPass;
import de.johni0702.minecraft.betterportals.client.render.PortalDetail;
import de.johni0702.minecraft.view.impl.compat.celeritas.CeleritasTerrainBootstrapState;
import de.johni0702.minecraft.view.impl.compat.celeritas.CeleritasTerrainDetail;
import de.johni0702.minecraft.view.impl.compat.celeritas.CeleritasViewportExt;
import de.johni0702.minecraft.view.impl.compat.celeritas.CeleritasPassClassifier;
import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.terrain.SimpleWorldRenderer;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Restores portal visibility state in Celeritas' native terrain setup method. */
@Mixin(value = SimpleWorldRenderer.class, remap = false)
public abstract class MixinSimpleWorldRenderer_Celeritas {
    @Unique private static final String betterportals$setupTerrainMethod = "setupTerrain(Lorg/embeddedt/embeddium/impl/render/viewport/Viewport;Lorg/embeddedt/embeddium/impl/render/terrain/SimpleWorldRenderer$CameraState;IZZ)V";
    @Unique private static final Logger betterportals$logger = LogManager.getLogger("betterportals/view");
    @Shadow protected RenderSectionManager renderSectionManager;

    @Unique private RenderSectionManager betterportals$bootstrapManager;
    @Unique private boolean betterportals$bootstrapDisabled;
    @Unique private Set<BlockPos> betterportals$pumpedOrigins;
    @Unique private Map<BlockPos, CeleritasTerrainBootstrapState> betterportals$originStates;
    @Unique private BlockPos betterportals$currentOrigin;
    @Unique private int betterportals$lastGeneration = Integer.MIN_VALUE;
    @Unique private int betterportals$lastOriginX = Integer.MIN_VALUE;
    @Unique private int betterportals$lastOriginY = Integer.MIN_VALUE;
    @Unique private int betterportals$lastOriginZ = Integer.MIN_VALUE;

    @ModifyVariable(method = betterportals$setupTerrainMethod, at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private Viewport betterportals$setCeleritasVisibilityOrigin(Viewport viewport) {
        betterportals$resetForNewManager();
        RenderPass current = CeleritasPassClassifier.currentSyntheticPass();
        BlockPos origin = current == null ? null : current.get(ChunkVisibilityDetail.class).getOrigin();
        int x;
        int y;
        int z;
        if (origin == null) {
            x = viewport.getChunkCoord().x();
            y = viewport.getChunkCoord().y();
            z = viewport.getChunkCoord().z();
            this.betterportals$currentOrigin = null;
        } else {
            ((CeleritasViewportExt) (Object) viewport).betterportals$setVisibilityOrigin(origin);
            x = Math.floorDiv(origin.getX(), 16);
            y = Math.floorDiv(origin.getY(), 16);
            z = Math.floorDiv(origin.getZ(), 16);
            this.betterportals$currentOrigin = new BlockPos(x, y, z);
            CeleritasTerrainBootstrapState state = this.betterportals$originStates.computeIfAbsent(
                    this.betterportals$currentOrigin, ignored -> new CeleritasTerrainBootstrapState());
            state.incrementAttempts();
            if (CeleritasPassClassifier.isPortalChild(current)) {
                current.set(CeleritasTerrainDetail.class, new CeleritasTerrainDetail(state.isReady(), state.isTimedOut()));
            }
        }
        if (x != this.betterportals$lastOriginX || y != this.betterportals$lastOriginY || z != this.betterportals$lastOriginZ) {
            this.renderSectionManager.markGraphDirty();
            this.betterportals$lastOriginX = x;
            this.betterportals$lastOriginY = y;
            this.betterportals$lastOriginZ = z;
        }
        return viewport;
    }

    @ModifyVariable(method = betterportals$setupTerrainMethod, at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private int betterportals$uniqueCeleritasGraphGeneration(int vanillaFrame) {
        betterportals$resetForNewManager();
        if (this.betterportals$lastGeneration == Integer.MIN_VALUE) {
            this.betterportals$lastGeneration = vanillaFrame;
        } else if (vanillaFrame > this.betterportals$lastGeneration) {
            this.betterportals$lastGeneration = vanillaFrame;
        } else if (this.betterportals$lastGeneration < Integer.MAX_VALUE) {
            this.betterportals$lastGeneration++;
        }
        return this.betterportals$lastGeneration;
    }

    @Inject(
            method = betterportals$setupTerrainMethod,
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/render/chunk/RenderSectionManager;update(Lorg/embeddedt/embeddium/impl/render/viewport/Viewport;IZ)V",
                    shift = At.Shift.AFTER),
            require = 1)
    private void betterportals$dispatchFreshPortalGraph(
            Viewport viewport,
            SimpleWorldRenderer.CameraState cameraState,
            int frame,
            boolean spectator,
            boolean updateChunksImmediately,
            CallbackInfo ci) {
        RenderPass current = ViewRenderManager.Companion.getINSTANCE().getCurrent();
        if (current == null) {
            return;
        }

        this.betterportals$resetForNewManager();
        RenderSectionManager manager = this.renderSectionManager;
        BlockPos origin = this.betterportals$currentOrigin;
        // Multiple passes can share one Celeritas renderer. Commit this pass's graph before drawing it instead of
        // using the result from a recursive pass in the same world while the new search is still running.
        manager.finishAllGraphUpdates();
        if (this.betterportals$bootstrapDisabled || origin == null
                || this.betterportals$pumpedOrigins.contains(origin) || manager.getTotalSections() == 0) {
            return;
        }

        long started = System.nanoTime();
        try {
            manager.updateChunks(false);
            manager.uploadChunks();
            this.betterportals$pumpedOrigins.add(origin);
            betterportals$logger.debug(
                    "[CeleritasBootstrap] manager={} sections={} graphDispatchUs={}",
                    Integer.toHexString(System.identityHashCode(manager)),
                    manager.getTotalSections(),
                    (System.nanoTime() - started) / 1_000L);
        } catch (RuntimeException e) {
            this.betterportals$bootstrapDisabled = true;
            betterportals$logger.warn("Disabling Celeritas portal terrain bootstrap for this renderer", e);
        }
    }

    @Inject(method = betterportals$setupTerrainMethod, at = @At("RETURN"), require = 1)
    private void betterportals$updatePortalTerrainReadiness(
            Viewport viewport,
            SimpleWorldRenderer.CameraState cameraState,
            int frame,
            boolean spectator,
            boolean updateChunksImmediately,
            CallbackInfo ci) {
        RenderPass current = CeleritasPassClassifier.currentSyntheticPass();
        BlockPos origin = this.betterportals$currentOrigin;
        if (!CeleritasPassClassifier.isPortalChild(current) || origin == null) {
            return;
        }

        CeleritasTerrainBootstrapState state = this.betterportals$originStates.get(origin);
        if (state == null) {
            return;
        }

        state.observeBuilt(this.renderSectionManager.isSectionBuilt(origin.getX(), origin.getY(), origin.getZ()));
        if (!state.isReady() && !state.isTimedOut()
                && (state.getAttempts() >= 30 || System.nanoTime() - state.getStartedNanos() >= 2_000_000_000L)) {
            state.setTimedOut();
            betterportals$logger.warn(
                    "[CeleritasBootstrap] Timed out waiting for portal terrain manager={} origin={} attempts={}",
                    Integer.toHexString(System.identityHashCode(this.renderSectionManager)), origin, state.getAttempts());
        }
        current.set(CeleritasTerrainDetail.class, new CeleritasTerrainDetail(state.isReady(), state.isTimedOut()));
    }

    @Unique
    private void betterportals$resetForNewManager() {
        RenderSectionManager manager = this.renderSectionManager;
        if (manager != this.betterportals$bootstrapManager) {
            this.betterportals$bootstrapManager = manager;
            this.betterportals$bootstrapDisabled = false;
            this.betterportals$pumpedOrigins = new HashSet<>();
            this.betterportals$originStates = new HashMap<>();
            this.betterportals$currentOrigin = null;
            this.betterportals$lastGeneration = Integer.MIN_VALUE;
            this.betterportals$lastOriginX = Integer.MIN_VALUE;
            this.betterportals$lastOriginY = Integer.MIN_VALUE;
            this.betterportals$lastOriginZ = Integer.MIN_VALUE;
        }
    }

}
