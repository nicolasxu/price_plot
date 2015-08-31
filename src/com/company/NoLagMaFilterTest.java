package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by nick on 7/27/15.
 */
public class NoLagMaFilterTest extends AlStepFilterTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFilter() throws Exception {

        for(int fileIndex = 0; fileIndex < this.fileNames.size(); fileIndex++) {
            String fileName = this.fileNames.get(fileIndex);
            // pctFilter 15, length 30 yields good result
            ArrayList<Double> tempInput = new ArrayList<Double>();
            ArrayList<Double> tempOutput = new ArrayList<Double>();
            //NoLagMaFilter tempFilter = new NoLagMaFilter(30, 20);
            //NoLagMaFilter tempFilter = new NoLagMaFilter(50, 40);
            NoLagMaFilter tempFilter = new NoLagMaFilter(60, 40);

            this.readDataFileTo(fileName, tempInput);

            tempFilter.filter(tempInput, tempOutput);
            System.out.println("====== " + fileName + " =======");
            //Util.calculateWinLoss(tempInput, tempOutput, tempFilter.buySellSignal, "No Lag MA Filter");
            Util.calculateCapStrategy(tempInput, tempOutput, tempFilter.buySellSignal, "No Lag MA Filter");
            //Util.calculateBuyHold(tempInput, tempOutput, tempFilter.buySellSignal, "No Lag MA Filter");

        }

    }
}