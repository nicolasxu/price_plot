package com.company;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nick on 7/13/15.
 */
public class AdaptiveLaguerreFilter extends IFilter{

    int laguerreLength;        // laguerreLength
    int laguerreOrder;         // Laguerre Filter Order
    int smoothLength; //Adaptive Factor Smoothing Period

    ArrayList<Integer> buySellSignal;
    ArrayList<Double> diff;
    private ArrayList<Double> gamma;
    private ArrayList<Double> nodeValues; // number of nodes == laguerreOrder

    // test
    boolean firstGammaPrinted = false;
    // end of test

    public AdaptiveLaguerreFilter(int laguerreLength, int laguerreOrder, int smoothLength) {
        this.laguerreLength = laguerreLength;
        this.laguerreOrder = laguerreOrder;
        this.smoothLength = smoothLength;
        this.buySellSignal = new ArrayList<Integer>();
        this.diff = new ArrayList<Double>();
        this.gamma = new ArrayList<Double>();
        this.nodeValues = new ArrayList<Double>();
    }

    public AdaptiveLaguerreFilter() {
        this.laguerreLength = 5;
        this.laguerreOrder = 2;
        this.smoothLength = 10;
        this.buySellSignal = new ArrayList<Integer>();
        this.diff = new ArrayList<Double>();
        this.gamma = new ArrayList<Double>();
        this.nodeValues = new ArrayList<Double>();
        // test
        this.nodeValues.add(1.0);
        this.nodeValues.add(1.0);
        this.nodeValues.add(1.0);
        this.nodeValues.add(1.0);



    }
    private double adaptiveGamma(int index) {
        // index is the current data index
        double eff, sum = 0, max = 0, min = 1000000000;

        // find the max and min value in the past laguerreLength value
        for(int i = 0; i < this.laguerreLength; i++) {

            double diffValue = this.diff.get(index - i);

            if(diffValue > max) {
                max = diffValue;
            }

            if(diffValue < min) {
                min = diffValue;
            }
        }

        // calculate the eff
        if(max - min != 0) {
            eff = (this.diff.get(index) - min) / (max - min);
        } else {
            eff = 0;
        }

        return eff;
    }

    private void setValueAt(ArrayList<Integer> al, int index /* start from 0 */, int value) {

        if(index < 0) {
            return;
        }

        if(index < al.size()) {
            al.set(index, value);
            return;
        }

        // index 5, size = 5
        // index 7, size = 5
        while(index > al.size()) {
            // fill empty index with 0.0
            al.add(0);
        }

        al.add(value);

    }

    private void setValueAt(ArrayList<Double> al, int index /* start from 0 */, double value) {

        if(index < 0) {
            return;
        }

        if(index < al.size()) {
            al.set(index, value);
            return;
        }

        // index 5, size = 5
        // index 7, size = 5
        while(index > al.size()) {
            // fill empty index with 0.0
            al.add(0.0);
        }

        al.add(value);

    }

    private double median(int index) {
        double[] arrayToSort = new double[this.smoothLength];
        double resultMedian;
        for(int i = 0; i < this.smoothLength; i++) {
            arrayToSort[i] = this.gamma.get(index - i);
        }

        Arrays.sort(arrayToSort);
        int num = (int)Math.round((this.smoothLength -1) / 2);

        if(Math.floorMod(this.smoothLength, 2) > 0) {
            resultMedian = arrayToSort[num];
        } else {
            resultMedian = (arrayToSort[num] + arrayToSort[num + 1]) / 2;
        }

        // TODO


        return resultMedian;
    }

    private double SMA(ArrayList<Double> inputArray, int length, int index){
        double sum = 0;
        for(int i = 0; i < length; i++) {
            sum = sum + inputArray.get(index -i);
        }

        return sum/length;
    }

    private double triMaGen(ArrayList<Double> arrayInput, int length, int index) {

        int len1 = (int)Math.floor((length +1) * 0.5);
        int len2 = (int)Math.ceil((length + 1) * 0.5);

        double sum = 0;

        for(int i = 0; i < len2; i++) {
            sum = sum + SMA(arrayInput, len1, index -i);
        }

        double triMaGen = sum / len2;

        return triMaGen;
    }

    private double laguerre(double currentInputValue, double sgamma, int index) {

        double gam = 1 - sgamma;


        for(int i = 0; i < this.laguerreOrder; i++) {
            if(index <=this.laguerreOrder) {
                this.nodeValues.add(currentInputValue);
                // make 0, 1, 2, 3 of currentInputValue if order is 4
                System.out.println("current value added!");
            } else {

                if(i==0) {
                    double midValue = (1 - gam) * currentInputValue + gam * this.nodeValues.get(i);
                    this.nodeValues.set(i, midValue);
                } else {
                    double midValue = -gam*this.nodeValues.get(i - 1) + this.nodeValues.get(i - 1) + gam*this.nodeValues.get(i);
                    this.nodeValues.set(i, midValue);
                }

            }
        }

        return this.triMaGen(this.nodeValues, this.laguerreOrder, this.laguerreOrder - 1);
    }

    public void filter(ArrayList<Double> input, ArrayList<Double> output) {

        if(output == null) {
            output = new ArrayList<Double>();
        }

        for(int i = Math.max(0, output.size() - 1); i < input.size(); i++) {

            if(i >= this.laguerreLength) {

                // set buySellSignal invalid value
                this.setValueAt(this.buySellSignal, i, -1);

                // set output initial value
                this.setValueAt(output, i, input.get(i));

                // set diff invalid value
                double currentDiff = Math.abs(input.get(i) - output.get(i -1));
                this.setValueAt(this.diff, i, currentDiff);

                // set gamma invalid value
                this.setValueAt(this.gamma, i, 0);

                if(i >= 2 * this.laguerreLength) {

                    // calculate & set gamma
                    double currentAdaptiveGamma = this.adaptiveGamma(i);

                    this.setValueAt(this.gamma, i, currentAdaptiveGamma);

                    if(firstGammaPrinted == false) {
                        System.out.println("first gamma, gamma["+i+"]=" + this.gamma.get(i));
                        this.firstGammaPrinted = true;
                    }

                    // calculate sgamma
                    double sgamma = median(i);
                    this.setValueAt(output, i, laguerre(input.get(i), sgamma, i));

                    // calculate buySellSignal

                }
            }
        }
    }
}


