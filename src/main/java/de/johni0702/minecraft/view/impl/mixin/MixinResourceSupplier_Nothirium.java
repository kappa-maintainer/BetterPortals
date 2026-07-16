package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.ViewDebug;
import meldexun.nothirium.mc.util.ResourceSupplier;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResourceSupplier.class, remap = false)
public abstract class MixinResourceSupplier_Nothirium {
    @Unique private static final Logger betterportals$logger = LogManager.getLogger("betterportals/view");
    @Unique private static boolean betterportals$reportedClipSupport;
    @Shadow @Final private ResourceLocation file;

    @Inject(method = "get()Ljava/lang/String;", at = @At("RETURN"), cancellable = true, require = 1)
    private void betterportals$enablePortalClipping(CallbackInfoReturnable<String> cir) {
        if (!"nothirium".equals(this.file.getResourceDomain())
                || !"shaders/chunk_vert.glsl".equals(this.file.getResourcePath())) {
            return;
        }

        String source = cir.getReturnValue();
        String clippedSource = source.replace(
                "uniform mat4 u_ModelViewProjectionMatrix;",
                "uniform mat4 u_ModelViewProjectionMatrix;\nuniform mat4 u_ModelViewMatrix;")
                .replace(
                        "gl_Position = u_ModelViewProjectionMatrix * vec4(pos, 1.0);",
                        "gl_ClipVertex = u_ModelViewMatrix * vec4(pos, 1.0);\n"
                                + "    gl_Position = u_ModelViewProjectionMatrix * vec4(pos, 1.0);");
        if (clippedSource.equals(source) || !clippedSource.contains("gl_ClipVertex")) {
            betterportals$logger.warn("Unable to enable BetterPortals clipping in the Nothirium terrain vertex shader");
            return;
        }
        if (!betterportals$reportedClipSupport && ViewDebug.isEnabled()) {
            betterportals$reportedClipSupport = true;
            betterportals$logger.debug("Enabled BetterPortals clipping in the Nothirium terrain vertex shader");
        }
        cir.setReturnValue(clippedSource);
    }
}
