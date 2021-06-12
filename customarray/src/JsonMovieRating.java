import edu.ucla.cs.bigfuzz.customarray.applicable.JsonMovieRating.JsonMovieRatingCustomArray;
import org.json.simple.JSONArray;

import java.io.IOException;

public class JsonMovieRating {
    public void JsonMovieRating(String inputFile) throws IOException {
        JSONArray results0 = JsonMovieRatingCustomArray.parseJSON(inputFile);
        JSONArray results1 = JsonMovieRatingCustomArray.map1(results0);
        JSONArray results2 = JsonMovieRatingCustomArray.map2(results1);
        JSONArray results3 = JsonMovieRatingCustomArray.filter1(results2);
        JSONArray results4 = JsonMovieRatingCustomArray.reduceByKey1(results3);
    }
}
