package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nick on 7/17/15.
 */
public class MA_fn extends IFilter {

    int filterMode;
    ArrayList<Integer> buySellSignal;

    public MA_fn(int filterMode) {
        this.filterMode = filterMode; // valid value: 4,10
        this.buySellSignal = new ArrayList<Integer>();
        this.buySellSignal.add(-1);

    }
    public void filter(ArrayList<Double> input, ArrayList<Double> output) {
        if(output == null) {
            output = new ArrayList<Double>();
        }

        for(int index = Math.max(0, output.size() -1); index < input.size(); index++) {
            double currentResult = this.FN(index, input);
            output.add(currentResult);


            // calculate buySellSignal
            if(index > 0) {
                // 2nd and on...
                buySellSignal.add(index, buySellSignal.get(index -1));
                if (output.get(index) > output.get(index -1)) {
                    this.buySellSignal.set(index, 1);
                }
                if (output.get(index) <  output.get(index -1)) {
                    this.buySellSignal.set(index, 0);
                }
            }

        }
    }

    private double FN(int index, ArrayList<Double> input) {
        switch (this.filterMode) {
            case 4:
                return
                        0.755921135321*input.get(index)
                                +0.372695463315*input.get(Math.max(index -1, 0))
                                -0.1227477536509*input.get(Math.max(index-2, 0))
                                -0.0682196256116*input.get(Math.max(index-3, 0))
                                +0.0930420041115*input.get(Math.max(index-4, 0))
                                -0.00899996832959*input.get(Math.max(index-5, 0))
                                -0.0542664725221*input.get(Math.max(index-6, 0))
                                +0.0360714890012*input.get(Math.max(index-7, 0))
                                +0.01776244392449*input.get(Math.max(index-8, 0))
                                -0.0360800341007*input.get(Math.max(index-9, 0))
                                +0.00769970759653*input.get(Math.max(index-10, 0))
                                +0.02233317596740*input.get(Math.max(index-11, 0))
                                -0.01874632243583*input.get(Math.max(index-12, 0))
                                -0.00585956702243*input.get(Math.max(index-13, 0))
                                +0.01757001382762*input.get(Math.max(index-14, 0))
                                -0.00589271510556*input.get(Math.max(index-15, 0))
                                -0.00977759936478*input.get(Math.max(index-16, 0))
                                +0.01019259783060*input.get(Math.max(index-17, 0))
                                +0.001309849787704*input.get(Math.max(index-18, 0))
                                -0.00837412415197*input.get(Math.max(index-19, 0))
                                +0.00398336707035*input.get(Math.max(index-20, 0))
                                +0.00380943659834*input.get(Math.max(index-21, 0))
                                -0.00517254056171*input.get(Math.max(index-22, 0))
                                +0.0003078573492474*input.get(Math.max(index-23, 0))
                                +0.00353726807775*input.get(Math.max(index-24, 0))
                                -0.002326411760936*input.get(Math.max(index-25, 0))
                                -0.001106818856381*input.get(Math.max(index-26, 0))
                                +0.002285484046142*input.get(Math.max(index-27, 0))
                                -0.000606758580570*input.get(Math.max(index-28, 0))
                                -0.001192883741798*input.get(Math.max(index-29, 0))
                                +0.001128169580325*input.get(Math.max(index-30, 0))
                                +0.0001081585096803*input.get(Math.max(index-31, 0))
                                -0.000803993770218*input.get(Math.max(index-32, 0))
                                +0.000422687550919*input.get(Math.max(index-33, 0))
                                +0.0002492026134532*input.get(Math.max(index-34, 0))
                                -0.000418437384649*input.get(Math.max(index-35, 0))
                                +0.0001099568341645*input.get(Math.max(index-36, 0))
                                +0.0001818118740379*input.get(Math.max(index-37, 0))
                                -0.0001819042045230*input.get(Math.max(index-38, 0))
                                +0.00001376031370469*input.get(Math.max(index-39, 0))
                                +0.0000944464605727*input.get(Math.max(index-40, 0))
                                -0.0000726949275881*input.get(Math.max(index-41, 0))
                                -0.000001649184802174*input.get(Math.max(index-42, 0))
                                +0.0000454599752580*input.get(Math.max(index-43, 0))
                                -0.0000434882759643*input.get(Math.max(index-44, 0))
                                +0.00002296858969507*input.get(Math.max(index-45, 0))
                                -0.00000726301267758*input.get(Math.max(index-46, 0))
                                +0.000001073598073258*input.get(Math.max(index-47, 0))
                                +0.000000082040859009*input.get(Math.max(index-48, 0))
                                -0.000000045208071155*input.get(Math.max(index-49, 0));

            case 10:
                return   0.412811092345*input.get(index)
                        +0.355641710546*input.get(Math.max(index - 1, 0))
                        +0.2555772895076*input.get(Math.max(index - 2, 0))
                        +0.1369165973419*input.get(Math.max(index - 3, 0))
                        +0.02686304531672*input.get(Math.max(index - 4, 0))
                        -0.0521769521715*input.get(Math.max(index - 5, 0))
                        -0.0884588057873*input.get(Math.max(index - 6, 0))
                        -0.0831860965254*input.get(Math.max(index - 7, 0))
                        -0.0487118807291*input.get(Math.max(index - 8, 0))
                        -0.00350277417466*input.get(Math.max(index - 9, 0))
                        +0.0342788087231*input.get(Math.max(index - 10, 0))
                        +0.0523888853817*input.get(Math.max(index - 11, 0))
                        +0.0475546667181*input.get(Math.max(index - 12, 0))
                        +0.02520586307093*input.get(Math.max(index - 13, 0))
                        -0.00368699254568*input.get(Math.max(index - 14, 0))
                        -0.02728515336991*input.get(Math.max(index - 15, 0))
                        -0.0372846351129*input.get(Math.max(index - 16, 0))
                        -0.03152767975630*input.get(Math.max(index - 17, 0))
                        -0.01407852615658*input.get(Math.max(index - 18, 0))
                        +0.00700110348243*input.get(Math.max(index - 19, 0))
                        +0.02312862554055*input.get(Math.max(index - 20, 0))
                        +0.02851851989785*input.get(Math.max(index - 21, 0))
                        +0.02211687547876*input.get(Math.max(index - 22, 0))
                        +0.00755235313221*input.get(Math.max(index - 23, 0))
                        -0.00869944464385*input.get(Math.max(index - 24, 0))
                        -0.02008231537255*input.get(Math.max(index - 25, 0))
                        -0.02251758768719*input.get(Math.max(index - 26, 0))
                        -0.01578694859550*input.get(Math.max(index - 27, 0))
                        -0.00330085253872*input.get(Math.max(index - 28, 0))
                        +0.00953015453459*input.get(Math.max(index - 29, 0))
                        +0.01757959219632*input.get(Math.max(index - 30, 0))
                        +0.01800400103278*input.get(Math.max(index - 31, 0))
                        +0.01119557677629*input.get(Math.max(index - 32, 0))
                        +0.000390958490311*input.get(Math.max(index - 33, 0))
                        -0.00981456377333*input.get(Math.max(index - 34, 0))
                        -0.01538550702846*input.get(Math.max(index - 35, 0))
                        -0.01441074566486*input.get(Math.max(index - 36, 0))
                        -0.00771949678782*input.get(Math.max(index - 37, 0))
                        +0.001631697080736*input.get(Math.max(index - 38, 0))
                        +0.00972371543905*input.get(Math.max(index - 39, 0))
                        +0.01339417009587*input.get(Math.max(index - 40, 0))
                        +0.01145167280688*input.get(Math.max(index - 41, 0))
                        +0.00502725766998*input.get(Math.max(index - 42, 0))
                        -0.003022900631165*input.get(Math.max(index - 43, 0))
                        -0.00936694634454*input.get(Math.max(index - 44, 0))
                        -0.01156038159001*input.get(Math.max(index - 45, 0))
                        -0.00897159657458*input.get(Math.max(index - 46, 0))
                        -0.002926150423218*input.get(Math.max(index - 47, 0))
                        +0.00394124706681*input.get(Math.max(index - 48, 0))
                        +0.00882105617866*input.get(Math.max(index - 49, 0))
                        +0.00986665604256*input.get(Math.max(index - 50, 0))
                        +0.00687961415634*input.get(Math.max(index - 51, 0))
                        +0.001293904479950*input.get(Math.max(index - 52, 0))
                        -0.00449412421533*input.get(Math.max(index - 53, 0))
                        -0.00814380034140*input.get(Math.max(index - 54, 0))
                        -0.00830768632714*input.get(Math.max(index - 55, 0))
                        -0.00511687156582*input.get(Math.max(index - 56, 0))
                        -0.0000445692183062*input.get(Math.max(index - 57, 0))
                        +0.00476192791482*input.get(Math.max(index - 58, 0))
                        +0.00738283631512*input.get(Math.max(index - 59, 0))
                        +0.00688526196671*input.get(Math.max(index - 60, 0))
                        +0.00364332286119*input.get(Math.max(index - 61, 0))
                        -0.000885426588501*input.get(Math.max(index - 62, 0))
                        -0.00480675636141*input.get(Math.max(index - 63, 0))
                        -0.00657629091712*input.get(Math.max(index - 64, 0))
                        -0.00560133038342*input.get(Math.max(index - 65, 0))
                        -0.002426465653825*input.get(Math.max(index - 66, 0))
                        +0.001548912237520*input.get(Math.max(index - 67, 0))
                        +0.00468108320773*input.get(Math.max(index - 68, 0))
                        +0.00575700257575*input.get(Math.max(index - 69, 0))
                        +0.00445821561554*input.get(Math.max(index - 70, 0))
                        +0.001439132205861*input.get(Math.max(index - 71, 0))
                        -0.001990574524888*input.get(Math.max(index - 72, 0))
                        -0.00442928836396*input.get(Math.max(index - 73, 0))
                        -0.00495229516478*input.get(Math.max(index - 74, 0))
                        -0.00345676171484*input.get(Math.max(index - 75, 0))
                        -0.000656883117772*input.get(Math.max(index - 76, 0))
                        +0.002249336676810*input.get(Math.max(index - 77, 0))
                        +0.00408885509014*input.get(Math.max(index - 78, 0))
                        +0.00418356614118*input.get(Math.max(index - 79, 0))
                        +0.002594540898784*input.get(Math.max(index - 80, 0))
                        +0.0000553128562730*input.get(Math.max(index - 81, 0))
                        -0.002361195395403*input.get(Math.max(index - 82, 0))
                        -0.00369291117546*input.get(Math.max(index - 83, 0))
                        -0.00346842691132*input.get(Math.max(index - 84, 0))
                        -0.001867672110957*input.get(Math.max(index - 85, 0))
                        +0.000388082612835*input.get(Math.max(index - 86, 0))
                        +0.002357221520603*input.get(Math.max(index - 87, 0))
                        +0.00326814890759*input.get(Math.max(index - 88, 0))
                        +0.002818619403996*input.get(Math.max(index - 89, 0))
                        +0.001268927910240*input.get(Math.max(index - 90, 0))
                        -0.000695777711141*input.get(Math.max(index - 91, 0))
                        -0.002265349161073*input.get(Math.max(index - 92, 0))
                        -0.002836294763406*input.get(Math.max(index - 93, 0))
                        -0.002240906737136*input.get(Math.max(index - 94, 0))
                        -0.000787645344714*input.get(Math.max(index - 95, 0))
                        +0.000891126810789*input.get(Math.max(index - 96, 0))
                        +0.002111930144085*input.get(Math.max(index - 97, 0))
                        +0.002416233672577*input.get(Math.max(index - 98, 0))
                        +0.001739651574046*input.get(Math.max(index - 99, 0))
                        +0.000412892652433*input.get(Math.max(index - 100, 0))
                        -0.000994996596019*input.get(Math.max(index - 101, 0))
                        -0.001918672766073*input.get(Math.max(index - 102, 0))
                        -0.002021509771176*input.get(Math.max(index - 103, 0))
                        -0.001315124358665*input.get(Math.max(index - 104, 0))
                        -0.0001321787206968*input.get(Math.max(index - 105, 0))
                        +0.001026780931878*input.get(Math.max(index - 106, 0))
                        +0.001703344781971*input.get(Math.max(index - 107, 0))
                        +0.001660729205597*input.get(Math.max(index - 108, 0))
                        +0.000963117418202*input.get(Math.max(index - 109, 0))
                        -0.0000697653913498*input.get(Math.max(index - 110, 0))
                        -0.001006622660525*input.get(Math.max(index - 111, 0))
                        -0.001483086478152*input.get(Math.max(index - 112, 0))
                        -0.001341540888869*input.get(Math.max(index - 113, 0))
                        -0.000679647790356*input.get(Math.max(index - 114, 0))
                        +0.0002058883085059*input.get(Math.max(index - 115, 0))
                        +0.000950154297969*input.get(Math.max(index - 116, 0))
                        +0.001268992962424*input.get(Math.max(index - 117, 0))
                        +0.001065581251561*input.get(Math.max(index - 118, 0))
                        +0.000455760583637*input.get(Math.max(index - 119, 0))
                        -0.0002925184086283*input.get(Math.max(index - 120, 0))
                        -0.000874922188537*input.get(Math.max(index - 121, 0))
                        -0.001073288207873*input.get(Math.max(index - 122, 0))
                        -0.000835023781472*input.get(Math.max(index - 123, 0))
                        -0.0002828823174691*input.get(Math.max(index - 124, 0))
                        +0.000345499900625*input.get(Math.max(index - 125, 0))
                        +0.000797989615227*input.get(Math.max(index - 126, 0))
                        +0.000907432796666*input.get(Math.max(index - 127, 0))
                        +0.000651212333867*input.get(Math.max(index - 128, 0))
                        +0.0001506822556327*input.get(Math.max(index - 129, 0))
                        -0.000384840428786*input.get(Math.max(index - 130, 0))
                        -0.000743673762986*input.get(Math.max(index - 131, 0))
                        -0.000792911811139*input.get(Math.max(index - 132, 0))
                        -0.000524310347491*input.get(Math.max(index - 133, 0))
                        -0.0000504444431105*input.get(Math.max(index - 134, 0))
                        +0.000442883789106*input.get(Math.max(index - 135, 0))
                        +0.000768485201285*input.get(Math.max(index - 136, 0))
                        +0.000802596979347*input.get(Math.max(index - 137, 0))
                        +0.000524739213257*input.get(Math.max(index - 138, 0))
                        +0.00001551748805700*input.get(Math.max(index - 139, 0))
                        -0.000578032271651*input.get(Math.max(index - 140, 0))
                        -0.001095420416889*input.get(Math.max(index - 141, 0))
                        -0.001414635671128*input.get(Math.max(index - 142, 0))
                        -0.001483948206384*input.get(Math.max(index - 143, 0))
                        -0.001326429865682*input.get(Math.max(index - 144, 0))
                        -0.001020246913650*input.get(Math.max(index - 145, 0))
                        -0.000665694566242*input.get(Math.max(index - 146, 0))
                        -0.000352665284736*input.get(Math.max(index - 147, 0))
                        -0.0001435194754345*input.get(Math.max(index - 148, 0))
                        +0.0000725499276845*input.get(Math.max(index - 149, 0));
        }
        return 0.0;
    }

    public void calculateWinLoss(ArrayList<Double> input, ArrayList<Double> output, ArrayList<Integer> signal) {
        int total = input.size();
        int currentSignal;
        int previousSignal;
        boolean bought = false;
        boolean sold = false;
        double boughtPrice = 0.0;
        double soldPrice = 0.0;
        int winCount = 0;
        int lossCount = 0;
        double profitSize = 50 * 0.00001; // 50 points
        ArrayList<Integer> winLossList = new ArrayList<Integer>();
        ArrayList<Integer> winLossInputIndex = new ArrayList<Integer>();


        for(int i = 0; i < total; i++) {

            currentSignal = signal.get(i);
            if(i -1 < 0) {
                previousSignal = signal.get(0);
            } else {
                previousSignal = signal.get(i - 1);
            }

            // trend reverse
            if(currentSignal == 1 && previousSignal == 0) {
                if(sold) {
                    sold = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                }
                bought = true;
                boughtPrice = input.get(i);
            }

            if(currentSignal == 0 && previousSignal == 1) {
                if(bought) {
                    bought = false;
                    lossCount++;
                    winLossList.add(0);
                    winLossInputIndex.add(i);
                }
                sold = true;
                soldPrice = input.get(i);
            }

            // trend continue
            if(currentSignal ==1 && previousSignal ==1) {
                // buy trend continue
                double currentPrice = input.get(i);
                if(bought) {
                    if (currentPrice - boughtPrice >= profitSize) {
                        winCount++;
                        winLossList.add(1);
                        winLossInputIndex.add(i);
                        bought = false;
                    }
                }

            }

            if(currentSignal == 0 && previousSignal == 0) {
                // sell trend continue
                double currentPrice = input.get(i);
                if(sold) {
                    if(soldPrice - currentPrice >= profitSize) {
                        winCount++;
                        winLossList.add(1);
                        winLossInputIndex.add(1);
                        sold = false;

                    }
                }
            }
        }
        System.out.println("Moving Average FN P&L with ("+this.filterMode+")");
        System.out.println("Moving Average FN  Win Count: " + winCount);
        System.out.println("Moving Average FN  Lose Count: " + lossCount);

        System.out.println("----------------------------------------");
        int tempWin = 0, tempLoss = 0;
        int maxLossRow = 0, tempMaxLossRow = 0;
        int maxLossRowPoint = 0;

        for(int i = 0; i < winLossList.size(); i++) {
            if(winLossList.get(i) == 0) {
                tempLoss++;
                tempMaxLossRow++;
                if(tempMaxLossRow > maxLossRow) {
                    maxLossRow = tempMaxLossRow;
                    maxLossRowPoint = i;
                }
            } else {
                tempWin++;
                tempMaxLossRow = 0;
            }
            //System.out.println(i + " :" + winLossList.get(i));
        }
        System.out.println("tempwins: " + tempWin + " tempLoss: " + tempLoss);
        System.out.println("Max loss in a row: " + maxLossRow + " end at: " + winLossInputIndex.get(maxLossRowPoint));

    }
}
