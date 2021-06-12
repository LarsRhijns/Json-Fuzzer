package edu.ucla.cs.bigfuzz.customarray.applicable.JsonMovieRating;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.customarray.applicable.JsonSalary.JsonSalaryAnalysisCustomArray;
import edu.ucla.cs.bigfuzz.dataflow.MapEvent;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonMovieRatingCustomArray {
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
     * This map splits on ":", "," and "_", which is not necessary for Json typed data.
     * @param lines Json values to be mapped
     * @return mapped lines
     */
    public static JSONArray map1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonMovieRatingCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        return lines;
    }

    /**
     * This map takes the first rating and discards all the others
     * @param lines Json values to be mapped
     * @return mapped lines
     */
    public static JSONArray map2(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonMovieRatingCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray mapped = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;
            JSONObject newObj = new JSONObject();

            JSONArray ratings = (JSONArray) jsonObject.get("ratings");
            long rating = Math.toIntExact((Long) ratings.get(0));

            newObj.put("movietitle", jsonObject.get("movietitle"));
            newObj.put("rating", rating);

            mapped.add(newObj);
        }

        return mapped;
    }

    /**
     * This filter keeps only the movies which have a rating above 4
     * @param lines Json values to be filtered
     * @return filtered lines
     */
    public static JSONArray filter1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonMovieRatingCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray filtered = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;
            if (Math.toIntExact((Long) jsonObject.get("rating")) > 4) {
                filtered.add(jsonObject);
            }
        }

        return filtered;
    }

    /**
     * This reduceByKey sums the ratings of the same movies
     * @param lines Json values to be reduced by key
     * @return reduced lines
     */
    public static JSONArray reduceByKey1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonMovieRatingCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray reduced = new JSONArray();
        JSONObject aggregateObject = new JSONObject();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            String movietitle = (String) jsonObject.get("movietitle");

            if (!aggregateObject.containsKey(movietitle)) {
                long rating = Math.toIntExact((Long) jsonObject.get("rating"));
                jsonObject.remove("rating");
                jsonObject.put("ratingSum", rating);
                aggregateObject.put(movietitle, jsonObject);
            } else {
                JSONObject agg = (JSONObject) aggregateObject.get(movietitle);
                aggregateObject.remove(movietitle);
                long ratingSum = (long) agg.get("ratingSum") + Math.toIntExact((Long) jsonObject.get("rating"));
                agg.remove("ratingSum");
                agg.put("ratingSum", ratingSum);
                aggregateObject.put(movietitle, agg);
            }
        }
        reduced.add(aggregateObject);
        return reduced;
    }
}
