package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/24/15.
 */
public class HybridFilter extends IFilter {
    // 4 max loss in NoFilter, then to ALStepFilter till next win, then switch back to NoFilter
    AlStepFilter primaryFilter;
    NoFilter secondaryFilter;
    ArrayList<Double> primaryOutput;
    ArrayList<Double> secondaryOutput;

    public HybridFilter() {
        this.primaryFilter = new AlStepFilter(10);
        this.secondaryFilter = new NoFilter();

        this.primaryOutput = new ArrayList<Double>();
        this.secondaryOutput = new ArrayList<Double>();

    }
    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        this.primaryFilter.filter(input, this.primaryOutput);
        this.secondaryFilter.filter(input, this.secondaryOutput);

    }

    public void calculate(ArrayList<Double> priceData) {
        int total = priceData.size();
        int signalToUse = 0; // 0 is primary, 1 is secondary
        int currentSignal = -1;
        int prevSignal = -1;
        int position = 0;
        int initialLotSize = 1;
        double boughtPrice = 0;
        double soldPrice = 0;
        double profitSize = 50 * 0.00001;
        ArrayList<Integer> winLossRecord = new ArrayList<Integer>();
        double totalProfit = 0;
        int maxLossCount = 30;

        for(int index = 0; index < total; index++) {

            // getting signals
            if(signalToUse == 0) {
                // getting primary signal
                currentSignal = this.primaryFilter.buySellSignal.get(index);
                if(index > 0) {
                    prevSignal = this.primaryFilter.buySellSignal.get(index -1);
                } else {
                    prevSignal = currentSignal;
                }
            } else {
                // getting secondary signal
                currentSignal = this.secondaryFilter.buySellSignal.get(index);
                if(index > 0) {
                    prevSignal = this.secondaryFilter.buySellSignal.get(index -1);
                } else {
                    prevSignal = currentSignal;
                }
            }

            // 1. reverse up
            if(currentSignal ==1 && prevSignal == 0) {
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
                    case 0:
                        // open new position
                        position = initialLotSize;
                        boughtPrice = priceData.get(index);

                        break;
                    case 1:
                        if(position > 0) {
                            System.out.println("already have positive position, do nothing");
                        }
                        break;
                    case -1:
                        // losing money
                        // 1. calculate loss
                        // 2. find out next position
                        // 3. open new position
                        // 4. check change signal

                        double currentPrice = priceData.get(index);
                        double singleProfit = soldPrice - currentPrice;
                        double dealProfit = singleProfit * Math.abs(position); // it is a lost here
                        totalProfit = totalProfit + dealProfit;
                        winLossRecord.add(0);
                        double prevLossCount = Util.maxPrevLoseCount(winLossRecord);
                        if (prevLossCount >= maxLossCount) {
                            signalToUse = 1;
                        }
                        position = (int)Math.pow(2, prevLossCount);
                        boughtPrice = currentPrice;

                        break;
                    default:
                        System.out.println("position error");
                }


            }

            // 2. reverse down
            if(currentSignal ==0 && prevSignal ==1) {
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
                    case 0:
                        position = -initialLotSize;
                        soldPrice = priceData.get(index);
                        break;
                    case 1:
                        // losing money
                        // 1. calculate loss
                        double currentPrice = priceData.get(index);
                        double singleProfit = currentPrice - boughtPrice;
                        double dealProfit = singleProfit * Math.abs(position); // it is a lost here
                        totalProfit = totalProfit + dealProfit;
                        winLossRecord.add(0);
                        double prevLossCount = Util.maxPrevLoseCount(winLossRecord);
                        if (prevLossCount >= maxLossCount) {
                            signalToUse = 1;
                        }
                        position = -(int)Math.pow(2, prevLossCount);
                        soldPrice = currentPrice;
                        // 2. find out next position
                        // 3. open new position
                        // 4. check change signal

                        break;
                    case -1:
                        if(position < 0) {
                            System.out.println("already have negative position, do nothing");
                        }
                        break;
                    default:
                        System.out.println("position error");
                }
            }

            // 3. up continue
            if(currentSignal ==1 && prevSignal ==1) {
                if( position > 0) {
                    // taking profit
                    double currentPrice = priceData.get(index);
                    if (currentPrice - boughtPrice >= profitSize) {
                        // close
                        double singleProfit = currentPrice - boughtPrice;
                        double dealProfit = singleProfit * Math.abs(position);
                        totalProfit = totalProfit + dealProfit;
                        position = 0;
                        winLossRecord.add(1); // win
                        boughtPrice = 0;
                        soldPrice = 0;
                        signalToUse = 0; // switch back to primary signal
                    }

                }
            }

            // 4. down continue
            if(currentSignal == 0 && prevSignal == 0) {
                if(position < 0) {
                    double currentPrice = priceData.get(index);
                    if(soldPrice - currentPrice >= profitSize) {
                        // close
                        double singleProfit = soldPrice - currentPrice;
                        double dealProfit = singleProfit * Math.abs(position);
                        totalProfit = totalProfit + dealProfit;
                        position = 0;
                        winLossRecord.add(1); // win
                        boughtPrice = 0;
                        soldPrice = 0;
                        signalToUse = 0;
                    }
                }
            }

            // 5 - 1. just close if profiting
            if(position > 0) {
                double currentPrice = priceData.get(index);
                if(currentPrice - boughtPrice >= profitSize) {
                    // closing
                    double singleProfit = currentPrice - boughtPrice;
                    double dealProfit = singleProfit * Math.abs(position);
                    totalProfit = totalProfit + dealProfit;
                    position = 0;
                    winLossRecord.add(1); // win
                    boughtPrice = 0;
                    soldPrice = 0;
                    signalToUse = 0;

                }
            }
            // 5 - 2. just taking profit
            if (position < 0) {
                double currentPrice = priceData.get(index);
                if(soldPrice - currentPrice >= profitSize) {
                    // closing
                    double singleProfit = soldPrice - currentPrice;
                    double dealProfit = singleProfit * Math.abs(position);
                    totalProfit = totalProfit + dealProfit;
                    position = 0;
                    winLossRecord.add(1); // win
                    boughtPrice = 0;
                    soldPrice = 0;
                    signalToUse = 0;
                }
            }
        }
        System.out.println("totalProfit is: " + totalProfit);
    }
}
