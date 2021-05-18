package edu.bigfuzztabulardata;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.util.Random;

public class DataFormat {
    private String dataType;
    private String range;
    private String rangeType;
    private String[] specialValues;
    private final boolean defaultRange;

    public DataFormat(String dataType, String range, String rangeType, String[] specialValues, boolean defaultRange) {
        this.dataType = dataType;
        this.range = range;
        this.rangeType = rangeType;
        this.specialValues = specialValues;
        this.defaultRange = defaultRange;
    }

    /**
     * Uses the RgxGen library to generate a random input that lies within a range defined by a regular expression.
     * @return Random input within a range.
     */
    public String generateInputInRange() {
        String s = "";
        if (rangeType.equals("regex")) {
            s = generateRegexValue();
        } else {
            s = generateIntervalValue();
        }
        return s;
    }

    private String generateRegexValue() {
        RgxGen generator = new RgxGen(range);
        String s = generator.generate();
        if (defaultRange) {
            if(dataType.contains("byte") || dataType.contains("short") || dataType.contains("int") || dataType.contains("long")) {
                long decimal = Long.parseLong(s, 2);
                s = decimal + "";
            }
        }
        return s;
    }

    private String generateIntervalValue() {
        String[] bounds = range.split("-");
        long low = Long.parseLong(bounds[0]);
        long high = Long.parseLong(bounds[1]);
        return low + (long) (Math.random() * (high - low)) + "";
    }

    public String generateInputOutsideRange() { //TODO: still a lot of trouble with this
        String s = "";
        if (rangeType.equals("regex")) {
            s = generateRegexValueOutsideRange();
        } else {
            s = generateIntervalValueOutsideRange();
        }
        return s;
    }

    private String generateRegexValueOutsideRange() {
        RgxGen generator = new RgxGen(range);
        String s = generator.generateNotMatching(); //TODO: it now generates just any string
//        if (dataType.contains("int")) { //TODO: Doesnt work properly; Library's fault?; Look into the concept again later
//            RgxGen generator = new RgxGen("[^0-9]*|" + range);
//            s = generator.generateNotMatching();
//        }
        if (defaultRange) {
            return "TODO:DefaultRange";
        }
        return s;
    }

    private String generateIntervalValueOutsideRange() { //TODO: generate values close to the interval boundaries
        String[] bounds = range.split("-");
        long low = Long.parseLong(bounds[0]);
        long high = Long.parseLong(bounds[1]);

        if ((int) (Math.random() * 2) == 0) {
            return Integer.MIN_VALUE + (long) (Math.random() * (low - Integer.MIN_VALUE)) + "";
        }

        return high + (long) (Math.random() * (Integer.MAX_VALUE - high)) + "";
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

    /**
     * Return datatype as a readable string.
     * @return string representation of a datatype.
     */
    public String toString() {
        if (getSpecialValues() == null) {
            return "DataType: " + getDataType() + " | Range (" + rangeType + "): " + getRange();
        }
        String s = "";
        s += "DataType: " + getDataType() + " | Range (" + rangeType + "): " + getRange() + " | Special Values: " + "[";
        for (int i = 0; i < getSpecialValues().length; i++) {
            s+= getSpecialValues()[i] + ", ";
        }
        s = s.substring(0, s.length()-2);
        s += "]";

        return s;
    }

    public static String getArrayType(String dataType) {
        return dataType.substring(6, dataType.length() - 1);
    }

    public static String trimRangeString(String range) {
        String trimmedString = "";
        if (range.substring(0, 6).equals("regex(")) {
            trimmedString = range.substring(6, range.length() - 1);
        } else {
            trimmedString = range.substring(9, range.length() - 1);
        }
        return trimmedString;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String[] getSpecialValues() {
        return specialValues;
    }

    public void setSpecialValues(String[] specialValues) {
        this.specialValues = specialValues;
    }

    public String getRangeType() {
        return rangeType;
    }

    public boolean isDefaultRange() {
        return defaultRange;
    }
}
