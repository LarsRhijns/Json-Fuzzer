package edu.bigfuzztabulardata;

import nl.flotsam.xeger.Xeger;

public class DataFormat {
    private String dataType;
    private String range;
    private String[] specialValues;

    public DataFormat(String dataType, String range, String[] specialValues) {
        this.dataType = dataType;
        this.range = range;
        this.specialValues = specialValues;
    }

    //TODO: somehow make it return as the appropriate datatype; Xeger distribution seems off (often small numbers)

    /**
     * Uses the Xeger library to generate a random input that lies within a range defined by a regular expression.
     * @return Random input within a range.
     */
    public String generateInputInRange() {
        String s = "";
        Xeger generator = new Xeger(range);
        s = generator.generate();
        return s;
    }

    /**
     * Return datatype as a readable string.
     * @return string representation of a datatype.
     */
    public String toString() {
        if (getSpecialValues() == null) {
            return "DataType: " + getDataType() + " | Range: " + getRange();
        }
        String s = "";
        s += "DataType: " + getDataType() + " | Range: " + getRange() + " | Special Values: " + "[";
        for (int i = 0; i < getSpecialValues().length; i++) {
            s+= getSpecialValues()[i] + ", ";
        }
        s = s.substring(0, s.length()-2);
        s += "]";

        return s;
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
