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

    public void calculateWinLoss(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                                 ArrayList<Integer> filterBuySellSignal) {
        int total = priceData.size();
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

            currentSignal = filterBuySellSignal.get(i);
            if(i -1 < 0) {
                previousSignal = filterBuySellSignal.get(0);
            } else {
                previousSignal = filterBuySellSignal.get(i - 1);
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
                boughtPrice = priceData.get(i);
            }

            if(currentSignal == 0 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                }
                sold = true;
                soldPrice = priceData.get(i);
            }

            // trend continue
            if(currentSignal ==1 && previousSignal ==1) {
                // buy trend continue
                double currentPrice = priceData.get(i);
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
                double currentPrice = priceData.get(i);
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
        System.out.println("NoFilter Wins: " + tempWin + " Loss: " + tempLoss);
        System.out.println("NoFilter Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
        System.out.println("----------------------------------------");

        for(Integer key: maxLossInRowDist.keySet()) {
            //System.out.println(key + " :" + maxLossInRowDist.get(key));
        }

    }

    public void calculate2(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                           ArrayList<Integer> filterBuySellSignal) {


        int total = priceData.size();
        int currentSignal;
        int previousSignal;
        int position = 0; // -1 is shorting position, 1 is longing position, 0 is no position
        double lastDealPrice = 0; // when open a position, set this value to current price
        double totalPL = 0; // total profit and loss

        for(int i = 0; i < total; i++) {

            currentSignal = filterBuySellSignal.get(i);
            if(i -1 < 0) {
                previousSignal = currentSignal;
            } else {
                previousSignal = filterBuySellSignal.get(i - 1);
            }

            if(currentSignal == 1 && previousSignal == 0) {
                // reverse to upwards
                // both open and close position will be handle at reverse point

                switch(position) {
                    case 0:
                        // no profit or loss
                        position = 1;
                        lastDealPrice = priceData.get(i);
                        break;
                    case -1:
                        // reverse position & calculate p&l
                        double currentPrice = priceData.get(i);
                        double profit = lastDealPrice - currentPrice; // positive is profit, negative is loss
                        totalPL = totalPL + profit;
                        lastDealPrice = currentPrice;
                        position = 1;

                        break;
                    case 1:
                        // someting is wrong, since an upwards trend can not reverse to upward trend
                        System.out.println("error: cannot reverse to upwards trend from upward trend");
                        break;
                    default:
                        System.out.println(" position error in reverse to upwards processing");
                }

            }

            if(currentSignal == 0 && previousSignal == 1) {
                // reverse to downwards
                switch (position) {
                    case 0:
                        position = -1;
                        lastDealPrice = priceData.get(i);
                        break;
                    case -1:
                        System.out.println("error: cannot reverse to downwards trend from downwards trend");
                        break;
                    case 1:
                        //System.out.println("reversing position to 1");
                        double currentPrice = priceData.get(i);
                        double profit = currentPrice - lastDealPrice;
                        totalPL = totalPL + profit;
                        lastDealPrice = currentPrice;
                        position = -1;
                        break;
                    default:
                        System.out.println(" position error in reverse to upwards processing");
                }
            }

        }

        System.out.println("total profit is: " + totalPL);
    }
}
