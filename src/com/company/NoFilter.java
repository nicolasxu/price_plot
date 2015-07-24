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

//        for(Integer value: winLossList) {
//            System.out.println(value);
//        }
        // calculate loss distribution
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
        System.out.println("NoFilter Win: " + tempWin + ", Loss: " + tempLoss);
        System.out.println("NoFilter Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));
        System.out.println("----------------------------------------");
        System.out.println("\n");
        for(Integer key: maxLossInRowDist.keySet()) {
            if(key > 0) {
                System.out.println(key + " :" + maxLossInRowDist.get(key));
            }

        }

    }

    public void calculate2(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                           ArrayList<Integer> filterBuySellSignal) {
        // buy/sell and hold till next trend reverse

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

    public void calculate3(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                           ArrayList<Integer> filterBuySellSignal) {
    // only allowing max 5 fails in a row, then starts from initial lot size
        int total = priceData.size();
        int currentSignal = -1; // 0 is sell, 1 is buy
        int prevSignal = -1;
        double lastDealPrice = 0;
        int position = 0;
        double boughtPrice = 0;
        double soldPrice = 0;
        int initialLosSize = 1;
        double totalProfit = 0;
        double profitSize = 50 * 0.00001;
        ArrayList<Integer> winLossList = new ArrayList<Integer>();
        int maxLossCountAllowed = 3;


        for(int index = 0; index < total; index++) {

            // load signal from filter
            currentSignal = filterBuySellSignal.get(index);
            if(index -1 < 0) {
                prevSignal = currentSignal;
            } else {
                prevSignal = filterBuySellSignal.get(index - 1);
            }
            // 1. reverse to up
            if(currentSignal == 1 && prevSignal == 0) {
                // buy
                int positionDirection = 0;
                if(position > 0) {
                    positionDirection = 1;
                }
                if(position == 0) {
                    positionDirection = 0;
                }
                if(position < 0) {
                    positionDirection = -1;
                }

                switch (positionDirection) {
                    case 1:
                        System.out.println("can't buy when position is positive, system error");
                        break;
                    case 0:
                        position = initialLosSize;
                        boughtPrice = priceData.get(index);
                        //System.out.println("bought at price: " + boughtPrice + " with position: " + position);
                        break;
                    case -1:
                        double currentPrice = priceData.get(index);
                        double singleProfit = soldPrice - currentPrice;
                        double dealProfit = singleProfit * Math.abs(position);
                        totalProfit = totalProfit + dealProfit;
                        //System.out.println("dealProfit: " + dealProfit);
                        //System.out.println("totalProfit: " + totalProfit);

                        // if direction is -1, then this has to be a losing deal.
                        // TODO: check how many loosing position before... to determine next position, max previous loss
                        //     is 5
                        winLossList.add(0); // losing
                        int maxCount = Util.maxPrevLoseCount(winLossList);
                        int calculatedCount = maxCount % maxLossCountAllowed;
                        // 1  0
                        // 2  1
                        // 4  2
                        // 8  3
                        // 16 4

                        //System.out.println("winLossList add: 0, currentSignal: " + currentSignal);
                        position = (int)Math.pow(2, calculatedCount);


                        boughtPrice = currentPrice;
                        //System.out.println("bought at price: " + boughtPrice + " with position: " + position);


                        break;
                    default:
                        System.out.println(" position direction error ");

                }

            }
            // 2. reverse down
            if(currentSignal == 0 && prevSignal == 1) {
                // sell

                int positionDirection = 0;
                if(position > 0) {
                    positionDirection = 1;
                }
                if(position == 0) {
                    positionDirection = 0;
                }
                if(position < 0) {
                    positionDirection = -1;
                }
                switch (positionDirection) {
                    case 1:
                        double currentPrice = priceData.get(index);
                        double singleProfit = currentPrice - boughtPrice;
                        double dealProfit = singleProfit * Math.abs(position);
                        totalProfit = totalProfit + dealProfit;
                        //System.out.println("dealProfit: " + dealProfit);
                        //System.out.println("totalProfit: " + totalProfit);
                        // if current signal is sell, and current position is 1, then it must be a losing deal
                        winLossList.add(0); // losing
                        int maxCount = Util.maxPrevLoseCount(winLossList);
                        int calculatedCount = maxCount % maxLossCountAllowed;


                        //System.out.println("winLossList add: 1, currentSignal: " + currentSignal);
                        // open new position
                        position = -(int)Math.pow(2, calculatedCount);
                        soldPrice = currentPrice;
                        //System.out.println("sold at price: " + soldPrice + " with position: " + Math.abs(position));
                        break;
                    case 0:

                        position = -initialLosSize;
                        soldPrice = priceData.get(index);
                        //System.out.println("sold at price: " + soldPrice);

                        break;
                    case -1:
                        System.out.println("can't sell when position is negative, system error");
                        break;
                    default:
                        System.out.println(" position direction error ");
                }

            }

            // 3. up trend continue
            if(currentSignal == 1 && prevSignal == 1) {
                //
                if(position > 0) {
                    // now position should > 0
                    double currentPrice = priceData.get(index);
                    double singleProfit = currentPrice - boughtPrice;
                    double dealProfit = singleProfit * Math.abs(position);
                    totalProfit = totalProfit + dealProfit;
                    //System.out.println("dealProfit: " + dealProfit);

                    //System.out.println("totalProfit: " + totalProfit);

                    position = 0; // close all position
                    winLossList.add(1);
                    //System.out.println("winLossList add: 1, currentSignal: " + currentSignal);
                    boughtPrice = 0;
                    soldPrice = 0;
                }

                if(position < 0) {
                    System.out.println("error, up trend continues, but has negative position");
                }




            }

            // 4. down trend continue
            if(currentSignal == 0 && prevSignal == 0) {

                if(position <0) {
                    double currentPrice = priceData.get(index);
                    double singleProfit = soldPrice - currentPrice;
                    double dealProfit = singleProfit * Math.abs(position);
                    totalProfit = totalProfit + dealProfit;
                    //System.out.println("dealProfit: " + dealProfit);

                    //System.out.println("totalProfit: " + totalProfit);

                    position = 0; // close all position
                    winLossList.add(1);
                    //System.out.println("winLossList add: 1, currentSignal: " + currentSignal);
                    boughtPrice = 0;
                    soldPrice = 0;
                }

                if(position > 0) {
                    System.out.println("error, down trend continues, but has positive position");
                }
            }
        }
    System.out.println("total profit: " + totalProfit);
    }
}
