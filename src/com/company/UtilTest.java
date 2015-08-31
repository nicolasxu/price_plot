package com.company;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
/**
 * Created by nick on 7/23/15.
 */
public class UtilTest {

    @Before
    public void setUp() throws Exception {



    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMaxPrevLoseCount() throws Exception {
        ArrayList<Integer> winLossRecords = new ArrayList<Integer>();
        winLossRecords.add(0);
        winLossRecords.add(0);
        winLossRecords.add(0);
        winLossRecords.add(0);

        int result = Util.maxPrevLoseCount(winLossRecords);
        Assert.assertEquals(result, 4);


        winLossRecords.add(1);
        winLossRecords.add(0);
        winLossRecords.add(0);

        result = Util.maxPrevLoseCount(winLossRecords);
        Assert.assertEquals(2, result);

        winLossRecords.add(1);
        result = Util.maxPrevLoseCount(winLossRecords);

        Assert.assertEquals(0, result);
    }


    @Test
    public void testFindTickPattern() throws Exception {
        System.out.println("testing FindTickPattern...");
        ArrayList<Double> data = new ArrayList<Double>();

        Util.readCSVFileTo("ticks2015.08.26.csv", data);

    }
}