package edu.ucla.cs.jqf.bigfuzz;

//import org.apache.commons.lang.ArrayUtils;

import org.apache.commons.lang.RandomStringUtils;

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
    int maxGenerateTimes = 10;

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
     * Loads provided path to input file and calls mutation in loaded input. Applies changes to the field "fileRows"
     *
     * @param inputFile path to input file
     * @throws IOException
     */
    public void mutateFile(String inputFile) throws IOException {
        // Create a reader for the file
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        ArrayList<String> rows = new ArrayList<>();

        // If the file exists, add every line to the rows list.
        if (file.exists()) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                rows.add(readLine);
            }
        } else {
            System.out.println("File does not exist!");
            return;
        }

        br.close();

        // Mutate the loaded rows
        //TODO: 50/50 chance of generating extra rows
        mutate(rows);

        fileRows = rows;
    }

    /**
     * Selects a random line from the provided list and applies a randomly selected mutation method on it. The random line is updated in the provided list.
     * @param list input list in which a line needs to be mutated
     */
    public void mutate(ArrayList<String> list) {
        // set seet of the mutation.
        // TODO: add seed to configuration/result -> evaluation such that the results can be reproducded?
        r.setSeed(System.currentTimeMillis());

        // Select the line in the input to be mutated and split the value son the delimiter
        int lineNum = r.nextInt(list.size());
        String[] rowElements = list.get(lineNum).split(",");

        // Randomly select the column which will be mutated
        int rowElementId = r.nextInt(list.size());

        int method = selectMutationMethod();
        System.out.println("Mutation: method=" + method + ", line_index=" + lineNum + ", column_index= " + rowElementId);

        // Mutate the row using the selected mutation method
        String[] mutationResult = applyMutationMethod(method, rowElements, rowElementId);

        // Append all row elements together and set the mutation result in the original input list.
        String rowString = listToString(mutationResult);
        list.set(lineNum, rowString);
    }

    /***
     * Randomly select a mutation method between 0 (inc) and 5 (ex).
     * @return random number between 0 <= x < 5
     */
    private int selectMutationMethod() {
        // 0: random change value
        // 1: random change into float
        // 2: random insert
        // 3: random delete one column
        // 4: random add one coumn
        return r.nextInt(5);
    }

    /**
     * Apply a mutation method to the provided list, on a specific element ID if applicable for said mutation method.
     * @param method integer indicating a method. Integers correspond to the following operations:
     *         0: random change value
     *         1: random change into float
     *         2: random insert value in element
     *         3: random delete one column/element
     *         4: random add one column/element
     * @param rowElements Element list on which the mutation is performed
     * @param elementId Element ID of which element needs to be mutated (if applicable by the mutation method)
     * @return mutated element list. If undefined method is provided the original list is returned.
     */
    private String[] applyMutationMethod(int method, String[] rowElements, int elementId) {
        String[] mutationResult = rowElements;
        switch (method) {
            case 0:
                mutationResult = changeToRandomValue(rowElements, elementId);
                break;
            case 1:
                mutationResult = changeToFloat(rowElements, elementId);
                break;
            case 2:
                mutationResult = changeToRandomInsert(rowElements, elementId);
                break;
            case 3:
                mutationResult = removeOneElement(rowElements, elementId);
                break;
            case 4:
                String one = Integer.toString(r.nextInt(10000));
                int columnIndexNewElement = r.nextInt(rowElements.length+1);
                mutationResult = addOneElement(rowElements, one, columnIndexNewElement);
                break;
        }
        return mutationResult;
    }

    /**
     * Randomly insert a character somewhere in an elements value.
     * @param rowElements list of elements
     * @param elementId element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId index is mutated
     */
    private String[] changeToRandomInsert(String[] rowElements, int elementId) {
        char temp = (char) r.nextInt(255);
        int pos = r.nextInt(rowElements[elementId].length());
        rowElements[elementId] = rowElements[elementId].substring(0, pos) + temp + rowElements[elementId].substring(pos);
        return rowElements;
    }

    /**
     * Change the value on the specified elementId index from an Integer to a Float. Also add a random Float to that value.
     * @param rowElements list of elements
     * @param elementId element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId is mutated from an integer to a float + a random value.
     */
    private String[] changeToFloat(String[] rowElements, int elementId) {
        //TODO: Add check to see if the column can be parsed to an Integer, otherwise just return the original value
        int value = Integer.parseInt(rowElements[elementId]);
        float v = (float) value + r.nextFloat();
        rowElements[elementId] = Float.toString(v);
        return rowElements;
    }

    /**
     * Change the value on the specified elementId index to a random Integer
     * @param rowElements list of elements
     * @param elementId element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId is mutated to a random Integer
     */
    private String[] changeToRandomValue(String[] rowElements, int elementId) {
        rowElements[elementId] = Integer.toString(r.nextInt());
        return rowElements;
    }


    /**
     * Add one element to the provided String list. Provided value is inserted at the provided index. If the element needs to be inserted at the en of the list, use index input.size() + 1
     * @param rowElements String list in which the new element is inserted
     * @param elementValue Value which needs to be inserted in the provided list
     * @param index Index at which the new element needs to be inserted
     * @return New List in which the provided value is inserted in the input list at index
     */
    public static String[] addOneElement(String[] rowElements, String elementValue, int index) {
        List<String> result = new LinkedList<>();

        for (int i = 0; i < rowElements.length; i++) {
            if (i == index) {
                result.add(elementValue);
            }
            result.add(rowElements[i]);
        }

        if(index == rowElements.length + 1) {
            result.add(elementValue);
        }

        return result.toArray(rowElements);
    }

    /**
     * Takes a list of String of which it then removes one element. The provided index is removed.
     * @param rowElements list of String from which one index needs to be removed
     * @param index Index of the element that needs to be removed
     * @return a new list of String, where the element at index is removed
     */
    public static String[] removeOneElement(String[] rowElements, int index) {
        LinkedList<String> result = new LinkedList<>();

        for (int i = 0; i < rowElements.length; i++) {
            if (i != index) {
                result.add(rowElements[i]);
            }
        }

        return result.toArray(rowElements);
    }


    @Override
    public void randomDuplicateRows(ArrayList<String> rows) {

    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes)+1;
        for(int i=0;i<generatedTimes;i++)
        {
            int bits = (int)(Math.random()*6);
            String tempRow = RandomStringUtils.randomNumeric(bits);
            int method =(int)(Math.random() * 2);
            if(method == 0){
                int next = (int)(Math.random()*2);
                if(next == 0) {
                    rows.add("$" + tempRow);
                }else {
                    rows.add(tempRow);
                }
            }
            else{
                rows.add(RandomStringUtils.randomNumeric(3));
            }
        }
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
}
