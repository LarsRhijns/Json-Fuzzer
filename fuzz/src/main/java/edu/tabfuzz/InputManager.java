package edu.tabfuzz;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InputManager {

    private final DataFormat[] inputs;
    private final WriterSettings ws;

    /**
     * TODO: Easy tabular to CSV converter that automatically uses the proper input format, with a wide range of options to provide the specification
     * Takes an CSV file of input specification and transforms it into an array of DataFormats.
     * Expects per cell an input type (String) and optionally an input range (Regex) and interesting values (Regex) (. in case of empty)
     * White space is delimiter
     * @param file CSV file of input specification.
     */
    public InputManager(File file, WriterSettings ws) {
        this.ws = ws;
        this.inputs = readInputs(file);
    }

    private DataFormat[] readInputs(File file) {
        ArrayList<DataFormat> dataFormatArrayList  = new ArrayList<>();
        try {
            Scanner s = new Scanner(file);
            String columnName = "";
            String datatype = "";
            String range = "";
            String specialValues = "";
            while (s.hasNextLine()) {
                String nl = s.nextLine();
                // Find writersettings:
                if (nl.contains("Separator: ") || nl.contains("QuoteChar: ") || nl.contains("EscapeChar: ")) {
                    char separator = CSVWriter.DEFAULT_SEPARATOR;
                    char quoteChar = Character.MIN_VALUE;
                    char escapeChar = Character.MIN_VALUE;

                    for (int i = 0; i < 5; i++) {
                        if (nl.trim().equals("")) {
                            ws.setEscapeChar(escapeChar);
                            ws.setQuoteChar(quoteChar);
                            ws.setSeparator(separator);
                            break;
                        } else {
                            String[] split = nl.split(": ", 2);
                            switch (split[0]) {
                                case "Separator":
                                    if (split[1].length() == 3) {
                                        separator = split[1].charAt(1);
                                    }
                                    break;
                                case "QuoteChar":
                                    if (split[1].length() == 3) {
                                        quoteChar = split[1].charAt(1);
                                    }
                                    break;
                                case "EscapeChar":
                                    if (split[1].length() == 3) {
                                        escapeChar = split[1].charAt(1);
                                    }
                                    break;
                            }
                        }
                        nl = s.nextLine();
                    }
                }

                // Read in the data columns:
                if (nl.trim().equals("")) {
                    if (!datatype.equals("")) {
                        DataFormat df = new DataFormat(columnName, datatype, range, specialValues);
                        dataFormatArrayList.add(df);
                    } else {
                        System.err.println("Specification without datatype has been ignored. (Could be empty lines)");
                    }
                    columnName = "";
                    datatype = "";
                    range = "";
                    specialValues = "";
                } else {
                    String[] split = nl.split(": ", 2);
                    switch (split[0]) {
                        case "Column":
                            columnName = split[1];
                            break;
                        case "Datatype":
                            datatype = split[1];
                            break;
                        case "Range":
                            range = split[1];
                            break;
                        case "Special":
                            specialValues = split[1];
                            break;
                    }
                }
            }
            if (!datatype.equals("")) {
                DataFormat df = new DataFormat(columnName, datatype, range, specialValues);
                dataFormatArrayList.add(df);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DataFormat[] dfar = new DataFormat[dataFormatArrayList.size()];
        return dataFormatArrayList.toArray(dfar);
    }

    /**
     * Return the inputs array.
     * @return DataFormat array of the inputs.
     */
    public DataFormat[] getInputs() {
        return inputs;
    }

    /**
     * Return a visual representation of the inputs array.
     * @return DataFormat array of inputs as a String.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (DataFormat input : inputs) {
            s.append(input.toString()).append("\n");
        }
        return s.toString();
    }
}
