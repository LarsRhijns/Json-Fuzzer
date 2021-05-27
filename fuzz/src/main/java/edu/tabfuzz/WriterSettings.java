package edu.tabfuzz;

import com.opencsv.CSVWriter;

public class WriterSettings {

    private char separator;
    private char quoteChar;
    private char escapeChar;

    public WriterSettings () {
        this.separator = CSVWriter.DEFAULT_SEPARATOR;
        this.quoteChar = Character.MIN_VALUE;
        this.escapeChar = Character.MIN_VALUE;
    }

    public char getSeparator() {
        return separator;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public char getEscapeChar() {
        return escapeChar;
    }

    public String getLineEnd() {
        return CSVWriter.DEFAULT_LINE_END;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }

    public void setEscapeChar(char escapeChar) {
        this.escapeChar = escapeChar;
    }
}
