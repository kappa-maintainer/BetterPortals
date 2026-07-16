package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.betterportals.impl.client.renderer.PortalCamera;
import de.johni0702.minecraft.view.client.render.RenderPass;
import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;
import net.minecraft.client.renderer.culling.Frustum;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.embeddedt.embeddium.impl.render.viewport.ViewportProvider;
import org.embeddedt.embeddium.impl.shadow.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Celeritas uses frustum results to traverse its terrain graph. PortalCamera's aperture planes become an extremely
 * narrow wedge near the portal plane and can prevent traversal to otherwise visible sections, so terrain uses the
 * transformed ordinary frustum while the portal surface and GL clip plane provide the final clipping.
 */
@Mixin(PortalCamera.class)
@Implements(@Interface(iface = ViewportProvider.class, prefix = "celeritas$"))
public abstract class MixinPortalCamera_Celeritas {
    @Shadow @Final private Frustum inner;
    @Unique private double celeritas$cameraX;
    @Unique private double celeritas$cameraY;
    @Unique private double celeritas$cameraZ;

    @Inject(method = "setPosition", at = @At("RETURN"))
    private void betterportals$capturePosition(double x, double y, double z, CallbackInfo ci) {
        this.celeritas$cameraX = x;
        this.celeritas$cameraY = y;
        this.celeritas$cameraZ = z;
    }

    public Viewport celeritas$sodium$createViewport() {
        RenderPass renderPass = ViewRenderManager.Companion.getINSTANCE().getCurrent();
        double cameraX = renderPass != null ? renderPass.getCamera().getViewPosition().x : this.celeritas$cameraX;
        double cameraY = renderPass != null ? renderPass.getCamera().getViewPosition().y : this.celeritas$cameraY;
        double cameraZ = renderPass != null ? renderPass.getCamera().getViewPosition().z : this.celeritas$cameraZ;
        return new Viewport((minX, minY, minZ, maxX, maxY, maxZ) -> this.inner.isBoxInFrustum(
                minX + cameraX,
                minY + cameraY,
                minZ + cameraZ,
                maxX + cameraX,
                maxY + cameraY,
                maxZ + cameraZ),
                new Vector3d(cameraX, cameraY, cameraZ));
    }
}
