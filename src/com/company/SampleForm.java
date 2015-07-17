package com.company;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nick on 7/1/15.
 */
public class SampleForm extends ApplicationFrame {
    private JButton clickMeButton;
    private JPanel rootPanel;
    String fileName;
    public KalmanFilter kFilter;
    public ALF_2 laguerreFilter;
    public MA_fn mafnFilter;

    public SampleForm() {

        super("Hello World");

        // 1. create data collection
        XYDataset dataCollection = createDataCollection();

        // 2. create jFreeChart
        JFreeChart chart = createChart(dataCollection);

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

    private void readDataFileTo(ArrayList<Double> data) {
        this.fileName = "tick50diff201501.csv"; //"tick50diff.csv";
        String filePath = "/Users/nick/IdeaProjects/price_plot/";

        FileReader fr;
        BufferedReader br;
        String line = null;

        try {
            fr = new FileReader(filePath + fileName);
            br = new BufferedReader(fr);
            while((line = br.readLine()) != null) {


                String[] columns = line.split(",");
                double bid, ask, mid;

                if(this.fileName.contains("IB")) {
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
        /* test
        for(int i = 0 ;i < data.size(); i++) {
            System.out.println(i + ": " + data.get(i));
        }
        */


    }



    private XYDataset createDataCollection() {

        // read data
        ArrayList<Double>  data = new ArrayList<Double>();
        this.readDataFileTo(data);

        // series
        XYSeries priceSeries = new XYSeries("Price");
        XYSeries smoothedSeries = new XYSeries("Smoothed");
        XYSeries kalmanSeries = new XYSeries("Kalman");
        XYSeries laguerreSeries = new XYSeries("Laguerre - not adaptive");
        XYSeries mafnSeries = new XYSeries("Moving Average FN");

        // Fisher filter
        ArrayList<Double> fisherData = new ArrayList<Double>();
        ArrayList<Double> fisherTrigger = new ArrayList<Double>();
        FisherFilter fisherFilter = new FisherFilter(15);
        fisherFilter.filter(data, fisherData, fisherTrigger);
        fisherFilter.calculateWinLoss(data, fisherData, fisherFilter.buySellSignal);


        // Super Smooth filter
        ArrayList<Double> smoothedData = new ArrayList<Double>();
        SuperSmootherFilter ssFilter = new SuperSmootherFilter(3); // 14
        ssFilter.filter(data, smoothedData);
        //ssFilter.calculateWinLoss(data, smoothedData, ssFilter.buySellSignal);

        // Kalman Filter
        ArrayList<Double> kalmanData = new ArrayList<Double>();
        kFilter = new KalmanFilter(1); // 5, 10

        kFilter.filter(data, kalmanData);
        //kFilter.calculateWinLoss(data, kalmanData, kFilter.buySellSignal);
        System.out.println("input size: " + data.size() + " output size: " + kalmanData.size() + " signal size: " + kFilter.buySellSignal.size());

        // Laguerre Filter
        ArrayList<Double> laguerreData = new ArrayList<Double>();
        laguerreFilter = new ALF_2(2); // 10
        laguerreFilter.filter(data, laguerreData);
        //laguerreFilter.calculateWinLoss(data, laguerreData, laguerreFilter.buySellSignal);

        // MA_fn Filter
        ArrayList<Double> mafnData = new ArrayList<Double>();
        mafnFilter = new MA_fn(10); // must be 4 for now
        mafnFilter.filter(data, mafnData);
        //mafnFilter.calculateWinLoss(data, mafnData, mafnFilter.buySellSignal);




        // prepare series data for all filter series
        for(int i = 0; i < data.size(); i++) {
            priceSeries.add(i, data.get(i));
            smoothedSeries.add(i, smoothedData.get(i));
            kalmanSeries.add(i, kalmanData.get(i));
            laguerreSeries.add(i, laguerreData.get(i));
            mafnSeries.add(i, mafnData.get(i));

        }

        XYSeriesCollection dataCollection = new XYSeriesCollection();
        dataCollection.addSeries(priceSeries);
        //dataCollection.addSeries(smoothedSeries);
        //dataCollection.addSeries(kalmanSeries);
        //dataCollection.addSeries(laguerreSeries);
        dataCollection.addSeries(mafnSeries);

        return dataCollection;
    }

    private JFreeChart createChart(XYDataset dataCollection) {

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                fileName, // chart title
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

                    // use kalman signal
//                    if(item < kFilter.buySellSignal.size()) {
//                        isBuy = kFilter.buySellSignal.get(item);
//                    }

                    // use laguerre signal
                    if(item < mafnFilter.buySellSignal.size()) {
                        isBuy = mafnFilter.buySellSignal.get(item);
                    }



                    //System.out.println("item: " + item + " buySell: " + isBuy);
                    if(isBuy == 1) {

                        return Color.red;
                    }
                    if(isBuy == 0){

                        return Color.green;
                    }
                    if(isBuy == -1) {

                        return Color.pink;
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


}
