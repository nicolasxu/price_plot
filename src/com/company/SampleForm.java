package com.company;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Created by nick on 7/1/15.
 */
public class SampleForm extends ApplicationFrame {
    private JButton clickMeButton;
    private JPanel rootPanel;

    public KalmanFilter kFilter;
    public ALF_2 laguerreFilter;
    public MA_fn mafnFilter;
    SuperSmootherFilter ssFilter;
    FATL fatlFilter2;
    FatlStepFilter fatlStepFilter;
    StepFilter stepFilter;
    NoFilter noFilter;
    AlStepFilter alStepFilter;
    FirFilter firFilter;
    NoLagMaFilter noLagMaFilter;
    SATL satlFilter;
    String tickFileName;
    ArrayList<Double> filterInputData;
    int fisherPeriod;

    public SampleForm() {


        super("plot");

       this.tickFileName = "tick50diff201101.csv";

        this.fisherPeriod = 10;
        this.filterInputData = new ArrayList<Double>();

        // 1. create data collection
        XYDataset priceDataCollection = createDataCollection();
        XYDataset fisherDataCollection = createFisherDataCollection();

        // 2. create jFreeChart
        //JFreeChart chart = createChart(priceDataCollection);
        JFreeChart chart = createCombinedChart(priceDataCollection, fisherDataCollection);

        // 3. create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        chartPanel.setPreferredSize(screenSize);

        // 4. attach chartPanel to the frame

        setContentPane(chartPanel); // rootPanel

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        clickMeButton.addActionListener(new ActionListener() {

            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(SampleForm.this, "You clicked the button!");

            }
        });

        RefineryUtilities.centerFrameOnScreen(SampleForm.this);
        setVisible(true);
    }

    private XYDataset createDataCollection() {

        // read data
        //ArrayList<Double>  filterInputData = new ArrayList<Double>();
        //ArrayList<Double> stepTicksData = new ArrayList<Double>();
        if(this.filterInputData.size() == 0) {
            Util.readCSVFileTo(this.tickFileName, filterInputData);
        }

        ArrayList<Double> compressedData = new ArrayList<Double>();
        Util.compressStepData(filterInputData, compressedData);
        System.out.println("Original data size: " + filterInputData.size() + " compressData.size: " + compressedData.size());

        //Util.tickToStep(data, 50 * 0.00001, stepTicksData);


        // find pattern in ticks
        //Util.findTickPatternSignal(data, 9);

        // series
        XYSeries priceSeries = new XYSeries("Price");
        XYSeries smoothedSeries = new XYSeries("Smoothed");
        XYSeries kalmanSeries = new XYSeries("Kalman");
        XYSeries laguerreSeries = new XYSeries("Laguerre - not adaptive");
        XYSeries mafnSeries = new XYSeries("Moving Average FN");
        XYSeries satlSeries = new XYSeries("SATL");
        XYSeries fatlSeries = new XYSeries("FATL");
        XYSeries fatlSeries2 = new XYSeries("FATL2");
        XYSeries fatl2StepSeries = new XYSeries("FATL2Step");
        XYSeries stepSeries = new XYSeries("Step");
        XYSeries noFilterSeries = new XYSeries("NoFilter");
        XYSeries alStepSeries = new XYSeries("AlStep");
        XYSeries firSeries = new XYSeries("fir");
        XYSeries noLagSeries = new XYSeries("No Lag MA");
        XYSeries stepTickSeries = new XYSeries("Step Ticks");

        // SATL filter
        ArrayList<Double> satlData = new ArrayList<Double>();
        satlFilter = new SATL();
        satlFilter.filter(filterInputData, satlData);
        //satlFilter.calculateWinLoss(data, satlData, satlFilter.buySellSignal);

        // FATL filter
        ArrayList<Double> fatlData = new ArrayList<Double>();
        FATL fatlFilter = new FATL();
        fatlFilter.filter(filterInputData, fatlData);
        //fatlFilter.calculateWinLoss(data, fatlData, fatlFilter.buySellSignal);

        // FATL2 filter
        ArrayList<Double> fatlData2 = new ArrayList<Double>();
        fatlFilter2 = new FATL();
        fatlFilter2.filter(fatlData, fatlData2);
        //fatlFilter2.calculateWinLoss(data, fatlData2, fatlFilter2.buySellSignal);


        // Fisher filter
        ArrayList<Double> fisherData = new ArrayList<Double>();
        ArrayList<Double> fisherTrigger = new ArrayList<Double>();
        FisherFilter fisherFilter = new FisherFilter(15);
        fisherFilter.filter(filterInputData, fisherData, fisherTrigger);
        //fisherFilter.calculateWinLoss(data, fisherData, fisherFilter.buySellSignal);


        // Super Smooth filter
        ArrayList<Double> smoothedData = new ArrayList<Double>();
        ssFilter = new SuperSmootherFilter(3); // 14
        ssFilter.filter(fatlData, smoothedData);
        //ssFilter.calculateWinLoss(data, smoothedData, ssFilter.buySellSignal);

        // Kalman Filter
        ArrayList<Double> kalmanData = new ArrayList<Double>();
        kFilter = new KalmanFilter(3); // 5, 10
        kFilter.filter(filterInputData, kalmanData);
        //kFilter.calculateWinLoss(filterInputData, kalmanData, kFilter.buySellSignal);
        Util.runSim3(filterInputData, kFilter.buySellSignal);


        System.out.println("input size: " + filterInputData.size() + " output size: " + kalmanData.size() + " signal size: " + kFilter.buySellSignal.size());

        // Laguerre Filter
        ArrayList<Double> laguerreData = new ArrayList<Double>();
        laguerreFilter = new ALF_2(40); // 10
        laguerreFilter.filter(smoothedData, laguerreData);
        //laguerreFilter.calculateWinLoss(data, laguerreData, laguerreFilter.buySellSignal);

        // MA_fn Filter
        ArrayList<Double> mafnData = new ArrayList<Double>();
        mafnFilter = new MA_fn(10); // must be 4 for now
        mafnFilter.filter(filterInputData, mafnData);
        //mafnFilter.calculateWinLoss(data, mafnData, mafnFilter.buySellSignal);

        // no Filter
        ArrayList<Double> noFilterData = new ArrayList<Double>();
        noFilter = new NoFilter();
        noFilter.filter(filterInputData, noFilterData);
        //Util.calculateWinLoss(filterInputData, noFilterData, noFilter.buySellSignal, "no filter");
        //Util.calculateCapStrategy(data, noFilterData, noFilter.buySellSignal, "no filter");
        //Util.calculateContinuousSignalDistribution(data, noFilterData, noFilter.buySellSignal, "no filter");


        // FatlStepFilter
        ArrayList<Double> fatl2StepOutput = new ArrayList<Double>();
        fatlStepFilter = new FatlStepFilter(50);
        fatlStepFilter.filter(filterInputData, fatl2StepOutput);
        //fatlStepFilter.calculateWinLoss(data, fatl2StepOutput, fatlStepFilter.buySellSignal);
        //fatlStepFilter.calculate2(data, fatl2StepOutput, fatlStepFilter.buySellSignal);

        // StepFilter
        ArrayList<Double> stepOutput = new ArrayList<Double>();
        stepFilter = new StepFilter(30);
        stepFilter.filter(filterInputData, stepOutput);
        //stepFilter.calculateWinLoss(data, stepOutput, stepFilter.buySellSignal);

        ArrayList<Double> alStepOutput = new ArrayList<Double>();
        alStepFilter = new AlStepFilter(10);
        alStepFilter.filter(filterInputData, alStepOutput);
        //alStepFilter.calculateWinLoss(data, alStepOutput, alStepFilter.buySellSignal);

        // fir filter
        ArrayList<Double> firOutput = new ArrayList<Double>();
        firFilter = new FirFilter(500);
        firFilter.filter(filterInputData, firOutput);
        //firFilter.calculateWinLoss(data, firOutput, firFilter.buySellSignal);
        //Util.calculateWinLoss(data, firOutput, firFilter.buySellSignal, "FIR Filter");

        // No Lag Moving Average Filter
        ArrayList<Double> noLagMaOutput = new ArrayList<Double>();
        noLagMaFilter = new NoLagMaFilter(50, 25); // length, pointFilter
        noLagMaFilter.filter(filterInputData, noLagMaOutput);
        //Util.calculateWinLoss(data, noLagMaOutput, noLagMaFilter.buySellSignal, "No Lag MA Filter");
        //Util.calculateCapStrategy(data, noLagMaOutput, noLagMaFilter.buySellSignal, "No Lag MA Filter");



        // build all tick data series
        /*
        for(int i = 0; i < data.size(); i++) {
            priceSeries.add(i, data.get(i));
            smoothedSeries.add(i, smoothedData.get(i));
            kalmanSeries.add(i, kalmanData.get(i));
            laguerreSeries.add(i, laguerreData.get(i));
            mafnSeries.add(i, mafnData.get(i));
            satlSeries.add(i, satlData.get(i));
            fatlSeries.add(i, fatlData.get(i));
            fatlSeries2.add(i, fatlData2.get(i));
            fatl2StepSeries.add(i, fatl2StepOutput.get(i));
            stepSeries.add(i, stepOutput.get(i));
            noFilterSeries.add(i, noFilterData.get(i));
            alStepSeries.add(i, alStepOutput.get(i));
            firSeries.add(i, firOutput.get(i));
            noLagSeries.add(i, noLagMaOutput.get(i));

        }
        */
        // build step tick data series
        XYSeries originalSeries = new XYSeries("Original Series");
        for(int i = 0; i < filterInputData.size(); i++) {
            noLagSeries.add(i, noLagMaOutput.get(i));
            stepTickSeries.add(i, filterInputData.get(i));
            kalmanSeries.add(i, kalmanData.get(i));


        }

        XYSeriesCollection dataCollection = new XYSeriesCollection();
        //dataCollection.addSeries(priceSeries);
        dataCollection.addSeries(stepTickSeries);
        //dataCollection.addSeries(originalSeries);
        //dataCollection.addSeries(smoothedSeries);
        dataCollection.addSeries(kalmanSeries);
        //dataCollection.addSeries(laguerreSeries);
        //dataCollection.addSeries(mafnSeries);
        //dataCollection.addSeries(satlSeries);
        //dataCollection.addSeries(fatlSeries2);
        //dataCollection.addSeries(fatlSeries);
        //dataCollection.addSeries(fatl2StepSeries);
        //dataCollection.addSeries(stepSeries);
        //dataCollection.addSeries(noFilterSeries);
        //dataCollection.addSeries(alStepSeries);
        //dataCollection.addSeries(firSeries);
        //dataCollection.addSeries(noLagSeries);

        return dataCollection;
    }

    private XYDataset createFisherDataCollection() {

        if(this.filterInputData.size() == 0) {
            Util.readCSVFileTo(this.tickFileName, filterInputData);
        }

        ArrayList<Double> fisherOutput = new ArrayList<Double>();
        ArrayList<Double> fisherTriggerOutput = new ArrayList<Double>();
        ArrayList<Double> noFilterOutput = new ArrayList<Double>();

        FisherFilter fisherFilter = new FisherFilter(this.fisherPeriod);
        NoLagMaFilter noLagMaFilter = new NoLagMaFilter(5, 25);

        noLagMaFilter.filter(this.filterInputData, noFilterOutput);
        fisherFilter.filter(filterInputData, fisherOutput, fisherTriggerOutput);
        XYSeries fisherSeries = new XYSeries("fisher");

        for(int i = 0; i < fisherOutput.size(); i++) {
            fisherSeries.add(i, fisherOutput.get(i));

        }

        XYSeriesCollection dataCollection = new XYSeriesCollection();
        dataCollection.addSeries(fisherSeries);
        return dataCollection;
    }

    private JFreeChart createChart(XYDataset dataCollection) {

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                this.tickFileName, // chart title
                "Count", // x axis label
                "Price", // y axis label
                dataCollection, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        // customize chart
        XYPlot plot = (XYPlot) chart.getPlot();
        // find out the max and min value for price series
        XYSeriesCollection collection = (XYSeriesCollection)plot.getDataset();
        XYSeries priceSeries = collection.getSeries(0);
        double maxY = priceSeries.getMaxY();
        double minY = priceSeries.getMinY();
        // set max and min for range axis (y axis)
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setRange(minY, maxY);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {

            public Color getItemColor(int series, int item) {
                // modify code here to change color for different part of the line in one serie line
                if(series == 1) {

                    int isBuy = 0;


                    // use laguerre signal
                    if(item <  kFilter.buySellSignal.size()) {
                        isBuy = kFilter.buySellSignal.get(item);
                    }

                    if(isBuy == 1) {

                        return Color.red; // buy
                    }
                    if(isBuy == -1){

                        return Color.green; // sell
                    }
                    if(isBuy == 0) {

                        return Color.pink; // neutral
                    } else {
                        return Color.yellow;
                    }
                } else {
                    return Color.yellow;
                }



            }

            @Override
            protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
                super.drawFirstPassShape(g2, pass, series, item, shape);

                //g2.setStroke(getItemStroke(series, item));
                Color c1 = getItemColor(series, item - 1);
                Color c2 = getItemColor(series, item);

                // color of the line is determined by the 1st point, c1
                GradientPaint linePaint = new GradientPaint(0, 0, c1, 0, 0, c2);
                g2.setPaint(linePaint);
                g2.draw(shape);
            }
        };

        // Customize point shape
        Rectangle rect = new Rectangle();
        rect.setRect(-1,0,2,2); // cordiantes and dimension
        renderer.setSeriesShape(1, rect);


        Ellipse2D.Double ellipse = new Ellipse2D.Double(-1,0,2,2);

        renderer.setSeriesShape(0, ellipse);

        // finaly, attach the renderer
        plot.setRenderer(renderer);

        return chart;
    }

    private XYPlot createFisherPlot(XYDataset fisherDataCollection) {
        String fisherRangeLabel = "Fisher Value";
        NumberAxis fisherValueAxis = new NumberAxis(fisherRangeLabel);
        XYPlot fisherPlot = new XYPlot(fisherDataCollection, null, fisherValueAxis, (XYItemRenderer)null);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        fisherPlot.setRenderer(renderer);

        return fisherPlot;

    }

    private CombinedDomainXYPlot createCombinedPlot(XYPlot pricePlot, XYPlot fisherPlot) {
        NumberAxis numberAxis = new NumberAxis();
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(numberAxis); // passing in the common Axis
        combinedPlot.add(pricePlot);
        combinedPlot.add(fisherPlot);
        return combinedPlot;
    }

    private JFreeChart createCombinedChart(XYDataset priceDataCollection, XYDataset fisherDataCollection ){

        // 1. get price plot
        JFreeChart priceChart = this.createChart(priceDataCollection);
        XYPlot pricePlot = (XYPlot)priceChart.getPlot();

        // 2. get fisher plot
        XYPlot fisherPlot = createFisherPlot(fisherDataCollection);

        // 3. create combined plot
        CombinedDomainXYPlot combinedPlot = createCombinedPlot(pricePlot, fisherPlot);


        // 4. create combined chart
        JFreeChart chart = new JFreeChart("Tick data analysis", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, false);

        return chart;

    }

}
