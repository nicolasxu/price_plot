package com.company;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/18/15.
 */
public class NoFilter extends IFilter {

    ArrayList<Integer> buySellSignal;

    public NoFilter() {
        this.buySellSignal = new ArrayList<Integer>();
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();
        }

        for(int index = 0; index < input.size(); index++) {
            output.add(input.get(index));

            if(index ==0) {
                this.buySellSignal.add(-1);
            } else {
                // 2nd and on...

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
                        winLossInputIndex.add(i);
                        sold = false;

                    }
                }
            }
        }

        int tempWin = 0, tempLoss = 0;
        int maxLossRow = 0, tempMaxLossRow = 0;
        int maxLossRowPoint = 0;
        HashMap<Integer, Integer> maxLossInRowDist = new HashMap<Integer, Integer>();


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

                if(maxLossInRowDist.containsKey(tempMaxLossRow)) {
                    int prevNumber = maxLossInRowDist.get(tempMaxLossRow);
                    prevNumber++;
                    maxLossInRowDist.put(tempMaxLossRow, prevNumber);
                } else {
                    maxLossInRowDist.put(tempMaxLossRow, 1);
                }

                tempMaxLossRow = 0;
            }
        }
        System.out.println("NoFilter P&L with ()");
        System.out.println("----------------------------------------");
        System.out.println("NoFilter tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("NoFilter Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

        for(Integer key: maxLossInRowDist.keySet()) {
            System.out.println(key + " :" + maxLossInRowDist.get(key));
        }

    }

}
