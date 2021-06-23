import edu.ucla.cs.bigfuzz.customarray.applicable.JsonProperty.JsonPropertyCustomArray;
import org.json.simple.JSONArray;

import java.io.IOException;

public class JsonProperty {
    public void JsonProperty(String inputFile) throws IOException {
        JSONArray results0 = JsonPropertyCustomArray.parseJSON(inputFile);
        JSONArray results1 = JsonPropertyCustomArray.map1(results0);
        JSONArray results2 = JsonPropertyCustomArray.map2(results1);
    }
}
