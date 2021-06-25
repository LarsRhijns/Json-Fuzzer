import edu.ucla.cs.bigfuzz.customarray.applicable.JsonFindSalary.JsonFindSalaryCustomArray;
import edu.ucla.cs.bigfuzz.customarray.applicable.JsonProperty.JsonPropertyCustomArray;
import org.json.simple.JSONArray;

import java.io.IOException;

public class JsonFindSalary {
    public void JsonFindSalary(String inputFile) throws IOException {
        JSONArray results0 = JsonFindSalaryCustomArray.parseJSON(inputFile);
        JSONArray results1 = JsonFindSalaryCustomArray.map1(results0);
        JSONArray results2 = JsonFindSalaryCustomArray.map2(results1);
        JSONArray results3 = JsonFindSalaryCustomArray.filter1(results2);
        JSONArray results4 = JsonFindSalaryCustomArray.reduce1(results3);
    }
}
