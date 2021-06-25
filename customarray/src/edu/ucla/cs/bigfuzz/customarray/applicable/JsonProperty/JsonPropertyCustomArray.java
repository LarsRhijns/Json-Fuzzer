package edu.ucla.cs.bigfuzz.customarray.applicable.JsonProperty;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.dataflow.MapEvent;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonPropertyCustomArray {
    public static JSONArray parseJSON(String inputFile) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader(inputFile);
            Object obj = parser.parse(reader);
            return (org.json.simple.JSONArray) obj;
        } catch (Exception e) {
            throw new IOException("File read/parse exception for JSON file.");
        }
    }

    /**
     * This map implements a split on "," (Which is not needed for JSON values)
     * @param lines JSON values that need to be mapped
     * @return mapped lines
     */
    public static JSONArray map1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonPropertyCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        // Since the lines are already mapped by property this map is unnecessary.
        return lines;
    }

    /**
     * This map ...
     * @param lines JSON values that need to be mapped
     * @return mapped lines
     */
    public static JSONArray map2(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonPropertyCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray mapped = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject mappedObject = new JSONObject();

            double a = (Double) jsonObject.get("worth");
            long s2 = Math.toIntExact((Long) jsonObject.get("years"));
            double s3 = (Double) jsonObject.get("interest");

            if (s2 > 1000000) {
                // This can happen after a mutate, if the integer is too big the loop will get stuck too long
                // Throw an exception to highlight this case
                throw new RuntimeException("Stuck too long in loop.");
            }

            for (int i = -1; i < s2; i++) {
                a *= (1+s3);
            }

            mappedObject.put("worth", a);
            mappedObject.put("years", s2);
            mappedObject.put("interest", s3);
            mappedObject.put("name", jsonObject.get("name"));
            mapped.add(mappedObject);
        }

        return mapped;
    }
}
