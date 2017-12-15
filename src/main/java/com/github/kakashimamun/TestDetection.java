package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kakas on 12/14/2017.
 */
public class TestDetection {
    public static void main(String[] args) {

        List<XYChart> charts = new ArrayList<XYChart>();

        double[] d = Reader.read("src/main/resources/raw_data").stream().mapToDouble(v->(double)v).toArray();

        STLDecomposer decomposer = new STLDecomposer(d,24*7);

        XYChart chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();

        XYSeries series = chart.addSeries("" + 1, null, decomposer.getData());
        series.setMarker(SeriesMarkers.CIRCLE);
        charts.add(chart);

        System.out.println(decomposer.getData().length);
        System.out.println(decomposer.getResidual().length);
        System.out.println(decomposer.getTrend().length);
        System.out.println(decomposer.getSeasonal().length);

        GeneralizedESD gesd = new GeneralizedESD();

        double[] anomalies = gesd.PerformESD(new DataFrame(decomposer.getResidual()),0.01,0.05);

        double[] value = new double[anomalies.length];

        int i=0;
        for(double val:anomalies){
            if(val!=0){
                value[i] = d[i];
            }
            i++;
        }

        chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
        series = chart.addSeries("" + 1, null, value);
        series.setMarker(SeriesMarkers.NONE);
        charts.add(chart);


//        chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
//        series = chart.addSeries("" + 1, null, decomposer.getTrend());
//        series.setMarker(SeriesMarkers.NONE);
//        charts.add(chart);
//
//        chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
//        series = chart.addSeries("" + 1, null, decomposer.getResidual());
//        series.setMarker(SeriesMarkers.NONE);
//        charts.add(chart);
//
//        chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
//        series = chart.addSeries("" + 1, null, decomposer.getData());
//        series.setMarker(SeriesMarkers.NONE);
//        charts.add(chart);
//
//        chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
//        series = chart.addSeries("" + 1, null, decomposer.getWeights());
//        series.setMarker(SeriesMarkers.NONE);
//        charts.add(chart);

        new SwingWrapper<XYChart>(charts).displayChartMatrix();
    }
}
