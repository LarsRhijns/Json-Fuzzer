package edu.bigfuzztabulardata;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Mutation {

    private static final int MUTATIONS_AMOUNT = 6;
    private static final String GENERATED_INPUT_FILES_FOLDER = "fuzz/src/main/java/edu/bigfuzztabulardata/generatedInputFiles/";
    private String currentFile = "";

    /**
     * String fileName = "InputFile";
     *         fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
     *         String filePath = GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
     */
    public Mutation() {
        currentFile = generateFileName();
    }

    public void mutate(List<String[]> data) {
        currentFile = generateFileName();
        performRandomMutation(data);
    }

    public void mutateFile(String inputFile) {
        currentFile = "Mutated File: " + inputFile; //TODO: Make it possible to mutate a file multiple times without overwriting it
        List<String[]> data;
        try {
            CSVReader reader = new CSVReader(new FileReader(inputFile));
            data = reader.readAll();
            performRandomMutation(data);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public void performRandomMutation(List<String[]> data) {
        int r = (int) (Math.random() * MUTATIONS_AMOUNT);
        switch (r) {
            case 0:
                dataDistributionMutation(data);
                break;
            case 1:
                dataTypeMutation(data);
                break;
            case 2:
                dataFormatMutation(data);
                break;
            case 3:
                dataColumnMutation(data);
                break;
            case 4:
                nullDataMutation(data);
                break;
            case 5:
                emptyDataMutation(data);
                break;
        }
    }

    private void dataDistributionMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private void dataTypeMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private void dataFormatMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private void dataColumnMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private void nullDataMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private void emptyDataMutation(List<String[]> data) {
        List<String[]> newData = null;

        writeMutation(data, currentFile);
    }

    private String writeMutation(List<String[]> data, String newFilePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
            for (String[] dataRow : data) {
                writer.writeNext(dataRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFilePath;
    }

    private String generateFileName() {
        String fileName = "MutatedFile";
        fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
        return GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
    }
}
