import edu.ucla.cs.bigfuzz.customarray.applicable.JsonStudentGrades.JsonStudentGradesCustomArray;
import org.json.simple.JSONArray;

import java.io.IOException;

public class JsonStudentGrades {

    public void JsonStudentGrades(String inputFile) throws IOException {
        JSONArray results0 = JsonStudentGradesCustomArray.parseJSON(inputFile);
        JSONArray results1 = JsonStudentGradesCustomArray.flatMap1(results0);
        JSONArray results2 = JsonStudentGradesCustomArray.map1(results1);
        JSONArray results3 = JsonStudentGradesCustomArray.map2(results2);
        JSONArray results4 = JsonStudentGradesCustomArray.reduceByKey1(results3);
        JSONArray results5 = JsonStudentGradesCustomArray.filter1(results4);
    }
}
