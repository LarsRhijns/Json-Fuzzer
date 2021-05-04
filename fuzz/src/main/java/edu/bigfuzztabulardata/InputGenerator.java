package edu.bigfuzztabulardata;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputGenerator {

    DataFormat[] inputSpecification;
    String generatedInputFilesFolder = "fuzz/src/main/java/edu/bigfuzztabulardata/generatedInputFiles/";

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
        String filePath = generatedInputFilesFolder + fileName + ".txt";
        try {

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            for (int i = 0; i < 2; i++) { //TODO: i is the amount of lines; figure out how many lines an input file should have.
                String[] inputData = generateInputData();
                writer.writeNext(inputData);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * Generates a row of data in the form of an array.
     * @return a row of data in the form of an array.
     */
    private String[] generateInputData() {
        String[] dataFile = new String[inputSpecification.length];
        for(int i = 0; i < inputSpecification.length; i++) {

            dataFile[i] = inputSpecification[i].generateInputInRange();
        }

        return dataFile;
    }

}
