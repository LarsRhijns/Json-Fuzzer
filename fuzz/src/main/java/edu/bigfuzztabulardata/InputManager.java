package edu.bigfuzztabulardata;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jacoco.report.csv.CSVFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputManager {

    private DataFormat[] inputs;

    /**
     * TODO: Easy tabular to CSV converter that automatically uses the proper input format, with a wide range of options to provide the specification
     * Takes an CSV file of input specification and transforms it into an array of DataFormats.
     * Expects per cell an input type (String) and optionally an input range (Regex) and interesting values (Regex) (. in case of empty)
     * White space is delimiter
     * @param file CSV file of input specification.
     */
    public InputManager(File file) {
        this.inputs = readInputs(file);
    }

    /**
     * Reads a CSV file of input specifications and returns them as an array.
     * @param file CSV file of input specification.
     * @return array of input specification.
     */
    private DataFormat[] readInputs(File file) {
        String[] inputs = null;
        try {
            CSVReader reader = new CSVReader(new FileReader(file));
            inputs = reader.readNext();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return processInputs(inputs);
    }

    /**
     * Takes an array of inputs and returns them as DataFormats
     * @param inputs array of inputs.
     * @return array of DataFormats.
     */
    private DataFormat[] processInputs(String[] inputs) {
        DataFormat[] processedInputs = new DataFormat[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            try {
                String input = inputs[i];
                String[] splitInputs = input.split(" ");
                if (splitInputs.length > 3) {
                    //TODO: find a way to allow/remove whitespaces in the input file.
                    throw new Exception("Input file contains invalid whitespaces.");
                }
                String[] specialValues = null;
                if (splitInputs.length == 3) {
                    specialValues = splitInputs[2].split(",");
                }
                DataFormat df;
                if (splitInputs[1].equals(".*")) {
                    String dataType = splitInputs[0];
                    if (splitInputs[0].contains("array")) {
                        dataType = DataFormat.getArrayType(splitInputs[0]);
                        System.out.println("Array type found: " + dataType);
                    }
                    df = new DataFormat(splitInputs[0], getDefaultRange(dataType), specialValues, true);
                } else {
                    df = new DataFormat(splitInputs[0], splitInputs[1], specialValues, false);
                }
                processedInputs[i] = df;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processedInputs;
    }

    /**
     * In case there is no specification for the range (or .*), return a regex that represents all possible values of the corresponding datatype.
     * @param dataType
     * @return
     */
    public String getDefaultRange(String dataType) { //TODO: Add support for arrays.
        String range = "";
        switch (dataType) {
            case "byte":
                range = "(-|^$)[0-1]{1,7}";;
                break;
            case "short":
                range = "(-|^$)[0-1]{1,15}";;
                break;
            case "int":
                range = "(-|^$)[0-1]{1,31}";;
                break;
            case "long":
                range = "(-|^$)[0-1]{1,63}";;
                break;
            case "float": //TODO: Don't allow range on floats for the moment
                range = "";;
                break;
            case "double": //TODO: Don't allow range on doubles for the moment
                range = "";;
                break;
            case "boolean":
                range = "true|false";;
                break;
            case "char":
                range = ".";;
                break;
            case "String":
                range = ".*";
                break;
        }

        return range;
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
        String s = "";
        for (int i = 0; i < inputs.length; i++) {
            s += inputs[i].toString() + "\n";
        }
        return s;
    }
}
