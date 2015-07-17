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

        for(int i = Math.max(0, output.size() -1 ); i < input.size(); i++) {

            if(i < 1) {
                Double inputNumber = input.get(i);
                output.add(inputNumber);
                buySellSignal.add(-1);
                this.previousVelocity = 0.0;
                this.velocity = this.previousVelocity;

            } else {
                this.velocity = this.previousVelocity;

                // store the velocity before running at current bar
                if(i == input.size() - 1) {

                    this.velocity = this.previousVelocity;
                }
                this.distance = input.get(i) - output.get(i - 1);
                this.distance = Double.parseDouble(df.format(this.distance));

                this.error    = output.get(i-1) + this.distance * this.sqrtK100;

                this.velocity = this.velocity + this.distance*this.k100;
                this.previousVelocity = this.velocity;

                double currentBarValue = this.error + this.velocity;
                //currentBarValue = Double.parseDouble(df.format(currentBarValue));


                if(i >= output.size()) {
                    output.add(currentBarValue);
                } else {
                    output.set(i, currentBarValue);
                }

                // calculating trend
                if(this.signal == SignalMode.KALMAN) {
                    // Kalman signal
                    if(this.velocity > 0) {

                        if(i >= buySellSignal.size()) {
                            // buy
                            buySellSignal.add(1);
                        } else {
                            buySellSignal.set(i, 1);
                        }

                    } else {


                        if(i >= buySellSignal.size()) {
                            // sell
                            buySellSignal.add(0);
                        } else {
                            buySellSignal.set(i, 0);
                        }
                    }
                    //System.out.println("kalman signal["+i+"] = " + this.buySellSignal.get(i));
                } else {
                    // Trend signal
                    if(output.get(i -1 ) > output.get(i)) {
                        if(i >= buySellSignal.size()) {
                            buySellSignal.add(1);
                        } else {
                            buySellSignal.set(i, 1);
                        }

                    } else {
                        if(i >= buySellSignal.size()) {
                            buySellSignal.add(0);
                        } else {
                            buySellSignal.set(i, 0);
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
        double profitSize = 50 * 0.00001; // 50 points

        ArrayList<Integer> winLossList = new ArrayList<Integer>();


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
                }
                bought = true;
                boughtPrice = input.get(i);
            }

            if(currentSignal == 0 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
                    winLossList.add(0);
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
        System.out.println("Max loss in a row: " + maxLossRow + " ends at: " + maxLossRowPoint);

    }
}

