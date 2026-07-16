package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.taumc.celeritas.impl.render.terrain.CeleritasWorldRenderer;

@Mixin(value = CeleritasWorldRenderer.class, remap = false)
public abstract class MixinCeleritasWorldRenderer_Celeritas {
    @Inject(method = "getEffectiveRenderDistance", at = @At("HEAD"), cancellable = true, require = 1)
    private void betterportals$useAllocationRenderDistance(CallbackInfoReturnable<Integer> cir) {
        ViewRenderManager manager = ViewRenderManager.Companion.getINSTANCE();
        if (manager.getCurrent() != null) {
            // Portal passes publish a temporary visual distance, but Celeritas' persistent section manager must not
            // be resized for every recursive view.
            cir.setReturnValue(manager.getRealRenderDistanceChunks());
        }
    }
}
