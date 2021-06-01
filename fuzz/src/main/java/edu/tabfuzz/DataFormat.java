package edu.tabfuzz;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.util.*;

public class DataFormat {
    private String columnName;
    private String dataType;
    private String[] range;
    private String[] specialValues;

    public DataFormat(String columnName, String dataType, String range, String specialValues) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.range = processRange(dataType, range);
        this.specialValues = processSpecialValues(specialValues);
    }

    private String[] processRange(String dataType, String range) {
        if (dataType.equals("String")) {
            String[] result = new String[1];
            if (range.equals("")) {
                range = ".*";
            }
            result[0] = range;
            return result;
        }
        if (dataType.equals("char")) {
            String[] result = new String[1];
            if (range.equals("")) {
                range = ".";
            }
            result[0] = range;
            return result;
        }
        //TODO: Cleanup this method
        String[] result = range.split(",");
        ArrayList<String> result1 = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            if (!result[i].trim().equals("")) {
                result1.add(result[i].trim());
            }
        }
        String[] result2 = new String[result1.size()];
        return result1.toArray(result2);
    }

    private String[] processSpecialValues(String specialValues) {
        //TODO: Cleanup this method
        String[] result = specialValues.split(",");
        ArrayList<String> result1 = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            if (!result[i].trim().equals("")) {
                result1.add(result[i].trim());
            }
        }
        String[] result2 = new String[result1.size()];
        return result1.toArray(result2);
    }
    /**
     * Uses the RgxGen library to generate a random input that lies within a range defined by a regular expression.
     * @return Random input within a range.
     */
    public String generateInputInRange() {
        String s = "";
        if (dataType.equals("String") || dataType.equals("char")) {
            RgxGen generator;
            if (range[0].trim().equals("")) {
                if (dataType.equals("String")) {
                    generator = new RgxGen(".*");
                } else {
                    generator = new RgxGen(".");
                }
            } else {
                generator = new RgxGen(range[0]);
            }
            s = generator.generate();
        } else if (dataType.equals("boolean")) {
            int boolSelection = (int) (Math.random() * 2);
            if (boolSelection == 0) {
                return "true";
            }
            return "false";
        } else if (dataType.contains("array(")) {
            generateArrayInputInRange(5); //TODO: What should array size be?
        }
        else {
            s = generateIntervalValue(range);
        }
        return s;
    }

    private String generateIntervalValue(String[] range) {
        String interval = "";
        if (range.length != 0) {
            int rangeSelection = (int) (Math.random() * range.length);
            interval = range[rangeSelection];
        }
        String s  = "";
        switch(dataType) {
            case "byte":
                s = generateByteValue(interval);
                break;
            case "short":
                s = generateShortValue(interval);
                break;
            case "int":
                s = generateIntValue(interval);
                break;
            case "long":
                s = generateLongValue(interval);
                break;
            case "float":
                s = generateFloatValue(interval);
                break;
            case "double":
                s = generateDoubleValue(interval);
                break;
        }
        return s;
    }

        public String generateInputOutsideRange() {
        String s = "";
            switch (dataType) {
                case "String":
                    if (range[0].equals("")) {
                        return generateInputInRange();
                    }
                    RgxGen generator = new RgxGen(range[0]);
                    s = generator.generateNotMatching();
                    break;
                case "char":
                    return "";
                //TODO: generate char outside regex
                case "boolean":
                    // In case of boolean just generate either true or false;
                    int boolSelection = (int) (Math.random() * 2);
                    if (boolSelection == 0) {
                        return "True";
                    }
                    return "False";
                default:
                    if (range.length == 0) {
                        generateIntervalValue(range);
                    }
                    s = generateIntervalValue(findReverseIntervals());
                    //TODO: Default range
                    break;
            }
        return s;
    }

    Comparator<String> c = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            if (s1.contains(">") || s2.contains("<")) {
                return 1;
            } else if (s1.contains("<") || s2.contains(">") ) {
                return -1;
            } else {
                String[] s1split = s1.split("#");
                String[] s2split = s2.split("#");
                if (Double.parseDouble(s1split[0]) > Double.parseDouble(s2split[0])) {
                    return 1;
                }
                return -1;
            }
        }
    };


    public String[] findReverseIntervals() {
        String[] sortedRanges = range;
        Arrays.sort(sortedRanges, c);
        ArrayList<String> result = new ArrayList<>();
        switch(dataType) {
            case "byte":
                result = constructReverseIntervals(Byte.MIN_VALUE, Byte.MAX_VALUE, sortedRanges);
                break;
            case "short":
                result = constructReverseIntervals(Short.MIN_VALUE, Short.MAX_VALUE, sortedRanges);
                break;
            case "int":
                result = constructReverseIntervals(Integer.MIN_VALUE, Integer.MAX_VALUE, sortedRanges);
                break;
            case "long":
                result = constructReverseIntervals(Long.MIN_VALUE, Long.MAX_VALUE, sortedRanges);
                break;
            case "float":
                result = constructReverseIntervals(Float.MIN_VALUE, Float.MAX_VALUE, sortedRanges);
                break;
            case "double":
                result = constructReverseIntervals(Double.MIN_VALUE, Double.MAX_VALUE, sortedRanges);
                break;
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
        String[] res = new String[result.size()];
        return result.toArray(res);
    }

    private ArrayList<String> constructReverseIntervals(double minValue, double maxValue, String[] sortedRanges) {
        // Format the data to all intervals:
        for (int i = 0; i < sortedRanges.length; i++) {
            if (sortedRanges[i].contains("<")) {
                sortedRanges[i] = minValue + "#" + sortedRanges[i].substring(1);
            }
            if (sortedRanges[i].contains(">")) {
                sortedRanges[i] = sortedRanges[i].substring(1) + "#" + maxValue;
            }
        }
        // Construct the reverse intervals:
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < sortedRanges.length-1; i++) { //TODO: Doesnt allow overlapping ranges
            String[] s1split = sortedRanges[i].split("#");
            double s1low = Double.parseDouble(s1split[0]);
            double s1high = Double.parseDouble(s1split[1]);
            String[] s2split = sortedRanges[i+1].split("#");
            double s2low = Double.parseDouble(s2split[0]);
            double s2high = Double.parseDouble(s2split[1]);

            if (i == 0 && s1low != minValue) {
                double val = s1low - 1;
                result.add(minValue + "#" + val);
            }
            if (i == sortedRanges.length - 1 && s2high != maxValue) {
                double val = s2high + 1;
                result.add(val + "#" + maxValue);
            }
            double val1 = s1high + 1;
            double val2 = s2low - 1;
            result.add(val1 + "#" + val2);
        }

         return result;
    }


    public String changeDataType(String element) {
        //TODO: There could be more variation in the way the data type is changed
        String s = "";
        switch (dataType) {
            case "byte":
                // Relevant data type mutation: Too high, float/double, String
                s = changeDataTypeByte(element);
                break;
            case "short":
                // Relevant data type mutation: Too high, float/double, String
                s = changeDataTypeShort(element);
                break;
            case "int":
                s = changeDataTypeInt(element);
                // Relevant data type mutation: Too high, float/double, String
                break;
            case "long":
                // Relevant data type mutation: float/double, String
                s = changeDataTypeLong(element);
                break;
            case "float":
                // Relevant data type mutation: Too high, String
                s = changeDataTypeFloat(element);
                break;
            case "double":
                // Relevant data type mutation: String
                s = changeDataTypeDouble(element);
                break;
            case "boolean":
                // Relevant data type mutation: Anything that is not True/False
                s = changeDataTypeBoolean(element);
                break;
            case "char":
                // Relevant data type mutation: Too long
                s = changeDataTypeChar(element);
                break;
            case "String":
                // Relevant data type mutation: None
                s = changeDataTypeString(element);
                break;
            case "Array":
                // Relevant data type mutation: changetype of a random element?/ all elements?, make the array itself not an array?
                s = changeDataTypeArray(element);
        }
        return s;
    }

    public String changeDataTypeByte(String element) {
        String s = "";
        int r = (int) (Math.random() * 3);
        switch (r) {
            case 0:
                s = element + ".0";
                break;
            case 1:
                s = element + "q";
                break;
            case 2:
                short temp = (short) Byte.MAX_VALUE + 1;
                s = temp + "";
                break;
        }
        return s;
    }

    public String changeDataTypeShort(String element) {
        String s = "";
        int r = (int) (Math.random() * 3);
        switch (r) {
            case 0:
                s = element + ".0";
                break;
            case 1:
                s = element + "q";
                break;
            case 2:
                int temp = (int) Short.MAX_VALUE + 1;
                s = temp + "";
                break;
        }
        return s;
    }

    public String changeDataTypeInt(String element) {
        String s = "";
        int r = (int) (Math.random() * 3);
        switch (r) {
            case 0:
                s = element + ".0";
                break;
            case 1:
                s = element + "q";
                break;
            case 2:
                long temp = (long) Integer.MAX_VALUE + 1;
                s = temp + "";
                break;
        }
        return s;
    }

    public String changeDataTypeLong(String element) {
        String s = "";
        int r = (int) (Math.random() * 2);
        switch (r) {
            case 0:
                s = element + ".0";
                break;
            case 1:
                s = element + "q";
                break;
        }
        return s;
    }

    public String changeDataTypeFloat(String element) {
        String s = "";
        int r = (int) (Math.random() * 2);
        switch (r) {
            case 0:
                s = element + "q";
                break;
            case 1:
                double temp = Float.MAX_VALUE + 1;
                s = temp + "";
                break;
        }
        return s;
    }

    public String changeDataTypeDouble(String element) {
        return element + "q";
    }

    public String changeDataTypeBoolean(String element) {
        return element + "q";
    }

    public String changeDataTypeChar(String element) {
        return element + "q";
    }

    public String changeDataTypeString(String element) {
        //TODO: Not relevant for Strings (every datatype is also a valid string), Dont do this mutation on strings?
        return element;
    }

    public String changeDataTypeArray(String element) {
        // TODO: Implement this
        return element;
    }

    /**
     * Generates a String representation of an array of the corresponding dataType.
     * @param arraySize size of the array.
     * @return String representation of an input array.
     */
    public String generateArrayInputInRange(int arraySize) {
        String array = "[";
        for (int i = 0; i < arraySize; i++) {
            array += generateInputInRange() + ", ";
        }
        array = array.substring(0, array.length() - 2);
        array += "]";

        return array;
    }

    private String generateByteValue(String interval) {
        if (interval.equals("")) {
            Random r = new Random();
            return r.nextInt(Byte.MAX_VALUE + 1) + "";
        }
        byte low;
        byte high;
        if (interval.contains(">")) {
            low = Byte.parseByte(interval.substring(1));
            high = Byte.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Byte.MIN_VALUE;
            high = Byte.parseByte(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Byte.parseByte(bounds[0]);
            high = Byte.parseByte(bounds[1]);
        }
        return low + (byte) (Math.random() * (high - low)) + "";
    }

    private String generateShortValue(String interval) {
        if (interval.equals("")) {
            Random r = new Random();
            return r.nextInt(Short.MAX_VALUE + 1) + "";
        }
        short low;
        short high;
        if (interval.contains(">")) {
            low = Short.parseShort(interval.substring(1));
            high = Short.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Short.MIN_VALUE;
            high = Short.parseShort(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Short.parseShort(bounds[0]);
            high = Short.parseShort(bounds[1]);
        }
        return low + (short) (Math.random() * (high - low)) + "";
    }

    private String generateIntValue(String interval) {
        if (interval.equals("")) {
            Random r = new Random();
            return r.nextInt() + "";
        }
        int low;
        int high;
        if (interval.contains(">")) {
            low = Integer.parseInt(interval.substring(1));
            high = Integer.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Integer.MIN_VALUE;
            high = Integer.parseInt(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Integer.parseInt(bounds[0]);
            high = Integer.parseInt(bounds[1]);
        }
        return low + (int) (Math.random() * (high - low)) + "";
    }

    private String generateLongValue(String interval) {
        if (interval.equals("")) {
            Random r = new Random();
            return r.nextLong() + "";
        }
        long low;
        long high;
        if (interval.contains(">")) {
            low = Long.parseLong(interval.substring(1));
            high = Long.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Long.MIN_VALUE;
            high = Long.parseLong(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Long.parseLong(bounds[0]);
            high = Long.parseLong(bounds[1]);
        }
        return low + (long) (Math.random() * (high - low)) + "";
    }

    private String generateFloatValue(String interval) { //TODO: Check what the string representation should be
        if (interval.equals("")) {
            Random r = new Random();
            int signSelection = (int) (Math.random() * 2);
            if (signSelection == 0) {
                return r.nextFloat() * Float.MAX_VALUE + "";
            }
            return "-" + r.nextFloat() * Float.MAX_VALUE + "";
        }
        float low;
        float high;
        if (interval.contains(">")) {
            low = Float.parseFloat(interval.substring(1));
            high = Float.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Float.MIN_VALUE;
            high = Float.parseFloat(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Float.parseFloat(bounds[0]);
            high = Float.parseFloat(bounds[1]);
        }
        return low + (Math.random() * (high - low)) + "";
    }

    private String generateDoubleValue(String interval) { //TODO: Check what the string representation should be
        if (interval.equals("")) {
            Random r = new Random();
            int signSelection = (int) (Math.random() * 2);
            if (signSelection == 0) {
                return r.nextDouble() * Double.MAX_VALUE + "";
            }
            return "-" + r.nextDouble() * Double.MAX_VALUE + "";
        }
        double low;
        double high;
        if (interval.contains(">")) {
            low = Double.parseDouble(interval.substring(1));
            high = Double.MAX_VALUE;
        } else if (interval.contains("<")) {
            low = Double.MIN_VALUE;
            high = Double.parseDouble(interval.substring(1));
        } else {
            String[] bounds = interval.split("#");
            low = Double.parseDouble(bounds[0]);
            high = Double.parseDouble(bounds[1]);
        }
        return low + (Math.random() * (high - low)) + "";
    }

    /**
     * Return datatype as a readable string.
     * @return string representation of a datatype.
     */
    public String toString() {

        String rangeString = "[";
        if (getRange().length != 0) {
            for (int i = 0; i < range.length; i++) {
                rangeString += range[i] + ", ";
            }
            rangeString = rangeString.substring(0, rangeString.length() - 2);
        }
        rangeString += "]";


        String specialValuesString = "[";
        if (getSpecialValues().length != 0) {
            for (int i = 0; i < specialValues.length; i++) {
                specialValuesString += specialValues[i] + ", ";
            }
            specialValuesString = specialValuesString.substring(0, specialValuesString.length()-2);
        }
        specialValuesString += "]";


        return "ColumnName: " + columnName + " | DataType: " + dataType + " | Range: " + rangeString + " | Special Values: " + specialValuesString;
    }

    public static String getArrayType(String dataType) {
        return dataType.substring(6, dataType.length() - 1);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String[] getRange() {
        return range;
    }

    public void setRange(String[] range) {
        this.range = range;
    }

    public String[] getSpecialValues() {
        return specialValues;
    }

    public void setSpecialValues(String[] specialValues) {
        this.specialValues = specialValues;
    }

}
