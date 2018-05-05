package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kakas on 12/14/2017.
 */
public class TestDetection {
    public static void main(String[] args) {

        List<XYChart> charts = new ArrayList<XYChart>();

        List<Double> d = Reader.read("src/main/resources/raw_data");

        XYChart chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(800).height(600).build();
        XYSeries series = chart.addSeries("Real Data", null, d);
        series.setMarker(SeriesMarkers.CIRCLE);
        charts.add(chart);

        TsAnomalyDetector detector = TsAnomalyDetector.builder().alpha(0.05).outlierBound(0.005).type(SeasonalHybridESD.ESDType.BOTH).build();

        List<TsAnomalyDetector.DataWrapper> anomalies = detector.detect(d, 24*7,Double::doubleValue);

        double[] value = new double[anomalies.size()];

        int i=0;
        for(TsAnomalyDetector.DataWrapper val:anomalies){
            if(val.isAnomaly){
                value[i] = (double) val.getPoint();
            }
            i++;
        }

        series = chart.addSeries("Anomalies", null, value);
        series.setMarker(SeriesMarkers.SQUARE);
        series.setMarkerColor(Color.RED);


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
