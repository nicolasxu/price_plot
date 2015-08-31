package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/23/15.
 */
public class Util {
    static public void readCSVFileTo(String fileName, ArrayList<Double> data) {

        //fileName = "ticks2015.08.26.csv"; //"tick50diff.csv";
        String filePath = "/Users/nick/IdeaProjects/price_plot/";

        FileReader fr;
        BufferedReader br;
        String line = null;

        try {
            fr = new FileReader(filePath + fileName);
            br = new BufferedReader(fr);
            while((line = br.readLine()) != null) {


                String[] columns = line.split(",");
                double bid, ask, mid;

                if(fileName.contains("IB") || fileName.contains("ticks")) {
                    bid = Double.parseDouble(columns[1]);
                    ask = Double.parseDouble(columns[2]);
                    mid = (bid + ask) / 2;
                } else {
                    bid = Double.parseDouble(columns[0]);
                    ask = Double.parseDouble(columns[1]);
                    mid = (bid + ask) / 2;
                }


                if(data == null) {
                    data = new ArrayList<Double>();
                }
                data.add(mid);

                //System.out.println("bid:" + bid + " ask: " + ask);

            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }


    }
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
        System.out.println(filterName + " Win: " + tempWin + ", Loss: " + tempLoss + " for total: " + priceData.size());
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

        CapStrategy strategy = new CapStrategy(priceData, filterOutput, filterBuySellSignal, FilterName);
        strategy.calculate();

    }

    static public void calculateContinuousSignalDistribution(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                                                             ArrayList<Integer> filterBuySellSignal, String FilterName) {
        // this method is intended for no filter filter only

        int counter = 0;
        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        int prevSignal = -1;
        for(int index = 0; index < filterBuySellSignal.size(); index++) {
            int currSignal = filterBuySellSignal.get(index);
            if(currSignal != -1) {
                // valid signal

                if(currSignal == prevSignal) {
                    counter++;
                } else {
                    Integer currDist = dist.get(counter);
                    if(currDist == null) {
                        // no key exist
                        dist.put(counter, 1);
                    } else {
                        // key exist
                        currDist++;
                        dist.put(counter, currDist);
                    }
                    // reset counter;
                    counter = 1;
                }

                prevSignal = currSignal;
            }
        }
        System.out.println("Continous signal distribution: ");
        for(Integer key: dist.keySet()) {
            System.out.println(key + " : " + dist.get(key));
        }
    }

    static public void calculateTickDistribution(ArrayList<Double> priceData) {
        int maxDistributionNumber = 800;
        int minDistributionNumber = 8;


    }

    static public void findTickPattern(ArrayList<Double> inputTicks, int targetCount){
        // find continus x > x + 1, or x < x + 1 pattern for count in a row.
        System.out.println("Finding patterns in "+ inputTicks.size() +" ticks...");
        double prevousValue = 0;
        int directionCounter = 0;
        int previousDirection = 0; // -1: below, 1: above, 0: initial value
        int hitCounter = 0; // times that meets criteria

        for(int tickIndex = 0; tickIndex < inputTicks.size(); tickIndex++ ) {

            double currentValue = inputTicks.get(tickIndex);

            if (currentValue >= prevousValue ) {
                int currentDirection = 1;
                if (previousDirection > 0) {
                    directionCounter++;

                } else {
                    directionCounter = 0;

                }
                previousDirection = currentDirection;
            } else {
                int currentDirection = -1;
                if(previousDirection < 0) {
                    directionCounter++;
                } else {
                    directionCounter = 0;
                }
                previousDirection = currentDirection;
            }

            if(directionCounter >= targetCount) {
                System.out.println("more than "+ targetCount + " at index: "+ tickIndex);
                hitCounter++;
            }

            prevousValue = currentValue;
        }

        System.out.println("Finding pattern done!");
        System.out.println("Result: " +hitCounter+ " hits");

    }
}
