package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.compat.nothirium.NothiriumShaderCompat;
import meldexun.nothirium.api.renderer.chunk.ChunkRenderPass;
import meldexun.nothirium.mc.renderer.chunk.ChunkRendererGL43;
import meldexun.renderlib.util.GLShader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRendererGL43.class, remap = false)
public abstract class MixinChunkRendererGL43_Nothirium {
    @Shadow @Final private GLShader shader;

    @Inject(
            method = "renderChunks",
            at = @At(value = "INVOKE", target = "Lmeldexun/renderlib/util/GLShader;use()V", shift = At.Shift.AFTER),
            require = 1)
    private void betterportals$uploadClipModelView(ChunkRenderPass pass, CallbackInfo ci) {
        NothiriumShaderCompat.uploadModelView(this.shader, true);
    }
}
