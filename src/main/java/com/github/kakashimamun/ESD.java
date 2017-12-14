package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import lombok.Builder;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Kakas on 12/14/2017.
 */
public class ESD {

    @Builder
    static class Point implements Comparable<Point>{
        int originalIndex;
        double value;

        @Override
        public int compareTo(Point o) {
            return Double.compare(this.value,o.value);
        }
    }

    public double[] PerformESD(DataFrame df){

        // Preserving old index
        List<Point> points = IntStream.range(0, df.getDataPoints().size())
                .mapToObj(i -> Point.builder().originalIndex(i).value(df.getDataPoints().get(i)).build())
                .collect(Collectors.toList());

        double median = median(points);
        double mad = mad(df.getDataPoints(),median);

        Double[] ri = new Double[16];
        Point[] value = new Point[16];

        for(int i=0;i<16;i++){
            median = median(points);
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

        Double[] gamma = new Double[16];
        int n = df.getDataPoints().size();
        for(int i=0;i<ri.length;i++){
            int nn = n-i-2;
            int nm = n-i-1;
            int np = n-i;
            TDistribution distribution = new TDistribution(nn);
            double p = 1- (0.5/(2*np));
            double tpn = distribution.density(p);
            gamma[i] = (nm*tpn)/Math.sqrt((nn+tpn*tpn)*(np));

        }

        List<Point> anomaly = new ArrayList<>();

        for(int i=0;i<16;i++){
            if(ri[i]>gamma[i]){
               anomaly.add(value[i]);
            }
        }

        double[] anom = new double[df.getDataPoints().size()];

        for(Point p:anomaly){
            anom[p.originalIndex] = p.value;
        }

        return anom;

    }

    private Double mad(List<Double> values, double median) {
        List<Double> input = new ArrayList<>(values);
        arrayAbsDistance(input, median);
        return medianDouble(input);
    }

    private void arrayAbsDistance(List<Double> array, double value) {
        for (int i=0; i<array.size();i++) {
            array.set(i, Math.abs(array.get(i) - value));
        }
    }

    private Double median(List<Point> points) {
        if (points.size()==0) {
            throw new IllegalArgumentException("to calculate median we need at least 1 element");
        }

        List<Point> input = new ArrayList<Point>(points);

        Collections.sort(input);

        if (input.size()%2==0) {
            return (input.get(input.size()/2-1).value + input.get(input.size()/2).value)/2;
        }
        return input.get(input.size()/2).value;
    }

    private Double medianDouble(List<Double> points) {
        if (points.size()==0) {
            throw new IllegalArgumentException("to calculate median we need at least 1 element");
        }

        List<Double> input = new ArrayList<Double>(points);

        Collections.sort(input);

        if (input.size()%2==0) {
            return (input.get(input.size()/2-1) + input.get(input.size()/2))/2;
        }
        return input.get(input.size()/2);
    }
}
