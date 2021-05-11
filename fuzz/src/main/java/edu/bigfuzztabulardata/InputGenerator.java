package edu.bigfuzztabulardata;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputGenerator {

    DataFormat[] inputSpecification;
    String generatedInputFilesFolder = "fuzz/src/main/java/edu/bigfuzztabulardata/generatedInputFiles/";
    private static final int INPUT_FILE_AMOUNT_OF_LINES = 5;
    private static final int ARRAY_SIZE = 5;

    public InputGenerator(DataFormat[] inputSpecification) {
        this.inputSpecification = inputSpecification;
    }

    /**
     * Generates an inputfile (CSV format) based on the inputSpecification generated in the InputManager.
     * @return the filePath of the generated file
     */
    public String generateInputFile() {
        String fileName = "InputFile";
        fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
        String filePath = generatedInputFilesFolder + fileName + ".csv";
        try {

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            for (int i = 0; i < INPUT_FILE_AMOUNT_OF_LINES; i++) { //TODO: i is the amount of lines; figure out how many lines an input file should have.
                String[] inputData = generateInputData();
                writer.writeNext(inputData);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * Generates a row of data in the form of an array.
     * @return a row of data in the form of an array.
     */
    private String[] generateInputData() {
        String[] dataFile = new String[inputSpecification.length];
        for(int i = 0; i < inputSpecification.length; i++) {
            if (inputSpecification[i].getDataType().contains("array")) {
                dataFile[i] = inputSpecification[i].generateArrayInputInRange(ARRAY_SIZE); //TODO: Find a way to get the correct array size/ Build support for arrays within arrays?
            } else {
                dataFile[i] = inputSpecification[i].generateInputInRange();
            }
        }

        return dataFile;
    }

}
