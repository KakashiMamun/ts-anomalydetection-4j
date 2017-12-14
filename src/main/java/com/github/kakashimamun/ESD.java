package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kakas on 12/14/2017.
 */
public class ESD {

    public List<Double> PerformESD(DataFrame df){
        double median = median(df.getDataPoints());
        double mad = mad(df.getDataPoints(),median);

        List<Double> points = new ArrayList<>(df.getDataPoints());

        Double[] ri = new Double[16];
        Double[] value = new Double[16];

        for(int i=0;i<16;i++){
            median = median(points);
            int index = 0;
            int maxIndex = 0;
            double val = 0;
            double max = Integer.MIN_VALUE;
            for(Double d:points){
                if(Math.abs(d-median) > max){
                    max = Math.abs(d-median);
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

        List<Double> anomaly = new ArrayList<>();

        for(int i=0;i<16;i++){
            if(ri[i]>gamma[i]){
               anomaly.add(value[i]);
            }
        }

        return anomaly;

    }

    private Double mad(List<Double> values, double median) {
        List<Double> input = new ArrayList<>(values);
        arrayAbsDistance(input, median);
        return median(input);
    }

    private void arrayAbsDistance(List<Double> array, double value) {
        for (int i=0; i<array.size();i++) {
            array.set(i, Math.abs(array.get(i) - value));
        }
    }

    private Double median(List<Double> points) {
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
