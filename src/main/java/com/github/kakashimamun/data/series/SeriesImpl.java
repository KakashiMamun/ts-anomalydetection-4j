package com.github.kakashimamun.data.series;

import com.github.kakashimamun.utils.CsvUtils;
import com.github.kakashimamun.utils.Mean;
import com.github.kakashimamun.utils.TupleTwo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class SeriesImpl implements Series {

    public static Function<SortedMap, Double> AVG() {
        return map ->{
            return Mean.apply(map.values());
        } ;
    }

    public static Function<SortedMap<Instant,Double>, Double> PERCENTILE(int quantile) {
        return map ->{

            double[] values = ArrayUtils.toPrimitive(map.values().stream().toArray(Double[]::new));

            return StatUtils.percentile(values,quantile);
        };
    }

    public static Function<SortedMap<Instant,Double>, Double> GEOMETRIC_MEAN() {
        return map ->{

            double[] values = ArrayUtils.toPrimitive(map.values().stream().toArray(Double[]::new));

            return StatUtils.geometricMean(values);
        };
    }

    public static Function<SortedMap<Instant,Double>, Double> MAX() {
        return map ->{

            double[] values = ArrayUtils.toPrimitive(map.values().stream().toArray(Double[]::new));

            return StatUtils.max(values);
        };
    }

    public static Function<SortedMap<Instant,Double>, Double> MIN() {
        return map ->{

            double[] values = ArrayUtils.toPrimitive(map.values().stream().toArray(Double[]::new));

            return StatUtils.min(values);
        };
    }


    public static SeriesImpl fromCSV(String filePath, int skipLine){

        File initialFile = new File(filePath);

        SeriesImpl series = new SeriesImpl();

        try {

            InputStream targetStream = FileUtils.openInputStream(initialFile);

            CsvUtils.csv(targetStream,",",skipLine, str->{

                Instant ins = Instant.ofEpochSecond(Long.parseLong(str[0]));
                Double value = Double.parseDouble(str[1]);

                series.addPoint(ins,value);

                return true;
            },ex->{
                System.err.println(ex);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


        return series;
    }

    private TreeMap<Instant,Double> points;

    public SeriesImpl(){
        points = new TreeMap<>();
    }

    private SeriesImpl(TreeMap points){
        this.points = points;
    }

    @Override
    public int count() {
        return points.size();
    }

    @Override
    public Double point(Instant time) {
        return points.get(time);
    }

    @Override
    public void addPoint(Instant time, Double point) {
        points.put(time,point);
    }

    @Override
    public String head(int limit) {
        StringBuilder sb = new StringBuilder();
        int max = Math.min(limit, count());
        points.entrySet().stream().limit(max).forEach(entry -> {
                sb.append(entry.getKey() + " -> "+entry.getValue());
                sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

    @Override
    public String tail(int limit) {
        StringBuilder sb = new StringBuilder();
        int max = Math.min(limit, count());

        int skip = count() - max;

        points.entrySet().stream().skip(skip).forEach(entry -> {
                sb.append(entry.getKey() + " -> "+entry.getValue());
                sb.append(System.lineSeparator());
        });

        return sb.toString();
    }

    @Override
    // TODO: implement split series
    public TupleTwo<Series, Series> split(double ratio) {
       return null;
    }

    @Override
    public Stream<Double> values() {
        return points.values().stream();
    }

    @Override
    public Stream<Map.Entry<Instant,Double>> points() {
        return points.entrySet().stream();
    }

    @Override
    public Series makeCopy() {
        return null;
    }


    @Override
    public Series reSample(ChronoUnit unit, Function<SortedMap<Instant,Double>, Double> function) {

        TreeMap<Instant, Double> clonedPoints = (TreeMap<Instant, Double>) points.clone();

        TreeMap<Instant, Double> sampled = new TreeMap<>();

        Instant start = clonedPoints.firstKey();
        Instant end = clonedPoints.lastKey();

        Instant current = start;

        while (current.isBefore(end)){
            Instant subMapEnd = current.plus(unit.getDuration());

            SortedMap<Instant, Double> subMap = clonedPoints.subMap(current, subMapEnd);

            if(subMap.isEmpty()){
                subMap = sampled.subMap(current, subMapEnd);
            }

            sampled.put(current, function.apply(subMap));

            current = subMapEnd;
        }

        return new SeriesImpl(sampled).preFillNa();
    }

    @Override
    public Series preFillNa(){

        double[] prev = new double[1];

        this.points.entrySet().forEach(instantDoubleEntry -> {
            if(instantDoubleEntry.getValue() != null && !instantDoubleEntry.getValue().isNaN()){
                prev[0] = instantDoubleEntry.getValue();
            }else {
                instantDoubleEntry.setValue(prev[0]);
            }
        });

        return this;
    }

    @Override
    public Instant first() {
        return points.firstKey();
    }

    @Override
    public Instant last() {
        return points.lastKey();
    }

    @Override
    public Instant floor(Instant instant) {
        return points.floorKey(instant);
    }

    @Override
    public Instant ceil(Instant instant) {
        return points.ceilingKey(instant);
    }

    @Override
    public Instant lower(Instant instant) {
        return points.lowerKey(instant);
    }

    @Override
    public Instant higher(Instant instant) {
        return points.higherKey(instant);
    }

}
