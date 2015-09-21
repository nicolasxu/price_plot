package com.company;

/**
 * Created by nick on 8/31/15.
 */
public class TickSignal {
    public int tickIndex;
    public int signal; // -1 sell, 1 buy, other value is useless
    public TickSignal(int index, int signal) {
        this.tickIndex = index;
        this.signal = signal;
    }

}
