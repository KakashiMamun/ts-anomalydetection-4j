package com.github.kakashimamun.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Kakas on 12/14/2017.
 */
@Getter
public class DataFrame{

    List<Double> dataPoints;

    private DataFrame(){
        dataPoints = new ArrayList<>();
    }

    public DataFrame(List<Objects> objs, Function<Object,Double> function){
        this();
        for(Object o:objs){
            dataPoints.add(function.apply(o));
        }
    }
    public DataFrame(Object[] objs, Function<Object,Double> function){
        this();
        for(Object o:objs){
            dataPoints.add(function.apply(o));
        }
    }
    public DataFrame(double[] values){
        this();
        for(double d:values){
            dataPoints.add(d);
        }
    }


}
