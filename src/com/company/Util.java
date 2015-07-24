package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/23/15.
 */
public class Util {
    static public int maxPrevLoseCount(ArrayList<Integer> profitLossRecord) {
        if (profitLossRecord == null) {
            return 0;
        }

        if(profitLossRecord.size() == 0 ) {
            return 0;
        }

        int lastIndex = profitLossRecord.size() - 1;
        int currentIndex = lastIndex;
        int count = 0;
        int currntWinLoss = 0; // initialize as 0
        while (currentIndex >=0 && currntWinLoss == 0 ) {
            if(profitLossRecord.get(currentIndex) == 0) {
                count++;

            }
            currntWinLoss = profitLossRecord.get(currentIndex);

            currentIndex--;
        }

        return count;
    }
}
