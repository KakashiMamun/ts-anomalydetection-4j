package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TsAnomalyDetector {

    @Builder
    @Getter
    static final class DataWrapper<T>{
        T point;
        boolean isAnomaly;

    }

    public<T> List<DataWrapper> detect(List<T> data, int period, double outlierBound, double alpha, Function<T,Double> convertor){

        double[] d = data.stream().mapToDouble(v->convertor.apply(v)).toArray();

        STLDecomposer decomposer = new STLDecomposer(d,period);

        SeasonalHybridESD esd = SeasonalHybridESD.builder().alpha(alpha).outlierPercentage(outlierBound).build();

        boolean[] anomalies =  internalDetect(decomposer,esd);

        List<DataWrapper> value = new ArrayList<>(data.size());

        int i=0;
        for(Boolean val:anomalies){
            value.add(i,DataWrapper.builder().isAnomaly(val).point(data.get(i)).build());
            i++;
        }

        return value;
    }

    private boolean[] internalDetect(STLDecomposer decomposer, SeasonalHybridESD esd){
        boolean[] anomalies = esd.PerformESD(new DataFrame(decomposer.getResidual()));
        return anomalies;
    }
}
