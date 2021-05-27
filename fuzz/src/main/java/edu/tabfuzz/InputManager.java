package edu.tabfuzz;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InputManager {

    private DataFormat[] inputs;
    private WriterSettings ws;

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
                    char separator = CSVWriter.DEFAULT_SEPARATOR;;
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

//    /**
//     * Reads a CSV file of input specifications and returns them as an array.
//     * @param file CSV file of input specification.
//     * @return array of input specification.
//     */
//    private DataFormat[] readInputs2(File file) {
//        String[] inputs = null;
//        try {
//            CSVReader reader = new CSVReader(new FileReader(file));
//            inputs = reader.readNext();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return processInputs2(inputs);
//    }
//
//    /**
//     * Takes an array of inputs and returns them as DataFormats
//     * @param inputs array of inputs.
//     * @return array of DataFormats.
//     */
//    private DataFormat[] processInputs2(String[] inputs) {
//        DataFormat[] processedInputs = new DataFormat[inputs.length];
//        for(int i = 0; i < inputs.length; i++) {
//            try {
//                String input = inputs[i];
//                String[] splitInputs = input.split(" ");
//                if (splitInputs.length > 3) {
//                    throw new Exception("Input file contains invalid whitespaces.");
//                }
//                String[] specialValues = null;
//                if (splitInputs.length == 3) {
//                    specialValues = splitInputs[2].split(",");
//                }
//                DataFormat df;
//                if (splitInputs[1].equals("regex(.*)")) {
//                    String dataType = splitInputs[0];
//                    if (splitInputs[0].contains("array")) {
//                        dataType = DataFormat.getArrayType(splitInputs[0]);
//                        System.out.println("Array type found: " + dataType); //TODO: remove prints
//                    }
//                    df = new DataFormat(splitInputs[0], getDefaultRange(dataType), "regex", specialValues, true);
//                } else if (splitInputs[1].substring(0, 6).equals("regex(")){
//                    df = new DataFormat(splitInputs[0], DataFormat.trimRangeString(splitInputs[1]), "regex", specialValues, false);
//                } else {
//                    df = new DataFormat(splitInputs[0], DataFormat.trimRangeString(splitInputs[1]), "interval", specialValues, false);
//                }
//                processedInputs[i] = df;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return processedInputs;
//    }

//    /**
//     * In case there is no specification for the range (or .*), return a regex that represents all possible values of the corresponding datatype.
//     * @param dataType
//     * @return
//     */
//    public String getDefaultRange(String dataType) { //TODO: Add support for arrays.
//        String range = "";
//        switch (dataType) {
//            case "byte":
//                range = "(-|^$)[0-1]{1,7}";;
//                break;
//            case "short":
//                range = "(-|^$)[0-1]{1,15}";;
//                break;
//            case "int":
//                range = "(-|^$)[0-1]{1,31}";;
//                break;
//            case "long":
//                range = "(-|^$)[0-1]{1,63}";;
//                break;
//            case "float": //TODO: Don't allow range on floats for the moment
//                range = "";;
//                break;
//            case "double": //TODO: Don't allow range on doubles for the moment
//                range = "";;
//                break;
//            case "boolean":
//                range = "true|false";;
//                break;
//            case "char":
//                range = ".";;
//                break;
//            case "String":
//                range = ".*";
//                break;
//        }
//
//        return range;
//    }

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
        String s = "";
        for (int i = 0; i < inputs.length; i++) {
            s += inputs[i].toString() + "\n";
        }
        return s;
    }
}
