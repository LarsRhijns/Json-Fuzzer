package edu.tabfuzz;

import com.opencsv.CSVWriter;

public class WriterSettings {

    private final char separator;
    private final char quoteChar;
    private final char escapeChar;
    private final String lineEnd;

    public WriterSettings () {
        this.separator = CSVWriter.DEFAULT_SEPARATOR;
        this.quoteChar = Character.MIN_VALUE;
        this.escapeChar = Character.MIN_VALUE;
        lineEnd = CSVWriter.DEFAULT_LINE_END;

    }

    public WriterSettings (char separator, char quoteChar, char escapeChar, String lineEnd) {
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.lineEnd = lineEnd;
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
        return lineEnd;
    }
}
