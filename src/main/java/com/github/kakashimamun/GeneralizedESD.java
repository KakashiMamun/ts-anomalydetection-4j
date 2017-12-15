package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import com.github.kakashimamun.utils.Median;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Kakas on 12/14/2017.
 */
public class GeneralizedESD {

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

    public static final double DEFAULT_K = 0.2;

    public double[] PerformESD(DataFrame df,double maxOutliers,double alpha){

        int outlierLimit = checkNUpdate(maxOutliers,df.getDataPoints().size());

        // Preserving old index
        List<Point> points = IntStream.range(0, df.getDataPoints().size())
                .mapToObj(i -> Point.builder().originalIndex(i).value(df.getDataPoints().get(i)).build())
                .collect(Collectors.toList());

        double median = Median.median(points);
        double mad = mad(df.getDataPoints(),median);

        List<Point> outliers = calculateESD(points,outlierLimit,alpha,mad);

        double[] anomalies = new double[df.getDataPoints().size()];

        for(Point p:outliers){
            anomalies[p.originalIndex] = p.value;
        }

        return anomalies;

    }

    private List<Point> calculateESD(List<Point> points,int outlierLimit,double alpha,double mad) {

        Double[] ri = new Double[outlierLimit];
        Point[] value = new Point[outlierLimit];

        for(int i=0;i<outlierLimit;i++){
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

            ri[i] = (max/mad);
            value[i] = val;
            points.remove(maxIndex);
        }

        Double[] gamma = new Double[outlierLimit];
        int n = points.size();
        for(int i=0;i<outlierLimit;i++){
            int nn = n-(i+1)-1;
            int nm = n-(i+1);
            int np = n-(i+1)+1;
            TDistribution distribution = new TDistribution(nn);
            double p = 1- (alpha/(2*np));
            double tpn = distribution.density(p);
            gamma[i] = (nm*tpn)/Math.sqrt((nn+tpn*tpn)*(np));

        }

        List<Point> anomaly = new ArrayList<>();

        for(int i=0;i<outlierLimit;i++){
            if(ri[i]>gamma[i]){
                anomaly.add(value[i]);
            }
        }

        return anomaly;
    }

    private int checkNUpdate(double maxOutliers, int size) {
        double max =  maxOutliers<1/size? 1/size:maxOutliers;

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
