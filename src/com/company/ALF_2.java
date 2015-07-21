package com.company;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nick on 7/15/15. not working~~~~~~~~~~
 */


public class ALF_2 extends IFilter {

    ArrayList<Double> l0, l1, l2, l3;

    ArrayList<Double> firOputput;

    ArrayList<Integer> buySellSignal;

    ArrayList<Double> diff;

    ArrayList<Double> alpha;

    int length;

    int adaptBackLength;

    double hh,ll;

    public ALF_2(int length) {

        this.l0 = new ArrayList<Double>();
        this.l1 = new ArrayList<Double>();
        this.l2 = new ArrayList<Double>();
        this.l3 = new ArrayList<Double>();
        this.diff = new ArrayList<Double>();

        this.firOputput = new ArrayList<Double>();

        this.buySellSignal = new ArrayList<Integer>();

        this.alpha = new ArrayList<Double>();

        this.hh = 0;
        this.ll = 2;

        this.length = length; // 2

        this.adaptBackLength = 2; // 5

    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();
        }

        for(int currentBarIndex = Math.max(0, output.size()); currentBarIndex < input.size(); currentBarIndex++) {


            if(currentBarIndex == 0) {
                // first bar
                l0.add(0.0);
                l1.add(0.0);
                l2.add(0.0);
                l3.add(0.0);
                output.add(0.0);
                this.firOputput.add(0.0);
                this.buySellSignal.add(-1);
                this.diff.add(0.0);

            } else {
                // 2nd and on..

                this.diff.add(Math.abs(input.get(currentBarIndex) - this.diff.get(currentBarIndex - 1)));
                this.hh = this.diff.get(currentBarIndex);
                this.ll = this.diff.get(currentBarIndex);

                // go back length and find the ll and hh
                if(currentBarIndex >= this.length -1) {
                    // currentBarIndex: 9, 10 ...
                    for(int i = 0; i < this.length; i++) {
                        // i: 0 ~ 9
                        double theDiff = this.diff.get(currentBarIndex - i);
                        if(theDiff > this.hh) {
                            this.hh = theDiff;
                        }
                        if(theDiff < this.ll) {
                            this.ll = theDiff;
                        }
                    }
                }
                double medianAlpha = 0;
                if(currentBarIndex > this.length && (this.hh - this.ll !=0) ) {
                    double currentAlpha = (this.diff.get(currentBarIndex) - this.ll) / (this.hh - this.ll);
                    this.setValueAt(this.alpha, currentBarIndex, currentAlpha);
                    //this.alpha.add(currentAlpha);
                     medianAlpha = this.median(currentBarIndex);



                }

                double l0t = medianAlpha * input.get(currentBarIndex) + (1-medianAlpha)* l0.get(currentBarIndex -1);
                l0.add(l0t);

                double l1t = -(1- medianAlpha) * l0t + l0.get(currentBarIndex -1) + (1-medianAlpha)* l1.get(currentBarIndex -1);
                l1.add(l1t);

                double l2t = -(1-medianAlpha) * l1t + l1.get(currentBarIndex -1) + (1-medianAlpha)* l2.get(currentBarIndex -1);
                l2.add(l2t);

                double l3t = -(1-medianAlpha) * l2t + l2.get(currentBarIndex -1) + (1-medianAlpha)* l3.get(currentBarIndex -1);
                l3.add(l3t);

                double lagResult = (l0t + 2*l1t + 2*l2t + l3t) / 6;
                output.add(lagResult);

                if(currentBarIndex > 2){
                    double firResult = (input.get(currentBarIndex) + 2*input.get(currentBarIndex -1) + 2*input.get(currentBarIndex -2) + input.get(currentBarIndex -3)) / 6;
                    this.firOputput.add(firResult);
                } else {
                    this.firOputput.add(0.0);
                }

                // calculate buySellSignal
                buySellSignal.add(currentBarIndex, buySellSignal.get(currentBarIndex -1));
                if (output.get(currentBarIndex) > output.get(currentBarIndex -1)) {
                    this.buySellSignal.set(currentBarIndex, 1);
                }
                if (output.get(currentBarIndex) <  output.get(currentBarIndex -1)) {
                    this.buySellSignal.set(currentBarIndex, 0);
                }
            }
        }
    }

    private void setValueAt(ArrayList<Integer> al, int index /* start from 0 */, int value) {

        if(index < 0) {
            return;
        }

        if(index < al.size()) {
            al.set(index, value);
            return;
        }

        // index 5, size = 5
        // index 7, size = 5
        while(index > al.size()) {
            // fill empty index with 0.0
            al.add(0);
        }

        al.add(value);

    }

    private void setValueAt(ArrayList<Double> al, int index /* start from 0 */, double value) {

        if(index < 0) {
            return;
        }

        if(index < al.size()) {
            al.set(index, value);
            return;
        }

        // index 5, size = 5
        // index 7, size = 5
        while(index > al.size()) {
            // fill empty index with 0.0
            al.add(0.0);
        }

        al.add(value);

    }
    private double median(int index) {
        double[] arrayToSort = new double[this.adaptBackLength];
        double resultMedian;
        for(int i = 0; i < this.adaptBackLength; i++) {
            //System.out.println("index - i= " +index +" - "+ i);
            //System.out.println("this.alpha.size(): " + this.alpha.size());
            arrayToSort[i] = this.alpha.get(Math.max(0, index - i));
        }

        Arrays.sort(arrayToSort);
        int num = (int)Math.round((this.adaptBackLength -1) / 2);

        if(Math.floorMod(this.adaptBackLength, 2) > 0) {
            resultMedian = arrayToSort[num];
        } else {
            resultMedian = (arrayToSort[num] + arrayToSort[num + 1]) / 2;
        }

        // TODO


        return resultMedian;
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
        System.out.println("Laguerre P&L with Laguerre("+this.length+")");
        System.out.println("Laguerre(adaptive) Win Count: " + winCount);
        System.out.println("Laguerre(adaptive) Lose Count: " + lossCount);

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
        System.out.println("tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

    }

}