package edu.ucla.cs.bigfuzz.customarray.applicable.JsonFindSalary;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.dataflow.MapEvent;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonFindSalaryCustomArray {
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
     * This map removes the $ sign if it is present in the property of the object
     * @param lines Json values to be mapped
     * @return mapped lines
     */
    public static JSONArray map1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonFindSalaryCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray mapped = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject mappedObj = new JSONObject();

            String salary = (String) jsonObject.get("salary");

            if (salary.contains("$")) {
                salary = salary.replace("$", "");
            }

            mappedObj.put("salary", salary);
            mapped.add(mappedObj);
        }

        return mapped;
    }

    /**
     * This map makes the salary property an integer instead of a string
     * @param lines Json values to be mapped
     * @return mapped lines
     */
    public static JSONArray map2(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonFindSalaryCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray mapped = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject mappedObj = new JSONObject();

            long salary = Long.parseLong((String) jsonObject.get("salary"));

            mappedObj.put("salary", salary);
            mapped.add(mappedObj);
        }

        return mapped;
    }

    /**
     * This filter keeps only the objects with a salary below 300
     * @param lines Json values to be filtered
     * @return filtered lines
     */
    public static JSONArray filter1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonFindSalaryCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray filtered = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;

            long salary = Math.toIntExact((Long) jsonObject.get("salary"));

            if (salary < 300) {
                filtered.add(jsonObject);
            }
        }

        return filtered;
    }

    /**
     * This reduce sums all salaries together
     * @param lines Json values to be reduces
     * @return reduced lines
     */
    public static JSONArray reduce1(JSONArray lines) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();
        int iid = JsonFindSalaryCustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));

        JSONArray aggregate = new JSONArray();
        JSONObject sumObj = new JSONObject();
        sumObj.put("sum", 0);

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }
            JSONObject jsonObject = (JSONObject) obj;

            long salary = Math.toIntExact((Long) jsonObject.get("salary"));
            long sum = (long) sumObj.get("sum");

            sumObj.remove("sum");
            sumObj.put("sum", sum + salary);
        }

        aggregate.add(sumObj);
        return aggregate;
    }
}
