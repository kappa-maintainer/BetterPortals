package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.client.render.RenderPass;
import de.johni0702.minecraft.view.client.render.TransformedFrustum;
import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;
import org.embeddedt.embeddium.impl.render.viewport.Viewport;
import org.embeddedt.embeddium.impl.render.viewport.ViewportProvider;
import org.embeddedt.embeddium.impl.shadow.joml.Vector3d;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransformedFrustum.class)
@Implements(@Interface(iface = ViewportProvider.class, prefix = "celeritas$"))
public abstract class MixinTransformedFrustum_Celeritas {
    @Shadow
    public abstract boolean isBoxInFrustum(
            double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

    public Viewport celeritas$sodium$createViewport() {
        RenderPass current = ViewRenderManager.Companion.getINSTANCE().getCurrent();
        if (current == null) {
            throw new IllegalStateException("A transformed frustum requires an active render pass");
        }

        double cameraX = current.getCamera().getViewPosition().x;
        double cameraY = current.getCamera().getViewPosition().y;
        double cameraZ = current.getCamera().getViewPosition().z;
        return new Viewport((minX, minY, minZ, maxX, maxY, maxZ) -> this.isBoxInFrustum(
                minX + cameraX,
                minY + cameraY,
                minZ + cameraZ,
                maxX + cameraX,
                maxY + cameraY,
                maxZ + cameraZ),
                new Vector3d(cameraX, cameraY, cameraZ));
    }
}
