import edu.ucla.cs.bigfuzz.customarray.CustomArray;


import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static edu.ucla.cs.bigfuzz.customarray.applicable.JsonSalary.JsonSalaryAnalysisCustomArray.*;

public class JsonSalaryAnalysis {

    public void JsonSalaryAnalysis(String inputFile) throws IOException {
        File file=new File(inputFile);
        ArrayList<String> list;
        if(file.exists()) {
            list = CustomArray.read(inputFile);
        } else {
            System.out.println("File does not exist!");
            return;
        }

        JSONArray jsonArray = parseJSON(inputFile);
        JSONArray results1 = map1(jsonArray);
        JSONArray results2 = filter1(results1, 90024);
        JSONArray results3 = map2(results2);
        JSONArray results4 = mapValues1(results3);
        JSONArray results5 = reduceByKey1(results4);
        JSONArray results6 = mapValues2(results5);
    }
}
