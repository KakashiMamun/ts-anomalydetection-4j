package com.github.kakashimamun.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This class is copied from https://github.com/chen0040/java-data-frame
 */


public class CollectionUtils {
   public static <T> List<T> clone(List<T> that, Function<T, T> transformer) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.size(); ++i){
         result.add(transformer.apply(that.get(i)));
      }
      return result;
   }


   public static <T> List<T> toList(T[] that, Function<T, T> transformer) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(transformer.apply(that[i]));
      }
      return result;
   }

   public static List<Double> toList(double[] that) {
      List<Double> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(that[i]);
      }
      return result;
   }

   public static <T> void exchange(List<T> a, int i, int j) {
      T temp = a.get(i);
      a.set(i, a.get(j));
      a.set(j, temp);
   }


   public static double[] toDoubleArray(List<Double> list) {
      double[] result = new double[list.size()];
      for(int i=0; i < list.size(); ++i) {
         result[i] = list.get(i);
      }
      return result;
   }

   public static String[] toArray(List<String> list) {
      String[] result = new String[list.size()];
      for(int i=0; i < list.size(); ++i) {
         result[i] = list.get(i);
      }
      return result;
   }
}
