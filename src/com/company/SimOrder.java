package com.company;

/**
 * Created by nick on 8/31/15.
 */
public class SimOrder {
    public int buySellFlag; // 1 buy order, -1 sell order, 0 empty order
    public double tp; // 0 is no tp
    public double sl; // 0 is no sl
    public double openPrice;
    public double closePrice;
    public int volume;

    public double pl; // profit and loss, calculated value

    public SimOrder() {
        this.buySellFlag = 0;
        this.tp = 0;
        this.sl = 0;
        this.openPrice  = 0;
        this.closePrice = 0;
        this.volume     = 0;
        this.pl         = 0;

    }

    public void open(int flag, double tp, double sl, double openPrice, int volume) {
        this.buySellFlag = flag;
        this.tp = tp;
        this.sl = sl;
        this.openPrice = openPrice;
        this.volume = volume;
    }

    private void calculateProfit() {

        if (this.closePrice == 0) {
            // if not closed, do nothing
            return;
        }

        if(this.buySellFlag == 0 || this.volume == 0) {
            // if order is empty, do nothing
            return;
        }

        if(this.buySellFlag == 1) {
            // calculate buy order p&l
            this.pl = (this.closePrice - this.openPrice) * this.volume;
        }

        if(this.buySellFlag == -1) {
            // calculate sell order p&l
            this.pl = (this.openPrice - this.closePrice) * this.volume;

        }

        if(this.pl >0) {
            //System.out.println("win");
        } else {
           // System.out.println("loss");
        }
    }

    /* Check current price against stop loss or take profit
       If condition meets, then execute sl or tp.
     */
    public double trySLandTP(double currentPrice) {

        if(this.closePrice !=0) {
            // if close price is set, this order is closed before, so do nothing.
            return 0;
        }

        // buy order
        if (this.buySellFlag == 1) {
            if (currentPrice <= this.sl && this.sl != 0) {
                // trigger stop loss
                this.closePrice = currentPrice;
                this.calculateProfit();
            }
            if(currentPrice >= this.tp && this.tp !=0) {
                // trigger take profit
                this.closePrice = currentPrice;
                this.calculateProfit();
            }
        }

        // sell order
        if(this.buySellFlag == -1) {
            if(currentPrice >= this.sl && this.sl !=0) {
                // trigger stop loss
                this.closePrice = currentPrice;
                this.calculateProfit();
            }

            if(currentPrice <= this.tp && this.tp !=0) {
                // trigger take profit
                this.closePrice = currentPrice;
                this.calculateProfit();
            }
        }

        return this.pl;
    }

    /*
        Check if this order is empty
     */
    public boolean isEmptyOrder() {
        if(this.buySellFlag == 0 || this.openPrice == 0 || this.volume == 0 ) {
            return true;
        }

        return false;
    }
    /*
        Check if this order is closed
     */
    public boolean isClosed() {
        if(this.closePrice == 0) {
            return false;
        } else {
            return true;
        }
    }

    /*
    *  Close order at current price and return the profit or loss
    * */
    public double close(double currentPrice) {

        if(this.isClosed() || this.isEmptyOrder()) {
            return 0;
        }

        if(this.buySellFlag == 1) {
            this.closePrice = currentPrice;
            this.calculateProfit();
        }

        if(this.buySellFlag == -1) {
            this.closePrice = currentPrice;
            this.calculateProfit();
        }


        return this.pl;
    }
}
