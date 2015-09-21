package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/21/15.
 */
public class AlStepFilter extends IFilter {

    ALF_2 alfFilter;
    ArrayList<Double> alfOutput;
    double prevPrice;
    ArrayList<Integer> buySellSignal;
    int step;


    public AlStepFilter(int step) {
        this.alfFilter = new ALF_2(2);
        this.alfOutput = new ArrayList<Double>();
        this.prevPrice = 0;
        this.buySellSignal = new ArrayList<Integer>();
        this.step = step;
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();

        }
        this.alfFilter.filter(input, alfOutput);

        for(int index = 0; index < input.size(); index++) {

            if(index == 0) {
                // first index
                this.prevPrice = alfOutput.get(index); // first value of filter output
                output.add(this.prevPrice);
                this.buySellSignal.add(-1);



            } else {
                // 2nd index and on...



                if(Math.abs(alfOutput.get(index) - prevPrice) >= this.step * 0.00001) {
                    output.add(alfOutput.get(index));
                } else {
                    output.add(output.get(index -1));
                }

                this.prevPrice = alfOutput.get(index);

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
        System.out.println("Laguerre Step P&L with Laguerre(step: "+this.step+")");
        System.out.println("wins: " + tempWin + " Loss: " + tempLoss);
        System.out.println("Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
        System.out.println("--------------------------------------------");
        System.out.println("\n");

    }


}
