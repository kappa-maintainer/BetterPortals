package de.johni0702.minecraft.view.impl.client

import de.johni0702.minecraft.view.impl.LOGGER
import net.minecraft.client.multiplayer.WorldClient

/**
 * Observes Celeritas' renderer state without changing its chunk or renderer lifecycle.
 */
internal object CeleritasViewDiagnostics {
    private val available by lazy {
        runCatching { Class.forName("org.taumc.celeritas.CeleritasVintage") }.isSuccess
    }
    val isAvailable get() = available
    private var reportedFailure = false

    fun logInitialization(world: WorldClient, stage: String) {
        if (available) {
            LOGGER.info("[Celeritas] view dim={} stage={}", world.provider.dimension, stage)
        }
    }

    fun log(view: ClientState, stage: String) {
        if (!available) return

        try {
            val world = view.world
            val loadedChunks = world.chunkProvider.javaClass
                .getMethod("celeritas\$getLoadedChunks")
                .invoke(world.chunkProvider) as Map<*, *>
            val tracker = Class.forName("org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder")
                .getMethod("get", Any::class.java)
                .invoke(null, world)
            val readyChunks = tracker.javaClass.getMethod("getReadyChunks").invoke(tracker) as Collection<*>
            val renderer = view.renderGlobal?.javaClass?.getMethod("celeritas\$getWorldRenderer")?.invoke(view.renderGlobal)
            val sectionManager = renderer?.javaClass?.getMethod("getRenderSectionManager")?.invoke(renderer)
            val sections = sectionManager?.javaClass?.getMethod("getTotalSections")?.invoke(sectionManager)
            val visible = renderer?.javaClass?.getMethod("getVisibleChunkCount")?.invoke(renderer)
            val complete = renderer?.javaClass?.getMethod("isTerrainRenderComplete")?.invoke(renderer)

            LOGGER.info(
                "[Celeritas] view dim={} stage={} loadedChunks={} readyChunks={} sections={} visible={} complete={}",
                world.provider.dimension, stage, loadedChunks.size, readyChunks.size, sections, visible, complete
            )
        } catch (t: Throwable) {
            if (!reportedFailure) {
                reportedFailure = true
                LOGGER.warn("Unable to collect Celeritas view diagnostics:", t)
            }
        }
    }
}
