package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/2/15.
 */
public abstract class IFilter {
    public void filter(ArrayList<Double> input, ArrayList<Double> output) {}
    // if you want to recalculate the whole output, you have to set output to size 0 by output.clear()
    // otherwise, the filter will always recalcualte the last one, or those haven't been
    // calculated yet.

}
