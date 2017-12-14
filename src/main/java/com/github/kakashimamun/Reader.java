package com.github.kakashimamun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kakas on 12/14/2017.
 */
public class Reader {

    public static List<Double> read(String filename){

        List<Double> doubles = new ArrayList<Double>();

        try {
            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                doubles.add(Double.valueOf(line));
            }
            fileReader.close();
            System.out.println("Contents of file:");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doubles;
    }
}
