package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import com.github.kakashimamun.utils.Median;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Kakas on 12/14/2017.
 */

@Builder
public class SeasonalHybridESD {

    @Builder
    @Getter
    public static class Point implements Comparable<Point>{
        int originalIndex;
        double value;

        @Override
        public int compareTo(Point o) {
            return Double.compare(this.value,o.value);
        }
    }

    public static final double DEFAULT_ALPHA = 0.05;
    public static final double DEFAULT_OUTLIER_LIMIT = 0.01;

    @Builder.Default
    private double outlierPercentage = DEFAULT_OUTLIER_LIMIT;
    @Builder.Default
    private double alpha = DEFAULT_ALPHA;

    public boolean[] PerformESD(DataFrame df){

        // Preserving old index
        List<Point> points = IntStream.range(0, df.getDataPoints().size())
                .mapToObj(i -> Point.builder().originalIndex(i).value(df.getDataPoints().get(i)).build())
                .collect(Collectors.toList());

        double median = Median.median(points);
        double mad = mad(df.getDataPoints(),median);

        List<Point> outliers = calculateESD(points,mad);

        boolean[] anomalies = new boolean[df.getDataPoints().size()];

        for(Point p:outliers){
            anomalies[p.originalIndex] = true;
        }

        return anomalies;
    }

    private List<Point> calculateESD(List<Point> points,double medianAbsoluteDeviation) {

        int maxOutlier = calculateMaxOutlier(points.size());

        Double[] ri = new Double[maxOutlier];
        Point[] value = new Point[maxOutlier];

        for(int i=0;i<maxOutlier;i++){
            double median = Median.median(points);
            int index = 0;
            int maxIndex = 0;
            Point val = null;
            double max = Integer.MIN_VALUE;
            for(Point d:points){
                if(Math.abs(d.value-median) > max){
                    max = Math.abs(d.value-median);
                    maxIndex = index;
                    val = d;
                }
                index++;
            }

            ri[i] = (max/medianAbsoluteDeviation);
            value[i] = val;
            points.remove(maxIndex);
        }

        Double[] gamma = new Double[maxOutlier];
        int n = points.size();
        for(int i=0;i<maxOutlier;i++){
            int nn = n-(i+1)-1;
            int nm = n-(i+1);
            int np = n-(i+1)+1;
            TDistribution distribution = new TDistribution(nn);
            double p = 1- (alpha/(2*np));
            double tpn = distribution.density(p);
            gamma[i] = (nm*tpn)/Math.sqrt((nn+tpn*tpn)*(np));

        }

        List<Point> anomaly = new ArrayList<>();

        for(int i=0;i<maxOutlier;i++){
            if(ri[i]>gamma[i]){
                anomaly.add(value[i]);
            }
        }

        return anomaly;
    }

    private int calculateMaxOutlier(int size) {
        double s = 1/size;

        double max =  outlierPercentage<s? s:outlierPercentage;
        return (int) (max*size);
    }

    private Double mad(List<Double> values, double median) {
        List<Double> input = new ArrayList<>(values);
        arrayAbsDistance(input, median);
        return Median.medianDouble(input);
    }

    private void arrayAbsDistance(List<Double> array, double value) {
        for (int i=0; i<array.size();i++) {
            array.set(i, Math.abs(array.get(i) - value));
        }
    }

}
