package com.company;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {



    public static void main(String[] args) {

	// write your code here
        System.out.println("Hello world!");
        SampleForm myForm = new SampleForm();

        // test

        double closePrice[];
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

        // build arrayList

        ArrayList<Double> input = new ArrayList<Double>();

        for(int i = 0; i < 100; i++) {
            input.add(closePrice[i]);
        }

        StepFilter stepFilter = new StepFilter(10);
        ArrayList<Double> output = new ArrayList<Double>();
        stepFilter.filter(input, output);

        for(Double value: output) {
            //System.out.println(value);
        }

        // end of test

    }
}
