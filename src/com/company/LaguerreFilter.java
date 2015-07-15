package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/15/15.
 */
public class LaguerreFilter extends IFilter {

    double alpha;

    ArrayList<Double> l0, l1, l2, l3;

    ArrayList<Double> firOputput;

    ArrayList<Integer> buySellSignal;

    public LaguerreFilter(double alpha) {
        this.alpha = alpha;
        this.l0 = new ArrayList<Double>();
        this.l1 = new ArrayList<Double>();
        this.l2 = new ArrayList<Double>();
        this.l3 = new ArrayList<Double>();

        this.firOputput = new ArrayList<Double>();

        this.buySellSignal = new ArrayList<Integer>();

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
                firOputput.add(0.0);
                this.buySellSignal.add(-1);
            } else {
                // 2nd and on..
                double l0t = alpha * input.get(currentBarIndex) + (1-alpha)* l0.get(currentBarIndex -1);
                l0.add(l0t);

                double l1t = -(1- alpha) * l0t + l0.get(currentBarIndex -1) + (1-alpha)* l1.get(currentBarIndex -1);
                l1.add(l1t);

                double l2t = -(1-alpha) * l1t + l1.get(currentBarIndex -1) + (1-alpha)* l2.get(currentBarIndex -1);
                l2.add(l2t);

                double l3t = -(1-alpha) * l2t + l2.get(currentBarIndex -1) + (1-alpha)* l3.get(currentBarIndex -1);
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
                //System.out.println("Laguerre buySellSignal["+currentBarIndex+"]=" + this.buySellSignal.get(currentBarIndex));


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
                }
                bought = true;
                boughtPrice = input.get(i);
            }

            if(currentSignal == 0 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
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
                        sold = false;

                    }
                }
            }
        }
        System.out.println("Laguerre P&L with Laguerre("+this.alpha+")");
        System.out.println("Laguerre(not adaptive) Win Count: " + winCount);
        System.out.println("Laguerre(not adaptive) Lose Count: " + lossCount);

    }

}
