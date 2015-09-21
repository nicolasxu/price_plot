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
        ArrayList<Double> ticks = new ArrayList<Double>();
        ArrayList<TickSignal> signals = new ArrayList<TickSignal>();
        ArrayList<Double> stepTicks = new ArrayList<Double>();

        // 1. read file to ticks
        Util.readCSVFileTo("ticks2015.08.27.csv", ticks); // 26 ~ 28


        // 2. generate signal
        Util.findTickPatternSignal(ticks, 10, signals);
        System.out.println("signal size: " + signals.size());

        // 3. run sims
        Util.runSim(ticks, signals);
        System.out.println("end of test...");

    }

    @Test
    public void testTickToStep() throws Exception {
        System.out.println("testing testTickToStep()");

        ArrayList<Double> stepData = new ArrayList<Double>();
        ArrayList<Double> filterOutput = new ArrayList<Double>();
        String fileName = "tick50diff201411.csv";
        Util.readCSVFileTo(fileName, stepData);
        System.out.println("period: " + fileName);

        KalmanFilter kFilter = new KalmanFilter(5);
        kFilter.filter(stepData, filterOutput);

        Util.runSimForStepTicks(stepData, kFilter.buySellSignal);

    }
}