package edu.bigfuzztabulardata;

import com.github.curiousoddman.rgxgen.RgxGen;

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
        if (dataType.equals("String") || dataType.equals("char")) {
            String[] result = new String[1];
            result[0] = range;
            return result;
        }
        String[] result = range.split(",");
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }

    private String[] processSpecialValues(String specialValues) {
        String[] result = specialValues.split(",");
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }
    /**
     * Uses the RgxGen library to generate a random input that lies within a range defined by a regular expression.
     * @return Random input within a range.
     */
    public String generateInputInRange() {
        String s = "";
        if (dataType.equals("String") || dataType.equals("char")) {
            RgxGen generator = new RgxGen(range[0]);
            s = generator.generate();
        } else if (dataType.equals("boolean")) {
            int boolSelection = (int) (Math.random() * 2);
            if (boolSelection == 0) {
                return "True";
            }
            return "False";
        } else if (dataType.contains("array(")) {
            generateArrayInputInRange(5); //TODO: What should array size be?
        }
        else {
            s = generateIntervalValue();
        }
        return s;
    }

    private String generateIntervalValue() {
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

    private String generateByteValue(String interval) {
        if (interval.equals("")) {
            return Byte.MIN_VALUE + (byte) (Math.random() * (Byte.MAX_VALUE - Byte.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Byte.parseByte(bounds[0]);
            high = Byte.parseByte(bounds[1]);
        }
        return low + (byte) (Math.random() * (high - low)) + "";
    }

    private String generateShortValue(String interval) {
        if (interval.equals("")) {
            return Short.MIN_VALUE + (short) (Math.random() * (Short.MAX_VALUE - Short.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Short.parseShort(bounds[0]);
            high = Short.parseShort(bounds[1]);
        }
        return low + (short) (Math.random() * (high - low)) + "";
    }

    private String generateIntValue(String interval) {
        if (interval.equals("")) {
            return Integer.MIN_VALUE + (int) (Math.random() * (Integer.MAX_VALUE - Integer.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Integer.parseInt(bounds[0]);
            high = Integer.parseInt(bounds[1]);
        }
        return low + (int) (Math.random() * (high - low)) + "";
    }

    private String generateLongValue(String interval) {
        if (interval.equals("")) {
            return Long.MIN_VALUE + (long) (Math.random() * (Long.MAX_VALUE - Long.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Long.parseLong(bounds[0]);
            high = Long.parseLong(bounds[1]);
        }
        return low + (long) (Math.random() * (high - low)) + "";
    }

    private String generateFloatValue(String interval) {
        if (interval.equals("")) {
            return Float.MIN_VALUE + (Math.random() * (Float.MAX_VALUE - Float.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Float.parseFloat(bounds[0]);
            high = Float.parseFloat(bounds[1]);
        }
        return low + (Math.random() * (high - low)) + "";
    }

    private String generateDoubleValue(String interval) {
        if (interval.equals("")) {
            return Double.MIN_VALUE + (double) (Math.random() * (Double.MAX_VALUE - Double.MIN_VALUE)) + "";
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
            String[] bounds = interval.split("-");
            low = Double.parseDouble(bounds[0]);
            high = Double.parseDouble(bounds[1]);
        }
        return low + (Math.random() * (high - low)) + "";
    }

//    public String generateInputOutsideRange() { //TODO: still a lot of trouble with this
//        String s = "";
//        if (rangeType.equals("regex")) {
//            s = generateRegexValueOutsideRange();
//        } else {
//            s = generateIntervalValueOutsideRange();
//        }
//        return s;
//    }

//    private String generateRegexValueOutsideRange() {
//        RgxGen generator = new RgxGen(range);
//        String s = generator.generateNotMatching(); //TODO: it now generates just any string -> type errors instead of range errors
////        if (dataType.contains("int")) {
////            RgxGen generator = new RgxGen("[^0-9]*|" + range);
////            s = generator.generateNotMatching();
////        }
//        //TODO: Doesnt work properly; Library's fault?; Look into the concept again later
//        if (defaultRange) {
//            return "TODO:DefaultRange";
//        }
//        return s;
//    }

//    private String generateIntervalValueOutsideRange() { //TODO: generate values close to the interval boundaries
//        String[] bounds = range.split("-");
//        long low = Long.parseLong(bounds[0]);
//        long high = Long.parseLong(bounds[1]);
//
//        if ((int) (Math.random() * 2) == 0) {
//            return Integer.MIN_VALUE + (long) (Math.random() * (low - Integer.MIN_VALUE)) + "";
//        }
//
//        return high + (long) (Math.random() * (Integer.MAX_VALUE - high)) + "";
//    }

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

        String rangeString = "[";
        for (int i = 0; i < range.length; i++) {
            rangeString += range[i] + ", ";
        }
        rangeString = rangeString.substring(0, rangeString.length()-2);
        rangeString += "]";

        String specialValuesString = "[";
        for (int i = 0; i < specialValues.length; i++) {
            specialValuesString += specialValues[i] + ", ";
        }
        specialValuesString = specialValuesString.substring(0, specialValuesString.length()-2);
        specialValuesString += "]";

        if (getSpecialValues() == null) {
            return "DataType: " + getDataType() + " | Range: " + getRange();
        }

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
