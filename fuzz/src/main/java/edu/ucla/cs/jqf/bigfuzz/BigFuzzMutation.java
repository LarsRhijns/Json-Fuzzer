package edu.ucla.cs.jqf.bigfuzz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface BigFuzzMutation {

    /**
     * mutate on an csv file
     * @param inputFile
     * @param nextInputFile
     * @throws IOException
     */
    public void mutate(File inputFile, File nextInputFile) throws IOException;

	ArrayList<String> mutateFile(File inputFile) throws IOException;

	/**
     * mutate on rows of an input file
     * @param rows
     */
    public void mutate(ArrayList<String> rows);

    /**
     * Randomly duplicate some rows and then randomly insert into the input lines
     * @param rows
     */
    public void randomDuplicateRows(ArrayList<String> rows);

    /**
     * Randomly generate some rows and then randomly insert into the input lines
     * @param rows
     */
    public void randomGenerateRows(ArrayList<String> rows);



    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows);

    public void randomDuplacteOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows);

    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows);

    /**
     * write rows into csv txt file
     * @param outputFile
     * @throws IOException
     */
    public void writeFile(File outputFile, List<String> fileRows) throws IOException;
    public void deleteFile(String currentFile) throws IOException;
}
