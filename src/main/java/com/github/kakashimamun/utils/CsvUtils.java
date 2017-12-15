package com.github.kakashimamun.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This class is copied from https://github.com/chen0040/java-data-frame
 */

public class CsvUtils {
   public static final String quoteSplitPM = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";



   public static int atoi(String s)
   {
      int value = 0;
      try {
         value = Integer.parseInt(s);
      }catch(NumberFormatException ex){
         value = 0;
      }
      return value;
   }

   public static List<Map<Integer, String>> readHeartScale(InputStream inputStream){
      try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
         List<String> lines = reader.lines().collect(Collectors.toList());
         return lines.stream()
                 .filter(line -> !StringUtils.isEmpty(line))
                 .map(line -> {

                    StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

                    String label = st.nextToken();
                    Map<Integer, String> row = new HashMap<>();

                    int m = st.countTokens() / 2;
                    for (int j = 0; j < m; j++) {
                       int index = atoi(st.nextToken());
                       String value = st.nextToken();

                       row.put(index, value);
                    }


                    row.put(0, label);
                    return row;
                 })
                 .collect(Collectors.toList());
      }
      catch (IOException e) {
         System.err.println("Failed to read the heartScale data");
      }

      return new ArrayList<>();


   }

   public static boolean csv(InputStream inputStream, String cvsSplitBy, int skippedLineCount, Function<String[], Boolean> onLineReady, Consumer<Exception> onFailed){

      String line;
      if(cvsSplitBy==null) cvsSplitBy = ",";


      boolean success = true;
      try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
         int lineCount = 0;
         while ((line = br.readLine()) != null) {

            lineCount++;

            if(lineCount <= skippedLineCount) {
               continue;
            }

            line = line.trim();

            if(line.equals("")) continue;

            boolean containsQuote = false;
            if(line.contains("\"")){
               containsQuote = true;
               cvsSplitBy = cvsSplitBy + quoteSplitPM;
            }

            String[] values = filterEmpty(line.split(cvsSplitBy));

            if(containsQuote){
               for(int i=0; i < values.length; ++i){
                  values[i] = StringUtils.stripQuote(values[i]);
               }
            }

            if(onLineReady != null){
               onLineReady.apply(values);
            }

         }

      }
      catch (IOException e) {
         success = false;
         if(onFailed != null) onFailed.accept(e);
         else e.printStackTrace();
      }

      return success;
   }

   private static String[] filterEmpty(String[] a) {
      List<String> result = new ArrayList<>();
      for(int i=0; i < a.length; ++i){
         String v = a[i].trim();
         if(StringUtils.isEmpty(v)){
            continue;
         }
         result.add(v);
      }

      return CollectionUtils.toArray(result);

   }

}
