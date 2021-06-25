package edu.ucla.cs.bigfuzz.customarray.applicable.JsonExternalUDF;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.customarray.applicable.JsonMovieRating.JsonMovieRatingCustomArray;
import edu.ucla.cs.bigfuzz.dataflow.MapEvent;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonExternalUDFCustomArray {
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
     * This map splits on "," which is not necessary for Json typed data.
     * @param lines Json values to be mapped
     * @return mapped lines
     */
    public static JSONArray map1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonExternalUDFCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        return lines;
    }

    /**
     * This filter filters out any case where the square of number 3 is smaller than or equal to
     * number 1 squared plus number 2 squared
     * @param lines Json values to be filtered
     * @return filtered lines
     */
    public static JSONArray filter1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonExternalUDFCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray filtered = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            long number1 = Math.toIntExact((Long) jsonObject.get("number1"));
            long number2 = Math.toIntExact((Long) jsonObject.get("number2"));
            long number3 = Math.toIntExact((Long) jsonObject.get("number3"));



            if ((number1^2 + number2^2) < (number3^2)) {
                filtered.add(jsonObject);
            }
        }

        return filtered;
    }
}
