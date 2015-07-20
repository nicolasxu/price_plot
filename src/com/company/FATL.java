package com.company;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/17/15.
 */
public class FATL extends IFilter {

    public ArrayList<Integer> buySellSignal;

    public FATL() {
        this.buySellSignal = new ArrayList<Integer>();
        //this.buySellSignal.add(-1);
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();
        }
        for(int index = Math.max(0, output.size() -1); index < input.size(); index++) {

            double satlValue = this.FN(index, input);
            output.add(satlValue);
            if(index == 0 ) {
                this.buySellSignal.add(-1);
            } else {
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
        double faltValue =
                          0.4360409450 * input.get(Math.max(0, index - 0))
                        + 0.3658689069 * input.get(Math.max(0, index - 1))
                        + 0.2460452079 * input.get(Math.max(0, index - 2))
                        + 0.1104506886 * input.get(Math.max(0, index - 3))
                        - 0.0054034585 * input.get(Math.max(0, index - 4))
                        - 0.0760367731 * input.get(Math.max(0, index - 5))
                        - 0.0933058722 * input.get(Math.max(0, index - 6))
                        - 0.0670110374 * input.get(Math.max(0, index - 7))
                        - 0.0190795053 * input.get(Math.max(0, index - 8))
                        + 0.0259609206 * input.get(Math.max(0, index - 9))
                        + 0.0502044896 * input.get(Math.max(0, index - 10))
                        + 0.0477818607 * input.get(Math.max(0, index - 11))
                        + 0.0249252327 * input.get(Math.max(0, index - 12))
                        - 0.0047706151 * input.get(Math.max(0, index - 13))
                        - 0.0272432537 * input.get(Math.max(0, index - 14))
                        - 0.0338917071 * input.get(Math.max(0, index - 15))
                        - 0.0244141482 * input.get(Math.max(0, index - 16))
                        - 0.0055774838 * input.get(Math.max(0, index - 17))
                        + 0.0128149838 * input.get(Math.max(0, index - 18))
                        + 0.0226522218 * input.get(Math.max(0, index - 19))
                        + 0.0208778257 * input.get(Math.max(0, index - 20))
                        + 0.0100299086 * input.get(Math.max(0, index - 21))
                        - 0.0036771622 * input.get(Math.max(0, index - 22))
                        - 0.0136744850 * input.get(Math.max(0, index - 23))
                        - 0.0160483392 * input.get(Math.max(0, index - 24))
                        - 0.0108597376 * input.get(Math.max(0, index - 25))
                        - 0.0016060704 * input.get(Math.max(0, index - 26))
                        + 0.0069480557 * input.get(Math.max(0, index - 27))
                        + 0.0110573605 * input.get(Math.max(0, index - 28))
                        + 0.0095711419 * input.get(Math.max(0, index - 29))
                        + 0.0040444064 * input.get(Math.max(0, index - 30))
                        - 0.0023824623 * input.get(Math.max(0, index - 31))
                        - 0.0067093714 * input.get(Math.max(0, index - 32))
                        - 0.0072003400 * input.get(Math.max(0, index - 33))
                        - 0.0047717710 * input.get(Math.max(0, index - 34))
                        + 0.0005541115 * input.get(Math.max(0, index - 35))
                        + 0.0007860160 * input.get(Math.max(0, index - 36))
                        + 0.0130129076 * input.get(Math.max(0, index - 37))
                        + 0.0040364019 * input.get(Math.max(0, index - 38)) ;
        return faltValue;
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
        System.out.println("FATL P&L with ()");
        //System.out.println("FATL Win Count: " + winCount);
        //System.out.println("FATL Lose Count: " + lossCount);

        System.out.println("----------------------------------------");
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
            //System.out.println(i + " :" + winLossList.get(i));
        }
        System.out.println("FATL tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("FATL Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
        for(Integer key: maxLossInRowDist.keySet()) {
            System.out.println(key + " :" + maxLossInRowDist.get(key));
        }

    }
}

