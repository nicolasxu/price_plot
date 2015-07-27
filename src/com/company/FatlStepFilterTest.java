package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by nick on 7/24/15.
 */
public class FatlStepFilterTest extends AlStepFilterTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCalculateWinLoss() throws Exception {

        for(int fileIndex = 0; fileIndex < this.fileNames.size(); fileIndex++) {
            String fileName = this.fileNames.get(fileIndex);

            ArrayList<Double> tempInput = new ArrayList<Double>();
            ArrayList<Double> tempOutput = new ArrayList<Double>();
            FatlStepFilter tempFilter = new FatlStepFilter(25);

            this.readDataFileTo(fileName, tempInput);

            tempFilter.filter(tempInput, tempOutput);
            System.out.println("====== " +fileName+ " =======");
            tempFilter.calculateWinLoss(tempInput, tempOutput, tempFilter.buySellSignal);

        }


    }
}