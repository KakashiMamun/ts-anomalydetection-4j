package com.github.kakashimamun.data.series;


import com.github.kakashimamun.utils.TupleTwo;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Series extends Serializable {

    int count();

    Double point(Instant time);

    void addPoint(Instant time, Double point);

    String head(int limit);
    String tail(int limit);

    TupleTwo<Series, Series> split(double ratio);

    Stream<Double> values();

    Stream<Map.Entry<Instant,Double>> points();

    Series makeCopy();

    Series reSample(ChronoUnit unit, Function<SortedMap<Instant, Double>, Double> function);

    Series preFillNa();

    Instant first();

    Instant last();

    Instant floor(Instant instant);

    Instant ceil(Instant instant);

    Instant lower(Instant instant);

    Instant higher(Instant instant);
}
