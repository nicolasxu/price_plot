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

    static public void findTickPatternSignal(ArrayList<Double> inputTicks, int targetCount, ArrayList<TickSignal> signals){
        if (signals == null) {
            System.out.println("signals object is empty");
            return;
        }

        // find continus x > x + 1, or x < x + 1 pattern for count in a row.
        System.out.println("Finding patterns in "+ inputTicks.size() +" ticks...");
        double prevousValue = 0;
        int directionCounter = 0;
        int previousDirection = 0; // -1: below, 1: above, 0: initial value
        int hitCounter = 0; // times that meets criteria
        int previousHitTickIndex = 0;

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
                // filter out the consecutive hits
                if(tickIndex != previousHitTickIndex + 1 ) {
                    // hit
                    hitCounter++;
                    System.out.println("more than "+ targetCount + " at index: "+ tickIndex + " " + previousDirection);
                    signals.add(new TickSignal(tickIndex, previousDirection));

                    previousHitTickIndex = tickIndex;
                    directionCounter = 0;
                }


            }

            prevousValue = currentValue;
        }

        System.out.println("total hit: " +hitCounter + "\n");
        System.out.println("Finding pattern done!");

    }

    static public void runSim(ArrayList<Double> inputTicks, ArrayList<TickSignal> signals){

        ArrayList<SimOrder> orders = new ArrayList<SimOrder>();
        double totalProfit = 0;
        double point = 0.00001;
        double tpPoints = 80 * point;
        double slPoints = 30  * point;
        int volume = 10000;

        for(int tickIndex = 0; tickIndex < inputTicks.size(); tickIndex++) {

            // 1. process signals at each tick
            for(int signalIndex = 0; signalIndex < signals.size(); signalIndex++) {

                TickSignal thisSignal = signals.get(signalIndex);
                if(tickIndex == thisSignal.tickIndex) {
                    // time to create buy or sell order
                    if(thisSignal.signal == 1) {
                        // buy
                        double currentPrice = inputTicks.get(tickIndex);
                        SimOrder buyOrder = new SimOrder();
                        // open(int flag, double tp, double sl, double openPrice, int volume)
                        buyOrder.open(1, tpPoints > 0? currentPrice + tpPoints: 0 ,
                                slPoints > 0? currentPrice - slPoints: 0,
                                currentPrice,
                                volume);
                        orders.add(buyOrder);
                    }

                    if(thisSignal.signal == -1) {
                        // sell
                        double currentPrice = inputTicks.get(tickIndex);
                        SimOrder sellOrder = new SimOrder();
                        // open(int flag, double tp, double sl, double openPrice, int volume)
                        sellOrder.open(-1, tpPoints>0? currentPrice - tpPoints:0,
                                slPoints > 0? currentPrice + slPoints:0,
                                currentPrice,
                                volume);
                        orders.add(sellOrder);
                    }

                }
            }

            // 2. process opened order at each tick
            for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++ ) {
                SimOrder currentOrder = orders.get(orderIndex);
                double currentTickPrice = inputTicks.get(tickIndex);
                double profit = currentOrder.trySLandTP(currentTickPrice);
                totalProfit = totalProfit + profit;
            }

        }

        // 3. process un-closed orders
        int unclosedOrderCount = 0;
        double lastPrice = inputTicks.get(inputTicks.size() -1);
        for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
            SimOrder currentOrder = orders.get(orderIndex);
            if(currentOrder.closePrice == 0) {
                unclosedOrderCount++;
                double thisProfit = currentOrder.close(lastPrice);
                totalProfit = totalProfit + thisProfit;
            }
        }
        System.out.println("Unclosed order count: " + unclosedOrderCount);

        System.out.println("Total sim profit is: " + totalProfit);

    }

    /* Convert raw tick to step ticks */
    static public void tickToStep(ArrayList<Double> inputTicks, double stepPoints, ArrayList<Double> stepTicks) {
        // points example: 50 * 0.00001
        // one point is: 0.00001

        for(int tickIndex = 0; tickIndex < inputTicks.size(); tickIndex++) {
            if(tickIndex > 0) {
                if(Math.abs( inputTicks.get(tickIndex) - stepTicks.get(stepTicks.size() -1)) >= stepPoints) {
                    stepTicks.add(inputTicks.get(tickIndex));
                }
            } else {
                stepTicks.add(inputTicks.get(0));
            }
        }
    }

    /*
     *  1. take step ticks
     *  2. buy/sell only at turn, e.g: -1 to 1, or 1 to -1
     *  3. tp or sl may set
     * */
    static public void runSimForStepTicks(ArrayList<Double> stepTicks, ArrayList<Integer> signals) {
        // -1 sell, 1 buy, 0 neutral

        int previousSignal = 0;
        int currentSignal = 0;
        double point = 0.00001;
        double tpPoints = 50 * point;
        double slPoints = 150  * point;
        int originalVolume = 100000; // 100k
        int volume = 100000; // 100k
        double totalProfit = 0;
        ArrayList<SimOrder> orders = new ArrayList<SimOrder>();

        if(stepTicks.size() != signals.size()) {
            System.out.println("stepTicks.size(): " + stepTicks.size() + " signals.size(): " + signals.size());
            System.out.println("error: signal count doesn't equal tick count");
            return;
        }

        for(int tickIndex = 0; tickIndex < stepTicks.size(); tickIndex++) {

            // 1. process signal
            if (tickIndex == 0) {
                // 1st tick
                previousSignal = signals.get(tickIndex);
                currentSignal = previousSignal;

            } else {
                // 2nd and on
                currentSignal = signals.get(tickIndex);


                if(previousSignal == -1 && currentSignal == 1) {
                    // buy
                    double currentPrice = stepTicks.get(tickIndex);
                    SimOrder buyOrder = new SimOrder();

                    buyOrder.open(1, tpPoints > 0? currentPrice + tpPoints: 0 ,
                            slPoints > 0? currentPrice - slPoints: 0,
                            currentPrice,
                            volume);
                    orders.add(buyOrder);
                    volume = originalVolume;
                    System.out.println(tickIndex + ": buy");



                }

                if(previousSignal == 1 && currentSignal == -1) {
                    // sell

                    double currentPrice = stepTicks.get(tickIndex);
                    SimOrder sellOrder = new SimOrder();
                    // open(int flag, double tp, double sl, double openPrice, int volume)
                    sellOrder.open(-1, tpPoints>0? currentPrice - tpPoints:0,
                            slPoints > 0? currentPrice + slPoints:0,
                            currentPrice,
                            volume);
                    orders.add(sellOrder);
                    volume = originalVolume;
                    System.out.println(tickIndex + ": sell");


                }

                // end
                previousSignal = currentSignal;
            }

            // 2. process open order

            for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++ ) {
                SimOrder currentOrder = orders.get(orderIndex);
                double currentTickPrice = stepTicks.get(tickIndex);
                double profit = currentOrder.trySLandTP(currentTickPrice);
                totalProfit = totalProfit + profit;

                // 2.1 adjust next order position if loss
                if(profit < 0) {
                    volume = volume * 3;
                }
            }

        }

        // 3. process un-closed order
        int profitOrderCount = 0;
        int lossOrderCount = 0;
        int unclosedOrderCount = 0;
        double lastPrice = stepTicks.get(stepTicks.size() -1);
        for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
            SimOrder currentOrder = orders.get(orderIndex);
            if(currentOrder.closePrice == 0) {
                unclosedOrderCount++;
                double thisProfit = currentOrder.close(lastPrice);
                totalProfit = totalProfit + thisProfit;
            }

            if(currentOrder.pl > 0){
                profitOrderCount++;
            } else {
                lossOrderCount++;
            }
        }
        System.out.println("Unclosed order count: " + unclosedOrderCount);
        System.out.println("profit order count: " + profitOrderCount);
        System.out.println("loss order count: " + lossOrderCount);
        System.out.println("Total sim profit is: " + totalProfit);


    }

    static public void compressStepData(ArrayList<Double> input, ArrayList<Double> output) {

        double currentPrice = 0;
        double pm1Price = 0;
        double pm2Price = 0;
        int lastBuyIndex     = 0;
        int lastSellIndex    = 0;
        double lastBuyPrice  = 0;
        double lastSellPrice = 0;

        int rangeEndIndex    = 0;



        for(int inputIndex = 0; inputIndex < input.size(); inputIndex++) {




            if(inputIndex <= 1) {
                // 0, 1 index
                currentPrice = input.get(inputIndex);
                pm1Price = input.get(Math.max(0, inputIndex -1));
                pm2Price = input.get(Math.max(0, inputIndex -2));
                output.add(currentPrice);
            } else {
                // 2 and on...
                currentPrice = input.get(inputIndex);
                pm1Price     = input.get(inputIndex - 1);
                pm2Price     = input.get(inputIndex - 2);

                output.add(currentPrice);

                if(Math.abs(lastBuyIndex - lastSellIndex) == 1) {
                    //System.out.println("range formed at index: " + lastSellIndex);
                   // range established
                    double max = lastBuyPrice;
                    double min = lastSellPrice;

                    if(rangeEndIndex == 0) {
                        rangeEndIndex = inputIndex - 1;
                    }

                    if (Math.abs(currentPrice - max) <= 15 * 0.00001 ||
                            Math.abs(currentPrice - min) <= 15 * 0.00001) {
                        //System.out.println("within range");
                        // still within range established before
                        output.remove(output.size() -1);
                        // remove last added, since it is still within range

                        // roll back by one tick
                        currentPrice = input.get(rangeEndIndex );
                        pm1Price     = input.get(rangeEndIndex - 1);
                        pm2Price     = input.get(rangeEndIndex - 2);


                    } else {
                        // price moving out of range, reset range end index
                        rangeEndIndex = 0;
                    }

                }

                if(currentPrice > pm1Price && pm2Price > pm1Price) {
                    // buy
                    lastBuyIndex = inputIndex;
                    lastBuyPrice = currentPrice;

                }

                if(currentPrice < pm1Price && pm2Price < pm1Price) {
                    // sell
                    lastSellIndex = inputIndex;
                    lastSellPrice = currentPrice;
                }

                //System.out.println("lastBuyIndex: " + lastBuyIndex + " lastSellIndex: " + lastSellIndex);


                pm1Price = currentPrice;
            }

        }

    }

    static public void runSim3(ArrayList<Double> stepTicks, ArrayList<Integer> signals) {
        // variable position size based on prev p&l
        // 1, -1 sell
        // -1, 1 buy
        // X, 0 close position
        // no stop loss
        if(stepTicks.size() != signals.size()) {
            System.out.println("signal size doesn't match input tick size");
            return;
        }

        double takeProfitPoints = 100 * 0.00001;
        double stopLossPoint = 0 * 0.00001;
        int originalVolume = 100000; // 10k
        int currentVolume = originalVolume;
        int maxVolume = originalVolume * 10; // 10 times original volume

        int currentSignal  = 0;
        int previousSignal = 0;
        double totalProfit = 0;
        double thisProfit = 0;
        int buyFlag = 1;
        int sellFlag = -1;
        int lossVolumeToAmortize = 0;

        ArrayList<SimOrder> orders = new ArrayList<SimOrder>();
        double currentPrice = 0;

        for(int tickIndex = 0; tickIndex < stepTicks.size(); tickIndex++ ) {

            if (tickIndex < 1) {
                // index 0
                currentSignal = signals.get(tickIndex);
                previousSignal = currentSignal;
                currentPrice = stepTicks.get(tickIndex);
            } else {
                // index 1 and on
                currentSignal = signals.get(tickIndex);
                previousSignal = signals.get(tickIndex - 1);
                currentPrice = stepTicks.get(tickIndex);

                if (currentSignal == 1 && previousSignal == 1) {

                    for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
                        SimOrder order = orders.get(orderIndex);
                        order.trySLandTP(currentPrice);

                    }
                }

                if(currentSignal == -1 && previousSignal == -1) {
                    for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
                        SimOrder order = orders.get(orderIndex);
                        order.trySLandTP(currentPrice);

                    }
                }

                if(currentSignal == 1 && previousSignal == -1) {
                    // reverse

                    // close previous order
                    for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
                        SimOrder order = orders.get(orderIndex);
                        order.close(currentPrice);
                    }

                    // set next volume based on previous order p&l
                    if(orders.size() > 0) {
                        SimOrder lastOrder = orders.get(orders.size() -1);
                        if(lastOrder.pl < 0) {
                            // loss
                            // update lossVolumeToAmortize
                            lossVolumeToAmortize =(int)(lossVolumeToAmortize + (-lastOrder.pl) / takeProfitPoints);

                        }

                        // calculate next volume size
                        if(lossVolumeToAmortize > 0) {
                            if (lossVolumeToAmortize + originalVolume < maxVolume) {
                                currentVolume = lossVolumeToAmortize + originalVolume;
                                lossVolumeToAmortize = 0;
                            } else {
                                currentVolume = maxVolume;
                                lossVolumeToAmortize = lossVolumeToAmortize - maxVolume;
                                lossVolumeToAmortize = lossVolumeToAmortize + originalVolume;
                            }
                        } else {
                            currentVolume = originalVolume;
                        }

                    }

                    // open new buy order
                    SimOrder buyOrder = new SimOrder();
                    buyOrder.open(buyFlag,
                            takeProfitPoints > 0? currentPrice + takeProfitPoints: 0,
                            stopLossPoint > 0? currentPrice - stopLossPoint: 0,
                            currentPrice,
                            currentVolume);

                    orders.add(buyOrder);
                    System.out.println("buy at " + tickIndex);

                }

                if(currentSignal == -1 && previousSignal == 1) {
                    // reverse
                    // close previous order
                    for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
                        SimOrder order = orders.get(orderIndex);
                        order.close(currentPrice);
                    }

                    // set next volume based on previous order p&l
                    if(orders.size() > 0) {
                        SimOrder lastOrder = orders.get(orders.size() -1);
                        if(lastOrder.pl < 0) {
                            // loss
                            // update lossVolumeToAmortize
                            lossVolumeToAmortize =(int)(lossVolumeToAmortize + (-lastOrder.pl) / takeProfitPoints);

                        }

                        // calculate next volume size
                        if(lossVolumeToAmortize > 0) {
                            if (lossVolumeToAmortize + originalVolume < maxVolume) {
                                currentVolume = lossVolumeToAmortize + originalVolume;
                                lossVolumeToAmortize = 0;
                            } else {
                                currentVolume = maxVolume;
                                lossVolumeToAmortize = lossVolumeToAmortize - maxVolume;
                                lossVolumeToAmortize = lossVolumeToAmortize + originalVolume;
                            }
                        } else {
                            currentVolume = originalVolume;
                        }

                    }

                    // open new sell order
                    SimOrder sellOrder = new SimOrder();
                    sellOrder.open(sellFlag,
                            takeProfitPoints > 0 ? currentPrice - takeProfitPoints : 0,
                            stopLossPoint > 0 ? currentPrice + stopLossPoint : 0,
                            currentPrice,
                            currentVolume);

                    orders.add(sellOrder);
                    System.out.println("sell at " + tickIndex);


                }

                if(currentSignal == 0) {
                    // enter to slow changing
                    // close all orders
                    for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
                        SimOrder order = orders.get(orderIndex);
                        order.close(currentPrice);
                    }
                }





            }


        }

        for(int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
            SimOrder order = orders.get(orderIndex);
            order.close(currentPrice);
            totalProfit = totalProfit + order.pl;
        }

        System.out.println("Total profit is: " + totalProfit);





    }
}
