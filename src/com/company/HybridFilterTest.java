package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by nick on 7/24/15.
 */
public class HybridFilterTest extends AlStepFilterTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCalculate() throws Exception {

        System.out.println("Testing " + this.fileNames.size() + " cases");

        for(int fileIndex = 0; fileIndex < this.fileNames.size(); fileIndex++) {
            String fileName = this.fileNames.get(fileIndex);

            ArrayList<Double> tempInput = new ArrayList<Double>();
            ArrayList<Double> tempOutput = new ArrayList<Double>();
            HybridFilter tempFilter = new HybridFilter();

            this.readDataFileTo(fileName, tempInput);

            tempFilter.filter(tempInput, tempOutput);
            System.out.println("====== " +fileName+ " =======");

            tempFilter.calculate(tempInput);

        }
    }
}