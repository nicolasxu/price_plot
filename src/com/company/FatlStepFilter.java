package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/18/15.
 */
public class FatlStepFilter extends IFilter {

    FATL fatlFilter;
    FATL fatlFilter2;
    int step;
    ArrayList<Double> fatl1Output;
    ArrayList<Double> fatl2Output;
    double prevPrice;
    public ArrayList<Integer> buySellSignal;

    public FatlStepFilter(int step) {
        this.fatlFilter  = new FATL();
        this.fatlFilter2 = new FATL();
        this.step = step;
        this.fatl1Output = new ArrayList<Double>();
        this.fatl2Output = new ArrayList<Double>();
        this.prevPrice = 0;
        this.buySellSignal = new ArrayList<Integer>();

    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {
        this.fatlFilter.filter(input, fatl1Output);
        //this.fatlFilter2.filter(fatl1Output, fatl2Output);


        for(int index = 0; index < input.size(); index++) {
            if(index == 0) {
                this.prevPrice = input.get(index);
                output.add(this.prevPrice);
                this.buySellSignal.add(-1);
            } else {
                // 2nd and on...
                if(Math.abs(fatl1Output.get(index) - prevPrice) >= this.step * 0.00001) {
                    output.add(fatl1Output.get(index));
                } else {
                    output.add(output.get(index -1));
                }

                this.prevPrice = fatl1Output.get(index);

                // calculate buySellSignal
                buySellSignal.add(index, buySellSignal.get(index -1));
                if (output.get(index) > output.get(index -1)) {
                    this.buySellSignal.set(index, 1);
                }
                if (output.get(index) <  output.get(index -1)) {
                    this.buySellSignal.set(index, 0);
                }
            }
        }

        /*
        if(MathAbs(xma-prevxma)>=dStep) XXMA[bar]=xma+dPriceShift;
        else XXMA[bar]=XXMA[bar-1];

        if(bar<rates_total-1) prevxma=xma;
        */

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
        System.out.println("FATL2Step P&L with ()");
        System.out.println("FATL2Step wins: " + tempWin + " Loss: " + tempLoss);
        System.out.println("FATL2Step Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
        System.out.println("\n");

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
