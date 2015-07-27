package com.company;

import java.util.ArrayList;

/**
 * Created by nick on 7/25/15.
 */
public class NoLagMaFilter extends IFilter {

    int length;
    ArrayList<Double> alphas;
    double pointFilter;
    double pctFilter;

    // below is not input
    int Len;
    double weight;
    double point;
    ArrayList<Integer> buySellSignal;

    public NoLagMaFilter(int length, int point) {

        this.length = length;
        this.pointFilter = point;
        this.alphas = new ArrayList<Double>();

        this.pctFilter = 0;
        this.Len = 0;
        this.weight = 0;
        this.point = 0;
        this.buySellSignal = new ArrayList<Integer>();

        this.init();
    }

    private void init() {

        this.pctFilter = 0; // 10% in decimal

        double t, g, cycle = 4;
        double coeff = 3 * Math.PI;
        double phase = this.length -1;

        this.Len = (int)(this.length * 4 + phase);
        this.weight = 0;


        for(int i = 0; i < this.Len; i++) {

            // 1. calculate t
            if ( i <= phase -1) {
                t = 1.0 * i/(phase -1);
            } else {
                t = 1.0 + (i - phase + 1)*(2 * cycle - 1.0) / (cycle * this.length - 1.0);
            }

            // 2. calculate g
            if(t <= 0.5) {
                g = 1.0;
            } else {
                g = 1 / (coeff * t + 1);
            }

            // 3. calculate alpha
            this.alphas.add(g * Math.cos(Math.PI * t));

            // 4. calculate weight
            this.weight = weight + this.alphas.get(i);

        }

        int _digit = 5;
        double _point = 0.00001;

        this.point = _point * Math.pow(10, _digit % 2);

    }


    // can only calculate once
    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        double calculatedFilterPoint = 0;
        double sumDel = 0;
        double avgDel = 0;
        double sumPow = 0;
        double stdDev = 0;
        for(int index = 0; index < input.size(); index++) {
            if(index < this.length) {

                output.add(input.get(index));

                this.buySellSignal.add(-1);

            } else {
                //index >= this.Len

                // 1. calculate sum
                double sum = input.get(index) * this.alphas.get(0);
                for(int i = 1; i < this.Len; i++) {
                    sum = sum + input.get(Math.max(0, index - i)) * this.alphas.get(i);
                }

                // 2. calculate output
                if(this.weight > 0) {
                    output.add(sum / this.weight);
                }

                // 3. calculate filter point
                if(this.pctFilter > 0) {

                    if(index >= this.Len + this.length) {
                        // calculate filter points

                        if(output.get(index -1) > 0) {
                            sumDel = Math.abs(output.get(index) -  output.get(index -1));
                            for(int i = 1; i < this.length; i++) {
                                sumDel = sumDel + Math.abs(output.get(index - i) - output.get(index-i-1));
                            }
                            avgDel = sumDel / this.length;

                            sumPow = Math.pow(Math.abs(output.get(index) - output.get(index -1) - avgDel) , 2);
                            for (int i = 1; i < this.length; i++) {
                                sumPow = sumPow + Math.pow(Math.abs(output.get(index-i) - output.get(index -i-1) - avgDel), 2);
                            }
                            stdDev = Math.sqrt(sumPow / this.length);

                            calculatedFilterPoint = this.pctFilter * stdDev;
                            //System.out.println("calculatedFilterPoint: " + calculatedFilterPoint);
                        } else {
                            calculatedFilterPoint = 0;
                        }

                    } else {
                        calculatedFilterPoint = 0;
                    }

                } else if (this.pointFilter >0){
                    calculatedFilterPoint = this.pointFilter * 0.00001;

                } else {
                    calculatedFilterPoint = 0;
                } // end of 3. calculate filter point

                // 4. alter output based on filter point
                if(Math.abs(output.get(index) -  output.get(index -1)) < calculatedFilterPoint) {
                    output.set(index, output.get(index -1));
                }

                // 5. calculate buySellSignal
                this.buySellSignal.add(this.buySellSignal.get(index -1));

                if(output.get(index) > output.get(index -1)) {
                    this.buySellSignal.set(index, 1); // buy signal
                }
                if(output.get(index) < output.get(index -1)) {
                    this.buySellSignal.set(index, 0);  // sell signal
                }
            }
        }
    }
}
