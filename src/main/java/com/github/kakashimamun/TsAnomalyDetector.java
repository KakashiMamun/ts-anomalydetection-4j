package com.github.kakashimamun;

import com.github.kakashimamun.data.DataFrame;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Builder
public class TsAnomalyDetector {

    @Builder
    @Getter
    static final class DataWrapper<T>{
        T point;
        boolean isAnomaly;

    }

    @Builder.Default
    private double outlierBound = SeasonalHybridESD.DEFAULT_OUTLIER_LIMIT;

    @Builder.Default
    private double alpha = SeasonalHybridESD.DEFAULT_ALPHA;



    public<T> List<DataWrapper> detect(List<T> data, int period, Function<T,Double> converter){

        SeasonalHybridESD esd = SeasonalHybridESD.builder().build();
        return detectInternal(data,period,converter,esd);
    }
    public<T> List<DataWrapper> detect(List<T> data, int period, double outlierBound, Function<T,Double> converter){

        SeasonalHybridESD esd = SeasonalHybridESD.builder().outlierPercentage(outlierBound).build();
        return detectInternal(data,period,converter,esd);
    }
    public<T> List<DataWrapper> detect(List<T> data, int period, double outlierBound, double alpha,  Function<T,Double> converter){

        SeasonalHybridESD esd = SeasonalHybridESD.builder().outlierPercentage(outlierBound).alpha(alpha).build();
        return detectInternal(data,period,converter,esd);
    }

    private <T> List<DataWrapper> detectInternal(List<T> data, int period,Function<T,Double> converter, SeasonalHybridESD esd){
        double[] d = data.stream().mapToDouble(v->converter.apply(v)).toArray();

        STLDecomposer decomposer = new STLDecomposer(d,period);
        boolean[] anomalies =  getAnomalies(decomposer,esd);

        List<DataWrapper> value = new ArrayList<>(data.size());

        int i=0;
        for(Boolean val:anomalies){
            value.add(i,DataWrapper.builder().isAnomaly(val).point(data.get(i)).build());
            i++;
        }

        return value;
    }



    private boolean[] getAnomalies(STLDecomposer decomposer, SeasonalHybridESD esd){
        boolean[] anomalies = esd.PerformESD(new DataFrame(decomposer.getResidual()));
        return anomalies;
    }
}
