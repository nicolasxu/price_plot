package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by nick on 7/22/15.
 */
public class NoFilterTest extends AlStepFilterTest {


    @Before
    public void setUp() throws Exception {

        System.out.println("runing setup...");
        super.setUp();

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCalculateWinLoss() throws Exception {

    }

    @Test
    public void testFilter() throws Exception {

        System.out.println(this.fileNames.size());

        for(int fileIndex = 0; fileIndex < this.fileNames.size(); fileIndex++) {
            String fileName = this.fileNames.get(fileIndex);

            ArrayList<Double> tempInput = new ArrayList<Double>();
            ArrayList<Double> tempOutput = new ArrayList<Double>();
            NoFilter tempFilter = new NoFilter();

            this.readDataFileTo(fileName, tempInput);

            tempFilter.filter(tempInput, tempOutput);
            System.out.println("====== " +fileName+ " =======");
            //tempFilter.calculateWinLoss(tempInput, tempOutput, tempFilter.buySellSignal);
            tempFilter.calculate3(tempInput, tempOutput, tempFilter.buySellSignal);



        }

    }
}