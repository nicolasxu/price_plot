package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 8/1/15.
 */
public class CapStrategy {

    ArrayList<Double> priceData;
    ArrayList<Double> filterOutput;
    ArrayList<Integer> filterBuySellSignal;
    String filterName;

    // private
    private double position;
    private double totalProfit;
    private double boughtPrice;
    private double soldPrice;
    private double cumulativeLossBelowCap;
    private double lotToAmortize;
    private double amortiseFactor;
    private double initialLotSize;
    private double profitPoints;
    private double capPosition;
    private boolean showConsoleLog;
    private int profitDealCount;



    public CapStrategy(ArrayList<Double> priceData, ArrayList<Double> filterOutput,
                       ArrayList<Integer> filterBuySellSignal, String filterName) {
        // max wrong 4 times, -> max position: pow(2, 4) = 8. Amount more than 8 has to be amortised
        // by doubling the initial amount

        this.priceData = priceData;
        this.filterOutput = filterOutput;
        this.filterBuySellSignal = filterBuySellSignal;
        this.filterName = filterName;

        // init
        this.position = 0;
        this.totalProfit = 0;
        this.boughtPrice = 0;
        this.soldPrice = 0;
        this.cumulativeLossBelowCap = 0;
        this.lotToAmortize = 0;
        this.amortiseFactor = 2;
        this.initialLotSize = 1;
        this.profitPoints = 50 * 0.00001;
        this.capPosition = this.initialLotSize * 16;
        this.showConsoleLog = false;
        this.profitDealCount = 0;

    }
    private void logThis(String msg) {
        if(this.showConsoleLog == true) {
            System.out.println(msg);
        }
    }

    public void calculate() {

        int currentSignal;
        int previousSignal;
        for(int index = 0; index < this.priceData.size(); index++) {



            currentSignal = filterBuySellSignal.get(index);
            if(index -1 < 0) {
                previousSignal = filterBuySellSignal.get(0);
            } else {
                previousSignal = filterBuySellSignal.get(index - 1);
            }
            // 1. reverse to up trend
            if(currentSignal == 1 && previousSignal == 0) {

                this.reverseUp(index);
                logThis("reversing up at index: " + index);

            }
            // 2. reverse to down trend
            if(currentSignal == 0 && previousSignal == 1) {

                this.reverseDown(index);
                logThis("reversing down at index: " + index);

            }
            // 3. up trend continue
            if(currentSignal == 1 && previousSignal == 1) {
                this.upContinue(index);
                logThis("upContinue at index: " + index);
            }
            // 4. down trend continue
            if(currentSignal == 0 && previousSignal == 0) {
                this.downContinue(index);
                logThis("downContinue at index: " + index);

            }

            //
            logThis(" -- position: " + position  + " at index: " + index);
            logThis(" -- lotToAmortize: " + lotToAmortize + " at index: " + index);
            logThis(" -- cumulativeLossBelowCap: " + cumulativeLossBelowCap + " at index: " + index);
            logThis(" -- profit: " + totalProfit + " at index: " + index);
            logThis(" -- profitDealCount: " + profitDealCount);
            logThis("--------------------------------------------");
            logThis("\n");
        }

        System.out.println("Cap Strategy for "+this.filterName+" total profit: " + this.totalProfit);
    }

    private void reverseUp(int index) {
        if(position > 0) {
            System.out.println("error, signal, 1, 0, position: " + position);
            return;
        }
        if(position < 0) {
            // previous negative position not closed, it is losing deal
            // 1. calculate the deal loss
            double currentPrice = priceData.get(index);
            double thisLoss = (soldPrice - currentPrice) * Math.abs(position); // value is negative

            // 2. update total profit
            totalProfit = totalProfit + thisLoss;
            logThis("reverseUp at index" + index);


            // 3. add deal loss to cumulative loss, since we need to use cumulative loss to calculate
            //    next position size, cumulative loss will be reset to 0 in the immediate next profit deal
            cumulativeLossBelowCap = cumulativeLossBelowCap + Math.abs(thisLoss); // in absolute dollar amount

            // 4. calculate next possible position
            double nextPossiblePosition = cumulativeLossBelowCap / profitPoints + initialLotSize;
            double nextActualPosition = 0;
            double lotToAmortizeThisDeal = nextPossiblePosition - capPosition;
            if(lotToAmortizeThisDeal > 0) {
                // next position is over capPosition;

                logThis("alert! - cap reached at index: " + index);
                // Amount above the cap will be amortized by later profiting deal
                // update amortize amount
                //lotToAmortize = lotToAmortize + lotToAmortizeThisDeal;

                lotToAmortize = lotToAmortize + nextPossiblePosition;

                // Since we will amortize the amount over the cap in the future profiting deals,
                // we can subtract the amount update cumulative loss over the cap position.
                //cumulativeLossBelowCap = cumulativeLossBelowCap - (lotToAmortizeThisDeal ) * profitPoints;
                cumulativeLossBelowCap = 0;
                logThis("cumulativeLossBelowCap set to 0 at index " + index);
                // lotToAmortize will only be amortized when opening new position, whereas cumulativeLossBelowCap
                //  is reset to 0 in the immediate next profit deal, meaning
                //nextActualPosition = capPosition;
                nextActualPosition = initialLotSize;
            } else {
                // loss is below or equal the loss cap
                nextActualPosition = nextPossiblePosition;

            }

            // 5. buy
            position = nextActualPosition; // buying



            // 6. update bought price
            boughtPrice = priceData.get(index);
            return;
        }
        if(position == 0) {
            if (lotToAmortize > 0) {

                position = amortiseFactor * initialLotSize; // buying


                // adjust lossAboveCap
                lotToAmortize = lotToAmortize - (amortiseFactor * initialLotSize - initialLotSize );
                cumulativeLossBelowCap = cumulativeLossBelowCap + Math.abs((amortiseFactor - 1)* initialLotSize) * profitPoints;
                // e.g. : 750 - 50
            } else {
                // no cumulative loss
                position = initialLotSize;
                //System.out.println("reverseUp() - position: " + position + " at index: " + index);
            }
            boughtPrice = priceData.get(index);
            return;
        }
    }

    private void reverseDown(int index) {

        if(position > 0) {
            // 1. calculate the deal loss
            double currentPrice = priceData.get(index);
            double thisLoss = ( currentPrice - boughtPrice) * Math.abs(position); // value is negative

            // 2. update total profit
            totalProfit = totalProfit + thisLoss;
            logThis("reverseDown  at index: " + index);


            // 3. add deal loss to cumulative loss, since we need to use cumulative loss to calculate
            //    next position size, cumulative loss will be reset to 0 in the immediate next profit deal
            cumulativeLossBelowCap = cumulativeLossBelowCap + Math.abs(thisLoss); // in absolute dollar amount

            // 4. calculate next possible position
            double nextPossiblePositionAbs = cumulativeLossBelowCap / profitPoints + initialLotSize;
            double nextActualPositionAbs = 0;
            double lotToAmortizeThisDeal = nextPossiblePositionAbs - capPosition;
            if(lotToAmortizeThisDeal > 0) {
                // next position is over capPosition;

                // Amount above the cap will be amortized by later profiting deal
                // update amortize amount
                //lotToAmortize = lotToAmortize + lotToAmortizeThisDeal;
                lotToAmortize = lotToAmortize + nextPossiblePositionAbs;

                // Since we will amortize the amount over the cap in the future profiting deals,
                // we can subtract the amount update cumulative loss over the cap position.
                //cumulativeLossBelowCap = cumulativeLossBelowCap - (lotToAmortizeThisDeal ) * profitPoints;
                cumulativeLossBelowCap = 0;
                logThis("cumulativeLossBelowCap set to 0 at index " + index);
                // lotToAmortize will only be amortized when opening new position, whereas cumulativeLossBelowCap
                //  is reset to 0 in the immediate next profit deal, meaning
                //nextActualPositionAbs = capPosition;
                nextActualPositionAbs = initialLotSize;
            } else {
                // loss is below or equal the loss cap
                nextActualPositionAbs = nextPossiblePositionAbs;

            }

            // 5. sell
            position = -nextActualPositionAbs; // buying
            logThis("reverseDown() at index: " + index);


            // 6. update bought price
            soldPrice = priceData.get(index);

            return;
        }
        if(position < 0) {
            System.out.println("error, signal, 0, 1, position: " + position);
            return;
        }
        if(position == 0) {
            if(lotToAmortize > 0) {

                position = -amortiseFactor * initialLotSize; // selling


                // adjust lossAboveCap
                lotToAmortize = lotToAmortize - (amortiseFactor * initialLotSize - initialLotSize);
                cumulativeLossBelowCap = cumulativeLossBelowCap + Math.abs((amortiseFactor - 1)* initialLotSize) * profitPoints;
            } else {
                // no cumulative loss
                position = -initialLotSize;


            }
            soldPrice = priceData.get(index);
            return;
        }
    }

    private void upContinue(int index) {
        if(position > 0) {
            // set cumulativeLossBelowCap to 0
            // 1. calculate deal profit
            double currentPrice = priceData.get(index);
            if(currentPrice - boughtPrice >= profitPoints) {
                double thisProfit = (currentPrice - boughtPrice) * position;
                totalProfit = totalProfit + thisProfit;

                logThis("upContinue, totalProfit: " + totalProfit + " at index: " + index);
                // 2. close position to take profit
                position = 0;
                logThis("upContinue() - position: " + position + " at index: " + index);
                this.boughtPrice = 0;
                this.soldPrice = 0;

                // 3. reset cumulativeLossBelowCap
                cumulativeLossBelowCap = 0;
                logThis("cumulativeLossBelowCap set to 0 since profiting deal");
                profitDealCount++;

                return;
            }
        }
        if(position < 0) {
            System.out.println("error, signal 1, 1, position: " + position);
            return;
        }
        if(position == 0) {
            // do nothing
            return;
        }
    }

    private void downContinue(int index) {

        // sell trend continue
        if(this.position > 0) {
            System.out.println("error, signal 0, 0, position: " + position);
            return;
        }
        if(this.position < 0) {
            // set cumulativeLossBelowCap to 0
            // 1. calculate deal profit
            double currentPrice = priceData.get(index);
            if(soldPrice - currentPrice >= profitPoints) {
                double thisProfit = (soldPrice - currentPrice ) * Math.abs(position);
                totalProfit = totalProfit + thisProfit;

                logThis("downContinue, totalProfit: " + totalProfit + " at index: " + index);
                // 2. close position to take profit
                position = 0;
                logThis("downContinue() - position: " + position + " at index: " + index);

                this.boughtPrice = 0;
                this.soldPrice = 0;

                // 3. reset cumulativeLossBelowCap
                cumulativeLossBelowCap = 0;
                logThis("cumulativeLossBelowCap set to 0 since profiting deal");
                profitDealCount++;
                logThis("profitDealCount: " + profitDealCount + " at index: " + index);
                return;
            }
        }
        if(this.position == 0) {
            // do nothing
            return;
        }
    }
}
