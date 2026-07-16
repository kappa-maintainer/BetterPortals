package de.johni0702.minecraft.view.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.embeddium.impl.gl.shader.ShaderType;
import org.embeddedt.embeddium.impl.render.chunk.ShaderChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Celeritas' core-profile terrain shader omits the compatibility clip vertex required by BetterPortals' remote-world
 * clipping plane. Keep the terrain on the portal-visible side without affecting its fragment or cloud shaders.
 */
@Mixin(ShaderChunkRenderer.class)
public abstract class MixinShaderChunkRenderer_Celeritas {
    @Unique private static final Logger betterportals$logger = LogManager.getLogger("betterportals/view");
    @Unique private static boolean betterportals$reportedClipSupport;

    @ModifyExpressionValue(
            method = "loadShader",
            at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/gl/shader/ShaderParser;parseShader(Ljava/lang/String;Ljava/util/function/Function;Lorg/embeddedt/embeddium/impl/gl/shader/ShaderConstants;)Ljava/lang/String;"),
            remap = false,
            require = 1)
    private String betterportals$enablePortalClipping(
            String source,
            @Local(argsOnly = true) ShaderType type,
            @Local(argsOnly = true) String path) {
        if (type != ShaderType.VERTEX || !"sodium:blocks/block_layer_opaque.vsh".equals(path)) {
            return source;
        }

        String compatibilitySource = source.replaceFirst("(?m)^#version\\s+330\\s+core\\s*$", "#version 330 compatibility");
        if (compatibilitySource.equals(source)) {
            betterportals$logger.warn("Unable to enable BetterPortals clipping in the Celeritas terrain vertex shader: unsupported GLSL version");
            return source;
        }
        String clippedSource = compatibilitySource.replaceFirst(
                "gl_Position\\s*=\\s*u_ProjectionMatrix\\s*\\*\\s*u_ModelViewMatrix\\s*\\*\\s*vec4\\(position,\\s*1\\.0\\)\\s*;",
                "vec4 betterportals_viewPos = u_ModelViewMatrix * vec4(position, 1.0);\n"
                        + "    gl_ClipVertex = betterportals_viewPos;\n"
                        + "    gl_Position = u_ProjectionMatrix * betterportals_viewPos;");

        if (clippedSource.equals(compatibilitySource)) {
            betterportals$logger.warn("Unable to enable BetterPortals clipping in the Celeritas terrain vertex shader: position transform not found");
            return source;
        } else if (!betterportals$reportedClipSupport) {
            betterportals$reportedClipSupport = true;
            betterportals$logger.info("Enabled BetterPortals clipping in the Celeritas terrain vertex shader");
        }
        return clippedSource;
    }
}
