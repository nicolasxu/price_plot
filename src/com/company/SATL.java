package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/17/15.
 */
public class SATL extends IFilter {

    public ArrayList<Integer> buySellSignal;

    public SATL() {
        this.buySellSignal = new ArrayList<Integer>();
        this.buySellSignal.add(-1);
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();
        }
        for(int index = Math.max(0, output.size() -1); index < input.size(); index++) {

            double satlValue = this.FN(index, input);
            output.add(satlValue);

            if(index > 0) {
                // 2nd and on..
                buySellSignal.add(index, buySellSignal.get(index -1));
                if (output.get(index) > output.get(index -1)) {
                    this.buySellSignal.set(index, 1);
                }
                if (output.get(index) <  output.get(index -1)) {
                    this.buySellSignal.set(index, 0);
                }
            }

        }



    }
    private double FN(int index, ArrayList<Double> input) {
        double saltValue =
                         0.0982862174 * input.get(Math.max(index - 0, 0))
                        +0.0975682269 * input.get(Math.max(index - 1, 0))
                        +0.0961401078 * input.get(Math.max(index - 2, 0))
                        +0.0940230544 * input.get(Math.max(index - 3, 0))
                        +0.0912437090 * input.get(Math.max(index - 4, 0))
                        +0.0878391006 * input.get(Math.max(index - 5, 0))
                        +0.0838544303 * input.get(Math.max(index - 6, 0))
                        +0.0793406350 * input.get(Math.max(index - 7, 0))
                        +0.0743569346 * input.get(Math.max(index - 8, 0))
                        +0.0689666682 * input.get(Math.max(index - 9, 0))
                        +0.0632381578 * input.get(Math.max(index - 10, 0))
                        +0.0572428925 * input.get(Math.max(index - 11, 0))
                        +0.0510534242 * input.get(Math.max(index - 12, 0))
                        +0.0447468229 * input.get(Math.max(index - 13, 0))
                        +0.0383959950 * input.get(Math.max(index - 14, 0))
                        +0.0320735368 * input.get(Math.max(index - 15, 0))
                        +0.0258537721 * input.get(Math.max(index - 16, 0))
                        +0.0198005183 * input.get(Math.max(index - 17, 0))
                        +0.0139807863 * input.get(Math.max(index - 18, 0))
                        +0.0084512448 * input.get(Math.max(index - 19, 0))
                        +0.0032639979 * input.get(Math.max(index - 20, 0))
                        -0.0015350359 * input.get(Math.max(index - 21, 0))
                        -0.0059060082 * input.get(Math.max(index - 22, 0))
                        -0.0098190256 * input.get(Math.max(index - 23, 0))
                        -0.0132507215 * input.get(Math.max(index - 24, 0))
                        -0.0161875265 * input.get(Math.max(index - 25, 0))
                        -0.0186164872 * input.get(Math.max(index - 26, 0))
                        -0.0205446727 * input.get(Math.max(index - 27, 0))
                        -0.0219739146 * input.get(Math.max(index - 28, 0))
                        -0.0229204861 * input.get(Math.max(index - 29, 0))
                        -0.0234080863 * input.get(Math.max(index - 30, 0))
                        -0.0234566315 * input.get(Math.max(index - 31, 0))
                        -0.0231017777 * input.get(Math.max(index - 32, 0))
                        -0.0223796900 * input.get(Math.max(index - 33, 0))
                        -0.0213300463 * input.get(Math.max(index - 34, 0))
                        -0.0199924534 * input.get(Math.max(index - 35, 0))
                        -0.0184126992 * input.get(Math.max(index - 36, 0))
                        -0.0166377699 * input.get(Math.max(index - 37, 0))
                        -0.0147139428 * input.get(Math.max(index - 38, 0))
                        -0.0126796776 * input.get(Math.max(index - 39, 0))
                        -0.0105938331 * input.get(Math.max(index - 40, 0))
                        -0.0084736770 * input.get(Math.max(index - 41, 0))
                        -0.0063841850 * input.get(Math.max(index - 42, 0))
                        -0.0043466731 * input.get(Math.max(index - 43, 0))
                        -0.0023956944 * input.get(Math.max(index - 44, 0))
                        -0.0005535180 * input.get(Math.max(index - 45, 0))
                        +0.0011421469 * input.get(Math.max(index - 46, 0))
                        +0.0026845693 * input.get(Math.max(index - 47, 0))
                        +0.0040471369 * input.get(Math.max(index - 48, 0))
                        +0.0052380201 * input.get(Math.max(index - 49, 0))
                        +0.0062194591 * input.get(Math.max(index - 50, 0))
                        +0.0070340085 * input.get(Math.max(index - 51, 0))
                        +0.0076266453 * input.get(Math.max(index - 52, 0))
                        +0.0080376628 * input.get(Math.max(index - 53, 0))
                        +0.0083037666 * input.get(Math.max(index - 54, 0))
                        +0.0083694798 * input.get(Math.max(index - 55, 0))
                        +0.0082901022 * input.get(Math.max(index - 56, 0))
                        +0.0080741359 * input.get(Math.max(index - 57, 0))
                        +0.0077543820 * input.get(Math.max(index - 58, 0))
                        +0.0073260526 * input.get(Math.max(index - 59, 0))
                        +0.0068163569 * input.get(Math.max(index - 60, 0))
                        +0.0062325477 * input.get(Math.max(index - 61, 0))
                        +0.0056078229 * input.get(Math.max(index - 62, 0))
                        +0.0049516078 * input.get(Math.max(index - 63, 0))
                        +0.0161380976 * input.get(Math.max(index - 64, 0));
        return saltValue;
    }

    public void calculateWinLoss(ArrayList<Double> input, ArrayList<Double> output, ArrayList<Integer> signal) {
        int total = input.size();
        int currentSignal;
        int previousSignal;
        boolean bought = false;
        boolean sold = false;
        double boughtPrice = 0.0;
        double soldPrice = 0.0;
        int winCount = 0;
        int lossCount = 0;
        double profitSize = 50 * 0.00001; // 50 points
        ArrayList<Integer> winLossList = new ArrayList<Integer>();
        ArrayList<Integer> winLossInputIndex = new ArrayList<Integer>();


        for(int i = 0; i < total; i++) {

            currentSignal = signal.get(i);
            if(i -1 < 0) {
                previousSignal = signal.get(0);
            } else {
                previousSignal = signal.get(i - 1);
            }

            // trend reverse
            if(currentSignal == 1 && previousSignal == 0) {
                if(sold) {
                    sold = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                }
                bought = true;
                boughtPrice = input.get(i);
            }

            if(currentSignal == 0 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                }
                sold = true;
                soldPrice = input.get(i);
            }

            // trend continue
            if(currentSignal ==1 && previousSignal ==1) {
                // buy trend continue
                double currentPrice = input.get(i);
                if(bought) {
                    if (currentPrice - boughtPrice >= profitSize) {
                        winCount++;
                        winLossList.add(1);
                        winLossInputIndex.add(i);
                        bought = false;
                    }
                }

            }

            if(currentSignal == 0 && previousSignal == 0) {
                // sell trend continue
                double currentPrice = input.get(i);
                if(sold) {
                    if(soldPrice - currentPrice >= profitSize) {
                        winCount++;
                        winLossList.add(1);
                        winLossInputIndex.add(1);
                        sold = false;

                    }
                }
            }
        }
        System.out.println("SATL P&L with ()");
        System.out.println("SATL Win Count: " + winCount);
        System.out.println("SATL Lose Count: " + lossCount);

        System.out.println("----------------------------------------");
        int tempWin = 0, tempLoss = 0;
        int maxLossRow = 0, tempMaxLossRow = 0;
        int maxLossRowPoint = 0;

        for(int i = 0; i < winLossList.size(); i++) {
            if(winLossList.get(i) == 0) {
                tempLoss++;
                tempMaxLossRow++;
                if(tempMaxLossRow > maxLossRow) {
                    maxLossRow = tempMaxLossRow;
                    maxLossRowPoint = i;
                }
            } else {
                tempWin++;
                tempMaxLossRow = 0;
            }
            //System.out.println(i + " :" + winLossList.get(i));
        }
        System.out.println("SATL tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("SATL Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

    }
}
