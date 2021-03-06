package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nick on 7/21/15.
 */
public class AlStepFilterTest {


    double closePrice[];
    ArrayList<Double> inputSampleData;
    ArrayList<Double> input;
    AlStepFilter alfStepFilter;
    String fileName;
    ArrayList<String> fileNames;
    String filePath;

    protected void readDataFileTo(String fileName, ArrayList<Double> data) {

        FileReader fr;
        BufferedReader br;
        String line = null;

        try {
            fr = new FileReader(this.filePath + fileName);
            br = new BufferedReader(fr);
            while((line = br.readLine()) != null) {


                String[] columns = line.split(",");
                double bid, ask, mid;

                if(fileName.contains("IB")) {
                    bid = Double.parseDouble(columns[1]);
                    ask = Double.parseDouble(columns[2]);
                    mid = (bid + ask) / 2;
                } else {
                    bid = Double.parseDouble(columns[0]);
                    ask = Double.parseDouble(columns[1]);
                    mid = (bid + ask) / 2;
                }


                if(data == null) {
                    data = new ArrayList<Double>();
                }
                data.add(mid);

                //System.out.println("bid:" + bid + " ask: " + ask);

            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        this.input = new ArrayList<Double>();
        this.filePath = "/Users/nick/IdeaProjects/price_plot/";
        this.fileNames = new ArrayList<String>();

        // 2010 data
        fileNames.add("tick50diff201001.csv");
        fileNames.add("tick50diff201002.csv");
        fileNames.add("tick50diff201003.csv");
        fileNames.add("tick50diff201004.csv");
        fileNames.add("tick50diff201005.csv");
        fileNames.add("tick50diff201006.csv");
        fileNames.add("tick50diff201007.csv");
        fileNames.add("tick50diff201008.csv");
        fileNames.add("tick50diff201009.csv");
        fileNames.add("tick50diff201010.csv");
        fileNames.add("tick50diff201011.csv");
        fileNames.add("tick50diff201012.csv");

        // 2011 data
        fileNames.add("tick50diff201101.csv");
        fileNames.add("tick50diff201102.csv");
        fileNames.add("tick50diff201103.csv");
        fileNames.add("tick50diff201104.csv");
        fileNames.add("tick50diff201105.csv");
        fileNames.add("tick50diff201106.csv");
        fileNames.add("tick50diff201107.csv");
        fileNames.add("tick50diff201108.csv");
        fileNames.add("tick50diff201109.csv");
        fileNames.add("tick50diff201110.csv");
        fileNames.add("tick50diff201111.csv");
        fileNames.add("tick50diff201112.csv");

        // 2012 data
        fileNames.add("tick50diff201201.csv");
        fileNames.add("tick50diff201202.csv");
        fileNames.add("tick50diff201203.csv");
        fileNames.add("tick50diff201204.csv");
        fileNames.add("tick50diff201205.csv");
        fileNames.add("tick50diff201206.csv");
        fileNames.add("tick50diff201207.csv");
        fileNames.add("tick50diff201208.csv");
        fileNames.add("tick50diff201209.csv");
        fileNames.add("tick50diff201210.csv");
        fileNames.add("tick50diff201211.csv");
        fileNames.add("tick50diff201212.csv");

        // 2013 data
        fileNames.add("tick50diff201301.csv");
        fileNames.add("tick50diff201302.csv");
        fileNames.add("tick50diff201303.csv");
        fileNames.add("tick50diff201304.csv");
        fileNames.add("tick50diff201305.csv");
        fileNames.add("tick50diff201306.csv");
        fileNames.add("tick50diff201307.csv");
        fileNames.add("tick50diff201308.csv");
        fileNames.add("tick50diff201309.csv");
        fileNames.add("tick50diff201310.csv");
        fileNames.add("tick50diff201311.csv");
        fileNames.add("tick50diff201312.csv");

        fileNames.add("tick50diff201401.csv");
        fileNames.add("tick50diff201402.csv");
        fileNames.add("tick50diff201403.csv");
        fileNames.add("tick50diff201404.csv");
        fileNames.add("tick50diff201405.csv");
        fileNames.add("tick50diff201406.csv");
        fileNames.add("tick50diff201407.csv");
        fileNames.add("tick50diff201408.csv");
        fileNames.add("tick50diff201409.csv");
        fileNames.add("tick50diff201410.csv");
        fileNames.add("tick50diff201411.csv");
        fileNames.add("tick50diff201412.csv");
        // 2015 data
        fileNames.add("tick50diff201501.csv");
        fileNames.add("tick50diff201502.csv");
        fileNames.add("tick50diff201503.csv");
        fileNames.add("tick50diff201504.csv");
        fileNames.add("tick50diff201505.csv");
        fileNames.add("tick50diff201506.csv");



        closePrice = new double[100];
        closePrice[0]=1.1815;
        closePrice[1]=1.1758;
        closePrice[2]=1.1623;
        closePrice[3]=1.1712;
        closePrice[4]=1.1585;
        closePrice[5]=1.1503;
        closePrice[6]=1.1555;
        closePrice[7]=1.1681;
        closePrice[8]=1.1681;
        closePrice[9]=1.1563;
        closePrice[10]=1.1605;
        closePrice[11]=1.1603;
        closePrice[12]=1.1566;
        closePrice[13]=1.1601;
        closePrice[14]=1.1587;
        closePrice[15]=1.1552;
        closePrice[16]=1.1562;
        closePrice[17]=1.144;
        closePrice[18]=1.1407;
        closePrice[19]=1.1367;
        closePrice[20]=1.1323;
        closePrice[21]=1.1352;
        closePrice[22]=1.1306;
        closePrice[23]=1.1344;
        closePrice[24]=1.126;
        closePrice[25]=1.1315;
        closePrice[26]=1.1315;
        closePrice[27]=1.1326;
        closePrice[28]=1.1218;
        closePrice[29]=1.1306;
        closePrice[30]=1.1224;
        closePrice[31]=1.1211;
        closePrice[32]=1.1247;
        closePrice[33]=1.1198;
        closePrice[34]=1.1058;
        closePrice[35]=1.1023;
        closePrice[36]=1.1009;
        closePrice[37]=1.1005;
        closePrice[38]=1.1033;
        closePrice[39]=1.1025;
        closePrice[40]=1.0898;
        closePrice[41]=1.0942;
        closePrice[42]=1.0872;
        closePrice[43]=1.0805;
        closePrice[44]=1.0818;
        closePrice[45]=1.0887;
        closePrice[46]=1.089;
        closePrice[47]=1.0943;
        closePrice[48]=1.1037;
        closePrice[49]=1.0905;
        closePrice[50]=1.0923;
        closePrice[51]=1.1012;
        closePrice[52]=1.1007;
        closePrice[53]=1.0973;
        closePrice[54]=1.0889;
        closePrice[55]=1.0916;
        closePrice[56]=1.0889;
        closePrice[57]=1.0882;
        closePrice[58]=1.0834;
        closePrice[59]=1.0765;
        closePrice[60]=1.0731;
        closePrice[61]=1.072;
        closePrice[62]=1.0769;
        closePrice[63]=1.0795;
        closePrice[64]=1.0794;
        closePrice[65]=1.071;
        closePrice[66]=1.0842;
        closePrice[67]=1.0768;
        closePrice[68]=1.075;
        closePrice[69]=1.0794;
        closePrice[70]=1.081;
        closePrice[71]=1.0782;
        closePrice[72]=1.0814;
        closePrice[73]=1.0695;
        closePrice[74]=1.0702;
        closePrice[75]=1.0664;
        closePrice[76]=1.062;
        closePrice[77]=1.0586;
        closePrice[78]=1.0646;
        closePrice[79]=1.0608;
        closePrice[80]=1.0587;
        closePrice[81]=1.0659;
        closePrice[82]=1.0627;
        closePrice[83]=1.0614;
        closePrice[84]=1.0575;
        closePrice[85]=1.0565;
        closePrice[86]=1.063;
        closePrice[87]=1.0755;
        closePrice[88]=1.0796;
        closePrice[89]=1.0758;
        closePrice[90]=1.0783;
        closePrice[91]=1.0715;
        closePrice[92]=1.0649;
        closePrice[93]=1.0657;
        closePrice[94]=1.0669;
        closePrice[95]=1.068;
        closePrice[96]=1.0667;
        closePrice[97]=1.0619;
        closePrice[98]=1.0591;
        closePrice[99]=1.0602;

        this.inputSampleData = new ArrayList<Double>();

        for(int i = 0; i < 100; i++) {
            this.inputSampleData.add(closePrice[i]);
        }

        this.alfStepFilter = new AlStepFilter(10);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCalculateWinLoss() throws Exception {

    }

    @Test
    public void testFilter() throws Exception {


        for(int fileIndex = 0; fileIndex < this.fileNames.size(); fileIndex++) {
            String fileName = this.fileNames.get(fileIndex);

            ArrayList<Double> tempInput = new ArrayList<Double>();
            ArrayList<Double> tempOutput = new ArrayList<Double>();
            AlStepFilter tempFilter = new AlStepFilter(25);

            this.readDataFileTo(fileName, tempInput);

            tempFilter.filter(tempInput, tempOutput);
            System.out.println("====== " +fileName+ " =======");
            tempFilter.calculateWinLoss(tempInput, tempOutput, tempFilter.buySellSignal);

        }



    }
}