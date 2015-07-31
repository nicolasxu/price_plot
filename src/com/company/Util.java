package com.company;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/23/15.
 */
public class Util {
    static public int maxPrevLoseCount(ArrayList<Integer> profitLossRecord) {
        if (profitLossRecord == null) {
            return 0;
        }

        if(profitLossRecord.size() == 0 ) {
            return 0;
        }

        int lastIndex = profitLossRecord.size() - 1;
        int currentIndex = lastIndex;
        int count = 0;
        int currntWinLoss = 0; // initialize as 0
        while (currentIndex >=0 && currntWinLoss == 0 ) {
            if(profitLossRecord.get(currentIndex) == 0) {
                count++;

            }
            currntWinLoss = profitLossRecord.get(currentIndex);

            currentIndex--;
        }

        return count;
    }
    static public void calculateWinLoss(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                                 ArrayList<Integer> filterBuySellSignal, String filterName) {
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
        //System.out.println(filterName + " P&L with ()");
        System.out.println(filterName + " Win: " + tempWin + ", Loss: " + tempLoss);
        System.out.println(filterName + " Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

        for(Integer key: maxLossInRowDist.keySet()) {
            if(key > 0) {
                System.out.println(key + " :" + maxLossInRowDist.get(key));
            }

        }
        System.out.println("----------------------------------------");
        System.out.println("\n");

    }

    static public void calculateBuyHold(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                           ArrayList<Integer> filterBuySellSignal, String FilterName) {
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
        System.out.println(FilterName + " total profit is: " + totalPL);
    }


    static public void calculateCapStrategy(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                                         ArrayList<Integer> filterBuySellSignal, String FilterName) {
        // max wrong 4 times, -> max position: pow(2, 4) = 8. Amount more than 8 has to be amortised
        // by doubling the initial amount
        int total = priceData.size();
        int currentSignal;
        int previousSignal;
        boolean bought = false;
        boolean sold = false;
        double boughtPrice = 0.0;
        double soldPrice = 0.0;
        double totalProfit = 0;


        double profitPoints = 50 * 0.00001; // 50 points
        ArrayList<Integer> winLossList = new ArrayList<Integer>();
        ArrayList<Integer> winLossInputIndex = new ArrayList<Integer>();

        double initialLotSize = 1;
        double position = 0;
        double cumulativeLossBelowCap = 0;
        int amortiseFactor = 2; // recover lot size =  initialLotSize * amortiseFactor;

        double capPosition = initialLotSize * 8; // e.g.: 100k * 8

        double lotToAmortize = 0;

        for(int index = 0; index < total; index++) {

            currentSignal = filterBuySellSignal.get(index);
            if(index -1 < 0) {
                previousSignal = filterBuySellSignal.get(0);
            } else {
                previousSignal = filterBuySellSignal.get(index - 1);
            }

            // 1. reverse to up trend
            if(currentSignal == 1 && previousSignal == 0) {
                if(position > 0) {
                    System.out.println("error, signal, 1, 0, position: " + position);
                }
                if(position < 0) {
                    // previous negative position not closed, it is losing deal
                    // 1. calculate the deal loss
                    double currentPrice = priceData.get(index);
                    double thisLoss = (soldPrice - currentPrice) * Math.abs(position); // value is negative

                    // 2. add deal loss to cumulative loss, since we need to use cumulative loss to calculate
                    //    next position size, cumulative loss will be reset to 0 in the immediate next profit deal
                    cumulativeLossBelowCap = cumulativeLossBelowCap + Math.abs(thisLoss);
                    // 3. calculate next possible position
                    double nextPossiblePosition = cumulativeLossBelowCap / profitPoints + initialLotSize;
                    double nextDealSize = 0;
                    if(nextPossiblePosition - capPosition > 0) {
                        // next position is over capPosition;
                        // update cumulative loss over the cap position.

                        lotToAmortize = nextPossiblePosition - capPosition;
                        cumulativeLossBelowCap = cumulativeLossBelowCap - (lotToAmortize - initialLotSize) * profitPoints;
                        // lotToAmortize will only be amortized when opening new position, whereas cumulativeLossBelowCap
                        //  is reset to 0 in the immediate next profiting deal, meaning
                        nextDealSize = capPosition;
                    } else {
                        // loss is below or equal the loss cap
                        nextDealSize = nextPossiblePosition;

                    }
                    position = nextDealSize; // buying
                    boughtPrice = priceData.get(index);

                }
                if(position == 0) {
                    if (lotToAmortize > 0) {

                        position = amortiseFactor * initialLotSize; // buying
                        // adjust lossAboveCap
                        lotToAmortize = lotToAmortize - (amortiseFactor * initialLotSize - initialLotSize );
                        // e.g. : 750 - 50
                    } else {
                        // no cumulative loss
                        position = initialLotSize;
                    }
                    boughtPrice = priceData.get(index);
                }

            }

            // 2. reverse to down trend
            if(currentSignal == 0 && previousSignal == 1) {
                if(position > 0) {

                }
                if(position < 0) {
                    System.out.println("error, signal, 0, 1, position: " + position);
                }
                if(position == 0) {
                    if(lotToAmortize > 0) {

                        position = -amortiseFactor * initialLotSize; // selling
                        // adjus lossAboveCap
                        lotToAmortize = lotToAmortize - (amortiseFactor * initialLotSize - initialLotSize);
                    } else {
                        // no cumulative loss
                        position = -initialLotSize;
                    }
                    soldPrice = priceData.get(index);
                }

            }

            // 3. up trend continue
            if(currentSignal ==1 && previousSignal ==1) {
                // buy trend continue
                if(position > 0) {
                    // set cumulativeLossBelowCap to 0
                    // 1. calculate deal profit
                    double currentPrice = priceData.get(index);
                    double thisProfit = (currentPrice - boughtPrice) * position;
                    totalProfit = totalProfit + thisProfit;

                    // 2. close position to take profit
                    position = 0;

                    // 2. reset cumulativeLossBelowCap
                    cumulativeLossBelowCap = 0;

                }
                if(position < 0) {
                    System.out.println("error, signal 1, 1, position: " + position);
                }
                if(position == 0) {
                    // do nothing
                }

            }

            // 4. down trend continue
            if(currentSignal == 0 && previousSignal == 0) {
                // sell trend continue
                if(position > 0) {

                }
                if(position < 0) {

                }
                if(position == 0) {
                    // do nothing

                }

            }
        }

        int tempWin = 0, tempLoss = 0;
        int maxLossRow = 0, tempMaxLossRow = 0;
        int maxLossRowPoint = 0;
        HashMap<Integer, Integer> maxLossInRowDist = new HashMap<Integer, Integer>();

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
        //System.out.println(filterName + " P&L with ()");
        //System.out.println(filterName + " Win: " + tempWin + ", Loss: " + tempLoss);
        //System.out.println(filterName + " Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

        for(Integer key: maxLossInRowDist.keySet()) {
            if(key > 0) {
                //System.out.println(key + " :" + maxLossInRowDist.get(key));
            }

        }
        //System.out.println("----------------------------------------");
        //System.out.println("\n");




    }
}
