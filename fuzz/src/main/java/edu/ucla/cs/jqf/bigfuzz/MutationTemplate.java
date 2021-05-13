package edu.ucla.cs.jqf.bigfuzz;

//import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MutationTemplate implements BigFuzzMutation {

    Random r = new Random();
    ArrayList<String> fileRows = new ArrayList<String>();
    String delete;

    /**
     * Read a random line from the input file which contains references to other input file. Use selected file to perform the mutation
     *
     * @param inputFile     File from which a line is read which contains other input files paths
     * @param nextInputFile File path where the new input should be stored
     * @throws IOException
     */
    public void mutate(String inputFile, String nextInputFile) throws IOException {
        // Select random row from input file to mutate over
        List<String> fileList = Files.readAllLines(Paths.get(inputFile));
        Random random = new Random();
        int n = random.nextInt(fileList.size());
        String fileToMutate = fileList.get(n);

        // Mutate selected input file
        mutateFile(fileToMutate);

        //Create a file name for the about to be created input
        String fileName = nextInputFile + "+" + fileToMutate.substring(fileToMutate.lastIndexOf('/') + 1);
        writeFile(fileName);

        // Genereate a path string, which can be used to delete the input file if it is not needed anymore
        String path = System.getProperty("user.dir") + "/" + fileName;
        delete = path;

        // write next input config
        BufferedWriter bw = new BufferedWriter(new FileWriter(nextInputFile));

        for (int i = 0; i < fileList.size(); i++) {
            if (i == n)
                bw.write(path);
            else
                bw.write(fileList.get(i));
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }

    @Override
    public void mutateFile(String inputFile, int index) throws IOException {

    }

    /**
     * Loads provided path to input file and calls mutation in loaded input
     *
     * @param inputFile path to input file
     * @throws IOException
     */
    public void mutateFile(String inputFile) throws IOException {
        // Create a reader for the file
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        ArrayList<String> rows = new ArrayList<String>();

        // If the file exists, add every line to the rows list.
        if (file.exists()) {
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                rows.add(readLine);
            }
        } else {
            System.out.println("File does not exist!");
            return;
        }

        br.close();

        // Mutate the loaded rows
        mutate(rows);

        fileRows = rows;
    }

    public static String[] removeOneElement(String[] input, int index) {
        List result = new LinkedList();

        for (int i = 0; i < input.length; i++) {
            if (i == index) {
                continue;
            }
            result.add(input[i]);
        }

        return (String[]) result.toArray(input);
    }

    public void mutate(ArrayList<String> list) {
        // set seet of the mutation.
        // TODO: add seed to configuration/result -> evaluation such that the results can be reproducded?
        r.setSeed(System.currentTimeMillis());

        // Select the line in the input to be mutated and split the value son the delimiter
        int lineNum = r.nextInt(list.size());
        String[] rowElements = list.get(lineNum).split("$del$");

        // Randomly select the column which will be mutated
        int rowElementId = r.nextInt(Integer.parseInt("$cols$"));

        int method = selectMutationMethod();
        System.out.println("Mutation: method=" + method + ", line_index=" + lineNum + ", column_index= " + rowElementId);

        // Mutate the row using the selected mutation method
        String[] mutationResult = applyMutationMethod(method, rowElements, rowElementId);

        // Append all row elements together and set the mutation result in the original input list.
        String rowString = listToString(mutationResult);
        list.set(lineNum, rowString);
    }

    private String listToString(String[] mutationResult) {
        StringBuilder row = new StringBuilder();
        for (int j = 0; j < mutationResult.length; j++) {
            if (j == 0) {
                row = new StringBuilder(mutationResult[j]);
            } else {
                row.append(",").append(mutationResult[j]);
            }
        }
        return row.toString();
    }

    private int selectMutationMethod() {
        // 0: random change value
        // 1: random change into float
        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        return r.nextInt(5);
    }

    private String[] applyMutationMethod(int method, String[] columns, int columnID) {
        String[] mutationResult = columns;
        switch(method) {
            case 0:
                mutationResult = changeToRandomValue(columns, columnID);
                break;
            case 1:
                mutationResult = changeToFloat(columns, columnID);
                break;
            case 2:
                mutationResult = changeToRandomInsert(columns, columnID);
                break;
            case 3:
                mutationResult = removeOneElement(columns, columnID);
                break;
            case 4:
                String one = Integer.toString(r.nextInt(10000));
                mutationResult = addOneElement(columns, one, columnID);
                break;
        }
        return mutationResult;
    }

    private String[] changeToRandomInsert(String[] columns, int columnID) {
        char temp = (char) r.nextInt(255);
        int pos = r.nextInt(columns[columnID].length());
        columns[columnID] = columns[columnID].substring(0, pos) + temp + columns[columnID].substring(pos);
        return columns;
    }

    private String[] changeToFloat(String[] columns, int columnID) {
        int value = Integer.parseInt(columns[columnID]);
        float v = (float) value + r.nextFloat();
        columns[columnID] = Float.toString(v);
        return columns;
    }

    private String[] changeToRandomValue(String[] columns, int columnID) {
        columns[columnID] = Integer.toString(r.nextInt());
        return columns;
    }


    public static String[] addOneElement(String[] input, String value, int index) {
        List result = new LinkedList();

        for (int i = 0; i < input.length; i++) {
            result.add(input[i]);
            if (i == index) {
                result.add(value);
            }
        }

        return (String[]) result.toArray(input);
    }



    @Override
    public void randomDuplicateRows(ArrayList<String> rows) {

    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {

    }

    @Override
    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void randomDuplacteOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void writeFile(String outputFile) throws IOException {
        File fout = new File(outputFile);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < fileRows.size(); i++) {
            if (fileRows.get(i) == null) {
                continue;
            }
            bw.write(fileRows.get(i));
            bw.newLine();
        }

        bw.close();
        fos.close();
    }

    @Override
    public void deleteFile(String currentFile) throws IOException {
        File del = new File(delete);
        del.delete();
    }

}
