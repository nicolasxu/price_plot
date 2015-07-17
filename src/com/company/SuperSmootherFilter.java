package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/2/15.
 */
public class SuperSmootherFilter extends IFilter{

    public int ssPeriod; // smooth period
    public int minBarCount = 2; // if input bar count is less than it, just copy
    // the input, no calculation.
    public ArrayList<Integer> buySellSignal;

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {


        // input count should always equal output count
        double coeA = Math.pow(Math.E, -1.414*3.14159/this.ssPeriod);
        double coeB = 2 * coeA * Math.cos(1.414 * 180 / this.ssPeriod);
        double coeC2 = coeB;
        double coeC3 = - coeA * coeA;
        double coeC1 = 1 - coeC2 - coeC3;



        for(int index = Math.max(0, output.size() - 1); index < input.size(); index ++) {

            if(index < minBarCount) {
                Double inputNumber = input.get(index);
                output.add(inputNumber);
                buySellSignal.add(-1);
            }

            if(index >= minBarCount) {
                double inputI    = input.get(index);
                double inputIm1  = input.get(index-1);
                double outputIm1 = output.get(index-1);
                double outputIm2 = output.get(index-2);
                double result = coeC1 * (inputI + inputIm1) / 2 + coeC2 * outputIm1 + coeC3 * outputIm2;
                if(index >= output.size()) {
                    output.add(result);
                } else {
                    output.set(index, result);
                }
                //System.out.println("SuperSmoother result: " + result);

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
        //double latestValue = output.get(output.size() - 1);
        //System.out.println("SuperSmoother latest value: " + latestValue);
    }
    public SuperSmootherFilter(int period) {
        this.ssPeriod = period;
        this.buySellSignal = new ArrayList<Integer>();

    }
    public void setPeriod(int p) {
        this.ssPeriod = p;
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
        System.out.println("SuperSmoother P&L with Laguerre("+this.ssPeriod+")");
        System.out.println("SuperSmoother Win Count: " + winCount);
        System.out.println("SuperSmoother Lose Count: " + lossCount);

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
        System.out.println("SuperSmoother tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("SuperSmoother Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

    }
}

