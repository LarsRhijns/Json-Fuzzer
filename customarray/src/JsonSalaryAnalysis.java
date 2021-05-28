import edu.ucla.cs.bigfuzz.customarray.CustomArray;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.mortbay.util.ajax.JSON;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JsonSalaryAnalysis {

    public void JsonSalaryAnalysis(String inputFile) throws IOException {
        File file=new File(inputFile);
        ArrayList<String> list;
        if(file.exists()) {
            list = CustomArray.read(inputFile);
        } else {
            System.out.println("File does not exist!");
            return;
        }

        JSONArray jsonArray = parseJSON(inputFile);
        JSONArray results1 = map1(jsonArray);
        JSONArray results2 = filter1(results1, 90024);
        JSONArray results3 = map2(results2);
        JSONArray results4 = mapValues1(results3);
        JSONArray results5 = reduceByKey1(results4);
        JSONArray results6 = mapValues2(results5);
    }

    private JSONArray parseJSON(String inputFile) throws IOException {
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
    public JSONArray map1(JSONArray lines) {
        // Since the lines are already mapped by property this map is unnecessary.
        return lines;
    }

    /**
     * This filter implements a filter on the zipcodes that match 90024
     * @param lines JSON values that need to be filtered
     * @return filtered lines (90024, Age, Income)
     */
    public JSONArray filter1(JSONArray lines, int zipcode) {
        JSONArray filtered = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            if ((long) jsonObject.get("zipcode") == zipcode) {
                filtered.add(jsonObject);
            }
        }
        return filtered;
    }

    /**
     * This map implements a map from any age to its specific range (being: 0-19, 20-39, 40-65 or >65).
     * These ranges will be the new value for the age value.
     * @param lines JSON values that need to be mapped
     * @return mapped lines (AgeRange, Income)
     */
    public JSONArray map2(JSONArray lines) {
        JSONArray mappedLines = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;
            long age = (long) jsonObject.get("age"); // TODO Age should be a string value
            jsonObject.remove("age");

            if (age < 20) {
                jsonObject.put("age", "0-19");
            } else if (age >= 20 && age < 40) {
                jsonObject.put("age", "20-39");
            } else if (age >= 40 && age <= 65) {
                jsonObject.put("age", "40-65");
            } else {
                jsonObject.put("age", ">65");
            }
            mappedLines.add(jsonObject);
        }
        return mappedLines;
    }

    /** This mapValue implements a mapValue that adds a 1 to each JSON value
     * @param lines JSON values that need to be mapped with a value
     * @return value mapped lines (AgeRange, (Income, 1))
     */
    public JSONArray mapValues1(JSONArray lines) {
        JSONArray mappedLines = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.put("count", 1);
            mappedLines.add(jsonObject);
        }
        return mappedLines;
    }

    /** This reduceByKey implements a reduce that sums all the incomes together in a (AgeRange, IncomeSum)
     * pair and this is paired with the amount of occurrences of that age range.
     * @param lines JSON values that need to be reduced by key
     * @return reduced lines (AgeRange, (IncomeSum, Occurrences))
     */
    public JSONArray reduceByKey1(JSONArray lines) {
        JSONArray reducedLine = new JSONArray();
        JSONObject aggregateObject = new JSONObject();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            String ageRange = (String) jsonObject.get("age");
            if (!aggregateObject.containsKey("ageRange")) {
                // ageRange property is not yet in this aggregate, thus add it as a JSON Object
                JSONObject range = new JSONObject();
                range.put("incomeSum", jsonObject.get("salary"));
                range.put("occurrences", jsonObject.get("count"));
                aggregateObject.put(ageRange, range);
            } else {
                // If ageRange is already in the aggregate, then update its values
                JSONObject range = (JSONObject) aggregateObject.get(ageRange);

                long incomeSum = (long) range.get("incomeSum");
                range.remove("incomeSum");
                range.put("incomeSum", (long) jsonObject.get("salary") + incomeSum);

                int occurrences = (int) range.get("occurrences");
                range.remove("occurrences");
                range.put("occurrences", (int) jsonObject.get("count") + occurrences);
            }
        }
        reducedLine.add(aggregateObject);
        return reducedLine;
    }

    /**
     * This mapValue implements a mapValue that takes
     * @param lines JSON values that need to be reduced by key
     * @return value mapped lines (AgeRange, (Occurrences, Average))
     */
    public JSONArray mapValues2(JSONArray lines) {
        JSONArray mappedLines = new JSONArray();

        for (Object obj : lines) {
            if (!(obj instanceof JSONObject)) {
                throw new IllegalStateException("Input row is not a JSON Object, please check input:" + obj.toString());
            }

            JSONObject jsonObject = (JSONObject) obj;

            for (Object k : jsonObject.keySet()) {
                String key = (String) k;
                JSONObject aggr = (JSONObject) jsonObject.get(key);
                JSONObject newAggr = new JSONObject();
                long incomeSum = (long) aggr.get("incomeSum");
                int occurences = (int) aggr.get("occurrences");
                newAggr.put("occurences", occurences);
                newAggr.put("averageIncome", ((double) incomeSum) / ((double) occurences));
                jsonObject.remove(key);
                jsonObject.put(key, newAggr);
            }

            mappedLines.add(jsonObject);
        }

        return mappedLines;
    }
}
