package de.johni0702.minecraft.view.impl.compat.celeritas;

/** Mutable per-origin state owned by the BetterPortals Celeritas bootstrap mixin. */
public final class CeleritasTerrainBootstrapState {
    private final long startedNanos = System.nanoTime();
    private int attempts;
    private int builtAttempt = -1;
    private boolean ready;
    private boolean timedOut;

    public long getStartedNanos() {
        return this.startedNanos;
    }

    public int incrementAttempts() {
        return ++this.attempts;
    }

    public int getAttempts() {
        return this.attempts;
    }

    public boolean isReady() {
        return this.ready;
    }

    public void observeBuilt(boolean built) {
        if (!built) {
            this.builtAttempt = -1;
            this.ready = false;
        } else if (this.builtAttempt == -1) {
            this.builtAttempt = this.attempts;
        } else if (this.attempts > this.builtAttempt) {
            this.ready = true;
        }
    }

    public boolean isTimedOut() {
        return this.timedOut;
    }

    public void setTimedOut() {
        this.timedOut = true;
    }
}
