package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.compat.nothirium.NothiriumContext;
import de.johni0702.minecraft.view.impl.compat.nothirium.NothiriumRenderGlobalExt;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 2000)
public abstract class MixinRenderGlobal_Nothirium implements NothiriumRenderGlobalExt {
    @Unique private static NothiriumRenderGlobalExt betterportals$activeNothiriumOwner;
    @Unique private final NothiriumContext betterportals$nothiriumContext = new NothiriumContext();

    @Override
    public void betterportals$activateNothiriumContext() {
        if (betterportals$activeNothiriumOwner == this) {
            return;
        }

        if (betterportals$activeNothiriumOwner == null) {
            this.betterportals$captureNothiriumContext();
        } else {
            betterportals$activeNothiriumOwner.betterportals$captureNothiriumContext();
            AccessorChunkRenderManager_Nothirium.betterportals$setRenderer(this.betterportals$nothiriumContext.renderer);
            AccessorChunkRenderManager_Nothirium.betterportals$setProvider(this.betterportals$nothiriumContext.provider);
            AccessorChunkRenderManager_Nothirium.betterportals$setDispatcher(this.betterportals$nothiriumContext.dispatcher);
        }
        betterportals$activeNothiriumOwner = this;
    }

    @Inject(method = "setWorldAndLoadRenderers", at = @At("HEAD"))
    private void betterportals$activateBeforeWorldChange(WorldClient world, CallbackInfo ci) {
        this.betterportals$activateNothiriumContext();
    }

    @Inject(method = "markBlocksForUpdate", at = @At("HEAD"), cancellable = true)
    private void betterportals$ignoreUpdatesWithoutNothiriumContext(
            int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ,
            boolean updateImmediately,
            CallbackInfo ci) {
        if (ChunkRenderManager.getProvider() == null) {
            ci.cancel();
        }
    }

    @Override
    public void betterportals$captureNothiriumContext() {
        this.betterportals$nothiriumContext.renderer = AccessorChunkRenderManager_Nothirium.betterportals$getRenderer();
        this.betterportals$nothiriumContext.provider = AccessorChunkRenderManager_Nothirium.betterportals$getProvider();
        this.betterportals$nothiriumContext.dispatcher = AccessorChunkRenderManager_Nothirium.betterportals$getDispatcher();
    }
}
