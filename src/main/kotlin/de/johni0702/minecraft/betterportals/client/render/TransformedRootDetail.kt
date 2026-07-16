package de.johni0702.minecraft.betterportals.client.render

import de.johni0702.minecraft.betterportals.common.PortalAgent
import de.johni0702.minecraft.view.client.render.RenderPass
import de.johni0702.minecraft.view.client.render.get
import de.johni0702.minecraft.view.client.render.set

class TransformedRootDetail(val ingressAgent: PortalAgent<*>)

var RenderPass.transformedRootDetail: TransformedRootDetail?
    get() = get()
    set(value) { set(value) }
