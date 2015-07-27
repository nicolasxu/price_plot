package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/24/15.
 */
public class FirFilter extends IFilter {
    int period;
    double wsum;
    ArrayList<Double> w;
    ArrayList<Integer> buySellSignal;
    public FirFilter(int period) {
        this.period = period;
        this.w = new ArrayList<Double>();
        this.wsum = 0;
        this.buySellSignal = new ArrayList<Integer>();

        for(int index = 0; index < this.period; index++) {
            double currentValue = 0.5 - 0.5 * Math.cos(2 * Math.PI * (index + 1)/(this.period + 1));
            w.add(currentValue);
            wsum = wsum + currentValue;
        }
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        for(int index = 0; index < input.size(); index++) {

            output.add(0.0);

            if(index < this.period - 1) {
                // period = 5
                // 1, 2, 3, 4
                this.buySellSignal.add(-1);
            } else {
               double currentValue = 0;
               for(int k = 0; k < this.period; k++) {
                   // x[i]+=price[i-k]*w[k];
                   currentValue = currentValue + input.get(index - k) * w.get(k);
               }
                currentValue = currentValue / wsum;
                output.set(index, currentValue);

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

//    public void calculateWinLoss(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
//                                 ArrayList<Integer> filterBuySellSignal) {
//        int total = priceData.size();
//        int currentSignal;
//        int previousSignal;
//        boolean bought = false;
//        boolean sold = false;
//        double boughtPrice = 0.0;
//        double soldPrice = 0.0;
//        int winCount = 0;
//        int lossCount = 0;
//        double profitSize = 50 * 0.00001; // 50 points
//        ArrayList<Integer> winLossList = new ArrayList<Integer>();
//        ArrayList<Integer> winLossInputIndex = new ArrayList<Integer>();
//
//
//        for(int i = 0; i < total; i++) {
//
//            currentSignal = filterBuySellSignal.get(i);
//            if(i -1 < 0) {
//                previousSignal = filterBuySellSignal.get(0);
//            } else {
//                previousSignal = filterBuySellSignal.get(i - 1);
//            }
//
//            // trend reverse
//            if(currentSignal == 1 && previousSignal == 0) {
//                if(sold) {
//                    sold = false;
//                    lossCount++;
//                    winLossList.add(0);
//                    winLossInputIndex.add(i);
//                }
//                bought = true;
//                boughtPrice = priceData.get(i);
//            }
//
//            if(currentSignal == 0 && previousSignal == 1) {
//                if(bought) {
//                    bought = false;
//                    lossCount++;
//                    winLossList.add(0);
//                    winLossInputIndex.add(i);
//                }
//                sold = true;
//                soldPrice = priceData.get(i);
//            }
//
//            // trend continue
//            if(currentSignal ==1 && previousSignal ==1) {
//                // buy trend continue
//                double currentPrice = priceData.get(i);
//                if(bought) {
//                    if (currentPrice - boughtPrice >= profitSize) {
//                        winCount++;
//                        winLossList.add(1);
//                        winLossInputIndex.add(i);
//                        bought = false;
//                    }
//                }
//
//            }
//
//            if(currentSignal == 0 && previousSignal == 0) {
//                // sell trend continue
//                double currentPrice = priceData.get(i);
//                if(sold) {
//                    if(soldPrice - currentPrice >= profitSize) {
//                        winCount++;
//                        winLossList.add(1);
//                        winLossInputIndex.add(i);
//                        sold = false;
//
//                    }
//                }
//            }
//        }
//
//        int tempWin = 0, tempLoss = 0;
//        int maxLossRow = 0, tempMaxLossRow = 0;
//        int maxLossRowPoint = 0;
//        HashMap<Integer, Integer> maxLossInRowDist = new HashMap<Integer, Integer>();
//
////        for(Integer value: winLossList) {
////            System.out.println(value);
////        }
//        // calculate loss distribution
//        for(int i = 0; i < winLossList.size(); i++) {
//
//            if(winLossList.get(i) == 0) {
//                tempLoss++;
//                tempMaxLossRow++;
//                if(tempMaxLossRow > maxLossRow) {
//                    maxLossRow = tempMaxLossRow;
//                    maxLossRowPoint = i;
//                }
//            } else {
//                tempWin++;
//
//                if(maxLossInRowDist.containsKey(tempMaxLossRow)) {
//                    int prevNumber = maxLossInRowDist.get(tempMaxLossRow);
//                    prevNumber++;
//                    maxLossInRowDist.put(tempMaxLossRow, prevNumber);
//                } else {
//                    maxLossInRowDist.put(tempMaxLossRow, 1);
//                }
//
//                tempMaxLossRow = 0;
//            }
//        }
//        System.out.println("FIR Filter P&L with ()");
//        System.out.println("FIR Filter Win: " + tempWin + ", Loss: " + tempLoss);
//        System.out.println("FIR Filter Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
//        System.out.println("----------------------------------------");
//        System.out.println("\n");
//        for(Integer key: maxLossInRowDist.keySet()) {
//            if(key > 0) {
//                System.out.println(key + " :" + maxLossInRowDist.get(key));
//            }
//
//        }
//
//    }
}
