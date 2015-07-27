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

        NoLagMaFilter noLagFilter = new NoLagMaFilter(10);
        ArrayList<Double> noLagOutput = new ArrayList<Double>();
        noLagFilter.filter(inputSampleData, noLagOutput);

        for(Double value: noLagOutput) {
            System.out.println(value);
        }

    }
}