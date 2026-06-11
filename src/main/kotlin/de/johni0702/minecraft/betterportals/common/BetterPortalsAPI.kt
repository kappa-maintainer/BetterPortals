package de.johni0702.minecraft.betterportals.common

import de.johni0702.minecraft.betterportals.impl.BetterPortalsMod
import net.minecraft.world.World
import net.minecraftforge.fml.common.Loader

/**
 * Entry point into the BetterPortals API.
 */
interface BetterPortalsAPI {
    companion object {
        @JvmStatic
        val instance by lazy { BetterPortalsMod as BetterPortalsAPI }
    }

    fun getPortalManager(world: World): PortalManager
}
