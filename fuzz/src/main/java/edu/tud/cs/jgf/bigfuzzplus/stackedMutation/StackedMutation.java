/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jgf.bigfuzzplus.stackedMutation;

//import org.apache.commons.lang.ArrayUtils;

import edu.ucla.cs.jqf.bigfuzz.BigFuzzMutation;
import org.apache.commons.lang.RandomStringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import static edu.tud.cs.jgf.bigfuzzplus.BigFuzzPlusDriver.PRINT_MUTATIONS;
import static edu.tud.cs.jgf.bigfuzzplus.stackedMutation.HighOrderMutation.*;

public class StackedMutation implements BigFuzzMutation {
    private final Random r = new Random();
    private long randomizationSeed;
    ArrayList<String> fileRows = new ArrayList<>();
    String delete;
    int maxGenerateTimes = 20;
    int maxDuplicatedTimes = 10;
    int maxMutationStack = 2;
    char delimiter = ',';


    // *********** REPRODUCIBILITY ****************
    // TODO: extend this section such that runs can be hardcoded.

    boolean useFixedMutationResult = false;
    String[] fixedMutationResultList = {};
    int fixedMutationResultPointer = 0;
    // ********************************************


    @SuppressWarnings({"rawtypes"})
    ArrayList<HighOrderMutationMethod> mutationMethodTracker = new ArrayList();
    @SuppressWarnings({"rawtypes"})
    ArrayList<Integer> mutationColumnTracker = new ArrayList();
    ArrayList<Integer> mutationStackTracker = new ArrayList();
    ArrayList<MutationPair> appliedmutations = new ArrayList();

    public StackedMutationEnum.StackedMutationMethod stackedMutationMethod = StackedMutationEnum.StackedMutationMethod.Disabled;

    /**
     * Read a random line from the input file which contains references to other input file. Use selected file to perform the mutation
     *
     * @param inputFile     File from which a line is read which contains other input files paths
     * @param nextInputFile File path where the new input should be stored
     * @throws IOException When method fails to write file name to directory "user.dir/"
     */
    public void mutate(String inputFile, String nextInputFile) throws IOException {
        // Select random row from input file to mutate over
        List<String> fileList = Files.readAllLines(Paths.get(inputFile));

        int n = r.nextInt(fileList.size());
        String fileToMutate = fileList.get(n);

        // Empty applied mutations, as it is only containing the mutations performed in this cycle
        appliedmutations.clear();

        // Mutate selected input file
        mutateFile(fileToMutate);

        //Create a file name for the about to be created input
        String fileName = nextInputFile + "+" + fileToMutate.substring(fileToMutate.lastIndexOf('/') + 1);
        writeFile(fileName);

        // Generate a path string, which can be used to delete the input file if it is not needed anymore
        String path = System.getProperty("user.dir") + "/" + fileName;
        delete = path;

        // write next input config
        BufferedWriter bw = new BufferedWriter(new FileWriter(nextInputFile));

        // Write the input to the pre-defined path
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
     * @throws IOException Throws exception if it fails to read to content of the input file
     */
    public void mutateFile(String inputFile) throws IOException {
        // Create a reader for the file
        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        ArrayList<String> rows = new ArrayList<>();

        // If the file exists, add every line to the rows list. These will be all provided input seeds to the program
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
        //TODO: 50/50 chance of generating extra rows, but row generation is not implemented by BigFuzz
        mutate(rows);

        fileRows = rows;
    }

    /**
     * Mutate the provides input rows. Method select one of the rows provided in the list and uses it to mutate. Said line will be directly applied to the provided list
     *
     * @param rows List of program inputs in which one row will be mutated
     */
    public void mutate(ArrayList<String> rows) {
        // Select the line in the input to be mutated and split the value son the delimiter
        int lineNum = r.nextInt(rows.size());
        String[] rowElements = rows.get(lineNum).split(",");
        String rowString = rows.get(lineNum);

        // Depending on the stacked Mutation method used to start this program, correct mutation method will be applied. Default is a single mutation
        String[] mutatedElements;
        switch (stackedMutationMethod) {
            case Permute_random:
            case Permute_max:
                mutatedElements = mutate_permute(rowElements);
                break;
            case Smart_stack:
                mutatedElements = smart_mutate(rowElements);
                break;
            case Single_mutate:
                mutatedElements = single_mutate(rowElements);
                break;
            default:
                mutatedElements = mutateLine(rowElements);
        }
        //TODO: Dynamic delimiter
        // Change the delimiter back to ',' after mutation
        delimiter = ',';

        // Append all row elements together and set the mutation result in the original input list.
        String mutatedRowString = listToString(mutatedElements);

        if (PRINT_MUTATIONS) {
            System.out.println("Input before mutation:" + rowString);
            System.out.println("Input after mutation:" + mutatedRowString);
        }
        // Only use hard coded inputs if the fixed mutation results is enabled:
        if(useFixedMutationResult) {
            mutatedRowString = nextMutationResultInList();
        }

        rows.set(lineNum, mutatedRowString);
    }

    /**
     * Apply the max amount of mutations, where only 1 mutation is applied per column
     * @param rowElements elements that needs to be mutated
     * @return mutated elements where each element is at most mutated once.
     */
    private String[] single_mutate(String[] rowElements) {
        ArrayList<HighOrderMutationMethod> appliedMutationPerColumn = new ArrayList<>();
        for (int i = 0; i < rowElements.length; i++) {
            appliedMutationPerColumn.add(HighOrderMutationMethod.NoMutation);
        }
        boolean[] mutatedColumns = new boolean[rowElements.length];
        int mutatedColumnsCount = 0;

        boolean changeDelimiter = false;

        // Apply at most the max amount of mutations
        for (int i = 0; i < maxMutationStack; i++) {
            if(rowElements.length <= mutatedColumnsCount) {
                break;
            }

            // Randomly pick a pointer to use as for next mutation
            int rowElementIdPointer = r.nextInt(rowElements.length - mutatedColumnsCount);
            // Get a random mutations method which can still be applied to the randomly selected column
            HighOrderMutationMethod mutationMethod = HighOrderMutation.getRandomMutation(r);

            int rowElementId = 0;
            int loopPointer=0;

            // Loop through all the row elements. If the column has already been mutated skip. If the loop pointer is equal to the rowElementIdPointer, use that element to mutate
            for (int j = 0; j < rowElements.length; j++) {
                if(!mutatedColumns[j]) {
                    if(rowElementIdPointer == loopPointer) {
                        rowElementId = j;
                    } else {
                        loopPointer++;
                    }
                }
            }

            // Remove element is always called on the last element, therefore a swap needs to be done
            if(mutationMethod == HighOrderMutationMethod.RemoveElement) {
                // If the last element has not been mutated yet, use said column to apply the remove element on
                // Else swap the mutation
                if(!mutatedColumns[rowElements.length-1]) {
                    rowElementId = rowElements.length-1;
                } else {
                    mutationMethod = appliedMutationPerColumn.get(rowElements.length-1);
                    appliedMutationPerColumn.set(rowElements.length-1, mutationMethod);
                }
            }

            // Change delimiter is not applied on a column, therefore dont add it to the count
            if(mutationMethod != HighOrderMutationMethod.ChangeDelimiter) {
                appliedMutationPerColumn.set(rowElementId, mutationMethod);
                mutatedColumns[rowElementId] = true;
                mutatedColumnsCount++;
            } else {
                changeDelimiter= true;
            }
        }
        mutationStackTracker.add(appliedMutationPerColumn.size());

        // Create a mutation list containing the mutation and element ID
        LinkedList<MutationPair> mutations = new LinkedList<>();

        // Collect all mutations in a linked list
        for (int i = 0; i < appliedMutationPerColumn.size(); i++) {
            if(mutatedColumns[i]) {
                mutations.add(new MutationPair(i, appliedMutationPerColumn.get(i)));
            }
        }

        if(changeDelimiter) {
            mutations.add(new MutationPair(0, HighOrderMutationMethod.ChangeDelimiter));
        }

        // Apply all mutations
        for (MutationPair pair :
                mutations) {
            rowElements = applyMutationMethod(pair.getMutation(), rowElements, pair.getElementId());
        }
        return rowElements;
    }

    /**
     * Apply the smart mutation to the passed row columns/elements. This method tries to stack as many mutations as given in one of the input parameters of the program.
     * The mutation takes the mutation rules, defined in HighOrderMutation, when stacking mutations.
     * If the mutation stacking fails on one of the columns, mutations stacking is stopped even when the max amount of mutation stacks is not reached. This means the method is not deterministic.
     *
     * @param rowElements elements on which the mutations need to be applied
     * @return mutated elements
     */
    private String[] smart_mutate(String[] rowElements) {
        // Create lists of applied mutations to a column per rowElement
        ArrayList<ArrayList<HighOrderMutationMethod>> appliedMutationPerColumn = new ArrayList<>();
        for (int i = 0; i < rowElements.length; i++) {
            appliedMutationPerColumn.add(new ArrayList<>());
        }

        // Create a mutation list containing the mutation and element ID
        LinkedList<MutationPair> mutations = new LinkedList<>();

        // If the mutation will delete an element, the next mutation should not apply a mutation to that column. Keep a counter of the amount of columns removed
        // ASSUMPTION: remove element will always remove the LAST element
        int elementDeletionCount = 0;

        int mutationStackRandomCount = r.nextInt(maxMutationStack ) + 1;

        // Generate as many mutations as possible which is withing the maxMutationStack value.
        for (int i = 0; i < mutationStackRandomCount; i++) {
            // If all the elements have been deleted, stop stacking mutations as no more mutations can be applied
            if (elementDeletionCount == rowElements.length) {
                break;
            }
            // Randomly select the column which will be mutated
            int rowElementId = r.nextInt(rowElements.length - elementDeletionCount);

            // Get a random mutations method which can still be applied to the randomly selected column
            HighOrderMutationMethod mutationMethod = HighOrderMutation.getRandomSmartMutation(r, appliedMutationPerColumn.get(rowElementId));

            // If No mutation is found, there can't be more mutations stacked. Stop trying to stack mutations
            if (mutationMethod == HighOrderMutationMethod.NoMutation) {
                break;
            }

            // If the mutation will delete an element, the next mutation should not
            if (mutationMethod == HighOrderMutationMethod.RemoveElement) {
                elementDeletionCount++;
            }

            mutations.add(new MutationPair(rowElementId, mutationMethod));
            appliedMutationPerColumn.get(rowElementId).add(mutationMethod);
        }

        mutationStackTracker.add(mutations.size());

        // Apply all mutations in sequential order
        for (MutationPair pair :
                mutations) {
            rowElements = applyMutationMethod(pair.getMutation(), rowElements, pair.getElementId());
        }
        return rowElements;
    }

    /**
     * Apply a single random mutation on provided elements
     *
     * @param rowElements Elements on which mutation is applied
     * @return mutated elements
     */
    private String[] mutateLine(String[] rowElements) {
        // Randomly select the column which will be mutated
        int rowElementId = r.nextInt(rowElements.length);
        HighOrderMutationMethod method = selectMutationMethod();

        // Mutate the row using the selected mutation method
        String[] mutationResult = applyMutationMethod(method, rowElements, rowElementId);

        return mutationResult;
    }

    /**
     * Stack mutations randomly defined in the program arguments. If Mutations can interfere/cancel each other out.
     *
     * @param rows elements that should be mutated
     * @return mutated elements
     */
    private String[] mutate_permute(String[] rows) {
        int mutationCount = 1;
        switch (stackedMutationMethod) {
            case Permute_random:
                mutationCount = r.nextInt(maxMutationStack) + 1; // prevent 0 mutation counts
                break;
            case Permute_max:
                mutationCount = maxMutationStack;
                break;

        }
        String[] mutatedElements = rows;
        for (int i = 0; i < mutationCount; i++) {
            mutatedElements = mutateLine(rows);
        }
        return mutatedElements;
    }

    /**
     * Changes delimiter that is different from default delimiter
     */
    private void changeDelimiter() {
        //TODO Add dynamic delimiters
        if (delimiter == ',') {
            delimiter = '~';
        }
    }

    /***
     * Randomly select a mutation method.
     * @return Random MutationMethod
     */
    private HighOrderMutation.HighOrderMutationMethod selectMutationMethod() {
        return HighOrderMutation.getRandomMutation(r);
    }

    /**
     * return the next mutation in the fixed list. If the list is empty, return an error. If the pointer is exceeding the list length, keep returning the last element.
     * @return String next input element in the fixed list
     */
    private String nextMutationResultInList() {
        if(fixedMutationResultList.length == 0) {
            System.err.println("Fixed mutation list is enabled, but the list of inputs to use is empty");
        }
        fixedMutationResultPointer = Math.min(fixedMutationResultPointer, fixedMutationResultList.length - 1);
        String nextMutation = fixedMutationResultList[fixedMutationResultPointer];
        fixedMutationResultPointer++;
        return nextMutation;
    }

    /**
     * Apply a mutation method to the provided list, on a specific element ID if applicable for said mutation method.
     *
     * @param method      a method matching the HighOrderMutation methods. If HighOrderMutation Method is not defined, no mutation is applied
     * @param rowElements Element list on which the mutation is performed
     * @param elementId   Element ID of which element needs to be mutated (if applicable by the mutation method)
     * @return mutated element list. If undefined method is provided the original list is returned.
     */
    //TODO: Fix warning on mutation result
    private String[] applyMutationMethod(HighOrderMutationMethod method, String[] rowElements, int elementId) {
        String[] mutationResult = rowElements;
        switch (method) {
            case ChangeValue:
                if (rowElements[elementId] != null && !rowElements[elementId].equals(""))
                    mutationResult = changeToRandomValue(rowElements, elementId);
                break;
            case ChangeType:
                mutationResult = changeType(rowElements, elementId);
                break;
            case RandomCharacter:
                if (rowElements[elementId] != null && !rowElements[elementId].equals(""))
                    mutationResult = changeToRandomInsert(rowElements, elementId);
                break;
            case RemoveElement:
                mutationResult = removeOneElement(rowElements);
                break;
            case AddElement:
                String one = Integer.toString(r.nextInt(10000));
                int columnIndexNewElement = r.nextInt(rowElements.length + 1);
                mutationResult = addOneElement(rowElements, one);
                break;
            case EmptyColumn:
                mutationResult = emptyOneElement(rowElements, elementId);
                break;
            case ChangeDelimiter:
                changeDelimiter();
                break;
        }

        saveMutation(elementId, method);

        return mutationResult;
    }

    /**
     * Randomly insert a character somewhere in an elements value.
     *
     * @param rowElements list of elements
     * @param elementId   element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId index is mutated
     */
    private String[] changeToRandomInsert(String[] rowElements, int elementId) {
        // Take a random ASCII character which could be used as delimiter in a column
        char temp = (char) r.nextInt(255);
        int pos = r.nextInt(rowElements[elementId].length());
        rowElements[elementId] = rowElements[elementId].substring(0, pos) + temp + rowElements[elementId].substring(pos);
        return rowElements;
    }

    /**
     * Change the value on the specified elementId index from an Integer to a Float. If the element is a float, change it to a string
     *
     * @param rowElements list of elements
     * @param elementId   element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId is mutated from an integer to a float + a random value.
     */
    private String[] changeType(String[] rowElements, int elementId) {
        if (isFloat(rowElements[elementId])) {
            rowElements[elementId] = rowElements[elementId] + "a";
        } else {
            int value;
            try {
                value = Integer.parseInt(rowElements[elementId]);
            } catch (Exception e) {
                return rowElements;
            }
            rowElements[elementId] = Float.toString((float) value);
        }
        return rowElements;
    }

    /**
     * Checks if string is a float by checking if there is a '.' and the left and right sides can be parsed to integers
     *
     * @param rowElement elements which is checked to be a float
     * @return true if the string can be parsed to a float
     */
    private boolean isFloat(String rowElement) {
        // If there is a . in the element and the last and first index are the same, we know there is exactly 1 '.'
        if (rowElement.indexOf('.') >= 0 && rowElement.indexOf('.') == rowElement.indexOf('.')) {
            String[] splitted = rowElement.split("\\.");
            // To allow for .xxxx floats instead of xxx.xxx
            for (int i = 0; i < splitted.length; i++) {
                //To allow for -.xxx values
                if (splitted[i].equals("-")) {
                    continue;
                }
                try {
                    Integer.parseInt(splitted[i]);
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Change the value on the specified elementId index to a random Integer
     *
     * @param rowElements list of elements
     * @param elementId   element ID of the element that needs to be mutated
     * @return list of elements where the element on the elementId is mutated to a random Integer
     */
    private String[] changeToRandomValue(String[] rowElements, int elementId) {
        rowElements[elementId] = Integer.toString(r.nextInt());
        return rowElements;
    }

    /**
     * Add one element to the provided String list. Provided value is inserted at the end.
     *
     * @param rowElements  String list in which the new element is inserted
     * @param elementValue Value which needs to be inserted in the provided list
     * @return New List in which the provided value is inserted in the input list at index
     */
    public static String[] addOneElement(String[] rowElements, String elementValue) {
        String[] result = new String[rowElements.length + 1];

        System.arraycopy(rowElements, 0, result, 0, rowElements.length);
        result[rowElements.length] = elementValue;

        return result;
    }

    /**
     * Takes a list of String of which it then removes one element. The provided index is removed.
     *
     * @param rowElements list of String from which one index needs to be removed
     * @return a new list of String, where the element at index is removed
     */
    public static String[] removeOneElement(String[] rowElements) {
        if (rowElements == null || rowElements.length == 0) {
            return rowElements;
        }

        String[] result = new String[rowElements.length - 1];

        // Remove last element from the rowElements
        // Smart_mutations relies on the last element being removed
        System.arraycopy(rowElements, 0, result, 0, rowElements.length - 1);

        return result;
    }

    /**
     * Empties (empty string) the element at the specified elementId index
     *
     * @param rowElements list of String from which one index needs to be removed
     * @param elementId   Index of the element that needs to be removed
     * @return list of rowElements, where the element at index is removed
     */
    private String[] emptyOneElement(String[] rowElements, int elementId) {
        rowElements[elementId] = "";
        return rowElements;
    }

    @Override
    public void randomDuplicateRows(ArrayList<String> rows) {
        int ind = r.nextInt(rows.size());
        int duplicatedTimes = r.nextInt(maxDuplicatedTimes) + 1;
        String duplicatedValue = rows.get(ind);
        for (int i = 0; i < duplicatedTimes; i++) {
            int insertPos = r.nextInt(rows.size());
            rows.add(insertPos, duplicatedValue);
        }
    }

    @Override
    public void randomGenerateRows(ArrayList<String> rows) {
        int generatedTimes = r.nextInt(maxGenerateTimes) + 1;
        for (int i = 0; i < generatedTimes; i++) {
            int bits = (int) (r.nextDouble() * 6);
            String tempRow = RandomStringUtils.randomNumeric(bits); // TODO: Fix string generation to use r field
            int method = (int) (r.nextDouble()  * 2);
            if (method == 0) {
                int next = (int) (r.nextDouble()  * 2);
                if (next == 0) {
                    rows.add("$" + tempRow);
                } else {
                    rows.add(tempRow);
                }
            } else {
                rows.add(RandomStringUtils.randomNumeric(3));
            }
        }
    }

    @Override
    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void randomDuplicateOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {

    }

    @Override
    public void writeFile(String outputFile) throws IOException {
        File f_out = new File(outputFile);
        FileOutputStream fos = new FileOutputStream(f_out);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String fileRow : fileRows) {
            if (fileRow == null) {
                continue;
            }
            bw.write(fileRow);
            bw.newLine();
        }

        bw.close();
        fos.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteFile(String currentFile) throws IOException {
        // Check if delete is not null (which it is when the file is deleted in the first run)
        if (delete != null) {
            File del = new File(delete);
            del.delete();
        }
    }

    @Override
    public void setStackedMutationMethod(StackedMutationEnum.StackedMutationMethod stackedMutationMethod) {
        this.stackedMutationMethod = stackedMutationMethod;
    }

    /**
     * Concatenated the list of string elements to a string using the delimiter
     *
     * @param mutationResult elements that need to be concatenated
     * @return String of concatenated elements
     */
    private String listToString(String[] mutationResult) {
        if (mutationResult == null) {
            return "";
        }
        StringBuilder row = new StringBuilder();
        for (int j = 0; j < mutationResult.length; j++) {
            if (j == 0) {
                row = new StringBuilder(mutationResult[j]);
            } else {
                row.append(delimiter).append(mutationResult[j]);
            }
        }
        return row.toString();
    }

    private void saveMutation(int rowElementId, HighOrderMutationMethod method) {
        mutationColumnTracker.add(rowElementId);
        mutationMethodTracker.add(method);
    }


    public void setMutationStackCount(int intMutationStackCount) {
        maxMutationStack = intMutationStackCount;
    }

    public void setSeed(long seed) {
        randomizationSeed = seed;
        r.setSeed(seed);
    }

    public long getRandomizationSeed() {
        return randomizationSeed;
    }


    public ArrayList<HighOrderMutationMethod> getMutationMethodTracker() {
        return mutationMethodTracker;
    }

    public ArrayList<Integer> getMutationColumnTracker() {
        return mutationColumnTracker;
    }

    public ArrayList<Integer> getMutationStackTracker() {
        return mutationStackTracker;
    }
}
