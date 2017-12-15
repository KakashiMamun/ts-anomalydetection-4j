package com.github.kakashimamun.utils;


/**
 * This class is copied from https://github.com/chen0040/java-data-frame
 */

public class StringUtils {
   public static double parseDouble(String text) {
      try {
         return Double.parseDouble(text);
      } catch(NumberFormatException ex) {
         return 0;
      }
   }

   public static String stripQuote(String sentence){
      if(sentence.startsWith("\"") && sentence.endsWith("\"")){
         return sentence.substring(1, sentence.length()-1);
      }
      return sentence;
   }


   public static boolean isEmpty(String line) {
      return line == null || line.equals("");
   }
}
