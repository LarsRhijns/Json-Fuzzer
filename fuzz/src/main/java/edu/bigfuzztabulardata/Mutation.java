package edu.bigfuzztabulardata;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang.ArrayUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Mutation {

    private static final int MUTATIONS_AMOUNT = 6;
    private static final String GENERATED_INPUT_FILES_FOLDER = "fuzz/src/main/java/edu/bigfuzztabulardata/generatedInputFiles/";
    private int mutationGeneration = 0;
    private DataFormat[] dataSpecification;

    /**
     * String fileName = "InputFile";
     *         fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
     *         String filePath = GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
     */
    public Mutation(DataFormat[] dataSpecification) {
        this.dataSpecification = dataSpecification;
    }

    public void mutate(List<String[]> data) {
        performRandomMutation(data, constructFilePath());
    }

    public void mutateFile(String fileName) {
        String currentFile = constructFilePath(fileName); //TODO: Make it possible to mutate a file multiple times without overwriting it
        List<String[]> data;
        try {
            CSVReader reader = new CSVReader(new FileReader(currentFile));
            data = reader.readAll();
            performRandomMutation(data, constructFilePath(fileName + "-mutation" + mutationGeneration));
            mutationGeneration++;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public void performRandomMutation(List<String[]> data, String currentFile) {
        int r = (int) (Math.random() * MUTATIONS_AMOUNT);
        r = 1;
        List<String[]> newData= null;
        switch (r) {
            case 0:
                newData = dataDistributionMutation(data);
                break;
            case 1:
                newData = dataTypeMutation(data);
                break;
            case 2:
                newData = dataFormatMutation(data);
                break;
            case 3:
                newData = dataColumnMutation(data);
                break;
            case 4:
                newData = nullDataMutation(data);
                break;
            case 5:
                newData = emptyDataMutation(data);
                break;
        }

        writeMutation(newData, currentFile);
    }

    private List<String[]> dataDistributionMutation(List<String[]> data) {
        //TODO: Fix implementation
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        newData.get(randomRow)[randomColumn] = dataSpecification[randomColumn].generateInputOutsideRange();
        return newData;
    }

    private List<String[]> dataTypeMutation(List<String[]> data) {
        //TODO: Implement
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        newData.get(randomRow)[randomColumn] = dataSpecification[randomColumn].changeDataType(newData.get(randomRow)[randomColumn]);
        return newData;
    }

    private List<String[]> dataFormatMutation(List<String[]> data) {
        List<String[]> newData = data;
        //TODO: I need an example of datatypes that use delimiters; Or do they mean the CSV inputfile?
        return newData;
    }

    private List<String[]> dataColumnMutation(List<String[]> data) {
        List<String[]> newData = data;

        return newData;
    }

    /**
     * Picks a random cell and removes the cell.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> nullDataMutation(List<String[]> data) {
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        String[] updatedColumn = newData.get(randomRow);
        updatedColumn = (String[]) ArrayUtils.remove(updatedColumn, randomColumn);
        newData.set(randomRow, updatedColumn);
        return newData;
    }

    /**
     * Picks a random cell and removes its data.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> emptyDataMutation(List<String[]> data) {
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        newData.get(randomRow)[randomColumn] = "";
        return newData;
    }

    private String writeMutation(List<String[]> data, String newFilePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
            System.out.println(newFilePath);
            for (String[] dataRow : data) {
                writer.writeNext(dataRow);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFilePath;
    }

    private String constructFilePath(String fileName) {
        return GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
    }

    private String constructFilePath() {
        String fileName = "MutatedFile";
        fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
        return GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
    }
}
