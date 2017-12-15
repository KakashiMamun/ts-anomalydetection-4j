package com.github.kakashimamun.utils;

import java.util.Collection;

/**
 * This class is copied from https://github.com/chen0040/java-data-frame
 */

public class Mean {
    public static double apply(double[] values){
        int length = values.length;
        if(length==0) return Double.NaN;
        double sum = 0;
        for(int i=0; i < length; ++i){
            sum += values[i];
        }
        return sum / length;
    }

    public static double apply(Double[] values) {
        return apply(values);
    }


    public static double apply(Collection values) {
        int length = values.size();
        if(length==0) return Double.NaN;
        double[] sum = {0};

        values.forEach(value->{
            sum[0] += (double) value;
        });
        return sum[0] / length;
    }
}
