package edu.ucla.cs.jqf.bigfuzz;

import java.io.IOException;
import java.util.ArrayList;

public interface BigFuzzMutation {


    /**
     * mutate on an csv file
     * @param inputFile
     * @throws IOException
     */
    public void mutate(String inputFile, String nextInputFile) throws IOException;

    /**
     * mutate on rows of an input file
     * @param rows
     */
    public void mutate(ArrayList<String> rows);

    /**
     * Randomly generate some rows and then randomly insert into the input lines
     * @param rows
     */
    public void randomGenerateRows(ArrayList<String> rows);

    /**
     * write rows into csv txt file
     * @param outputFile
     * @throws IOException
     */
    public void writeFile(String outputFile) throws IOException;
    public void deleteFile(String currentFile) throws IOException;

//    void setStackedMutationMethod(StackedMutationEnum.StackedMutationMethod stackedMutationMethod);
}
