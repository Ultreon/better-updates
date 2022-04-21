package com.ultreon.mods.betterupdates.common;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Ticker {
    private int currentTicks;
    private final Predicate<Ticker> autoReset;
    private final Consumer<Ticker> onTick;

    public Ticker(int currentTicks, Predicate<Ticker> autoReset, Consumer<Ticker> onTick) {
        this.currentTicks = currentTicks;
        this.autoReset = autoReset;
        this.onTick = onTick;
    }

    public Ticker(Predicate<Ticker> autoReset, Consumer<Ticker> onTick) {
        this.autoReset = autoReset;
        this.onTick = onTick;
    }

    public Ticker(int currentTicks) {
        this(currentTicks, (ticker) -> false);
    }

    public Ticker(int currentTicks, @NotNull Predicate<Ticker> autoReset) {
        this(currentTicks, autoReset, (ticker) -> {
        });
    }

    public void advance() {
        this.currentTicks++;
        onTick.accept(this);
        if (autoReset.test(this)) {
            reset();
        }
    }

    public void reset() {
        this.currentTicks = 0;
    }

    public int getCurrentTicks() {
        return currentTicks;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "currentTicks=" + currentTicks +
                '}';
    }
}
