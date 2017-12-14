package com.github.kakashimamun;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kakas on 12/14/2017.
 */
public class ESD {

    double[] data;

    public void PerformESD(double[] values){
        double median = median(values);
        double mad = mad(values);


    }

    private Double mad(double[] values) {
        double[] input = values.clone();
        Double median = median(input);
        arrayAbsDistance(input, median);
        return median(input);
    }

    private void arrayAbsDistance(double[] array, double value) {
        for (int i=0; i<array.length;i++) {
            array[i] = Math.abs(array[i] - value);
        }
    }

    private Double median(double[] input) {
        if (input.length==0) {
            throw new IllegalArgumentException("to calculate median we need at least 1 element");
        }
        Arrays.sort(input);
        if (input.length%2==0) {
            return (input[input.length/2-1] + input[input.length/2])/2;
        }
        return input[input.length/2];
    }
}
