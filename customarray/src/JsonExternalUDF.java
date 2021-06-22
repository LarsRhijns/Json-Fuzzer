import edu.ucla.cs.bigfuzz.customarray.applicable.JsonExternalUDF.JsonExternalUDFCustomArray;
import org.json.simple.JSONArray;

import java.io.IOException;

public class JsonExternalUDF {
    public void JsonExternalUDF(String inputFile) throws IOException {
        JSONArray results0 = JsonExternalUDFCustomArray.parseJSON(inputFile);
        JSONArray results1 = JsonExternalUDFCustomArray.map1(results0);
        JSONArray results2 = JsonExternalUDFCustomArray.filter1(results1);
    }
}
