package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.client.ViewEntity;
import meldexun.nothirium.mc.renderer.chunk.RenderChunkDispatcher;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderChunkDispatcher.class, remap = false)
public abstract class MixinRenderChunkDispatcher_Nothirium {
    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;availableProcessors()I"),
            require = 1)
    private int betterportals$limitAuxiliaryWorkers(Runtime runtime) {
        return Minecraft.getMinecraft().player instanceof ViewEntity ? 3 : runtime.availableProcessors();
    }
}
