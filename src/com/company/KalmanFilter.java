package com.company;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by nick on 7/8/15.
 */
public class KalmanFilter extends IFilter {

    enum SignalMode {
        TREND,
        KALMAN
    };

    int smoothRatio;
    double sqrtK100;
    double k100;
    int min_rates_total;

    double velocity;
    double previousVelocity;
    double distance;
    double error;

    public ArrayList<Integer> buySellSignal;

    SignalMode signal;


    public KalmanFilter(int ratio) {
        this.smoothRatio = ratio;
        this.signal = SignalMode.KALMAN;
        this.min_rates_total = 2;
        this.sqrtK100 = Math.sqrt(this.smoothRatio/100.0);
        this.k100 = this.smoothRatio/100.0;
        this.buySellSignal = new ArrayList<Integer>();



    }
    public void setSignalModeKalman() {
        this.signal = SignalMode.KALMAN;
    }
    public void setSignalModeTrend() {
        this.signal = SignalMode.TREND;
    }


    public void filter(ArrayList<Double> input, ArrayList<Double> output) {
        System.out.println("filter() triggered");
        if(input.size() < this.min_rates_total) {
            return;
        }

        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.HALF_UP);

        for(int inputIndex = Math.max(0, output.size() -1 ); inputIndex < input.size(); inputIndex++) {

            if(inputIndex < 1) {
                Double inputNumber = input.get(inputIndex);
                output.add(inputNumber);
                buySellSignal.add(-1);
                this.previousVelocity = 0.0;
                this.velocity = this.previousVelocity;

            } else {
                this.velocity = this.previousVelocity;

                // store the velocity before running at current bar
                if(inputIndex == input.size() - 1) {

                    this.velocity = this.previousVelocity;
                }
                this.distance = input.get(inputIndex) - output.get(inputIndex - 1);
                this.distance = Double.parseDouble(df.format(this.distance));

                this.error    = output.get(inputIndex-1) + this.distance * this.sqrtK100;

                this.velocity = this.velocity + this.distance*this.k100;

                double currentBarValue = this.error + this.velocity;

                // Part 1, writing output data
                if(inputIndex >= output.size()) {
                    output.add(currentBarValue);
                } else {
                    output.set(inputIndex, currentBarValue);
                }

                // Part 2, reset, if not pass filter point
                double filterPoint = 22 * 0.00001;

                if(Math.abs(output.get(inputIndex) -  output.get(inputIndex -1)) < filterPoint) {
                    output.set(inputIndex, output.get(inputIndex - 1));
                    // no update of the velocity
                    //this.velocity = this.previousVelocity;

                } else {
                    // part 3, update velocity if pass the filterPoint threshold
                    this.previousVelocity = this.velocity;
                }



                // calculating trend
                if(this.signal == SignalMode.KALMAN) {
                    // Kalman signal
                    buySellSignal.add(0);
                    if(this.velocity > 1.5 * 0.00001) {

                        if(inputIndex >= buySellSignal.size()) {
                            // buy
                            buySellSignal.add(1);
                        } else {

                            buySellSignal.set(inputIndex, 1);
                        }

                    }
                    if(this.velocity < -1.5 * 0.00001) {

                        if(inputIndex >= buySellSignal.size()) {
                            // sell
                            buySellSignal.add(-1);
                        } else {
                            buySellSignal.set(inputIndex, -1);
                        }
                    }

                    if(inputIndex < 1240 && inputIndex > 1230) {
                        //System.out.println("velocity: " + velocity);
                        //System.out.println("inputIndex: " + inputIndex + " buySellSignal: " + buySellSignal.get(inputIndex));
                    }

                } else {
                    // Trend signal
                    if(output.get(inputIndex -1 ) > output.get(inputIndex)) {
                        if(inputIndex >= buySellSignal.size()) {
                            buySellSignal.add(1);
                        } else {
                            buySellSignal.set(inputIndex, 1);
                        }

                    } else {
                        if(inputIndex >= buySellSignal.size()) {
                            buySellSignal.add(-1);
                        } else {
                            buySellSignal.set(inputIndex, -1);
                        }
                    }
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
        double profitSize   = 100 * 0.00001;  // 00 points
        double stopLossSize = 100 * 0.00001; // 100 points

        ArrayList<Integer> winLossList = new ArrayList<Integer>(); // 0 is loss, 1 is win
        ArrayList<Integer> winLossInputIndex = new ArrayList<Integer>();


        for(int i = 0; i < total; i++) {

            currentSignal = signal.get(i);
            if(i -1 < 0) {
                previousSignal = signal.get(0);
            } else {
                previousSignal = signal.get(i - 1);
            }

            // trend reverse
            if(currentSignal == 1 && previousSignal == -1) {
                if(sold) {
                    sold = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                    System.out.println("loss: " + (soldPrice - input.get(i)));

                }
                bought = true;
                boughtPrice = input.get(i);

                System.out.println(i + ": buy");
            }

            if(currentSignal == -1 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                    System.out.println("loss: " + (input.get(i) - boughtPrice));
                }
                sold = true;
                soldPrice = input.get(i);
                System.out.println(i + ": sell");

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

            if(currentSignal == -1 && previousSignal == -1) {
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
        System.out.println("P&L with Kalman("+ this.smoothRatio  +")");
        System.out.println("Kalman Win Count: " + winCount);
        System.out.println("Kalman Lose Count: " + lossCount);
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
        }
        System.out.println("tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("Max loss in a row: " + maxLossRow + " ends at: " + winLossInputIndex.get(maxLossRowPoint) );


    }
}

