package edu.ucla.cs.bigfuzz.customarray.applicable.JsonStudentGrades;

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
import java.util.Iterator;
import java.util.Set;

public class JsonStudentGradesCustomArray {
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
     * This flapMap implements a split on "," (Which is not needed for JSON values)
     * @param lines JSON values that need to be mapped
     * @return mapped lines
     */
    public static JSONArray flatMap1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonStudentGradesCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        // Since the lines are already mapped by property this map is unnecessary.
        return lines;
    }

    /**
     * This map implements a split on ":" (Which is not needed for JSON values)
     * @param lines JSON values that need to be mapped
     * @return mapped lines
     */
    public static JSONArray map1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonStudentGradesCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        // Since the lines are already mapped by property this map is unnecessary.
        return lines;
    }

    /**
     * This map converts each grade higher than 40 to ("Pass",1), else to ("Fail",1)
     * @param lines JSON values that need to be mapped
     * @return mapped lines
     */
    public static JSONArray map2(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonStudentGradesCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray mapped = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject map = new JSONObject();
            map.put("count", 1);
            String course = (String) jsonObject.get("course");

            if (Math.toIntExact((Long) jsonObject.get("grade")) > 40) {
                map.put("course", course + " Pass");
            } else {
                map.put("course", course + " Fail");
            }

            mapped.add(map);
        }

        return mapped;
    }

    /**
     * This reduceByKey reduces on the courses
     * @param lines JSON values that need to be reduced by key
     * @return reduced lines
     */
    public static JSONArray reduceByKey1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonStudentGradesCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray reducedLine = new JSONArray();
        JSONObject aggregateObject = new JSONObject();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            String course = (String) jsonObject.get("course");

            if (!aggregateObject.containsKey("course")) {
                JSONObject courseCount = new JSONObject();
                courseCount.put("course", course);
                courseCount.put("occurrences", jsonObject.get("count"));
                aggregateObject.put(course, courseCount);
            } else {
                JSONObject courseCount = (JSONObject) aggregateObject.get(course);
                int occurrences = (int) courseCount.get("occurrences");
                courseCount.remove("occurrences");
                courseCount.put("occurrences", occurrences + (int) jsonObject.get("count"));

                aggregateObject.remove(course);
                aggregateObject.put(course, courseCount);
            }
        }
        reducedLine.add(aggregateObject);
        return reducedLine;
    }

    /**
     * This filter filters out all courses that have less than 40 passed grades
     * @param lines JSON values to be filtered
     * @return filtered lines
     */
    public static JSONArray filter1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonStudentGradesCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray filtered = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                JSONObject courseCount = (JSONObject) jsonObject.get(key);
                if (Math.toIntExact((Integer) courseCount.get("occurrences")) > 40) {
                    filtered.add(jsonObject);
                }
            }
        }
        return filtered;
    }
}
