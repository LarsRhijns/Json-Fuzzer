package edu.tabfuzz;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvException;
import edu.ucla.cs.jqf.bigfuzz.BigFuzzMutation;
import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class TabFuzzMutation implements BigFuzzMutation {

    private static final int MUTATIONS_AMOUNT = 5;
    private static final String GENERATED_INPUT_FILES_FOLDER = "fuzz/src/main/java/edu/tabfuzz/generatedInputFiles/";
    private int mutationGeneration = 0;
    private DataFormat[] dataSpecification;

    /**
     * String fileName = "InputFile";
     *         fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
     *         String filePath = GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
     */
    public TabFuzzMutation(DataFormat[] dataSpecification) {
        this.dataSpecification = dataSpecification;
    }

    public void mutate(List<String[]> data) {
        performRandomMutation(data, constructFilePath());
    }

    public void mutateFile(String fileName, String newFileName) {
        String currentFile = "";
        try {
            Scanner sc = new Scanner(new File(fileName));
            currentFile = sc.nextLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String newFilePath = configurationFileGenerator(newFileName);

        List<String[]> data;
        try {
            CSVReader reader = new CSVReader(new FileReader(currentFile));
            data = reader.readAll();
            performRandomMutation(data, newFilePath);
            mutationGeneration++;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public String configurationFileGenerator(String filePath) {
        String actualFile = "fuzz/src/main/java/edu/tabfuzz/generatedInputFiles/" + filePath.substring(filePath.lastIndexOf('/')+1);
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(actualFile);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualFile;
    }

    public void performRandomMutation(List<String[]> data, String currentFile) {
        int r = (int) (Math.random() * MUTATIONS_AMOUNT);
//        r = 2;
        List<String[]> newData= null;
        switch (r) {
            case 0:
                newData = dataDistributionMutation(data);
                break;
            case 1:
                newData = dataTypeMutation(data);
                break;
            case 2:
                newData = dataColumnMutation(data);
                break;
            case 3:
                newData = nullDataMutation(data);
                break;
            case 4:
                newData = emptyDataMutation(data);
                break;
        }

        writeMutation(newData, currentFile);
    }

    private List<String[]> dataDistributionMutation(List<String[]> data) {
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        //TODO: Pick data within range sometimes
        newData.get(randomRow)[randomColumn] = dataSpecification[randomColumn].generateInputOutsideRange();
        return newData;
    }

    /**
     * Picks a random cell and changes the datatype of the element.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> dataTypeMutation(List<String[]> data) {
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * newData.get(randomRow).length);
        newData.get(randomRow)[randomColumn] = dataSpecification[randomColumn].changeDataType(newData.get(randomRow)[randomColumn]);
        return newData;
    }

    /**
     * Picks a random random and adds an extra column at a random index.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> dataColumnMutation(List<String[]> data) {
        List<String[]> newData = data;
        int randomRow = (int) (Math.random() * newData.size());
        int randomColumn = (int) (Math.random() * (newData.get(randomRow).length + 1));
        RgxGen generator = new RgxGen(".{1,5}");
        String[] updatedColumn = newData.get(randomRow);
        updatedColumn = (String[]) ArrayUtils.add(updatedColumn, randomColumn, generator.generate());
        newData.set(randomRow, updatedColumn);
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

    @Override
    public void mutate(String inputFile, String nextInputFile) throws IOException {
        mutateFile(inputFile, nextInputFile);
    }

    @Override
    public void mutateFile(String inputFile, int index) throws IOException {
        System.err.println("Don't think this is used?");
    }

    @Override
    public void mutate(ArrayList<String> rows) {
        System.err.println("Don't think this is used?");

    }

    @Override
    public void randomDuplicateRows(ArrayList<String> rows) {
        System.err.println("This should never be run");
    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    @Override
    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    @Override
    public void randomDuplacteOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    @Override
    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    @Override
    public void writeFile(String outputFile) throws IOException {

    }

    @Override
    public void deleteFile(String currentFile) throws IOException {

    }
}
