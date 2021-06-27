import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.customarray.SalaryItem;
import org.apache.commons.lang3.tuple.Pair;

//import org.supercsv.cellprocessor.ParseDouble;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SalaryAnalysis {

    public void SalaryAnalysis(String input) throws IOException {
        File file=new File(input);
        ArrayList<String> list;
        if(file.exists())
        {
            list = CustomArray.read(input);
        }
        else
        {
            System.out.println("File does not exist!");
            return;
        }
        ArrayList<SalaryItem> results1 = CustomArray.map1(list);
        ArrayList<SalaryItem> results2 = CustomArray.filter1(results1, "90024");
        ArrayList<Pair<String, Integer>> results3 = CustomArray.map2(results2);
        ArrayList<Pair<String, Pair<Integer, Integer>>> results4 = CustomArray.mapValues1(results3);
        Map<String, Pair<Integer, Integer>> results5 = CustomArray.reduceByKey1(results4);
        Map<String, Double> result6 = CustomArray.mapValues2(results5);
    }

}