package de.johni0702.minecraft.view.impl.mixin;

import de.johni0702.minecraft.view.impl.client.render.ViewRenderManager;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkRenderManager.class, remap = false)
public abstract class MixinChunkRenderManager_Nothirium {
    @Unique private static int betterportals$setupGeneration;

    @ModifyArg(
            method = "setup",
            at = @At(
                    value = "INVOKE",
                    target = "Lmeldexun/nothirium/api/renderer/chunk/IChunkRenderer;setup(Lmeldexun/nothirium/api/renderer/chunk/IRenderChunkProvider;DDDLmeldexun/renderlib/util/Frustum;I)V"),
            index = 5,
            require = 1)
    private static int betterportals$useUniqueSetupGeneration(int frame) {
        return ++betterportals$setupGeneration;
    }

    @Redirect(
            method = "allChanged",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I",
                    remap = true),
            remap = false,
            require = 1)
    private static int betterportals$usePersistentRenderDistance(GameSettings settings) {
        return ViewRenderManager.Companion.getINSTANCE().getRealRenderDistanceChunks();
    }
}
