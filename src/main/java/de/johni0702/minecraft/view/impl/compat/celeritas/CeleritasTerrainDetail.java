package de.johni0702.minecraft.view.impl.compat.celeritas;

/** Readiness of Celeritas terrain near a portal child pass's visibility origin. */
public final class CeleritasTerrainDetail {
    private final boolean ready;
    private final boolean timedOut;

    public CeleritasTerrainDetail(boolean ready, boolean timedOut) {
        this.ready = ready;
        this.timedOut = timedOut;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isTimedOut() {
        return this.timedOut;
    }
}
