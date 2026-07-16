package de.johni0702.minecraft.view.impl.compat.nothirium;

import de.johni0702.minecraft.betterportals.client.render.PortalDetail;
import de.johni0702.minecraft.betterportals.client.render.TransformedRootDetail;
import de.johni0702.minecraft.view.client.render.RenderPass;
import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;

public final class NothiriumPassClassifier {
    private NothiriumPassClassifier() {
    }

    public static RenderPass currentSyntheticPass() {
        RenderPass current = ViewRenderManager.Companion.getINSTANCE().getCurrent();
        return isSynthetic(current) ? current : null;
    }

    public static boolean isSynthetic(RenderPass pass) {
        return pass != null && (pass.getParent() != null
                || pass.get(PortalDetail.class) != null
                || pass.get(TransformedRootDetail.class) != null);
    }
}
