package com.github.kakashimamun.utils;

import com.github.kakashimamun.GeneralizedESD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Median {


    public static Double median(List<GeneralizedESD.Point> points) {
        if (points.size()==0) {
            throw new IllegalArgumentException("to calculate median we need at least 1 element");
        }

        List<GeneralizedESD.Point> input = new ArrayList<GeneralizedESD.Point>(points);

        Collections.sort(input);

        if (input.size()%2==0) {
            return (input.get(input.size()/2-1).getValue() + input.get(input.size()/2).getValue())/2;
        }
        return input.get(input.size()/2).getValue();
    }

    public static Double medianDouble(List<Double> points) {
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
