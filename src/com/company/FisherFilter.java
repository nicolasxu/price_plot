package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nick on 7/17/15.
 */
public class FisherFilter extends IFilter{

    private int period; // fisher period to go back and find the max and min number
    private ArrayList<Double> midValues; // mid value buffer needed for calculation

    public ArrayList<Integer> buySellSignal;

    public void filter(ArrayList<Double> input, ArrayList<Double> outputFisher,
                       ArrayList<Double> outputTrigger) {

        for(int index = Math.max(0, outputFisher.size() - 1); index < input.size(); index ++) {

            if ( index < this.period) {
                // less than required bar count,  e.g: period == 10, index will be 0 ~ 9
                // just copy input to output

                double valueZero = 0;

                // writer fisher
                if(outputFisher.size() >= index + 1) {
                    outputFisher.set(index, valueZero);
                } else {
                    outputFisher.add(valueZero);
                }

                // write trigger
                if(outputTrigger.size() >= index +1) {
                    outputTrigger.set(index, valueZero);
                } else {
                    outputTrigger.add(valueZero);
                }

                // write mid value
                if(midValues.size() >= index + 1) {
                    midValues.set(index, valueZero);
                } else {
                    midValues.add(valueZero);
                }

                this.buySellSignal.add(-1);

            } else {
                // more bar then the required this.period
                // do calculation
                // starts from [period]
                double price = input.get(index);
                double maxH = price;
                double minL = price;

                // find the max and min in past period - 1 bars
                for(int j = 0; j< this.period; j++) {
                    double nPrice = input.get(index - j);
                    if(nPrice > maxH) maxH = nPrice;
                    if(nPrice < minL) minL = nPrice;
                }

                // calculation mid value calculation
                double valueI = 0.5 * 2.0 * ((price - minL)/(maxH - minL) - 0.5) + 0.5 * midValues.get(index - 1);

                if(valueI > 0.9999) valueI = 0.9999;
                if(valueI < -0.9999) valueI = -0.9999;

                // write calculation mid value buffer
                if(midValues.size() >= index + 1) {
                    midValues.set(index, valueI);
                } else {
                    midValues.add(valueI);
                }

                // current fisher value index calculation
                double fisherI = 0.25 * Math.log((1 + midValues.get(index)) / (1 - midValues.get(index))) + 0.5 * outputFisher.get(index - 1);

                // write fisher
                if(outputFisher.size() >= index +1) {
                    outputFisher.set(index, fisherI );
                } else {
                    outputFisher.add(fisherI);
                }
                //System.out.println("Fisher result: " + fisherI);

                // write trigger
                double triggerI = outputFisher.get(index - 1);
                if(outputTrigger.size() >= index +1) {
                    outputTrigger.set(index, triggerI);
                } else {
                    outputTrigger.add(triggerI);
                }

                // calculate buySellSignal
                buySellSignal.add(index, buySellSignal.get(index -1));
                if (outputFisher.get(index) > outputFisher.get(index -1)) {
                    this.buySellSignal.set(index, 1);
                }
                if (outputFisher.get(index) <  outputFisher.get(index -1)) {
                    this.buySellSignal.set(index, 0);
                }
            }
        }
        double latestInput = input.get(input.size() - 1);
        //System.out.println("latest fisher input value: " + latestInput);
        //double latestFisher  = outputFisher.get(outputFisher.size() - 1 );
        //double latestTrigger = outputTrigger.get(outputTrigger.size()  - 1);
        //System.out.println("Fisher latest: " + latestFisher + " Trigger latest: " + latestTrigger);


    }
    public FisherFilter(int period) {
        this.period = period;
        this.midValues = new ArrayList<Double>();
        this.buySellSignal = new ArrayList<Integer>();
    }

    public void setPeriod (int p) {
        this.period = p;
    }
    public int getPeriod () {
        return this.period;
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
        System.out.println("Fisher P&L with Laguerre("+this.period+")");
        System.out.println("Fisher Win Count: " + winCount);
        System.out.println("Fisher Lose Count: " + lossCount);

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
        System.out.println("Fisher tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("Fisher Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

    }
}

