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

    //TODO: somehow make it return as the appropriate datatype; Xeger distribution seems off -> RgxGen seems way more random and evenly distributed

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
        String s = "";
        String[] bounds = range.split("-");
        long low = Long.parseLong(bounds[0]);
        long high = Long.parseLong(bounds[1]);
        return low + (long) (Math.random() * (high - low)) + "";
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
}
