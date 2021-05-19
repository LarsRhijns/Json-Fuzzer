package edu.ucla.cs.jqf.bigfuzz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class JsonMutation implements BigFuzzMutation {

    Random r = new Random();
    ArrayList<String> fileRows = new ArrayList<String>();
    String delete;

    @Override
    public void mutate(String inputFile, String nextInputFile) throws IOException {

    }

    @Override
    public void mutateFile(String inputFile, int index) throws IOException {

    }

    @Override
    public void mutate(ArrayList<String> rows) {

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

    }

    @Override
    public void deleteFile(String currentFile) throws IOException {

    }
}
