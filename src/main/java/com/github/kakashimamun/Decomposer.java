package com.github.kakashimamun;

import com.github.servicenow.ds.stats.stl.SeasonalTrendLoess;

/**
 * Created by Kakas on 12/14/2017.
 */
public class Decomposer {

    SeasonalTrendLoess.Decomposition stl;

    public Decomposer(double[] values,int period){

        SeasonalTrendLoess.Builder builder = new SeasonalTrendLoess.Builder();
        SeasonalTrendLoess smoother = builder.
                setPeriodLength(period).    // Data has a period of 12
                setPeriodic().
                setNonRobust().         // Not expecting outliers, so no robustness iterations
                buildSmoother(values);

        stl = smoother.decompose();
    }

    public double[] getSeasonal(){
        return stl.getSeasonal();
    }
    public double[] getTrend(){
        return stl.getTrend();
    }
    public double[] getResidual(){
        return stl.getResidual();
    }

    public double[] getData(){
        return stl.getData();
    }

    public double[] getWeights(){
        return stl.getWeights();
    }
}
